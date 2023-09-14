package ChessHeuristics;

import ChessHeuristics.SolverAlgorithm.Solver;
import ChessHeuristics.SolverAlgorithm.UnMoveConditions.UnMoveCondition;
import ReverseChess.StandardChess.BoardBuilder;
import ReverseChess.StandardChess.ChessBoard;
import ReverseChess.StandardChess.Coordinate;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.List;

public class SolverIndividualFunctionTest {

    public void makeMoveTest(String fen, Coordinate origin, Coordinate target, boolean pass, String piece,
                             boolean promotion) {
        makeMoveTest(fen, origin, target, pass, piece, promotion, false);

    }

    public void makeMoveTest(String fen, Coordinate origin, Coordinate target, boolean pass) {
        makeMoveTest(fen, origin, target, pass, "", false);
    }

    public void makeMoveTest(String fen, Coordinate origin, Coordinate target, boolean pass, String piece) {
        makeMoveTest(fen, origin, target, pass, piece, false);
    }

    public void makeMoveTest(String fen, Coordinate origin, Coordinate target, boolean pass, String piece,
                             boolean promotion, boolean enPassant) {
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

    @Test
    public void castleViolation3() {
        makeMoveTest("rnbqkbnr/ppppppp1/6p1/8/5Q2/8/PPPPPPP1/RNBQKB1R b KQkq - 0 1",
                new Coordinate(6, 7), new Coordinate(5, 5),
                false, "", false, false);
    }

    @Test
    public void castleViolation3CounterExample() {
        makeMoveTest("rnbqkbnr/ppppppp1/6p1/8/5Q2/8/PPPPPPP1/RNBQKB1R b KQq - 0 1",
                new Coordinate(6, 7), new Coordinate(5, 5),
                true, "", false, false);
    }
    @Test
    public void castleViolation3CounterExample3() {
        makeMoveTest("rnbqkbnr/ppppppp1/6p1/8/5Q2/8/PPPPPPP1/RNBQKB1R b KQk - 0 1",
                new Coordinate(6, 7), new Coordinate(5, 5),
                false, "", false, false);
    }

    @Test
    public void castleViolation3CounterExampl2e() {
        makeMoveTest("rnbqkbnr/ppppppp1/6p1/8/5Q2/8/PPPPPPP1/RNBQKB1R b KQ - 0 1",
                new Coordinate(6, 7), new Coordinate(5, 5),
                true, "", false, false);
    }

    @Test
    public void continueWhileInCheck() {
        Solver solver = new Solver(new UnMoveCondition(0, 99, '-', "-", "-", 'K',
                '-', "-", false));
        solver.setAdditionalDepth(0);
        solver.setNumberOfSolutions(3);
        List<String> solutions = solver.solve("k7/7R/8/8/8/7r/K7/RRRRRRR1 b - - 0 1", 3);
        System.out.println(solutions);
        Assertions.assertEquals(2, solutions.size());
        Assertions.assertTrue(solutions.get(0).contains("k7/7R/8/8/8/7r/1K6/RRRRRRR1")
                || solutions.get(0).contains("2k5/7R/8/8/8/7r/1K6/RRRRRRR1")
                && solutions.get(1).contains("k7/7R/8/8/8/7r/1K6/RRRRRRR1")
                || solutions.get(1).contains("2k5/7R/8/8/8/7r/1K6/RRRRRRR1"));
    }

    @Test
    public void continueWhileInCheckAdditionalDepth() {
        Solver solver = new Solver(new UnMoveCondition(0, 99, '-', "-", "-", 'K',
                '-', "-", false));
        solver.setAdditionalDepth(1);
        solver.setNumberOfSolutions(3);
        List<String> solutions = solver.solve("k7/7R/8/8/8/7r/K7/RRRRRRR1 b - - 0 1", 3);
        System.out.println(solutions);
        Assertions.assertEquals(2, solutions.size());
        Assertions.assertTrue(solutions.get(0).contains("k7/7R/8/8/8/7r/1K6/RRRRRRR1")
                || solutions.get(0).contains("2k5/7R/8/8/8/7r/1K6/RRRRRRR1")
                && solutions.get(1).contains("k7/7R/8/8/8/7r/1K6/RRRRRRR1")
                || solutions.get(1).contains("2k5/7R/8/8/8/7r/1K6/RRRRRRR1"));
    }

    @Test
    public void continueWhileEnPassant() {
        // Find en passant under regular circumstances
        UnMoveCondition enPassantFirstPly = new UnMoveCondition(0, 1, '-', "-", "-",
                '-', '-', "e.p.", false);
        Solver solver = new Solver(enPassantFirstPly);
        solver.setAdditionalDepth(0);
        solver.setNumberOfSolutions(3);
        List<String> solutions = solver.solve("k7/8/4P1K1/8/8/8/8/8 w - - 0 1", 1);
        System.out.println(solutions);
        Assertions.assertEquals(2, solutions.size());

        // Cannot double move away from having the opposing king in check
        solver = new Solver(enPassantFirstPly);
        solver.setAdditionalDepth(0);
        solver.setNumberOfSolutions(3);
        solutions = solver.solve("k7/8/4PK2/8/8/8/8/8 w - - 0 1", 1);
        System.out.println(solutions);
        Assertions.assertEquals(0, solutions.size());
    }

    @Test
    public void testUnCastling() {
        Solver solver = new Solver(p ->
                !(p.split(":")[1].contains("x")) && !(p.split(":")[1].startsWith("R")));
        solver.setAdditionalDepth(0);
        Assertions.assertTrue(solver.solve("5rk1/8/6K1/8/8/8/7P/8 b - - 0 1", 1)
                .stream().anyMatch(s -> s.contains("4k2r/8/6K1/8/8/8/7P/8")));

        solver = new Solver(p ->
                !(p.split(":")[1].contains("x")) && !(p.split(":")[1].startsWith("R")));
        solver.setAdditionalDepth(0);
        Assertions.assertTrue(solver.solve("2kr4/8/2K5/8/8/8/7P/8 b - - 0 1", 1)
                .stream().anyMatch(s -> s.contains("r3k3/8/2K5/8/8/8/7P/8 w")));
    }

    @Test
    public void castlingViolationTest() {
        Solver solver = new Solver(new UnMoveCondition(0, 99, '-', "-", "-", 'K',
                '-', "-", true));
        solver.setAdditionalDepth(0);
        solver.setNumberOfSolutions(1);
        Assertions.assertNotEquals(0, solver.solve("4k2r/8/8/8/4K3/8/8/r7 b k - 0 1", 1).size());
    }

    @Test
    public void castlingViolationTestTwo() {
        Solver solver = new Solver(new UnMoveCondition(0, 99, '-', "-", "-", 'K',
                '-', "-", true));
        solver.setAdditionalDepth(0);
        solver.setNumberOfSolutions(1);
        Assertions.assertNotEquals(0, solver.solve("4k2r/8/8/8/4K3/8/8/7r b k - 0 1", 1).size());
    }


}
