import Heuristics.Deductions.CombinedPawnMap;
import Heuristics.Deductions.PawnMap;
import StandardChess.BoardBuilder;
import StandardChess.ChessBoard;
import StandardChess.Coordinate;
import org.junit.Assert;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class SolverTest {







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

        System.out.println("Testing 3...");
        new Solver().solve(BoardBuilder.buildBoard(board2 + " b"), 1);

        System.out.println("Testing 4...");
        new Solver().solve(BoardBuilder.buildBoard(board2 + " w"), 1);


    }

    @Test
    public void chessMysteries3() {
        // Whose move?
        // What side?
        // En passant
        String board1 = "B7/8/P7/4P3/5B2/4P3/3Q4/5K1k";
        String board2 = "k1K5/4Q3/3P4/2B5/3P4/7P/8/7B";
//        System.out.println("Testing 1...");
//        new Solver().solve(BoardBuilder.buildBoard(board1 + " w"), 1);
//
//        System.out.println("Testing 2...");
//        new Solver().solve(BoardBuilder.buildBoard(board1 + " b"), 1);
//
//        System.out.println("Testing 3...");
//        new Solver().solve(BoardBuilder.buildBoard(board2 + " b"), 1);

        System.out.println("Testing 4...");
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
        SolverImpossibleStateDetector detectorTwo = DetectorUpdater.update(BoardBuilder.buildBoard("rnbqkbnr/pppppppp/8/8/8/7N/PPPPPPPP/RNBQKB1R"), "rnbqkbnr/pppppppp/8/8/8/7N/PPPPPPPP/RNBQKB1R b :Pf2-f4", detector);
        System.out.println(((PawnMap) detectorTwo.getDeductions().get(1)).getPawnOrigins());
        System.out.println(((PawnMap) detectorTwo.getDeductions().get(2)).getPawnOrigins());
        System.out.println(((CombinedPawnMap) detectorTwo.getDeductions().get(3)).getWhitePaths());
        System.out.println(((CombinedPawnMap) detectorTwo.getDeductions().get(3)).getBlackPaths());



        detector = StateDetectorFactory.getDetector("rnbqkbnr/pppppppp/8/8/5P2/7N/PPPPP1PP/RNBQKB1R w KQkq - 0 1");
        detector.testState();
        detectorTwo = DetectorUpdater.update(BoardBuilder.buildBoard("rnbqkbnr/pppppppp/8/8/8/7N/PPPPPPPP/RNBQKB1R"), "rnbqkbnr/pppppppp/8/8/5P2/8/PPPPPNPP/RNBQKB1R b :Rf2-f4", detector);
        System.out.println(((PawnMap) detectorTwo.getDeductions().get(1)).getPawnOrigins());
        System.out.println(((PawnMap) detectorTwo.getDeductions().get(2)).getPawnOrigins());
        System.out.println(((CombinedPawnMap) detectorTwo.getDeductions().get(3)).getWhitePaths());
        System.out.println(((CombinedPawnMap) detectorTwo.getDeductions().get(3)).getBlackPaths());


        System.out.println("-------------------");
        detector = StateDetectorFactory.getDetector("rnbqkb1r/ppppp1pp/4n3/8/5PN1/8/PPPPP1PP/RNBQKB1R b KQkq - 0 1");
        detector.testState();
        detectorTwo = DetectorUpdater.update(BoardBuilder.buildBoard("rnbqkbnr/pppppppp/8/8/8/7N/PPPPPPPP/RNBQKB1R"),"rnbqkb1r/ppppp1pp/8/2n5/5PN1/8/PPPPP1PP/RNBQKB1R w :Rc5-b2", detector);
        System.out.println(((PawnMap) detectorTwo.getDeductions().get(1)).getPawnOrigins());
        System.out.println(((PawnMap) detectorTwo.getDeductions().get(2)).getPawnOrigins());
        System.out.println(((CombinedPawnMap) detectorTwo.getDeductions().get(3)).getWhitePaths());
        System.out.println(((CombinedPawnMap) detectorTwo.getDeductions().get(3)).getBlackPaths());

        System.out.println("-------------------");

        detector = StateDetectorFactory.getDetector("rnbqkb1r/ppp1p1pp/3p4/2n5/5PN1/8/PPPPP1PP/RNBQKB1R b KQkq - 0 1");
        detector.testState();
        detectorTwo = DetectorUpdater.update(BoardBuilder.buildBoard("rnbqkbnr/pppppppp/8/8/8/7N/PPPPPPPP/RNBQKB1R"),"rnbqkb1r/ppppp1pp/8/2n5/5PN1/8/PPPPP1PP/RNBQKB1R w :Pd6-d7", detector);
        System.out.println(((PawnMap) detectorTwo.getDeductions().get(1)).getPawnOrigins());
        System.out.println(((PawnMap) detectorTwo.getDeductions().get(2)).getPawnOrigins());
        System.out.println(((CombinedPawnMap) detectorTwo.getDeductions().get(3)).getWhitePaths());
        System.out.println(((CombinedPawnMap) detectorTwo.getDeductions().get(3)).getBlackPaths());

        System.out.println("-------------------");

        detector = StateDetectorFactory.getDetector("rnbq1bnr/pppppkpp/5N2/8/5P2/8/PPPPP1PP/RNBQKB1R w - - 0 1");
        detector.testState();
        detectorTwo = DetectorUpdater.update(BoardBuilder.buildBoard("rnbqkbnr/pppppppp/8/8/8/7N/PPPPPPPP/RNBQKB1R"), "rnbqkbnr/ppppp1pp/5p2/8/5PN1/8/PPPPP1PP/RNBQKB1R b :Rf6xPg4", detector);
        System.out.println(((PawnMap) detectorTwo.getDeductions().get(1)).getPawnOrigins());
        System.out.println(((PawnMap) detectorTwo.getDeductions().get(2)).getPawnOrigins());
        System.out.println(((CombinedPawnMap) detectorTwo.getDeductions().get(3)).getWhitePaths());
        System.out.println(((CombinedPawnMap) detectorTwo.getDeductions().get(3)).getBlackPaths());

        System.out.println("!-------------------!");

        detector = StateDetectorFactory.getDetector("rnbq1bnr/pppppkpp/5N2/8/5P2/8/PPPPP1PP/RNBQKB1R b KQkq - 0 1");
        detector.testState();
        detectorTwo = DetectorUpdater.update(BoardBuilder.buildBoard("rnbqkbnr/pppppppp/8/8/8/7N/PPPPPPPP/RNBQKB1R"), "rnbqkbnr/ppppp1pp/5p2/8/5PN1/8/PPPPP1PP/RNBQKB1R w :Rf6xPg4", detector);
        System.out.println(((PawnMap) detectorTwo.getDeductions().get(1)).getPawnOrigins());
        System.out.println(((PawnMap) detectorTwo.getDeductions().get(2)).getPawnOrigins());
        System.out.println(((CombinedPawnMap) detectorTwo.getDeductions().get(3)).getWhitePaths());
        System.out.println(((CombinedPawnMap) detectorTwo.getDeductions().get(3)).getBlackPaths());

        System.out.println("!-------------------!");

        detector = StateDetectorFactory.getDetector("rnbq1bnr/pppppkpp/5N2/8/5P2/8/PPPPP1PP/RNBQKB1R b KQkq - 0 1");
        detector.testState();
        detectorTwo = DetectorUpdater.update(BoardBuilder.buildBoard("rnbqkbnr/pppppppp/8/8/8/7N/PPPPPPPP/RNBQKB1R"), "rnbqkbnr/ppppp1pp/5p2/8/5PN1/8/PPPPP1PP/RNBQKB1R w :Rf6xBg4", detector);
        System.out.println(((PawnMap) detectorTwo.getDeductions().get(1)).getPawnOrigins());
        System.out.println(((PawnMap) detectorTwo.getDeductions().get(2)).getPawnOrigins());
        System.out.println(((CombinedPawnMap) detectorTwo.getDeductions().get(3)).getWhitePaths());
        System.out.println(((CombinedPawnMap) detectorTwo.getDeductions().get(3)).getBlackPaths());

        System.out.println("!-------------------!");

        detector = StateDetectorFactory.getDetector("rnbq1bnr/pppppkpp/5N2/8/5P2/8/PPPPP1PP/RNBQKB1R w KQkq - 0 1");
        detector.testState();
        detectorTwo = DetectorUpdater.update(BoardBuilder.buildBoard("rnbqkbnr/pppppppp/8/8/8/7N/PPPPPPPP/RNBQKB1R"), "rnbqkbnr/ppppp1pp/5p2/8/5PN1/8/PPPPP1PP/RNBQKB1R b :Rf6xBg4", detector);
        System.out.println(((PawnMap) detectorTwo.getDeductions().get(1)).getPawnOrigins());
        System.out.println(((PawnMap) detectorTwo.getDeductions().get(2)).getPawnOrigins());
        System.out.println(((CombinedPawnMap) detectorTwo.getDeductions().get(3)).getWhitePaths());
        System.out.println(((CombinedPawnMap) detectorTwo.getDeductions().get(3)).getBlackPaths());


    }


}
