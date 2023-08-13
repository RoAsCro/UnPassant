package Heuristics;

import Heuristics.Observations.PieceNumber;
import StandardChess.Coordinate;

import java.util.List;
import java.util.Map;

public interface StateDetector {
    public PieceNumber getPieceNumber();

    int getPawnNumbers(boolean white);

    int pawnTakeablePieces(boolean white);

    void reducePawnTakeablePieces(boolean white, int subtrahend);

    Map<Coordinate, Integer> getCaptureSet(boolean white);

    Map<Coordinate, Path> getPawnOrigins(boolean white);

    Map<Coordinate, Boolean> getOriginFree(boolean white);

    int getCapturedPieces(boolean white);

    void setCapturedPieces(boolean white, int capturedPieces);

    int getMaxCaptures(boolean white, Coordinate coordinate);

    //CombinePawnMap stuff
    Map<Coordinate, List<Path>> getPawnPaths(boolean white);

    Map<Coordinate, Path> getSinglePawnPaths(boolean white);

    int minimumPawnCaptures(boolean white);

    void update();

    void reTest(BoardInterface boardInterface);

    boolean getKingMovement(boolean white);

    void setKingMovement(boolean white, boolean moved);

    boolean getRookMovement(boolean white, boolean queen);

    void setRookMovement(boolean white, boolean queen, boolean moved);

    Map<Coordinate, Map<Coordinate, Path>> getStartLocations();

    Map<Coordinate, Boolean> getCaged();

    Map<String, Map<Path, Integer>> getPromotionNumbers();

    Path getCagedCaptures(boolean white);

    void setPawnsCapturedByPawns(boolean white, int pawnsCapturedByPawns);

    int getPawnsCapturedByPawns(boolean white);

    Path getPromotedPawns(boolean white);

    Map<Coordinate, Path> getPromotedPieceMap();
}
