import StandardChess.BoardBuilder;
import StandardChess.ChessBoard;
import StandardChess.Coordinate;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class BoardReaderTest {

    ChessBoard board = BoardBuilder.buildBoard();

    private void testFEN(String fen) {
        ChessBoard boardTwo = BoardBuilder.buildBoard(fen);
        Assertions.assertEquals(fen,
                boardTwo.getReader().toFEN());
    }
    @Test
    public void testToFENStandard() {
        Assertions.assertEquals("rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq -",
                this.board.getReader().toFEN());
    }

    @Test
    public void testToFENSpaces() {
        testFEN("r1bq2nr/pppppppp/3p4/8/8/8/PPPPPPPP/RNBQKBNR w KQkq -");
    }

    @Test
    public void testToFENCastling() {
        testFEN("r1bq2nr/pppppppp/3p4/8/8/8/PPPPPPPP/RNBQKBNR w Kk -");
    }

    @Test
    public void testToFENTurn() {
        testFEN("r1bq2nr/pppppppp/3p4/8/8/8/PPPPPPPP/RNBQKBNR b KQkq -");
    }

}
