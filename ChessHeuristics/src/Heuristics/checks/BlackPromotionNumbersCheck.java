package Heuristics.checks;

import Heuristics.BoardInterface;

public class BlackPromotionNumbersCheck extends PromotionNumbersCheck {
    @Override
    public Boolean check(BoardInterface boardInterface) {
        return super.check(boardInterface, false);
    }
}
