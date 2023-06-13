import Heuristics.BoardInterface;
import Heuristics.Deductions.PawnMap;
import Heuristics.Deductions.PawnMapBlack;
import Heuristics.Deductions.PawnMapWhite;
import StandardChess.BoardBuilder;
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
        System.out.println(blackMap.getPawnOrigins());
    }

    @Test
    void testPawnMapsExtremities() {
        BoardInterface board = new BoardInterface(BoardBuilder.buildBoard("rnbqkbnr/P3n3/3n4/2n5/1n6/8/8/4K3 w kq - 0 1"));
        PawnMapWhite map = new PawnMapWhite();
        map.deduce(board);
        System.out.println(map.getPawnOrigins());

        PawnMapBlack blackMap = new PawnMapBlack();
        blackMap.deduce(board);
        System.out.println(blackMap.getPawnOrigins());
    }
}
