package Heuristics.Detector.Data;

import Heuristics.Path;
import StandardChess.Coordinate;

import java.util.Map;
import java.util.TreeMap;

public class StandardPromotionData implements PromotionData{
    // <Type of piece, <potentially promoted pieces, how many are may not be promoted>
    private final Map<String, Map<Path, Integer>> promotionNumbers = new TreeMap<>();
    private final Map<Coordinate, Path> promotedPieceMap = new TreeMap<>();

    public StandardPromotionData() {
        for (int y  = 0 ; y < 8 ; y = y + 7) {
            for (int x = 0; x < 8; x++) {
                this.promotedPieceMap.put(new Coordinate(x, y), new Path());
            }
        }
    }
    @Override
    public Map<String, Map<Path, Integer>> getPromotionNumbers() {
        return this.promotionNumbers;
    }

    @Override
    public Map<Coordinate, Path> getPromotedPieceMap() {
        return this.promotedPieceMap;
    }
}
