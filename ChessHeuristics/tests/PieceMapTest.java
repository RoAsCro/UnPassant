import Heuristics.BoardInterface;
import Heuristics.Deductions.CombinedPawnMap;
import Heuristics.Deductions.PawnMapBlack;
import Heuristics.Deductions.PawnMapWhite;
import Heuristics.Deductions.PieceMap;
import Heuristics.Path;
import StandardChess.BoardBuilder;
import StandardChess.Coordinate;
import StandardChess.StandardBoard;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

public class PieceMapTest {
    PawnMapWhite pawnMapWhite;
    PawnMapBlack pawnMapBlack;
    CombinedPawnMap combinedPawnMap;
    PieceMap pieceMap;

    @BeforeEach
    void setup() {
        pawnMapWhite = new PawnMapWhite();
        pawnMapBlack = new PawnMapBlack();
        combinedPawnMap = new CombinedPawnMap(pawnMapWhite, pawnMapBlack);
        pieceMap = new PieceMap(combinedPawnMap);

    }

    @Test
    void testBishopAtStartLocation() {
        BoardInterface boardInterface = new BoardInterface(BoardBuilder.buildBoard());
        this.pieceMap.deduce(boardInterface);
        Map<Coordinate, List<Path>> map = this.pieceMap.getStartLocations();
        Coordinate start1 = new Coordinate(2, 0);
        Coordinate start2 = new Coordinate(5, 0);

        System.out.println(this.pieceMap.getStartLocations());

        Assertions.assertEquals(1, map.get(start1).size());
        Assertions.assertEquals(1, map.get(start2).size());
        Path path = Path.of(start1);
        Assertions.assertEquals(path, map.get(start1).get(0));
        Assertions.assertEquals(Path.of(start2), map.get(start2).get(0));
    }

    @Test
    void testBishopAtNotStartLocation() {
        BoardInterface boardInterface = new BoardInterface(BoardBuilder.buildBoard("rnbqkbnr/pppppppp/8/2B5/8/2PPP3/PP2BPPP/RN1QK1NR w KQkq - 0 1"));
        this.pieceMap.deduce(boardInterface);
        Map<Coordinate, List<Path>> map = this.pieceMap.getStartLocations();
        Coordinate start1 = new Coordinate(2, 0);
        Coordinate start2 = new Coordinate(5, 0);

        System.out.println(this.pieceMap.getStartLocations());

        Assertions.assertEquals(1, map.get(start1).size());
        Assertions.assertEquals(1, map.get(start2).size());
        Assertions.assertTrue(map.get(start1).get(0).contains(new Coordinate(3, 1)));
        Assertions.assertEquals(Path.of(start2, new Coordinate(4, 1)), map.get(start2).get(0));
    }

    @Test
    void testBishopNotCollidingWithPawns() {
        BoardInterface boardInterface = new BoardInterface(BoardBuilder.buildBoard("rnbqkbnr/pppppppp/8/2B2B2/8/P6P/P1PPPP1P/RN1QK1NR w KQkq - 0 1"));
        this.pieceMap.deduce(boardInterface);
        Map<Coordinate, List<Path>> map = this.pieceMap.getStartLocations();
        Coordinate start1 = new Coordinate(2, 0);
        Coordinate start2 = new Coordinate(5, 0);

        System.out.println(this.pieceMap.getStartLocations());

        Assertions.assertEquals(Path.of(start1, new Coordinate(1, 1), new Coordinate(2, 2)), map.get(start1).get(0));
        Assertions.assertEquals(Path.of(start2, new Coordinate(6, 1), new Coordinate(5, 2)), map.get(start2).get(0));
    }

    @Test
    void testBishopNotCollidingWithPawnsTwo() {
        BoardInterface boardInterface = new BoardInterface(BoardBuilder.buildBoard("rnbqkbnr/pppppppp/8/2B2B2/8/P1P2P1P/P2PP2P/RN1QK1NR w KQkq - 0 1"));
        this.pieceMap.deduce(boardInterface);
        Map<Coordinate, List<Path>> map = this.pieceMap.getStartLocations();
        Coordinate start1 = new Coordinate(2, 0);
        Coordinate start2 = new Coordinate(5, 0);

        System.out.println(this.pieceMap.getStartLocations());

        Assertions.assertEquals(Path.of(start1, new Coordinate(1, 1), new Coordinate(2, 2)), map.get(start1).get(0));
        Assertions.assertEquals(Path.of(start2, new Coordinate(6, 1), new Coordinate(5, 2)), map.get(start2).get(0));
    }

    @Test
    void testBishopMultiple() {
        BoardInterface boardInterface = new BoardInterface(BoardBuilder.buildBoard("3qkb2/2pppp2/8/2B5/4B1B1/8/1B4B1/RN1QK1NR w KQ - 0 1"));
        this.pieceMap.deduce(boardInterface);
        Map<Coordinate, List<Path>> map = this.pieceMap.getStartLocations();
        Coordinate start1 = new Coordinate(2, 0);
        Coordinate start2 = new Coordinate(5, 0);

        System.out.println(this.pieceMap.getStartLocations());

        Assertions.assertEquals(2, map.get(start1).size());
        Assertions.assertEquals(3, map.get(start2).size());
        Path minimumPath1 = Path.of(start1, new Coordinate(1, 1));
        Path minimumPath2 = Path.of(start2, new Coordinate(6, 1));

        Assertions.assertTrue(map.get(start1).contains(minimumPath1));
        Assertions.assertTrue(map.get(start2).contains(minimumPath2));
        Assertions.assertTrue(!map.get(start1).get(0).equals(minimumPath1) || !map.get(start1).get(1).equals(minimumPath1));
        Assertions.assertTrue(!map.get(start2).get(0).equals(minimumPath2) || !map.get(start2).get(1).equals(minimumPath2));

    }
}
