package Heuristics.checks;

import Heuristics.BoardInterface;
import Heuristics.Check;

public class TooManyPawnsCheck implements Check {

    @Override
    public Boolean check(BoardInterface boardInterface) {
        return boardInterface.getBlackPawnNumber() <= BoardInterface.MAX_PAWN_NUMBER &&
                boardInterface.getWhitePawnNumber() <= BoardInterface.MAX_PAWN_NUMBER;
    }
}
