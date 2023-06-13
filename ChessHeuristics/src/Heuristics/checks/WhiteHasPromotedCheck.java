package Heuristics.checks;

import Heuristics.BoardInterface;

public class WhiteHasPromotedCheck extends HasPromotedCheck{
    @Override
    public Boolean check(BoardInterface boardInterface) {
        return super.check(boardInterface, true);
    }
}
