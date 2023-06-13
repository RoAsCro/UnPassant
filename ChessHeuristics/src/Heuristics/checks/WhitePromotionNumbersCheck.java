package Heuristics.checks;

import Heuristics.BoardInterface;

public class WhitePromotionNumbersCheck extends PromotionNumbersCheck {
    @Override
    public Boolean check(BoardInterface boardInterface) {
        return super.check(boardInterface, true);
    }
}
