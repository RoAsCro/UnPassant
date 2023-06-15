import Heuristics.BoardInterface;
import Heuristics.Path;
import Heuristics.Pathfinder;
import StandardChess.BoardBuilder;
import StandardChess.Coordinate;
import StandardChess.Coordinates;
import StandardChess.StandardPieceFactory;
import StandardChess.StandardPieces.StandardPiece;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.LinkedList;
import java.util.List;
import java.util.function.Function;

public class PathfinderTest {

    private static final Function<Path, Integer> PATH_DEVIATION = p -> p.stream()
            .reduce(new Coordinate(p.get(0).getX(), 0), (c, d) -> {
                if (c.getX() != d.getX()) {
                    return new Coordinate(d.getX(), c.getY() + 1);
                }
                return c;
            }).getY();

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
    void findPawnWhite() {
        BoardInterface board = new BoardInterface(BoardBuilder.buildBoard());
        Coordinate target = new Coordinate(2, 5);
        Path path = Pathfinder.findShortestPath(StandardPieceFactory.getInstance().getPiece("P"),
                new Coordinate(1,1),
                (b, c) -> c.equals(target),
                board);
        System.out.println(path);
        Assertions.assertEquals(5, path.size());

    }

    @Test
    void findPawnBlackWrongWay() {
        BoardInterface board = new BoardInterface(BoardBuilder.buildBoard());
        Coordinate target = new Coordinate(2, 5);
        Path path = Pathfinder.findShortestPath(StandardPieceFactory.getInstance().getPiece("p"),
                new Coordinate(1,1),
                (b, c) -> c.equals(target),
                board);
        System.out.println(path);
        Assertions.assertTrue(path.isEmpty());
    }

    @Test
    void findPawnBlack() {
        BoardInterface board = new BoardInterface(BoardBuilder.buildBoard());
        Coordinate target = new Coordinate(1, 1);
        Path path = Pathfinder.findShortestPath(StandardPieceFactory.getInstance().getPiece("p"),
                new Coordinate(5,6),
                (b, c) -> c.equals(target),
                board);
        System.out.println(path);
        Assertions.assertEquals(6, path.size());
    }

    @Test
    void findPawnTakeCondition() {
        BoardInterface board = new BoardInterface(BoardBuilder.buildBoard());
        Coordinate target = new Coordinate(2, 5);
//        List<Integer>  =
        int x = 4;
        Path path = Pathfinder.findShortestPath(StandardPieceFactory.getInstance().getPiece("P"),
                new Coordinate(x,1),
                (b, c) -> c.equals(target),
                board,
                p -> PATH_DEVIATION.apply(p) < 2
        );
        System.out.println(path);
        Assertions.assertEquals(0, path.size());
    }

    @Test
    void findPawnTakeConditionSucceed() {
        BoardInterface board = new BoardInterface(BoardBuilder.buildBoard());
        Coordinate target = new Coordinate(2, 5);
//        List<Integer>  =
        int x = 4;
        Path path = Pathfinder.findShortestPath(StandardPieceFactory.getInstance().getPiece("P"),
                new Coordinate(x,1),
                (b, c) -> c.equals(target),
                board,
                p -> PATH_DEVIATION.apply(p) < 4
        );
        System.out.println(path);
//        Assertions.assertEquals(0, path.size());
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

    @Test
    void testPathExclusivity() {
        Path path1 = new Path();
        for (int i = 1 ; i < 6 ; i++) {
            path1.add(new Coordinate(0, i));
        }

        Path path2 = new Path();
        for (int i = 6 ; i > 1 ; i--) {
            path2.add(new Coordinate(0, i));
        }
        Assertions.assertTrue(Pathfinder.pathsExclusive(path1, path2));
    }

    @Test
    void testPathExclusivityTwo() {
        Path path1 = new Path();
        for (int i = 1 ; i < 4 ; i++) {
            path1.add(new Coordinate(0, i));
        }
        path1.add(new Coordinate(1, 4));
        path1.add(new Coordinate(0, 5));

        Path path2 = new Path();
        for (int i = 6 ; i > 1 ; i--) {
            path2.add(new Coordinate(0, i));
        }
        Assertions.assertFalse(Pathfinder.pathsExclusive(path1, path2));
    }

    @Test
    void testPathExclusivityThree() {
        Path path1 = new Path();
        for (int i = 1 ; i < 6 ; i++) {
            path1.add(new Coordinate(0, i));
        }

        Path path2 = new Path();
        for (int i = 4 ; i > 1 ; i--) {
            path2.add(new Coordinate(0, i));
        }
        Assertions.assertTrue(Pathfinder.pathsExclusive(path1, path2));
    }

    @Test
    void testReductionCondition() {
        BoardInterface board = new BoardInterface(BoardBuilder.buildBoard());
        Coordinate target = new Coordinate(4, 3);
        Path path1 = Pathfinder.findShortestPath(new FakeWhitePawn(),
                new Coordinate(4,1),
                (b, c) -> c.equals(target),
                board,
                p -> PATH_DEVIATION.apply(p) < 4
        );
        Path path2 = Pathfinder.findShortestPath(StandardPieceFactory.getInstance().getPiece("P"),
                new Coordinate(4,1),
                (b, c) -> c.equals(target),
                board,
                p -> PATH_DEVIATION.apply(p) < 4,
                (p1, p2) -> PATH_DEVIATION.apply(p1) < PATH_DEVIATION.apply(p2)
        );
        Assertions.assertNotEquals(path1, path2);
        Assertions.assertTrue(PATH_DEVIATION.apply(path1) > 0);
        Assertions.assertEquals(0, (int) PATH_DEVIATION.apply(path2));

    }


    /**
     * FakeWhitePawn returns moves in a different order to a PawnStrategy, meaning that the paths
     * formed using it will prioritise diagonals. This allows the testing of reduction conditions.
     */
    private static class FakeWhitePawn extends StandardPiece {

        public FakeWhitePawn() {
            super("white", null);
        }
        @Override
        public Coordinate[] getMoves(Coordinate origin) {
            return new Coordinate[] {
            Coordinates.add(origin, Coordinates.UP_RIGHT), Coordinates.add(origin, Coordinates.UP_LEFT),
            Coordinates.add(origin, Coordinates.UP)};
        }
    }
}
