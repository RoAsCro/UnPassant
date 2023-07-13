package Heuristics.Deductions;

import Heuristics.BoardInterface;
import Heuristics.Observation;
import Heuristics.Observations.PawnNumber;
import Heuristics.Observations.PieceNumber;
import Heuristics.Path;
import Heuristics.Pathfinder;
import StandardChess.Coordinate;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CaptureLocations extends AbstractDeduction {

    PawnNumber pawnNumber;
    PieceNumber pieceNumber;
    PieceMap pieceMap;
    PawnMapWhite pawnMapWhite;
    PawnMapBlack pawnMapBlack;
    CombinedPawnMap combinedPawnMap;


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
        this.pieceMap.deduce(board);
        System.out.println("STARTING" + this.pawnMapWhite.getPawnOrigins());

        int missingPawns = (8 - this.pawnNumber.getBlackPawns());
        int missingNonPawns = (16 - this.pieceNumber.getBlackPieces()) - missingPawns;
        Path ofWhichCaged = Path.of(this.pieceMap.getCaged().entrySet().stream()
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


        Map<Integer, Path> reachable = Map.of(0, new Path(), 7, new Path());
        if (!ofWhichRook.isEmpty()) {
            ofWhichRook.stream().forEach(coordinate2 -> {

                board.getBoardFacts().getCoordinates("white", "pawn")
                    .stream().filter(coordinate -> coordinate.getY() > 5)
                    .forEach(coordinate -> {
                            if (!this.pieceMap.findPath(board, "rook", "r", coordinate2, coordinate, 5).isEmpty()) {
                                reachable.get(coordinate2.getX()).add(coordinate);
                            }
                    });
            });
//        ofWhichRook.forEach(coordinate -> );
        }
        int maxCaptures = this.pawnMapWhite.capturedPieces() - this.combinedPawnMap.captures("white");

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

        if (ofWhichQueen + ofWhichBishop + innaccessibleTakenRooks != 0) {
            this.pawnMapWhite.updateMaxCapturedPieces(ofWhichQueen + ofWhichBishop + innaccessibleTakenRooks);
            this.pieceMap.deduce(board);
        }

        //TODO Update pawn maps, making it so that only pawns on the enemy pawn rank account for caged rooks




        return false;
    }
}
