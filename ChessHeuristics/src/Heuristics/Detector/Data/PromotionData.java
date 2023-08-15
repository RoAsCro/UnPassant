package Heuristics.Detector.Data;

import Heuristics.Path;
import StandardChess.Coordinate;

import java.util.Map;

public interface PromotionData {
    Map<String, Map<Path, Integer>> getPromotionNumbers();
    Map<Coordinate, Path> getPromotedPieceMap();
}
