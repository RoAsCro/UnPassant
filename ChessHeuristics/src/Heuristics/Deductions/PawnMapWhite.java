package Heuristics.Deductions;

import Heuristics.BoardInterface;

public class PawnMapWhite extends PawnMap{
    @Override
    public boolean deduce(BoardInterface board) {
        return super.deduce(board, "white");
    }

    @Override
    public int capturedPieces() {
        return super.capturedPieces("white");
    }
}
