import Heuristics.Deductions.CombinedPawnMap;
import Heuristics.Deductions.PawnMap;
import StandardChess.BoardBuilder;
import StandardChess.ChessBoard;
import StandardChess.Coordinate;
import org.junit.Assert;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class SolverTest {

    public void makeMoveTest(String fen, Coordinate origin, Coordinate target, boolean pass, String piece, boolean promotion) {
        ChessBoard board = BoardBuilder.buildBoard(fen);
        Assertions.assertEquals(pass, new Solver().makeMove(board,
                origin, target, piece, promotion));
        System.out.println(board.getReader().toFEN());
    }

    public void makeMoveTest(String fen, Coordinate origin, Coordinate target, boolean pass) {
        makeMoveTest(fen, origin, target, pass, "", false);
    }

    public void makeMoveTest(String fen, Coordinate origin, Coordinate target, boolean pass, String piece) {
        makeMoveTest(fen, origin, target, pass, piece, false);
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
    public void unPromoteCapturePiece() {
        makeMoveTest("rnbqk1N1/ppppp3/8/8/8/8/PPPPP3/RNBQK1b1 b Qq - 0 1",
                new Coordinate(6, 0), new Coordinate(7, 1),
                true, "r", true);
    }





    @Test
    public void chessMysteries1() {
        // Simple
        new Solver().solve(BoardBuilder.buildBoard("k1K5/8/8/8/8/8/7P/6B1 b - - 0 1"), 2);
    }

    @Test
    public void chessMysteries2() {
        // Part 1 - Whose move?
        // Part 2 - Which Direction?
        String board1 = "k1K5/4Q3/8/2B1P3/3P4/7P/8/7B";
        String board2 = "7B/8/7P/3P4/2B1P3/8/4Q3/k1K5";
        System.out.println("Testing 1...");
        new Solver().solve(BoardBuilder.buildBoard(board1 + " w"), 1);

        System.out.println("Testing 2...");
        new Solver().solve(BoardBuilder.buildBoard(board1 + " b"), 1);

        System.out.println("Testing 4...");
        new Solver().solve(BoardBuilder.buildBoard(board2 + " b"), 1);

        System.out.println("Testing 3...");
        new Solver().solve(BoardBuilder.buildBoard(board2 + " w"), 1);


    }


    @Test
    public void test() {
        // Simple
        new Solver().solve(BoardBuilder.buildBoard("4Q3/8/8/8/8/8/1rk5/K7 w - - 0 1"), 1);
    }

    @Test
    public void test2() {
        // Simple
        SolverImpossibleStateDetector detector = StateDetectorFactory.getDetector("rnbqkbnr/pppppppp/8/8/5P2/7N/PPPPP1PP/RNBQKB1R w KQkq - 0 1");
        detector.testState();
        SolverImpossibleStateDetector detectorTwo = DetectorUpdater.update("rnbqkbnr/pppppppp/8/8/8/7N/PPPPPPPP/RNBQKB1R b :Pf2-f4", detector);
        System.out.println(((PawnMap) detectorTwo.getDeductions().get(1)).getPawnOrigins());
        System.out.println(((PawnMap) detectorTwo.getDeductions().get(2)).getPawnOrigins());
        System.out.println(((CombinedPawnMap) detectorTwo.getDeductions().get(3)).getWhitePaths());
        System.out.println(((CombinedPawnMap) detectorTwo.getDeductions().get(3)).getBlackPaths());



        detector = StateDetectorFactory.getDetector("rnbqkbnr/pppppppp/8/8/5P2/7N/PPPPP1PP/RNBQKB1R w KQkq - 0 1");
        detector.testState();
        detectorTwo = DetectorUpdater.update("rnbqkbnr/pppppppp/8/8/5P2/8/PPPPPNPP/RNBQKB1R b :Rf2-f4", detector);
        System.out.println(((PawnMap) detectorTwo.getDeductions().get(1)).getPawnOrigins());
        System.out.println(((PawnMap) detectorTwo.getDeductions().get(2)).getPawnOrigins());
        System.out.println(((CombinedPawnMap) detectorTwo.getDeductions().get(3)).getWhitePaths());
        System.out.println(((CombinedPawnMap) detectorTwo.getDeductions().get(3)).getBlackPaths());


    }


}
