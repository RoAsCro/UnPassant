package Heuristics.Deductions;

import Heuristics.BoardInterface;

public class PawnMapBlack extends PawnMap{
    @Override
    public boolean deduce(BoardInterface board) {
        return super.deduce(board, "black");
    }
}
