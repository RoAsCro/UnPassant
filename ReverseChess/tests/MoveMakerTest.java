import StandardChess.BoardBuilder;
import StandardChess.ChessBoard;
import StandardChess.Coordinate;
import StandardChess.MoveMaker;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class MoveMakerTest {

    ChessBoard board = BoardBuilder.buildBoard("rnbqkbnr/pp1p1ppp/2p5/1Q1Pp1B1/8/2N5/PPP1PPPP/R3KBNR w KQkq - 0 1");

    @Test
    public void tryMakeMove() {
        MoveMaker moveMaker = new MoveMaker(board);
        Coordinate target = new Coordinate(0, 3);
        moveMaker.makeMove(new Coordinate(2, 2), target);
        Assertions.assertEquals(this.board.at(target).getType(), "knight");
    }

}
