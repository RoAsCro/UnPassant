package Heuristics.Deductions;

import Heuristics.BoardInterface;
import Heuristics.Observation;
import Heuristics.Observations.PawnNumber;
import Heuristics.Observations.PieceNumber;
import Heuristics.Path;
import StandardChess.Coordinate;
import StandardChess.Coordinates;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.function.BiPredicate;
import java.util.stream.Collectors;

public class CaptureLocations extends AbstractDeduction {

    PawnNumber pawnNumber;
    PieceNumber pieceNumber;
    PieceMap pieceMap;
    PawnMapWhite pawnMapWhite;
    PawnMapBlack pawnMapBlack;
    CombinedPawnMap combinedPawnMap;
    Path whiteCagedCaptures = new Path();
    Path blackCagedCaptures = new Path();

    private final Path promotedWhitePawns = new Path();
    private final Path promotedBlackPawns = new Path();
    private static final BiPredicate<Coordinate, Coordinate> DARK_TEST =
            (c1, c2) -> c2.getX() != c1.getX() && !Coordinates.light(c1);
    private static final BiPredicate<Coordinate, Coordinate> LIGHT_TEST =
            (c1, c2) -> c2.getX() != c1.getX() && Coordinates.light(c1);




    public CaptureLocations(PawnMapWhite pawnMapWhite, PawnMapBlack pawnMapBlack, PieceMap pieceMap, CombinedPawnMap combinedPawnMap) {
        this.pieceMap = pieceMap;
        this.pawnMapWhite = pawnMapWhite;
        this.pawnMapBlack = pawnMapBlack;
        this.combinedPawnMap = combinedPawnMap;
        this.pieceNumber = (PieceNumber) pawnMapWhite.getObservations().stream().filter(o -> o instanceof PieceNumber).findAny().orElse(null);
        this.pawnNumber = (PawnNumber) pawnMapWhite.getObservations().stream().filter(o -> o instanceof PawnNumber).findAny().orElse(null);

    }
    @Override
    public List<Observation> getObservations() {
        return null;
    }

    public Path getCagedCaptures(boolean white) {
        return white ? whiteCagedCaptures : blackCagedCaptures;
    }

    @Override
    public boolean deduce(BoardInterface board) {
        int whiteRemovals = reductions(board, true);
        int blackRemovals = reductions(board, false);
        if (
                whiteRemovals + blackRemovals != 0
        ) {
            this.pawnMapWhite.updateMaxCapturedPieces(whiteRemovals);
            this.pawnMapBlack.updateMaxCapturedPieces(blackRemovals);
            this.pawnMapWhite.deduce(board);
            this.pawnMapBlack.deduce(board);
            this.combinedPawnMap.deduce(board);
            this.pieceMap.deduce(board);
        }


        if (!(this.pawnMapWhite.getState() && this.pawnMapBlack.getState()
                && this.combinedPawnMap.getState() && this.pieceMap.getState())) {
            this.state = false;
            return false;
        }
        this.promotedWhitePawns.addAll(pawnCaptureLocations(true));
        this.promotedBlackPawns.addAll(pawnCaptureLocations(false));
        return false ;
    }

    private int reductions(BoardInterface board, boolean white) {
        int capturesToRemove = 0;
        Path ofWhichCaged = Path.of(this.pieceMap.getCaged().entrySet().stream()
                .filter(entry -> entry.getKey().getY() == (white ? FINAL_RANK_Y : FIRST_RANK_Y))
                .filter(Map.Entry::getValue) //Is Caged
                .filter(entry -> {
                    Map<Coordinate, Path> map = this.pieceMap.getStartLocations().get(entry.getKey());
                    if (map.isEmpty()){
                        return true;
                    }
                    if (map.size() == 1) {
//                        System.out.println(entry.getKey());
                        return this.pieceMap.getStartLocations()
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
                    .stream().filter(coordinate -> white ? (coordinate.getY() >= BLACK_PIECE_Y)  : (coordinate.getY() <= WHITE_PIECE_Y))
                    .forEach(coordinate -> {
                        if (!this.pieceMap.findPath(board, "rook", white ? "r" : "R", coordinate2, coordinate).isEmpty()) {
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
                if (white) {
                    this.whiteCagedCaptures.add(coordinate);
                } else {
                    this.blackCagedCaptures.add(coordinate);
                }
            }
        }

        // Account for bishops being taken on the correct colour
        // this is only done in situations where all captures made by pawns are made by certain pawn paths

        List<BiPredicate<Coordinate, Coordinate>> predicates =
                new LinkedList<>(this.pieceMap.getStartLocations().entrySet().stream()
                        .filter(entry -> entry.getKey().getY() == (white ? FINAL_RANK_Y : FIRST_RANK_Y)) //Correct colour
                        .filter(entry -> entry.getKey().getX() == Q_BISHOP_X || entry.getKey().getX() == K_BISHOP_X) //Is a Bishop
                        .filter(entry -> !(this.pieceMap.getCaged().get(entry.getKey()))) //Is not Caged
                        .filter(entry -> entry.getValue().isEmpty()) //Is missing
                        .map(entry -> ((!Coordinates.light(entry.getKey())  ? DARK_TEST : LIGHT_TEST)))
                        .toList());

        if (!predicates.isEmpty()) {

            Map<Coordinate, List<Path>> paths = everySingularPawnPath(white);//Every path that's a single path;

            if (pawnCaptures(paths, white)) {
                if (!predicates.isEmpty()) {
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
        int otherValue = singlePawns.values().stream()
                .map(pathList -> pathList
                        .stream().map(CombinedPawnMap.PATH_DEVIATION)
                        .reduce((i, j) -> i < j ? i : j)
                        .orElse(0))
                .reduce(0, Integer::sum);
        return otherValue == this.combinedPawnMap.minimumCaptures(white) && otherValue != 0;
    }

    private List<Coordinate> pawnCaptureLocations(boolean white) {
//        System.out.println((!white ? this.pawnMapWhite : this.pawnMapBlack).maxPieces);
//        System.out.println((white ? pieceNumber.getWhitePieces() : pieceNumber.getBlackPieces()));
//        System.out.println(this.combinedPawnMap.capturesTwo(white ? "black" : "white"));

        // If every capture of the opponent has been made by pawns
        if ((!white ? this.pawnMapWhite : this.pawnMapBlack).maxPieces - (white ? pieceNumber.getWhitePieces() : pieceNumber.getBlackPieces())
                == this.combinedPawnMap.minimumCaptures(!white)) {
            // The deviation a pawn can make
            int unnaccountedCaptures = ((!white ? this.pawnMapBlack : this.pawnMapWhite).maxPieces - (white ? pieceNumber.getBlackPieces() : pieceNumber.getWhitePieces()))
                    - this.combinedPawnMap.minimumCaptures(white);
            List<BiPredicate<Coordinate, Coordinate>> pawnPredicates = new LinkedList<>();
            List<Coordinate> missingPawns = new LinkedList<>();
            Map<Coordinate, List<Path>> otherPlayerPaths = everySingularPawnPath(!white);
            // Might always be true
            if (pawnCaptures(otherPlayerPaths, !white)) {
                int y = white ? WHITE_PIECE_Y : BLACK_PIECE_Y;
                for (int x = 0 ; x < K_ROOK_X ; x++) {
                    Coordinate c = new Coordinate(x, y);
                    // If that origin has no pawn - should only be possible if there is a pawn missing
                    if ((white ? this.pawnMapWhite : this.pawnMapBlack).getPawnOrigins().values()
                            .stream().flatMap(Path::stream).noneMatch(c::equals)) {
                        pawnPredicates.add((c1, c2) -> (c1.equals(c) && c2.equals(c)) || c1.getX() != c2.getX() && Math.abs(c2.getX() - c.getX()) <= unnaccountedCaptures);
                        missingPawns.add(c);
                    }
                }
                List<BiPredicate<Coordinate, Coordinate>> newPredicates = predicateIterate(!white, pawnPredicates);
                return missingPawns.stream().filter(c -> pawnPredicates.stream().anyMatch(p -> p.test(c, c))).toList();
            }
        }
        return new LinkedList<>();
    }

    private List<BiPredicate<Coordinate, Coordinate>> predicateIterate(boolean white, List<BiPredicate<Coordinate, Coordinate>> predicates) {
        Map<Coordinate, List<Path>> paths = everySingularPawnPath(white);
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
        return predicates;
    }

    private Map<Coordinate, List<Path>> everySingularPawnPath(boolean white) {
        return (white ? this.combinedPawnMap.getWhitePaths() : this.combinedPawnMap.getBlackPaths())
                .entrySet()
                .stream()
                .filter(entry -> this.combinedPawnMap.getSinglePath(white, entry.getKey()) != null)
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue)); //Every path that's a single path;
    }

    public Path getPromotedPawns(boolean white) {
        return white ? this.promotedWhitePawns : this.promotedBlackPawns;
    }

}
