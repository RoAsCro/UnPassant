import Heuristics.BoardInterface;
import Heuristics.Deductions.CombinedPawnMap;
import Heuristics.Deductions.PawnMapBlack;
import Heuristics.Deductions.PawnMapWhite;
import Heuristics.Deductions.PieceMap;
import Heuristics.Observation;
import Heuristics.Path;
import Heuristics.Pathfinder;
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

    void test(BoardInterface boardInterface, Coordinate start1, Coordinate start2, int size1, int size2) {
        pieceMap.getObservations().forEach(observation -> observation.observe(boardInterface));
        this.pieceMap.deduce(boardInterface);
        Map<Coordinate, Map<Coordinate, Path>> map = this.pieceMap.getStartLocations();
        System.out.println(this.pieceMap.getStartLocations());

        Assertions.assertEquals(size1, map.get(start1).size());
        Assertions.assertEquals(size2, map.get(start2).size());
    }

    @Test
    void testBishopAtStartLocation() {
        BoardInterface boardInterface = new BoardInterface(BoardBuilder.buildBoard());
        Coordinate start1 = new Coordinate(2, 0);
        Coordinate start2 = new Coordinate(5, 0);
        test(boardInterface, start1, start2, 1, 1);
//        pieceMap.getObservations().forEach(observation -> observation.observe(boardInterface));
//        this.pieceMap.deduce(boardInterface);
//
//        Map<Coordinate, Map<Coordinate, Path>> map = this.pieceMap.getStartLocations();
//
//
//        System.out.println(this.pieceMap.getStartLocations());
//
//        Assertions.assertEquals(1, map.get(start1).size());
//        Assertions.assertEquals(1, map.get(start2).size());
        Map<Coordinate, Map<Coordinate, Path>> map = this.pieceMap.getStartLocations();
        Path path = Path.of(start1);
        Assertions.assertEquals(path, map.get(start1).get(start1));
        Assertions.assertEquals(Path.of(start2), map.get(start2).get(start2));
    }

    @Test
    void testBishopAtNotStartLocation() {
        BoardInterface boardInterface = new BoardInterface(BoardBuilder.buildBoard("rnbqkbnr/pppppppp/8/2B5/8/2PPP3/PP2BPPP/RN1QK1NR w KQkq - 0 1"));
//        pieceMap.getObservations().forEach(observation -> observation.observe(boardInterface));
//        this.pieceMap.deduce(boardInterface);
//        Map<Coordinate, List<Path>> map = this.pieceMap.getStartLocations();
        Coordinate start1 = new Coordinate(2, 0);
        Coordinate start2 = new Coordinate(5, 0);
        test(boardInterface, start1, start2, 1, 1);
//        Assertions.assertEquals(1, map.get(start1).size());
//        Assertions.assertEquals(1, map.get(start2).size());
        Map<Coordinate, Map<Coordinate, Path>> map = this.pieceMap.getStartLocations();

        Assertions.assertTrue(map.get(start1).get(new Coordinate(2, 4)).contains(new Coordinate(3, 1)));
        Assertions.assertEquals(Path.of(start2, new Coordinate(4, 1)), map.get(start2).get(new Coordinate(4, 1)));
    }
//
    @Test
    void testBishopNotCollidingWithPawns() {
        BoardInterface boardInterface = new BoardInterface(BoardBuilder.buildBoard("r1bqkb1r/pppppppp/8/2B2B2/8/P6P/P1PPPP1P/RN1QK1NR w KQkq - 0 1"));
        pieceMap.getObservations().forEach(observation -> observation.observe(boardInterface));

        this.pieceMap.deduce(boardInterface);
        Map<Coordinate, Map<Coordinate, Path>> map = this.pieceMap.getStartLocations();
        Coordinate start1 = new Coordinate(2, 0);
        Coordinate start2 = new Coordinate(5, 0);

        System.out.println(this.pieceMap.getStartLocations());
        System.out.println(this.pawnMapWhite.getPawnOrigins());
        System.out.println(this.combinedPawnMap.getWhitePaths().get(new Coordinate(0, 2)).get(0));
        System.out.println(Pathfinder.pathsExclusive(this.combinedPawnMap.getWhitePaths().get(new Coordinate(0, 2)).get(0),
                Path.of(start1, new Coordinate(1, 1), new Coordinate(0, 2), new Coordinate(-1, -1))));

        Assertions.assertEquals(Path.of(start1, new Coordinate(1, 1), new Coordinate(2, 2)), map.get(start1).get(new Coordinate(2, 4)));
        Assertions.assertEquals(Path.of(start2, new Coordinate(6, 1), new Coordinate(5, 2)), map.get(start2).get(new Coordinate(5, 4)));
    }
//
    @Test
    void testBishopNotCollidingWithPawnsTwo() {
        BoardInterface boardInterface = new BoardInterface(BoardBuilder.buildBoard("r2qk2r/p1pppp1p/8/2B2B2/8/P1P2P1P/P2PP2P/RN1QK1NR w KQkq - 0 1"));
        pieceMap.getObservations().forEach(observation -> observation.observe(boardInterface));

        this.pieceMap.deduce(boardInterface);
        Map<Coordinate, Map<Coordinate, Path>> map = this.pieceMap.getStartLocations();
        Coordinate start1 = new Coordinate(2, 0);
        Coordinate start2 = new Coordinate(5, 0);

        System.out.println(this.pieceMap.getStartLocations());

        Assertions.assertEquals(Path.of(start1, new Coordinate(1, 1), new Coordinate(2, 2)), map.get(start1).get(new Coordinate(2, 4)));
        Assertions.assertEquals(Path.of(start2, new Coordinate(6, 1), new Coordinate(5, 2)), map.get(start2).get(new Coordinate(5, 4)));
    }

    @Test
    void testBishopMultiple() {
        BoardInterface boardInterface = new BoardInterface(BoardBuilder.buildBoard("3qkb2/2pppp2/8/2B5/4B1B1/8/1B4B1/RN1QK1NR w KQ - 0 1"));
        pieceMap.getObservations().forEach(observation -> observation.observe(boardInterface));
        this.pieceMap.deduce(boardInterface);
        Map<Coordinate, Map<Coordinate, Path>> map = this.pieceMap.getStartLocations();
        Coordinate start1 = new Coordinate(2, 0);
        Coordinate start2 = new Coordinate(5, 0);

        System.out.println(this.pieceMap.getStartLocations());

        Assertions.assertEquals(2, map.get(start1).size());
        Assertions.assertEquals(3, map.get(start2).size());
        Path minimumPath1 = Path.of(start1, new Coordinate(1, 1));
        Path minimumPath2 = Path.of(start2, new Coordinate(6, 1));

        Assertions.assertTrue(map.get(start1).containsValue(minimumPath1));
        Assertions.assertTrue(map.get(start2).containsValue(minimumPath2));
        Assertions.assertTrue(map.get(start1).containsKey(new Coordinate(2, 4)));
        Assertions.assertTrue(map.get(start2).containsKey(new Coordinate(4, 3)));

    }
//
//
    @Test
    void testBishopMultipleTwo() {
        BoardInterface boardInterface = new BoardInterface(BoardBuilder.buildBoard("3qkb2/2pppp2/8/8/8/8/1B1BB1B1/RN1QK1NR w KQ - 0 1"));
        pieceMap.getObservations().forEach(observation -> observation.observe(boardInterface));
        this.pieceMap.deduce(boardInterface);
        Map<Coordinate, Map<Coordinate, Path>> map = this.pieceMap.getStartLocations();
        Coordinate start1 = new Coordinate(2, 0);
        Coordinate start2 = new Coordinate(5, 0);

        System.out.println(this.pieceMap.getStartLocations());

        test(boardInterface, start1, start2, 2, 2);
//        Assertions.assertEquals(2, map.get(start1).size());
//        Assertions.assertEquals(2, map.get(start2).size());
        Path minimumPath1A = Path.of(start1, new Coordinate(1, 1));
        Path minimumPath1B = Path.of(start1, new Coordinate(3, 1));

        Path minimumPath2A = Path.of(start2, new Coordinate(6, 1));
        Path minimumPath2B = Path.of(start2, new Coordinate(4, 1));


        Assertions.assertTrue(map.get(start1).containsValue(minimumPath1A));
        Assertions.assertTrue(map.get(start1).containsValue(minimumPath1B));
        Assertions.assertTrue(map.get(start2).containsValue(minimumPath2A));
        Assertions.assertTrue(map.get(start2).containsValue(minimumPath2B));
    }
//
    @Test
    void testRookAtStartLocation() {
        BoardInterface boardInterface = new BoardInterface(BoardBuilder.buildBoard());
        pieceMap.getObservations().forEach(observation -> observation.observe(boardInterface));
        this.pieceMap.deduce(boardInterface);
        Map<Coordinate, Map<Coordinate, Path>> map = this.pieceMap.getStartLocations();
        Coordinate start1 = new Coordinate(0, 0);
        Coordinate start2 = new Coordinate(7, 0);

        System.out.println(this.pieceMap.getStartLocations());
        System.out.println(this.pieceMap.getCaged());

        test(boardInterface, start1, start2, 1, 1);

//        Assertions.assertEquals(1, map.get(start1).size());
//        Assertions.assertEquals(1, map.get(start2).size());
        Path path = Path.of(start1);
        Assertions.assertEquals(path, map.get(start1).get(start1));
        Assertions.assertEquals(Path.of(start2), map.get(start2).get(start2));
    }
//
    @Test
    void testRooksNoPath() {
        BoardInterface boardInterface = new BoardInterface(BoardBuilder.buildBoard("rnbqkbnr/pppppppp/8/8/R6R/1P4PP/P1PPPP2/1NBQKBN1 w kq - 0 1"));
//        pieceMap.getObservations().forEach(observation -> observation.observe(boardInterface));
//        this.pieceMap.deduce(boardInterface);
//        Map<Coordinate, List<Path>> map = this.pieceMap.getStartLocations();
        Coordinate start1 = new Coordinate(0, 0);
        Coordinate start2 = new Coordinate(7, 0);
        test(boardInterface, start1, start2, 0, 0);


//        System.out.println(this.pieceMap.getStartLocations());
//        System.out.println(this.pawnMapWhite.getPawnOrigins());
//
//        Assertions.assertEquals(0, map.get(start1).size());
//        Assertions.assertEquals(0, map.get(start2).size());

    }
//
    @Test
    void testRooksThroughBishops() {
        BoardInterface boardInterface = new BoardInterface(BoardBuilder.buildBoard("rnbqkbnr/pppppppp/8/8/2P1PPR1/8/PP1P2PP/1NBQKBN1 w kq - 0 1"));
//        pieceMap.getObservations().forEach(observation -> observation.observe(boardInterface));
//        this.pieceMap.deduce(boardInterface);
//        Map<Coordinate, List<Path>> map = this.pieceMap.getStartLocations();
        Coordinate start1 = new Coordinate(0, 0);
        Coordinate start2 = new Coordinate(7, 0);
//
//        System.out.println(this.pieceMap.getStartLocations());
//        System.out.println(this.pawnMapWhite.getPawnOrigins());
//
//        Assertions.assertEquals(0, map.get(start1).size());
//        Assertions.assertEquals(1, map.get(start2).size());
        test(boardInterface, start1, start2, 0, 1);


    }
//
    @Test
    void testRooksEscape() {
        BoardInterface boardInterface = new BoardInterface(BoardBuilder.buildBoard("rnbqkbnr/pppppppp/8/2R5/1P4RP/P7/2PPPPP1/1NBQKBN1 w kq - 0 1"));
//        pieceMap.getObservations().forEach(observation -> observation.observe(boardInterface));
//        this.pieceMap.deduce(boardInterface);
//        Map<Coordinate, List<Path>> map = this.pieceMap.getStartLocations();
        Coordinate start1 = new Coordinate(0, 0);
        Coordinate start2 = new Coordinate(7, 0);
        test(boardInterface, start1, start2, 2, 2);
//
//
//        System.out.println(this.pieceMap.getStartLocations());
//        System.out.println(this.pawnMapWhite.getPawnOrigins());
//
//        Assertions.assertEquals(2, map.get(start1).size());
//        Assertions.assertEquals(2, map.get(start2).size());

    }
//
    @Test
    void testRooksPawnCage() {
        BoardInterface boardInterface = new BoardInterface(BoardBuilder.buildBoard("rnbqkbnr/pppppppp/8/2R5/6R1/PPP1P2P/3P1PP1/1NBQKBN1 w kq - 0 1"));
//        pieceMap.getObservations().forEach(observation -> observation.observe(boardInterface));
//        this.pieceMap.deduce(boardInterface);
//        Map<Coordinate, List<Path>> map = this.pieceMap.getStartLocations();
        Coordinate start1 = new Coordinate(0, 0);
        Coordinate start2 = new Coordinate(7, 0);

//        System.out.println(this.pieceMap.getStartLocations());
//        System.out.println(this.pawnMapWhite.getPawnOrigins());
        test(boardInterface, start1, start2, 0, 0);
//
//        Assertions.assertEquals(0, map.get(start1).size());
//        Assertions.assertEquals(0, map.get(start2).size());

    }
//
    @Test
    void testRooksPawnCageTwo() {
        BoardInterface boardInterface = new BoardInterface(BoardBuilder.buildBoard("rnbqkbnr/pppppppp/8/8/1RP2R1P/1P1P2P1/P3PP2/1NBQKBN1 w kq - 0 1"));
//        pieceMap.getObservations().forEach(observation -> observation.observe(boardInterface));
//        this.pieceMap.deduce(boardInterface);
//        Map<Coordinate, List<Path>> map = this.pieceMap.getStartLocations();
        Coordinate start1 = new Coordinate(0, 0);
        Coordinate start2 = new Coordinate(7, 0);

//        System.out.println(this.pieceMap.getStartLocations());
//        System.out.println(this.pawnMapWhite.getPawnOrigins());

        test(boardInterface, start1, start2, 2, 2);

//        Assertions.assertEquals(2, map.get(start1).size());
//        Assertions.assertEquals(2, map.get(start2).size());

    }
//
    @Test
    void testRoyaltyStartLocation() {
        BoardInterface boardInterface = new BoardInterface(BoardBuilder.buildBoard());
//        pieceMap.getObservations().forEach(observation -> observation.observe(boardInterface));
//        this.pieceMap.deduce(boardInterface);
//        Map<Coordinate, List<Path>> map = this.pieceMap.getStartLocations();
        Coordinate start1 = new Coordinate(3, 0);
        Coordinate start2 = new Coordinate(4, 0);
//
//        System.out.println(this.pieceMap.getStartLocations());
//        System.out.println(this.pawnMapWhite.getPawnOrigins());
//
//        Assertions.assertEquals(1, map.get(start1).size());
//        Assertions.assertEquals(1, map.get(start2).size());
        test(boardInterface, start1, start2, 1, 1);


    }
//
    @Test
    void testRoyaltyStartLocationBlocked() {
        //Board impossible, may affect future tests
        BoardInterface boardInterface = new BoardInterface(BoardBuilder.buildBoard("rnbqkbnr/pppppppp/8/8/4QK2/8/PPPPPPPP/RNB2BNR"));
//        pieceMap.getObservations().forEach(observation -> observation.observe(boardInterface));
//        this.pieceMap.deduce(boardInterface);
//        Map<Coordinate, List<Path>> map = this.pieceMap.getStartLocations();
        Coordinate start1 = new Coordinate(3, 0);
        Coordinate start2 = new Coordinate(4, 0);

//        System.out.println(this.pieceMap.getStartLocations());
//        System.out.println(this.pawnMapWhite.getPawnOrigins());
//
//        Assertions.assertEquals(0, map.get(start1).size());
//        Assertions.assertEquals(0, map.get(start2).size());
        test(boardInterface, start1, start2, 0, 0);


    }
//
    @Test
    void testRoyaltyEscape() {
        BoardInterface boardInterface = new BoardInterface(BoardBuilder.buildBoard("rnbqkbnr/pppppppp/8/8/3QK3/2P5/PP1PPPPP/RNB2BNR"));
//        pieceMap.getObservations().forEach(observation -> observation.observe(boardInterface));
//        this.pieceMap.deduce(boardInterface);
//        Map<Coordinate, List<Path>> map = this.pieceMap.getStartLocations();
        Coordinate start1 = new Coordinate(3, 0);
        Coordinate start2 = new Coordinate(4, 0);
//
//        System.out.println(this.pieceMap.getStartLocations());
//        System.out.println(this.pawnMapWhite.getPawnOrigins());
//
//        Assertions.assertEquals(1, map.get(start1).size());
//        Assertions.assertEquals(1, map.get(start2).size());
        test(boardInterface, start1, start2, 1, 1);

    }
//
    @Test
    void testRoyaltyThroughBishop() {
        BoardInterface boardInterface = new BoardInterface(BoardBuilder.buildBoard("rnbqkbnr/pppppppp/8/8/3QK3/6P1/PPPPPP1P/RNB2BNR"));
//        pieceMap.getObservations().forEach(observation -> observation.observe(boardInterface));
//        this.pieceMap.deduce(boardInterface);
//        Map<Coordinate, List<Path>> map = this.pieceMap.getStartLocations();
        Coordinate start1 = new Coordinate(3, 0);
        Coordinate start2 = new Coordinate(4, 0);
        test(boardInterface, start1, start2, 1, 1);

//        System.out.println(this.pieceMap.getStartLocations());
//        System.out.println(this.pawnMapWhite.getPawnOrigins());
//
//        Assertions.assertEquals(1, map.get(start1).size());
//        Assertions.assertEquals(1, map.get(start2).size());

    }

    @Test
    void testRoyaltyThroughCagedBishop() {
        BoardInterface boardInterface = new BoardInterface(BoardBuilder.buildBoard("rnbqkbnr/pppppppp/8/8/3QK3/7P/PPPPPPP1/RNB2BNR"));
        pieceMap.getObservations().forEach(observation -> observation.observe(boardInterface));
        this.pieceMap.deduce(boardInterface);
        Map<Coordinate, Map<Coordinate, Path>> map = this.pieceMap.getStartLocations();
        Coordinate start1 = new Coordinate(3, 0);
        Coordinate start2 = new Coordinate(4, 0);

        System.out.println(this.pieceMap.getStartLocations());
        System.out.println(this.pawnMapWhite.getPawnOrigins());

        Assertions.assertEquals(0, map.get(start1).size());
        Assertions.assertEquals(0, map.get(start2).size());

    }

    @Test
    void testCagedRookNotPathedToByOtherRookStart() {
        BoardInterface boardInterface = new BoardInterface(BoardBuilder.buildBoard("rnbqkbnr/pppppppp/8/8/P7/5N2/1PPPPPPP/RNBQKBR1 w - - 0 1"));
        Coordinate start1 = new Coordinate(0, 0);
        Coordinate start2 = new Coordinate(7, 0);
        test(boardInterface, start1, start2, 1, 1);

    }

    @Test
    void testRookOnOtherSide() {
        BoardInterface boardInterface = new BoardInterface(BoardBuilder.buildBoard("rRbqkbnr/p1pppppp/8/1p2n3/P6P/5N2/1PPPPPP1/RNBQKB2 w - - 0 1"));
        Coordinate start1 = new Coordinate(0, 0);
        Coordinate start2 = new Coordinate(7, 0);
        test(boardInterface, start1, start2, 2, 2);

    }

    @Test
    void testRooksToEachOtherBothCaged() {
        BoardInterface boardInterface = new BoardInterface(BoardBuilder.buildBoard("r1bqk2r/pp1ppp1p/6p1/2p5/8/1P2P1P1/P1PPKP1P/R5R1 w - - 0 1"));
        Coordinate start1 = new Coordinate(0, 0);
        Coordinate start2 = new Coordinate(7, 0);
        test(boardInterface, start1, start2, 2, 2);

    }

    @Test
    void testRookOpposingCage() {
        BoardInterface boardInterface = new BoardInterface(BoardBuilder.buildBoard("rRbqk2r/pp1ppp1p/6p1/2p5/P6P/5N2/1PPPPPP1/RNBQKB2 w - - 0 1"));
        Coordinate start1 = new Coordinate(0, 0);
        Coordinate start2 = new Coordinate(7, 0);
        test(boardInterface, start1, start2, 1, 1);

    }

    @Test
    void testMultiple() {
        BoardInterface boardInterface = new BoardInterface(BoardBuilder.buildBoard("3qk3/2pppp2/8/2P3B1/R4B2/5N1P/3PPPP1/1N1QKB1R w K - 0 1"));
        pieceMap.getObservations().forEach(observation -> observation.observe(boardInterface));
        this.pieceMap.deduce(boardInterface);
        Map<Coordinate, Map<Coordinate, Path>> map = this.pieceMap.getStartLocations();
        Coordinate start1 = new Coordinate(3, 0);
        Coordinate start2 = new Coordinate(4, 0);

        System.out.println(this.pieceMap.getStartLocations());
        System.out.println(this.pawnMapWhite.getPawnOrigins());
        System.out.println(this.pieceMap.startPiecePairs);

//        Assertions.assertEquals(0, map.get(start1).size());
//        Assertions.assertEquals(0, map.get(start2).size());

    }
}
