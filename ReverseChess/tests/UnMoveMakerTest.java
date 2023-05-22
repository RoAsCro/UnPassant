import StandardChess.*;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class UnMoveMakerTest {

    ChessBoard board = BoardBuilder.buildBoard("2k2bnr/prqp2pP/bp3p2/nQpP2B1/1P2p1p1/2NPNB2/P1P2PPP/R4RK1 w - - 0 1");
    ChessBoard boardTwo = BoardBuilder.buildBoard("rnbq1rk1/p1p2pp1/2P2P2/5n1p/8/1N1p4/PP1P1PPP/2KR1B1R w Kq - 0 1");
    ChessBoard boardThree = BoardBuilder.buildBoard("2kr1bn1/pppppppp/4q3/8/4R3/8/PPPPPPPP/1NKR1B2 w - - 0 1");
    ChessBoard boardFour = BoardBuilder.buildBoard("rnB1kNnr/pp2p1pp/1p1p4/4b3/8/5P2/PPP1PPPP/RNqQKBNR w KQkq - 0 1");




    public void makeUnMove(Coordinate origin, Coordinate target, String originType, String targetType, boolean pass, ChessBoard board) {
        UnMoveMaker unMoveMaker = new UnMoveMaker(board);
        Assertions.assertEquals(pass, unMoveMaker.makeUnMove(origin, target));
        Assertions.assertEquals(targetType, board.at(target).getType(), "target piece wrong");
        Assertions.assertEquals(originType, board.at(origin).getType(), "origin piece wrong");
    }

    public void makeUnMoveWithCapture(Coordinate origin, Coordinate target, String originType, String targetType, boolean pass, String capturePiece, ChessBoard board, boolean ep) {
        UnMoveMaker unMoveMaker = new UnMoveMaker(board);
        unMoveMaker.setCaptureFlag(true);
        unMoveMaker.setEnPassantFlag(ep);
        unMoveMaker.setCapturePiece(StandardPieceFactory.getInstance().getPiece(capturePiece));
        Assertions.assertEquals(pass, unMoveMaker.makeUnMove(origin, target));
        Assertions.assertEquals(targetType, board.at(target).getType(), "target piece wrong");
        Assertions.assertEquals(originType, board.at(origin).getType(), "origin piece wrong");
        unMoveMaker.setEnPassantFlag(false);
    }

    public void makeUnMoveWithPromotion(Coordinate origin, Coordinate target, String originType, String targetType, boolean pass, ChessBoard board, boolean capture, String piece ) {
        UnMoveMaker unMoveMaker = new UnMoveMaker(board);
        unMoveMaker.setCaptureFlag(true);
        unMoveMaker.setPromotionFlag(true);
        unMoveMaker.setCaptureFlag(capture);
        unMoveMaker.setCapturePiece(StandardPieceFactory.getInstance().getPiece(piece));
        Assertions.assertEquals(pass, unMoveMaker.makeUnMove(origin, target));
        Assertions.assertEquals(targetType, board.at(target).getType(), "target piece wrong");
        Assertions.assertEquals(originType, board.at(origin).getType(), "origin piece wrong");
        unMoveMaker.setPromotionFlag(false);
        unMoveMaker.setCaptureFlag(false);

    }

    @Test
    public void tryMakeUnMove() {
        Coordinate target = new Coordinate(0, 3);
        Coordinate origin = new Coordinate(2, 2);
        makeUnMove(origin, target, "null", "knight", true, this.board);
    }

    @Test
    public void tryMakeUnMoveBlockedAlly() {
        Coordinate target = new Coordinate(2, 1);
        Coordinate origin = new Coordinate(4, 2);
        makeUnMove(origin, target, "knight", "pawn", false, this.board);
    }

    @Test
    public void tryMakeUnMoveBlockedEnemy() {
        Coordinate target = new Coordinate(6, 3);
        Coordinate origin = new Coordinate(4, 2);
        makeUnMove(origin, target, "knight", "pawn", false, this.board);;
    }

    @Test
    public void tryMakeUnMovePawnRightDirection() {
        Coordinate target = new Coordinate(5, 6);
        Coordinate origin = new Coordinate(5, 5);
        makeUnMove(origin, target, "null", "pawn", true, this.board);
    }

    @Test
    public void tryMakeUnMovePawnWrongDirection() {
        Coordinate target = new Coordinate(6, 2);
        Coordinate origin = new Coordinate(6, 3);
        makeUnMove(origin, target, "pawn", "null", false, this.board);
    }

    @Test
    public void tryMakeUnMovePawnDoubleMovePass() {
        Coordinate target = new Coordinate(1, 1);
        Coordinate origin = new Coordinate(1, 3);
        makeUnMove(origin, target, "null", "pawn", true, this.board);
    }

    @Test
    public void tryMakeUnMovePawnDoubleMoveFail() {
        Coordinate target = new Coordinate(4, 5);
        Coordinate origin = new Coordinate(4, 3);
        makeUnMove(origin, target, "pawn", "null", false, this.board);
    }

    @Test
    public void tryMakeUnMovePawnUnCaptureCaptureFlag() {
        Coordinate target = new Coordinate(3, 5);
        Coordinate origin = new Coordinate(2, 4);
        makeUnMoveWithCapture(origin, target, "queen", "pawn", true, "q", this.board, false);
    }


    @Test
    public void tryMakeUnMovePawnUnCaptureNoCaptureFlag() {
        Coordinate target = new Coordinate(4, 1);
        Coordinate origin = new Coordinate(3, 2);
        makeUnMove(origin, target, "pawn", "null", false, this.boardTwo);
    }

    @Test
    public void tryMakeUnMovePawnUnPassant() {
        Coordinate target = new Coordinate(4, 3);
        Coordinate origin = new Coordinate(3, 2);
        this.boardTwo.setTurn("b");
        makeUnMoveWithCapture(origin, target, "null", "pawn",  true, "p", this.boardTwo, true);
        Assertions.assertEquals("pawn", this.boardTwo.at(new Coordinate(3, 3)).getType());
    }

    @Test
    public void tryMakeUnMovePawnUnPassantUnCaptureLocationBlocked() {
        Coordinate target = new Coordinate(4, 4);
        Coordinate origin = new Coordinate(5, 5);
        this.boardTwo.setTurn("w");
        makeUnMoveWithCapture(origin, target, "pawn", "null",  false, "p", this.boardTwo, true);
        Assertions.assertEquals("knight", this.boardTwo.at(new Coordinate(5, 4)).getType());
    }
    @Test
    public void tryMakeUnMovePawnUnPassantNotAPawn() {
        Coordinate target = new Coordinate(4, 4);
        Coordinate origin = new Coordinate(4, 3);
        this.boardThree.setTurn("w");
        makeUnMoveWithCapture(origin, target, "rook", "null",  false, "p", this.boardThree, true);
    }

    @Test
    public void tryMakeUnMoveCastleWhiteKing() {
        Coordinate target = Coordinates.WHITE_KING;
        Coordinate origin = new Coordinate(6, 0);
        this.board.setTurn("w");
        makeUnMove(origin, target, "null", "king", true, this.board);
        Assertions.assertEquals("null", this.board.at(new Coordinate(5, 0)).getType());
        Assertions.assertEquals("rook", this.board.at(new Coordinate(7, 0)).getType());
    }

    @Test
    public void tryMakeUnMoveCastleWhiteQueen() {
        Coordinate target = Coordinates.WHITE_KING;
        Coordinate origin = new Coordinate(2, 0);
        this.boardTwo.setTurn("w");
        makeUnMove(origin, target, "null", "king", true, this.boardTwo);
        Assertions.assertEquals("null", this.boardTwo.at(new Coordinate(1, 0)).getType());
        Assertions.assertEquals("rook", this.boardTwo.at(new Coordinate(0, 0)).getType());
    }

    @Test
    public void tryMakeUnMoveCastleBlackKing() {
        Coordinate target = Coordinates.BLACK_KING;
        Coordinate origin = new Coordinate(6, 7);
        this.boardTwo.setTurn("w");
        makeUnMove(origin, target, "null", "king", true, this.boardTwo);
        Assertions.assertEquals("null", this.boardTwo.at(new Coordinate(6, 7)).getType());
        Assertions.assertEquals("rook", this.boardTwo.at(new Coordinate(7, 7)).getType());
    }

    @Test
    public void tryMakeUnMoveCastleBlackQueen() {
        Coordinate target = Coordinates.BLACK_KING;
        Coordinate origin = new Coordinate(2, 7);
        this.boardThree.setTurn("b");
        makeUnMove(origin, target, "null", "king", true, this.boardThree);
        Assertions.assertEquals("null", this.boardThree.at(new Coordinate(1, 7)).getType());
        Assertions.assertEquals("rook", this.boardThree.at(new Coordinate(0, 7)).getType());
    }

    @Test
    public void tryMakeUnMoveCastleBlocked() {
        Coordinate target = Coordinates.WHITE_KING;
        Coordinate origin = new Coordinate(2, 0);
        this.boardThree.setTurn("w");
        makeUnMove(origin, target, "king", "null", false, this.boardThree);
        Assertions.assertEquals("knight", this.boardThree.at(new Coordinate(1, 0)).getType());
        Assertions.assertEquals("null", this.boardThree.at(new Coordinate(0, 0)).getType());
    }

    @Test
    public void tryMakeUnMoveCastleTaking() {
        ChessBoard board = BoardBuilder.buildBoard("2kr1bn1/pppppppp/4q3/8/4R3/1N6/PPPPPPPP/2KR1B2 w - - 0 1");
        Coordinate target = Coordinates.WHITE_KING;
        Coordinate origin = new Coordinate(2, 0);
        board.setTurn("w");
        makeUnMoveWithCapture(origin, target, "king", "null", false, "r", board, false);
        Assertions.assertEquals("rook", board.at(new Coordinate(3, 0)).getType());
        Assertions.assertEquals("null", board.at(new Coordinate(0, 0)).getType());
    }

    @Test
    public void tryMakeUnMovePromote() {
        Coordinate target = new Coordinate(2, 6);
        Coordinate origin = new Coordinate(2, 7);
        boardFour.setTurn("w");
        makeUnMoveWithPromotion(origin, target, "null", "pawn", true, boardFour, false, "p");
    }

    @Test
    public void tryMakeUnMovePromoteCapture() {
        Coordinate target = new Coordinate(3, 1);
        Coordinate origin = new Coordinate(2, 0);
        boardFour.setTurn("b");
        makeUnMoveWithPromotion(origin, target, "queen", "pawn", true, boardFour, true, "q");
    }

    @Test
    public void tryMakeUnMovePromoteTooFar() {
        Coordinate target = new Coordinate(6, 5);
        Coordinate origin = new Coordinate(5, 7);
        boardFour.setTurn("w");
        makeUnMoveWithPromotion(origin, target, "knight", "null", false, boardFour, false, "p");
    }

    @Test
    public void tryMakeUnMovePromoteCaptureVertical() {
        Coordinate target = new Coordinate(5, 6);
        Coordinate origin = new Coordinate(5, 7);
        boardFour.setTurn("w");
        makeUnMoveWithPromotion(origin, target, "knight", "null", false, boardFour, true, "q");
    }

}
