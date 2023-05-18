import StandardChess.BoardBuilder;
import StandardChess.ChessBoard;
import StandardChess.Coordinate;
import StandardChess.Piece;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class KingStrategyTest {

    ChessBoard board = BoardBuilder.buildBoard("8/8/4k3/8/8/8/8/8");
    Coordinate origin = new Coordinate(4, 5);
    Piece piece = board.at(origin);


    @Test
    public void testTryMoveNormal() {
        // Diagonal
        Assertions.assertTrue(piece.tryMove(origin, new Coordinate(5, 6), board));
        Assertions.assertTrue(piece.tryMove(origin, new Coordinate(5, 4), board));
        Assertions.assertTrue(piece.tryMove(origin, new Coordinate(3, 6), board));
        Assertions.assertTrue(piece.tryMove(origin, new Coordinate(3, 4), board));
        // Horizontal
        Assertions.assertTrue(piece.tryMove(origin, new Coordinate(3, 5), board));
        Assertions.assertTrue(piece.tryMove(origin, new Coordinate(5, 5), board));
        // Vertical
        Assertions.assertTrue(piece.tryMove(origin, new Coordinate(4, 6), board));
        Assertions.assertTrue(piece.tryMove(origin, new Coordinate(4, 4), board));
    }

    @Test
    public void testTryMoveCollision() {
        ChessBoard boardTwo = BoardBuilder.buildBoard("8/8/4kn2/4N3/8/8/8/8");

        // Allied piece on the target
        Assertions.assertFalse(piece.tryMove(origin, new Coordinate(5, 5), boardTwo));

        // Opposing piece on target
        Assertions.assertTrue(piece.tryMove(origin, new Coordinate(4, 4), boardTwo));
    }

    @Test
    public void testTryMoveInvalidDirection() {
        Assertions.assertFalse(piece.tryMove(origin, new Coordinate(2, 6), board));
        Assertions.assertFalse(piece.tryMove(origin, new Coordinate(0, 0), board));
    }

    @Test
    public void testTryMoveInvalidDistance() {
        Assertions.assertFalse(piece.tryMove(origin, new Coordinate(4, 0), board));
        Assertions.assertFalse(piece.tryMove(origin, new Coordinate(6, 7), board));
    }

}
