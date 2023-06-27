package Heuristics.Deductions;

import Heuristics.BoardInterface;
import StandardChess.Coordinate;

import java.util.Map;

public class PawnMapBlack extends PawnMap{
    public PawnMapBlack() {
        super("black");
    }

    @Override
    public boolean deduce(BoardInterface board) {
        return super.deduce(board, "black");
    }

    @Override
    public void update() {
        super.update("black");
    }

    @Override
    public int capturedPieces() {
        return super.capturedPieces("black");
    }

    @Override
    public Map<Coordinate, Integer> getCaptureSet() {
        return super.getCaptureSet("black");
    }
}
