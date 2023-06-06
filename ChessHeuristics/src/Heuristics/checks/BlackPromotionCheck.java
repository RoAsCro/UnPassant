package Heuristics.checks;

import Heuristics.BoardInterface;

public class BlackPromotionCheck extends PromotionCheck {
    @Override
    public Boolean check(BoardInterface boardInterface) {
        return super.check(boardInterface, false);
    }
}
