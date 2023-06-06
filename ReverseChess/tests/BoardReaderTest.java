import StandardChess.*;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class BoardReaderTest {

    ChessBoard board = BoardBuilder.buildBoard();

    private void testFEN(String fen) {
        ChessBoard boardTwo = BoardBuilder.buildBoard(fen);
        Assertions.assertEquals(fen,
                boardTwo.getReader().toFEN());
    }
    @Test
    public void testToFENStandard() {
        Assertions.assertEquals("rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq -",
                this.board.getReader().toFEN());
    }

    @Test
    public void testToFENSpaces() {
        testFEN("r1bq2nr/pppppppp/3p4/8/8/8/PPPPPPPP/RNBQKBNR w KQkq -");
    }

    @Test
    public void testToFENCastling() {
        testFEN("r1bq2nr/pppppppp/3p4/8/8/8/PPPPPPPP/RNBQKBNR w Kk -");
    }

    @Test
    public void testToFENTurn() {
        testFEN("r1bq2nr/pppppppp/3p4/8/8/8/PPPPPPPP/RNBQKBNR b KQkq -");
    }

    @Test
    public void testToFENMissingEndingBug() {
        testFEN("1r1q1rk1/1b4b1/ppnpp1pp/5n2/3P1P2/P2BBNQ1/1P1N2PP/3R1RK1 w - -");
    }

    private void testCheck(String fen, boolean inCheck, Coordinate kingLocation) {
        ChessBoard boardTwo = BoardBuilder.buildBoard(fen);
        Assertions.assertEquals(inCheck, boardTwo.getReader().inCheck(kingLocation));
    }

    @Test
    public void testInCheckNot() {
        testCheck("rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq -", false, Coordinates.WHITE_KING);
        testCheck("rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq -", false, Coordinates.BLACK_KING);
    }

    @Test
    public void testInCheckBlocked() {
        testCheck("k7/8/8/8/8/8/6P1/7B", false, new Coordinate(0, 7));
    }

    @Test
    public void testInCheckOwnColour() {
        testCheck("k7/8/8/8/8/8/8/7b", false, new Coordinate(0, 7));
    }

    @Test
    public void testInCheckBishop() {
        testCheck("k7/8/8/8/8/8/8/7B", true, new Coordinate(0, 7));
        testCheck("k7/8/8/8/8/8/6B1/8", true, new Coordinate(0, 7));
    }

    @Test
    public void testInCheckRook() {
        testCheck("4K3/8/8/8/8/8/8/4r3", true, new Coordinate(4, 7));
        testCheck("8/8/1Kr5/8/8/8/8/8", true, new Coordinate(1, 5));
    }

    @Test
    public void testInCheckQueen() {
        testCheck("4K3/8/8/8/8/8/8/4q3", true, new Coordinate(4, 7));
        testCheck("8/8/1Kq5/8/8/8/8/8", true, new Coordinate(1, 5));
        testCheck("8/8/1k6/Q7/8/8/8/8", true, new Coordinate(1, 5));
        testCheck("4K3/5q2/8/8/8/8/8/8", true, new Coordinate(4, 7));
        testCheck("8/Q7/1k6/8/8/8/8/8", true, new Coordinate(1, 5));
        testCheck("8/2Q5/1k6/8/8/8/8/8", true, new Coordinate(1, 5));
    }

    @Test
    public void testInCheckKing() {
        testCheck("4K3/8/8/8/8/8/8/4k3", false, new Coordinate(4, 7));
        testCheck("8/8/1Kk5/8/8/8/8/8", true, new Coordinate(1, 5));
        testCheck("8/8/1k6/K7/8/8/8/8", true, new Coordinate(1, 5));
        testCheck("4K3/5k2/8/8/8/8/8/8", true, new Coordinate(4, 7));
        testCheck("8/K7/1k6/8/8/8/8/8", true, new Coordinate(1, 5));
        testCheck("8/2K5/1k6/8/8/8/8/8", true, new Coordinate(1, 5));
    }

    @Test
    public void testInCheckKnight() {
        testCheck("4K3/2n5/8/8/8/8/8/8", true, new Coordinate(4, 7));
        testCheck("4K3/6n1/8/8/8/8/8/8", true, new Coordinate(4, 7));
        testCheck("4K3/8/3n4/8/8/8/8/8", true, new Coordinate(4, 7));
        testCheck("4K3/8/5n2/8/8/8/8/8", true, new Coordinate(4, 7));
        testCheck("8/8/1n6/3K4/8/8/8/8", true, new Coordinate(3, 5));
        testCheck("8/8/5n2/3K4/8/8/8/8", true, new Coordinate(3, 5));
        testCheck("8/2n5/8/3K4/8/8/8/8", true, new Coordinate(3, 5));
        testCheck("8/4n3/8/3K4/8/8/8/8", true, new Coordinate(3, 5));
    }

    @Test
    public void testInCheckPawnTrue() {
        testCheck("8/8/1k6/P7/8/8/8/8", true, new Coordinate(1, 5));
        testCheck("8/8/1k6/2P5/8/8/8/8", true, new Coordinate(1, 5));
        testCheck("8/2p4/1K6/8/8/8/8/8", true, new Coordinate(1, 5));
        testCheck("8/p7/1K6/8/8/8/8/8", true, new Coordinate(1, 5));
    }
    @Test
    public void testInCheckPawnFalse() {
        testCheck("4k3/4P3/8/8/8/8/8/8", false, new Coordinate(4, 7));
        testCheck("8/1p6/1K6/8/8/8/8/8", false, new Coordinate(1, 5));

        testCheck("8/8/1K6/p7/8/8/8/8", false, new Coordinate(1, 5));
        testCheck("8/8/1K6/2p5/8/8/8/8", false, new Coordinate(1, 5));

        testCheck("8/1p6/8/1K6/8/8/8/8", false, new Coordinate(1, 4));

        testCheck("8/8/1k6/8/3P4/8/8/8", false, new Coordinate(1, 5));

        testCheck("8/8/8/Pk6/8/8/8/8 w KQkq b5", false, new Coordinate(1, 4));

    }

    @Test
    public void testNextWhileNoConsumer() {
        BoardReader reader = board.getReader();
        reader.to(new Coordinate(1, 0));
        reader.nextWhile(Coordinates.RIGHT, c -> !board.at(c).getType().equals("rook"));
        Assertions.assertEquals(new Coordinate(6, 0), reader.getCoord());
    }

    @Test
    public void testNextWhileStarting() {
        // Starting square fulfills the condition
        BoardReader reader = board.getReader();
        reader.to(new Coordinate(0, 0));
        reader.nextWhile(Coordinates.RIGHT, c -> !board.at(c).getType().equals("rook"));
        Assertions.assertEquals(new Coordinate(6, 0), reader.getCoord());
    }

//    @Test
//    public void testNextWhileOffBoard() {
//        // Starting square fulfills the condition
//        BoardReader reader = board.getReader();
//        reader.to(new Coordinate(0, 0));
//        reader.nextWhile(Coordinates.RIGHT, c -> !board.at(c).getType().equals("failing string"));
//        Assertions.assertEquals(new Coordinate(7, 0), reader.getCoord());
//    }


    // TODO test other functions
}
