import Heuristics.BoardInterface;
import Heuristics.Deductions.PawnMap;
import Heuristics.Deductions.PawnMapBlack;
import Heuristics.Deductions.PawnMapWhite;
import StandardChess.BoardBuilder;
import StandardChess.Coordinate;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class PawnMapTest {

    @Test
    void testPawnMaps() {
        BoardInterface board = new BoardInterface(BoardBuilder.buildBoard("4k3/p1Pp4/p3p1p1/5p1p/5P1P/P3P1P1/P1pP4/4K3 w - - 0 1"));
        PawnMapWhite map = new PawnMapWhite();
        map.deduce(board);
        System.out.println(map.getPawnOrigins());

        PawnMapBlack blackMap = new PawnMapBlack();
        blackMap.deduce(board);
    }

    @Test
    void testPawnMapsPawnPositions() {
        BoardInterface board = new BoardInterface(BoardBuilder.buildBoard("r3k2r/P6p/P6p/P6p/P6p/P6p/P6p/4K3 w kq - 0 1"));
        PawnMapWhite map = new PawnMapWhite();
        map.deduce(board);
        System.out.println(map.getPawnOrigins());
        map.getPawnOrigins().entrySet().stream().forEach(entry ->{
            Assertions.assertEquals(1, entry.getValue().size());
        });

        PawnMapBlack blackMap = new PawnMapBlack();
        blackMap.deduce(board);
        System.out.println(blackMap.getPawnOrigins());
        blackMap.getPawnOrigins().entrySet().stream().forEach(entry ->{
            Assertions.assertEquals(1, entry.getValue().size());
        });
    }

    @Test
    void testPawnMapsTwo() {
        BoardInterface board = new BoardInterface(BoardBuilder.buildBoard("r3k2r/p2p4/1pp5/2p5/2P5/1PP5/P2P4/4K3 w kq - 0 1"));
//        board = new BoardInterface(BoardBuilder.buildBoard("r3k2r/p7/1pp5/2p5/2P5/1PP5/P7/4K3 w kq - 0 1"));
        PawnMapWhite map = new PawnMapWhite();
        map.deduce(board);
        System.out.println(map.getPawnOrigins());
        Assertions.assertEquals(1, map.getPawnOrigins().get(new Coordinate(2, 3)).size());

        PawnMapBlack blackMap = new PawnMapBlack();
        blackMap.deduce(board);
        System.out.println(blackMap.getPawnOrigins());
        Assertions.assertEquals(1, blackMap.getPawnOrigins().get(new Coordinate(2, 4)).size());
    }

    @Test
    void testPawnMapsThree() {
        BoardInterface board = new BoardInterface(BoardBuilder.buildBoard("4k3/5p2/p2pp3/1p6/1p6/1PPPP3/2P2PPP/4K3 w - - 0 1"));
        PawnMapWhite map = new PawnMapWhite();
        map.deduce(board);
        System.out.println(map.getPawnOrigins());
        Assertions.assertEquals(1, map.getPawnOrigins().get(new Coordinate(1, 2)).size());
        Assertions.assertEquals(1, map.getPawnOrigins().get(new Coordinate(2, 1)).size());
        Assertions.assertEquals(1, map.getPawnOrigins().get(new Coordinate(2, 2)).size());
        Assertions.assertEquals(2, map.getPawnOrigins().get(new Coordinate(3, 2)).size());
        Assertions.assertEquals(2, map.getPawnOrigins().get(new Coordinate(4, 2)).size());
        Assertions.assertEquals(1, map.getPawnOrigins().get(new Coordinate(5, 1)).size());
        Assertions.assertEquals(1, map.getPawnOrigins().get(new Coordinate(7, 1)).size());
        Assertions.assertEquals(1, map.getPawnOrigins().get(new Coordinate(6, 1)).size());
    }

    @Test
    void testPawnMapFour() {
        BoardInterface board = new BoardInterface(BoardBuilder.buildBoard("4k3/p4p2/3ppp2/1p6/1p6/1PPPP3/2P2PPP/4K3 w - - 0 1"));
        PawnMapBlack blackMap = new PawnMapBlack();
        blackMap.deduce(board);
        System.out.println(blackMap.getPawnOrigins());
        Assertions.assertEquals(1, blackMap.getPawnOrigins().get(new Coordinate(0, 6)).size());
        Assertions.assertEquals(3, blackMap.getPawnOrigins().get(new Coordinate(1, 4)).size());
        Assertions.assertEquals(4, blackMap.getPawnOrigins().get(new Coordinate(1, 3)).size());
        Assertions.assertEquals(3, blackMap.getPawnOrigins().get(new Coordinate(3, 5)).size());
        Assertions.assertEquals(2, blackMap.getPawnOrigins().get(new Coordinate(4, 5)).size());
        Assertions.assertEquals(1, blackMap.getPawnOrigins().get(new Coordinate(5, 5)).size());

    }

    @Test
    void p() {
        BoardInterface board = new BoardInterface(BoardBuilder.buildBoard("r3k2r/p2p4/1pp5/2p5/2P5/1PP5/P2P4/4K3 w kq - 0 1"));
//        board = new BoardInterface(BoardBuilder.buildBoard("r3k2r/p7/1pp5/2p5/2P5/1PP5/P7/4K3 w kq - 0 1"));
        PawnMapWhite map = new PawnMapWhite();
        map.deduce(board);

        System.out.println(map.getPawnOrigins().get(new Coordinate(1, 2)).hashCode());
        System.out.println(map.getPawnOrigins().get(new Coordinate(2, 2)).equals(map.getPawnOrigins().get(new Coordinate(1, 2))));

        PawnMapWhite map2 = new PawnMapWhite();
        map2.deduce(board);



        PawnMapBlack blackMap = new PawnMapBlack();
        blackMap.deduce(board);
        System.out.println(blackMap.getPawnOrigins());
    }
}