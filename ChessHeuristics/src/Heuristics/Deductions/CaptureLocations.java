package Heuristics.Deductions;

import Heuristics.BoardInterface;
import Heuristics.Observation;
import Heuristics.Observations.PawnNumber;
import Heuristics.Observations.PieceNumber;
import Heuristics.Path;
import Heuristics.Pathfinder;
import StandardChess.BoardBuilder;
import StandardChess.Coordinate;

import java.util.*;
import java.util.function.BiFunction;
import java.util.function.BiPredicate;

public class CaptureLocations extends AbstractDeduction {

    PawnNumber pawnNumber;
    PieceNumber pieceNumber;
    PieceMap pieceMap;
    PawnMapWhite pawnMapWhite;
    PawnMapBlack pawnMapBlack;
    CombinedPawnMap combinedPawnMap;
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

    @Override
    public boolean deduce(BoardInterface board) {
        int capturesToRemove = 0;
        this.pieceMap.deduce(board);
        int whiteRemovals = reductions(board, true);
        int blackRemovals = reductions(board, false);
//        System.out.println("STARTING" + this.pawnMapWhite.getPawnOrigins());
//
//        int missingPawns = (8 - this.pawnNumber.getBlackPawns());
//        int missingNonPawns = (16 - this.pieceNumber.getBlackPieces()) - missingPawns;
//        Path ofWhichCaged = Path.of(this.pieceMap.getCaged().entrySet().stream()
//                .filter(Map.Entry::getValue) //Is Caged
//                .filter(entry -> this.pieceMap.getStartLocations().get(entry.getKey()).isEmpty()) //Is missing
//                .map(Map.Entry::getKey)
//                .toList());
//        int ofWhichBishop = (int) ofWhichCaged.stream()
//                .filter(coordinate -> coordinate.getX() == 2 || coordinate.getX() == 5) // Is a bishop
//                .count();
//
//        // Rooks are the only piece capable of being both caged and captured on the pawn rank
//        Path ofWhichRook = Path.of(ofWhichCaged.stream()
//                .filter(coordinate -> coordinate.getX() == 0 || coordinate.getX() == 7) // Is a rook
//                .toList());
//
//        int ofWhichQueen = ofWhichCaged.size() - ofWhichBishop - ofWhichRook.size();
//
//
//        Map<Integer, Path> reachable = Map.of(0, new Path(), 7, new Path());
//        if (!ofWhichRook.isEmpty()) {
//            ofWhichRook.stream().forEach(coordinate2 -> {
//
//                board.getBoardFacts().getCoordinates("white", "pawn")
//                    .stream().filter(coordinate -> coordinate.getY() > 5)
//                    .forEach(coordinate -> {
//                            if (!this.pieceMap.findPath(board, "rook", "r", coordinate2, coordinate, 5).isEmpty()) {
//                                reachable.get(coordinate2.getX()).add(coordinate);
//                            }
//                    });
//            });
//        }
//        int maxCaptures = this.pawnMapWhite.capturedPieces() - this.combinedPawnMap.captures("white");
//
//        int innaccessibleTakenRooks = 0;
//
//        for (int i = 0 ; i < ofWhichRook.size() ; i++){
//            Coordinate coordinate = ofWhichRook.get(i);
//            int size = reachable.get(coordinate.getX()).size();
//            if (size == 0) {
//                innaccessibleTakenRooks += 1;
//            } else if (!(ofWhichRook.size() == 1) && !(size > 1)) {
//                if (reachable.get(coordinate.getX() == 0 ? 7 : 0).size() == 1
//                        && !reachable.get(coordinate.getX() == 0 ? 7 : 0).contains(coordinate)) {
//                    innaccessibleTakenRooks += 1;
//                }
//            }
//        }
//
//
//
//
//        // Account for bishops being taken on the correct colour
//        // this is only done in situations where all captures are made by certain pawn paths
//        List<BiPredicate<Coordinate, Coordinate>> predicates = new LinkedList<>(this.pieceMap.getStartLocations().entrySet().stream()
//                .filter(entry -> entry.getKey().getX() == 2 || entry.getKey().getX() == 5) //Is a Bishop
//                .filter(entry -> !(this.pieceMap.getCaged().get(entry.getKey()))) //Is not Caged
//                .filter(entry -> entry.getValue().isEmpty()) //Is missing
//                .map(entry -> entry.getKey().getX() == 2 ? LIGHT_TEST : DARK_TEST)
//                .toList());
//        System.out.println(predicates.size());
//
//        int otherValue = this.combinedPawnMap.getWhitePaths().entrySet().stream()
//                .filter(entry -> this.combinedPawnMap.getSinglePath("white",  entry.getKey()) != null) //Every path that's a single path
//                .map(entry -> entry.getValue()
//                        .stream().map(CombinedPawnMap.PATH_DEVIATION)
//                        .reduce((i, j) -> i < j ? i : j)
//                        .orElse(0))
//                .reduce(0, Integer::sum);
//        System.out.println(otherValue);
//
//        if (otherValue == this.combinedPawnMap.captures("white")) {
//
//
//            for (List<Path> paths : this.combinedPawnMap.getWhitePaths().values()) {
//                Path path = paths.get(0);
//                Iterator<BiPredicate<Coordinate, Coordinate>> predicateIterator = predicates.iterator();
//                while (predicateIterator.hasNext()) {
//                    BiPredicate<Coordinate, Coordinate> predicate = predicateIterator.next();
//                    for (int j = 0; j < path.size() - 1; j++) {
//                        if (predicate.test(path.get(j), path.get(j + 1))) {
//                            System.out.println(path);
//                            predicateIterator.remove();
//                            break;
//                        }
//                    }
//                }
//            }
//            if (!predicates.isEmpty()) {
//                capturesToRemove += predicates.size();
//            }
//        }


        // Updates the pawn map
        if (whiteRemovals + blackRemovals != 0) {
            System.out.println("Rerunning...");
            this.pawnMapWhite.updateMaxCapturedPieces(whiteRemovals);
            this.pawnMapBlack.updateMaxCapturedPieces(blackRemovals);
            this.pieceMap.deduce(board);
        }
        System.out.println("WR" + whiteRemovals);
        System.out.println("BR" + blackRemovals);



        return true ;
    }

    private int reductions(BoardInterface board, boolean white) {
        int capturesToRemove = 0;
        String colour = white ? "white" : "black";
        int missingPawns = (8 - (white ? this.pawnNumber.getBlackPawns() : this.pawnNumber.getWhitePawns()));
        int missingNonPawns = (16 - (white ? this.pawnNumber.getBlackPawns() : this.pawnNumber.getWhitePawns())) - missingPawns;
        Path ofWhichCaged = Path.of(this.pieceMap.getCaged().entrySet().stream()
                .filter(entry -> entry.getKey().getY() == (white ? 7 : 0))
                .filter(Map.Entry::getValue) //Is Caged
                .filter(entry -> this.pieceMap.getStartLocations().get(entry.getKey()).isEmpty()) //Is missing
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

        System.out.println(colour);

        System.out.println("Ofwhick" + ofWhichBishop);


        Map<Integer, Path> reachable = Map.of(0, new Path(), 7, new Path());
        if (!ofWhichRook.isEmpty()) {
            ofWhichRook.stream().forEach(coordinate2 -> {

                board.getBoardFacts().getCoordinates(colour, "pawn")
                        .stream().filter(coordinate -> white ? (coordinate.getY() > 5)  : (coordinate.getY() < 2))
                        .forEach(coordinate -> {
                            if (!this.pieceMap.findPath(board, "rook", white ? "r" : "R", coordinate2, coordinate, 5).isEmpty()) {
                                reachable.get(coordinate2.getX()).add(coordinate);
                            }
                        });
            });
        }

//        int maxCaptures = (white ? this.pawnMapWhite.capturedPieces() : this.pawnMapBlack.capturedPieces())
//                - this.combinedPawnMap.captures(colour);

        int innaccessibleTakenRooks = 0;

        for (int i = 0 ; i < ofWhichRook.size() ; i++){
            Coordinate coordinate = ofWhichRook.get(i);
            int size = reachable.get(coordinate.getX()).size();
            if (size == 0) {
                innaccessibleTakenRooks += 1;
            } else if (!(ofWhichRook.size() == 1) && !(size > 1)) {
                if (reachable.get(coordinate.getX() == 0 ? 7 : 0).size() == 1
                        && !reachable.get(coordinate.getX() == 0 ? 7 : 0).contains(coordinate)) {
                    innaccessibleTakenRooks += 1;
                }
            }
        }


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
        System.out.println(predicates.size());

        Map<Coordinate, List<Path>> paths = white ? this.combinedPawnMap.getWhitePaths() : this.combinedPawnMap.getBlackPaths();

        int otherValue = paths.entrySet().stream()
                .filter(entry -> this.combinedPawnMap.getSinglePath(colour,  entry.getKey()) != null) //Every path that's a single path
                .map(entry -> entry.getValue()
                        .stream().map(CombinedPawnMap.PATH_DEVIATION)
                        .reduce((i, j) -> i < j ? i : j)
                        .orElse(0))
                .reduce(0, Integer::sum);
        System.out.println(otherValue);

        if (otherValue == this.combinedPawnMap.captures(colour)) {


            for (List<Path> pathList : paths.values()) {
                Path path = pathList.get(0);
                Iterator<BiPredicate<Coordinate, Coordinate>> predicateIterator = predicates.iterator();
                while (predicateIterator.hasNext()) {
                    BiPredicate<Coordinate, Coordinate> predicate = predicateIterator.next();
                    for (int j = 0; j < path.size() - 1; j++) {
                        if (predicate.test(path.get(j), path.get(j + 1))) {
                            System.out.println(path);
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

        return ofWhichQueen + ofWhichBishop + innaccessibleTakenRooks + capturesToRemove;
    }

}
