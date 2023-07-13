package Heuristics.Deductions;

import Heuristics.BoardInterface;
import Heuristics.Observation;
import StandardChess.Coordinate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
        Map<Integer, Integer> startLocations = new HashMap<>(Map.of(0, 0, 1, 0, 2, 0, 3, 0, 4, 0, 5, 0, 6, 0, 7, 0));
        this.pawnMapWhite.getPawnOrigins().forEach((key, value) -> value.forEach(
                coordinate -> startLocations.replace(coordinate.getX(), startLocations.get(coordinate.getX() + 1))));
//        this.pawnMapWhite.getPawnOrigins()
        //        this.pieceMap.getPromotedPieceMap().
//        this.pawnMap.getMaxCaptures()

        return false;
    }
}
