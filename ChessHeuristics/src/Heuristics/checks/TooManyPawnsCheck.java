package Heuristics.checks;

import Heuristics.BoardInterface;
import Heuristics.Check;

public class TooManyPawnsCheck implements Check {

    @Override
    public Boolean check(BoardInterface boardInterface) {
        return boardInterface.getBoardFacts().getCoordinates("white", "pawn").size() <= BoardInterface.MAX_PAWN_NUMBER &&
                boardInterface.getBoardFacts().getCoordinates("black", "pawn").size() <= BoardInterface.MAX_PAWN_NUMBER;
    }
}
