package Heuristics.Deductions;

import Heuristics.BoardInterface;

public class BlackPromotedPieces extends PromotedPieces {
    @Override
    public boolean deduce(BoardInterface board) {
        return super.deduce(board, "black");
    }
}
