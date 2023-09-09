package ReverseChess;

import ReverseChess.StandardChess.BoardBuilder;
import ReverseChess.StandardChess.ChessBoard;
import ReverseChess.StandardChess.Coordinate;
import ReverseChess.StandardChess.Piece;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class KnightStrategyTest {
    ChessBoard board = BoardBuilder.buildBoard("8/8/4n3/8/8/8/8/8");
    Coordinate origin = new Coordinate(4, 5);
    Piece piece = board.at(origin);


    @Test
    public void testTryMoveNormal() {
        Assertions.assertTrue(piece.tryMove(origin, new Coordinate(6, 4), board));
        Assertions.assertTrue(piece.tryMove(origin, new Coordinate(6, 6), board));
        Assertions.assertTrue(piece.tryMove(origin, new Coordinate(2, 4), board));
        Assertions.assertTrue(piece.tryMove(origin, new Coordinate(2, 6), board));
        Assertions.assertTrue(piece.tryMove(origin, new Coordinate(5, 7), board));
        Assertions.assertTrue(piece.tryMove(origin, new Coordinate(5, 3), board));
        Assertions.assertTrue(piece.tryMove(origin, new Coordinate(3, 7), board));
        Assertions.assertTrue(piece.tryMove(origin, new Coordinate(3, 3), board));
    }

    @Test
    public void testTryMoveCollision() {
        ChessBoard boardTwo = BoardBuilder.buildBoard("8/2n5/4n3/2N5/8/8/8/8");
        // Allied piece on the target
        Assertions.assertFalse(piece.tryMove(origin, new Coordinate(2, 6), boardTwo));
        // Opposing piece on target
        Assertions.assertTrue(piece.tryMove(origin, new Coordinate(2, 4), boardTwo));
    }

    @Test
    public void testTryMoveInvalidDirection() {
        Assertions.assertFalse(piece.tryMove(origin, new Coordinate(5, 4), board));
        Assertions.assertFalse(piece.tryMove(origin, new Coordinate(4, 7), board));
        Assertions.assertFalse(piece.tryMove(origin, new Coordinate(0, 0), board));

    }

    @Test
    public void testTryUnMoveCollision() {
        ChessBoard boardTwo = BoardBuilder.buildBoard("8/2n5/4n3/2N5/8/8/8/8");
        // Opposing piece on target
        Assertions.assertFalse(piece.tryUnMove(origin, new Coordinate(2, 4), boardTwo));
    }

}
