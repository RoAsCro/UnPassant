import StandardChess.BoardBuilder;
import StandardChess.ChessBoard;
import StandardChess.Coordinate;
import StandardChess.Piece;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class RookStrategyTest {
    ChessBoard board = BoardBuilder.buildBoard("8/3b4/r3r3/4B3/8/8/8/8");
    Coordinate origin = new Coordinate(4, 5);
    Piece piece = board.at(origin);


    @Test
    public void testTryMoveNormal() {
        // Horizontal
        Assertions.assertTrue(piece.tryMove(origin, new Coordinate(7, 5), board));
        Assertions.assertTrue(piece.tryMove(origin, new Coordinate(1, 5), board));
        // Vertical
        Assertions.assertTrue(piece.tryMove(origin, new Coordinate(4, 7), board));
        Assertions.assertTrue(piece.tryMove(origin, new Coordinate(4, 6), board));
    }

    @Test
    public void testTryMoveCollision() {
        // Opposing piece in the way
        Assertions.assertFalse(piece.tryMove(origin, new Coordinate(4, 0), board));
        // Allied piece on the target
        Assertions.assertFalse(piece.tryMove(origin, new Coordinate(0, 5), board));
        // Opposing piece on target
        Assertions.assertTrue(piece.tryMove(origin, new Coordinate(4, 4), board));
    }

    @Test
    public void testTryMoveInvalidDirection() {
        Assertions.assertFalse(piece.tryMove(origin, new Coordinate(3, 4), board));
        Assertions.assertFalse(piece.tryMove(origin, new Coordinate(0, 0), board));
        Assertions.assertFalse(piece.tryMove(origin, new Coordinate(6, 4), board));

    }

    @Test
    public void testTryUnMoveCollision() {
        // Opposing piece on target
        Assertions.assertFalse(piece.tryUnMove(origin, new Coordinate(4, 4), board));
    }
}
