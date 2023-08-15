package Heuristics.Detector.Data;

import Heuristics.Path;
import StandardChess.Coordinate;

import java.util.List;
import java.util.Map;

public interface PawnData {

    Map<Coordinate, List<Path>> getPawnPaths(boolean white); //Important
    int minimumPawnCaptures(boolean white); //Important
    Map<Coordinate, Boolean> getOriginFree(boolean white);
}
