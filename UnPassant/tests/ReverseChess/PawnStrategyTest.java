package ReverseChess;

import ReverseChess.StandardChess.BoardBuilder;
import ReverseChess.StandardChess.ChessBoard;
import ReverseChess.StandardChess.Coordinate;
import ReverseChess.StandardChess.Piece;
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
        Assertions.assertFalse(boardTwo.at(whiteOriginTwo).tryMove(whiteOriginTwo, new Coordinate(0, 5), boardTwo));


        Coordinate blackOriginTwo = new Coordinate(7, 5);
        Assertions.assertFalse(boardTwo.at(blackOriginTwo).tryMove(blackOriginTwo, new Coordinate(7, 3), boardTwo));
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

    @Test
    public void testTryUnMoveNormal() {
        ChessBoard boardTwo = BoardBuilder.buildBoard("1P1P4/2p5/1p1p4/4P3/4p3/1P1P4/2P5/1P1P4");
        Coordinate whiteOriginTwo =new Coordinate(1, 2);
        Piece whitePieceTwo = boardTwo.at(whiteOriginTwo);
        Assertions.assertTrue(whitePieceTwo.tryUnMove(whiteOriginTwo, new Coordinate(1, 1), boardTwo));

        Coordinate blackOriginTwo =new Coordinate(1, 5);
        Piece blackPieceTwo = boardTwo.at(whiteOriginTwo);
        Assertions.assertTrue(blackPieceTwo.tryUnMove(blackOriginTwo, new Coordinate(1, 6), boardTwo));
    }

    @Test
    public void testTryUnMoveNormalBlocked() {
        ChessBoard boardTwo = BoardBuilder.buildBoard("1P1P4/1Pp5/1p1p4/4P3/4p3/1P1P4/1pP5/1P1P4");
        Coordinate whiteOriginTwo =new Coordinate(1, 2);
        Piece whitePieceTwo = boardTwo.at(whiteOriginTwo);
        Assertions.assertFalse(whitePieceTwo.tryUnMove(whiteOriginTwo, new Coordinate(1, 1), boardTwo));

        Coordinate blackOriginTwo =new Coordinate(1, 5);
        Piece blackPieceTwo = boardTwo.at(whiteOriginTwo);
        Assertions.assertFalse(blackPieceTwo.tryUnMove(blackOriginTwo, new Coordinate(1, 6), boardTwo));
    }

    @Test
    public void testTryUnMoveWrongDirection() {
        ChessBoard boardTwo = BoardBuilder.buildBoard("1P1P4/2p5/1p1p4/4P3/4p3/1P1P4/2P5/1P1P4");
        Coordinate whiteOriginTwo =new Coordinate(1, 2);
        Piece whitePieceTwo = boardTwo.at(whiteOriginTwo);
        Assertions.assertFalse(whitePieceTwo.tryUnMove(whiteOriginTwo, new Coordinate(1, 3), boardTwo));

        Coordinate blackOriginTwo =new Coordinate(1, 5);
        Piece blackPieceTwo = boardTwo.at(whiteOriginTwo);
        Assertions.assertFalse(blackPieceTwo.tryUnMove(blackOriginTwo, new Coordinate(1, 4), boardTwo));
    }

    @Test
    public void testTryUnMoveTooFar() {
        ChessBoard boardTwo = BoardBuilder.buildBoard("8/8/8/3P4/4p3/8/8/8");
        Coordinate whiteOriginTwo =new Coordinate(3, 4);
        Piece whitePieceTwo = boardTwo.at(whiteOriginTwo);
        Assertions.assertFalse(whitePieceTwo.tryUnMove(whiteOriginTwo, new Coordinate(3, 1), boardTwo));

        Coordinate blackOriginTwo =new Coordinate(4, 3);
        Piece blackPieceTwo = boardTwo.at(whiteOriginTwo);
        Assertions.assertFalse(blackPieceTwo.tryUnMove(blackOriginTwo, new Coordinate(4, 6), boardTwo));
    }

    @Test
    public void testTryUnMoveDouble() {
        ChessBoard boardTwo = BoardBuilder.buildBoard("8/8/8/4p3/4P3/8/8/8");
        Coordinate whiteOriginTwo =new Coordinate(4, 3);
        Piece whitePieceTwo = boardTwo.at(whiteOriginTwo);
        Assertions.assertTrue(whitePieceTwo.tryUnMove(whiteOriginTwo, new Coordinate(4, 1), boardTwo));

        Coordinate blackOriginTwo =new Coordinate(4, 4);
        Piece blackPieceTwo = boardTwo.at(whiteOriginTwo);
        Assertions.assertTrue(blackPieceTwo.tryUnMove(blackOriginTwo, new Coordinate(4, 6), boardTwo));
    }

    @Test
    public void testTryUnMoveDoubleBadPosition() {
        ChessBoard boardTwo = BoardBuilder.buildBoard("8/8/8/3P4/4p3/8/8/8");
        Coordinate whiteOriginTwo =new Coordinate(3, 4);
        Piece whitePieceTwo = boardTwo.at(whiteOriginTwo);
        Assertions.assertFalse(whitePieceTwo.tryUnMove(whiteOriginTwo, new Coordinate(3, 2), boardTwo));

        Coordinate blackOriginTwo =new Coordinate(4, 3);
        Piece blackPieceTwo = boardTwo.at(whiteOriginTwo);
        Assertions.assertFalse(blackPieceTwo.tryUnMove(blackOriginTwo, new Coordinate(4, 5), boardTwo));
    }

    @Test
    public void testTryUnMoveDoubleBlockedPartway() {
        ChessBoard boardTwo = BoardBuilder.buildBoard("8/8/4N3/4p3/4P3/4n3/8/8");
        Coordinate whiteOriginTwo =new Coordinate(4, 3);
        Piece whitePieceTwo = boardTwo.at(whiteOriginTwo);
        Assertions.assertFalse(whitePieceTwo.tryUnMove(whiteOriginTwo, new Coordinate(4, 1), boardTwo));

        Coordinate blackOriginTwo =new Coordinate(4, 4);
        Piece blackPieceTwo = boardTwo.at(whiteOriginTwo);
        Assertions.assertFalse(blackPieceTwo.tryUnMove(blackOriginTwo, new Coordinate(4, 6), boardTwo));
    }

    @Test
    public void testTryUnMoveDoubleBlockedOnTarget() {
        ChessBoard boardTwo = BoardBuilder.buildBoard("8/4N3/8/4p3/4P3/8/4n3/8");
        Coordinate whiteOriginTwo =new Coordinate(4, 3);
        Piece whitePieceTwo = boardTwo.at(whiteOriginTwo);
        Assertions.assertFalse(whitePieceTwo.tryUnMove(whiteOriginTwo, new Coordinate(4, 1), boardTwo));

        Coordinate blackOriginTwo =new Coordinate(4, 4);
        Piece blackPieceTwo = boardTwo.at(whiteOriginTwo);
        Assertions.assertFalse(blackPieceTwo.tryUnMove(blackOriginTwo, new Coordinate(4, 6), boardTwo));
    }

    @Test
    public void testTryUnCapture() {
        ChessBoard boardTwo = BoardBuilder.buildBoard("8/8/4P3/8/8/4p3/8/8");
        Coordinate whiteOriginTwo =new Coordinate(4, 5);
        Piece whitePieceTwo = boardTwo.at(whiteOriginTwo);
        Assertions.assertTrue(whitePieceTwo.tryUnMove(whiteOriginTwo, new Coordinate(5, 4), boardTwo));

        Coordinate blackOriginTwo =new Coordinate(4, 2);
        Piece blackPieceTwo = boardTwo.at(blackOriginTwo);
        Assertions.assertTrue(blackPieceTwo.tryUnMove(blackOriginTwo, new Coordinate(3, 3), boardTwo));
    }

    @Test
    public void testTryUnCaptureBlocked() {
        ChessBoard boardTwo = BoardBuilder.buildBoard("8/8/4P3/5n2/3N4/4p3/8/8");
        Coordinate whiteOriginTwo =new Coordinate(4, 5);
        Piece whitePieceTwo = boardTwo.at(whiteOriginTwo);
        Assertions.assertFalse(whitePieceTwo.tryUnMove(whiteOriginTwo, new Coordinate(5, 4), boardTwo));

        Coordinate blackOriginTwo =new Coordinate(4, 2);
        Piece blackPieceTwo = boardTwo.at(blackOriginTwo);
        Assertions.assertFalse(blackPieceTwo.tryUnMove(blackOriginTwo, new Coordinate(3, 3), boardTwo));
    }
    @Test
    public void testTryUnCaptureTooFar() {
        ChessBoard boardTwo = BoardBuilder.buildBoard("8/8/4P3/8/8/4p3/8/8");
        Coordinate whiteOriginTwo =new Coordinate(4, 5);
        Piece whitePieceTwo = boardTwo.at(whiteOriginTwo);
        Assertions.assertFalse(whitePieceTwo.tryUnMove(whiteOriginTwo, new Coordinate(6, 3), boardTwo));

        Coordinate blackOriginTwo =new Coordinate(4, 2);
        Piece blackPieceTwo = boardTwo.at(blackOriginTwo);
        Assertions.assertFalse(blackPieceTwo.tryUnMove(blackOriginTwo, new Coordinate(2, 4), boardTwo));
    }

    @Test
    public void testTryUnMoveBackRow() {
        ChessBoard boardTwo = BoardBuilder.buildBoard("8/7p/8/8/8/8/7P/8");
        Coordinate whiteOriginTwo =new Coordinate(7, 1);
        Piece whitePieceTwo = boardTwo.at(whiteOriginTwo);
        Assertions.assertFalse(whitePieceTwo.tryUnMove(whiteOriginTwo, new Coordinate(7, 0), boardTwo));

        Coordinate blackOriginTwo =new Coordinate(7, 6);
        Piece blackPieceTwo = boardTwo.at(blackOriginTwo);
        Assertions.assertFalse(blackPieceTwo.tryUnMove(blackOriginTwo, new Coordinate(7, 7), boardTwo));
    }

}
