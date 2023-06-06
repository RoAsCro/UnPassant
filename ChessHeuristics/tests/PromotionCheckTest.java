import Heuristics.BoardInterface;
import Heuristics.checks.BlackPromotionCheck;
import Heuristics.checks.TooManyPawnsCheck;
import Heuristics.checks.WhitePromotionCheck;
import StandardChess.BoardBuilder;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class PromotionCheckTest {
    @Test
    public void noPromotion() {
        BoardInterface board = new BoardInterface(BoardBuilder.buildBoard());
        Assertions.assertFalse(new WhitePromotionCheck().check(board));
        Assertions.assertFalse(new BlackPromotionCheck().check(board));

    }

    @Test
    public void tooManyPawns() {
        BoardInterface board = new BoardInterface(BoardBuilder.buildBoard("rnbqkbnr/pppppppp/8/8/8/1PP5/1PPPPPPP/1NBQKBNR w Kkq - 0 1"));
        Assertions.assertFalse(new WhitePromotionCheck().check(board));

    }
}
