import Heuristics.BoardInterface;
import Heuristics.checks.TooManyPiecesCheck;
import StandardChess.BoardBuilder;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class ToomManyPiecesCheckTest {

    @Test
    public void rightNumberOfPieces() {
        BoardInterface board = new BoardInterface(BoardBuilder.buildBoard());
        Assertions.assertTrue(new TooManyPiecesCheck().check(board));
    }

    @Test
    public void tooManyPieces() {
        BoardInterface board = new BoardInterface(BoardBuilder.buildBoard("rnbqkbnr/pppppppp/8/8/5B2/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1"));
        Assertions.assertFalse(new TooManyPiecesCheck().check(board));

    }

}
