package ChessHeuristics;

import ChessHeuristics.Heuristics.Detector.DetectorInterface;
import ChessHeuristics.Heuristics.Detector.StateDetectorFactory;
import ChessHeuristics.Heuristics.HeuristicsUtil;
import ChessHeuristics.SolverAlgorithm.Solver;
import ChessHeuristics.SolverAlgorithm.StateConditions;
import ChessHeuristics.SolverAlgorithm.UnMoveConditions.UnMoveCondition;
import ReverseChess.StandardChess.BoardBuilder;
import ReverseChess.StandardChess.ChessBoard;
import ReverseChess.StandardChess.Coordinate;
import ReverseChess.StandardChess.StandardPieceFactory;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Objects;

public class SolverTest {


    @Test
    public void chessMysteries3() {
        //Page 16
        Solver solver = new Solver();
        solver.setAdditionalDepth(0);
        solver.setNumberOfSolutions(100);
        long start = System.currentTimeMillis();
        Assertions.assertEquals(4, solver.solve("k1K5/8/8/8/8/8/7P/6B1 b - - 0 1", 2).size());
        System.out.println(System.currentTimeMillis() - start);
        // This is solved entirely through iterating through possible scenarios
    }

    @Test
    public void chessMysteries1() {
        //Page 4
        String board1 = "k1K5/4Q3/8/2B1P3/3P4/7P/8/7B";
        String board2 = "7B/8/7P/3P4/2B1P3/8/4Q3/k1K5";
        System.out.println("Testing 1...");
        Solver solver = new Solver();
        solver.setAdditionalDepth(1);
        solver.setNumberOfSolutions(100);
        long start = System.currentTimeMillis();
        Assertions.assertEquals(0,solver.solve(board1 + " w", 1).size());
        System.out.println(System.currentTimeMillis() - start);

        System.out.println("Testing 2...");
        solver = new Solver();
        solver.setAdditionalDepth(1);
        solver.setNumberOfSolutions(100);
        Assertions.assertEquals(0, solver.solve(board1 + " b", 1).size());

        System.out.println("Testing 3...");
        solver = new Solver();
        solver.setAdditionalDepth(1);
        solver.setNumberOfSolutions(100);
        Assertions.assertEquals(0, solver.solve(board2 + " b", 1).size());

        System.out.println("Testing 4...");
        solver = new Solver();
        solver.setAdditionalDepth(1);
        solver.setNumberOfSolutions(100);
        start = System.currentTimeMillis();
        Assertions.assertEquals(3, solver.solve(board2 + " w", 1).size());
        System.out.println(System.currentTimeMillis() - start);
        // Only one scenario, after eliminating impossible board states, has valid moves

    }

    @Test
    public void chessMysteries2() {
        // Page 13
        String board1 = "B7/8/P7/4P3/5B2/4P3/3Q4/5K1k";
        String board2 = "k1K5/4Q3/3P4/2B5/3P4/7P/8/7B";
        System.out.println("Testing 1...");
        Solver solver = new Solver();
        solver.setAdditionalDepth(0);
        solver.setNumberOfSolutions(100);
        long start = System.currentTimeMillis();
        Assertions.assertNotEquals(0, solver.solve(board1 + " w", 1).size());
        System.out.println(System.currentTimeMillis() - start);

        solver = new Solver();
        solver.setAdditionalDepth(1);
        solver.setNumberOfSolutions(1);
        System.out.println("Testing 2...");
        start = System.currentTimeMillis();
        Assertions.assertEquals(0, solver.solve(board1 + " b", 1).size());
        System.out.println(System.currentTimeMillis() - start);

        solver = new Solver();
        solver.setAdditionalDepth(1);
        solver.setNumberOfSolutions(1);
        System.out.println("Testing 3...");
        start = System.currentTimeMillis();
        Assertions.assertEquals(0, solver.solve(board2 + " b", 1).size());
        System.out.println(System.currentTimeMillis() - start);

        System.out.println("Testing 4...");
        // Set no. of solutions to 1
        solver = new Solver();
        solver.setAdditionalDepth(1);
        solver.setNumberOfSolutions(2);
        start = System.currentTimeMillis();
        Assertions.assertNotEquals(0, solver.solve(board2 + " w", 1).size());
        System.out.println(System.currentTimeMillis() - start);
    }

    @Test
    public void chessMysteries4() {
        // pp30
        List<String> list = List.of(
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
                Solver solver = new Solver();
                solver.setAdditionalDepth(2);
                solver.setNumberOfSolutions(1);
                if (!s.equals("2nR3K/pk1Rp1p1/p2p4/P1p5/1Pp4B/2PP2P1/4P2P/n7 b - - 0 1")) {
                    long start = System.currentTimeMillis();
                    Assertions.assertEquals(0, solver.solve(b.getReader().toFEN(), 2).size(), (s));
                    System.out.println((float) (System.currentTimeMillis() - start) / 1000);

                } else {
                    long start = System.currentTimeMillis();
                    Assertions.assertNotEquals(0, solver.solve(b.getReader().toFEN(), 2).size());
                    System.out.println((float) (System.currentTimeMillis() - start) / 1000);

                }

                System.out.println("Finished");
                System.out.println(s);
            } else {
                System.out.println("Not initially valid - " + s);
                Assertions.assertNotEquals("2nR3K/pk1Rp1p1/p2p4/P1p5/1Pp4B/2PP2P1/4P2P/n7 b - - 0 1", s);
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
        // With all pawns now accounted for, the missing piece being a black bishop or knight are eliminated as
        // possibilities
        // The CaptureLocations Deduction will see that no definitive black capture took place on a black square
        // In the case of every scenario except white bishop,
        // it will therefore reduce the number of captures black pawns can make by one, since the black square
        // white bishop is missing
        // When the PromotionMap is told that black has one certain promoted piece, and that there is exactly one
        // pawn whose start location is unaccounted for, it creates a path from that start location to the 1st rank
        // To not clash with the white pawn paths, it must make a capture
        // In all remaining scenarios except the missing piece being a white bishop, this will exceed the number of
        // possible captures

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
                Solver solver = new Solver(string -> true,
                        detectorInterface -> detectorInterface.canCastle(false, true)
                                || detectorInterface.canCastle(false, false)
                );
                solver.setNumberOfSolutions(1);
                solver.setAdditionalDepth(2);
                long start = System.currentTimeMillis();
                Assertions.assertEquals(0, solver.solve(b.getReader().toFEN(), 2).size());
                System.out.println((float) (System.currentTimeMillis() - start) / 1000);

                System.out.println("Finished 1");

                solver = new Solver();
                solver.setNumberOfSolutions(1);
                solver.setAdditionalDepth(2);
                b = BoardBuilder.buildBoard(s);
                b.setTurn(b.getTurn().equals("white") ? "black" : "white");
                start = System.currentTimeMillis();
                Assertions.assertNotEquals(0, solver.solve(b.getReader().toFEN(), 2).size());
                System.out.println((float) (System.currentTimeMillis() - start) / 1000);

                System.out.println("Finished 2");

                solver = new Solver(
                        new UnMoveCondition(0, 1, '-', "-", "-",
                                'N', '-', "-", true),
                        detectorInterface -> detectorInterface.canCastle(false, true)
                                || detectorInterface.canCastle(false, false));
                solver.setNumberOfSolutions(1);
                solver.setAdditionalDepth(2);
                b = BoardBuilder.buildBoard("r3k3/ppp3pp/6p1/P2Kp2P/1NB5/p5P1/PP3PP1/R7 b - - 0 1");
                b.setTurn(b.getTurn().equals("white") ? "black" : "white");
                start = System.currentTimeMillis();
                Assertions.assertNotEquals(0, solver.solve(b.getReader().toFEN(), 3).size());
                System.out.println((float) (System.currentTimeMillis() - start) / 1000);

                System.out.println("Finished 3");

            }
        }
        // The original puzzle is actually created incorrectly - with the knight moved as the last white move,
        // the pawn can move more freely than the apparent solution allows for
        // In the above, the first tests an amended version of the puzzle, the knight having not moved, with an extra
        // check that black can castle
        // The second does the same without the check for castling, therefore a line is found
        // The third tries the puzzle under the original parameters, with the castling check and therefore
        // finds alternative lines

        // The pawn map constructs maps for both pawns
        // The black move cannot be K or R - this means black cannot castle
        // The black move cannot be g6 - The PieceMap will note this will result in the king moving
        // It cannot be e5 - white will be in check on black's turn
        // a4-a3 will be moved
        // The pawn map is rebuilt
        // All of black's captures were made by pawns
        // 4 / 5 of white's captures were made by pawns
        // CaptureLocations see pawn captures took place on white, therefore the black bishop cannot have
        // been taken by white
        // The PawnMaps are rebuilt on the basis that there is one fewer pawn capture
        // CaptureLocations sees that one of white's missing pieces is a pawn:
        // That pawn cannot move off it's file
        // None of the pawns that have taken pieces can have taken on that file
        // Therefore in order for it to have been taken, it must have promoted
        // PromotionSquares comes up with a path from d2 rank 8
        // UnCastle sees that this will pass d7, and therefore the king must have moved
    }

    @Test
    public void ChessMysteries6() {
        // pp45
        Solver solver = new Solver();
        solver.setNumberOfSolutions(2);
        solver.setAdditionalDepth(1);
        long start = System.currentTimeMillis();
        Assertions.assertNotEquals(0,
                solver.solve("r1b1k2r/p1p1p1p1/1p3p1p/8/8/P7/1PPPPPPP/2BQKB2 w - - 0 1", 2).size());
        System.out.println((float) (System.currentTimeMillis() - start) / 1000);

        solver = new Solver();
        solver.setNumberOfSolutions(2);
        solver.setAdditionalDepth(1);
        start = System.currentTimeMillis();
        Assertions.assertEquals(0,
                solver.solve("r1b1k2r/p1p1p1p1/1p3p1p/8/8/P7/1PPPPPPP/2BQKB2 w k - 0 1", 2).size());
        System.out.println((float) (System.currentTimeMillis() - start) / 1000);

    }

    @Test
    public void ChessMysteries7() {
        // pp46
        Solver solver = new Solver( new UnMoveCondition(0, 2, '-', "-", "-", '-',
                '-', "x", true)
        );
        solver.setNumberOfSolutions(2);
        solver.setAdditionalDepth(0);
        long start = System.currentTimeMillis();
        List<String> solutions = solver.solve("4k2r/8/8/8/2p5/5P2/2P2PP1/3b1RK1 w k - 0 1", 2);
        System.out.println((float) (System.currentTimeMillis() - start) / 1000);
        System.out.println(solutions);
        Assertions.assertTrue((solutions.get(0).contains("4k2r/8/8/2p5/8/5P2/2P2PP1/3bK2R")
                || solutions.get(0).contains("4k2r/8/8/8/2p5/5P2/2P1bPP1/4K2R"))
        && (solutions.get(1).contains("4k2r/8/8/2p5/8/5P2/2P2PP1/3bK2R")
                || solutions.get(1).contains("4k2r/8/8/8/2p5/5P2/2P1bPP1/4K2R"))) ;

        solver = new Solver(
                new UnMoveCondition(0, 2, '-', "-", "-", '-',
                        '-', "x", true)
        );
        solver.setNumberOfSolutions(1);
        solver.setAdditionalDepth(0);
        start = System.currentTimeMillis();
        Assertions.assertEquals(0,
                solver.solve("4k2r/8/8/8/2p5/5P2/2P2PP1/3b1RK1 w k - 0 1", 3).size());
        System.out.println((float) (System.currentTimeMillis() - start) / 1000);

        // The first here has 2 depth, checking that it comes to one of two valid solutions to the
        // first part of the problem, the only moves available with the given castling rights and the capture
        // restrictions
        // The second part checks with one more depth - the detector uses the information gathered by the piece and
        // pawn
        // maps to conclude that the bishop A. must have promoted, B. Must have promoted on that square, C. must have
        // passed
        // a critical king square to get there - this would violate the castling constraints that happen as a result
        // of the iteration

    }



   @Test
    public void ChessMysteries8() {
        // pp61
        List<String> list = List.of(
                    "2b5/pp1p4/PRP5/pR3N2/2K5/2P5/2k1PNP1/1nrnB3 b - - 0 1",
                "2b5/pp1p4/PRP5/pR3N2/2K5/2P5/2k1PN1P/1nrnB3 b - - 0 1"
        );

        int count = 0;
        for (String st : list) {
            count++;
            Solver solver = new Solver(s -> !(s.split(":")[1].charAt(0) == 'P'
                    && (s.split(":")[1].endsWith("1")  || s.split(":")[1].endsWith("8"))),
                    new StateConditions.NoPromotions()
            );

            solver.setNumberOfSolutions(1);
            solver.setAdditionalDepth(1);
            if (count == 1) {
                long start = System.currentTimeMillis();
                Assertions.assertEquals(0, solver.solve(st, 2).size());
                System.out.println((float) (System.currentTimeMillis() - start) / 1000);

            } else {
                long start = System.currentTimeMillis();
                Assertions.assertNotEquals(0, solver.solve(st, 2).size());
                System.out.println((float) (System.currentTimeMillis() - start) / 1000);

            }
        }
        // The above Tests the two possibilities, finding one has no solutions
    }

    @Test
    public void ChessMysteries9() {
        // pp64
        List<String> list = List.of(
                "2b5/pp1p4/PR1P4/pR3N2/2K5/2P5/2k1PNP1/1nrnB3 b - - 0 1");

        for (String st : list) {
            Solver solver = new Solver(
                    s -> !(s.split(":")[1].charAt(0) == 'P'
                            && (s.split(":")[1].endsWith("1")
                            || s.split(":")[1].endsWith("8"))),
                    new StateConditions.PromotionLimit(0, 0, List.of("rook"))
                            .and(new StateConditions.PromotionLimit(0, 0, List.of("knight")))
                            .and(new StateConditions.PromotionLimit(0, 0, List.of("bishop")))
            );
            solver.setNumberOfSolutions(3);
            solver.setAdditionalDepth(0);
//            if (count == 1) {
            long start = System.currentTimeMillis();
            List<String> solutions = solver.solve(st, 2);
            System.out.println((float) (System.currentTimeMillis() - start) / 1000);
            System.out.println(solutions);
            Assertions.assertNotEquals(0, solutions.size());

        }
        // With it set to 3 solutions, the set of two un-moves it finds must be the only solution

    }

    @Test
    public void ChessMysteries10() {
        // pp73
        List<String> list = List.of(
                "8/8/8/8/NN1Q4/1k1B4/8/R1B1K2R w - - 0 1",
                "8/8/8/8/NN1Q4/1k1B4/8/R1B1K2R b - - 0 1",
                "R2K1B1R/8/4B1k1/4Q1NN/8/8/8/8 b - - 0 1");

        int count = 0;
        for (String st : list) {
            count++;
            Solver solver = new Solver();
            solver.setNumberOfSolutions(1);
            solver.setAdditionalDepth(1);
            long start = System.currentTimeMillis();

            List<String> solutions = solver.solve(st, 2);
            System.out.println((float) (System.currentTimeMillis() - start) / 1000);

            if (count == 1) {
                Assertions.assertNotEquals(0, solutions.size());
            } else if (count == 2) {
                Assertions.assertEquals(0, solutions.size());
            } else if (count == 3) {
                Assertions.assertNotEquals(0, solutions.size());
            }
        }
    }

    @Test
    public void ChessMysteries11() {
        // pp74
        List<String> list = List.of(
                "QN6/7R/1k6/1N6/1K6/8/8/8 b - - 0 1");

        for (String st : list) {
            Solver solver = new Solver();
            solver.setNumberOfSolutions(3);
            solver.setAdditionalDepth(1);
            long start = System.currentTimeMillis();
            List<String> solutions = solver.solve(st, 2);
            System.out.println((float) (System.currentTimeMillis() - start) / 1000);
            Assertions.assertNotEquals(0, solutions.size());
        }

    }

    @Test
    public void ChessMysteries12() {
        // pp78
        List<String> list = List.of(
                "r3k3/8/8/8/8/8/5PP1/6bK w q - 0 1");

        for (String st : list) {
            Solver solver = new Solver();
            solver.setNumberOfSolutions(1);
            solver.setAdditionalDepth(1);
            long start = System.currentTimeMillis();
            List<String> solutions = solver.solve(st, 2);
            System.out.println((float) (System.currentTimeMillis() - start) / 1000);
            Assertions.assertEquals(0, solutions.size());
        }

    }

    @Test
    public void ChessMysteries13() {
        // pp90
        List<String> list = List.of(
                "r3k2r/pbpp1ppp/2n4n/4p1q1/1b6/8/1PPPPPPP/1NBQKBNR b Kkq - 0 1"
        );

        for (String st : list) {
            Solver solver = new Solver(
                    s -> {
                String move = s.split(":")[1];
                if (move.contains("x") && !(s.split(":")[2].equals("0") || s.split(" ")[1].contains("b"))) {
                    return false;
                }
                if (move.charAt(1) == 'R' || move.charAt(1) == 'K') {
                    return false;
                }
                    return move.charAt(4) != 'R' && !(s.split(" ")[1].contains("b") && move.charAt(0) == 'N') ;
            }
            , new StateConditions.NoPromotions()
            );
            solver.setNumberOfSolutions(2);
            solver.setAdditionalDepth(0);
            List<String> solutions = solver.solve(st, 11);
        }
        // Fails, requires a proof game
    }

    @Test
    public void ChessMysteries14() {
        // pp103
        List<String> list = List.of(
                "r3k3/P5P1/1P1P1P2/3PpK2/8/8/6B1/8 b q - 0 1"
        );

        for (String st : list) {
            Solver solver = new Solver();
            solver.setNumberOfSolutions(2);
            solver.setAdditionalDepth(2);
            long start = System.currentTimeMillis();
            List<String> solutions = solver.solve(st, 1);
            System.out.println((float) (System.currentTimeMillis() - start) / 1000);
            Assertions.assertEquals(1, solutions.size());
            Assertions.assertTrue(solutions.get(0).contains("Pe7-e5"));
            solver = new Solver(
                    new UnMoveCondition(0, 1, '-', "-", "f4", 'P', '-',
                            "any", false)); //Do not allow double pawn move
            solver.setNumberOfSolutions(1);
            solver.setAdditionalDepth(1);
            start = System.currentTimeMillis();
            solutions = solver.solve(st, 2);
            System.out.println((float) (System.currentTimeMillis() - start) / 1000);
            Assertions.assertEquals(0, solutions.size());

        }
        // This successfully uncovers that the only valid move while maintaining castling rights is a double pawn move
    }

    @Test
    public void ChessMysteries15() {
        // pp113
        List<String> list = List.of(
                "RbB5/n1p2pp1/2B1p3/PpKp4/kP2P3/p2p3P/P2P3P/8 b - - 0 1"
        );

        for (String st : list) {

            Solver solver = new Solver();
            solver.setNumberOfSolutions(5);
            solver.setAdditionalDepth(1);
            long start = System.currentTimeMillis();
            List<String> solutions = solver.solve(st, 1);
            System.out.println((float) (System.currentTimeMillis() - start) / 1000);
            Assertions.assertEquals(1, solutions.size());
            Assertions.assertTrue(solutions.get(0).contains("Pb7-b5"));

        }
        // This successfully uncovers that the only valid move while maintaining castling rights is a double pawn move
        // The first part tests it while disallowing the only valid move

    }

    @Test
    public void ChessMysteries16() {
        // pp115
        List<String> list = List.of(
                "1K6/p1p1p1p1/p1kP2p1/8/P1N3N1/1P1bP1PP/1Q1Pq2P/r3n2B w - - 0 1",
                "2K5/p1p1p1p1/p1kP2p1/8/P1N3N1/1P1bP1PP/1Q1Pq2P/r3n2B w - - 0 1",
                "3K4/p1p1p1p1/p1kP2p1/8/P1N3N1/1P1bP1PP/1Q1Pq2P/r3n2B w - - 0 1",
                "4K3/p1p1p1p1/p1kP2p1/8/P1N3N1/1P1bP1PP/1Q1Pq2P/r3n2B w - - 0 1",
                "5K2/p1p1p1p1/p1kP2p1/8/P1N3N1/1P1bP1PP/1Q1Pq2P/r3n2B w - - 0 1",
                "6K1/p1p1p1p1/p1kP2p1/8/P1N3N1/1P1bP1PP/1Q1Pq2P/r3n2B w - - 0 1",
                "7K/p1p1p1p1/p1kP2p1/8/P1N3N1/1P1bP1PP/1Q1Pq2P/r3n2B w - - 0 1",
                "8/p1p1pKp1/p1kP2p1/8/P1N3N1/1P1bP1PP/1Q1Pq2P/r3n2B w - - 0 1",
                "8/p1p1p1pK/p1kP2p1/8/P1N3N1/1P1bP1PP/1Q1Pq2P/r3n2B w - - 0 1",
                "8/p1p1p1p1/p1kPK1p1/8/P1N3N1/1P1bP1PP/1Q1Pq2P/r3n2B w - - 0 1",
                "8/p1p1p1p1/p1kP2p1/K7/P1N3N1/1P1bP1PP/1Q1Pq2P/r3n2B w - - 0 1",
                "8/p1p1p1p1/p1kP2p1/4K3/P1N3N1/1P1bP1PP/1Q1Pq2P/r3n2B w - - 0 1",
                "8/p1p1p1p1/p1kP2p1/6K1/P1N3N1/1P1bP1PP/1Q1Pq2P/r3n2B w - - 0 1",
                "8/p1p1p1p1/p1kP2p1/8/PKN3N1/1P1bP1PP/1Q1Pq2P/r3n2B w - - 0 1",
                "8/p1p1p1p1/p1kP2p1/8/P1NK2N1/1P1bP1PP/1Q1Pq2P/r3n2B w - - 0 1",
                "8/p1p1p1p1/p1kP2p1/8/P1N2KN1/1P1bP1PP/1Q1Pq2P/r3n2B w - - 0 1",
                "8/p1p1p1p1/p1kP2p1/8/P1N3NK/1P1bP1PP/1Q1Pq2P/r3n2B w - - 0 1",
                "8/p1p1p1p1/p1kP2p1/8/P1N3N1/1PKbP1PP/1Q1Pq2P/r3n2B w - - 0 1",
                "8/p1p1p1p1/p1kP2p1/8/P1N3N1/1P1bP1PP/1Q1Pq2P/r3n1KB w - - 0 1"
        );

        int count = 0;
        for (String st : list) {
            count++;
//            System.out.println(count);
            System.out.println(st);
            ChessBoard board = BoardBuilder.buildBoard(st);
            board.setTurn(Objects.equals(board.getTurn(), "white") ? "black" : "white");
            if (!StateDetectorFactory.getDetector(board).testState()) {
                continue;
            }
            Solver solver = new Solver();
            solver.setNumberOfSolutions(1);
            solver.setAdditionalDepth(1);
            long start = System.currentTimeMillis();
            List<String> solutions = solver.solve(st, 3);
            System.out.println((float) (System.currentTimeMillis() - start) / 1000);
            if (count != list.size()) {
                Assertions.assertEquals(0, solutions.size());
            } else {
                Assertions.assertNotEquals(0, solutions.size());
            }
        }
        // This correctly assesses using CaptureLocations that after a depth of two,
        // the position becomes impossible as black's captures no longer add up
    }

    @Test
    public void ChessMysteries17() {
        //pp126
        // NO WHITE PAWN HAS PROMOTED.
        //ON WHAT SQUARE WAS THE OTHER WHITE BISHOP
        //CAPTURED?
        List<String> list = List.of(
                "rqr5/2Rp1bpp/1kp5/4p1Pp/K7/1P3N1P/PQ1P1PP1/5B2 b - - 0 1"
        );

        for (String st : list) {
            System.out.println(st);
            ChessBoard board = BoardBuilder.buildBoard(st);
            board.setTurn(Objects.equals(board.getTurn(), "white") ? "black" : "white");
            if (!StateDetectorFactory.getDetector(board).testState()) {
                continue;
            }
            Solver solver = new Solver(s -> true, new StateConditions.PromotionLimit(1, 0,
                    HeuristicsUtil.PIECE_CODES.keySet().stream().toList()));
            solver.setNumberOfSolutions(10);
            solver.setAdditionalDepth(0);
            long start = System.currentTimeMillis();
            List<String> solutions = solver.solve(st, 4);
            System.out.println((float) (System.currentTimeMillis() - start) / 1000);
            Assertions.assertEquals(1, solutions.size());
            Assertions.assertEquals("rqr5/1pRp1bpp/k1p5/4p1Pp/K1P5/1P3N1P/PQ1P1PP1/5B2 b " +
                    "- -:Pc4-c5, Pb7-b5, Pc5e.p.Pb6, Ka6xPb6", solutions.get(0));
        }

    }

    @Test
    public void ChessMysteries18() {
        //pp145

        List<String> list = List.of(
                "2B5/8/6p1/6Pk/3P2qb/3p4/3PB1P1/2NrNKQR b - - 0 1"
        );

        for (String st : list) {
            ChessBoard board = BoardBuilder.buildBoard(st);
            board.setTurn(Objects.equals(board.getTurn(), "white") ? "black" : "white");
            if (!StateDetectorFactory.getDetector(board).testState()) {
                continue;
            }
            Solver solver = new Solver(
                    new UnMoveCondition(0, 5, '-', "-", "-", '-', '-',
                            "x", true));
            solver.setNumberOfSolutions(5);
            solver.setAdditionalDepth(4);
            long start = System.currentTimeMillis();
            List<String> solutions = solver.solve(st, 1);
            System.out.println((float) (System.currentTimeMillis() - start) / 1000);
            Assertions.assertEquals(3, solutions.size());
        }
        //Smullyan is wrong here, there are three possible previous moves
    }

    @Test
    public void ChessMysteries19() {
        //pp146 M2

        List<String> list = List.of(
                "7k/8/6KB/8/p7/8/Q7/8 b - - 0 1",
                    "7k/8/6KB/8/p7/8/Q7/8 w - - 0 1"
        );
        int count = 0;
        for (String st : list) {
            count++;
            ChessBoard board = BoardBuilder.buildBoard(st);
            board.setTurn(Objects.equals(board.getTurn(), "white") ? "black" : "white");
            if (!StateDetectorFactory.getDetector(board).testState()) {
                continue;
            }
            Solver solver = new Solver(
                    new UnMoveCondition(0, 10, '-', "-", "-", '-',
                            '-', "x", true)
                            .and(new UnMoveCondition(0, 10, 'w', "-", "-", 'Q',
                                    '-', "any", true))
                            .and(new UnMoveCondition(0, 10, 'w', "-", "-", 'K',
                                    '-', "any", true))
                    );
            solver.setNumberOfSolutions(2);
            solver.setAdditionalDepth(8);
            long start = System.currentTimeMillis();
            List<String> solutions = solver.solve(st, 1);
            System.out.println((float) (System.currentTimeMillis() - start) / 1000);

            if (count == 1) {
                Assertions.assertEquals(0, solutions.size());
            } else {
                Assertions.assertEquals(1, solutions.size());
                Assertions.assertTrue(solutions.get(0).contains("Bg5-h6"));
            }
        }
    }
    @Test
    public void ChessMysteries20() {
        //pp146 M3

        List<String> list = List.of(
                "7r/pppppKpP/6P1/8/8/8/n7/8 w - - 0 1"
        );

        for (String st : list) {
            for (int x = 0 ; x < 8 ; x++ ) {
                for (int y = 0; y < 8; y++) {
                    ChessBoard board = BoardBuilder.buildBoard(st);
                    Coordinate target = new Coordinate(x, y);
                    if (!board.at(target).getType().equals("null")) {
                        continue;
                    }
                    board.place(target, StandardPieceFactory.getInstance().getPiece("k"));
                    board.setTurn(Objects.equals(board.getTurn(), "white") ? "black" : "white");
                    System.out.println(board.getReader().toFEN());
                    if (!StateDetectorFactory.getDetector(board).testState()) {
                        continue;
                    }
                    Solver solver = new Solver(
                            new UnMoveCondition(0, 5, '-', "-", "-", '-',
                                    '-', "x", true)
                                    .and(new UnMoveCondition(0, 5, '-', "-", "-",
                                            'P', '-', "any", true))
                            );
                    solver.setNumberOfSolutions(1);
                    solver.setAdditionalDepth(1);
                    long start = System.currentTimeMillis();
                    List<String> solutions = solver.solve(board.getReader().toFEN(), 3);
                    System.out.println((float) (System.currentTimeMillis() - start) / 1000);

                    if (target.equals(new Coordinate(2, 7))) {
                        Assertions.assertNotEquals(0, solutions.size());
                    } else {
                        Assertions.assertEquals(0, solutions.size());

                    }
                }
            }
        }
        // Successfully finds that the only option is c8
    }

    @Test
    public void ChessMysteries21() {
        //pp150

        List<String> list = List.of(
                "r3kqR1/1p1ppp2/5BpP/6PN/4P2p/3Q1P2/2PK2pP/8 b q - 0 1",
                "r3kqR1/1p1ppp2/5BpP/6PN/4P2p/3Q1P2/2PK2pP/8 b - - 0 1"
        );

        int count = 0;
        for (String st : list) {
            count++;
            System.out.println(st);
            ChessBoard board = BoardBuilder.buildBoard(st);
            board.setTurn(Objects.equals(board.getTurn(), "white") ? "black" : "white");
            Solver solver = new Solver();
            solver.setNumberOfSolutions(1);
            solver.setAdditionalDepth(1);
            long start = System.currentTimeMillis();
            List<String> solutions = solver.solve(st, 2);
            System.out.println((float) (System.currentTimeMillis() - start) / 1000);
            if (count == 1) {
                Assertions.assertEquals(0, solutions.size());
            } else {
                Assertions.assertNotEquals(0, solutions.size());
            }
        }
        // After every possible move black could make, there reaches a state where a2 needs to promote
        // It needs to do this by crossing a8
        // After a second level of depth, it comes about that every move results in one fewer capture for white
        // This means the promotion must take place without a capture
        // This is not possible if black can castle
        // The solver figures this out
    }

    @Test
    public void ChessMysteries22() {
        // pp56
        List<String> list = List.of(
                "r3kr2/1pp2p2/1pn2npP/1Q1pp3/1b6/2N2NPP/1PPP1P2/R3K1R1 w q - 0 1",
                "r3kr2/1pp2p2/1pn2npP/1Q1pp3/1b6/2N2NPP/1PPP1P2/R3K1R1 w - - 0 1",

                "r3kr2/1pp2p2/1pn2npP/1Q1pp3/1b6/2N2NPP/1PPP1P2/R3K3 w q - 0 1"
        );
        for (String st : list) {
            Solver solver = new Solver();
            solver.setNumberOfSolutions(1);
            solver.setAdditionalDepth(0);
            long start = System.currentTimeMillis();
            List<String> sollutions = solver.solve(st, 2);
            System.out.println((float) (System.currentTimeMillis() - start) / 1000);
            switch (st) {
                case "r3kr2/1pp2p2/1pn2npP/1Q1pp3/1b6/2N2NPP/1PPP1P2/R3K1R1 w q - 0 1" ->
                        Assertions.assertEquals(0, sollutions.size());
                case "r3kr2/1pp2p2/1pn2npP/1Q1pp3/1b6/2N2NPP/1PPP1P2/R3K1R1 w - - 0 1" ->
                        Assertions.assertNotEquals(0, sollutions.size());
                case "r3kr2/1pp2p2/1pn2npP/1Q1pp3/1b6/2N2NPP/1PPP1P2/R3K3 w q - 0 1" ->
                        Assertions.assertNotEquals(0, sollutions.size());
            }
        }
    }

    @Test
    public void MeisterwerkederRetroAnalyse() {
        //Karl Fabel's in Die Schwalbe in 1985

        List<String> list = List.of(
                "6K1/7B/4Pk2/8/6Q1/4Q3/8/B7"
        );

        for (String st : list) {
            System.out.println(st);
            ChessBoard board = BoardBuilder.buildBoard(st);
            board.setTurn(Objects.equals(board.getTurn(), "white") ? "black" : "white");
            if (!StateDetectorFactory.getDetector(board).testState()) {
                System.out.println("c");
                continue;
            }
            Solver solver = new Solver();
            solver.setNumberOfSolutions(2);
            solver.setAdditionalDepth(1);
            long start = System.currentTimeMillis();
            List<String> solutions = solver.solve(st, 6);
            System.out.println((float) (System.currentTimeMillis() - start) / 1000);
            System.out.println(solutions);
            Assertions.assertEquals(1, solutions.size());
            Assertions.assertTrue( solutions.get(0).contains("6K1/4pp1B/4k3/4P3/3P2Q1/4Q3/8/B7"));

        }
    }

    @Test
    public void Keym1() {
        //pp 36, 123

        List<String> list = List.of(
                "B1Q5/4p1p1/7p/1P3r1q/1P6/P5K1/P2P4/7k w - - 0 1"
        );

        for (String st : list) {
            System.out.println(st);
            ChessBoard board = BoardBuilder.buildBoard(st);
            board.setTurn(Objects.equals(board.getTurn(), "white") ? "black" : "white");
            if (!StateDetectorFactory.getDetector(board).testState()) {
                System.out.println("c");
                continue;
            }
            Solver solver = new Solver();
            solver.setNumberOfSolutions(2);
            solver.setAdditionalDepth(6);
            long start = System.currentTimeMillis();

            List<String> solutions = solver.solve(st, 1);
            System.out.println((float) (System.currentTimeMillis() - start) / 1000);

            Assertions.assertEquals(1, solutions.size());
            Assertions.assertTrue(solutions.get(0).contains("Kf3xPg3"));
            System.out.println(solutions);

            solver = new Solver(s ->
                    !(s.split(":")[2].equals("0") && s.contains("Kf3xPg3")));
            solver.setNumberOfSolutions(2);
            solver.setAdditionalDepth(1);
            start = System.currentTimeMillis();

            solutions = solver.solve(st, 5);
            System.out.println((float) (System.currentTimeMillis() - start) / 1000);
            System.out.println(solutions);
            Assertions.assertEquals(0, solutions.size());
        }
    }

    @Test
    public void Keym2() {
        //pp 122 no.385
        List<String> list = List.of(
                "r3k3/2p1p3/2P1P3/2KpP3/8/8/8/8 b q - 0 1"
        );

        for (String st : list) {
            System.out.println(st);
            ChessBoard board = BoardBuilder.buildBoard(st);
            board.setTurn(Objects.equals(board.getTurn(), "white") ? "black" : "white");
            DetectorInterface detectorInterface = StateDetectorFactory.getDetectorInterface(board);
            if (!detectorInterface.testState()) {
                System.out.println(detectorInterface.getErrorMessage());
                continue;
            }

            Solver solver = new Solver();
            solver.setNumberOfSolutions(2);
            solver.setAdditionalDepth(2);

            long start = System.currentTimeMillis();
            List<String> solutions = solver.solve(st, 1);
            System.out.println((float) (System.currentTimeMillis() - start) / 1000);
            Assertions.assertEquals(1, solutions.size());
            Assertions.assertTrue(solutions.get(0).contains("Pd7-d5"));
            System.out.println(solutions);

            solver = new Solver(s ->
                    !(s.split(":")[2].equals("0") && s.contains("Pd7-d5")));
            solver.setNumberOfSolutions(2);
            solver.setAdditionalDepth(1);
            start = System.currentTimeMillis();
            solutions = solver.solve(st, 2);
            System.out.println((float) (System.currentTimeMillis() - start) / 1000);
            System.out.println(solutions);
            Assertions.assertEquals(0, solutions.size());
        }
        // The question here is one of whether en passant must have taken place if castling is possible
    }

    @Test
    public void skakbladet1() {
        //https://skak.dk/images/skakbladet/1924/1924-08.pdf
        //Type A
        // pp88
        // N. Hoeg
        // http://www.anselan.com/length/length.html number 1

        List<String> list = List.of(
                "8/8/8/8/P7/kP6/prP5/K7 w - - 0 1",
                "8/8/8/8/P7/kP6/prP5/K7 b - - 0 1"
        );

        int count = 0;
        for (String st : list) {
            count++;
            System.out.println(st);
            ChessBoard board = BoardBuilder.buildBoard(st);
            board.setTurn(Objects.equals(board.getTurn(), "white") ? "black" : "white");
            if (!StateDetectorFactory.getDetector(board).testState()) {
                System.out.println("c");
                continue;
            }
            Solver solver = new Solver();
            solver.setNumberOfSolutions(2);
            solver.setAdditionalDepth(2);
            long start = System.currentTimeMillis();
            List<String> solutions = solver.solve(st, 1);
            System.out.println((float) (System.currentTimeMillis() - start) / 1000);
            if (count == 2) {
                Assertions.assertNotEquals(0, solutions.size());
                Assertions.assertTrue(solutions.get(0).contains("Kb4xNa3"));
            } else {
                Assertions.assertEquals(0, solutions.size());

            }
            System.out.println(solutions);
        }
        // Type A - after both, only black is found to have an availbale move, and it must be Kb4xNa3
    }

    @Test
    public void Schwarzkopf() {
        //Bernd Schwarzkopf
        //Retro Mailing List, 24-Apr-2007
        //Black to move. Last 4 single moves?
        // http://www.anselan.com/length/length.html number 13 - original not found

        List<String> list = List.of(
                "b7/8/8/8/8/4p3/2k1Kp2/5r2 w - - 0 1"
        );

        for (String st : list) {
            System.out.println(st);
            ChessBoard board = BoardBuilder.buildBoard(st);
            board.setTurn(Objects.equals(board.getTurn(), "white") ? "black" : "white");
            if (!StateDetectorFactory.getDetector(board).testState()) {
                System.out.println("c");
                continue;
            }
            Solver solver = new Solver();
            solver.setNumberOfSolutions(2);
            solver.setAdditionalDepth(1);
            long start = System.currentTimeMillis();

            List<String> solutions = solver.solve(st, 4);
            System.out.println((float) (System.currentTimeMillis() - start) / 1000);
            Assertions.assertEquals(1, solutions.size());
            Assertions.assertTrue(solutions.get(0).contains("b7/8/8/3p4/8/5K2/2k1Pp2/5r2"));


            System.out.println(solutions);
        }
        // Type B - Finds the only last 4 moves available, set to 2 solutions, finds 1
    }

    @Test
    public void Schwarzkopf2() {
        //Bernd Schwarzkopf
        //feenschach n°64, 1983
        //Last 3 single moves?
        // http://www.anselan.com/length/length.html number 30 - original not found

        List<String> list = List.of(
                "8/8/8/8/8/3PPPPP/1PPrR1RB/2brBkbK w - - 0 1",
                "8/8/8/8/8/3PPPPP/1PPrR1RB/2brBkbK b - - 0 1"
        );

        for (String st : list) {
            System.out.println(st);
            ChessBoard board = BoardBuilder.buildBoard(st);
            board.setTurn(Objects.equals(board.getTurn(), "white") ? "black" : "white");
            if (!StateDetectorFactory.getDetector(board).testState()) {
                System.out.println("c");
                continue;
            }
            Solver solver = new Solver();
            solver.setNumberOfSolutions(2);
            solver.setAdditionalDepth(2);
            long start = System.currentTimeMillis();
            List<String> solutions = solver.solve(st, 3);
            System.out.println(solutions);
            System.out.println((float) (System.currentTimeMillis() - start) / 1000);


        }
        // Fails  - Doesn't account for later piece cages
        // Finds the only last 4 moves available, set to 2 solutions, finds 1
    }

    @Test
    public void Dittmann () {
        //Wolfgang Dittmann
        //feenschach n°64, 1983
        // http://www.anselan.com/length/length.html number 28 - original not found

        List<String> list = List.of(
                "8/8/8/8/8/4P1P1/3Prp1P/5K1k w - - 0 1",
                "8/8/8/8/8/4P1P1/3Prp1P/5K1k b - - 0 1"
        );

        int count = 0;
        for (String st : list) {
            count++;
            System.out.println(st);
            ChessBoard board = BoardBuilder.buildBoard(st);
            board.setTurn(Objects.equals(board.getTurn(), "white") ? "black" : "white");
            if (!StateDetectorFactory.getDetector(board).testState()) {
                System.out.println("c");
                continue;
            }
            Solver solver = new Solver();
            solver.setNumberOfSolutions(2);
            solver.setAdditionalDepth(2);
            long start = System.currentTimeMillis();

            List<String> solutions = solver.solve(st, 1);
            System.out.println((float) (System.currentTimeMillis() - start) / 1000);
            System.out.println(solutions);
            if (count == 1) {
                Assertions.assertEquals(1, solutions.size());
                Assertions.assertTrue(solutions.get(0).contains("8/8/8/8/8/4P3/3PrpPP/5K1k"));
            } else {
                Assertions.assertEquals(1, solutions.size());
                Assertions.assertTrue(solutions.get(0).contains("8/8/8/8/8/4PpP1/3Pr2P/5K1k"));
            }
        }
    }
}
