package Heuristics.Deductions;

import Heuristics.BoardInterface;
import Heuristics.Path;
import Heuristics.Detector.StateDetector;
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
 * The state is set to false if after rerunning the previously completed Deductions, any of them are set to false.
 * CaptureLocations must only run deduce() after the pawns and pieces have been mapped, cages determined,
 * and certain promotions discovered, otherwise its results will not be accurate.
 */
public class CaptureLocations extends AbstractDeduction {
    private PathfinderUtil pathFinderUtil;
    private static final BiPredicate<Coordinate, Coordinate> DARK_TEST =
            (c1, c2) -> (c2.getX() != c1.getX() || c1.equals(c2) ) && !Coordinates.light(c1);
    private static final BiPredicate<Coordinate, Coordinate> LIGHT_TEST =
            (c1, c2) -> (c2.getX() != c1.getX() || c1.equals(c2))  && Coordinates.light(c1);

    public CaptureLocations() {
        super("Too many pawn captures - not all missing pieces are reachable.");
    }

    @Override
    public void registerDetector(StateDetector detector) {
        super.registerDetector(detector);
        this.pathFinderUtil = new PathfinderUtil(detector);
    }

    @Override
    public void deduce(BoardInterface board) {
        int whiteRemovals = reductions(board, true);
        int blackRemovals = reductions(board, false);
        if (
                whiteRemovals + blackRemovals != 0
        ) {
            //System.out.println("sdsds" + whiteRemovals + " " + blackRemovals);
//            this.detector.reducePawnTakeablePieces(true, whiteRemovals);
//            this.detector.reducePawnTakeablePieces(false, blackRemovals);
            this.detector.reTest(board);
        }

        this.detector.getCaptureData().getNonPawnCaptures(true).addAll(pawnCaptureLocations(true, board));
        this.detector.getCaptureData().getNonPawnCaptures(false).addAll(pawnCaptureLocations(false, board));
        //System.out.println(promotedBlackPawns);
    }

    private int reductions(BoardInterface board, boolean white) {
        int y = white ? FINAL_RANK_Y : FIRST_RANK_Y;
        int capturesToRemove = 0;
        Path ofWhichCaged = Path.of(this.detector.getPieceData().getCaged().entrySet().stream()
                .filter(entry -> entry.getKey().getY() == y)
                .filter(Map.Entry::getValue) //Is Caged
                .filter(entry -> {
                    Map<Coordinate, Path> map = this.detector.getPieceData().getPiecePaths().get(entry.getKey());
                    if (map.isEmpty()){
                        return true;
                    }
                    if (map.size() == 1) {
//                        System.out.println(entry.getKey());
                        return this.detector.getPieceData().getPiecePaths()
                                .get(new Coordinate(Math.abs(FINAL_RANK_Y - entry.getKey().getX()), entry.getKey().getY()))
                                .containsKey(map.keySet().stream().findAny().orElse(Coordinates.NULL_COORDINATE))
                                && entry.getKey().getX() == 0;
                    }
                    return false;
                }) //Is missing
                .map(Map.Entry::getKey)
                .toList());
        int ofWhichBishop = (int) ofWhichCaged.stream()
                .filter(coordinate -> {
                    boolean bishop = coordinate.getX() == K_BISHOP_X || coordinate.getX() == Q_BISHOP_X;
                    if (bishop) {
                        this.detector.getCaptureData().getNonPawnCaptures(white).add(coordinate);
                    }
                    return bishop;
                }) // Is a bishop
                .count();

        // Rooks are the only piece capable of being both caged and captured on the pawn rank
        Path ofWhichRook = Path.of(ofWhichCaged.stream()
                .filter(coordinate -> coordinate.getX() == Q_ROOK_X || coordinate.getX() == K_ROOK_X) // Is a rook
                .toList());

        int ofWhichQueen = ofWhichCaged.size() - ofWhichBishop - ofWhichRook.size();
        if (ofWhichQueen > 0) {
            this.detector.getCaptureData().getNonPawnCaptures(white).add(new Coordinate(QUEEN_X, y));
        }

        int inaccessibleTakenRooks = 0;
        // Check each rook can path to any opposing pawn
        if (!ofWhichRook.isEmpty()) {
             inaccessibleTakenRooks = findInaccessibleRooks(board, white, ofWhichRook);
        }

        // Account for bishops being taken on the correct colour
        // this is only done in situations where all captures made by pawns are made by certain pawn paths
        List<BiPredicate<Coordinate, Coordinate>> predicates =
                new LinkedList<>(this.detector.getPieceData().getPiecePaths().entrySet().stream()
                        .filter(entry -> entry.getKey().getY() == (white ? FINAL_RANK_Y : FIRST_RANK_Y)) //Correct colour
                        .filter(entry -> entry.getKey().getX() == Q_BISHOP_X || entry.getKey().getX() == K_BISHOP_X) //Is a Bishop
                        .filter(entry -> !(this.detector.getPieceData().getCaged().get(entry.getKey()))) //Is not Caged
                        .filter(entry -> entry.getValue().isEmpty()) //Is missing
                        .map(entry -> ((!Coordinates.light(entry.getKey())  ? DARK_TEST : LIGHT_TEST)))
                        .toList());
        if (!predicates.isEmpty()) {
            Map<Coordinate, List<Path>> paths = everySingularPawnPath(white);//Every path that's a single path;

            if (pawnCaptures(paths, white)) {
                if (!predicates.isEmpty()) {
                    capturesToRemove += predicateIterate(white, predicates).size();
                    Path.of(new Coordinate(Q_BISHOP_X, y), new Coordinate(K_BISHOP_X, y))
                                    .forEach(c -> {
                                        if (predicates.stream().anyMatch(p -> p.test(c, c))) {
                                            this.detector.getCaptureData().getNonPawnCaptures(white).add(c);
                                        }
                                    });
                }
            }
        }
//        if (this.detector.getNonPawnCaptures(white).size() != ofWhichQueen + ofWhichBishop + inaccessibleTakenRooks + capturesToRemove) {
//            throw new RuntimeException();
//        }
        return ofWhichQueen + ofWhichBishop + inaccessibleTakenRooks + capturesToRemove;
    }

    private int findInaccessibleRooks(BoardInterface board, boolean white, Path rooks) {
        Map<Integer, Path> reachable = Map.of(Q_ROOK_X, new Path(), K_ROOK_X, new Path());
        if (!rooks.isEmpty()) {
            rooks.forEach(coordinate2 -> board.getBoardFacts().getCoordinates(white, "pawn")
                    .stream().filter(coordinate -> white ? (coordinate.getY() >= BLACK_PAWN_Y)  : (coordinate.getY() <= WHITE_PAWN_Y))
                    .forEach(coordinate -> {
                        if (!this.pathFinderUtil.findPiecePath(board, "rook", white ? "r" : "R", coordinate2, coordinate).isEmpty()) {
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
     * Returns true if the number of captures made on the given paths is equal to minimum number of captures made by pawns
     * @param singlePawns
     * @return
     */
    private boolean pawnCaptures(Map<Coordinate, List<Path>> singlePawns, boolean white) {
        int otherValue = singlePawns.values().stream()
                .map(pathList -> pathList
                        .stream().map(PathfinderUtil.PATH_DEVIATION)
                        .reduce((i, j) -> i < j ? i : j)
                        .orElse(0))
                .reduce(0, Integer::sum);
        return otherValue == this.detector.getPawnData().minimumPawnCaptures(white) && otherValue != 0;
    }

    private List<Coordinate> pawnCaptureLocations(boolean white, BoardInterface boardInterface) {
        // If every capture of the opponent has been made by pawns
        int maxPiecesOpponentCanTake = this.detector.getCaptureData().pawnTakeablePieces(!white);
        int numberOfPiecesPlayerHasRemaining = boardInterface.getBoardFacts().pieceNumbers(white);
        int numberOfPromotedPiecesPlayerHas = this.detector.getPromotionData().getPromotionNumbers().entrySet()
                .stream()
                .filter(entry -> entry.getKey().charAt(entry.getKey().length()-1) == (white ? 'w' : 'b'))
                .flatMap(entry -> entry.getValue().entrySet().stream())
                .filter(entry -> entry.getKey() != null)
                .map(entry -> entry.getKey().size() - entry.getValue())
                .reduce(Integer::sum)
                .orElse(0);

        //System.out.println(numberOfPromotedPiecesPlayerHas);
        int numberOfPawnsPlayerHasLost = (MAX_PAWNS - boardInterface.getBoardFacts().getCoordinates(white, "pawn").size()) - numberOfPromotedPiecesPlayerHas;
        //System.out.println(numberOfPawnsPlayerHasLost);

        // MINUS THE NUMBER OF PROMOTED PIECES ON THE BOARD
        // I think the max pieces needs to be reversed? Maybe not
        int nonPawnsPlayerHasLost = (maxPiecesOpponentCanTake - numberOfPiecesPlayerHasRemaining) -
                (numberOfPawnsPlayerHasLost);
        int pCBP = (this.detector.getPawnData().minimumPawnCaptures(!white) - nonPawnsPlayerHasLost);
        //System.out.println(white);

        //System.out.println(pCBP);
        if (pCBP > 0) {


            // The deviation a pawn can make
            int unnaccountedCaptures = (this.detector.getCaptureData().pawnTakeablePieces(white) - boardInterface.getBoardFacts().pieceNumbers(!white))
                    - this.detector.getPawnData().minimumPawnCaptures(white);
            List<BiPredicate<Coordinate, Coordinate>> pawnPredicates = new LinkedList<>();
            List<Coordinate> missingPawns = new LinkedList<>();
            Map<Coordinate, List<Path>> otherPlayerPaths = everySingularPawnPath(!white);
            // TODO decision on this
            // Without the commented out if statement, this can easily produce false negatives - watch out when game testing
//            if (pawnCaptures(otherPlayerPaths, !white)) {

                int y = white ? WHITE_PAWN_Y : BLACK_PAWN_Y;
//            Path pawnOrigins = Path.of(Stream.iterate(0, i -> i + 1).limit(MAX_PAWNS).map(i -> new Coordinate(i, y)).toList());
//            System.out.println("BBBB" + pawnOrigins);
            Path pawnOrigins = Path.of(this.detector.getPawnData().getPawnPaths(white).values().stream()
                    .flatMap(l -> l.stream().map(LinkedList::getFirst))
                    .collect(Collectors.toSet()));
//            System.out.println(this.detector.getPawnOrigins(white).values().stream().flatMap(Path::stream).toList());

            for (int x = 0 ; x <= K_ROOK_X ; x++) {
                    Coordinate c = new Coordinate(x, y);
                    // If that origin has no pawn - should only be possible if there is a pawn missing
                    if (pawnOrigins
                            .stream().filter(c1 -> c1.getY() == y)
                            .noneMatch(c::equals)) {
                        pawnPredicates.add((c1, c2) -> (c1.equals(c) && c2.equals(c)) || c1.getX() != c2.getX() && Math.abs(c2.getX() - c.getX()) <= unnaccountedCaptures);
                        missingPawns.add(c);
                    }
                }
                //System.out.println(pawnPredicates.size());
                predicateIterate(!white, pawnPredicates);
                //System.out.println(pawnPredicates.size());
                if (Math.abs(pawnPredicates.size() - missingPawns.size()) >= pCBP) {
                    return new LinkedList<>();
                }
//                this.detector.getNonPawnCaptures(white)
                this.detector.getCaptureData().setPawnsCapturedByPawns(white, pCBP - Math.abs(pawnPredicates.size() - missingPawns.size()));
                return missingPawns.stream().filter(c -> pawnPredicates.stream().anyMatch(p -> p.test(c, c))).toList();
//            }
        }
        return new LinkedList<>();
    }

    private List<BiPredicate<Coordinate, Coordinate>> predicateIterate(boolean white, List<BiPredicate<Coordinate, Coordinate>> predicates) {
        Map<Coordinate, List<Path>> paths = everySingularPawnPath(white);
        //System.out.println(paths);
        if (pawnCaptures(paths, white)) {
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
//        System.out.println(predicates.size());
        return predicates;
    }

    private Map<Coordinate, List<Path>> everySingularPawnPath(boolean white) {
        Map<Coordinate, List<Path>> pathsInUse = this.detector.getPawnData().getPawnPaths(white);

        HashMap<Coordinate, List<Path>> returnMap = new HashMap<Coordinate, List<Path>>(
                pathsInUse.entrySet().stream()
                .filter(entry -> entry.getKey().getY() == (white ? WHITE_ESCAPE_Y : BLACK_ESCAPE_Y)) //On ranks 2 or 5
                .filter(entry -> pathsInUse //There is a pawn behind it
                        .containsKey(new Coordinate(entry.getKey().getX(), white ? WHITE_PAWN_Y : BLACK_PAWN_Y)))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue))
        );

        returnMap.putAll(this.detector.getPawnData().getPawnPaths(white)
                .entrySet()
                .stream()
                .filter(entry -> !returnMap.containsKey(entry.getKey()))
                .filter(entry -> entry.getValue().size() == 1 && !(entry.getValue().get(0).size() == 1))
                .filter(e -> {
                    int deviation = PathfinderUtil.PATH_DEVIATION.apply(e.getValue().get(0));
                    return (
//                            deviation >= getMaxCaptures(!white, e.getKey()) ||
                            deviation >= Math.abs(e.getKey().getY() - e.getValue().get(0).getFirst().getY()) ||
                                    deviation == 0);
                } )
//                .filter(entry -> this.detector.getSinglePawnPaths(white).get(entry.getKey()) != null)
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue)));

        return  returnMap; //Every path that's a single path;
    }
}
