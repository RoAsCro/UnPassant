import Heuristics.Deductions.CombinedPawnMap;
import Heuristics.Deductions.PawnMap;
import StandardChess.BoardBuilder;
import StandardChess.ChessBoard;
import StandardChess.Coordinate;
import org.junit.Assert;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.List;

public class SolverTest {


    @Test
    public void chessMysteries1() {
        // Simple
        Solver solver = new Solver();
        solver.setAdditionalDepth(2);
        solver.setNumberOfSolutions(100);
        Assertions.assertEquals(4, solver.solve(BoardBuilder.buildBoard("k1K5/8/8/8/8/8/7P/6B1 b - - 0 1"), 2).size());
        // This is solved entirely through iterating through possible scenarios
    }

    @Test
    public void chessMysteries2() {
        // Part 1 - Whose move?
        // Part 2 - Which Direction?
        String board1 = "k1K5/4Q3/8/2B1P3/3P4/7P/8/7B";
        String board2 = "7B/8/7P/3P4/2B1P3/8/4Q3/k1K5";
        System.out.println("Testing 1...");
        Assertions.assertEquals(0, new Solver().solve(BoardBuilder.buildBoard(board1 + " w"), 1).size());

        System.out.println("Testing 2...");
        Assertions.assertEquals(0, new Solver().solve(BoardBuilder.buildBoard(board1 + " b"), 1).size());

        System.out.println("Testing 3...");
        Assertions.assertEquals(0, new Solver().solve(BoardBuilder.buildBoard(board2 + " b"), 1).size());

        System.out.println("Testing 4...");
        Solver solver = new Solver();
        solver.setAdditionalDepth(2);
        solver.setNumberOfSolutions(100);
        Assertions.assertEquals(3, solver.solve(BoardBuilder.buildBoard(board2 + " w"), 1).size());
//        new Solver().solve(BoardBuilder.buildBoard(board2 + " w"), 1);

        // Only one scenario, after eliminating impossible board states, has valid moves


    }

    @Test
    public void chessMysteries3() {
        // Whose move?
        // What side?
        // En passant
        String board1 = "B7/8/P7/4P3/5B2/4P3/3Q4/5K1k";
        String board2 = "k1K5/4Q3/3P4/2B5/3P4/7P/8/7B";
        System.out.println("Testing 1...");
        Assertions.assertNotEquals(0, new Solver().solve(BoardBuilder.buildBoard(board1 + " w"), 1).size());

        System.out.println("Testing 2...");
        Assertions.assertEquals(0,new Solver().solve(BoardBuilder.buildBoard(board1 + " b"), 1).size());

        System.out.println("Testing 3...");
        Assertions.assertEquals(0,new Solver().solve(BoardBuilder.buildBoard(board2 + " b"), 1).size());

        System.out.println("Testing 4...");
        // Set no. of solutions to 1
        Solver solver = new Solver();
        solver.setAdditionalDepth(2);
        solver.setNumberOfSolutions(1);
        Assertions.assertNotEquals(0, solver.solve(BoardBuilder.buildBoard(board2 + " w"), 6).size());

        // As above for Test 1
        // Test 4 solving up to a depth of 6 yields:
        // Nb6xQa8, Ka7xNa8, Pe4-e5, Pd7-d5, Pe5xPd6
        // This is slightly different from Smullyan's solution, but is equally valid

    }

    @Test
    public void chessMysteries4() {
        // pp30
        List<String> list = List.of(
                "2nR3K/pk1Rp1p1/p2p4/P1p5/1Pp4p/2PP2P1/4P2P/n7 w - - 0 1",
                "2nR3K/pk1Rp1p1/p2p4/P1p5/1Pp4P/2PP2P1/4P2P/n7 w - - 0 1",
                "2nR3K/pk1Rp1p1/p2p4/P1p5/1Pp4r/2PP2P1/4P2P/n7 w - - 0 1",
                "2nR3K/pk1Rp1p1/p2p4/P1p5/1Pp4R/2PP2P1/4P2P/n7 w - - 0 1",
                "2nR3K/pk1Rp1p1/p2p4/P1p5/1Pp4n/2PP2P1/4P2P/n7 w - - 0 1",
                "2nR3K/pk1Rp1p1/p2p4/P1p5/1Pp4N/2PP2P1/4P2P/n7 w - - 0 1",
                "2nR3K/pk1Rp1p1/p2p4/P1p5/1Pp4b/2PP2P1/4P2P/n7 w - - 0 1",
                "2nR3K/pk1Rp1p1/p2p4/P1p5/1Pp4B/2PP2P1/4P2P/n7 w - - 0 1",
                "2nR3K/pk1Rp1p1/p2p4/P1p5/1Pp4q/2PP2P1/4P2P/n7 w - - 0 1",
                "2nR3K/pk1Rp1p1/p2p4/P1p5/1Pp4Q/2PP2P1/4P2P/n7 w - - 0 1",
                "2nR3K/pk1Rp1p1/p2p4/P1p5/1Pp4p/2PP2P1/4P2P/n7 b - - 0 1",
                "2nR3K/pk1Rp1p1/p2p4/P1p5/1Pp4P/2PP2P1/4P2P/n7 b - - 0 1",
                "2nR3K/pk1Rp1p1/p2p4/P1p5/1Pp4r/2PP2P1/4P2P/n7 b - - 0 1",
                "2nR3K/pk1Rp1p1/p2p4/P1p5/1Pp4R/2PP2P1/4P2P/n7 b - - 0 1",
                "2nR3K/pk1Rp1p1/p2p4/P1p5/1Pp4n/2PP2P1/4P2P/n7 b - - 0 1",
                "2nR3K/pk1Rp1p1/p2p4/P1p5/1Pp4N/2PP2P1/4P2P/n7 b - - 0 1",
                "2nR3K/pk1Rp1p1/p2p4/P1p5/1Pp4b/2PP2P1/4P2P/n7 b - - 0 1",
                "2nR3K/pk1Rp1p1/p2p4/P1p5/1Pp4B/2PP2P1/4P2P/n7 b - - 0 1",
                "2nR3K/pk1Rp1p1/p2p4/P1p5/1Pp4q/2PP2P1/4P2P/n7 b - - 0 1",
                "2nR3K/pk1Rp1p1/p2p4/P1p5/1Pp4Q/2PP2P1/4P2P/n7 b - - 0 1"
        );
        for (String s : list) {
            if (StateDetectorFactory.getDetector(s).testState()) {
                ChessBoard b = BoardBuilder.buildBoard(s);
                b.setTurn(b.getTurn().equals("white") ? "black" : "white");
                // set max states to 1?
                Solver solver = new Solver();
                solver.setAdditionalDepth(2);
                solver.setNumberOfSolutions(1);
                if (!s.equals("2nR3K/pk1Rp1p1/p2p4/P1p5/1Pp4B/2PP2P1/4P2P/n7 b - - 0 1")) {
                    Assertions.assertEquals(0, solver.solve(b, 2).size(), (s));
                } else {
                    Assertions.assertNotEquals(0, solver.solve(b, 2).size());
                }

                System.out.println("Finished");
                System.out.println(s);
            }
        }
        // The initial check eliminates all states where white is in check on black's turn
        // It then iterates through the last few moves for each remaining state
        // This eliminates all states that result in black being checked - everything except a promotion on d8
        // The scenario in which the missing piece is a pawn is therefore eliminated
        // The next ply will eliminate the piece captured on d8 being a queen or rook as they will be unable to move
        // in such a way that white will not be in check on black's turn
        // Due to the fact that the black square black bishop is caged and two knights already exist,
        // the captured piece on d8 therefore must be promoted piece.
        // With all pawns now accounted for, the missing piece being a black bishop or knight are eliminated as possibilities
        // The CaptureLocations Deduction will see that no definitive black capture took place on a black square
        // In the case of every scenario except white bishop,
        // it will therefore reduce the number of captures black pawns can make by one, since the black square
        // white bishop is missing
        // When the PromotionMap is told that black has one certain promoted piece, and that there is exactly one
        // pawn whose start location is unnaccounted for, it creates a path from that start location to the 1st rank
        // To not clash with the white pawn paths, it must make a capture
        // In all remaining scenarios except the missing piece being a white bishop, this will exceed the number of
        // possible captures
        // Retractor cannot solve this puzzle

    }

    @Test
    public void chessMysteries5() {

        //pp 39
        List<String> list = List.of(
                "r3k3/ppp3pp/6p1/P2Kp2P/1NB5/p5P1/PP3PP1/R7 w - - 0 1"
        );
        for (String s : list) {
            if (StateDetectorFactory.getDetector(s).testState()) {
                System.out.println(s);
                ChessBoard b = BoardBuilder.buildBoard(s);
                b.setTurn(b.getTurn().equals("white") ? "black" : "white");
                // set max states to 100?
                Solver solver = new Solver(string ->{
                    String[] g = string.split(" ");
                    String t = string.split(":")[1];
                    char p = t.charAt(0);
                    // This rook has not moved, the king is not part of the move
                    return string.charAt(0) == 'r'
                            && !(g[1].charAt(0) == 'w' && p == 'K')
                            && !t.startsWith("Pf7")
//                            && !t.startsWith("Pb4")
                            ;
                });
                solver.setNumberOfSolutions(1);
                solver.setAdditionalDepth(2);
                solver.solve(b, 2);
                System.out.println("Finished 1");

                solver = new Solver(
//                        string ->{
//                    String[] g = string.split(" ");
//                    String t = string.split(":")[1];
//                    char p = t.charAt(0);
//                    // This rook has not moved, the king is not part of the move
//                    return (g[1].charAt(0) == 'w' && p == 'k');
//                }
                );
                solver.setNumberOfSolutions(1);
                solver.setAdditionalDepth(2);
                b = BoardBuilder.buildBoard(s);
                b.setTurn(b.getTurn().equals("white") ? "black" : "white");
                solver.solve(b, 2);
                System.out.println("Finished 2");
            }
        }
        // This fails because pawn capture position is not accounted for
        // This is because doing so would require that it be first established whether or not any given missing pawn promoted
        // Then there is two options - either the pawn promoted or was taken on a particular square
        // The program as it is cannot account for both being pootentially true
    }

    @Test
    public void ChessMysteries6() {
        // pp45
        Solver solver = new Solver(p -> {
            SolverImpossibleStateDetector detector = StateDetectorFactory.getDetector(p.split(":")[0]);
            detector.testState();
            return detector.canCastle(false);
        });
        solver.setNumberOfSolutions(1);
        solver.setNumberOfSolutions(2);
        solver.setAdditionalDepth(2);
        Assertions.assertNotEquals(0, solver.solve(BoardBuilder.buildBoard("r1b1k2r/p1p1p1p1/1p3p1p/8/8/P7/1PPPPPPP/2BQKB2 w kq - 0 1"), 2).size());

        solver = new Solver(p -> {
            if (p.split(":")[1].endsWith("h8")) {
                return false;
            }
            SolverImpossibleStateDetector detector = StateDetectorFactory.getDetector(p.split(":")[0]);
            detector.testState();
            return detector.canCastle(false);
        });
        solver.setNumberOfSolutions(1);
        solver.setNumberOfSolutions(2);
        solver.setAdditionalDepth(2);
        Assertions.assertEquals(0, solver.solve(BoardBuilder.buildBoard("r1b1k2r/p1p1p1p1/1p3p1p/8/8/P7/1PPPPPPP/2BQKB2 w kq - 0 1"), 3).size());

        // This is entirely a matter of iterating through valid moves
    }

    @Test
    public void ChessMysteries7() {
        // pp45
        Solver solver = new Solver(p -> {
            SolverImpossibleStateDetector detector = StateDetectorFactory.getDetector(p.split(":")[0]);
            detector.testState();
            return detector.canCastle(false);
        });
        solver.setNumberOfSolutions(1);
        solver.setNumberOfSolutions(2);
        solver.setAdditionalDepth(2);
        Assertions.assertNotEquals(0, solver.solve(BoardBuilder.buildBoard("r1b1k2r/p1p1p1p1/1p3p1p/8/8/P7/1PPPPPPP/2BQKB2 w kq - 0 1"), 2).size());

        solver = new Solver(p -> {
            if (p.split(":")[1].endsWith("h8")) {
                return false;
            }
            SolverImpossibleStateDetector detector = StateDetectorFactory.getDetector(p.split(":")[0]);
            detector.testState();
            return detector.canCastle(false);
        });
        solver.setNumberOfSolutions(1);
        solver.setNumberOfSolutions(2);
        solver.setAdditionalDepth(2);
        Assertions.assertEquals(0, solver.solve(BoardBuilder.buildBoard("r1b1k2r/p1p1p1p1/1p3p1p/8/8/P7/1PPPPPPP/2BQKB2 w kq - 0 1"), 3).size());

        // This is entirely a matter of iterating through valid moves
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
