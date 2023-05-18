import StandardChess.Board;
import StandardChess.BoardBuilder;
import StandardChess.ChessBoard;
import StandardChess.Coordinate;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class BoardBuilderTest {

    ChessBoard board = BoardBuilder.buildBoard("rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1");

    @Test
    public void testFENInputRooks() {
        Assertions.assertEquals(this.board.at(new Coordinate(0, 0)).getType(), "rook");
        Assertions.assertEquals(this.board.at(new Coordinate(7, 0)).getType(), "rook");
    }

    @Test
    public void testFENInputKings() {
        Assertions.assertEquals(this.board.at(new Coordinate(4, 0)).getType(), "king");
        Assertions.assertEquals(this.board.at(new Coordinate(4, 7)).getType(), "king");
    }

    @Test
    public void testFENInputComplex() {
        ChessBoard boardTwo = BoardBuilder.buildBoard("p2p2p1/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR");
        Assertions.assertEquals(boardTwo.at(new Coordinate(3, 7)).getType(), "pawn");
    }

    @Test
    public void testFENInputColour() {
        Assertions.assertEquals(this.board.at(new Coordinate(0, 0)).getColour(), "white");
        Assertions.assertEquals(this.board.at(new Coordinate(0, 7)).getColour(), "black");

    }

    @Test
    public void testFENBadInput() {
        badInput("");
        badInput("8/8/8/8/8/8/8");
        badInput("g7/8/8/8/8/8/8/8");
    }

    public void badInput(String input) {
        Assertions.assertThrows(IllegalArgumentException.class, () -> BoardBuilder.buildBoard(input));
    }

    @Test
    public void testFENCastling() {

        uniformCastlingTest(true, this.board);

        ChessBoard boardTwo = BoardBuilder.buildBoard("rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQq - 0 1");
        Assertions.assertTrue(boardTwo.canCastleWhiteKing());
        Assertions.assertTrue(boardTwo.canCastleWhiteQueen());
        Assertions.assertFalse(boardTwo.canCastleBlackKing());
        Assertions.assertTrue(boardTwo.canCastleBlackQueen());

        boardTwo = BoardBuilder.buildBoard("rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w - - 0 1");
        uniformCastlingTest(false, boardTwo);

        boardTwo = BoardBuilder.buildBoard("rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w");
        uniformCastlingTest(true, boardTwo);

        boardTwo = BoardBuilder.buildBoard("1nbqkbn1/pppppppp/8/8/8/8/PPPPPPPP/RNBQ1BNR w");
        uniformCastlingTest(false, boardTwo);

    }

    public void uniformCastlingTest(boolean flag, ChessBoard board) {
        Assertions.assertEquals(flag, board.canCastleWhiteKing());
        Assertions.assertEquals(flag, board.canCastleWhiteQueen());
        Assertions.assertEquals(flag, board.canCastleBlackKing());
        Assertions.assertEquals(flag, board.canCastleBlackQueen());
    }

}