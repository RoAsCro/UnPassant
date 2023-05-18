import StandardChess.BoardBuilder;
import StandardChess.ChessBoard;
import StandardChess.Coordinate;
import StandardChess.Piece;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class PawnStrategyTest {

    ChessBoard board = BoardBuilder.buildBoard("8/2p5/1P1P4/8/8/1p1p4/2P5/8");
    Coordinate whiteOrigin = new Coordinate(2, 1);
    Coordinate blackOrigin = new Coordinate(2,6);
    Piece whitePiece = board.at(whiteOrigin);
    Piece blackPiece = board.at(blackOrigin);


    @Test
    public void testTryMoveNormal() {
        // Vertical
        Assertions.assertTrue(whitePiece.tryMove(whiteOrigin, new Coordinate(2, 2), board));
        Assertions.assertTrue(blackPiece.tryMove(blackOrigin, new Coordinate(2, 5), board));
    }

//    @Test
//    public void testTryMoveCollision() {
//        ChessBoard boardTwo = BoardBuilder.buildBoard("8/8/4kn2/4N3/8/8/8/8");
//
//        // Allied piece on the target
//        Assertions.assertFalse(piece.tryMove(origin, new Coordinate(5, 5), boardTwo));
//
//        // Opposing piece on target
//        Assertions.assertTrue(piece.tryMove(origin, new Coordinate(4, 4), boardTwo));
//    }
//
    @Test
    public void testTryMoveInvalidDirectionHorizontal() {
        Assertions.assertFalse(whitePiece.tryMove(whiteOrigin, new Coordinate(1, 1), board));
        Assertions.assertFalse(whitePiece.tryMove(whiteOrigin, new Coordinate(3, 1), board));

        Assertions.assertFalse(blackPiece.tryMove(blackOrigin, new Coordinate(1, 6), board));
        Assertions.assertFalse(blackPiece.tryMove(blackOrigin, new Coordinate(3, 6), board));

    }

    @Test
    public void testTryMoveInvalidDirectionVertical() {
        Assertions.assertFalse(whitePiece.tryMove(whiteOrigin, new Coordinate(2, 0), board));

        Assertions.assertFalse(blackPiece.tryMove(blackOrigin, new Coordinate(2, 7), board));

    }
//
//    @Test
//    public void testTryMoveInvalidDistance() {
//        Assertions.assertFalse(piece.tryMove(origin, new Coordinate(4, 0), board));
//        Assertions.assertFalse(piece.tryMove(origin, new Coordinate(6, 7), board));
//    }

}
