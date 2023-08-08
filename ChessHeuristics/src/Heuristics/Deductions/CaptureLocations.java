package Heuristics.Deductions;

import Heuristics.BoardInterface;
import Heuristics.Observation;
import Heuristics.Observations.PawnNumber;
import Heuristics.Observations.PieceNumber;
import Heuristics.Path;
import StandardChess.Coordinate;

import java.sql.Time;
import java.util.*;
import java.util.function.BiPredicate;

public class CaptureLocations extends AbstractDeduction {

    PawnNumber pawnNumber;
    PieceNumber pieceNumber;
    PieceMap pieceMap;
    PawnMapWhite pawnMapWhite;
    PawnMapBlack pawnMapBlack;
    CombinedPawnMap combinedPawnMap;
    Path whiteCagedCaptures = new Path();
    Path blackCagedCaptures = new Path();
    private static final BiPredicate<Coordinate, Coordinate> DARK_TEST =
            (c1, c2) -> c2.getX() != c1.getX() && (c1.getX() + c1.getY()) % 2 == 0;
    private static final BiPredicate<Coordinate, Coordinate> LIGHT_TEST =
            (c1, c2) -> c2.getX() != c1.getX() && (c1.getX() + c1.getY()) % 2 != 0;




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
//        this.pieceMap.deduce(board);

        int whiteRemovals = reductions(board, true);
        int blackRemovals = reductions(board, false);


        long start = System.nanoTime();
        // Updates the pawn map
        if (
                whiteRemovals + blackRemovals != 0
        ) {
//            System.out.println("K");
            this.pawnMapWhite.updateMaxCapturedPieces(whiteRemovals);
            this.pawnMapBlack.updateMaxCapturedPieces(blackRemovals);
            this.pawnMapWhite.deduce(board);
            this.pawnMapBlack.deduce(board);
            this.combinedPawnMap.deduce(board);
            this.pieceMap.deduce(board);
//            System.out.println(this.combinedPawnMap.getWhitePaths());
//            System.out.println(this.pawnMapWhite.maxPieces);
//            System.out.println(this.pawnMapWhite.getMaxCaptures(new Coordinate(3, 3)));

        }
//        System.out.println((System.nanoTime() - start)/1000);

        if (!(this.pawnMapWhite.getState() && this.pawnMapBlack.getState()
                && this.combinedPawnMap.getState() && this.pieceMap.getState())) {
            this.state = false;
        }

        return true ;
    }

    private int reductions(BoardInterface board, boolean white) {
        int capturesToRemove = 0;
        String colour = white ? "white" : "black";
//        int missingPawns = (8 - (white ? this.pawnNumber.getBlackPawns() : this.pawnNumber.getWhitePawns()));
//        int missingNonPawns = (16 - (white ? this.pawnNumber.getBlackPawns() : this.pawnNumber.getWhitePawns())) - missingPawns;
//        System.out.println("SLCT" + this.pieceMap.getStartLocations());
        Path ofWhichCaged = Path.of(this.pieceMap.getCaged().entrySet().stream()
                .filter(entry -> entry.getKey().getY() == (white ? 7 : 0))
                .filter(Map.Entry::getValue) //Is Caged
                .filter(entry -> {
                    Map<Coordinate, Path> map = this.pieceMap.getStartLocations().get(entry.getKey());
                    if (map.isEmpty()){
                        return true;
                    }
                    if (map.size() == 1) {
//                        System.out.println(entry.getKey());
                        return this.pieceMap.getStartLocations()
                                .get(new Coordinate(Math.abs(7 - entry.getKey().getX()), entry.getKey().getY()))
                                .containsKey(map.keySet().stream().findAny().orElse(new Coordinate(-1, -1)))
                                && entry.getKey().getX() == 0;
                    }
                    return false;
                }) //Is missing
                .map(Map.Entry::getKey)
                .toList());
        int ofWhichBishop = (int) ofWhichCaged.stream()
                .filter(coordinate -> coordinate.getX() == 2 || coordinate.getX() == 5) // Is a bishop
                .count();

        // Rooks are the only piece capable of being both caged and captured on the pawn rank
        Path ofWhichRook = Path.of(ofWhichCaged.stream()
                .filter(coordinate -> coordinate.getX() == 0 || coordinate.getX() == 7) // Is a rook
                .toList());

        int ofWhichQueen = ofWhichCaged.size() - ofWhichBishop - ofWhichRook.size();


        Map<Integer, Path> reachable = Map.of(0, new Path(), 7, new Path());
        // Check each rook can path to any opposing pawn
        if (!ofWhichRook.isEmpty()) {
            ofWhichRook.stream().forEach(coordinate2 -> {

                board.getBoardFacts().getCoordinates(colour, "pawn")
                        .stream().filter(coordinate -> white ? (coordinate.getY() > 5)  : (coordinate.getY() < 2))
                        .forEach(coordinate -> {
                            if (!this.pieceMap.findPath(board, "rook", white ? "r" : "R", coordinate2, coordinate).isEmpty()) {
                                reachable.get(coordinate2.getX()).add(coordinate);
                            }
                        });
            });
        }

//        int maxCaptures = (white ? this.pawnMapWhite.capturedPieces() : this.pawnMapBlack.capturedPieces())
//                - this.combinedPawnMap.captures(colour);

        int innaccessibleTakenRooks = 0;

//        System.out.println("Innacces" + reachable);
//        System.out.println("rook" + ofWhichRook);

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
                if (reachable.get(coordinate.getX() == 0 ? 7 : 0).size() == 1
                        && !reachable.get(coordinate.getX() == 0 ? 7 : 0).contains(coordinate)) {
                    increase = true;
                    innaccessibleTakenRooks += 1;
                }
            }
            if (increase) {
//                System.out.println("increases");
                if (white) {
//                    System.out.println("increases");

                    this.whiteCagedCaptures.add(coordinate);
                } else {
                    this.blackCagedCaptures.add(coordinate);
                }
            }
        }
//        System.out.println("Innacces" + innaccessibleTakenRooks);
//        System.out.println("Innacces" + reachable);



        // Account for bishops being taken on the correct colour
        // this is only done in situations where all captures are made by certain pawn paths

        List<BiPredicate<Coordinate, Coordinate>> predicates =
                new LinkedList<>(this.pieceMap.getStartLocations().entrySet().stream()
                        .filter(entry -> entry.getKey().getY() == (white ? 7 : 0)) //Correct colour
                        .filter(entry -> entry.getKey().getX() == 2 || entry.getKey().getX() == 5) //Is a Bishop
                        .filter(entry -> !(this.pieceMap.getCaged().get(entry.getKey()))) //Is not Caged
                        .filter(entry -> entry.getValue().isEmpty()) //Is missing
                        .map(entry -> ((entry.getKey().getX() + entry.getKey().getY()) % 2 == 0)  ? DARK_TEST : LIGHT_TEST)
                        .toList());
//        System.out.println(predicates.size());
        if (!predicates.isEmpty()) {
            Map<Coordinate, List<Path>> paths = white ? this.combinedPawnMap.getWhitePaths() : this.combinedPawnMap.getBlackPaths();
            int otherValue = paths.entrySet().stream()
                    .filter(entry -> this.combinedPawnMap.getSinglePath(colour, entry.getKey()) != null) //Every path that's a single path
                    .map(entry -> entry.getValue()
                            .stream().map(CombinedPawnMap.PATH_DEVIATION)
                            .reduce((i, j) -> i < j ? i : j)
                            .orElse(0))
                    .reduce(0, Integer::sum);
//        System.out.println(otherValue);

            if (otherValue == this.combinedPawnMap.captures(colour) && otherValue != 0) {


                for (List<Path> pathList : paths.values()) {
//                if (pathList.isEmpty()) {
//                    continue;
//                }
                    Path path = pathList.get(0);
                    Iterator<BiPredicate<Coordinate, Coordinate>> predicateIterator = predicates.iterator();
                    while (predicateIterator.hasNext()) {
                        BiPredicate<Coordinate, Coordinate> predicate = predicateIterator.next();
                        for (int j = 0; j < path.size() - 1; j++) {
                            if (predicate.test(path.get(j), path.get(j + 1))) {
//                            System.out.println(path);
                                predicateIterator.remove();
                                break;
                            }
                        }
                    }
                }
                if (!predicates.isEmpty()) {
                    capturesToRemove += predicates.size();
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

}
