import StandardChess.BoardBuilder;
import org.junit.Test;
import org.junit.jupiter.api.Assertions;

public class CheckTest {

    @Test
    public void checkTestTrueBlack() {
        BoardInterface board = new BoardInterface(BoardBuilder.buildBoard("8/8/3R3k/8/8/8/8/K7 b - - 0 1"));
        Assertions.assertTrue(new CheckAssertion().check(board));
    }

    @Test
    public void checkTestFalseBlack() {
        BoardInterface board = new BoardInterface(BoardBuilder.buildBoard("8/8/7k/8/8/8/8/K7 b - - 0 1"));
        Assertions.assertFalse(new CheckAssertion().check(board));
    }

    @Test
    public void checkTestFalseWhite() {
        BoardInterface board = new BoardInterface(BoardBuilder.buildBoard("8/8/3R3k/8/8/8/8/K7 w - - 0 1"));
        Assertions.assertFalse(new CheckAssertion().check(board));
    }
}
