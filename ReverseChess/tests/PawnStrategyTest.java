import StandardChess.BoardBuilder;
import StandardChess.ChessBoard;
import StandardChess.Coordinate;
import StandardChess.Piece;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class PawnStrategyTest {

    ChessBoard board = BoardBuilder.buildBoard("1P1P4/2p5/1P1P4/8/8/1p1p4/2P5/1P1P4");
    Coordinate whiteOrigin = new Coordinate(2, 1);
    Coordinate blackOrigin = new Coordinate(2,6);
    Piece whitePiece = board.at(whiteOrigin);
    Piece blackPiece = board.at(blackOrigin);


    @Test
    public void testTryMoveNormal() {
        Assertions.assertTrue(whitePiece.tryMove(whiteOrigin, new Coordinate(2, 2), board));
        Assertions.assertTrue(blackPiece.tryMove(blackOrigin, new Coordinate(2, 5), board));
    }

    @Test
    public void testTryMoveNormalCapture() {
        Assertions.assertTrue(whitePiece.tryMove(whiteOrigin, new Coordinate(1, 2), board));
        Assertions.assertTrue(whitePiece.tryMove(whiteOrigin, new Coordinate(3, 2), board));

        Assertions.assertTrue(blackPiece.tryMove(blackOrigin, new Coordinate(1, 5), board));
        Assertions.assertTrue(blackPiece.tryMove(blackOrigin, new Coordinate(3, 5), board));

    }

    @Test
    public void testTryMoveCollisionAlliedPieces() {
        ChessBoard boardTwo = BoardBuilder.buildBoard("1P1P4/2p5/1ppp4/8/8/1PPP4/2P5/1P1P4");

        Assertions.assertFalse(whitePiece.tryMove(whiteOrigin, new Coordinate(2, 2), boardTwo));
        Assertions.assertFalse(whitePiece.tryMove(whiteOrigin, new Coordinate(1, 2), boardTwo));
        Assertions.assertFalse(whitePiece.tryMove(whiteOrigin, new Coordinate(3, 2), boardTwo));

        Assertions.assertFalse(blackPiece.tryMove(blackOrigin, new Coordinate(2, 5), boardTwo));
        Assertions.assertFalse(blackPiece.tryMove(blackOrigin, new Coordinate(1, 5), boardTwo));
        Assertions.assertFalse(blackPiece.tryMove(blackOrigin, new Coordinate(3, 5), boardTwo));

    }

    @Test
    public void testTryMoveCollisionEnemyPieces() {
        ChessBoard boardTwo = BoardBuilder.buildBoard("1P1P4/2p5/1pPp4/8/8/1PpP4/2P5/1P1P4");

        Assertions.assertFalse(whitePiece.tryMove(whiteOrigin, new Coordinate(2, 2), boardTwo));

        Assertions.assertFalse(blackPiece.tryMove(blackOrigin, new Coordinate(2, 5), boardTwo));

    }
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

    @Test
    public void testTryMoveInvalidDirectionDiagonal() {
        Assertions.assertFalse(whitePiece.tryMove(whiteOrigin, new Coordinate(1, 0), board));
        Assertions.assertFalse(whitePiece.tryMove(whiteOrigin, new Coordinate(3, 0), board));


        Assertions.assertFalse(blackPiece.tryMove(blackOrigin, new Coordinate(1, 7), board));
        Assertions.assertFalse(blackPiece.tryMove(blackOrigin, new Coordinate(3, 7), board));

    }


//
//    @Test
//    public void testTryMoveInvalidDistance() {
//        Assertions.assertFalse(piece.tryMove(origin, new Coordinate(4, 0), board));
//        Assertions.assertFalse(piece.tryMove(origin, new Coordinate(6, 7), board));
//    }

}
