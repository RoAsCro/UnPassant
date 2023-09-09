package ReverseChess;

import ReverseChess.StandardChess.BoardBuilder;
import ReverseChess.StandardChess.ChessBoard;
import ReverseChess.StandardChess.Coordinate;
import ReverseChess.StandardChess.Piece;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class BishopStrategyTest {
    ChessBoard board = BoardBuilder.buildBoard("8/3b4/4b3/3B4/8/8/8/8");
    Coordinate origin = new Coordinate(4, 5);
    Piece piece = board.at(origin);


    @Test
    public void testTryMoveNormal() {
        Assertions.assertTrue(piece.tryMove(origin, new Coordinate(5, 6), board));
        Assertions.assertTrue(piece.tryMove(origin, new Coordinate(6, 7), board));

        Assertions.assertTrue(piece.tryMove(origin, new Coordinate(5, 4), board));
        Assertions.assertTrue(piece.tryMove(origin, new Coordinate(6, 3), board));
        Assertions.assertTrue(piece.tryMove(origin, new Coordinate(7, 2), board));
    }

    @Test
    public void testTryMoveCollision() {
        // Opposing piece in the way
        Assertions.assertFalse(piece.tryMove(origin, new Coordinate(2, 3), board));
        // Allied piece on the target
        Assertions.assertFalse(piece.tryMove(origin, new Coordinate(3, 6), board));
        // Opposing piece on target
        Assertions.assertTrue(piece.tryMove(origin, new Coordinate(3, 4), board));
    }

    @Test
    public void testTryMoveInvalidDirection() {
        Assertions.assertFalse(piece.tryMove(origin, new Coordinate(4, 3), board));
        Assertions.assertFalse(piece.tryMove(origin, new Coordinate(0, 5), board));
        Assertions.assertFalse(piece.tryMove(origin, new Coordinate(6, 4), board));

    }

    @Test
    public void testTryUnMoveCollision() {
        Assertions.assertFalse(piece.tryUnMove(origin, new Coordinate(3, 4), board));
    }

}
