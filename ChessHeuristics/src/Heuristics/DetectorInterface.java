package Heuristics;

import java.util.Map;

public interface DetectorInterface {
    Map<String, Map<Path, Integer>> getPromotions(boolean white);

    boolean testState(BoardInterface boardInterface);

    boolean testState();

    public boolean canCastle(boolean white);
}
