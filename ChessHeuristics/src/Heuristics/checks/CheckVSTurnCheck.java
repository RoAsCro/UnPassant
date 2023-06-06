package Heuristics.checks;

import Heuristics.BoardInterface;
import Heuristics.Check;

public class CheckVSTurnCheck implements Check {
    @Override
    public Boolean check(BoardInterface boardInterface) {

        return boardInterface.inCheck(boardInterface.getTurn());
    }
}
