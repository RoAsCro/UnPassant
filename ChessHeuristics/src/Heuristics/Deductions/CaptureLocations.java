package Heuristics.Deductions;

import Heuristics.BoardInterface;
import Heuristics.Observation;
import Heuristics.Path;
import Heuristics.StateDetector;
import StandardChess.Coordinate;
import StandardChess.Coordinates;

import java.util.*;
import java.util.function.BiPredicate;
import java.util.stream.Collectors;

public class CaptureLocations extends AbstractDeduction {
    private PiecePathFinderUtil pathFinderUtil;
    private static final BiPredicate<Coordinate, Coordinate> DARK_TEST =
            (c1, c2) -> c2.getX() != c1.getX() && !Coordinates.light(c1);
    private static final BiPredicate<Coordinate, Coordinate> LIGHT_TEST =
            (c1, c2) -> c2.getX() != c1.getX() && Coordinates.light(c1);




    public CaptureLocations(PawnMapWhite pawnMapWhite, PawnMapBlack pawnMapBlack, PieceMap pieceMap, CombinedPawnMap combinedPawnMap) {
    }

    public CaptureLocations() {}

    @Override
    public void registerDetector(StateDetector detector) {
        super.registerDetector(detector);
        this.pathFinderUtil = new PiecePathFinderUtil(detector);
    }
    @Override
    public List<Observation> getObservations() {
        return null;
    }

    public Path getCagedCaptures(boolean white) {
        return this.detector.getCagedCaptures(white);
    }

    @Override
    public boolean deduce(BoardInterface board) {
        int whiteRemovals = reductions(board, true);
        int blackRemovals = reductions(board, false);
        if (
                whiteRemovals + blackRemovals != 0
        ) {
            System.out.println("sdsds");
            this.detector.reducePawnTakeablePieces(true, whiteRemovals);
            this.detector.reducePawnTakeablePieces(false, blackRemovals);
            this.detector.reTest(board);
        }


//        if (!(this.pawnMapWhite.getState() && this.pawnMapBlack.getState()
//                && this.combinedPawnMap.getState() && this.pieceMap.getState())) {
//            this.state = false;
//            return false;
//        }
        this.detector.getPromotedPawns(true).addAll(pawnCaptureLocations(true));
        this.detector.getPromotedPawns(false).addAll(pawnCaptureLocations(false));
        //System.out.println(promotedBlackPawns);
        return false ;
    }

    private int reductions(BoardInterface board, boolean white) {
        int capturesToRemove = 0;
        Path ofWhichCaged = Path.of(this.detector.getCaged().entrySet().stream()
                .filter(entry -> entry.getKey().getY() == (white ? FINAL_RANK_Y : FIRST_RANK_Y))
                .filter(Map.Entry::getValue) //Is Caged
                .filter(entry -> {
                    Map<Coordinate, Path> map = this.detector.getStartLocations().get(entry.getKey());
                    if (map.isEmpty()){
                        return true;
                    }
                    if (map.size() == 1) {
//                        System.out.println(entry.getKey());
                        return this.detector.getStartLocations()
                                .get(new Coordinate(Math.abs(FINAL_RANK_Y - entry.getKey().getX()), entry.getKey().getY()))
                                .containsKey(map.keySet().stream().findAny().orElse(Coordinates.NULL_COORDINATE))
                                && entry.getKey().getX() == 0;
                    }
                    return false;
                }) //Is missing
                .map(Map.Entry::getKey)
                .toList());
        int ofWhichBishop = (int) ofWhichCaged.stream()
                .filter(coordinate -> coordinate.getX() == K_BISHOP_X || coordinate.getX() == Q_BISHOP_X) // Is a bishop
                .count();

        // Rooks are the only piece capable of being both caged and captured on the pawn rank
        Path ofWhichRook = Path.of(ofWhichCaged.stream()
                .filter(coordinate -> coordinate.getX() == 0 || coordinate.getX() == 7) // Is a rook
                .toList());

        int ofWhichQueen = ofWhichCaged.size() - ofWhichBishop - ofWhichRook.size();


        Map<Integer, Path> reachable = Map.of(Q_ROOK_X, new Path(), K_ROOK_X, new Path());
        // Check each rook can path to any opposing pawn
        if (!ofWhichRook.isEmpty()) {
            ofWhichRook.forEach(coordinate2 -> board.getBoardFacts().getCoordinates(white, "pawn")
                    .stream().filter(coordinate -> white ? (coordinate.getY() >= BLACK_PAWN_Y)  : (coordinate.getY() <= WHITE_PAWN_Y))
                    .forEach(coordinate -> {
                        if (!this.pathFinderUtil.findPath(board, "rook", white ? "r" : "R", coordinate2, coordinate).isEmpty()) {
                            reachable.get(coordinate2.getX()).add(coordinate);
                        }
                    }));
        }

        int innaccessibleTakenRooks = 0;
        //Check each rook is only pathing to one pawn
        for (int i = 0 ; i < ofWhichRook.size() ; i++){
            boolean increase = false;
            Coordinate coordinate = ofWhichRook.get(i);
            int size = reachable.get(coordinate.getX()).size();

//            System.out.println(size);
            if (size == 0) {
//                System.out.println("ZERO");
                increase = true;
                innaccessibleTakenRooks += 1;
            } else if (!(ofWhichRook.size() == 1) && !(size > 1)) {
                int rookX = Math.abs(coordinate.getX() - K_ROOK_X);
                if (reachable.get(rookX).size() == 1
                        && !reachable.get(rookX).contains(coordinate)) {
                    increase = true;
                    innaccessibleTakenRooks += 1;
                }
            }
            if (increase) {
                this.detector.getCagedCaptures(white).add(coordinate);
            }
        }

        // Account for bishops being taken on the correct colour
        // this is only done in situations where all captures made by pawns are made by certain pawn paths

        List<BiPredicate<Coordinate, Coordinate>> predicates =
                new LinkedList<>(this.detector.getStartLocations().entrySet().stream()
                        .filter(entry -> entry.getKey().getY() == (white ? FINAL_RANK_Y : FIRST_RANK_Y)) //Correct colour
                        .filter(entry -> entry.getKey().getX() == Q_BISHOP_X || entry.getKey().getX() == K_BISHOP_X) //Is a Bishop
                        .filter(entry -> !(this.detector.getCaged().get(entry.getKey()))) //Is not Caged
                        .filter(entry -> entry.getValue().isEmpty()) //Is missing
                        .map(entry -> ((!Coordinates.light(entry.getKey())  ? DARK_TEST : LIGHT_TEST)))
                        .toList());
        if (!predicates.isEmpty()) {
//            System.out.println(white);
//            System.out.println(predicates.size());
            Map<Coordinate, List<Path>> paths = everySingularPawnPath(white);//Every path that's a single path;

            if (pawnCaptures(paths, white)) {
                if (!predicates.isEmpty()) {
//                    System.out.println("What");
                    capturesToRemove += predicateIterate(white, predicates).size();
                }
            }
        }
//        System.out.println("finalVerdict");
//        System.out.println(white);
//        System.out.println(capturesToRemove);
//        System.out.println(ofWhichBishop);
//        System.out.println(ofWhichQueen);
//        System.out.println(innaccessibleTakenRooks);

        return ofWhichQueen + ofWhichBishop + innaccessibleTakenRooks + capturesToRemove;
    }

    /**
     * Returns true if the number of captures made on the given paths is equal to minimum number of captures made by pawns
     * @param singlePawns
     * @return
     */
    private boolean pawnCaptures(Map<Coordinate, List<Path>> singlePawns, boolean white) {
//        System.out.println(white);
//        System.out.println(singlePawns);
        int otherValue = singlePawns.values().stream()
                .map(pathList -> pathList
                        .stream().map(CombinedPawnMap.PATH_DEVIATION)
                        .reduce((i, j) -> i < j ? i : j)
                        .orElse(0))
                .reduce(0, Integer::sum);
//        System.out.println("p");
//        System.out.println(otherValue);
//
//        System.out.println(this.combinedPawnMap.minimumCaptures(white));
        return otherValue == this.detector.minimumPawnCaptures(white) && otherValue != 0;
    }

    private List<Coordinate> pawnCaptureLocations(boolean white) {
//        System.out.println((!white ? this.pawnMapWhite : this.pawnMapBlack).maxPieces);
//        System.out.println((white ? pieceNumber.getWhitePieces() : pieceNumber.getBlackPieces()));
//        System.out.println(this.combinedPawnMap.capturesTwo(white ? "black" : "white"));

        // If every capture of the opponent has been made by pawns
        //System.out.println(white);

        //System.out.println((!white ? this.pawnMapWhite : this.pawnMapBlack).maxPieces);
        //System.out.println(white ? pieceNumber.getWhitePieces() : pieceNumber.getBlackPieces());
        //System.out.println(this.combinedPawnMap.minimumCaptures(!white));
        int maxPiecesOpponentCanTake = this.detector.pawnTakeablePieces(!white);
        int numberOfPiecesPlayerHasRemaining = (white ? this.detector.getPieceNumber().getWhitePieces() : this.detector.getPieceNumber().getBlackPieces());
        //System.out.println(this.pieceMap.getPromotionNumbers());
        int numberOfPromotedPiecesPlayerHas = this.detector.getPromotionNumbers().entrySet()
                .stream()
                .filter(entry -> entry.getKey().charAt(entry.getKey().length()-1) == (white ? 'w' : 'b'))
                .flatMap(entry -> entry.getValue().entrySet().stream())
                .filter(entry -> entry.getKey() != null)
                .map(entry -> entry.getKey().size() - entry.getValue())
                .reduce(Integer::sum)
                .orElse(0);

        System.out.println(numberOfPromotedPiecesPlayerHas);
        int numberOfPawnsPlayerHasLost = (MAX_PAWNS - this.detector.getPawnNumbers(white)) - numberOfPromotedPiecesPlayerHas;
        System.out.println(numberOfPawnsPlayerHasLost);

        // MINUS THE NUMBER OF PROMOTED PIECES ON THE BOARD
        // I think the max pieces needs to be reversed
        int nonPawnsPlayerHasLost = (maxPiecesOpponentCanTake - numberOfPiecesPlayerHasRemaining) -
                (numberOfPawnsPlayerHasLost);
        //System.out.println(nonPawnsPlayerHasLost);
        int pCBP = (this.detector.minimumPawnCaptures(!white) - nonPawnsPlayerHasLost);
        if (pCBP > 0) {
            System.out.println("bbbbb");
            System.out.println(maxPiecesOpponentCanTake);
            System.out.println(numberOfPiecesPlayerHasRemaining);
            System.out.println(numberOfPawnsPlayerHasLost);
            System.out.println(pCBP);




            // The deviation a pawn can make
            int unnaccountedCaptures = (this.detector.pawnTakeablePieces(white) - (white ? this.detector.getPieceNumber().getBlackPieces() : this.detector.getPieceNumber().getWhitePieces()))
                    - this.detector.minimumPawnCaptures(white);
            List<BiPredicate<Coordinate, Coordinate>> pawnPredicates = new LinkedList<>();
            List<Coordinate> missingPawns = new LinkedList<>();
            Map<Coordinate, List<Path>> otherPlayerPaths = everySingularPawnPath(!white);
            // TODO
            // Without the commented out if statement, this can easily produce false negatives - watch out when game testing
//            if (pawnCaptures(otherPlayerPaths, !white)) {
            // if (minimum captures equals max captures???)
                int y = white ? WHITE_PAWN_Y : BLACK_PAWN_Y;
                for (int x = 0 ; x <= K_ROOK_X ; x++) {
                    Coordinate c = new Coordinate(x, y);
//                    System.out.println(c);
//                    System.out.println(this.pawnMapBlack.getPawnOrigins());
                    // If that origin has no pawn - should only be possible if there is a pawn missing
                    if (this.detector.getPawnOrigins(white).values()
                            .stream().flatMap(Path::stream).noneMatch(c::equals)) {
                        pawnPredicates.add((c1, c2) -> (c1.equals(c) && c2.equals(c)) || c1.getX() != c2.getX() && Math.abs(c2.getX() - c.getX()) <= unnaccountedCaptures);
                        missingPawns.add(c);
                    }
                }
                //System.out.println(pawnPredicates.size());
                List<BiPredicate<Coordinate, Coordinate>> newPredicates = predicateIterate(!white, pawnPredicates);
                //System.out.println(pawnPredicates.size());
                if (Math.abs(pawnPredicates.size() - missingPawns.size()) >= pCBP) {
                    return new LinkedList<>();
                }
                this.detector.setPawnsCapturedByPawns(white, pCBP - Math.abs(pawnPredicates.size() - missingPawns.size()));
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
        Map<Coordinate, List<Path>> pathsInUse = this.detector.getPawnPaths(white);

        HashMap<Coordinate, List<Path>> returnMap = new HashMap<Coordinate, List<Path>>(
                pathsInUse.entrySet().stream()
                .filter(entry -> entry.getKey().getY() == (white ? WHITE_ESCAPE_Y : BLACK_ESCAPE_Y))
                .filter(entry -> pathsInUse
                        .containsKey(new Coordinate(entry.getKey().getX(), white ? WHITE_PAWN_Y : BLACK_PAWN_Y)))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue))
        );
//        System.out.println(returnMap);
        returnMap.putAll(this.detector.getPawnPaths(white)
                .entrySet()
                .stream()
                .filter(entry -> !returnMap.containsKey(entry.getKey()))
                .filter(entry -> this.detector.getSinglePawnPaths(white).get(entry.getKey()) != null)
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue)));

        return  returnMap; //Every path that's a single path;
    }

    public Path getPromotedPawns(boolean white) {
        return this.detector.getPromotedPawns(white);
    }

    public int getWhitePawnsCapturedByPawns() {
        return this.detector.getPawnsCapturedByPawns(true);
    }

    public int getBlackPawnsCapturedByPawns() {
        return this.detector.getPawnsCapturedByPawns(false);
    }
}
