package Heuristics.checks;

import Heuristics.BoardInterface;

public class WhitePromotionCheck extends PromotionCheck{
    @Override
    public Boolean check(BoardInterface boardInterface) {
        return super.check(boardInterface, true);
    }
}
