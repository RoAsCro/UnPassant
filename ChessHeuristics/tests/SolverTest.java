import StandardChess.BoardBuilder;
import StandardChess.ChessBoard;
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
                    SolverImpossibleStateDetector detector = StateDetectorFactory.getDetector(string.split(":")[0]);
                    detector.testState();
                    return detector.canCastle(false);
                });
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
                solver = new Solver(string ->{
                    SolverImpossibleStateDetector detector = StateDetectorFactory.getDetector(string.split(":")[0]);
                    detector.testState();
                    return detector.canCastle(false);
                });
                solver.setNumberOfSolutions(1);
                solver.setAdditionalDepth(2);
                b = BoardBuilder.buildBoard("r3k3/ppp3pp/N5p1/P2Kp2P/2B5/p5P1/PP3PP1/R7 w - - 0 1");
                b.setTurn(b.getTurn().equals("white") ? "black" : "white");
                Assertions.assertNotEquals(0, solver.solve(b, 2).size());
                System.out.println("Finished 3");

            }
        }
        // The original puzzle is actually created incorrectly - with the knight moved as the last white move,
        // the pawn can move more freely than the apparent solution allows for
        // In the above, the first tests an amended version of the puzzle, with an extra check that black can castle
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
        Solver solver = new Solver(p -> {
            SolverImpossibleStateDetector detector = StateDetectorFactory.getDetector(p.split(":")[0]);
            detector.testState();
            return detector.canCastle(false);
        });
        solver.setNumberOfSolutions(1);
        solver.setNumberOfSolutions(2);
        solver.setAdditionalDepth(2);
        Assertions.assertNotEquals(0, solver.solve(BoardBuilder.buildBoard("r1b1k2r/p1p1p1p1/1p3p1p/8/8/P7/1PPPPPPP/2BQKB2 w - - 0 1"), 2).size());

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
        Assertions.assertEquals(0, solver.solve(BoardBuilder.buildBoard("r1b1k2r/p1p1p1p1/1p3p1p/8/8/P7/1PPPPPPP/2BQKB2 w - - 0 1"), 3).size());

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
        Assertions.assertTrue((solutions.get(0).contains("4k2r/8/8/2p5/8/5P2/2P2PP1/3bK2R") || solutions.get(0).contains("4k2r/8/8/8/2p5/5P2/2P1bPP1/4K2R"))
        && (solutions.get(1).contains("4k2r/8/8/2p5/8/5P2/2P2PP1/3bK2R") || solutions.get(1).contains("4k2r/8/8/8/2p5/5P2/2P1bPP1/4K2R"))) ;

        solver = new Solver(
                s -> {
                    int depth = Integer.parseInt(s.split(":")[2]);
                    return depth >= 2 || !s.split(":")[1].contains("x");
                }
        );
        solver.setNumberOfSolutions(1);
        solver.setAdditionalDepth(1);
        Assertions.assertEquals(0, solver.solve(BoardBuilder.buildBoard("4k2r/8/8/8/2p5/5P2/2P2PP1/3b1RK1 w k - 0 1"), 2).size());

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
        solver.setAdditionalDepth(2);
        Assertions.assertEquals(1, solver.solve(BoardBuilder.buildBoard("k2q3r/R1B4p/R4p2/K2B2p1/1P2n1P1/P7/2P5/r4B2 w - - 0 1"), 1).size());
        // This is entirely a matter of iterating through valid moves
    }

    // 54 cannot be done as it is a question of timing
    @Test
    public void ChessMysteries9() {
        // pp54
        List<String> list = List.of(
                "r3kr2/1pp2p2/1pn2npP/1Q1pp3/1b6/2N2NPP/1PPP1P2/R3K1R1 w q - 0 1",
                "r3kr2/1pp2p2/1pn2npP/1Q1pp3/1b6/2N2NPP/1PPP1P2/R3K1R1 w Q - 0 1",
                "r3kr2/1pp2p2/1pn2npP/1Q1pp3/1b6/2N2NPP/1PPP1P2/R3K1R1 b q - 0 1",
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
            solver.setAdditionalDepth(1);
            solver.solve(BoardBuilder.buildBoard(st), 1);
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
    public void ChessMysteries11() {
        // pp58
        List<String> list = List.of(
                "2b5/pp1p4/PRP5/pR3N2/2K5/2P5/2k1PNP1/1nrnB3 b - - 0 1",
                "2b5/pp1p4/PRP5/pR3N2/2K5/2P5/2k1PN1P/1nrnB3 b - - 0 1"
        );

        int count = 0;
        for (String st : list) {
            count++;
            Solver solver = new Solver( s ->{
//                String move = s.split(":")[1];
//                int xY = (Integer.parseInt(move.substring(move.length() - 1)) - 1) + (((int) move.charAt(move.length() - 2)) - 97);
//                if (s.split(":")[1].contains("x") && (s.split(":")[1].charAt(4) == 'N' || s.split(":")[1].charAt(4) == 'R'  ||
//                        (s.split(":")[1].contains("w") && s.split(":")[1].charAt(4) == 'B' && xY % 2 == 0))) {
//                    return false;
//                }
//                System.out.println(s);
                return !(s.split(":")[1].charAt(0) == 'P'
                        && (s.split(":")[1].endsWith("1")  || s.split(":")[1].endsWith("8")));

            },
                    d -> {
//                if (d.getPromotions().values().stream().flatMap(List::stream).toList().isEmpty()) {
//                    System.out.println(d.getPromotions().values().stream().flatMap(List::stream).toList());
//                }
                return d.getPromotions().values().stream().flatMap(List::stream).toList().isEmpty();
                    }
            );
            solver.setNumberOfSolutions(1);
            solver.setAdditionalDepth(1);
            if (count == 1) {
                Assertions.assertEquals(0, solver.solve(BoardBuilder.buildBoard(st), 2).size());
            } else {
                Assertions.assertNotEquals(0, solver.solve(BoardBuilder.buildBoard(st), 2).size());
            }
        }
        
    }

    @Test
    public void ChessMysteries12() {
        // pp63
        List<String> list = List.of(
                "2b5/pp1p4/PR1P4/pR3N2/2K5/2P5/2k1PNP1/1nrnB3 b - - 0 1");

        int count = 0;
        for (String st : list) {
            count++;
            Solver solver = new Solver(
                    s ->{
//                String move = s.split(":")[1];
//                int xY = (Integer.parseInt(move.substring(move.length() - 1)) - 1) + (((int) move.charAt(move.length() - 2)) - 97);
//                if (s.split(":")[1].contains("x") && (s.split(":")[1].charAt(4) == 'N' || s.split(":")[1].charAt(4) == 'R'  ||
//                        (s.split(":")[1].contains("w") && s.split(":")[1].charAt(4) == 'B' && xY % 2 == 0))) {
//                    return false;
//                }
//                System.out.println(s);
                return !(s.split(":")[1].charAt(0) == 'P'
                        && (s.split(":")[1].endsWith("1")  || s.split(":")[1].endsWith("8")));

            },
                    d -> {
//                if (d.getPromotions().values().stream().flatMap(List::stream).toList().isEmpty()) {
//                    System.out.println(d.getPromotions().values().stream().flatMap(List::stream).toList());
//                }
                        return d.getPromotions().values().stream().flatMap(List::stream).toList().isEmpty();
                    }
            );
            solver.setNumberOfSolutions(3);
            solver.setAdditionalDepth(1);
//            if (count == 1) {
            List<String> solutions = solver.solve(BoardBuilder.buildBoard(st), 2);
                Assertions.assertEquals(2, solutions.size());
            Assertions.assertTrue(solutions.stream().anyMatch(s -> s.contains("2b5/pp1p4/PR1P4/pqR2N2/2K5/2P5/1kP1PNP1/1nrnB3")));
//            } else {
//                Assertions.assertNotEquals(0, solver.solve(BoardBuilder.buildBoard(st), 2).size());
//            }
        }
        // For the above, an additional solution is discovered
        // With it set to 3 solutions, the two it finds must be the only solutions

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
//            Assertions.assertTrue(solutions.stream().anyMatch(s -> s.contains("2b5/pp1p4/PR1P4/pqR2N2/2K5/2P5/1kP1PNP1/1nrnB3")));
        }
        // This is a question of iterating
        // Finding that the positions are possible

    }

    @Test
    public void ChessMysteries15() {
        // pp78
        List<String> list = List.of(
                "r3k3/8/8/8/8/8/5PP1/6bK b Q - 0 1",
                "r3k3/8/8/8/8/8/5PP1/6bK w Q - 0 1");

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
        // pp89
        List<String> list = List.of(
//                "r3k2r/pbpp1ppp/2n4n/4p1q1/1b6/8/1PPPPPPP/1NBQKBNR w - - 0 1",
//                "r3k2r/pbpp1ppp/2n4n/4p1q1/1b6/8/1PPPPPPP/1NBQKBNR w - - 0 1",
//                "r3k2r/pbpp1ppp/2n4n/4p1q1/1b6/8/1PPPPPPP/1NBQKBNR w - - 0 1",
//                "r3k2r/pbpp1ppp/2n4n/4p1q1/1b6/8/1PPPPPPP/1NBQKBNR w - - 0 1",
//                "r3k2r/pbpp1ppp/2n4n/4p1q1/1b6/8/1PPPPPPP/1NBQKBNR w - - 0 1",
//                "r3k2r/pbpp1ppp/2n4n/4p1q1/1b6/8/1PPPPPPP/1NBQKBNR w - - 0 1",
//                "r3k2r/pbpp1ppp/2n4n/4p1q1/1b6/8/1PPPPPPP/1NBQKBNR w - - 0 1",
//                "r3k2r/pbpp1ppp/2n4n/4p1q1/1b6/8/1PPPPPPP/1NBQKBNR w - - 0 1",
//                "r3k2r/pbpp1ppp/2n4n/4p1q1/1b6/8/1PPPPPPP/1NBQKBNR w - - 0 1",
//                "r3k2r/pbpp1ppp/2n4n/4p1q1/1b6/8/1PPPPPPP/1NBQKBNR w - - 0 1",
//                "r3k2r/pbpp1ppp/2n4n/4p1q1/1b6/8/1PPPPPPP/1NBQKBNR w - - 0 1",
//                "r3k2r/pbpp1ppp/2n4n/4p1q1/1b6/8/1PPPPPPP/1NBQKBNR w - - 0 1",
//                "r3k2r/pbpp1ppp/2n4n/4p1q1/1b6/8/1PPPPPPP/1NBQKBNR w - - 0 1",
//                "r3k2r/pbpp1ppp/2n4n/4p1q1/1b6/8/1PPPPPPP/1NBQKBNR w - - 0 1",
//                "r3k2r/pbpp1ppp/2n4n/4p1q1/1b6/8/1PPPPPPP/1NBQKBNR w - - 0 1",
//                "r3k2r/pbpp1ppp/2n4n/4p1q1/1b6/8/1PPPPPPP/1NBQKBNR w - - 0 1",
//                "r3k2r/pbpp1ppp/2n4n/4p1q1/1b6/8/1PPPPPPP/1NBQKBNR w - - 0 1",
//                "r3k2r/pbpp1ppp/2n4n/4p1q1/1b6/8/1PPPPPPP/1NBQKBNR w - - 0 1",
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
//                "r3k2r/pbpp1ppp/2n4n/4p1q1/1b6/8/1PPPPPPP/1NBQKBNR b Kkq - 0 1"
//                "r1b1k2r/pppp1ppp/P1n4n/4p1q1/1b6/8/1PPPPPPP/1NBQKBNR b Kkq - 0 1",
//                "r1b1k2r/pppp1ppp/P1n4n/4p1q1/1b6/8/1PPPPPPP/1NBQKBNR b Kkq - 0 1",
//                "r1b1k2r/pppp1ppp/P1n4n/4p1q1/1b6/8/1PPPPPPP/1NBQKBNR b Kkq - 0 1",
//                "r1b1k2r/pppp1ppp/P1n4n/4p1q1/1b6/8/1PPPPPPP/1NBQKBNR b Kkq - 0 1",
//                "r1b1k2r/pppp1ppp/P1n4n/4p1q1/1b6/8/1PPPPPPP/1NBQKBNR b Kkq - 0 1",
//                "r1b1k2r/pppp1ppp/P1n4n/4p1q1/1b6/8/1PPPPPPP/1NBQKBNR b Kkq - 0 1",
//                "r1b1k2r/pppp1ppp/P1n4n/4p1q1/1b6/8/1PPPPPPP/1NBQKBNR b Kkq - 0 1",
//                "r1b1k2r/pppp1ppp/P1n4n/4p1q1/1b6/8/1PPPPPPP/1NBQKBNR b Kkq - 0 1",
//                "r1b1k2r/pppp1ppp/P1n4n/4p1q1/1b6/8/1PPPPPPP/1NBQKBNR b Kkq - 0 1",
//                "r1b1k2r/pppp1ppp/P1n4n/4p1q1/1b6/8/1PPPPPPP/1NBQKBNR b Kkq - 0 1",
//                "r1b1k2r/pppp1ppp/P1n4n/4p1q1/1b6/8/1PPPPPPP/1NBQKBNR b Kkq - 0 1",
//                "r1b1k2r/pppp1ppp/P1n4n/4p1q1/1b6/8/1PPPPPPP/1NBQKBNR b Kkq - 0 1",
//                "r1b1k2r/pppp1ppp/P1n4n/4p1q1/1b6/8/1PPPPPPP/1NBQKBNR b Kkq - 0 1",
//                "r1b1k2r/pppp1ppp/P1n4n/4p1q1/1b6/8/1PPPPPPP/1NBQKBNR b Kkq - 0 1",
//                "r1b1k2r/pppp1ppp/P1n4n/4p1q1/1b6/8/1PPPPPPP/1NBQKBNR b Kkq - 0 1",
//                "r1b1k2r/pppp1ppp/P1n4n/4p1q1/1b6/8/1PPPPPPP/1NBQKBNR b Kkq - 0 1",
//                "r1b1k2r/pppp1ppp/P1n4n/4p1q1/1b6/8/1PPPPPPP/1NBQKBNR b Kkq - 0 1"
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
                return d.getPromotions().values().stream().flatMap(List::stream).toList().isEmpty();
            }
            );
            solver.setNumberOfSolutions(1);
            solver.setAdditionalDepth(1);
//            System.out.println(count);
            List<String> solutions = solver.solve(BoardBuilder.buildBoard(st), count);
//            Assertions.assertEquals(0, solutions.size());
//            Assertions.assertTrue(solutions.stream().anyMatch(s -> s.contains("2b5/pp1p4/PR1P4/pqR2N2/2K5/2P5/1kP1PNP1/1nrnB3")));
        }
        // Theoretically, this can be solved by the algorithm
        // However, there is one solution out of thousands and it cannot be found in a timely manner

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
//            Solver solver = new Solver();
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
    @Test
    public void testUnCastling() {
        Solver solver = new Solver(p ->
            !(p.split(":")[1].contains("x")) && !(p.split(":")[1].startsWith("R")));
        solver.setAdditionalDepth(0);
        Assertions.assertTrue(solver.solve(BoardBuilder.buildBoard("5rk1/8/6K1/8/8/8/7P/8 b - - 0 1"), 1)
                .stream().anyMatch(s -> s.contains("4k2r/8/6K1/8/8/8/7P/8")));

        solver = new Solver(p ->
                !(p.split(":")[1].contains("x")) && !(p.split(":")[1].startsWith("R")));
        solver.setAdditionalDepth(0);
        Assertions.assertTrue(solver.solve(BoardBuilder.buildBoard("2kr4/8/2K5/8/8/8/7P/8 b - - 0 1"), 1)
                .stream().anyMatch(s -> s.contains("r3k3/8/2K5/8/8/8/7P/8 w")));
    }


//    @Test
//    public void test() {
//        // Simple
//        new Solver().solve(BoardBuilder.buildBoard("4Q3/8/8/8/8/8/1rk5/K7 w - - 0 1"), 1);
//    }
//
//    @Test
//    public void test2() {
//        // Simple
//        SolverImpossibleStateDetector detector = StateDetectorFactory.getDetector("rnbqkbnr/pppppppp/8/8/5P2/7N/PPPPP1PP/RNBQKB1R w KQkq - 0 1");
//        detector.testState();
//        SolverImpossibleStateDetector detectorTwo = DetectorUpdater.update(BoardBuilder.buildBoard("rnbqkbnr/pppppppp/8/8/8/7N/PPPPPPPP/RNBQKB1R"), "rnbqkbnr/pppppppp/8/8/8/7N/PPPPPPPP/RNBQKB1R b :Pf2-f4", detector);
//        System.out.println(((PawnMap) detectorTwo.getDeductions().get(1)).getPawnOrigins());
//        System.out.println(((PawnMap) detectorTwo.getDeductions().get(2)).getPawnOrigins());
//        System.out.println(((CombinedPawnMap) detectorTwo.getDeductions().get(3)).getWhitePaths());
//        System.out.println(((CombinedPawnMap) detectorTwo.getDeductions().get(3)).getBlackPaths());
//
//
//
//        detector = StateDetectorFactory.getDetector("rnbqkbnr/pppppppp/8/8/5P2/7N/PPPPP1PP/RNBQKB1R w KQkq - 0 1");
//        detector.testState();
//        detectorTwo = DetectorUpdater.update(BoardBuilder.buildBoard("rnbqkbnr/pppppppp/8/8/8/7N/PPPPPPPP/RNBQKB1R"), "rnbqkbnr/pppppppp/8/8/5P2/8/PPPPPNPP/RNBQKB1R b :Rf2-f4", detector);
//        System.out.println(((PawnMap) detectorTwo.getDeductions().get(1)).getPawnOrigins());
//        System.out.println(((PawnMap) detectorTwo.getDeductions().get(2)).getPawnOrigins());
//        System.out.println(((CombinedPawnMap) detectorTwo.getDeductions().get(3)).getWhitePaths());
//        System.out.println(((CombinedPawnMap) detectorTwo.getDeductions().get(3)).getBlackPaths());
//
//
//        System.out.println("-------------------");
//        detector = StateDetectorFactory.getDetector("rnbqkb1r/ppppp1pp/4n3/8/5PN1/8/PPPPP1PP/RNBQKB1R b KQkq - 0 1");
//        detector.testState();
//        detectorTwo = DetectorUpdater.update(BoardBuilder.buildBoard("rnbqkbnr/pppppppp/8/8/8/7N/PPPPPPPP/RNBQKB1R"),"rnbqkb1r/ppppp1pp/8/2n5/5PN1/8/PPPPP1PP/RNBQKB1R w :Rc5-b2", detector);
//        System.out.println(((PawnMap) detectorTwo.getDeductions().get(1)).getPawnOrigins());
//        System.out.println(((PawnMap) detectorTwo.getDeductions().get(2)).getPawnOrigins());
//        System.out.println(((CombinedPawnMap) detectorTwo.getDeductions().get(3)).getWhitePaths());
//        System.out.println(((CombinedPawnMap) detectorTwo.getDeductions().get(3)).getBlackPaths());
//
//        System.out.println("-------------------");
//
//        detector = StateDetectorFactory.getDetector("rnbqkb1r/ppp1p1pp/3p4/2n5/5PN1/8/PPPPP1PP/RNBQKB1R b KQkq - 0 1");
//        detector.testState();
//        detectorTwo = DetectorUpdater.update(BoardBuilder.buildBoard("rnbqkbnr/pppppppp/8/8/8/7N/PPPPPPPP/RNBQKB1R"),"rnbqkb1r/ppppp1pp/8/2n5/5PN1/8/PPPPP1PP/RNBQKB1R w :Pd6-d7", detector);
//        System.out.println(((PawnMap) detectorTwo.getDeductions().get(1)).getPawnOrigins());
//        System.out.println(((PawnMap) detectorTwo.getDeductions().get(2)).getPawnOrigins());
//        System.out.println(((CombinedPawnMap) detectorTwo.getDeductions().get(3)).getWhitePaths());
//        System.out.println(((CombinedPawnMap) detectorTwo.getDeductions().get(3)).getBlackPaths());
//
//        System.out.println("-------------------");
//
//        detector = StateDetectorFactory.getDetector("rnbq1bnr/pppppkpp/5N2/8/5P2/8/PPPPP1PP/RNBQKB1R w - - 0 1");
//        detector.testState();
//        detectorTwo = DetectorUpdater.update(BoardBuilder.buildBoard("rnbqkbnr/pppppppp/8/8/8/7N/PPPPPPPP/RNBQKB1R"), "rnbqkbnr/ppppp1pp/5p2/8/5PN1/8/PPPPP1PP/RNBQKB1R b :Rf6xPg4", detector);
//        System.out.println(((PawnMap) detectorTwo.getDeductions().get(1)).getPawnOrigins());
//        System.out.println(((PawnMap) detectorTwo.getDeductions().get(2)).getPawnOrigins());
//        System.out.println(((CombinedPawnMap) detectorTwo.getDeductions().get(3)).getWhitePaths());
//        System.out.println(((CombinedPawnMap) detectorTwo.getDeductions().get(3)).getBlackPaths());
//
//        System.out.println("!-------------------!");
//
//        detector = StateDetectorFactory.getDetector("rnbq1bnr/pppppkpp/5N2/8/5P2/8/PPPPP1PP/RNBQKB1R b KQkq - 0 1");
//        detector.testState();
//        detectorTwo = DetectorUpdater.update(BoardBuilder.buildBoard("rnbqkbnr/pppppppp/8/8/8/7N/PPPPPPPP/RNBQKB1R"), "rnbqkbnr/ppppp1pp/5p2/8/5PN1/8/PPPPP1PP/RNBQKB1R w :Rf6xPg4", detector);
//        System.out.println(((PawnMap) detectorTwo.getDeductions().get(1)).getPawnOrigins());
//        System.out.println(((PawnMap) detectorTwo.getDeductions().get(2)).getPawnOrigins());
//        System.out.println(((CombinedPawnMap) detectorTwo.getDeductions().get(3)).getWhitePaths());
//        System.out.println(((CombinedPawnMap) detectorTwo.getDeductions().get(3)).getBlackPaths());
//
//        System.out.println("!-------------------!");
//
//        detector = StateDetectorFactory.getDetector("rnbq1bnr/pppppkpp/5N2/8/5P2/8/PPPPP1PP/RNBQKB1R b KQkq - 0 1");
//        detector.testState();
//        detectorTwo = DetectorUpdater.update(BoardBuilder.buildBoard("rnbqkbnr/pppppppp/8/8/8/7N/PPPPPPPP/RNBQKB1R"), "rnbqkbnr/ppppp1pp/5p2/8/5PN1/8/PPPPP1PP/RNBQKB1R w :Rf6xBg4", detector);
//        System.out.println(((PawnMap) detectorTwo.getDeductions().get(1)).getPawnOrigins());
//        System.out.println(((PawnMap) detectorTwo.getDeductions().get(2)).getPawnOrigins());
//        System.out.println(((CombinedPawnMap) detectorTwo.getDeductions().get(3)).getWhitePaths());
//        System.out.println(((CombinedPawnMap) detectorTwo.getDeductions().get(3)).getBlackPaths());
//
//        System.out.println("!-------------------!");
//
//        detector = StateDetectorFactory.getDetector("rnbq1bnr/pppppkpp/5N2/8/5P2/8/PPPPP1PP/RNBQKB1R w KQkq - 0 1");
//        detector.testState();
//        detectorTwo = DetectorUpdater.update(BoardBuilder.buildBoard("rnbqkbnr/pppppppp/8/8/8/7N/PPPPPPPP/RNBQKB1R"), "rnbqkbnr/ppppp1pp/5p2/8/5PN1/8/PPPPP1PP/RNBQKB1R b :Rf6xBg4", detector);
//        System.out.println(((PawnMap) detectorTwo.getDeductions().get(1)).getPawnOrigins());
//        System.out.println(((PawnMap) detectorTwo.getDeductions().get(2)).getPawnOrigins());
//        System.out.println(((CombinedPawnMap) detectorTwo.getDeductions().get(3)).getWhitePaths());
//        System.out.println(((CombinedPawnMap) detectorTwo.getDeductions().get(3)).getBlackPaths());
//
//
//    }


}
