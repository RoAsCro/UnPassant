package Heuristics.Detector.Data;

import Heuristics.Path;
import StandardChess.Coordinate;

import java.util.Map;
import java.util.TreeMap;

public class StandardPieceData implements PieceData {
    private static final int WHITE = 0;
    private static final int BLACK = 1;
    private final Map<Coordinate, Boolean> caged = new TreeMap<>();
    private final Map<Coordinate, Map<Coordinate, Path>> piecePaths = new TreeMap<>();

    private final boolean[][] kingRookMovement = new boolean[][]{{false, false, false}, {false, false, false}};


    @Override
    public boolean getKingMovement(boolean white) {
        return kingRookMovement[white ? WHITE : BLACK][0];
    }
    @Override
    public void setKingMovement(boolean white, boolean moved) {
        this.kingRookMovement[white ? WHITE : BLACK][0] = moved;
    }
    @Override
    public boolean getRookMovement(boolean white, boolean queen) {
        return kingRookMovement[white ? WHITE : BLACK][queen ? 1 : 2];
    }
    @Override
    public void setRookMovement(boolean white, boolean queen, boolean moved) {
        this.kingRookMovement[white ? WHITE : BLACK][queen ? 1 : 2] = moved;
    }
    @Override
    public Map<Coordinate, Boolean> getCaged() {
        return this.caged;
    }

    @Override
    public Map<Coordinate, Map<Coordinate, Path>> getPiecePaths() {
        return this.piecePaths;
    }
}
