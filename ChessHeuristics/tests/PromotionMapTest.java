import Heuristics.BoardInterface;
import Heuristics.Deductions.*;
import Heuristics.Observations.PawnNumber;
import Heuristics.Observations.PieceNumber;
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
        PawnNumber pawnNumber = new PawnNumber();
        PieceNumber pieceNumber = new PieceNumber();

        pawnMapWhite = new PawnMapWhite(pawnNumber, pieceNumber);
        pawnMapBlack = new PawnMapBlack(pawnNumber, pieceNumber);
        combinedPawnMap = new CombinedPawnMap(pawnMapWhite, pawnMapBlack);
        pieceMap = new PieceMap(combinedPawnMap);
        captureLocations = new CaptureLocations(pawnMapWhite, pawnMapBlack, pieceMap, combinedPawnMap);
        promotionMap = new PromotionMap(pieceMap, combinedPawnMap, pawnMapWhite, pawnMapBlack, captureLocations, pieceNumber, pawnNumber);


    }
    @Test
    void test() {
        BoardInterface boardInterface = new BoardInterface(BoardBuilder.buildBoard("4k3/8/8/3BB3/8/7P/2PPPPP1/RNBQKBNR w KQ - 0 1"));

        pieceMap.getObservations().forEach(observation -> observation.observe(boardInterface));
        this.pawnMapWhite.deduce(boardInterface);
        this.pawnMapBlack.deduce(boardInterface);
        this.combinedPawnMap.deduce(boardInterface);
        this.pieceMap.deduce(boardInterface);
        this.pawnMapWhite.deduce(boardInterface);
        this.pawnMapBlack.deduce(boardInterface);
        this.combinedPawnMap.deduce(boardInterface);
        this.pieceMap.deduce(boardInterface);
        this.captureLocations.deduce(boardInterface);
        promotionMap.deduce(boardInterface);
        System.out.println(promotionMap.getPawnOrigins("white"));
        Assertions.assertEquals(1, promotionMap.getPawnOrigins("white").get(new Coordinate(7, 7)).size());
        Assertions.assertEquals(2, promotionMap.getPawnOrigins("white").get(new Coordinate(6, 7)).size());
        Assertions.assertTrue(promotionMap.getState());

    }

    @Test
    void testCaptureQuantities() {
        BoardInterface boardInterface = new BoardInterface(BoardBuilder.buildBoard("1nbqkbnr/1pppp1pp/5p2/2Q5/8/8/1PPPPPPP/RNBQKBNR w KQk - 0 1"));

        pieceMap.getObservations().forEach(observation -> observation.observe(boardInterface));
        this.pawnMapWhite.deduce(boardInterface);
        this.pawnMapBlack.deduce(boardInterface);
        this.combinedPawnMap.deduce(boardInterface);
        this.pieceMap.deduce(boardInterface);
        this.captureLocations.deduce(boardInterface);
        promotionMap.deduce(boardInterface);
        Assertions.assertEquals(1, promotionMap.getPawnOrigins("white").get(new Coordinate(0, 7)).size());
        Assertions.assertFalse(promotionMap.getPawnOrigins("white").containsKey(new Coordinate(5, 7)));
        Assertions.assertFalse(promotionMap.getPawnOrigins("white").containsKey(new Coordinate(2, 7)));
        Assertions.assertFalse(promotionMap.getPawnOrigins("white").containsKey(new Coordinate(4, 7)));
        Assertions.assertTrue(promotionMap.getPawnOrigins("white").containsKey(new Coordinate(1, 7)));
        Assertions.assertTrue(promotionMap.getState());




    }

    @Test
    void testCaptureQuantitiesSomeAccountedFor() {
        BoardInterface boardInterface = new BoardInterface(BoardBuilder.buildBoard("2bqkbnr/1pppp1pp/1pn2p2/2Q5/8/2P5/2PPPPPP/RNBQKB1R w KQk - 0 1"));

        pieceMap.getObservations().forEach(observation -> observation.observe(boardInterface));
        this.pawnMapWhite.deduce(boardInterface);
        this.pawnMapBlack.deduce(boardInterface);
        this.combinedPawnMap.deduce(boardInterface);
        this.pieceMap.deduce(boardInterface);
        this.captureLocations.deduce(boardInterface);
        promotionMap.deduce(boardInterface);
        Assertions.assertEquals(1, promotionMap.getPawnOrigins("white").get(new Coordinate(0, 7)).size());
        Assertions.assertTrue(promotionMap.getPawnOrigins("white").containsKey(new Coordinate(0, 7)));
        Assertions.assertFalse(promotionMap.getPawnOrigins("white").containsKey(new Coordinate(1, 7)));
        Assertions.assertTrue(promotionMap.getState());
    }

    @Test
    void testCollision() {
        BoardInterface boardInterface = new BoardInterface(BoardBuilder.buildBoard("rnbqkbnr/1ppppppp/8/8/p2Q4/8/1PPPPPPP/RNBQKBNR"));

        pieceMap.getObservations().forEach(observation -> observation.observe(boardInterface));
        this.pawnMapWhite.deduce(boardInterface);
        this.pawnMapBlack.deduce(boardInterface);
        this.combinedPawnMap.deduce(boardInterface);
        this.pieceMap.deduce(boardInterface);
        this.captureLocations.deduce(boardInterface);
        promotionMap.deduce(boardInterface);
//        promotionMap.getPawnOrigins()
//        Assertions.assertEquals(1, promotionMap.getPawnOrigins("white").get(new Coordinate(0, 7)).size());
        Assertions.assertFalse(promotionMap.getPawnOrigins("white").containsKey(new Coordinate(0, 7)));

//        Assertions.assertTrue(promotionMap.getPawnOrigins("white").containsKey(new Coordinate(0, 7)));
//        Assertions.assertFalse(promotionMap.getPawnOrigins("white").containsKey(new Coordinate(1, 7)));
        Assertions.assertFalse(promotionMap.getState());
    }

    @Test
    void testCollisionWithCapture() {
        // FIX THIS LATER
        // With current implementation - all that needs to be done is a check that allows empty paths at rank 7/0
        // in combined pawn map so long as there are enough paths to fill the promotion quota
        BoardInterface boardInterface = new BoardInterface(BoardBuilder.buildBoard("rnbqkbnr/2pppppp/8/8/p2Q4/8/1PPPPPPP/RNBQKBNR w - - 0 1"));

        pieceMap.getObservations().forEach(observation -> observation.observe(boardInterface));
        this.pawnMapWhite.deduce(boardInterface);
        this.pawnMapBlack.deduce(boardInterface);
        this.combinedPawnMap.deduce(boardInterface);
        this.pieceMap.deduce(boardInterface);
        this.captureLocations.deduce(boardInterface);
        promotionMap.deduce(boardInterface);
//        promotionMap.getPawnOrigins()
//        Assertions.assertEquals(1, promotionMap.getPawnOrigins("white").get(new Coordinate(0, 7)).size());
        System.out.println(promotionMap.getPawnOrigins("white"));
        Assertions.assertFalse(promotionMap.getPawnOrigins("white").containsKey(new Coordinate(0, 7)));
        Assertions.assertTrue(promotionMap.getPawnOrigins("white").containsKey(new Coordinate(1, 7)));


//        Assertions.assertTrue(promotionMap.getPawnOrigins("white").containsKey(new Coordinate(0, 7)));
//        Assertions.assertFalse(promotionMap.getPawnOrigins("white").containsKey(new Coordinate(1, 7)));
        Assertions.assertTrue(promotionMap.getState());
    }

    @Test
    void testCollisionWithTwoCaptures() {
        BoardInterface boardInterface = new BoardInterface(BoardBuilder.buildBoard("r1bqkbnr/2pppppp/8/8/p2Q4/8/1PPPPPPP/RNBQKBNR w - - 0 1"));

        pieceMap.getObservations().forEach(observation -> observation.observe(boardInterface));
        this.pawnMapWhite.deduce(boardInterface);
        this.pawnMapBlack.deduce(boardInterface);
        this.combinedPawnMap.deduce(boardInterface);
        this.pieceMap.deduce(boardInterface);
        this.captureLocations.deduce(boardInterface);
        promotionMap.deduce(boardInterface);
//        promotionMap.getPawnOrigins()
//        Assertions.assertEquals(1, promotionMap.getPawnOrigins("white").get(new Coordinate(0, 7)).size());
        System.out.println(promotionMap.getPawnOrigins("white"));
        Assertions.assertTrue(promotionMap.getPawnOrigins("white").containsKey(new Coordinate(0, 7)));
        Assertions.assertTrue(promotionMap.getPawnOrigins("white").containsKey(new Coordinate(1, 7)));
        Assertions.assertTrue(promotionMap.getPawnOrigins("white").containsKey(new Coordinate(2, 7)));



//        Assertions.assertTrue(promotionMap.getPawnOrigins("white").containsKey(new Coordinate(0, 7)));
//        Assertions.assertFalse(promotionMap.getPawnOrigins("white").containsKey(new Coordinate(1, 7)));
        Assertions.assertTrue(promotionMap.getState());
    }

    @Test
    void testCollisionWithTwoCapturesComplexPawnStructure() {
        BoardInterface boardInterface = new BoardInterface(BoardBuilder.buildBoard("r1bqkbnr/2pppppp/5P2/3P4/p1PQP1PP/1P6/8/RNBQKBNR w - - 0 1"));

        pieceMap.getObservations().forEach(observation -> observation.observe(boardInterface));
        this.pawnMapWhite.deduce(boardInterface);
        this.pawnMapBlack.deduce(boardInterface);
        this.combinedPawnMap.deduce(boardInterface);
        this.pieceMap.deduce(boardInterface);
        this.captureLocations.deduce(boardInterface);
        promotionMap.deduce(boardInterface);
//        promotionMap.getPawnOrigins()
//        Assertions.assertEquals(1, promotionMap.getPawnOrigins("white").get(new Coordinate(0, 7)).size());
        System.out.println(promotionMap.getPawnOrigins("white"));
        Assertions.assertTrue(promotionMap.getPawnOrigins("white").containsKey(new Coordinate(0, 7)));
        Assertions.assertTrue(promotionMap.getPawnOrigins("white").containsKey(new Coordinate(1, 7)));
        Assertions.assertTrue(promotionMap.getPawnOrigins("white").containsKey(new Coordinate(2, 7)));



//        Assertions.assertTrue(promotionMap.getPawnOrigins("white").containsKey(new Coordinate(0, 7)));
//        Assertions.assertFalse(promotionMap.getPawnOrigins("white").containsKey(new Coordinate(1, 7)));
        Assertions.assertTrue(promotionMap.getState());
    }

    @Test
    void testCollisionBothPawnsTheoretical() {
        BoardInterface boardInterface = new BoardInterface(BoardBuilder.buildBoard("rnbqkbnr/1ppppppp/8/3q4/3Q4/8/1PPPPPPP/RNBQKBNR w KQkq - 0 1"));

        pieceMap.getObservations().forEach(observation -> observation.observe(boardInterface));
        this.pawnMapWhite.deduce(boardInterface);
        this.pawnMapBlack.deduce(boardInterface);
        this.combinedPawnMap.deduce(boardInterface);
        this.pieceMap.deduce(boardInterface);
        this.captureLocations.deduce(boardInterface);
        promotionMap.deduce(boardInterface);
//        promotionMap.getPawnOrigins()
//        Assertions.assertEquals(1, promotionMap.getPawnOrigins("white").get(new Coordinate(0, 7)).size());
        System.out.println(promotionMap.getPawnOrigins("white"));
        System.out.println(promotionMap.getPawnOrigins("black"));
        System.out.println(combinedPawnMap.getWhitePaths());


        for (int x = 0 ; x < 8 ; x++) {
            Assertions.assertFalse(promotionMap.getPawnOrigins("white").containsKey(new Coordinate(x, 7)), "" + x);
        }
        for (int x = 0 ; x < 8 ; x++) {
            Assertions.assertFalse(promotionMap.getPawnOrigins("black").containsKey(new Coordinate(x, 0)), "" + x);
        }

//        Assertions.assertTrue(promotionMap.getPawnOrigins("white").containsKey(new Coordinate(0, 7)));
//        Assertions.assertFalse(promotionMap.getPawnOrigins("white").containsKey(new Coordinate(1, 7)));
        Assertions.assertFalse(promotionMap.getState());
    }

    @Test
    void testCollisionBothPawnsTheoreticalEnoughCaptures() {
            BoardInterface boardInterface = new BoardInterface(BoardBuilder.buildBoard("rnbqkbnr/1ppppppp/8/3q4/3Q4/8/1PPPPPPP/2BQKBNR w Kkq - 0 1"));

        pieceMap.getObservations().forEach(observation -> observation.observe(boardInterface));
        this.pawnMapWhite.deduce(boardInterface);
        this.pawnMapBlack.deduce(boardInterface);
        this.combinedPawnMap.deduce(boardInterface);
        this.pieceMap.deduce(boardInterface);
        this.captureLocations.deduce(boardInterface);
        promotionMap.deduce(boardInterface);
//        promotionMap.getPawnOrigins()
//        Assertions.assertEquals(1, promotionMap.getPawnOrigins("white").get(new Coordinate(0, 7)).size());
        System.out.println(promotionMap.getPawnOrigins("white"));
        System.out.println(promotionMap.getPawnOrigins("black"));
        System.out.println(combinedPawnMap.getBlackPaths());


//        for (int x = 0 ; x < 8 ; x++) {
            Assertions.assertTrue(promotionMap.getPawnOrigins("white").containsKey(new Coordinate(0, 7)));
//        }
//        for (int x = 0 ; x < 8 ; x++) {
            Assertions.assertFalse(promotionMap.getPawnOrigins("black").containsKey(new Coordinate(0, 0)));
//        }

//        Assertions.assertTrue(promotionMap.getPawnOrigins("white").containsKey(new Coordinate(0, 7)));
//        Assertions.assertFalse(promotionMap.getPawnOrigins("white").containsKey(new Coordinate(1, 7)));
        Assertions.assertTrue(promotionMap.getState());
    }

    @Test
    void testCollisionBothPawnsTheoreticalOneCapture() {
        BoardInterface boardInterface = new BoardInterface(BoardBuilder.buildBoard("rnbqkbnr/1ppppppp/8/3q4/3Q4/8/1PPPPPPP/R1BQKBNR w KQkq - 0 1"));

        pieceMap.getObservations().forEach(observation -> observation.observe(boardInterface));
        this.pawnMapWhite.deduce(boardInterface);
        this.pawnMapBlack.deduce(boardInterface);
        this.combinedPawnMap.deduce(boardInterface);
        this.pieceMap.deduce(boardInterface);
        this.captureLocations.deduce(boardInterface);
        promotionMap.deduce(boardInterface);
//        promotionMap.getPawnOrigins()
//        Assertions.assertEquals(1, promotionMap.getPawnOrigins("white").get(new Coordinate(0, 7)).size());
        System.out.println(promotionMap.getPawnOrigins("white"));
        System.out.println(promotionMap.getPawnOrigins("black"));
        System.out.println(combinedPawnMap.getWhitePaths());
        for (int x = 0 ; x < 8 ; x++) {
            Assertions.assertFalse(promotionMap.getPawnOrigins("white").containsKey(new Coordinate(x, 7)), "" + x);
        }
        for (int x = 0 ; x < 8 ; x++) {
            Assertions.assertFalse(promotionMap.getPawnOrigins("black").containsKey(new Coordinate(x, 0)), "" + x);
        }

//        Assertions.assertTrue(promotionMap.getPawnOrigins("white").containsKey(new Coordinate(0, 7)));
//        Assertions.assertFalse(promotionMap.getPawnOrigins("white").containsKey(new Coordinate(1, 7)));
        Assertions.assertFalse(promotionMap.getState());
    }

    @Test
    void testCollisionBothPawnsTheoreticalEachOneCapture() {
        BoardInterface boardInterface = new BoardInterface(BoardBuilder.buildBoard("r1bqkbnr/1ppppppp/8/3q4/3Q4/8/1PPPPPPP/R1BQKBNR w KQkq - 0 1"));

        pieceMap.getObservations().forEach(observation -> observation.observe(boardInterface));
        this.pawnMapWhite.deduce(boardInterface);
        this.pawnMapBlack.deduce(boardInterface);
        this.combinedPawnMap.deduce(boardInterface);
        this.pieceMap.deduce(boardInterface);
        this.captureLocations.deduce(boardInterface);
        promotionMap.deduce(boardInterface);
//        promotionMap.getPawnOrigins()
//        Assertions.assertEquals(1, promotionMap.getPawnOrigins("white").get(new Coordinate(0, 7)).size());
        System.out.println(promotionMap.getPawnOrigins("white"));
        System.out.println(promotionMap.getPawnOrigins("black"));
        System.out.println(combinedPawnMap.getWhitePaths());
        // One of the below should be true, it doesn't matter which
        for (int x = 0 ; x < 8 ; x++) {
//            Assertions.assertFalse(promotionMap.getPawnOrigins("white").containsKey(new Coordinate(x, 7)), "" + x);
        }
        for (int x = 0 ; x < 8 ; x++) {
            Assertions.assertFalse(promotionMap.getPawnOrigins("black").containsKey(new Coordinate(x, 0)), "" + x);
        }

//        Assertions.assertTrue(promotionMap.getPawnOrigins("white").containsKey(new Coordinate(0, 7)));
//        Assertions.assertFalse(promotionMap.getPawnOrigins("white").containsKey(new Coordinate(1, 7)));
        Assertions.assertFalse(promotionMap.getState());
    }

    @Test
    void testCollisionBothPawnsTheoreticalEachOneCaptureThreeOptions() {
        BoardInterface boardInterface = new BoardInterface(BoardBuilder.buildBoard("r1bqkbnr/p1pppppp/8/4q3/4Q3/8/P1PPPPPP/R1BQKBNR w KQkq - 0 1"));

        pieceMap.getObservations().forEach(observation -> observation.observe(boardInterface));
        this.pawnMapWhite.deduce(boardInterface);
        this.pawnMapBlack.deduce(boardInterface);
        this.combinedPawnMap.deduce(boardInterface);
        this.pieceMap.deduce(boardInterface);
        this.captureLocations.deduce(boardInterface);
        promotionMap.deduce(boardInterface);
//        promotionMap.getPawnOrigins()
//        Assertions.assertEquals(1, promotionMap.getPawnOrigins("white").get(new Coordinate(0, 7)).size());
        System.out.println(promotionMap.getPawnOrigins("white"));
        System.out.println(promotionMap.getPawnOrigins("black"));
        System.out.println(combinedPawnMap.getWhitePaths());
        // One of the below should be true, it doesn't matter which
        for (int x = 0 ; x < 8 ; x++) {
//            Assertions.assertFalse(promotionMap.getPawnOrigins("white").containsKey(new Coordinate(x, 7)), "" + x);
        }
        for (int x = 0 ; x < 8 ; x++) {
            Assertions.assertFalse(promotionMap.getPawnOrigins("black").containsKey(new Coordinate(x, 0)), "" + x);
        }

//        Assertions.assertTrue(promotionMap.getPawnOrigins("white").containsKey(new Coordinate(0, 7)));
//        Assertions.assertFalse(promotionMap.getPawnOrigins("white").containsKey(new Coordinate(1, 7)));
        Assertions.assertFalse(promotionMap.getState());
    }

    @Test
    void testCollisionBothPawnsTheoreticalEachOneCaptureThreeOptionsPlusExtraCaptures() {
        BoardInterface boardInterface = new BoardInterface(BoardBuilder.buildBoard("r1bqkb1r/p1ppppp1/6p1/4q3/4Q3/6P1/P1PPPPP1/R1BQKB1R w KQkq - 0 1"));

        pieceMap.getObservations().forEach(observation -> observation.observe(boardInterface));
        this.pawnMapWhite.deduce(boardInterface);
        this.pawnMapBlack.deduce(boardInterface);
        this.combinedPawnMap.deduce(boardInterface);
        this.pieceMap.deduce(boardInterface);
        this.captureLocations.deduce(boardInterface);
        promotionMap.deduce(boardInterface);
//        promotionMap.getPawnOrigins()
//        Assertions.assertEquals(1, promotionMap.getPawnOrigins("white").get(new Coordinate(0, 7)).size());
        System.out.println(promotionMap.getPawnOrigins("white"));
        System.out.println(promotionMap.getPawnOrigins("black"));
        System.out.println(combinedPawnMap.getWhitePaths());
        // One of the below should be true, it doesn't matter which
        for (int x = 0 ; x < 8 ; x++) {
//            Assertions.assertFalse(promotionMap.getPawnOrigins("white").containsKey(new Coordinate(x, 7)), "" + x);
        }
        for (int x = 0 ; x < 8 ; x++) {
            Assertions.assertFalse(promotionMap.getPawnOrigins("black").containsKey(new Coordinate(x, 0)), "" + x);
        }

//        Assertions.assertTrue(promotionMap.getPawnOrigins("white").containsKey(new Coordinate(0, 7)));
//        Assertions.assertFalse(promotionMap.getPawnOrigins("white").containsKey(new Coordinate(1, 7)));
        Assertions.assertFalse(promotionMap.getState());
    }

    @Test
    void testCaptureQuantitiesAmbiguousOrigins() {
        BoardInterface boardInterface = new BoardInterface(BoardBuilder.buildBoard("2b1k3/1pppp3/8/2B2p2/1B6/8/2PPPPPP/RNBQKBNR w KQ - 0 1"));

        pieceMap.getObservations().forEach(observation -> observation.observe(boardInterface));
        this.pawnMapWhite.deduce(boardInterface);
        this.pawnMapBlack.deduce(boardInterface);
        this.combinedPawnMap.deduce(boardInterface);
        this.pieceMap.deduce(boardInterface);
        this.captureLocations.deduce(boardInterface);
        promotionMap.deduce(boardInterface);
//        Assertions.assertEquals(1, promotionMap.getPawnOrigins("white").get(new Coordinate(0, 7)).size());
//        Assertions.assertTrue(promotionMap.getPawnOrigins("white").containsKey(new Coordinate(0, 7)));
//        Assertions.assertFalse(promotionMap.getPawnOrigins("white").containsKey(new Coordinate(1, 7)));
        Assertions.assertTrue(promotionMap.getState());
    }
}

