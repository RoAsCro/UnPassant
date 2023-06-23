package Heuristics.Deductions;

import Heuristics.BoardInterface;
import StandardChess.Coordinate;

import java.util.Map;

public class PawnMapBlack extends PawnMap{
    @Override
    public boolean deduce(BoardInterface board) {
        return super.deduce(board, "black");
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
