package Heuristics.Detector.Data;

import Heuristics.Path;
import StandardChess.Coordinate;

import java.util.Map;

public interface PieceData {
    Map<Coordinate, Boolean> getCaged(); //Important
    Map<Coordinate, Map<Coordinate, Path>> getStartLocations();

    boolean getKingMovement(boolean white);

    void setKingMovement(boolean white, boolean moved);

    boolean getRookMovement(boolean white, boolean queen);

    void setRookMovement(boolean white, boolean queen, boolean moved);
}
