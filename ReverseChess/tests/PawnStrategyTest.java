import StandardChess.BoardBuilder;
import StandardChess.ChessBoard;
import StandardChess.Coordinate;
import StandardChess.Piece;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class PawnStrategyTest {

    ChessBoard board = BoardBuilder.buildBoard("1P1P4/2p5/1P1P4/4P3/4p3/1p1p4/2P5/1P1P4");
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
    public void testTryMoveDoubleMove() {
        Assertions.assertTrue(whitePiece.tryMove(whiteOrigin, new Coordinate(2, 3), board));

        Assertions.assertTrue(blackPiece.tryMove(blackOrigin, new Coordinate(2, 4), board));
    }

    @Test
    public void testTryMoveDoubleMoveBadPosition() {
        ChessBoard boardTwo = BoardBuilder.buildBoard("8/8/7p/8/P7/8/8/8");

        Coordinate whiteOriginTwo = new Coordinate(0, 3);
        Assertions.assertTrue(board.at(whiteOriginTwo).tryMove(whiteOriginTwo, new Coordinate(0, 5), board));

        Coordinate blackOriginTwo = new Coordinate(7, 5);
        Assertions.assertTrue(board.at(blackOriginTwo).tryMove(blackOriginTwo, new Coordinate(7, 3), board));
    }

    @Test
    public void testTryMoveEnPassant() {
        ChessBoard boardTwo = BoardBuilder.buildBoard(
                "rnbqkbnr/8/8/3pP3/3Pp3/8/8/RNBQKBNR w KQkq d3 0 1");
        boardTwo.setEnPassant(new Coordinate(3, 5));
        Coordinate whiteOriginTwo = new Coordinate(4,4);
        Piece whitePieceTwo = boardTwo.at(whiteOriginTwo);
        Assertions.assertTrue(whitePieceTwo.tryMove(whiteOriginTwo, new Coordinate(3, 5), boardTwo));

        boardTwo.setEnPassant(new Coordinate(3, 2));
        Coordinate blackOriginTwo = new Coordinate(4,3);
        Piece blackPieceTwo = boardTwo.at(blackOriginTwo);
        Assertions.assertTrue(blackPieceTwo.tryMove(blackOriginTwo, new Coordinate(3, 2), boardTwo));
    }

    @Test
    public void testTryMoveNonPassant() {
        ChessBoard boardTwo = BoardBuilder.buildBoard(
                "rnbqkbnr/8/8/3pP3/3Pp3/8/8/RNBQKBNR w KQkq d3 0 1");
        boardTwo.setEnPassant(new Coordinate(3, 5));
        Coordinate whiteOriginTwo = new Coordinate(4,4);
        Piece whitePieceTwo = boardTwo.at(whiteOriginTwo);
        Assertions.assertFalse(whitePieceTwo.tryMove(whiteOriginTwo, new Coordinate(5, 5), boardTwo));

        boardTwo.setEnPassant(new Coordinate(3, 2));
        Coordinate blackOriginTwo = new Coordinate(4,3);
        Piece blackPieceTwo = boardTwo.at(blackOriginTwo);
        Assertions.assertFalse(blackPieceTwo.tryMove(blackOriginTwo, new Coordinate(5, 2), boardTwo));
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
    public void testTryMoveCollisionAlliedPiecesDoubleMove() {
        ChessBoard boardTwo = BoardBuilder.buildBoard("8/2p5/8/2p5/2P5/8/2P5/8");

        Assertions.assertFalse(whitePiece.tryMove(whiteOrigin, new Coordinate(2, 3), boardTwo));

        Assertions.assertFalse(blackPiece.tryMove(blackOrigin, new Coordinate(2, 4), boardTwo));

    }

    @Test
    public void testTryMoveCollisionEnemyPieces() {
        ChessBoard boardTwo = BoardBuilder.buildBoard("1P1P4/2p5/1pPp4/8/8/1PpP4/2P5/1P1P4");

        Assertions.assertFalse(whitePiece.tryMove(whiteOrigin, new Coordinate(2, 2), boardTwo));

        Assertions.assertFalse(blackPiece.tryMove(blackOrigin, new Coordinate(2, 5), boardTwo));

    }

    @Test
    public void testTryMoveCollisionEnemyPiecesDoubleMove() {
        ChessBoard boardTwo = BoardBuilder.buildBoard("8/2p5/8/2P5/2p5/8/2P5/8");

        Assertions.assertFalse(whitePiece.tryMove(whiteOrigin, new Coordinate(2, 3), boardTwo));

        Assertions.assertFalse(blackPiece.tryMove(blackOrigin, new Coordinate(2, 4), boardTwo));

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


    @Test
    public void testTryMoveInvalidDistanceDiagonal() {
        Assertions.assertFalse(whitePiece.tryMove(whiteOrigin, new Coordinate(4, 3), board));
        Assertions.assertFalse(whitePiece.tryMove(whiteOrigin, new Coordinate(0, 3), board));

        Assertions.assertFalse(blackPiece.tryMove(blackOrigin, new Coordinate(4, 4), board));
        Assertions.assertFalse(blackPiece.tryMove(blackOrigin, new Coordinate(0, 4), board));
    }

    @Test
    public void testTryMoveInvalidDistanceVertical() {
        Assertions.assertFalse(whitePiece.tryMove(whiteOrigin, new Coordinate(2, 4), board));

        Assertions.assertFalse(blackPiece.tryMove(blackOrigin, new Coordinate(2, 3), board));
    }

}
