package Heuristics.Deductions;

import Heuristics.BoardInterface;
import Heuristics.Path;
import Heuristics.Detector.StateDetector;
import StandardChess.BoardBuilder;
import StandardChess.Coordinate;
import StandardChess.Coordinates;

import java.util.*;
import java.util.function.BiPredicate;
import java.util.stream.Collectors;

import static Heuristics.HeuristicsUtil.*;

/**
 * CaptureLocations is a Deduction that determines which missing pieces may not have been captured by pawns.
 * It does not take into account the captures of or by pawns that have promoted, and will not fail under the
 * assumption that these have not taken place, i.e., if a missing pawn cannot have been captured if it did
 * not promote, it will not be accounted for when the other deductions are rerun.
 * <></>
 * The state will never be set to false, but in the course of making deductions, the CaptureLocations may
 * make a call to its registered StateDetector to rerun previously run Deductions with the new information it
 * has found.
 * <></>
 * CaptureLocations must only run deduce() after the pawns and pieces have been mapped, cages determined,
 * and certain promotions discovered, otherwise its results will not be accurate.
 */
public class CaptureLocations extends AbstractDeduction {
    /**A BiPredicate of two Coordinates testing if the two coordinates do not have the same x and that one is dark,
     * for use in testing the colour pawn captures are made on*/
    private static final BiPredicate<Coordinate, Coordinate> DARK_TEST =
            (c1, c2) -> (c2.getX() != c1.getX() || c1.equals(c2) ) && !Coordinates.light(c1);
    /**A BiPredicate of two Coordinates testing if the two coordinates do not have the same x and that one is light,
     * for use in testing the colour pawn captures are made on*/
    private static final BiPredicate<Coordinate, Coordinate> LIGHT_TEST =
            (c1, c2) -> (c2.getX() != c1.getX() || c1.equals(c2))  && Coordinates.light(c1);
    /**An arbitrarily high number that would never ordinarily come up*/
    private final static int ARBITRARILY_HIGH_NUMBER = 99;
    /**A pathFinderUtil to be used for finding Paths*/
    private PathfinderUtil pathFinderUtil;
    /**Stores a reference to the piecePaths stored in the StateDetector's PieceData*/
    private Map<Coordinate, Map<Coordinate, Path>> piecePaths;

    /**
     * A constructor setting the errorMessage to "Too many pawn captures - not all missing pieces are reachable."
     */
    public CaptureLocations() {
        super("Too many pawn captures - not all missing pieces are reachable.");
    }

    /**
     * Register's a StateDetector as described in the Deduction documentation, and sets the PathFinderUtil.
     * @param detector the StateDetector to be registered
     */
    @Override
    public void registerDetector(StateDetector detector) {
        super.registerDetector(detector);
        this.pathFinderUtil = new PathfinderUtil(detector);
        this.piecePaths = this.detector.getPieceData().getPiecePaths();
    }

    /**
     * Finds those missing pieces that cannot have been captured by any pawn on the board as a pawn, storing them in the
     * PromotionData and CaptureData. If it is found that there exists missing pieces that cannot have been captured
     * by pawns,the StateDetector's reTest() method will be called.
     * @param board the board to be checked
     */
    @Override
    public void deduce(BoardInterface board) {
        int whiteRemovals = pieceReductions(board, true);
        int blackRemovals = pieceReductions(board, false);
        if (whiteRemovals + blackRemovals != 0) {
            this.detector.reTest(board);
        }
        this.detector.getCaptureData().getNonPawnCaptures(true).addAll(pawnCaptureLocations(true, board));
        this.detector.getCaptureData().getNonPawnCaptures(false).addAll(pawnCaptureLocations(false, board));

    }

    /**
     * Finds those missing non-pawn pieces of the opposing player that cannot have been captured
     * by the given player's pawns, storing them ni the CaptureData, and returning the quantity.
     * @param board the board to be checked
     * @param white the player whose pawns' captures are being checked, true if white, false if black
     * @return the number of non-pawn pieces of the opposing player that cannot have been captured by pawns
     */
    private int pieceReductions(BoardInterface board, boolean white) {
        int y = white ? FINAL_RANK_Y : FIRST_RANK_Y;
        int capturesToRemove = 0;
        // Find caged opposing pieces
        Path ofWhichCaged = Path.of(this.detector.getPieceData().getCaged().entrySet().stream()
                .filter(entry -> entry.getKey().getY() == y) //Belongs to the opponent
                .filter(Map.Entry::getValue) //Is Caged
                .filter(entry -> {
                    Coordinate pieceCoord = entry.getKey();
                    Map<Coordinate, Path> map = this.piecePaths.get(pieceCoord);
                    if (map.isEmpty()){
                        return true;
                    }
                    if (map.size() == 1) {
                        int  x = pieceCoord.getX();
                        return this.piecePaths
                                .get(new Coordinate(Math.abs(FINAL_RANK_Y - x), pieceCoord.getY()))
                                .containsKey(map.keySet().stream().findAny().orElse(Coordinates.NULL_COORDINATE))
                                && x == 0;
                    }
                    return false;
                }) //Is missing
                .map(Map.Entry::getKey)
                .toList());
        Path nonPawnCaptures = this.detector.getCaptureData().getNonPawnCaptures(white);
        // of the above which are bishops
        int ofWhichBishop = (int) ofWhichCaged.stream().filter(coordinate -> {
                    int x = coordinate.getX();
                    boolean bishop = x == K_BISHOP_X || x == Q_BISHOP_X;
                    if (bishop) {
                        nonPawnCaptures.add(coordinate);
                    }
                    return bishop;
                }).count();

        // Rooks are the only piece capable of being both caged and captured on the pawn rank
        Path ofWhichRook = Path.of(ofWhichCaged.stream()
                .filter(coordinate -> coordinate.getX() == Q_ROOK_X || coordinate.getX() == K_ROOK_X) // Is a rook
                .toList());

        int ofWhichQueen = ofWhichCaged.size() - ofWhichBishop - ofWhichRook.size();
        if (ofWhichQueen > 0) {
            nonPawnCaptures.add(new Coordinate(QUEEN_X, y));
        }

        int inaccessibleTakenRooks = 0;
        // Check each rook can path to any opposing pawn
        if (!ofWhichRook.isEmpty()) {
             inaccessibleTakenRooks = findInaccessibleRooks(board, white, ofWhichRook);
        }

        // Account for bishops being taken on the correct colour
        // this is only done in situations where all captures made by pawns are made by definite pawn paths
        List<BiPredicate<Coordinate, Coordinate>> predicates =
                new LinkedList<>(this.piecePaths.entrySet().stream()
                        .filter(entry -> entry.getKey().getY() == (white ? FINAL_RANK_Y : FIRST_RANK_Y)) //Correct colour
                        .filter(entry -> entry.getKey().getX() == Q_BISHOP_X || entry.getKey().getX() == K_BISHOP_X) //Is a Bishop
                        .filter(entry -> !(this.detector.getPieceData().getCaged().get(entry.getKey()))) //Is not Caged
                        .filter(entry -> entry.getValue().isEmpty()) //Is missing
                        .map(entry -> ((!Coordinates.light(entry.getKey())  ? DARK_TEST : LIGHT_TEST)))
                        .toList());
        if (!predicates.isEmpty()) {
            Map<Coordinate, List<Path>> paths = everySingularPawnPath(white);//Every path that's a single path;

            if (pawnCaptures(paths, white) == 0) {
                if (!predicates.isEmpty()) {
                    capturesToRemove += predicateIterate(white, predicates, false).size();
                    Path.of(new Coordinate(Q_BISHOP_X, y), new Coordinate(K_BISHOP_X, y)).forEach(c -> {
                        if (predicates.stream().anyMatch(p -> p.test(c, c))) {
                            nonPawnCaptures.add(c);
                        }
                    });
                }
            }
        }
        return ofWhichQueen + ofWhichBishop + inaccessibleTakenRooks + capturesToRemove;
    }

    /**
     * Finds the rooks at Coordinates in the given Path inaccessible by pawns of the given colour,
     * adds them to the registered StateDetector's CaptureData, and returns the quantity.
     * @param board the board being checked
     * @param white the player whose pawns are being checked, true if white, false if black
     * @param rooks a Path of Coordinates of missing rooks of the opposing player
     * @return the number of rooks in the given Path which cannot be taken by pawns of the given player
     */
    private int findInaccessibleRooks(BoardInterface board, boolean white, Path rooks) {
        Map<Integer, Path> reachable = Map.of(Q_ROOK_X, new Path(), K_ROOK_X, new Path());
        // For each pawn Path, check if a missing rook's origin can path to a Coordinate on that Path
        if (!rooks.isEmpty()) {
            rooks.forEach(coordinate2 -> board.getBoardFacts().getCoordinates(white, "pawn")
                    .stream().filter(coordinate -> white ? (coordinate.getY() >= BLACK_PAWN_Y)  : (coordinate.getY() <= WHITE_PAWN_Y))
                    .forEach(coordinate -> {
                        if (!this.pathFinderUtil.findPiecePath(board, "rook", white ? "r" : "R",
                                coordinate2, coordinate).isEmpty()) {
                            reachable.get(coordinate2.getX()).add(coordinate);
                        }
                    }));
        }

        int innaccessibleTakenRooks = 0;
        //Check each rook is only pathing to one pawn
        for (int i = 0 ; i < rooks.size() ; i++){
            boolean increase = false;
            Coordinate coordinate = rooks.get(i);
            int size = reachable.get(coordinate.getX()).size();
            if (size == 0) {
                increase = true;
                innaccessibleTakenRooks += 1;
            } else if (!(rooks.size() == 1) && !(size > 1)) {
                int rookX = Math.abs(coordinate.getX() - K_ROOK_X);
                if (reachable.get(rookX).size() == 1
                        && !reachable.get(rookX).contains(coordinate)) {
                    increase = true;
                    innaccessibleTakenRooks += 1;
                }
            }
            if (increase) {
                this.detector.getCaptureData().getNonPawnCaptures(white).add(coordinate);
            }
        }
        return innaccessibleTakenRooks;
    }

    /**
     * Returns the difference between the number of captures made on the givenPaths and the
     * minimum number of captures made by pawns of the given player
     * @param singlePawns a Map of Coordinates and lists of Paths to be checked
     * @param white the player whose pawns are in the Map, true if white, false if black
     * @return the difference between the number of captures made on the givenPaths ant the
     * minimum number of captures made by pawns of the given player, or an arbitrarily high number if the number
     * of captures is 0
     */
    private int pawnCaptures(Map<Coordinate, List<Path>> singlePawns, boolean white) {
        int allCaptures = singlePawns.values().stream()
                .map(pathList -> pathList
                        .stream().map(PathfinderUtil.PATH_DEVIATION)
                        .reduce(Integer::min)
                        .orElse(0))
                .reduce(0, Integer::sum);
        if (allCaptures == 0 ) {
            return ARBITRARILY_HIGH_NUMBER;
        }
        return this.detector.getPawnData().minimumPawnCaptures(white) - allCaptures;
    }

    /**
     * Finds the pawns of the opposing player captured by pieces of the given player, sets the number of pawns
     * captured by pawns for the given player in the CaptureData, then finds which missing pawns of the opposing player
     * could not have been taken by pawns currently on the board of the given player, assuming the captured pawns
     * are not promoted, then returns these as a list of Coordinates
     * @param white the player whose pawns are doing the capturing, true if white, false if black
     * @param board the board being checked
     * @return a list of Coordinates of missing pawns of the opposing player that could not have been
     * captured by the given player if they did not promote
     */
    private List<Coordinate> pawnCaptureLocations(boolean white, BoardInterface board) {
        int maxPiecesOpponentCanTake = this.detector.getCaptureData().pawnTakeablePieces(!white);
        int numberOfPiecesPlayerHasRemaining = board.getBoardFacts().pieceNumbers(white);
        int numberOfPromotedPiecesPlayerHas = this.detector.getPromotionData().getPromotionNumbers().entrySet()
                .stream()
                .filter(entry -> entry.getKey().charAt(entry.getKey().length()-1) == (white ? 'w' : 'b'))
                .flatMap(entry -> entry.getValue().entrySet().stream())
                .filter(entry -> entry.getKey() != null)
                .map(entry -> entry.getKey().size() - entry.getValue())
                .reduce(Integer::sum)
                .orElse(0);

        int pawnsLostByPlayer = (MAX_PAWNS - board.getBoardFacts()
                .getCoordinates(white, "pawn").size()) - numberOfPromotedPiecesPlayerHas;
        int nonPawnsLostByPlayer = (maxPiecesOpponentCanTake - numberOfPiecesPlayerHasRemaining) -
                (pawnsLostByPlayer);
        int pawnCapturesByOpp = (this.detector.getPawnData().minimumPawnCaptures(!white) - nonPawnsLostByPlayer);
        if (pawnCapturesByOpp > 0) {
            // The deviation a pawn can make
            int unaccountedCaptures = (this.detector.getCaptureData().pawnTakeablePieces(white)
                    - board.getBoardFacts().pieceNumbers(!white))
                    - this.detector.getPawnData().minimumPawnCaptures(white);

            List<BiPredicate<Coordinate, Coordinate>> pawnPredicates = new LinkedList<>();
            List<Coordinate> missingPawns = new LinkedList<>();
            int y = white ? WHITE_PAWN_Y : BLACK_PAWN_Y;

            Path pawnOrigins = Path.of(this.detector.getPawnData().getPawnPaths(white).values().stream()
                    .flatMap(l -> l.stream().map(LinkedList::getFirst))
                    .collect(Collectors.toSet()));
            for (int x = 0 ; x <= K_ROOK_X ; x++) {
                    Coordinate c = new Coordinate(x, y);
                    // If that origin has no pawn
                    if (pawnOrigins
                            .stream().filter(c1 -> c1.getY() == y)
                            .noneMatch(c::equals)) {
                        pawnPredicates.add((c1, c2) ->(c1.equals(c) && c2.equals(c)) || c1.getX() != c2.getX() && Math.abs(c2.getX() - c.getX()) <= unaccountedCaptures);
                        missingPawns.add(c);
                    }
                }
            predicateIterate(!white, pawnPredicates ,true);
                if (Math.abs(pawnPredicates.size() - missingPawns.size()) >= pawnCapturesByOpp) {
                    return new LinkedList<>();
                }
                this.detector.getCaptureData().setPawnsCapturedByPawns(white, pawnCapturesByOpp
                        - Math.abs(pawnPredicates.size() - missingPawns.size()));

            return missingPawns.stream().filter(c -> pawnPredicates.stream().anyMatch(p -> p.test(c, c))).toList();
        }
        return new LinkedList<>();
    }

    /**
     * Goes through a given List of Coordinate BiPredicates, checking whether any pawn of the given player that
     * has one origin and one possible Path from the origin has a Path that has two adjacent Coordinates that fulfil
     * the BiPredicate condition. Used to check whether pawns of the given player have captured the pieces represented
     * by the BiPredicates. Two BiPredicates' conditions may not be fulfilled by the same two Coordinates on the Paths.
     * Returns the List of BiPredicates with the ones that have had their condition fulfilled removed.
     * If there are
     * <></>
     * If pawn = true all pawn Paths will be checked, otherwise only the Paths of pawns with one origin and one
     * Path from that origin.
     * @param white the player whose pawns are being checked, true if white, false if black
     * @param predicates the List of BiPredicates whose conditions are being checked
     * @param pawn whether pawns are being checked
     * @return the given List of BiPredicates with the ones fulfilled by the pawns removed
     */
    private List<BiPredicate<Coordinate, Coordinate>> predicateIterate(boolean white,
                                                                       List<BiPredicate<Coordinate, Coordinate>> predicates,
                                                                       boolean pawn) {
        Map<Coordinate, List<Path>> paths = pawn
                ? this.detector.getPawnData().getPawnPaths(white)
                : everySingularPawnPath(white);
        if (pawnCaptures(paths, white) < predicates.size()) {
            for (List<Path> pathList : paths.values()) {
                Path path = pathList.get(0);
                Iterator<BiPredicate<Coordinate, Coordinate>> predicateIterator = predicates.iterator();
                while (predicateIterator.hasNext()) {
                    BiPredicate<Coordinate, Coordinate> predicate = predicateIterator.next();
                    for (int j = 0; j < path.size() - 1; j++) {
                        if (predicate.test(path.get(j), path.get(j + 1))) {
                            predicateIterator.remove();
                            break;
                        }
                    }
                }
            }
        }
        else {
            predicates.clear();
            return new LinkedList<>();
        }
        return predicates;
    }

    /**
     * Returns a Map of Coordinates of pawn locations, and a List of their Paths, for
     * every pawn of the given player could only have taken the Path currently stored in the PawnData's
     * pawnPaths, as well as those that fulfill the pattern of being on the first rank away from their starting rank
     * with a pawn of the same colour y-1 away from them. These are the pawns and Paths that are usable for making
     * definite statements about captures made by pawns.
     * @param white the player whose pawns are being found, true if white, false if black
     * @return a Map of pawns and their Paths if there is only one Path the pawn could have taken to reach its
     * current location
     */
    private Map<Coordinate, List<Path>> everySingularPawnPath(boolean white) {
        Map<Coordinate, List<Path>> pathsInUse = this.detector.getPawnData().getPawnPaths(white);

        HashMap<Coordinate, List<Path>> returnMap = new HashMap<>(
                pathsInUse.entrySet().stream()
                        //On ranks 2 or 5
                        .filter(entry -> entry.getKey().getY() == (white ? WHITE_ESCAPE_Y : BLACK_ESCAPE_Y))
                        .filter(entry -> pathsInUse //There is a pawn behind it
                                .containsKey(new Coordinate(entry.getKey().getX(), white ? WHITE_PAWN_Y : BLACK_PAWN_Y)))
                        .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue)));

        returnMap.putAll(this.detector.getPawnData().getPawnPaths(white)
                .entrySet()
                .stream()
                .filter(entry -> !returnMap.containsKey(entry.getKey()))
                .filter(entry -> entry.getValue().size() == 1 && !(entry.getValue().get(0).size() == 1))
                .filter(e -> {
                    int deviation = PathfinderUtil.PATH_DEVIATION.apply(e.getValue().get(0));
                    return (deviation >= Math.abs(e.getKey().getY() - e.getValue().get(0).getFirst().getY()) ||
                                    deviation == 0);})
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue)));
        return  returnMap; //Every path that's a single path;
    }
}
