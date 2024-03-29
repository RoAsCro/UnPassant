package ReverseChess;

import ReverseChess.StandardChess.BoardBuilder;
import ReverseChess.StandardChess.ChessBoard;
import ReverseChess.StandardChess.Coordinate;
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
        Assertions.assertTrue(boardTwo.canCastle("king", "white"));
        Assertions.assertTrue(boardTwo.canCastle("queen", "white"));
        Assertions.assertFalse(boardTwo.canCastle("king", "black"));
        Assertions.assertTrue(boardTwo.canCastle("queen", "black"));

    }

    public void uniformCastlingTest(boolean flag, ChessBoard board) {
        Assertions.assertEquals(flag, board.canCastle("king", "white"));
        Assertions.assertEquals(flag, board.canCastle("queen", "white"));
        Assertions.assertEquals(flag, board.canCastle("king", "black"));
        Assertions.assertEquals(flag, board.canCastle("queen", "black"));
    }

}