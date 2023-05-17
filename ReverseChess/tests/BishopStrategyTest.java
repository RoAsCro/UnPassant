import StandardChess.Coordinate;
import StandardChess.Piece;
import StandardChess.StandardPieceFactory;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class BishopStrategyTest {

    @Test
    public void testTryMove() {
        Coordinate origin = new Coordinate(4, 4);
        Piece piece = StandardPieceFactory.getInstance().getPiece("b");
        Assertions.assertTrue(piece.tryMove(origin, new Coordinate(5, 5)));
        Assertions.assertTrue(piece.tryMove(origin, new Coordinate(3, 3)));
        Assertions.assertTrue(piece.tryMove(origin, new Coordinate(3, 5)));
        Assertions.assertTrue(piece.tryMove(origin, new Coordinate(6, 2)));
    }

}
