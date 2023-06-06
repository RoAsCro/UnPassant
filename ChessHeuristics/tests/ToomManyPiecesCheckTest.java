import StandardChess.BoardBuilder;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class ToomManyPiecesCheckTest {

    @Test
    public void rightNumberOfPieces() {
        BoardInterface board = new BoardInterface(BoardBuilder.buildBoard());
        Assertions.assertTrue(new TooManyPiecesCheck().check(board));
    }

}
