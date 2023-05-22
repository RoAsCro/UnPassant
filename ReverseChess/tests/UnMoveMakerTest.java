import StandardChess.*;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class UnMoveMakerTest {

    ChessBoard board = BoardBuilder.buildBoard("2k2bnr/prqp2pP/bp3p2/nQpP2B1/1P2p1p1/2NPNB2/P1P2PPP/R4RK1 w - - 0 1");
    ChessBoard boardTwo = BoardBuilder.buildBoard("rnbqkbnr/p1p2pp1/2P2P2/5n1p/8/3p4/PP1P1PPP/RNBQKBNR w KQkq - 0 1");



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
    public void tryMakeUnMoveCastle() {
        Coordinate target = new Coordinate(4, 0);
        Coordinate origin = new Coordinate(6, 0);
        this.board.setTurn("w");
        makeUnMove(origin, target, "null", "king", true, this.board);
        Assertions.assertEquals("null", this.board.at(new Coordinate(5, 0)).getType());
        Assertions.assertEquals("rook", this.board.at(new Coordinate(7, 0)).getType());
    }

}
