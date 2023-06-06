import StandardChess.BoardBuilder;
import org.junit.Test;
import org.junit.jupiter.api.Assertions;

public class CheckTest {

    @Test
    public void checkTest() {
        BoardInterface board = new BoardInterface(BoardBuilder.buildBoard("8/8/3R3k/8/8/8/8/K7 b - - 0 1"));
        Assertions.assertFalse(new CheckAssertion().check(board));

    }
}
