import StandardChess.Board;
import StandardChess.BoardBuilder;
import StandardChess.Coordinate;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class BoardBuilderTest {

    Board board = BoardBuilder.buildBoard("rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1");

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

        Assertions.assertTrue(this.board.canCastleWhiteKing());
        Assertions.assertTrue(this.board.canCastleWhiteQueen());
        Assertions.assertTrue(this.board.canCastleBlackKing());
        Assertions.assertTrue(this.board.canCastleBlackQueen());

        Board boardTwo = BoardBuilder.buildBoard("rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQq - 0 1");
        Assertions.assertFalse(boardTwo.canCastleWhiteKing());
        Assertions.assertTrue(boardTwo.canCastleWhiteQueen());
        Assertions.assertTrue(boardTwo.canCastleBlackKing());
        Assertions.assertTrue(boardTwo.canCastleBlackQueen());

    }



}