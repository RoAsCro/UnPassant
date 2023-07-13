package Heuristics.Deductions;

import Heuristics.BoardInterface;
import Heuristics.Observation;
import Heuristics.Observations.PieceNumber;
import Heuristics.Pathfinder;
import StandardChess.Coordinate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

public class PromotionMap extends AbstractDeduction {

    PieceMap pieceMap;
    CombinedPawnMap pawnMap;
    PawnMapWhite pawnMapWhite;

    PieceNumber pieceNumber;


    @Override
    public List<Observation> getObservations() {
        return null;
    }

    @Override
    public boolean deduce(BoardInterface board) {
        this.pieceMap.deduce(board);

        this.pawnMap.captures("white");
        List<Coordinate> origins = this.pawnMapWhite.getOriginFree().entrySet()
                .stream().filter(Map.Entry::getValue)
                .map(Map.Entry::getKey)
                .toList();
        // May have duplicates
        List<Coordinate> targets = this.pieceMap.getPromotedPieceMap().entrySet()
                .stream().filter(entry -> !entry.getValue().isEmpty())
                .map(Map.Entry::getKey)
                .toList();

        int maxCaptures = this.pawnMapWhite.capturedPieces() - this.pawnMap.captures("white");

//        origins.forEach(origin -> targets.forEach(target -> Pathfinder.));
//        this.pawnMapWhite.getPawnOrigins()
        //        this.pieceMap.getPromotedPieceMap().
//        this.pawnMap.getMaxCaptures()

        return false;
    }

    private class PawnMapWhiteTwo extends PawnMap {

        public PawnMapWhiteTwo() {
            super("white");
        }

        @Override
        public boolean deduce(BoardInterface board) {
            return super.deduce(board, "white");
        }

        @Override
        public void update() {
            super.update("white");
        }

        @Override
        public int capturedPieces() {
            return super.capturedPieces("white");
        }

        @Override
        public Map<Coordinate, Integer> getCaptureSet() {
            return super.getCaptureSet("white");
        }
    }
}
