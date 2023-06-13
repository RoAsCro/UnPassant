package Heuristics.Deductions;

import Heuristics.BoardInterface;

public class WhitePromotedPieces extends PromotedPieces{

    @Override
    public boolean deduce(BoardInterface board) {
        return super.deduce(board, "white");
    }
}
