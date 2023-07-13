package Heuristics.Deductions;

import Heuristics.BoardInterface;
import Heuristics.Observation;
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
        List<Coordinate> targets = this.pieceMap.getPromotedPieceMap().entrySet()
                .stream().filter(entry -> !entry.getValue().isEmpty())
                .map(Map.Entry::getKey)
                .toList();
//        origins.forEach(origin -> targets.forEach(target -> Pathfinder.));
//        this.pawnMapWhite.getPawnOrigins()
        //        this.pieceMap.getPromotedPieceMap().
//        this.pawnMap.getMaxCaptures()

        return false;
    }
}
