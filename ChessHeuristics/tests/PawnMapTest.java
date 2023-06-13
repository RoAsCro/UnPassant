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
