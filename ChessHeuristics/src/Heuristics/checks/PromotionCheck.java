package Heuristics.checks;

import Heuristics.BoardInterface;
import Heuristics.Check;

public abstract class PromotionCheck implements Check {

    public Boolean check(BoardInterface boardInterface, boolean white) {
        int pieceNumber = white ? boardInterface.getWhitePieceNumber() : boardInterface.getBlackPieceNumber();
        int pawnNumber = white ? boardInterface.getWhitePawnNumber() : boardInterface.getBlackPawnNumber();
        if (pawnNumber == BoardInterface.MAX_PAWN_NUMBER) {
            return false;
        }
        return Boolean.getBoolean("null");
    }
}
