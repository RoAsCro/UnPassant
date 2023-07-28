import Heuristics.BoardInterface;
import Heuristics.Deductions.CombinedPawnMap;
import Heuristics.Deductions.PawnMapBlack;
import Heuristics.Deductions.PawnMapWhite;
import Heuristics.Deductions.PieceMap;
import Heuristics.Observation;
import Heuristics.Observations.PawnNumber;
import Heuristics.Observations.PieceNumber;
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
        PawnNumber pawnNumber = new PawnNumber();
        PieceNumber pieceNumber = new PieceNumber();

        pawnMapWhite = new PawnMapWhite(pawnNumber, pieceNumber);
        pawnMapBlack = new PawnMapBlack(pawnNumber, pieceNumber);
        combinedPawnMap = new CombinedPawnMap(pawnMapWhite, pawnMapBlack);
        pieceMap = new PieceMap(combinedPawnMap);

    }

    void test(BoardInterface boardInterface, Coordinate start1, Coordinate start2, int size1, int size2, boolean possibleState) {
        pieceMap.getObservations().forEach(observation -> observation.observe(boardInterface));
        this.pieceMap.deduce(boardInterface);
        Map<Coordinate, Map<Coordinate, Path>> map = this.pieceMap.getStartLocations();
        System.out.println(this.pieceMap.getStartLocations());

        Assertions.assertEquals(size1, map.get(start1).size());
        Assertions.assertEquals(size2, map.get(start2).size());
        Assertions.assertEquals(possibleState, this.pieceMap.getState());
    }

    @Test
    void testBishopAtStartLocation() {
        BoardInterface boardInterface = new BoardInterface(BoardBuilder.buildBoard());
        Coordinate start1 = new Coordinate(2, 0);
        Coordinate start2 = new Coordinate(5, 0);
        test(boardInterface, start1, start2, 1, 1, true);
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
    void testBishopAtStartLocationBlack() {
        BoardInterface boardInterface = new BoardInterface(BoardBuilder.buildBoard());
        Coordinate start1 = new Coordinate(2, 7);
        Coordinate start2 = new Coordinate(5, 7);
        test(boardInterface, start1, start2, 1, 1, true);
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
        test(boardInterface, start1, start2, 1, 1, true);
//        Assertions.assertEquals(1, map.get(start1).size());
//        Assertions.assertEquals(1, map.get(start2).size());
        Map<Coordinate, Map<Coordinate, Path>> map = this.pieceMap.getStartLocations();

        Assertions.assertTrue(map.get(start1).get(new Coordinate(2, 4)).contains(new Coordinate(3, 1)));
        Assertions.assertEquals(Path.of(start2, new Coordinate(4, 1)), map.get(start2).get(new Coordinate(4, 1)));
    }

    @Test
    void testBishopAtNotStartLocationBlack() {
        BoardInterface boardInterface = new BoardInterface(BoardBuilder.buildBoard("rn1qk1nr/pp2bppp/2ppp3/2B5/2b5/2PPP3/PP2BPPP/RN1QK1NR w KQkq - 0 1"));
//        pieceMap.getObservations().forEach(observation -> observation.observe(boardInterface));
//        this.pieceMap.deduce(boardInterface);
//        Map<Coordinate, List<Path>> map = this.pieceMap.getStartLocations();
        Coordinate start1 = new Coordinate(2, 7);
        Coordinate start2 = new Coordinate(5, 7);
        test(boardInterface, start1, start2, 1, 1, true);
//        Assertions.assertEquals(1, map.get(start1).size());
//        Assertions.assertEquals(1, map.get(start2).size());
        Map<Coordinate, Map<Coordinate, Path>> map = this.pieceMap.getStartLocations();

        Assertions.assertTrue(map.get(start1).get(new Coordinate(2, 3)).contains(new Coordinate(3, 6)));
        Assertions.assertEquals(Path.of(start2, new Coordinate(4, 6)), map.get(start2).get(new Coordinate(4, 6)));
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

    @Test
    void testBishopNotCollidingWithPawnsBlack() {
        BoardInterface boardInterface = new BoardInterface(BoardBuilder.buildBoard("r2qk2r/p1pppp1p/p6p/8/2b2b2/8/P1PPPP1P/R2QK1NR w KQkq - 0 1"));
        pieceMap.getObservations().forEach(observation -> observation.observe(boardInterface));

        this.pieceMap.deduce(boardInterface);
        Map<Coordinate, Map<Coordinate, Path>> map = this.pieceMap.getStartLocations();
        Coordinate start1 = new Coordinate(2, 7);
        Coordinate start2 = new Coordinate(5, 7);

        System.out.println(this.pieceMap.getStartLocations());
        System.out.println(this.pawnMapBlack.getPawnOrigins());
//        System.out.println(this.combinedPawnMap.getWhitePaths().get(new Coordinate(0, 2)).get(0));
//        System.out.println(Pathfinder.pathsExclusive(this.combinedPawnMap.getWhitePaths().get(new Coordinate(0, 2)).get(0),
//                Path.of(start1, new Coordinate(1, 1), new Coordinate(0, 2), new Coordinate(-1, -1))));

        Assertions.assertEquals(Path.of(start1, new Coordinate(1, 6), new Coordinate(2, 5)), map.get(start1).get(new Coordinate(2, 3)));
        Assertions.assertEquals(Path.of(start2, new Coordinate(6, 6), new Coordinate(5, 5)), map.get(start2).get(new Coordinate(5, 3)));
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
    void testBishopNotCollidingWithPawnsTwoBlack() {
        BoardInterface boardInterface = new BoardInterface(BoardBuilder.buildBoard("r2qk2r/p2pp2p/p1p2p1p/8/2b2b2/P1P2P1P/P2PP2P/3QK3 w kq - 0 1"));
        pieceMap.getObservations().forEach(observation -> observation.observe(boardInterface));

        this.pieceMap.deduce(boardInterface);
        Map<Coordinate, Map<Coordinate, Path>> map = this.pieceMap.getStartLocations();
        Coordinate start1 = new Coordinate(2, 7);
        Coordinate start2 = new Coordinate(5, 7);

        System.out.println(this.pieceMap.getStartLocations());

        Assertions.assertEquals(Path.of(start1, new Coordinate(1, 6), new Coordinate(2, 5)), map.get(start1).get(new Coordinate(2, 3)));
        Assertions.assertEquals(Path.of(start2, new Coordinate(6, 6), new Coordinate(5, 5)), map.get(start2).get(new Coordinate(5, 3)));
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

    @Test
    void testBishopMultipleBlack() {
        BoardInterface boardInterface = new BoardInterface(BoardBuilder.buildBoard("rn1qk1nr/1b4b1/8/4b1b1/2b5/8/1B4B1/RN1QK1NR w KQ - 0 1"));
        pieceMap.getObservations().forEach(observation -> observation.observe(boardInterface));
        this.pieceMap.deduce(boardInterface);
        Map<Coordinate, Map<Coordinate, Path>> map = this.pieceMap.getStartLocations();
        Coordinate start1 = new Coordinate(2, 7);
        Coordinate start2 = new Coordinate(5, 7);

        System.out.println(this.pieceMap.getStartLocations());
        System.out.println(map.get(start1));
        Assertions.assertEquals(2, map.get(start1).size());
        Assertions.assertEquals(3, map.get(start2).size());
        Path minimumPath1 = Path.of(start1, new Coordinate(1, 6));
        Path minimumPath2 = Path.of(start2, new Coordinate(6, 6));

        Assertions.assertTrue(map.get(start1).containsValue(minimumPath1));
        Assertions.assertTrue(map.get(start2).containsValue(minimumPath2));
        Assertions.assertTrue(map.get(start1).containsKey(new Coordinate(2, 3)));
        Assertions.assertTrue(map.get(start2).containsKey(new Coordinate(4, 4)));

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

        test(boardInterface, start1, start2, 2, 2, true);
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

    @Test
    void testBishopMultipleTwoBlack() {
        BoardInterface boardInterface = new BoardInterface(BoardBuilder.buildBoard("3qk3/1b1bb1b1/8/8/8/8/1B1BB1B1/RN1QK1NR w KQ - 0 1"));
        pieceMap.getObservations().forEach(observation -> observation.observe(boardInterface));
        this.pieceMap.deduce(boardInterface);
        Map<Coordinate, Map<Coordinate, Path>> map = this.pieceMap.getStartLocations();
        Coordinate start1 = new Coordinate(2, 7);
        Coordinate start2 = new Coordinate(5, 7);

        System.out.println(this.pieceMap.getStartLocations());

        test(boardInterface, start1, start2, 2, 2, true);
//        Assertions.assertEquals(2, map.get(start1).size());
//        Assertions.assertEquals(2, map.get(start2).size());
        Path minimumPath1A = Path.of(start1, new Coordinate(1, 6));
        Path minimumPath1B = Path.of(start1, new Coordinate(3, 6));

        Path minimumPath2A = Path.of(start2, new Coordinate(6, 6));
        Path minimumPath2B = Path.of(start2, new Coordinate(4, 6));


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

        test(boardInterface, start1, start2, 1, 1, true);
        test(boardInterface, new Coordinate(0, 7), new Coordinate(7, 7), 1, 1, true);


//        Assertions.assertEquals(1, map.get(start1).size());
//        Assertions.assertEquals(1, map.get(start2).size());
        Path path = Path.of(start1);
        Assertions.assertEquals(path, map.get(start1).get(start1));
        Assertions.assertEquals(Path.of(start2), map.get(start2).get(start2));
        Assertions.assertEquals(Path.of(new Coordinate(0, 7)), map.get(new Coordinate(0, 7)).get(new Coordinate(0, 7)));
        Assertions.assertEquals(Path.of(new Coordinate(7, 7)), map.get(new Coordinate(7, 7)).get(new Coordinate(7, 7)));
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
        test(boardInterface, start1, start2, 0, 0, false);

    }

    @Test
    void testRooksNoPathBlack() {
        BoardInterface boardInterface = new BoardInterface(BoardBuilder.buildBoard("1nbqkbn1/p1pppp2/1p4pp/r6r/R6R/1P4PP/P1PPPP2/1NBQKBN1 w - - 0 1"));
//        pieceMap.getObservations().forEach(observation -> observation.observe(boardInterface));
//        this.pieceMap.deduce(boardInterface);
//        Map<Coordinate, List<Path>> map = this.pieceMap.getStartLocations();
        Coordinate start1 = new Coordinate(0, 7);
        Coordinate start2 = new Coordinate(7, 7);
        test(boardInterface, start1, start2, 0, 0, false);


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
        test(boardInterface, start1, start2, 0, 1, true);


    }

    @Test
    void testRooksThroughBishopsBlack() {
        BoardInterface boardInterface = new BoardInterface(BoardBuilder.buildBoard("1nbqkbn1/pp1p2pp/8/2p1ppr1/8/8/PP1PPPPP/RNBQKBNR w - - 0 1"));
//        pieceMap.getObservations().forEach(observation -> observation.observe(boardInterface));
//        this.pieceMap.deduce(boardInterface);
//        Map<Coordinate, List<Path>> map = this.pieceMap.getStartLocations();
        Coordinate start1 = new Coordinate(0, 7);
        Coordinate start2 = new Coordinate(7, 7);
//
//        System.out.println(this.pieceMap.getStartLocations());
//        System.out.println(this.pawnMapWhite.getPawnOrigins());
//
//        Assertions.assertEquals(0, map.get(start1).size());
//        Assertions.assertEquals(1, map.get(start2).size());
        test(boardInterface, start1, start2, 0, 1, true);


    }
//
    @Test
    void testRooksEscape() {
        BoardInterface boardInterface = new BoardInterface(BoardBuilder.buildBoard("rnbqkbnr/pppppppp/8/2R5/1P4RP/P7/2PPPPP1/1NBQKBN1 w kq - 0 1"));
        Coordinate start1 = new Coordinate(0, 0);
        Coordinate start2 = new Coordinate(7, 0);
        test(boardInterface, start1, start2, 2, 2, true);

    }

    @Test
    void testRooksEscapeBlack() {
        BoardInterface boardInterface = new BoardInterface(BoardBuilder.buildBoard("1nbqkbn1/2ppppp1/p7/1pR3rp/1Pr3RP/P7/2PPPPP1/1NBQKBN1 w - - 0 1"));
        Coordinate start1 = new Coordinate(0, 7);
        Coordinate start2 = new Coordinate(7, 7);
        test(boardInterface, start1, start2, 2, 2, true);
    }
//
    @Test
    void testRooksPawnCage() {
        // NOTE: Impossible board state
        BoardInterface boardInterface = new BoardInterface(BoardBuilder.buildBoard("rnbqkbnr/pppppppp/8/2R5/6R1/PPP1P2P/3P1PP1/1NBQKBN1 w kq - 0 1"));
        Coordinate start1 = new Coordinate(0, 0);
        Coordinate start2 = new Coordinate(7, 0);
        test(boardInterface, start1, start2, 0, 0, false);

    }

    @Test
    void testRooksPawnCageTwo() {
        // NOTE: Impossible board state

        BoardInterface boardInterface = new BoardInterface(BoardBuilder.buildBoard("rnbqkbnr/pppppppp/8/2R5/6R1/PPP1P2P/3P1PP1/1NBQKBN1 w kq - 0 1"));
        Coordinate start1 = new Coordinate(0, 0);
        Coordinate start2 = new Coordinate(7, 0);
        test(boardInterface, start1, start2, 0, 0, false);

    }

    @Test
    void testRooksPawnCageBlack() {
        // NOTE: Impossible board state

        BoardInterface boardInterface = new BoardInterface(BoardBuilder.buildBoard("1nbqkbn1/3p1pp1/ppp1p2p/2R3r1/2r3R1/PPP1P2P/3P1PP1/1NBQKBN1 w - - 0 1"));
        Coordinate start1 = new Coordinate(0, 7);
        Coordinate start2 = new Coordinate(7, 7);
        test(boardInterface, start1, start2, 0, 0, false);

    }

    @Test
    void testRooksPawnCageTwoBlack() {
        BoardInterface boardInterface = new BoardInterface(BoardBuilder.buildBoard("1nbqkbn1/p3pp2/1p1p2p1/1rp2r1p/1RP2R1P/1P1P2P1/P3PP2/1NBQKBN1 w - - 0 1"));
        Coordinate start1 = new Coordinate(0, 7);
        Coordinate start2 = new Coordinate(7, 7);
        test(boardInterface, start1, start2, 2, 2, true);

    }
//
    @Test
    void testRoyaltyStartLocation() {
        BoardInterface boardInterface = new BoardInterface(BoardBuilder.buildBoard());
        Coordinate start1 = new Coordinate(3, 0);
        Coordinate start2 = new Coordinate(4, 0);
        test(boardInterface, start1, start2, 1, 1, true);


    }

    @Test
    void testRoyaltyStartLocationBlack() {
        BoardInterface boardInterface = new BoardInterface(BoardBuilder.buildBoard());
        Coordinate start1 = new Coordinate(3, 7);
        Coordinate start2 = new Coordinate(4, 7);
        test(boardInterface, start1, start2, 1, 1, true);


    }
//
    @Test
    void testRoyaltyStartLocationBlocked() {
        //Board impossible, may affect future tests
        BoardInterface boardInterface = new BoardInterface(BoardBuilder.buildBoard("rnbqkbnr/pppppppp/8/8/4QK2/8/PPPPPPPP/RNB2BNR"));
        Coordinate start1 = new Coordinate(3, 0);
        Coordinate start2 = new Coordinate(4, 0);
        test(boardInterface, start1, start2, 0, 0, false);


    }

    @Test
    void testRoyaltyStartLocationBlockedBlack() {
        //Board impossible, may affect future tests
        BoardInterface boardInterface = new BoardInterface(BoardBuilder.buildBoard("rnb2bnr/pppppppp/8/4qk2/8/8/PPPPPPPP/RNBQKBNR"));
        Coordinate start1 = new Coordinate(3, 7);
        Coordinate start2 = new Coordinate(4, 7);
        test(boardInterface, start1, start2, 0, 0, false);


    }
//
    @Test
    void testRoyaltyEscape() {
        BoardInterface boardInterface = new BoardInterface(BoardBuilder.buildBoard("rnbqkbnr/pppppppp/8/8/3QK3/2P5/PP1PPPPP/RNB2BNR"));
        Coordinate start1 = new Coordinate(3, 0);
        Coordinate start2 = new Coordinate(4, 0);
        test(boardInterface, start1, start2, 1, 1, true);

    }

    @Test
    void testRoyaltyEscapeBlack() {
        BoardInterface boardInterface = new BoardInterface(BoardBuilder.buildBoard("rnb2bnr/pp1ppppp/2p5/3qk3/8/8/PPPPPPPP/RNBQKBNR w - - 0 1"));
        Coordinate start1 = new Coordinate(3, 7);
        Coordinate start2 = new Coordinate(4, 7);
        test(boardInterface, start1, start2, 1, 1, true);

    }
    @Test
    void testRoyaltyThroughBishop() {
        BoardInterface boardInterface = new BoardInterface(BoardBuilder.buildBoard("rnbqkbnr/pppppppp/8/8/3QK3/6P1/PPPPPP1P/RNB2BNR"));
        Coordinate start1 = new Coordinate(3, 0);
        Coordinate start2 = new Coordinate(4, 0);
        test(boardInterface, start1, start2, 1, 1, true);

    }

    @Test
    void testRoyaltyThroughBishopBlack() {
        BoardInterface boardInterface = new BoardInterface(BoardBuilder.buildBoard("rnb2bnr/pppppp1p/6p1/3qk3/8/6P1/PPPPPP1P/RNBQKBNR w - - 0 1"));
        Coordinate start1 = new Coordinate(3, 7);
        Coordinate start2 = new Coordinate(4, 7);
        test(boardInterface, start1, start2, 1, 1, true);

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
        Assertions.assertFalse(pieceMap.getState());

    }

    @Test
    void testRoyaltyThroughCagedBishopBlack() {
        BoardInterface boardInterface = new BoardInterface(BoardBuilder.buildBoard("rnb2bnr/ppppppp1/7p/3qk3/8/8/PPPPPPPP/RNBQKBNR w - - 0 1"));
        pieceMap.getObservations().forEach(observation -> observation.observe(boardInterface));
        this.pieceMap.deduce(boardInterface);
        Map<Coordinate, Map<Coordinate, Path>> map = this.pieceMap.getStartLocations();
        Coordinate start1 = new Coordinate(3, 7);
        Coordinate start2 = new Coordinate(4, 7);

        System.out.println(this.pieceMap.getStartLocations());
        System.out.println(this.pawnMapWhite.getPawnOrigins());

        Assertions.assertEquals(0, map.get(start1).size());
        Assertions.assertEquals(0, map.get(start2).size());
        Assertions.assertFalse(pieceMap.getState());


    }

    @Test
    void testCagedRookNotPathedToByOtherRookStart() {
        BoardInterface boardInterface = new BoardInterface(BoardBuilder.buildBoard("rnbqkbnr/pppppppp/8/8/P7/5N2/1PPPPPPP/RNBQKBR1 w - - 0 1"));
        Coordinate start1 = new Coordinate(0, 0);
        Coordinate start2 = new Coordinate(7, 0);
        test(boardInterface, start1, start2, 1, 1, true);

    }

    @Test
    void testCagedRookNotPathedToByOtherRookStartTwo() {
        BoardInterface boardInterface = new BoardInterface(BoardBuilder.buildBoard("rBb1kbnr/p1pp1ppp/8/8/8/8/P1PPPPPP/R1BQKB1R w KQkq - 0 1"));
        Coordinate start1 = new Coordinate(0, 0);
        Coordinate start2 = new Coordinate(7, 0);
        test(boardInterface, start1, start2, 1, 1, true);

    }

    @Test
    void testRookOnOtherSide() {
        BoardInterface boardInterface = new BoardInterface(BoardBuilder.buildBoard("rRbqkbnr/p1pppppp/8/1p2n3/P6P/5N2/1PPPPPP1/RNBQKB2 w - - 0 1"));
        Coordinate start1 = new Coordinate(0, 0);
        Coordinate start2 = new Coordinate(7, 0);
        test(boardInterface, start1, start2, 2, 2, true);

    }

    @Test
    void testRooksToEachOtherBothCaged() {
        BoardInterface boardInterface = new BoardInterface(BoardBuilder.buildBoard("r1bqk2r/pp1ppp1p/6p1/2p5/8/1P2P1P1/P1PPKP1P/R5R1 w - - 0 1"));
        Coordinate start1 = new Coordinate(0, 0);
        Coordinate start2 = new Coordinate(7, 0);
        test(boardInterface, start1, start2, 2, 2, true);

    }



    @Test
    void testRookOpposingCage() {
        BoardInterface boardInterface = new BoardInterface(BoardBuilder.buildBoard("rRbqk2r/pp1ppp1p/6p1/2p5/P6P/5N2/1PPPPPP1/RNBQKB2 w - - 0 1"));
        Coordinate start1 = new Coordinate(0, 0);
        Coordinate start2 = new Coordinate(7, 0);
        test(boardInterface, start1, start2, 1, 1, true);

    }

    @Test
    void testRookOpposingCageBlack() {
        BoardInterface boardInterface = new BoardInterface(BoardBuilder.buildBoard("r1bqk3/1pp1ppp1/8/p6p/7P/2P5/PP1PPPP1/RrB1KB1R w - - 0 1"));
        Coordinate start1 = new Coordinate(0, 7);
        Coordinate start2 = new Coordinate(7, 7);
        test(boardInterface, start1, start2, 1, 1, true);

    }

    @Test
    void testBishopOpposingCageBoth() {
        BoardInterface boardInterface = new BoardInterface(BoardBuilder.buildBoard("rn1qkbBr/p2ppp1p/1p4p1/8/8/1P4P1/P1PPP2P/RbBQK1NR w KQkq - 0 1"));
        Coordinate start1 = new Coordinate(2, 7);
        Coordinate start2 = new Coordinate(5, 0);
        test(boardInterface, start1, start2, 0, 0, true);

    }

    @Test
    void testRoyaltyOpposingCageBoth() {
        // IMPOSSIBLE BOARD STATE - may cause problems
        BoardInterface boardInterface = new BoardInterface(BoardBuilder.buildBoard("rn2kbQr/p2ppppp/1p6/8/8/6P1/PPPPP2P/RqB1K1NR w KQkq - 0 1"));
        Coordinate start1 = new Coordinate(3, 7);
        Coordinate start2 = new Coordinate(3, 0);
        test(boardInterface, start1, start2, 0, 0, true);

    }

    @Test
    void testPromotionMapRook() {

        BoardInterface boardInterface = new BoardInterface(BoardBuilder.buildBoard("r1bqkb1r/p1pppppp/8/1p6/5R1P/8/1PPPPPP1/RNBQKBNR w KQkq - 0 1"));
        pieceMap.getObservations().forEach(observation -> observation.observe(boardInterface));
        this.pieceMap.deduce(boardInterface);
        Map<Coordinate, Map<Coordinate, Path>> map = this.pieceMap.getStartLocations();
        Coordinate start1 = new Coordinate(3, 0);

        System.out.println(this.pieceMap.getStartLocations());
        System.out.println(this.pawnMapWhite.getPawnOrigins());
        System.out.println(this.pieceMap.startPiecePairs);
        System.out.println(pieceMap.getPromotedPieceMap());
        for (int i = 0 ; i < 5 ; i++) {
            Assertions.assertEquals(3, pieceMap.getPromotedPieceMap().get(new Coordinate(i, 7)).size(), new Coordinate(i, 7).toString());
        }
        for (int i = 5 ; i < 8 ; i++) {
            Assertions.assertEquals(0, pieceMap.getPromotedPieceMap().get(new Coordinate(i, 7)).size(), new Coordinate(i, 7).toString());
        }
        Assertions.assertTrue(pieceMap.getState());
//        System.out.println(pieceMap.promotedPieceMap);

//        Assertions.assertEquals(0, map.get(start1).size());
//        Assertions.assertEquals(0, map.get(start2).size());

    }

    @Test
    void testPromotionMapRookTwo() {

        BoardInterface boardInterface = new BoardInterface(BoardBuilder.buildBoard("rnb1k3/1ppp4/4pppp/p7/5R2/8/PPPPPPP1/RNBQKBNR w KQq - 0 1"));
        pieceMap.getObservations().forEach(observation -> observation.observe(boardInterface));
        this.pieceMap.deduce(boardInterface);
        Map<Coordinate, Map<Coordinate, Path>> map = this.pieceMap.getStartLocations();
        System.out.println(this.pieceMap.getStartLocations());
        System.out.println(this.pawnMapBlack.getPawnOrigins());
        System.out.println(this.pawnMapWhite.getPawnOrigins());

        System.out.println(this.pieceMap.startPiecePairs);
        System.out.println(pieceMap.getPromotedPieceMap());
        for (int i = 0 ; i < 2 ; i++) {
            Assertions.assertEquals(2, pieceMap.getPromotedPieceMap().get(new Coordinate(i, 7)).size(), new Coordinate(i, 7).toString());
        }
        for (int i = 2 ; i < 8 ; i++) {
            Assertions.assertEquals(0, pieceMap.getPromotedPieceMap().get(new Coordinate(i, 7)).size(), new Coordinate(i, 7).toString());
        }
        Assertions.assertTrue(pieceMap.getState());


    }

    @Test
    void testPromotionMapRookThree() {

        BoardInterface boardInterface = new BoardInterface(BoardBuilder.buildBoard("r1bqkb2/pppppppR/7p/8/8/7P/PPPPPP2/RNBQKBNR w KQq - 0 1"));
        pieceMap.getObservations().forEach(observation -> observation.observe(boardInterface));
        this.pieceMap.deduce(boardInterface);
        Map<Coordinate, Map<Coordinate, Path>> map = this.pieceMap.getStartLocations();
        System.out.println(this.pieceMap.getStartLocations());
        System.out.println(this.pawnMapBlack.getPawnOrigins());
        System.out.println(this.pawnMapWhite.getPawnOrigins());

        System.out.println(this.pieceMap.startPiecePairs);
        System.out.println(pieceMap.getPromotedPieceMap());
        for (int i = 0 ; i < 6 ; i++) {
            Assertions.assertEquals(0, pieceMap.getPromotedPieceMap().get(new Coordinate(i, 7)).size(), new Coordinate(i, 7).toString());
        }
        for (int i = 6 ; i < 8 ; i++) {
            Assertions.assertEquals(1, pieceMap.getPromotedPieceMap().get(new Coordinate(i, 7)).size(), new Coordinate(i, 7).toString());
        }
        Assertions.assertTrue(pieceMap.getState());


    }

    @Test
    void testPromotionMapRookFour() {

        BoardInterface boardInterface = new BoardInterface(BoardBuilder.buildBoard("r1bqkb1r/pp2pppp/1p6/3p1R2/8/8/PP1PPPPP/R1BQKB1R w KQkq - 0 1"));
        pieceMap.getObservations().forEach(observation -> observation.observe(boardInterface));
        this.pieceMap.deduce(boardInterface);
        Map<Coordinate, Map<Coordinate, Path>> map = this.pieceMap.getStartLocations();
        System.out.println(this.pieceMap.getStartLocations());
        System.out.println(this.pawnMapBlack.getPawnOrigins());
        System.out.println(this.pawnMapWhite.getPawnOrigins());

        System.out.println(this.pieceMap.startPiecePairs);
        System.out.println(pieceMap.getPromotedPieceMap());
        for (int i = 0 ; i < 5 ; i++) {
            Assertions.assertEquals(1, pieceMap.getPromotedPieceMap().get(new Coordinate(i, 7)).size(), new Coordinate(i, 7).toString());
        }
        for (int i = 5 ; i < 8 ; i++) {
            Assertions.assertEquals(0, pieceMap.getPromotedPieceMap().get(new Coordinate(i, 7)).size(), new Coordinate(i, 7).toString());
        }
        Assertions.assertTrue(pieceMap.getState());


    }

    @Test
    void testPromotionMapRookFive() {

        BoardInterface boardInterface = new BoardInterface(BoardBuilder.buildBoard("r1bqkb1r/pRpppppp/1p6/2R5/8/8/PP1PPPPP/R1BQKB1R w KQkq - 0 1"));
        pieceMap.getObservations().forEach(observation -> observation.observe(boardInterface));
        this.pieceMap.deduce(boardInterface);
        Map<Coordinate, Map<Coordinate, Path>> map = this.pieceMap.getStartLocations();
        System.out.println(this.pieceMap.getStartLocations());
        System.out.println(this.pawnMapBlack.getPawnOrigins());
        System.out.println(this.pawnMapWhite.getPawnOrigins());

        System.out.println(this.pieceMap.startPiecePairs);
        System.out.println(pieceMap.getPromotedPieceMap());
        for (int i = 0 ; i < 4 ; i++) {
            Assertions.assertEquals(1, pieceMap.getPromotedPieceMap().get(new Coordinate(i, 7)).size(), new Coordinate(i, 7).toString());
        }
        for (int i = 5 ; i < 8 ; i++) {
            Assertions.assertEquals(0, pieceMap.getPromotedPieceMap().get(new Coordinate(i, 7)).size(), new Coordinate(i, 7).toString());
        }
        Assertions.assertFalse(pieceMap.getState());


    }

    @Test
    void testPromotionMapBishop() {

        BoardInterface boardInterface = new BoardInterface(BoardBuilder.buildBoard("r1bqkb1r/p1pppppp/1p6/6B1/3B4/3P4/PP2PPPP/RN1QKBNR w KQkq - 0 1"));
        pieceMap.getObservations().forEach(observation -> observation.observe(boardInterface));
        this.pieceMap.deduce(boardInterface);
        Map<Coordinate, Map<Coordinate, Path>> map = this.pieceMap.getStartLocations();

        System.out.println(this.pieceMap.getStartLocations());
        System.out.println(this.pawnMapBlack.getPawnOrigins());
        System.out.println(this.pawnMapWhite.getPawnOrigins());

        System.out.println(this.pieceMap.startPiecePairs);
        System.out.println(pieceMap.getPromotedPieceMap());
        for (int i = 0 ; i < 0 ; i++) {
            Assertions.assertEquals(2, pieceMap.getPromotedPieceMap().get(new Coordinate(i, 7)).size(), new Coordinate(i, 7).toString());
        }
        for (int i = 0 ; i < 8 ; i++) {
            Assertions.assertEquals(0, pieceMap.getPromotedPieceMap().get(new Coordinate(i, 7)).size(), new Coordinate(i, 7).toString());
        }
        Assertions.assertTrue(pieceMap.getState());


    }

    @Test
    void testPromotionMapBishopTwo() {

        BoardInterface boardInterface = new BoardInterface(BoardBuilder.buildBoard("r3k2r/2p2ppp/p2pp3/1p4B1/3B2B1/4PB2/5PPP/RN1QK1NR w KQkq - 0 1"));
        pieceMap.getObservations().forEach(observation -> observation.observe(boardInterface));
        this.pieceMap.deduce(boardInterface);
        Map<Coordinate, Map<Coordinate, Path>> map = this.pieceMap.getStartLocations();

        System.out.println(this.pieceMap.getStartLocations());
        System.out.println(this.pawnMapBlack.getPawnOrigins());
        System.out.println(this.pawnMapWhite.getPawnOrigins());

        System.out.println(this.pieceMap.startPiecePairs);
        System.out.println(pieceMap.getPromotedPieceMap());
        for (int i = 0 ; i < 6 ; i++) {
            Assertions.assertEquals(2, pieceMap.getPromotedPieceMap().get(new Coordinate(i, 7)).size(), new Coordinate(i, 7).toString());
        }
        for (int i = 6 ; i < 8 ; i++) {
            Assertions.assertEquals(0, pieceMap.getPromotedPieceMap().get(new Coordinate(i, 7)).size(), new Coordinate(i, 7).toString());
        }
        Assertions.assertTrue(pieceMap.getState());


    }

    @Test
    void testPromotionMapBishopThree() {

        BoardInterface boardInterface = new BoardInterface(BoardBuilder.buildBoard("rnbqkb1r/ppppppp1/6p1/8/4B3/4P3/PPPP1PP1/R1BQKBNR w KQkq - 0 1"));
        pieceMap.getObservations().forEach(observation -> observation.observe(boardInterface));
        this.pieceMap.deduce(boardInterface);
        Map<Coordinate, Map<Coordinate, Path>> map = this.pieceMap.getStartLocations();

        System.out.println(this.pieceMap.getStartLocations());
        System.out.println(this.pawnMapBlack.getPawnOrigins());
        System.out.println(this.pawnMapWhite.getPawnOrigins());

        System.out.println(this.pieceMap.startPiecePairs);
        System.out.println(pieceMap.getPromotedPieceMap());
        for (int i = 0 ; i < 0 ; i++) {
            Assertions.assertEquals(2, pieceMap.getPromotedPieceMap().get(new Coordinate(i, 7)).size(), new Coordinate(i, 7).toString());
        }
        for (int i = 0; i < 8 ; i++) {
            Assertions.assertEquals(0, pieceMap.getPromotedPieceMap().get(new Coordinate(i, 7)).size(), new Coordinate(i, 7).toString());
        }
        Assertions.assertTrue(pieceMap.getState());


    }

    @Test
    void testPromotionMapBishopFour() {

        BoardInterface boardInterface = new BoardInterface(BoardBuilder.buildBoard("r1b1kbnr/pp1ppppp/1p6/8/5B2/8/PP1PPPPP/R1BQKB1R w KQkq - 0 1"));
        pieceMap.getObservations().forEach(observation -> observation.observe(boardInterface));
        this.pieceMap.deduce(boardInterface);
        Map<Coordinate, Map<Coordinate, Path>> map = this.pieceMap.getStartLocations();

        System.out.println(this.pieceMap.getStartLocations());
        System.out.println(this.pawnMapBlack.getPawnOrigins());
        System.out.println(this.pawnMapWhite.getPawnOrigins());

        System.out.println(this.pieceMap.startPiecePairs);
        System.out.println(pieceMap.getPromotedPieceMap());
        for (int i = 0 ; i < 0 ; i++) {
            Assertions.assertEquals(2, pieceMap.getPromotedPieceMap().get(new Coordinate(i, 7)).size(), new Coordinate(i, 7).toString());
        }
        for (int i = 0; i < 8 ; i++) {
            if (i == 1 || i == 3) {
                Assertions.assertEquals(1, pieceMap.getPromotedPieceMap().get(new Coordinate(i, 7)).size(), new Coordinate(i, 7).toString());

            } else {
                Assertions.assertEquals(0, pieceMap.getPromotedPieceMap().get(new Coordinate(i, 7)).size(), new Coordinate(i, 7).toString());
            }
        }
        Assertions.assertTrue(pieceMap.getState());


    }

    @Test
    void testPromotionMapBishopFive() {

        BoardInterface boardInterface = new BoardInterface(BoardBuilder.buildBoard("rBb1kbnr/p1pp1ppp/8/8/8/8/P1PPPPPP/R1BQKB1R w KQkq - 0 1"));
        pieceMap.getObservations().forEach(observation -> observation.observe(boardInterface));
        this.pieceMap.deduce(boardInterface);
        Map<Coordinate, Map<Coordinate, Path>> map = this.pieceMap.getStartLocations();

        System.out.println(this.pieceMap.getStartLocations());
        System.out.println(this.pawnMapBlack.getPawnOrigins());
        System.out.println(this.pawnMapWhite.getPawnOrigins());

        System.out.println(this.pieceMap.startPiecePairs);
        System.out.println(this.pieceMap.getStartLocations());
        System.out.println(pieceMap.getPromotedPieceMap());
        for (int i = 0 ; i < 1 ; i++) {
            Assertions.assertEquals(0, pieceMap.getPromotedPieceMap().get(new Coordinate(i, 7)).size(), new Coordinate(i, 7).toString());
        }
        for (int i = 1 ; i < 2 ; i++) {
            Assertions.assertEquals(1, pieceMap.getPromotedPieceMap().get(new Coordinate(i, 7)).size(), new Coordinate(i, 7).toString());
        }
        for (int i = 2 ; i < 8 ; i++) {
            Assertions.assertEquals(0, pieceMap.getPromotedPieceMap().get(new Coordinate(i, 7)).size(), new Coordinate(i, 7).toString());
        }
        Assertions.assertTrue(pieceMap.getState());

//        for (int i = 0; i < 8 ; i++) {
//            if (i == 1 || i == 3) {
//                Assertions.assertEquals(1, pieceMap.getPromotedPieceMap().get(new Coordinate(i, 7)).size(), new Coordinate(i, 7).toString());
//
//            } else {
//                Assertions.assertEquals(0, pieceMap.getPromotedPieceMap().get(new Coordinate(i, 7)).size(), new Coordinate(i, 7).toString());
//            }
//        }

    }

    @Test
    void testPromotionMapKnight() {

        BoardInterface boardInterface = new BoardInterface(BoardBuilder.buildBoard("rnbqkbn1/ppppppp1/4r3/3N2p1/4N3/6P1/PPPPPP2/R1BQK1NR w KQq - 0 1"));
        pieceMap.getObservations().forEach(observation -> observation.observe(boardInterface));
        this.pieceMap.deduce(boardInterface);
        Map<Coordinate, Map<Coordinate, Path>> map = this.pieceMap.getStartLocations();

        System.out.println(this.pieceMap.getStartLocations());
        System.out.println(this.pawnMapBlack.getPawnOrigins());
        System.out.println(this.pawnMapWhite.getPawnOrigins());

        System.out.println(this.pieceMap.startPiecePairs);
        System.out.println(pieceMap.getPromotedPieceMap());
        for (int i = 1 ; i < 2 ; i++) {
            Assertions.assertEquals(3, pieceMap.getPromotedPieceMap().get(new Coordinate(i, 7)).size(), new Coordinate(i, 7).toString());
        }
        for (int i = 2 ; i < 6 ; i++) {
            Assertions.assertEquals(0, pieceMap.getPromotedPieceMap().get(new Coordinate(i, 7)).size(), new Coordinate(i, 7).toString());
        }
        for (int i = 6; i < 8 ; i++) {
            Assertions.assertEquals(3, pieceMap.getPromotedPieceMap().get(new Coordinate(i, 7)).size(), new Coordinate(i, 7).toString());
        }
        Assertions.assertTrue(pieceMap.getState());


    }

    @Test
    void testPromotionMapBishopTrapped() {

        BoardInterface boardInterface = new BoardInterface(BoardBuilder.buildBoard("k7/8/1K6/8/8/6P1/PPPP1PPB/6N1 w - - 0 1"));
        pieceMap.getObservations().forEach(observation -> observation.observe(boardInterface));
        this.pieceMap.deduce(boardInterface);
        Map<Coordinate, Map<Coordinate, Path>> map = this.pieceMap.getStartLocations();

        System.out.println(this.pieceMap.getStartLocations());
        System.out.println(this.pawnMapBlack.getPawnOrigins());
        System.out.println(this.pawnMapWhite.getPawnOrigins());

        System.out.println(this.pieceMap.getStartLocations());
        System.out.println(pieceMap.getPromotedPieceMap());
        for (int i = 1 ; i < 8 ; i++) {
            Assertions.assertEquals(0, pieceMap.getPromotedPieceMap().get(new Coordinate(i, 7)).size(), new Coordinate(i, 7).toString());
        }
//        for (int i = 2 ; i < 6 ; i++) {
//            Assertions.assertEquals(0, pieceMap.getPromotedPieceMap().get(new Coordinate(i, 7)).size(), new Coordinate(i, 7).toString());
//        }
//        for (int i = 6; i < 8 ; i++) {
//            Assertions.assertEquals(3, pieceMap.getPromotedPieceMap().get(new Coordinate(i, 7)).size(), new Coordinate(i, 7).toString());
//        }

        Assertions.assertFalse(pieceMap.getState());

    }

    @Test
    void testPromotionMapBishopTrappedTwo() {

        BoardInterface boardInterface = new BoardInterface(BoardBuilder.buildBoard("rnbq2nr/pppp1ppp/3k4/8/8/5K2/PPPP1PPP/RNBQBBNR"));
        pieceMap.getObservations().forEach(observation -> observation.observe(boardInterface));
        this.pieceMap.deduce(boardInterface);
        Map<Coordinate, Map<Coordinate, Path>> map = this.pieceMap.getStartLocations();

        System.out.println(this.pieceMap.getStartLocations());
        System.out.println(this.pawnMapBlack.getPawnOrigins());
        System.out.println(this.pawnMapWhite.getPawnOrigins());

        System.out.println(this.pieceMap.getStartLocations());
        System.out.println(pieceMap.getPromotedPieceMap());
        for (int i = 1 ; i < 8 ; i++) {
            Assertions.assertEquals(0, pieceMap.getPromotedPieceMap().get(new Coordinate(i, 7)).size(), new Coordinate(i, 7).toString());
        }
//        for (int i = 2 ; i < 6 ; i++) {
//            Assertions.assertEquals(0, pieceMap.getPromotedPieceMap().get(new Coordinate(i, 7)).size(), new Coordinate(i, 7).toString());
//        }
//        for (int i = 6; i < 8 ; i++) {
//            Assertions.assertEquals(3, pieceMap.getPromotedPieceMap().get(new Coordinate(i, 7)).size(), new Coordinate(i, 7).toString());
//        }

        Assertions.assertFalse(pieceMap.getState());

    }

    @Test
    void testPromotionMapRookTrapped() {

        BoardInterface boardInterface = new BoardInterface(BoardBuilder.buildBoard("1nbqkbnr/1ppppppp/1p6/8/8/8/4PPPP/RNBQKBRR w KQk - 0 1"));
        pieceMap.getObservations().forEach(observation -> observation.observe(boardInterface));
        this.pieceMap.deduce(boardInterface);
        Map<Coordinate, Map<Coordinate, Path>> map = this.pieceMap.getStartLocations();

        System.out.println(this.pieceMap.getStartLocations());
        System.out.println(this.pawnMapBlack.getPawnOrigins());
        System.out.println(this.pawnMapWhite.getPawnOrigins());

        System.out.println(this.pieceMap.getStartLocations());
        System.out.println(pieceMap.getPromotedPieceMap());
        for (int i = 1 ; i < 8 ; i++) {
            Assertions.assertEquals(0, pieceMap.getPromotedPieceMap().get(new Coordinate(i, 7)).size(), new Coordinate(i, 7).toString());
        }
//        for (int i = 2 ; i < 6 ; i++) {
//            Assertions.assertEquals(0, pieceMap.getPromotedPieceMap().get(new Coordinate(i, 7)).size(), new Coordinate(i, 7).toString());
//        }
//        for (int i = 6; i < 8 ; i++) {
//            Assertions.assertEquals(3, pieceMap.getPromotedPieceMap().get(new Coordinate(i, 7)).size(), new Coordinate(i, 7).toString());
//        }

        Assertions.assertTrue(pieceMap.getState());

    }

    @Test
    void testPromotionMapRookNotTrapped() {

        BoardInterface boardInterface = new BoardInterface(BoardBuilder.buildBoard("1nbqkbnr/1ppppppp/1p6/8/8/8/5PPP/RNBQKBRR w KQk - 0 1"));
        pieceMap.getObservations().forEach(observation -> observation.observe(boardInterface));
        this.pieceMap.deduce(boardInterface);
        Map<Coordinate, Map<Coordinate, Path>> map = this.pieceMap.getStartLocations();

        System.out.println(this.pieceMap.getStartLocations());
        System.out.println(this.pawnMapBlack.getPawnOrigins());
        System.out.println(this.pawnMapWhite.getPawnOrigins());

        System.out.println(this.pieceMap.getStartLocations());
        System.out.println(pieceMap.getPromotedPieceMap());
        for (int i = 1 ; i < 2 ; i++) {
            Assertions.assertEquals(3, pieceMap.getPromotedPieceMap().get(new Coordinate(i, 7)).size(), new Coordinate(i, 7).toString());
        }
        for (int i = 2 ; i < 8 ; i++) {
            Assertions.assertEquals(0, pieceMap.getPromotedPieceMap().get(new Coordinate(i, 7)).size(), new Coordinate(i, 7).toString());
        }
//        for (int i = 2 ; i < 6 ; i++) {
//            Assertions.assertEquals(0, pieceMap.getPromotedPieceMap().get(new Coordinate(i, 7)).size(), new Coordinate(i, 7).toString());
//        }
//        for (int i = 6; i < 8 ; i++) {
//            Assertions.assertEquals(3, pieceMap.getPromotedPieceMap().get(new Coordinate(i, 7)).size(), new Coordinate(i, 7).toString());
//        }
        Assertions.assertTrue(pieceMap.getState());


    }


    @Test
    void testMultiple() {

        //NOT A TEST
        BoardInterface boardInterface = new BoardInterface(BoardBuilder.buildBoard("4kb2/pRppppp1/1p6/P7/8/8/B1R1P3/RNBQKBNR w KQ - 0 1"));
        pieceMap.getObservations().forEach(observation -> observation.observe(boardInterface));
        this.pieceMap.deduce(boardInterface);
        Map<Coordinate, Map<Coordinate, Path>> map = this.pieceMap.getStartLocations();
        Coordinate start1 = new Coordinate(3, 0);
        Coordinate start2 = new Coordinate(4, 0);

        System.out.println(this.pieceMap.getStartLocations());
        System.out.println(this.pawnMapWhite.getPawnOrigins());
        System.out.println(this.pieceMap.getPromotedPieceMap());
        System.out.println(pieceMap.getPromotionNumbers());
//        System.out.println(pieceMap.promotedPieceMap);

//        Assertions.assertEquals(0, map.get(start1).size());
//        Assertions.assertEquals(0, map.get(start2).size());

    }

    //FAILING TESTS

    @Test
    void testRooksBehindRoyalty() {
        BoardInterface boardInterface = new BoardInterface(BoardBuilder.buildBoard("rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/1QR2RK1 w kq - 0 1"));
        Coordinate start1 = new Coordinate(0, 0);
        Coordinate start2 = new Coordinate(7, 0);
        test(boardInterface, start1, start2, 0, 0, true);

    }

    @Test
    void testPromotionMapKnightImpossiblePawnTiming() {

        BoardInterface boardInterface = new BoardInterface(BoardBuilder.buildBoard("rnbqkbn1/ppppppp1/5rp1/8/4N3/8/PPPPPPP1/RNBQKBN1 w Qq - 0 1"));
        pieceMap.getObservations().forEach(observation -> observation.observe(boardInterface));
        this.pieceMap.deduce(boardInterface);
        Map<Coordinate, Map<Coordinate, Path>> map = this.pieceMap.getStartLocations();

        System.out.println(this.pieceMap.getStartLocations());
        System.out.println(this.pawnMapBlack.getPawnOrigins());
        System.out.println(this.pawnMapWhite.getPawnOrigins());

        System.out.println(this.pieceMap.startPiecePairs);
        System.out.println(pieceMap.getPromotedPieceMap());
//        for (int i = 1 ; i < 2 ; i++) {
//            Assertions.assertEquals(3, pieceMap.getPromotedPieceMap().get(new Coordinate(i, 7)).size(), new Coordinate(i, 7).toString());
//        }
//        for (int i = 2 ; i < 6 ; i++) {
//            Assertions.assertEquals(0, pieceMap.getPromotedPieceMap().get(new Coordinate(i, 7)).size(), new Coordinate(i, 7).toString());
//        }
        for (int i = 7; i < 8 ; i++) {
            Assertions.assertEquals(0, pieceMap.getPromotedPieceMap().get(new Coordinate(i, 7)).size(), new Coordinate(i, 7).toString());
        }

    }
}
