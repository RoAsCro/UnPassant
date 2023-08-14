import SolveAlgorithm.Solver;
import StandardChess.BoardBuilder;
import StandardChess.ChessBoard;
import StandardChess.Coordinate;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class SolverSingleMovesTest {

    public void makeMoveTest(String fen, Coordinate origin, Coordinate target, boolean pass, String piece, boolean promotion) {
        makeMoveTest(fen, origin, target, pass, piece, promotion, false);

    }

    public void makeMoveTest(String fen, Coordinate origin, Coordinate target, boolean pass) {
        makeMoveTest(fen, origin, target, pass, "", false);
    }

    public void makeMoveTest(String fen, Coordinate origin, Coordinate target, boolean pass, String piece) {
        makeMoveTest(fen, origin, target, pass, piece, false);
    }

    public void makeMoveTest(String fen, Coordinate origin, Coordinate target, boolean pass, String piece, boolean promotion, boolean enPassant) {
        ChessBoard board = BoardBuilder.buildBoard(fen);
        Assertions.assertEquals(pass, new Solver().makeMove(board,
                origin, target, piece, promotion, enPassant), (board.getReader().toFEN()));
        System.out.println(board.getReader().toFEN());
    }



    @Test
    public void makeMoveTestOne() {
        makeMoveTest("3qkbnr/pp1ppppp/2p5/5P1P/3QP3/2P1Q1P1/8/RNBQKBNR w KQk - 0 1",
                new Coordinate(7, 4), new Coordinate(7, 3),
                true);
    }

    @Test
    public void makeMoveTestPawnToBackLine() {
        makeMoveTest("rnbqk1nr/pppppppp/8/8/8/8/PPPPPPPP/RNBQK1NR w KQkq - 0 1",
                new Coordinate(5, 1), new Coordinate(5, 0),
                false);
    }

    @Test
    public void makeMoveTestPawnToBackLineBlack() {
        makeMoveTest("rnbqk1nr/pppppppp/8/8/8/8/PPPPPPPP/RNBQK1NR b KQkq - 0 1",
                new Coordinate(5, 6), new Coordinate(5, 7),
                false);
    }

    @Test
    public void makeMoveTestPawnFirstMove() {
        makeMoveTest("rnbqk1nr/pppppppp/8/8/5P2/8/PPPPP1PP/RNBQK1NR w KQkq - 0 1",
                new Coordinate(5, 3), new Coordinate(5, 1),
                true);
    }

    @Test
    public void makeMoveTestUnCapture() {
        makeMoveTest("rnbqk1nr/ppppp1pp/4p3/8/8/6P1/PPPPPP1P/RNBQK1NR b KQkq - 0 1",
                new Coordinate(4, 5), new Coordinate(5, 6),
                true, "b");
    }

    @Test
    public void makeMoveTestUnCapturePawnOnFinalRank() {
        makeMoveTest("rnbqk1nr/ppppp3/4b3/8/8/8/PPPPP3/RNBQK1NR b KQkq - 0 1",
                new Coordinate(6, 7), new Coordinate(5, 5),
                false, "p");
    }

    @Test
    public void makeMoveTestUnCapturePawnOnFirstRank() {
        makeMoveTest("rnbqk2r/ppppp3/4b3/8/8/8/PPPPP3/RNBQKnNR b KQkq - 0 1",
                new Coordinate(5, 0), new Coordinate(6, 2),
                false, "p");

    }

    @Test
    public void makeMoveTestUnCaptureImpossibleNumbers() {
        makeMoveTest("rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1",
                new Coordinate(6, 0), new Coordinate(5, 2),
                false, "q");
    }

    @Test
    public void makeMoveTestCheckOnWrongTurn() {
        makeMoveTest("rnbqkbnr/pppp4/4p3/6Q1/8/8/PPPPP3/RNB1KBNR w KQkq - 0 1",
                new Coordinate(6, 4), new Coordinate(6, 5),
                false, "q");
    }

    @Test
    public void makeMoveTestCheckOnTurnKing() {
        makeMoveTest("k1K5/8/8/8/8/8/7P/6B1 b - - 0 1",
                new Coordinate(0, 7), new Coordinate(1, 6),
                false, "Q");
    }

    @Test
    public void unPromote() {
        makeMoveTest("rnbqk1N1/ppppp3/8/8/8/8/PPPPP3/RNBQK1b1 w Qq - 0 1",
                new Coordinate(6, 7), new Coordinate(6, 6),
                true, "", true);
    }
    @Test
    public void unPromoteBlack() {
        makeMoveTest("rnbqk1N1/ppppp3/8/8/8/8/PPPPP3/RNBQK1b1 b Qq - 0 1",
                new Coordinate(6, 0), new Coordinate(6, 1),
                true, "", true);
    }

    @Test
    public void unPromoteCaptureNoPiece() {
        makeMoveTest("rnbqk1N1/ppppp3/8/8/8/8/PPPPP3/RNBQK1b1 b Qq - 0 1",
                new Coordinate(6, 0), new Coordinate(5, 1),
                false, "", true);
    }

    @Test
    public void unCaptureVerticalPawn() {
        makeMoveTest("rnbqk1N1/ppppp3/8/8/4P3/8/PPPP4/RNBQK1b1 w Qq - 0 1",
                new Coordinate(4, 3), new Coordinate(4, 2),
                false, "r");
    }

    @Test
    public void unPromoteCapturePiece() {
        makeMoveTest("rnbqk1N1/ppppp3/8/8/8/8/PPPPP3/RNBQK1b1 b Qq - 0 1",
                new Coordinate(6, 0), new Coordinate(7, 1),
                true, "r", true);
    }

    @Test
    public void unPassant() {
        makeMoveTest("rnbqk1nr/pppp2pp/4P3/8/8/6P1/PPPP1P1P/RNBQK1NR w - - 0 1",
                new Coordinate(4, 5), new Coordinate(5, 4),
                true, "p", false, true);
    }

    @Test
    public void unPassantIllegal() {
        makeMoveTest("rnbqk1nr/ppppp1pp/4P3/8/8/6P1/PPPP1P1P/RNBQK1NR w - - 0 1",
                new Coordinate(4, 5), new Coordinate(5, 4),
                false, "p", false, true);
    }

    @Test
    public void doubleMove() {
        makeMoveTest("rnbqk1nr/ppppp1pp/8/8/4P3/6P1/PPPP1P1P/RNBQK1NR w - - 0 1",
                new Coordinate(4, 3), new Coordinate(4, 1),
                true, "", false, false);
    }

    @Test
    public void castleViolation() {
        makeMoveTest("r3kr2/1pp2p2/1pn2npP/3pp3/1bQ5/2N2NPP/1PPP1P2/R3K1R1 w q - 0 1",
                new Coordinate(2, 3), new Coordinate(1, 4),
                false, "", false, false);
    }

    @Test
    public void castleViolation2() {
        makeMoveTest("r3kr2/1pp2p2/1pn2npP/1Q1pp3/1b6/2N2NPP/1PPP1P2/R3K1R1 w q - 0 1",
                new Coordinate(5, 2), new Coordinate(7, 1),
                false, "", false, false);
    }

//    @Test
//    public void unPassantBadPosition() {
//        makeMoveTest("rnbqk1nr/pppp2pp/8/4P3/8/6P1/PPPP1P1P/RNBQK1NR w - - 0 1",
//                new Coordinate(4, 4), new Coordinate(5, 3),
//                false, "p", false, true);
//    }

}
