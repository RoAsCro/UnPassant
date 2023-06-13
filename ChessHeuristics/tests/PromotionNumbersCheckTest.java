import Heuristics.BoardInterface;
import Heuristics.checks.BlackPromotionNumbersCheck;
import Heuristics.checks.WhitePromotionNumbersCheck;
import StandardChess.BoardBuilder;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class PromotionNumbersCheckTest {
    @Test
    public void noPromotion() {
        BoardInterface board = new BoardInterface(BoardBuilder.buildBoard());
        Assertions.assertTrue(new WhitePromotionNumbersCheck().check(board));
        Assertions.assertTrue(new BlackPromotionNumbersCheck().check(board));

    }

    @Test
    public void moreThanEightNonPawns() {
        BoardInterface board = new BoardInterface(BoardBuilder.buildBoard("rnbqkbnr/pppppppp/8/8/8/1RR5/1PPPPPPP/1NBQKBNR w Kkq - 0 1"));
        Assertions.assertTrue(new WhitePromotionNumbersCheck().check(board));
        Assertions.assertTrue(new BlackPromotionNumbersCheck().check(board));
    }

    @Test
    public void moreThanCorrectNumberOfPieces() {
        BoardInterface board = new BoardInterface(BoardBuilder.buildBoard("rnbqkbnr/ppp1pppp/3n4/8/8/1N6/1PPPPPPP/1NBQKBNR w Kkq - 0 1"));
        Assertions.assertTrue(new WhitePromotionNumbersCheck().check(board));
        Assertions.assertTrue(new BlackPromotionNumbersCheck().check(board));

    }

}
