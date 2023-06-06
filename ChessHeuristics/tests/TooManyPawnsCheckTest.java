import StandardChess.BoardBuilder;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class TooManyPawnsCheckTest {
    @Test
    public void rightNumberOfPawns() {
        BoardInterface board = new BoardInterface(BoardBuilder.buildBoard());
        Assertions.assertTrue(new TooManyPawnsCheck().check(board));
    }

    @Test
    public void tooManyPawns() {
        BoardInterface board = new BoardInterface(BoardBuilder.buildBoard("rnbqkbnr/pppppppp/8/8/8/1PP5/1PPPPPPP/1NBQKBNR w Kkq - 0 1"));
        Assertions.assertFalse(new TooManyPawnsCheck().check(board));

    }
}
