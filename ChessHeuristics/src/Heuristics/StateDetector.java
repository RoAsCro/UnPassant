package Heuristics;

import Heuristics.Observations.PawnNumber;
import Heuristics.Observations.PieceNumber;
import StandardChess.Coordinate;

import java.util.List;
import java.util.Map;

public interface StateDetector {
    public PieceNumber getPieceNumber();

    PawnNumber getPawnNumber();

    int pawnTakeablePieces(boolean white);

    void setPawnTakeablePieces(boolean white, int subtrahend);

    Map<Coordinate, Integer> getCaptureSet(boolean white);

    Map<Coordinate, Path> getPawnOrigins(boolean white);

    Map<Coordinate, Boolean> getOriginFree(boolean white);

    int getCapturedPieces(boolean white);

    void setCapturedPieces(boolean white, int capturedPieces);

    int getMaxCaptures(boolean white, Coordinate coordinate);

    //CombinePawnMap stuff
    Map<Coordinate, List<Path>> getPawnPaths(boolean white);

    Map<Coordinate, Path> getSingPawnPaths(boolean white);

    void update();
}
