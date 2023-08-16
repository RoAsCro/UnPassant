package Heuristics.Detector;

import Heuristics.BoardInterface;
import Heuristics.Path;
import StandardChess.Coordinate;

import java.util.Map;

public interface DetectorInterface {
    Map<String, Map<Path, Integer>> getPromotions(boolean white);

    Map<Coordinate, Path> getPawnMap(boolean white);

    Path getCages(boolean white);

    boolean testState(BoardInterface boardInterface);

    boolean testState();

    public boolean canCastle(boolean white);

    boolean getState();
}
