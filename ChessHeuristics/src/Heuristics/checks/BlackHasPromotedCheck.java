package Heuristics.checks;

import Heuristics.BoardInterface;
public class BlackHasPromotedCheck extends HasPromotedCheck{
    @Override
    public Boolean check(BoardInterface boardInterface) {
        return super.check(boardInterface, false);
    }
}
