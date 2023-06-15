import Heuristics.BoardInterface;
import Heuristics.Path;
import Heuristics.Pathfinder;
import StandardChess.BoardBuilder;
import StandardChess.Coordinate;
import StandardChess.StandardPieceFactory;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.LinkedList;
import java.util.List;

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
    void findBishopTwo() {
        BoardInterface board = new BoardInterface(BoardBuilder.buildBoard());
        Coordinate target = new Coordinate(6, 1);
        Path path = Pathfinder.findShortestPath(StandardPieceFactory.getInstance().getPiece("b"),
                new Coordinate(1,2),
                (b, c) -> c.equals(target),
                board);
        System.out.println(path);
        Assertions.assertEquals(6, path.size());
        Assertions.assertEquals(target, path.get(5));
    }

    @Test
    void findBishopFail() {
        BoardInterface board = new BoardInterface(BoardBuilder.buildBoard());
        Coordinate target = new Coordinate(4, 3);
        Path path = Pathfinder.findShortestPath(StandardPieceFactory.getInstance().getPiece("b"),
                new Coordinate(0,0),
                (b, c) -> c.equals(target),
                board);
        Assertions.assertTrue(path.isEmpty());
    }

    @Test
    void findPawn() {
        BoardInterface board = new BoardInterface(BoardBuilder.buildBoard());
        Coordinate target = new Coordinate(2, 5);
        Path path = Pathfinder.findShortestPath(StandardPieceFactory.getInstance().getPiece("P"),
                new Coordinate(1,1),
                (b, c) -> c.equals(target),
                board);
        System.out.println(path);
    }

    @Test
    void findEveryTileTest() {
        // Tests that no duplicates are found
        BoardInterface board = new BoardInterface(BoardBuilder.buildBoard());
        List<Coordinate> list = new LinkedList<>();
        Path path = Pathfinder.findFirstPath(StandardPieceFactory.getInstance().getPiece("k"),
                new Coordinate(0,0),
                (b, c) -> {list.add(c);
                            return list.size() == 64;},
                board);
        Assertions.assertEquals(path.size(), path.stream().distinct().toList().size());
    }
}
