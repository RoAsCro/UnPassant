import Heuristics.Detector.DetectorInterface;
import Heuristics.Detector.StateDetectorFactory;
import SolveAlgorithm.Solver;
import SolveAlgorithm.StateConditions;
import StandardChess.BoardBuilder;
import StandardChess.ChessBoard;
import StandardChess.Coordinate;
import StandardChess.StandardPieceFactory;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Objects;

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
        solver.setAdditionalDepth(1);
        solver.setNumberOfSolutions(100);
        Assertions.assertEquals(3, solver.solve(BoardBuilder.buildBoard(board2 + " w"), 1).size());
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
                Solver solver = new Solver(string -> true, detectorInterface -> detectorInterface.canCastle(false, true) || detectorInterface.canCastle(false, false)
                );
                solver.setNumberOfSolutions(1);
                solver.setAdditionalDepth(2);
                Assertions.assertEquals(0, solver.solve(b, 2).size());
                System.out.println("Finished 1");

                solver = new Solver();
                solver.setNumberOfSolutions(1);
                solver.setAdditionalDepth(2);
                b = BoardBuilder.buildBoard(s);
                b.setTurn(b.getTurn().equals("white") ? "black" : "white");
                Assertions.assertNotEquals(0, solver.solve(b, 2).size());
                System.out.println("Finished 2");

                System.out.println("Testing alternative... r3k3/ppp3pp/N5p1/P2Kp2P/2B5/p5P1/PP3PP1/R7 w - - 0 1");
                solver = new Solver(string -> !(Integer.parseInt(string.split(":")[2]) == 0) || string.split(":")[1].charAt(0) == 'N',
                        detectorInterface -> detectorInterface.canCastle(false, true) || detectorInterface.canCastle(false, false));
                solver.setNumberOfSolutions(1);
                solver.setAdditionalDepth(2);
                b = BoardBuilder.buildBoard("r3k3/ppp3pp/6p1/P2Kp2P/1NB5/p5P1/PP3PP1/R7 b - - 0 1");
                b.setTurn(b.getTurn().equals("white") ? "black" : "white");
                Assertions.assertNotEquals(0, solver.solve(b, 3).size());
                System.out.println("Finished 3");

            }
        }
        // The original puzzle is actually created incorrectly - with the knight moved as the last white move,
        // the pawn can move more freely than the apparent solution allows for
        // In the above, the first tests an amended version of the puzzle, the knight having not moved, with an extra check that black can castle
        // The second does the same without the check for castling, therefore a line is found
        // The third tries the puzzle under the original parameters, with the castling check and therefore finds alternative lines

        // The pawn map constructs maps for both pawns
        // The black move cannot be K or R - this means black cannot castle
        // The black move cannot be g6 - The PieceMap will note this will result in the king moving
        // It cannot be e5 - white will be in check on black's turn
        // a4-a3 will be moved
        // The pawn map is rebuilt
        // All of black's captures were made by pawns
        // 4 / 5 of white's captures were made by pawns
        // CaptureLocations see pawn captures took place on white, therefore the black bishop cannot have been taken by white
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
        Assertions.assertNotEquals(0, solver.solve(BoardBuilder.buildBoard("r1b1k2r/p1p1p1p1/1p3p1p/8/8/P7/1PPPPPPP/2BQKB2 w - - 0 1"), 2).size());

        solver = new Solver();
        solver.setNumberOfSolutions(2);
        solver.setAdditionalDepth(1);
        Assertions.assertEquals(0, solver.solve(BoardBuilder.buildBoard("r1b1k2r/p1p1p1p1/1p3p1p/8/8/P7/1PPPPPPP/2BQKB2 w k - 0 1"), 2).size());

        // This is entirely a matter of iterating through valid moves
    }

    @Test
    public void ChessMysteries7() {
        // pp46
        Solver solver = new Solver(
                s -> {
                    int depth = Integer.parseInt(s.split(":")[2]);
                    return depth >= 2 || !s.split(":")[1].contains("x");
                }
        );
        solver.setNumberOfSolutions(2);
        solver.setAdditionalDepth(0);
        List<String> solutions = solver.solve(BoardBuilder.buildBoard("4k2r/8/8/8/2p5/5P2/2P2PP1/3b1RK1 w k - 0 1"), 2);
        System.out.println(solutions);
        Assertions.assertTrue((solutions.get(0).contains("4k2r/8/8/2p5/8/5P2/2P2PP1/3bK2R") || solutions.get(0).contains("4k2r/8/8/8/2p5/5P2/2P1bPP1/4K2R"))
        && (solutions.get(1).contains("4k2r/8/8/2p5/8/5P2/2P2PP1/3bK2R") || solutions.get(1).contains("4k2r/8/8/8/2p5/5P2/2P1bPP1/4K2R"))) ;

        solver = new Solver(
                s -> {
                    int depth = Integer.parseInt(s.split(":")[2]);
                    return depth >= 2 || !s.split(":")[1].contains("x");
                }
        );
        solver.setNumberOfSolutions(1);
        solver.setAdditionalDepth(0);
        Assertions.assertEquals(0, solver.solve(BoardBuilder.buildBoard("4k2r/8/8/8/2p5/5P2/2P2PP1/3b1RK1 w k - 0 1"), 3).size());

        // The first here does not have any extra depth, checking that it comes to one of two valid solutions to the
        // first part of the problem, the only moves available with the given castling rights and the capture restrictions
        // The second part checks with additional depth - the detector uses the information gathered by the piece and pawn
        // maps to conclude that the bishop A. must have promoted, B. Must have promoted on that square, C. must have passed
        // a critical king sqaure to get there - this would violate the castling constraints that happen as a result of the iteration

    }

    @Test
    public void ChessMysteries8() {
        // pp49
        Solver solver = new Solver();
        solver.setNumberOfSolutions(1);
        solver.setNumberOfSolutions(1);
        solver.setAdditionalDepth(1);
        Assertions.assertEquals(1, solver.solve(BoardBuilder.buildBoard("k2q3r/R1B4p/R4p2/K2B2p1/1P2n1P1/P7/2P5/r4B2 w - - 0 1"), 1).size());
        // This is entirely a matter of iterating through valid moves
    }

    // 54 cannot be done as it is a question of timing
    @Test
    public void ChessMysteries9() {
        // pp54
        List<String> list = List.of(
                "r3kr2/1pp2p2/1pn2npP/1Q1pp3/1b6/2N2NPP/1PPP1P2/R3K1R1 w q - 0 1",
                "r3kr2/1pp2p2/1pn2npP/1Q1pp3/1b6/2N2NPP/1PPP1P2/R3K1R1 b Q - 0 1",

                "r3kr2/1pp2p2/1pn2npP/1Q1pp3/1b6/2N2NPP/1PPP1P2/R3K3 w q - 0 1",
                "r3kr2/1pp2p2/1pn2npP/1Q1pp3/1b6/2N2NPP/1PPP1P2/R3K3 b q - 0 1",
                "r3kr2/1pp2p2/1pn2npP/1Q1pp3/1b6/2N2NPP/1PPP1P2/R3K3 w Q - 0 1",
                "r3kr2/1pp2p2/1pn2npP/1Q1pp3/1b6/2N2NPP/1PPP1P2/R3K3 b Q - 0 1"



        );
        for (String st : list) {
            Solver solver = new Solver(s -> {
                boolean black = s.split(" ")[1].equals("b");
                String move = s.split(":")[1];

                int xY = (Integer.parseInt(move.substring(move.length() - 1)) - 1) + (((int) move.charAt(move.length() - 2)) - 97);

                if (black && move.contains("xQ") && xY % 2 == 0) {
                    return false;
                }
                xY = (Integer.parseInt(move.substring(2, 3)) - 1) + (((int) move.charAt(1)) - 97);
                return black || move.charAt(0) != 'Q' || xY % 2 != 0;
            }
            );
            solver.setNumberOfSolutions(1);
            solver.setAdditionalDepth(0);
            solver.solve(BoardBuilder.buildBoard(st), 2);
        }

        //This puzzle has two relevant parts, (a) and (b)
        // (a)
        // This is not really a problem of last n moves AND the additional rules can't be accounted for in the deductions
        // The first part can be spookfed by replacing the white bishop with a queen, resulting in it coming to
        // the same conclusion as you would if a captured queen never left white - every piece that can have been captured
        // by a black pawn was captured by a black pawn, which means the a2 pawn was either taken or promoted,
        // and since it cannot have reached the square on which b6 makes it'scapture, it promoted, and it must have promoted
        // on a8 as every white capture was carried out by the h6 pawn, resulting in the a8 rook being displaced
        // The second part of the puzzle is a question of timing, which is beyond the scope of UnPassant, and not of last
        // n moves
        // (b)
        // This is also not a last n moves question, but once again the first part can be solved -
        // With the extra missing piece, there's now no reason to believe that a2 must have promoted or been captured by  the b6 pawn
    }

    @Test
    public void ChessMysteries10() {
        // pp58
        List<String> list = List.of(
                "2kr3r/1qppp2p/pn3n1p/4b3/b6P/P1N2N2/1PPP1PP1/R3K2R b Q - 0 1",
                "2kr3r/1qppp2p/pn3n1p/4b3/b6P/P1N2N2/1PPP1PP1/R3K2R w K - 0 1",
                "2kr3r/1qppp2p/pn3n1p/4b3/b6P/P1N2N2/1PPP1PP1/R3K2R b Q - 0 1",
                "2kr3r/1qppp2p/pn3n1p/4b3/b6P/P1N2N2/1PPP1PP1/R3K2R w K - 0 1",
                "2kr3r/1qppp2p/pn3n1p/4b3/b6P/P1N2N2/1PPP1PP1/R3K2R w KQ - 0 1",
                "2kr3r/1qppp2p/pn3n1p/4b3/b6P/P1N2N2/1PPP1PP1/R3K2R b KQ - 0 1"

        );

        for (String st : list) {
            Solver solver = new Solver(
            );
            solver.setNumberOfSolutions(1);
            solver.setAdditionalDepth(1);
            solver.solve(BoardBuilder.buildBoard(st), 1);
        }

        //Timing puzzle
    }

   @Test
    public void ChessMysteries11() {
        // pp61
        List<String> list = List.of(
                "2b5/pp1p4/PRP5/pR3N2/2K5/2P5/2k1PNP1/1nrnB3 b - - 0 1",
                "2b5/pp1p4/PRP5/pR3N2/2K5/2P5/2k1PN1P/1nrnB3 b - - 0 1"
        );

        int count = 0;
        for (String st : list) {
            count++;
            StateConditions.NoPromotions noPromotions = new StateConditions.NoPromotions();
            Solver solver = new Solver( s ->{

                return !(s.split(":")[1].charAt(0) == 'P'
                        && (s.split(":")[1].endsWith("1")  || s.split(":")[1].endsWith("8")));

            },
                    noPromotions
            );

            solver.setNumberOfSolutions(1);
            solver.setAdditionalDepth(1);
            if (count == 1) {
                Assertions.assertEquals(0, solver.solve(BoardBuilder.buildBoard(st), 2).size());
            } else {
                Assertions.assertNotEquals(0, solver.solve(BoardBuilder.buildBoard(st), 2).size());
            }
        }
        // The above Tests the two possibilities, finding one has no solutions

    }

    @Test
    public void ChessMysteries12() {
        // pp64
        List<String> list = List.of(
                "2b5/pp1p4/PR1P4/pR3N2/2K5/2P5/2k1PNP1/1nrnB3 b - - 0 1");

        int count = 0;
        for (String st : list) {
            count++;
            Solver solver = new Solver(
                    s ->{
                return !(s.split(":")[1].charAt(0) == 'P'
                        && (s.split(":")[1].endsWith("1")  || s.split(":")[1].endsWith("8"))); //A piece does not promote

            },
                    d -> {
                        return d.getPromotions(true)
                                .entrySet().stream().filter(e -> !e.getKey().equals("queen"))
                                .flatMap(m -> m.getValue().keySet().stream())
                                .toList().isEmpty()
                                &&
                                d.getPromotions(false)
                                .entrySet().stream().filter(e -> !e.getKey().equals("queen"))
                                .flatMap(m -> m.getValue().keySet().stream())
                                .toList().isEmpty();
                    }
            );
            solver.setNumberOfSolutions(3);
            solver.setAdditionalDepth(0);
//            if (count == 1) {
            List<String> solutions = solver.solve(BoardBuilder.buildBoard(st), 3);
            System.out.println(solutions);
            Assertions.assertNotEquals(0, solutions.size());
        }
        // With it set to 3 solutions, the one it finds must be the only solution

    }

    @Test
    public void ChessMysteries13() {
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
            List<String> solutions = solver.solve(BoardBuilder.buildBoard(st), 2);
            if (count == 1) {
                Assertions.assertNotEquals(0, solutions.size());
//            Assertions.assertTrue(solutions.stream().anyMatch(s -> s.contains("2b5/pp1p4/PR1P4/pqR2N2/2K5/2P5/1kP1PNP1/1nrnB3")));
            } else if (count == 2) {
                Assertions.assertEquals(0, solutions.size());
            } else if (count == 3) {
                Assertions.assertNotEquals(0, solutions.size());

            }
        }
        // This is a question of iterating

    }

    @Test
    public void ChessMysteries14() {
        // pp73
        List<String> list = List.of(
                "QN6/7R/1k6/1N6/1K6/8/8/8 b - - 0 1");

        int count = 0;
        for (String st : list) {
            count++;
            Solver solver = new Solver();
            solver.setNumberOfSolutions(2);
            solver.setAdditionalDepth(1);
            List<String> solutions = solver.solve(BoardBuilder.buildBoard(st), 2);
            Assertions.assertNotEquals(0, solutions.size());
        }
        // This is a question of iterating
        // Finding that the positions are possible

    }

    @Test
    public void ChessMysteries15() {
        // pp78
        List<String> list = List.of(
                "r3k3/8/8/8/8/8/5PP1/6bK w q - 0 1");

        int count = 0;
        for (String st : list) {
            count++;
            Solver solver = new Solver();
            solver.setNumberOfSolutions(1);
            solver.setAdditionalDepth(1);
            List<String> solutions = solver.solve(BoardBuilder.buildBoard(st), 2);
            Assertions.assertEquals(0, solutions.size());
//            Assertions.assertTrue(solutions.stream().anyMatch(s -> s.contains("2b5/pp1p4/PR1P4/pqR2N2/2K5/2P5/1kP1PNP1/1nrnB3")));
        }
        // This is a question of iterating
        // Also a simple check of castling rights

    }

    @Test
    public void ChessMysteries16() {
        // pp89
        List<String> list = List.of(
                "4k2r/p4pp1/pp6/1p6/PPP5/p7/PP3P2/3K4 b k - 0 1",
                "4k2r/p4pp1/pp6/1p6/PPP5/p7/PP4P1/3K4 b k - 0 1");

        int count = 0;
        for (String st : list) {
            count++;
            Solver solver = new Solver();
            solver.setNumberOfSolutions(1);
            solver.setAdditionalDepth(1);
            List<String> solutions = solver.solve(BoardBuilder.buildBoard(st), 2);
//            Assertions.assertEquals(0, solutions.size());
//            Assertions.assertTrue(solutions.stream().anyMatch(s -> s.contains("2b5/pp1p4/PR1P4/pqR2N2/2K5/2P5/1kP1PNP1/1nrnB3")));
        }
        // Unfortunately the algorithm cannot solve this
        // It relies on both theoretically promoted pawns' paths being exclusive

    }

    @Test
    public void ChessMysteries17() {
        // pp91
        List<String> list = List.of(
                "r3k2r/pbpp1ppp/2n4n/4p1q1/1b6/8/1PPPPPPP/1NBQKBNR b Kkq - 0 1",
                "r3k2r/pbpp1ppp/2n4n/4p1q1/1b6/8/1PPPPPPP/1NBQKBNR b Kkq - 0 1",
                "r3k2r/pbpp1ppp/2n4n/4p1q1/1b6/8/1PPPPPPP/1NBQKBNR b Kkq - 0 1",
                "r3k2r/pbpp1ppp/2n4n/4p1q1/1b6/8/1PPPPPPP/1NBQKBNR b Kkq - 0 1",
                "r3k2r/pbpp1ppp/2n4n/4p1q1/1b6/8/1PPPPPPP/1NBQKBNR b Kkq - 0 1",
                "r3k2r/pbpp1ppp/2n4n/4p1q1/1b6/8/1PPPPPPP/1NBQKBNR b Kkq - 0 1",
                "r3k2r/pbpp1ppp/2n4n/4p1q1/1b6/8/1PPPPPPP/1NBQKBNR b Kkq - 0 1",
                "r3k2r/pbpp1ppp/2n4n/4p1q1/1b6/8/1PPPPPPP/1NBQKBNR b Kkq - 0 1",
                "r3k2r/pbpp1ppp/2n4n/4p1q1/1b6/8/1PPPPPPP/1NBQKBNR b Kkq - 0 1",
                "r3k2r/pbpp1ppp/2n4n/4p1q1/1b6/8/1PPPPPPP/1NBQKBNR b Kkq - 0 1"
        );

        int count = 0;
        for (String st : list) {
            count++;
            System.out.println(count);
            Solver solver = new Solver(s -> {
                String move = s.split(":")[1];
                if (move.contains("x") && !(s.split(":")[2].equals("0") || s.split(" ")[1].contains("b"))) {
                    return false;
                }
                if (move.charAt(1) == 'R' || move.charAt(1) == 'K') {
                    return false;
                }
                    return move.charAt(4) != 'R' && !(s.split(" ")[1].contains("b") && move.charAt(0) == 'N') ;
            }
            ,d -> {
//                if (d.getPromotions().values().stream().flatMap(List::stream).toList().isEmpty()) {
////                    System.out.println(d.getPromotions().values().stream().flatMap(List::stream).toList());
//                }
                return d.getPromotions(true).values()
                        .stream().flatMap(m -> m.keySet().stream())
                        .toList().isEmpty()
                        &&
                        d.getPromotions(false).values()
                                .stream().flatMap(m -> m.keySet().stream())
                                .toList().isEmpty();
            }
            );
            solver.setNumberOfSolutions(1);
            solver.setAdditionalDepth(0);
//            System.out.println(count);
            List<String> solutions = solver.solve(BoardBuilder.buildBoard(st), count);
//            Assertions.assertEquals(0, solutions.size());
//            Assertions.assertTrue(solutions.stream().anyMatch(s -> s.contains("2b5/pp1p4/PR1P4/pqR2N2/2K5/2P5/1kP1PNP1/1nrnB3")));
        }
        // Theoretically, this can be solved by the algorithm
        // However, there is one solution out of thousands, and there is no way to eliminate possibilities before
        //  getting to a depth of 15, therefore it cannot be found in a timely manner

    }

    @Test
    public void ChessMysteries17B() {
        // pp89
        List<String> list = List.of(
                "r3k2r/pbpp1ppp/2n4n/4p1q1/1b6/8/1PPPPPPP/1NBQKBNR b Kkq - 0 1"
        );

        int count = 11;
        for (String st : list) {
            count++;
            System.out.println(count);
            Solver solver = new Solver(s -> {
                String move = s.split(":")[1];
//                System.out.println(s);

                if (move.contains("x") && !(move.charAt(4) == 'P')) {
                    return false;
                }
//                System.out.println(move.charAt(0) == 'N');
//                System.out.println((s.split(" ")[1]));

                if (move.charAt(0) == 'N' && (s.split(" ")[1].contains("b"))) {
                    return false;
                }
                if (move.charAt(1) == 'R' || move.charAt(1) == 'K') {
                    return false;
                }
//                System.out.println("succeeds");
                return true;
            }
                    ,d -> {
//                if (d.getPromotions().values().stream().flatMap(List::stream).toList().isEmpty()) {
////                    System.out.println(d.getPromotions().values().stream().flatMap(List::stream).toList());
//                }
                return d.getPromotions(true).values()
                        .stream().flatMap(m -> m.keySet().stream())
                        .toList().isEmpty()
                        &&
                        d.getPromotions(false).values()
                                .stream().flatMap(m -> m.keySet().stream())
                                .toList().isEmpty();
            }
            );
            solver.setNumberOfSolutions(1);
            solver.setAdditionalDepth(0);
//            System.out.println(count);
//            List<String> solutions = solver.solve(BoardBuilder.buildBoard(st), count);
//            System.out.println(solutions);
//            Assertions.assertEquals(0, solutions.size());
//            Assertions.assertTrue(solutions.stream().anyMatch(s -> s.contains("2b5/pp1p4/PR1P4/pqR2N2/2K5/2P5/1kP1PNP1/1nrnB3")));
        }
        // Theoretically, this can be solved by the algorithm
        // However, there is one solution out of thousands, and there is no way to eliminate possibilities before
        //  getting to a depth of 15, therefore it cannot be found in a timely manner

    }

    @Test
    public void ChessMysteries18() {
        // pp93
        List<String> list = List.of(
                "3qk2r/pP1pbp1p/1pp2np1/2nBpb2/8/NP2PN1P/P2P1P1P/R2QK3 b Qk - 0 1",
                "3qk2r/p1Ppbp1p/1pp2np1/2nBpb2/8/NP2PN1P/P2P1P1P/R2QK3 b Qk - 0 1",
                "3qk2r/p2pbpPp/1pp2np1/2nBpb2/8/NP2PN1P/P2P1P1P/R2QK3 b Qk - 0 1",
                "3qk2r/p2pbp1p/Ppp2np1/2nBpb2/8/NP2PN1P/P2P1P1P/R2QK3 b Qk - 0 1",
                "3qk2r/p2pbp1p/1ppP1np1/2nBpb2/8/NP2PN1P/P2P1P1P/R2QK3 b Qk - 0 1",
                "3qk2r/p2pbp1p/1pp1Pnp1/2nBpb2/8/NP2PN1P/P2P1P1P/R2QK3 b Qk - 0 1",
                "3qk2r/p2pbp1p/1pp2npP/2nBpb2/8/NP2PN1P/P2P1P1P/R2QK3 b Qk - 0 1",
                "3qk2r/p2pbp1p/1pp2np1/P1nBpb2/8/NP2PN1P/P2P1P1P/R2QK3 b Qk - 0 1",
                "3qk2r/p2pbp1p/1pp2np1/1PnBpb2/8/NP2PN1P/P2P1P1P/R2QK3 b Qk - 0 1",
                "3qk2r/p2pbp1p/1pp2np1/2nBpbP1/8/NP2PN1P/P2P1P1P/R2QK3 b Qk - 0 1",
                "3qk2r/p2pbp1p/1pp2np1/2nBpb1P/8/NP2PN1P/P2P1P1P/R2QK3 b Qk - 0 1",
                "3qk2r/p2pbp1p/1pp2np1/2nBpb2/P7/NP2PN1P/P2P1P1P/R2QK3 b Qk - 0 1",
                "3qk2r/p2pbp1p/1pp2np1/2nBpb2/1P6/NP2PN1P/P2P1P1P/R2QK3 b Qk - 0 1",
                "3qk2r/p2pbp1p/1pp2np1/2nBpb2/2P5/NP2PN1P/P2P1P1P/R2QK3 b Qk - 0 1",
                "3qk2r/p2pbp1p/1pp2np1/2nBpb2/4P3/NP2PN1P/P2P1P1P/R2QK3 b Qk - 0 1",
                "3qk2r/p2pbp1p/1pp2np1/2nBpb2/5P2/NP2PN1P/P2P1P1P/R2QK3 b Qk - 0 1",
                "3qk2r/p2pbp1p/1pp2np1/2nBpb2/6P1/NP2PN1P/P2P1P1P/R2QK3 b Qk - 0 1",
                "3qk2r/p2pbp1p/1pp2np1/2nBpb2/7P/NP2PN1P/P2P1P1P/R2QK3 b Qk - 0 1",
                "3qk2r/p2pbp1p/1pp2np1/2nBpb2/8/NPP1PN1P/P2P1P1P/R2QK3 b Qk - 0 1",
                "3qk2r/p2pbp1p/1pp2np1/2nBpb2/8/NP1PPN1P/P2P1P1P/R2QK3 b Qk - 0 1",
                "3qk2r/p2pbp1p/1pp2np1/2nBpb2/8/NP2PNPP/P2P1P1P/R2QK3 b Qk - 0 1",
                "3qk2r/p2pbp1p/1pp2np1/2nBpb2/8/NP2PN1P/PP1P1P1P/R2QK3 b Qk - 0 1",
                "3qk2r/p2pbp1p/1pp2np1/2nBpb2/8/NP2PN1P/P1PP1P1P/R2QK3 b Qk - 0 1",
                "3qk2r/p2pbp1p/1pp2np1/2nBpb2/8/NP2PN1P/P2PPP1P/R2QK3 b Qk - 0 1",
                "3qk2r/p2pbp1p/1pp2np1/2nBpb2/8/NP2PN1P/P2P1PPP/R2QK3 b Qk - 0 1"

        );

        int count = 0;
        for (String st : list) {
            count++;
            System.out.println(st);
            System.out.println(StateDetectorFactory.getDetector(st).testState());
        }
        // Not a question of last n moves, however it is capable of eleminating most of the possibilities,
        // But then it becomes a question of timing

    }

    @Test
    public void ChessMysteries19() {
        // pp89
        List<String> list = List.of(
                "r2qk2r/p1p2ppp/1pn2n2/1Nb1p1B1/5P2/2P1P2N/PPPQ2PP/2KR3R w - - "
        );

        int count = 0;
        for (String st : list) {
            count++;
//            System.out.println(count);
            Solver solver = new Solver(
                    s -> {
                String move = s.split(":")[1];
//                System.out.println(s.split(":")[2]);
                if (s.split(":")[2].equals("0")) {
//                    System.out.println(s);
                    return (move.charAt(0) == 'P' && move.endsWith("f4"));
                }
                return true ;
            });
            solver.setNumberOfSolutions(100);
            solver.setAdditionalDepth(1);
//            System.out.println(count);
            List<String> solutions = solver.solve(BoardBuilder.buildBoard(st), 1);
//            Assertions.assertEquals(0, solutions.size());
//            Assertions.assertTrue(solutions.stream().anyMatch(s -> s.contains("2b5/pp1p4/PR1P4/pqR2N2/2K5/2P5/1kP1PNP1/1nrnB3")));
        }
        // This is again a question of timing

    }

    @Test
    public void ChessMysteries20() {
        // pp103
        List<String> list = List.of(
                "r3k3/P5P1/1P1P1P2/3PpK2/8/8/6B1/8 b q - 0 1"
        );

        int count = 0;
        for (String st : list) {
            count++;
//            System.out.println(count);
            Solver solver = new Solver();
            solver.setNumberOfSolutions(1);
            solver.setAdditionalDepth(0);
//            System.out.println(count);
            List<String> solutions = solver.solve(BoardBuilder.buildBoard(st), 2);
            Assertions.assertNotEquals(0, solutions.size());
            solver = new Solver(
                    s -> {
                        String move = s.split(":")[1];
                        if (s.split(":")[2].equals("0")) {
                            System.out.println(s);
                            return (move.charAt(0) == 'P' && move.endsWith("f4"));
                        }
                        return true ;
                    });
            solver.setNumberOfSolutions(1);
            solver.setAdditionalDepth(1);
//            System.out.println(count);
            solutions = solver.solve(BoardBuilder.buildBoard(st), 2);
            Assertions.assertEquals(0, solutions.size());

        }
        // This successfully uncovers that the only valid move while maintaining castling rights is a double pawn move

    }

    @Test
    public void ChessMysteries21() {
        // pp105
        List<String> list = List.of(
                "r2qk2r/p1pp1pp1/1pn3n1/2b1p3/2B1P1P1/N1P3N1/P1PP1P1P/R2QK2R w KQkq - 0 1"
        );
        DetectorInterface detector = StateDetectorFactory.getDetectorInterface(list.get(0));

        System.out.println(detector.testState());
        Assertions.assertNotEquals(0, (int) detector.getPromotions(true).values().stream()
                .map(e -> e.values().stream().reduce(Integer::sum).orElse(0)).reduce(Integer::sum).orElse(0)
        + (int) detector.getPromotions(false).values().stream()
                .map(e -> e.values().stream().reduce(Integer::sum).orElse(0)).reduce(Integer::sum).orElse(0));
        // Simply checking there has been a promotion
    }

    //TODO
    //Note the numbers
    @Test
    public void ChessMysteries23() {
        // pp113
        List<String> list = List.of(
                "RbB5/n1p2pp1/2B1p3/PpKp4/kP2P3/p2p3P/P2P3P/8 b - - 0 1"
        );

        int count = 0;
        for (String st : list) {
            count++;
//            System.out.println(count);
            Solver solver = new Solver(
                    s -> {
                        String move = s.split(":")[1];
//                System.out.println(s);
                        if (s.split(":")[2].equals("0")) {
//                            System.out.println(move);
                            return !(move.charAt(0) == 'P' && move.endsWith("b5"))
                                    ;
                        }
                        return true ;
                    });
            solver.setNumberOfSolutions(5);
            solver.setAdditionalDepth(1);
//            System.out.println(count);
            List<String> solutions = solver.solve(BoardBuilder.buildBoard(st), 4);
            Assertions.assertEquals(0, solutions.size());

            solver = new Solver(
                    s -> true);
            solver.setNumberOfSolutions(5);
            solver.setAdditionalDepth(1);
//            System.out.println(count);
            solutions = solver.solve(BoardBuilder.buildBoard(st), 4);
            Assertions.assertNotEquals(0, solutions.size());
        }
        // This successfully uncovers that the only valid move while maintaining castling rights is a double pawn move
        // The first part tests it while dissallowing the only valid move
        // exaplanation...

    }

    @Test
    public void ChessMysteries24() {
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
                "8/p1p1p1p1/p1kP1Kp1/8/P1N3N1/1P1bP1PP/1Q1Pq2P/r3n2B w - - 0 1",
                "8/p1p1p1p1/p1kP2pK/8/P1N3N1/1P1bP1PP/1Q1Pq2P/r3n2B w - - 0 1",
                "8/p1p1p1p1/p1kP2p1/K7/P1N3N1/1P1bP1PP/1Q1Pq2P/r3n2B w - - 0 1",
                "8/p1p1p1p1/p1kP2p1/4K3/P1N3N1/1P1bP1PP/1Q1Pq2P/r3n2B w - - 0 1",
                "8/p1p1p1p1/p1kP2p1/5K2/P1N3N1/1P1bP1PP/1Q1Pq2P/r3n2B w - - 0 1",
                "8/p1p1p1p1/p1kP2p1/6K1/P1N3N1/1P1bP1PP/1Q1Pq2P/r3n2B w - - 0 1",
                "8/p1p1p1p1/p1kP2p1/7K/P1N3N1/1P1bP1PP/1Q1Pq2P/r3n2B w - - 0 1",
                "8/p1p1p1p1/p1kP2p1/8/PKN3N1/1P1bP1PP/1Q1Pq2P/r3n2B w - - 0 1",
                "8/p1p1p1p1/p1kP2p1/8/P1NK2N1/1P1bP1PP/1Q1Pq2P/r3n2B w - - 0 1",
                "8/p1p1p1p1/p1kP2p1/8/P1N1K1N1/1P1bP1PP/1Q1Pq2P/r3n2B w - - 0 1",
                "8/p1p1p1p1/p1kP2p1/8/P1N2KN1/1P1bP1PP/1Q1Pq2P/r3n2B w - - 0 1",
                "8/p1p1p1p1/p1kP2p1/8/P1N3NK/1P1bP1PP/1Q1Pq2P/r3n2B w - - 0 1",
                "8/p1p1p1p1/p1kP2p1/8/P1N3N1/KP1bP1PP/1Q1Pq2P/r3n2B w - - 0 1",
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
            Solver solver = new Solver(
                    s -> true);
            solver.setNumberOfSolutions(1);
            solver.setAdditionalDepth(1);
            List<String> solutions = solver.solve(BoardBuilder.buildBoard(st), 3);
            if (count != list.size()) {
                Assertions.assertEquals(0, solutions.size());
            } else {
                Assertions.assertNotEquals(0, solutions.size());
            }
        }
        // This correctly assesses using CaptureLocations that after a depth of two, the position becomes impossible as black's captures no longer add up

    }

    @Test
    public void ChessMysteries27() {
        //pp126
        // NO WHITE PAWN HAS PROMOTED.
        //ON WHAT SQUARE WAS THE OTHER WHITE BISHOP
        //CAPTURED?
        List<String> list = List.of(
                "rqr5/2Rp1bpp/1kp5/4p1Pp/K7/1P3N1P/PQ1P1PP1/5B2 b - - 0 1"
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
            List<String> solutions = solver.solve(BoardBuilder.buildBoard(st), 5);
            if (count != list.size()) {
                Assertions.assertEquals(0, solutions.size());
            } else {
                Assertions.assertNotEquals(0, solutions.size());
            }
        }

    }

    @Test
    public void ChessMysteries28() {
        //
        //pp145

        List<String> list = List.of(
                "2B5/8/6p1/6Pk/3P2qb/3p4/3PB1P1/2NrNKQR b - - 0 1"
        );

        int count = 0;
        for (String st : list) {
            count++;
//            System.out.println(count);
//            System.out.println(st);
            ChessBoard board = BoardBuilder.buildBoard(st);
            board.setTurn(Objects.equals(board.getTurn(), "white") ? "black" : "white");
            if (!StateDetectorFactory.getDetector(board).testState()) {
                continue;
            }
            Solver solver = new Solver(
                    s -> {
//                        System.out.println(s);
                        String move = s.split(":")[1];
                        return Integer.parseInt(s.split(":")[2]) > 4 || move.charAt(3) != 'x';
                    });
            solver.setNumberOfSolutions(2);
            solver.setAdditionalDepth(1);
            List<String> solutions = solver.solve(BoardBuilder.buildBoard(st), 4);
            System.out.println(solutions);
        }
        //Smullyan is just sort of wrong here
        // Unpassant comes up with the alternative:
        // 2B5/6p1/8/6Pk/3P2q1/3pR3/3PBbP1/2NrNKQ1 b - -:Re3-h3, Bf2-h4, Rh3-h1, Pg7-g6
    }

    @Test
    public void ChessMysteries29() {
        //
        //pp146

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
                    s -> {
//                        System.out.println(s);
                        String move = s.split(":")[1];
                        return Integer.parseInt(s.split(":")[2]) >= 10
                                || (move.charAt(3) != 'x' &&
                                (s.split(" ")[1].charAt(0) != 'w' || (move.charAt(0) != 'K' && move.charAt(0) != 'Q')));
                    });
            solver.setNumberOfSolutions(1);
            solver.setAdditionalDepth(0);
//            List<String> solutions = solver.solve(BoardBuilder.buildBoard(st), 8);
//            System.out.println(solutions);
        }
        // Another one where theoretically there should be no problem as it's a matter of iterating backwards
    }
    @Test
    public void ChessMysteries30() {
        //
        //pp146

        List<String> list = List.of(
                "7r/pppppKpP/6P1/8/8/8/n7/8 w - - 0 1"
        );

        int count = 0;
        for (String st : list) {
            count++;
//            System.out.println(count);

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
                            s -> {
//                        System.out.println(s);
                                String move = s.split(":")[1];
                                return Integer.parseInt(s.split(":")[2]) >= 10
                                        || (move.charAt(3) != 'x' &&
                                        move.charAt(0) != 'P');
                            });
                    solver.setNumberOfSolutions(1);
                    solver.setAdditionalDepth(1);
                    List<String> solutions = solver.solve(BoardBuilder.buildBoard(board.getReader().toFEN()), 3);
//
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
    public void ChessMysteries30B() {
        //
        //pp126

        List<String> list = List.of(
                "7r/pppppKpP/6P1/8/8/8/n7/8 w - -"
        );

        int count = 0;
        for (String st : list) {
            count++;
//            System.out.println(count);

                    ChessBoard board = BoardBuilder.buildBoard(st);
                    Coordinate target = new Coordinate(4, 4);
                    if (!board.at(target).getType().equals("null")) {
                        continue;
                    }
                    board.place(target, StandardPieceFactory.getInstance().getPiece("k"));
                    board.setTurn(Objects.equals(board.getTurn(), "white") ? "black" : "white");
                    System.out.println(board.getReader().toFEN());
                    if (!StateDetectorFactory.getDetector(board).testState()) {
                        continue;
                    }
                    Solver solver = new Solver();
                    solver.setNumberOfSolutions(1);
                    solver.setAdditionalDepth(0);
                    List<String> solutions = solver.solve(BoardBuilder.buildBoard(board.getReader().toFEN()), 3);
//
//                    if (target.equals(new Coordinate(2, 7))) {
//                        Assertions.assertNotEquals(0, solutions.size());
//                    } else {
//                        Assertions.assertEquals(0, solutions.size());
//
//                    }
            }

        // Successfully finds that the only option is c8
    }

    @Test
    public void ChessMysteries31() {
        //
        //pp150

        List<String> list = List.of(
                "r3kqR1/1p1ppp2/5BpP/6PN/4P2p/3Q1P2/2PK2pP/8 b q - 0 1",
                "r3kqR1/1p1ppp2/5BpP/6PN/4P2p/3Q1P2/2PK2pP/8 b - - 0 1"
        );

        int count = 0;
        for (String st : list) {
            count++;
//            System.out.println(count);
            System.out.println(st);
            ChessBoard board = BoardBuilder.buildBoard(st);
            board.setTurn(Objects.equals(board.getTurn(), "white") ? "black" : "white");
            if (!StateDetectorFactory.getDetector(board).testState()) {
                System.out.println("c");
                continue;
            }
            Solver solver = new Solver();
            solver.setNumberOfSolutions(1);
            solver.setAdditionalDepth(1);

            List<String> solutions = solver.solve(BoardBuilder.buildBoard(st), 2);
            if (count == 1) {
                Assertions.assertEquals(0, solutions.size());
            } else {
                Assertions.assertNotEquals(0, solutions.size());
            }
            System.out.println(solutions);
        }
        // After every possible move black could make, there reaches a state where a2 needs to promote
        // It needs to do this by crossing a8
        // After a second level of depth, it comes about that every move results in one fewer capture for white
        // This means the promotion must take place without a capture
        // This is not possible if black can castle
        // The solver figures this out
    }

    @Test
    public void MeisterwerkederRetroAnalyse() {
        //Karl Fabel's in Die Schwalbe in 1985

        List<String> list = List.of(
                "6K1/7B/4Pk2/8/6Q1/4Q3/8/B7"
        );

        int count = 0;
        for (String st : list) {
            count++;
//            System.out.println(count);
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

            List<String> solutions = solver.solve(BoardBuilder.buildBoard(st), 6);
            System.out.println(solutions);
            Assertions.assertEquals(1, solutions.size());
            Assertions.assertTrue( solutions.get(0).contains("6K1/4pp1B/4k3/4P3/3P2Q1/4Q3/8/B7"));

        }
    }

    @Test
    public void DieScwalbe1() {
        //pp 36

        List<String> list = List.of(
                "B1Q5/4p1p1/7p/1P3r1q/1P6/P5K1/P2P4/7k w - - 0 1"
        );

        int count = 0;
        for (String st : list) {
            count++;
//            System.out.println(count);
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

            List<String> solutions = solver.solve(BoardBuilder.buildBoard(st), 5);
            Assertions.assertNotEquals(0, solutions.size());
            Assertions.assertTrue(solutions.get(0).contains("Kf3xPg3"));
            System.out.println(solutions);

            solver = new Solver(s ->
                    !(s.split(":")[2].equals("0") && s.contains("Kf3xPg3")));
            solver.setNumberOfSolutions(2);
            solver.setAdditionalDepth(1);

            solutions = solver.solve(BoardBuilder.buildBoard(st), 5);
            System.out.println(solutions);
            Assertions.assertEquals(0, solutions.size());
        }
        // Check it's finding no solutions but the one given
    }

    @Test
    public void DieScwalbe2() {
        //pp 122 no.385

        List<String> list = List.of(
                "r3k3/2p1p3/2P1P3/2KpP3/8/8/8/8 b q - 0 1"
        );

        int count = 0;
        for (String st : list) {
            count++;
//            System.out.println(count);
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
            solver.setAdditionalDepth(1);

            List<String> solutions = solver.solve(BoardBuilder.buildBoard(st), 2);
            Assertions.assertNotEquals(0, solutions.size());
            Assertions.assertTrue(solutions.get(0).contains("Pd7-d5"));
            System.out.println(solutions);

            solver = new Solver(s ->
                    !(s.split(":")[2].equals("0") && s.contains("Pd7-d5")));
            solver.setNumberOfSolutions(2);
            solver.setAdditionalDepth(1);

            solutions = solver.solve(BoardBuilder.buildBoard(st), 2);
            System.out.println(solutions);
            Assertions.assertEquals(0, solutions.size());
        }
        // Check it's finding no solutions but the one given
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
//            System.out.println(count);
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

            List<String> solutions = solver.solve(BoardBuilder.buildBoard(st), 1);
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
        //(1 + 5)
        // http://www.anselan.com/length/length.html number 13 - original not found
        // Type B

        List<String> list = List.of(
                "b7/8/8/8/8/4p3/2k1Kp2/5r2 w - - 0 1"
        );

        int count = 0;
        for (String st : list) {
            count++;
//            System.out.println(count);
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

            List<String> solutions = solver.solve(BoardBuilder.buildBoard(st), 4);
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
        //Last 3 single moves? Duplex.
        //(12 + 5)
        // http://www.anselan.com/length/length.html number 30 - original not found
        // Type B

        List<String> list = List.of(
                "8/8/8/8/8/3PPPPP/1PPrR1RB/2brBkbK w - - 0 1",
                "8/8/8/8/8/3PPPPP/1PPrR1RB/2brBkbK b - - 0 1"
        );

        int count = 0;
        for (String st : list) {
            count++;
//            System.out.println(count);
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

//            List<String> solutions = solver.solve(BoardBuilder.buildBoard(st), 3);
//            System.out.println(solutions);

//            Assertions.assertEquals(1, solutions.size());
//            Assertions.assertTrue(solutions.get(0).contains("b7/8/8/3p4/8/5K2/2k1Pp2/5r2"));


        }
        // Fails  - Doesn't account for later piece cages
        // Type D - Finds the only last 4 moves available, set to 2 solutions, finds 1
    }

    @Test
    public void Dittmann () {
        //Wolfgang Dittmann
        //feenschach n°64, 1983
        //Last single move? Duplex.
        //(5 + 3)
        // http://www.anselan.com/length/length.html number 28 - original not found
        // Type B

        List<String> list = List.of(
                "8/8/8/8/8/4P1P1/3Prp1P/5K1k w - - 0 1",
                "8/8/8/8/8/4P1P1/3Prp1P/5K1k b - - 0 1"
        );

        int count = 0;
        for (String st : list) {
            count++;
//            System.out.println(count);
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

            List<String> solutions = solver.solve(BoardBuilder.buildBoard(st), 1);
            System.out.println(solutions);
            if (count == 1) {
                Assertions.assertEquals(1, solutions.size());
                Assertions.assertTrue(solutions.get(0).contains("8/8/8/8/8/4P3/3PrpPP/5K1k"));
            } else {
                Assertions.assertEquals(1, solutions.size());
                Assertions.assertTrue(solutions.get(0).contains("8/8/8/8/8/4PpP1/3Pr2P/5K1k"));


            }


        }
        // Fails  - Doesn't account for later piece cages
        // Type D - Finds the only last 4 moves available, set to 2 solutions, finds 1
    }

//    @Test
//    public void ArabianKnights1() {
//        //pp7
//
//        List<String> list = List.of(
//                "2bqk3/1p1ppp2/p1p2p2/Pn5p/4P1n1/1PN5/B1PP1PBP/R2QK1NR w KQ - 0 1"
//        );
//
//        int count = 0;
//        for (String st : list) {
//            count++;
////            System.out.println(count);
//            System.out.println(st);
//            ChessBoard board = BoardBuilder.buildBoard(st);
//            board.setTurn(Objects.equals(board.getTurn(), "white") ? "black" : "white");
//            if (!StateDetectorFactory.getDetector(board).testState()) {
//                System.out.println("c");
//                continue;
//            }
//            Solver solver = new Solver();
//            solver.setNumberOfSolutions(1);
//            solver.setAdditionalDepth(1);
//
//            List<String> solutions = solver.solve(BoardBuilder.buildBoard(st), 4);
//            System.out.println(solutions);
////            if (count == 1) {
////                Assertions.assertEquals(1, solutions.size());
////                Assertions.assertTrue(solutions.get(0).contains("8/8/8/8/8/4P3/3PrpPP/5K1k"));
////            } else {
////                Assertions.assertEquals(1, solutions.size());
////                Assertions.assertTrue(solutions.get(0).contains("8/8/8/8/8/4PpP1/3Pr2P/5K1k"));
////
////
////            }
//
//
//        }
//        // Fails  - Doesn't account for later piece cages
//        // Type D - Finds the only last 4 moves available, set to 2 solutions, finds 1
//    }

    @Test
    public void ChessMysteries() {
        // pp89
        List<String> list = List.of(
                "rnbqkbnr/pppppppp/8/8/7P/8/8/7K w kq - 0 1"
        );

        int count = 0;
        for (String st : list) {
            count++;
            Solver solver = new Solver(
                    s -> {
                        String move = s.split(":")[1];
                        if (s.split(":")[2].equals("0")) {
                            return !(move.charAt(0) == 'K');
                        }
                        return true ;
                    });
            solver.setNumberOfSolutions(1);
            solver.setAdditionalDepth(0);
            List<String> solutions = solver.solve(BoardBuilder.buildBoard(st), 1);
//            Assertions.assertEquals(0, solutions.size());
//            Assertions.assertTrue(solutions.stream().anyMatch(s -> s.contains("2b5/pp1p4/PR1P4/pqR2N2/2K5/2P5/1kP1PNP1/1nrnB3")));
        }
        // Theoretically, this can be solved by the algorithm
        // However, there is one solution out of thousands and it cannot be found in a timely manner

    }

    @Test
    public void checkTest() {
        // pp89
        List<String> list = List.of(
                "k7/1Q6/8/8/8/8/8/7K b - - 0 1",
                "k7/1Q6/8/8/8/8/8/7K w - - 0 1",
                "k7/8/8/8/8/8/6q1/7K w - - 0 1",
                "k7/8/8/8/8/8/6q1/7K b - - 0 1"

        );

        int count = 0;
        for (String st : list) {
            count++;
//            System.out.println(count);
            Solver solver = new Solver();
            solver.setNumberOfSolutions(1);
            solver.setAdditionalDepth(0);
//            System.out.println(count);
            List<String> solutions = solver.solve(BoardBuilder.buildBoard(st), 1);
//            Assertions.assertEquals(0, solutions.size());
//            Assertions.assertTrue(solutions.stream().anyMatch(s -> s.contains("2b5/pp1p4/PR1P4/pqR2N2/2K5/2P5/1kP1PNP1/1nrnB3")));
        }
        // Theoretically, this can be solved by the algorithm
        // However, there is one solution out of thousands and it cannot be found in a timely manner

    }

    @Test
    public void ArbianKnights1() {
        //
        //pp3

        List<String> list = List.of(
                "8/8/8/1r1b4/B7/8/8/3k4 w - - 0 1"
        );

        int count = 0;
        for (String st : list) {
            count++;
//            System.out.println(count);

            for (int x = 0; x < 8; x++) {
                for (int y = 0; y < 8; y++) {
                    ChessBoard board = BoardBuilder.buildBoard(st);
                    Coordinate target = new Coordinate(x, y);
                    if (!board.at(target).getType().equals("null")) {
                        continue;
                    }
                    board.place(target, StandardPieceFactory.getInstance().getPiece("K"));
                    System.out.println(board.getReader().toFEN());

                    Solver solver = new Solver();
                    solver.setNumberOfSolutions(1);
                    solver.setAdditionalDepth(1);
                    List<String> solutions = solver.solve(BoardBuilder.buildBoard(board.getReader().toFEN()), 2);
                    if (board.getReader().toFEN().contains("8/8/8/1r1b4/B7/2K5/8/3k4")) {
                        Assertions.assertNotEquals(0, solutions.size());
                    } else {
                        Assertions.assertEquals(0, solutions.size());
                    }
                }
            }
        }
    }



//    @Test
//    public void ChessMysteries13() {
//        // pp63
//        List<String> list = List.of(
//                "r2qk1nr/pp2pppp/1pp5/5P2/8/6P1/PPPP2PP/R2QK1NR w - - 0 1",
//                "r2qk1nr/pp2pppp/1pp5/5P2/8/6P1/PPPP2PP/R2QK1NR b - - 0 1",
//                "r2qk1nr/pp2pppp/1pp5/8/5P2/6P1/PPPP2PP/R2QK1NR w - - 0 1",
//                "r2qk1nr/pp2pppp/1pp5/8/5P2/6P1/PPPP2PP/R2QK1NR b - - 0 1"
//                );
//
//        int count = 0;
//        for (String st : list) {
//            count++;
//            SolveAlgorithm.SolverV2 solver = new SolveAlgorithm.SolverV2();
//            solver.setNumberOfSolutions(3);
//            solver.setAdditionalDepth(1);
////            if (count == 1) {
//            List<String> solutions = solver.solve(BoardBuilder.buildBoard(st), 2);
////            Assertions.assertEquals(2, solutions.size());
////            Assertions.assertEquals(2, solutions.stream().anyMatch(s -> s.contains("2b5/pp1p4/PR1P4/pqR2N2/2K5/2P5/1kP1PNP1/1nrnB3")));
////            } else {
////                Assertions.assertNotEquals(0, solver.solve(BoardBuilder.buildBoard(st), 2).size());
////            }
//        }
//      // Timing, not last n
//
//    }

    // PP 66 is a question of timing, not last n moves
    //54, maybe




}
