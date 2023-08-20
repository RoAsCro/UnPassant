import StandardChess.BoardBuilder;
import StandardChess.ChessBoard;
import StandardChess.Coordinate;
import StandardChess.UnMoveMaker;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class MoveMakerTest {

    ChessBoard board = BoardBuilder.buildBoard("rnbqkbnr/pp1pppp1/2p5/1Q1P2B1/7p/2N1NB2/PPP1PPPP/R3K2R w KQkq - 0 1");

    @Test
    public void tryMakeMove() {
        MoveMaker moveMaker = new MoveMaker(board);
        Coordinate target = new Coordinate(0, 3);
        Coordinate origin = new Coordinate(2, 2);
        moveMaker.makeMove(origin, target);
        Assertions.assertEquals(this.board.at(target).getType(), "knight");
        Assertions.assertEquals(this.board.at(origin).getType(), "null");

    }

    @Test
    public void tryMakeMoveEmptySquare() {
        MoveMaker moveMaker = new MoveMaker(board);
        Coordinate target = new Coordinate(0, 1);
        Assertions.assertFalse(moveMaker.makeMove(new Coordinate(2, 2), target));
    }

    @Test
    public void tryEnPassant() {
        MoveMaker moveMaker = new MoveMaker(board);
        moveMaker.makeMove(new Coordinate(4, 6), new Coordinate(4, 4));
        Assertions.assertTrue(moveMaker.makeMove(new Coordinate(3, 4), new Coordinate(4, 5)));
        Assertions.assertEquals("null", this.board.at(new Coordinate(4, 4)).getType());

        moveMaker.makeMove(new Coordinate(6, 1), new Coordinate(6, 3));
        Assertions.assertTrue(moveMaker.makeMove(new Coordinate(7, 3), new Coordinate(6, 2)));
        Assertions.assertEquals("null", this.board.at(new Coordinate(6, 3)).getType());

    }

    @Test
    public void tryMakeMoveCastle() {
        MoveMaker moveMaker = new MoveMaker(board);
        Assertions.assertTrue(moveMaker.makeMove(new Coordinate(4, 0), new Coordinate(2, 0)));
        Assertions.assertEquals("rook", this.board.at(new Coordinate(3, 0)).getType());
        Assertions.assertEquals("null", this.board.at(new Coordinate(0, 0)).getType());

        this.board = BoardBuilder.buildBoard("rnbqkbnr/pp1pppp1/2p5/1Q1P2B1/7p/2N1NB2/PPP1PPPP/R3K2R w KQkq - 0 1");
        moveMaker = new MoveMaker(board);
        Assertions.assertTrue(moveMaker.makeMove(new Coordinate(4, 0), new Coordinate(6, 0)));
        Assertions.assertEquals("rook", this.board.at(new Coordinate(5, 0)).getType());
        Assertions.assertEquals("null", this.board.at(new Coordinate(7, 0)).getType());

    }

    @Test
    public void doubleMoveCastleEnPassant() {
        Coordinate origin = new Coordinate(5, 6);
        Coordinate target = new Coordinate(5, 4);
        ChessBoard board = BoardBuilder.buildBoard("r3k3/5p2/8/4P3/8/8/8/4K2R b Kq - 0 1");
        MoveMaker moveMaker = new MoveMaker(board);
        Assertions.assertTrue(moveMaker.makeMove(origin, target));

        Assertions.assertTrue(moveMaker.makeMove(new Coordinate(4, 0), new Coordinate(6, 0)));

        Assertions.assertTrue(moveMaker.makeMove(new Coordinate(4, 7), new Coordinate(2, 7)));


        Assertions.assertFalse(moveMaker.makeMove(new Coordinate(4, 4), new Coordinate(5, 5)));

    }

    @Test
    public void doubleMovePawnMoveEnPassant() {
        Coordinate origin = new Coordinate(5, 6);
        Coordinate target = new Coordinate(5, 4);
        ChessBoard board = BoardBuilder.buildBoard("3k4/5pp1/8/4P3/8/8/5P2/4K2R b K - 0 1");
        MoveMaker moveMaker = new MoveMaker(board);
        Assertions.assertTrue(moveMaker.makeMove(origin, target));

        Assertions.assertTrue(moveMaker.makeMove(new Coordinate(5, 1), new Coordinate(5, 2)));
        System.out.println(board.getReader().toFEN());

        Assertions.assertTrue(moveMaker.makeMove(new Coordinate(6, 6), new Coordinate(6, 5)));
        System.out.println(board.getReader().toFEN());

        Assertions.assertFalse(moveMaker.makeMove(new Coordinate(4, 4), new Coordinate(5, 5)),
                board.getReader().toFEN());
        System.out.println(board.getReader().toFEN());




    }

}
