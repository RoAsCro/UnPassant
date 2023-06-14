import Heuristics.BoardInterface;
import Heuristics.Path;
import Heuristics.Pathfinder;
import StandardChess.BoardBuilder;
import StandardChess.Coordinate;
import StandardChess.StandardPieceFactory;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class PathfinderTest {

    @Test
    void findBishop() {
        BoardInterface board = new BoardInterface(BoardBuilder.buildBoard());
        Coordinate target = new Coordinate(4, 4);
        Path path = Pathfinder.findShortestPath(StandardPieceFactory.getInstance().getPiece("b"),
                new Coordinate(0,0),
                (b, c) -> c.equals(target),
                board);
        System.out.println(path);
        Assertions.assertEquals(5, path.size());
        Assertions.assertEquals(target, path.get(4));
    }

    @Test
    void findBishopFail() {
        BoardInterface board = new BoardInterface(BoardBuilder.buildBoard());
        Coordinate target = new Coordinate(4, 3);
        Path path = Pathfinder.findShortestPath(StandardPieceFactory.getInstance().getPiece("b"),
                new Coordinate(0,0),
                (b, c) -> c.equals(target),
                board);
        System.out.println(path);
    }
}
