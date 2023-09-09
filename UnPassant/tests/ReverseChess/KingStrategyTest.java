package ReverseChess;

import ReverseChess.StandardChess.*;
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
    public void testTryMoveCastle() {
        ChessBoard boardTwo = BoardBuilder.buildBoard("r3k2r/8/8/8/PPPPPPPP/8/8/R3K2R w KQkq - 0 1");
        Coordinate originTwo = new Coordinate(4, 0);
        Piece pieceTwo = boardTwo.at(originTwo);
        Assertions.assertTrue(pieceTwo.tryMove(originTwo, new Coordinate(2, 0), boardTwo));
        Assertions.assertTrue(pieceTwo.tryMove(originTwo, new Coordinate(6, 0), boardTwo));

        originTwo = new Coordinate(4, 7);
        pieceTwo = boardTwo.at(originTwo);
        Assertions.assertTrue(pieceTwo.tryMove(originTwo, new Coordinate(2, 7), boardTwo));
        Assertions.assertTrue(pieceTwo.tryMove(originTwo, new Coordinate(6, 7), boardTwo));

    }

    @Test
    public void testTryMoveCastleTwo() {
        ChessBoard boardTwo = BoardBuilder.buildBoard("r3k2r/8/8/8/PPPPPPPP/8/8/R3K2R w KQkq - 0 1");
        Coordinate originTwo = new Coordinate(4, 0);
        Piece pieceTwo = boardTwo.at(originTwo);
        Assertions.assertTrue(pieceTwo.tryMove(originTwo, new Coordinate(2, 0), boardTwo));
        Assertions.assertTrue(pieceTwo.tryMove(originTwo, new Coordinate(6, 0), boardTwo));
        System.out.println(boardTwo.getReader().toFEN());

        originTwo = new Coordinate(4, 7);
        pieceTwo = boardTwo.at(originTwo);
        Assertions.assertTrue(pieceTwo.tryMove(originTwo, new Coordinate(2, 7), boardTwo));
        Assertions.assertTrue(pieceTwo.tryMove(originTwo, new Coordinate(6, 7), boardTwo));

    }

    @Test
    public void testTryMoveCastleBlocking() {
        ChessBoard boardTwo = BoardBuilder.buildBoard("rb2k1Br/8/8/8/8/8/8/R2bKb1R");
        Coordinate originTwo = new Coordinate(4, 0);
        Piece pieceTwo = boardTwo.at(originTwo);
        Assertions.assertFalse(pieceTwo.tryMove(originTwo, new Coordinate(2, 0), boardTwo));
        Assertions.assertFalse(pieceTwo.tryMove(originTwo, new Coordinate(6, 0), boardTwo));

        originTwo = new Coordinate(4, 7);
        pieceTwo = boardTwo.at(originTwo);
        Assertions.assertFalse(pieceTwo.tryMove(originTwo, new Coordinate(2, 7), boardTwo));
        Assertions.assertFalse(pieceTwo.tryMove(originTwo, new Coordinate(6, 7), boardTwo));

    }

    @Test
    public void testTryMoveCastleCheck() {
        ChessBoard boardTwo = BoardBuilder.buildBoard("r3k2r/6B1/3R4/PPPPPPPP/8/8/8/R3K1rR");
        Coordinate originTwo = new Coordinate(4, 0);
        Piece pieceTwo = boardTwo.at(originTwo);
        Assertions.assertFalse(pieceTwo.tryMove(originTwo, new Coordinate(2, 0), boardTwo));
        Assertions.assertFalse(pieceTwo.tryMove(originTwo, new Coordinate(6, 0), boardTwo));

        originTwo = new Coordinate(4, 7);
        pieceTwo = boardTwo.at(originTwo);
        System.out.println(boardTwo.at(new Coordinate(2, 5)).getType());
        Assertions.assertFalse(pieceTwo.tryMove(originTwo, new Coordinate(2, 7), boardTwo));
        Assertions.assertFalse(pieceTwo.tryMove(originTwo, new Coordinate(6, 7), boardTwo));

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

    @Test
    public void testTryUnMoveCastle() {
        ChessBoard boardTwo = BoardBuilder.buildBoard("2kr1rk1/8/8/8/PPPPPPPP/8/8/2KR1RK1");
        Coordinate originTwo = new Coordinate(2, 0);
        Piece pieceTwo = boardTwo.at(originTwo);
        Assertions.assertTrue(pieceTwo.tryUnMove(originTwo, Coordinates.WHITE_KING, boardTwo));
        originTwo = new Coordinate(6, 0);
        pieceTwo = boardTwo.at(originTwo);
        Assertions.assertTrue(pieceTwo.tryUnMove(originTwo, Coordinates.WHITE_KING, boardTwo));

        originTwo = new Coordinate(2, 7);
        pieceTwo = boardTwo.at(originTwo);
        Assertions.assertTrue(pieceTwo.tryUnMove(originTwo, Coordinates.BLACK_KING, boardTwo));
        originTwo = new Coordinate(6, 7);
        pieceTwo = boardTwo.at(originTwo);
        Assertions.assertTrue(pieceTwo.tryUnMove(originTwo, Coordinates.BLACK_KING, boardTwo));

    }

    @Test
    public void testTryUnMoveCastleFen() {
        ChessBoard boardTwo = BoardBuilder.buildBoard("2kr1rk1/8/8/8/PPPPPPPP/8/8/2KR1RK1");
        Coordinate originTwo = new Coordinate(2, 0);
        Piece pieceTwo = boardTwo.at(originTwo);
        Assertions.assertTrue(pieceTwo.tryUnMove(originTwo, Coordinates.WHITE_KING, boardTwo));
        pieceTwo.updateBoard(originTwo, Coordinates.WHITE_KING, boardTwo, true);

        System.out.println(boardTwo.getReader().toFEN());
        originTwo = new Coordinate(6, 0);
        pieceTwo = boardTwo.at(originTwo);
        Assertions.assertTrue(pieceTwo.tryUnMove(originTwo, Coordinates.WHITE_KING, boardTwo));
        pieceTwo.updateBoard(originTwo, Coordinates.WHITE_KING, boardTwo, true);

        System.out.println(boardTwo.getReader().toFEN());

        originTwo = new Coordinate(2, 7);
        pieceTwo = boardTwo.at(originTwo);
        Assertions.assertTrue(pieceTwo.tryUnMove(originTwo, Coordinates.BLACK_KING, boardTwo));
        pieceTwo.updateBoard(originTwo, Coordinates.BLACK_KING, boardTwo, true);

        System.out.println(boardTwo.getReader().toFEN());
        originTwo = new Coordinate(6, 7);
        pieceTwo = boardTwo.at(originTwo);
        Assertions.assertTrue(pieceTwo.tryUnMove(originTwo, Coordinates.BLACK_KING, boardTwo));
        pieceTwo.updateBoard(originTwo, Coordinates.BLACK_KING, boardTwo, true);

        System.out.println(boardTwo.getReader().toFEN());

    }
    @Test
    public void testTryUnMoveCastleCheckingWrongSquareBug() {
        ChessBoard boardTwo = BoardBuilder.buildBoard("2kr1rk1/8/8/8/PPPPPPPP/8/8/5RK1");
        Coordinate originTwo = new Coordinate(6, 0);
        Piece pieceTwo = boardTwo.at(originTwo);
        Assertions.assertTrue(pieceTwo.tryUnMove(originTwo, Coordinates.WHITE_KING, boardTwo));
    }

    @Test
    public void testTryUnMoveCastleCheck() {
        ChessBoard boardTwo = BoardBuilder.buildBoard("2kr1rk1/6B1/3R4/PPPPPPPP/8/8/4q3/2KR1RK1");
        Coordinate originTwo = new Coordinate(2, 0);
        Piece pieceTwo = boardTwo.at(originTwo);
        Assertions.assertFalse(pieceTwo.tryUnMove(originTwo, Coordinates.WHITE_KING, boardTwo));
        originTwo = new Coordinate(6, 0);
        pieceTwo = boardTwo.at(originTwo);
        Assertions.assertFalse(pieceTwo.tryUnMove(originTwo, Coordinates.WHITE_KING, boardTwo));

        originTwo = new Coordinate(2, 7);
        pieceTwo = boardTwo.at(originTwo);
        Assertions.assertFalse(pieceTwo.tryUnMove(originTwo, Coordinates.BLACK_KING, boardTwo));
        originTwo = new Coordinate(6, 7);
        pieceTwo = boardTwo.at(originTwo);
        Assertions.assertFalse(pieceTwo.tryUnMove(originTwo, Coordinates.BLACK_KING, boardTwo));

    }

    @Test
    public void testTryUnMoveCastleBadKingPosition() {
        ChessBoard boardTwo = BoardBuilder.buildBoard("1k1r1r1k/8/8/8/8/8/8/1K1R1R1K");
        Coordinate originTwo = new Coordinate(1, 0);
        Piece pieceTwo = boardTwo.at(originTwo);
        Assertions.assertFalse(pieceTwo.tryUnMove(originTwo, Coordinates.WHITE_KING, boardTwo));
        originTwo = new Coordinate(7, 0);
        pieceTwo = boardTwo.at(originTwo);
        Assertions.assertFalse(pieceTwo.tryUnMove(originTwo, Coordinates.WHITE_KING, boardTwo));

        originTwo = new Coordinate(2, 7);
        pieceTwo = boardTwo.at(originTwo);
        Assertions.assertFalse(pieceTwo.tryUnMove(originTwo, Coordinates.BLACK_KING, boardTwo));
        originTwo = new Coordinate(7, 7);
        pieceTwo = boardTwo.at(originTwo);
        Assertions.assertFalse(pieceTwo.tryUnMove(originTwo, Coordinates.BLACK_KING, boardTwo));

    }
    @Test
    public void testTryUnMoveCastleBadRookPosition() {
        ChessBoard boardTwo = BoardBuilder.buildBoard("2k3kr/8/8/8/8/8/8/2K1RRK1");
        Coordinate originTwo = new Coordinate(2, 0);
        Piece pieceTwo = boardTwo.at(originTwo);
        Assertions.assertFalse(pieceTwo.tryUnMove(originTwo, Coordinates.WHITE_KING, boardTwo));
        originTwo = new Coordinate(6, 0);
        pieceTwo = boardTwo.at(originTwo);
        Assertions.assertFalse(pieceTwo.tryUnMove(originTwo, Coordinates.WHITE_KING, boardTwo));

        originTwo = new Coordinate(2, 7);
        pieceTwo = boardTwo.at(originTwo);
        Assertions.assertFalse(pieceTwo.tryUnMove(originTwo, Coordinates.BLACK_KING, boardTwo));
        originTwo = new Coordinate(6, 7);
        pieceTwo = boardTwo.at(originTwo);
        Assertions.assertFalse(pieceTwo.tryUnMove(originTwo, Coordinates.BLACK_KING, boardTwo));

    }

    @Test
    public void testTryUnMoveCastlePieceBlocking() {
        ChessBoard boardTwo = BoardBuilder.buildBoard("1bkr1rkr/8/8/8/8/8/8/1RKRrRK1");
        Coordinate originTwo = new Coordinate(2, 0);
        Piece pieceTwo = boardTwo.at(originTwo);
        Assertions.assertFalse(pieceTwo.tryUnMove(originTwo, Coordinates.WHITE_KING, boardTwo));
        originTwo = new Coordinate(6, 0);
        pieceTwo = boardTwo.at(originTwo);
        Assertions.assertFalse(pieceTwo.tryUnMove(originTwo, Coordinates.WHITE_KING, boardTwo));

        originTwo = new Coordinate(2, 7);
        pieceTwo = boardTwo.at(originTwo);
        Assertions.assertFalse(pieceTwo.tryUnMove(originTwo, Coordinates.BLACK_KING, boardTwo));
        originTwo = new Coordinate(6, 7);
        pieceTwo = boardTwo.at(originTwo);
        Assertions.assertFalse(pieceTwo.tryUnMove(originTwo, Coordinates.BLACK_KING, boardTwo));

    }

    @Test
    public void testTryUnMoveIntoCheck() {
        ChessBoard boardTwo = BoardBuilder.buildBoard("rnbqkbnr/pppp4/4p3/6Q1/8/8/PPPPP3/RNB1KBNR b KQkq - 0 1");
        Coordinate originTwo = new Coordinate(4, 6);
        Piece pieceTwo = boardTwo.at(Coordinates.BLACK_KING);
        Assertions.assertTrue(pieceTwo.tryUnMove(Coordinates.BLACK_KING, originTwo, boardTwo));

    }

}
