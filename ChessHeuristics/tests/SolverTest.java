import StandardChess.BoardBuilder;
import StandardChess.Coordinate;
import org.junit.Assert;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class SolverTest {

    public void makeMoveTest(String fen, Coordinate origin, Coordinate target, boolean pass, String piece) {
        Assertions.assertEquals(pass, new Solver().makeMove(BoardBuilder.buildBoard(fen),
                origin, target, piece));
    }

    public void makeMoveTest(String fen, Coordinate origin, Coordinate target, boolean pass) {
        makeMoveTest(fen, origin, target, pass, "");
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


}
