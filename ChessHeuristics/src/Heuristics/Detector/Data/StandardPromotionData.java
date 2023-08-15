package Heuristics.Detector.Data;

import Heuristics.Path;
import StandardChess.Coordinate;

import java.util.Map;
import java.util.TreeMap;

public class StandardPromotionData implements PromotionData{
    // <Type of piece, <potentially promoted pieces, how many are may not be promoted>
    private final Map<String, Map<Path, Integer>> promotionNumbers = new TreeMap<>();
    private final Map<Coordinate, Path> promotedPieceMap = new TreeMap<>();


    @Override
    public Map<String, Map<Path, Integer>> getPromotionNumbers() {
        return this.promotionNumbers;
    }

    @Override
    public Map<Coordinate, Path> getPromotedPieceMap() {
        return this.promotedPieceMap;
    }
}
