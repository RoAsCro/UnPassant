import Heuristics.BoardInterface;
import Heuristics.Deductions.*;
import StandardChess.BoardBuilder;
import StandardChess.Coordinate;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class PromotionMapTest {

    PawnMapWhite pawnMapWhite;
    PawnMapBlack pawnMapBlack;
    CombinedPawnMap combinedPawnMap;
    PieceMap pieceMap;
    PromotionMap promotionMap;

    CaptureLocations captureLocations;
    @BeforeEach
    void setup() {
        pawnMapWhite = new PawnMapWhite();
        pawnMapBlack = new PawnMapBlack();
        combinedPawnMap = new CombinedPawnMap(pawnMapWhite, pawnMapBlack);
        pieceMap = new PieceMap(combinedPawnMap);
        captureLocations = new CaptureLocations(pawnMapWhite, pawnMapBlack, pieceMap, combinedPawnMap);
        promotionMap = new PromotionMap(pieceMap, combinedPawnMap, pawnMapWhite, pawnMapBlack, captureLocations);


    }
    @Test
    void test() {
        BoardInterface boardInterface = new BoardInterface(BoardBuilder.buildBoard("4k3/8/8/3BB3/8/7P/2PPPPP1/RNBQKBNR w KQ - 0 1"));

        pieceMap.getObservations().forEach(observation -> observation.observe(boardInterface));
        this.pieceMap.deduce(boardInterface);
        promotionMap.deduce(boardInterface);
        Assertions.assertEquals(1, promotionMap.getPawnOrigins("white").get(new Coordinate(7, 7)).size());
        Assertions.assertEquals(2, promotionMap.getPawnOrigins("white").get(new Coordinate(6, 7)).size());

    }

    @Test
    void testCaptureQuantities() {
        BoardInterface boardInterface = new BoardInterface(BoardBuilder.buildBoard("1nbqkbnr/1pppp1pp/5p2/2Q5/8/8/1PPPPPPP/RNBQKBNR w KQk - 0 1"));

        pieceMap.getObservations().forEach(observation -> observation.observe(boardInterface));
        this.pieceMap.deduce(boardInterface);
        promotionMap.deduce(boardInterface);
        Assertions.assertEquals(1, promotionMap.getPawnOrigins("white").get(new Coordinate(0, 7)).size());
        Assertions.assertFalse(promotionMap.getPawnOrigins("white").containsKey(new Coordinate(5, 7)));
        Assertions.assertFalse(promotionMap.getPawnOrigins("white").containsKey(new Coordinate(2, 7)));
        Assertions.assertFalse(promotionMap.getPawnOrigins("white").containsKey(new Coordinate(4, 7)));
        Assertions.assertTrue(promotionMap.getPawnOrigins("white").containsKey(new Coordinate(1, 7)));




    }

    @Test
    void testCaptureQuantitiesSomeAccountedFor() {
        BoardInterface boardInterface = new BoardInterface(BoardBuilder.buildBoard("2bqkbnr/1pppp1pp/1pn2p2/2Q5/8/2P5/2PPPPPP/RNBQKB1R w KQk - 0 1"));

        pieceMap.getObservations().forEach(observation -> observation.observe(boardInterface));
        this.pieceMap.deduce(boardInterface);
        promotionMap.deduce(boardInterface);
        Assertions.assertEquals(1, promotionMap.getPawnOrigins("white").get(new Coordinate(0, 7)).size());
        Assertions.assertTrue(promotionMap.getPawnOrigins("white").containsKey(new Coordinate(0, 7)));
        Assertions.assertFalse(promotionMap.getPawnOrigins("white").containsKey(new Coordinate(1, 7)));
    }

    @Test
    void testCaptureQuantitiesCollision() {
        BoardInterface boardInterface = new BoardInterface(BoardBuilder.buildBoard("rnbqkbnr/1ppppppp/8/8/p2Q4/8/1PPPPPPP/RNBQKBNR"));

        pieceMap.getObservations().forEach(observation -> observation.observe(boardInterface));
        this.pieceMap.deduce(boardInterface);
        promotionMap.deduce(boardInterface);
//        promotionMap.getPawnOrigins()
//        Assertions.assertEquals(1, promotionMap.getPawnOrigins("white").get(new Coordinate(0, 7)).size());
        Assertions.assertTrue(promotionMap.getPawnOrigins("white").get(new Coordinate(0, 7)).isEmpty());

//        Assertions.assertTrue(promotionMap.getPawnOrigins("white").containsKey(new Coordinate(0, 7)));
//        Assertions.assertFalse(promotionMap.getPawnOrigins("white").containsKey(new Coordinate(1, 7)));
    }

    @Test
    void testCaptureQuantitiesAmbiguousOrigins() {
        BoardInterface boardInterface = new BoardInterface(BoardBuilder.buildBoard("2b1k3/1pppp3/8/2B2p2/1B6/8/2PPPPPP/RNBQKBNR w KQ - 0 1"));

        pieceMap.getObservations().forEach(observation -> observation.observe(boardInterface));
        this.pieceMap.deduce(boardInterface);
        promotionMap.deduce(boardInterface);
//        Assertions.assertEquals(1, promotionMap.getPawnOrigins("white").get(new Coordinate(0, 7)).size());
//        Assertions.assertTrue(promotionMap.getPawnOrigins("white").containsKey(new Coordinate(0, 7)));
//        Assertions.assertFalse(promotionMap.getPawnOrigins("white").containsKey(new Coordinate(1, 7)));
    }
}
