import Heuristics.BoardInterface;
import Heuristics.Deductions.*;
import Heuristics.Detector.Data.StandardCaptureData;
import Heuristics.Detector.Data.StandardPawnData;
import Heuristics.Detector.Data.StandardPieceData;
import Heuristics.Detector.Data.StandardPromotionData;
import Heuristics.Detector.StandardStateDetector;
import StandardChess.BoardBuilder;
import StandardChess.Coordinate;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class PromotionMapTest {

//    PawnMap pawnMapWhite;
//    PawnMap pawnMapBlack;
    CombinedPawnMap combinedPawnMap;
    PieceMap pieceMap;
    PromotionMap promotionMap;

    CaptureLocations captureLocations;
    StandardStateDetector standardStateDetector;
    @BeforeEach
    void setup() {
        combinedPawnMap = new CombinedPawnMap();
        pieceMap = new PieceMap();
        captureLocations = new CaptureLocations();
        promotionMap = new PromotionMap();
        standardStateDetector = new StandardStateDetector(new StandardPawnData(), new StandardCaptureData(), new StandardPromotionData(), new StandardPieceData(), combinedPawnMap, pieceMap,  captureLocations, promotionMap);

    }
    @Test
    void test() {
        BoardInterface boardInterface = new BoardInterface(BoardBuilder.buildBoard("4k3/8/8/3BB3/8/7P/2PPPPP1/RNBQKBNR w KQ - 0 1"));

        this.standardStateDetector.testState(boardInterface);
        
        
        System.out.println(standardStateDetector.getPawnData().getPawnPaths(true));

        Assertions.assertEquals(1, standardStateDetector.getPawnData().getPawnPaths(true).get(new Coordinate(7, 7)).size());
        Assertions.assertEquals(2, standardStateDetector.getPawnData().getPawnPaths(true).get(new Coordinate(6, 7)).size());
        Assertions.assertTrue(promotionMap.getState());

    }

    @Test
    void test2() {
        BoardInterface boardInterface = new BoardInterface(BoardBuilder.buildBoard("4k3/pppp4/8/3BB3/8/7P/2PPPPP1/RNBQKBNR w KQ - 0 1"));

        this.standardStateDetector.testState(boardInterface);
        System.out.println(standardStateDetector.getPawnData().getPawnPaths(true));
        Assertions.assertEquals(1, standardStateDetector.getPawnData().getPawnPaths(true).get(new Coordinate(7, 7)).size());
        Assertions.assertEquals(2, standardStateDetector.getPawnData().getPawnPaths(true).get(new Coordinate(6, 7)).size());
        Assertions.assertTrue(promotionMap.getState());

    }

    @Test
    void testCaptureQuantities() {
        BoardInterface boardInterface = new BoardInterface(BoardBuilder.buildBoard("1nbqkbnr/1pppp1pp/5p2/2Q5/8/8/1PPPPPPP/RNBQKBNR w KQk - 0 1"));

        this.standardStateDetector.testState(boardInterface);
        Assertions.assertEquals(1, standardStateDetector.getPawnData().getPawnPaths(true).get(new Coordinate(0, 7)).size());
        Assertions.assertFalse(standardStateDetector.getPawnData().getPawnPaths(true).values().stream().map(l -> l.stream().map(p -> p.getLast()).toList()).flatMap(p -> p.stream()).toList().contains(new Coordinate(5, 7)));
        Assertions.assertFalse(standardStateDetector.getPawnData().getPawnPaths(true).values().stream().map(l -> l.stream().map(p -> p.getLast()).toList()).flatMap(p -> p.stream()).toList().contains(new Coordinate(2, 7)));
        Assertions.assertFalse(standardStateDetector.getPawnData().getPawnPaths(true).values().stream().map(l -> l.stream().map(p -> p.getLast()).toList()).flatMap(p -> p.stream()).toList().contains(new Coordinate(4, 7)));
        
        System.out.println(this.standardStateDetector.getPawnData().getPawnPaths(true));
//        Assertions.assertTrue(promotionMap.getPawnOrigins(true).containsKey(new Coordinate(1, 7)));
        Assertions.assertTrue(promotionMap.getState());

    }

    @Test
    void testCaptureQuantitiesSomeAccountedFor() {
        BoardInterface boardInterface = new BoardInterface(BoardBuilder.buildBoard("2bqkbnr/1pppp1pp/1pn2p2/2Q5/8/2P5/2PPPPPP/RNBQKB1R w - - 0 1"));


        this.standardStateDetector.testState(boardInterface);
        
        Assertions.assertEquals(1, standardStateDetector.getPawnData().getPawnPaths(true).get(new Coordinate(0, 7)).size());
        Assertions.assertTrue(standardStateDetector.getPawnData().getPawnPaths(true).values().stream().map(l -> l.stream().map(p -> p.getLast()).toList()).flatMap(p -> p.stream()).toList().contains(new Coordinate(0, 7)));
        Assertions.assertFalse(standardStateDetector.getPawnData().getPawnPaths(true).values().stream().map(l -> l.stream().map(p -> p.getLast()).toList()).flatMap(p -> p.stream()).toList().contains(new Coordinate(1, 7)));
        
        
        Assertions.assertTrue(promotionMap.getState());
    }

    @Test
    void testCollision() {
        BoardInterface boardInterface = new BoardInterface(BoardBuilder.buildBoard("rnbqkbnr/1ppppppp/8/8/p2Q4/8/1PPPPPPP/RNBQKBNR"));


        this.standardStateDetector.testState(boardInterface);
//        promotionMap.getPawnOrigins()
//        Assertions.assertEquals(1, promotionMap.getPawnOrigins("white").get(new Coordinate(0, 7)).size());
        Assertions.assertFalse(standardStateDetector.getPawnData().getPawnPaths(true).values().stream().map(l -> l.stream().map(p -> p.getLast()).toList()).flatMap(p -> p.stream()).toList().contains(new Coordinate(0, 7)));

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


        this.standardStateDetector.testState(boardInterface);
//        promotionMap.getPawnOrigins()
//        Assertions.assertEquals(1, promotionMap.getPawnOrigins("white").get(new Coordinate(0, 7)).size());
        
        Assertions.assertFalse(standardStateDetector.getPawnData().getPawnPaths(true).values().stream().map(l -> l.stream().map(p -> p.getLast()).toList()).flatMap(p -> p.stream()).toList().contains(new Coordinate(0, 7)));
        Assertions.assertTrue(standardStateDetector.getPawnData().getPawnPaths(true).values().stream().map(l -> l.stream().map(p -> p.getLast()).toList()).flatMap(p -> p.stream()).toList().contains(new Coordinate(1, 7)));


//        Assertions.assertTrue(promotionMap.getPawnOrigins("white").containsKey(new Coordinate(0, 7)));
//        Assertions.assertFalse(promotionMap.getPawnOrigins("white").containsKey(new Coordinate(1, 7)));
        Assertions.assertTrue(promotionMap.getState());
    }

    @Test
    void testCollisionWithTwoCaptures() {
        BoardInterface boardInterface = new BoardInterface(BoardBuilder.buildBoard("r1bqkbnr/2pppppp/8/8/p2Q4/8/1PPPPPPP/RNBQKBNR w - - 0 1"));


        this.standardStateDetector.testState(boardInterface);
//        promotionMap.getPawnOrigins()
//        Assertions.assertEquals(1, promotionMap.getPawnOrigins("white").get(new Coordinate(0, 7)).size());
        
        System.out.println(standardStateDetector.getPawnData().getPawnPaths(true));

        Assertions.assertTrue(standardStateDetector.getPawnData().getPawnPaths(true).values().stream().map(l -> l.stream().map(p -> p.getLast()).toList()).flatMap(p -> p.stream()).toList().contains(new Coordinate(0, 7)));
        Assertions.assertTrue(standardStateDetector.getPawnData().getPawnPaths(true).values().stream().map(l -> l.stream().map(p -> p.getLast()).toList()).flatMap(p -> p.stream()).toList().contains(new Coordinate(1, 7)));
        Assertions.assertTrue(standardStateDetector.getPawnData().getPawnPaths(true).values().stream().map(l -> l.stream().map(p -> p.getLast()).toList()).flatMap(p -> p.stream()).toList().contains(new Coordinate(2, 7)));



//        Assertions.assertTrue(promotionMap.getPawnOrigins("white").containsKey(new Coordinate(0, 7)));
//        Assertions.assertFalse(promotionMap.getPawnOrigins("white").containsKey(new Coordinate(1, 7)));
        Assertions.assertTrue(promotionMap.getState());
    }

    @Test
    void testCollisionWithTwoCapturesComplexPawnStructure() {
        BoardInterface boardInterface = new BoardInterface(BoardBuilder.buildBoard("r1bqkbnr/2pppppp/5P2/3P4/p1PQP1PP/1P6/8/RNBQKBNR w - - 0 1"));


        this.standardStateDetector.testState(boardInterface);
//        promotionMap.getPawnOrigins()
//        Assertions.assertEquals(1, promotionMap.getPawnOrigins("white").get(new Coordinate(0, 7)).size());
        System.out.println(standardStateDetector.getPawnData().getPawnPaths(true).values().stream().map(l -> l.stream().map(p -> p.getLast()).toList()).flatMap(p -> p.stream()).toList());
        Assertions.assertTrue(standardStateDetector.getPawnData().getPawnPaths(true).values().stream().map(l -> l.stream().map(p -> p.getLast()).toList()).flatMap(p -> p.stream()).toList().contains(new Coordinate(0, 7)));
        Assertions.assertTrue(standardStateDetector.getPawnData().getPawnPaths(true).values().stream().map(l -> l.stream().map(p -> p.getLast()).toList()).flatMap(p -> p.stream()).toList().contains(new Coordinate(1, 7)));
        Assertions.assertTrue(standardStateDetector.getPawnData().getPawnPaths(true).values().stream().map(l -> l.stream().map(p -> p.getLast()).toList()).flatMap(p -> p.stream()).toList().contains(new Coordinate(2, 7)));



//        Assertions.assertTrue(promotionMap.getPawnOrigins("white").containsKey(new Coordinate(0, 7)));
//        Assertions.assertFalse(promotionMap.getPawnOrigins("white").containsKey(new Coordinate(1, 7)));
        Assertions.assertTrue(promotionMap.getState());
    }

    @Test
    void testCollisionBothPawnsTheoretical() {
        BoardInterface boardInterface = new BoardInterface(BoardBuilder.buildBoard("rnbqkbnr/1ppppppp/8/3q4/3Q4/8/1PPPPPPP/RNBQKBNR w KQkq - 0 1"));


        this.standardStateDetector.testState(boardInterface);
//        promotionMap.getPawnOrigins()
//        Assertions.assertEquals(1, promotionMap.getPawnOrigins("white").get(new Coordinate(0, 7)).size());
        
        
        


        for (int x = 0 ; x < 8 ; x++) {
            Assertions.assertFalse(standardStateDetector.getPawnData().getPawnPaths(true).values().stream().map(l -> l.stream().map(p -> p.getLast()).toList()).flatMap(p -> p.stream()).toList().contains(new Coordinate(x, 7)), "" + x);
        }
        for (int x = 0 ; x < 8 ; x++) {
            Assertions.assertFalse(standardStateDetector.getPawnData().getPawnPaths(false).values().stream().map(l -> l.stream().map(p -> p.getLast()).toList()).flatMap(p -> p.stream()).toList().contains(new Coordinate(x, 0)), "" + x);
        }

//        Assertions.assertTrue(promotionMap.getPawnOrigins("white").containsKey(new Coordinate(0, 7)));
//        Assertions.assertFalse(promotionMap.getPawnOrigins("white").containsKey(new Coordinate(1, 7)));
        Assertions.assertFalse(promotionMap.getState());
    }

    @Test
    void testCollisionBothPawnsTheoreticalEnoughCaptures() {
        BoardInterface boardInterface = new BoardInterface(BoardBuilder.buildBoard("rnbqkbnr/1ppppppp/8/3q4/3Q4/8/1PPPPPPP/2BQKBNR w - - 0 1"));
        this.standardStateDetector.testState(boardInterface);

        Assertions.assertTrue(standardStateDetector.getPawnData().getPawnPaths(true).values().stream().map(l -> l.stream().map(p -> p.getLast()).toList()).flatMap(p -> p.stream()).toList().contains(new Coordinate(0, 7)));
        Assertions.assertTrue(promotionMap.getState());
    }

    @Test
    void testCollisionBothPawnsTheoreticalOneCapture() {
        BoardInterface boardInterface = new BoardInterface(BoardBuilder.buildBoard("rnbqkbnr/1ppppppp/8/3q4/3Q4/8/1PPPPPPP/R1BQKBNR w - - 0 1"));


        this.standardStateDetector.testState(boardInterface);
//        promotionMap.getPawnOrigins()
//        Assertions.assertEquals(1, promotionMap.getPawnOrigins("white").get(new Coordinate(0, 7)).size());
        
        
        
        for (int x = 0 ; x < 8 ; x++) {
            Assertions.assertFalse(standardStateDetector.getPawnData().getPawnPaths(true).values().stream().map(l -> l.stream().map(p -> p.getLast()).toList()).flatMap(p -> p.stream()).toList().contains(new Coordinate(x, 7)), "" + x);
        }
        for (int x = 0 ; x < 8 ; x++) {
            Assertions.assertFalse(standardStateDetector.getPawnData().getPawnPaths(false).values().stream().map(l -> l.stream().map(p -> p.getLast()).toList()).flatMap(p -> p.stream()).toList().contains(new Coordinate(x, 0)), "" + x);
        }

//        Assertions.assertTrue(promotionMap.getPawnOrigins("white").containsKey(new Coordinate(0, 7)));
//        Assertions.assertFalse(promotionMap.getPawnOrigins("white").containsKey(new Coordinate(1, 7)));
        Assertions.assertFalse(promotionMap.getState());
    }

    @Test
    void testCollisionBothPawnsTheoreticalEachOneCapture() {
        BoardInterface boardInterface = new BoardInterface(BoardBuilder.buildBoard("r1bqkbnr/1ppppppp/8/3q4/3Q4/8/1PPPPPPP/R1BQKBNR w KQkq - 0 1"));


        this.standardStateDetector.testState(boardInterface);
//        promotionMap.getPawnOrigins()
//        Assertions.assertEquals(1, promotionMap.getPawnOrigins("white").get(new Coordinate(0, 7)).size());
        
        
        
        // One of the below should be true, it doesn't matter which
        for (int x = 0 ; x < 8 ; x++) {
//            Assertions.assertFalse(promotionMap.getPawnOrigins("white").containsKey(new Coordinate(x, 7)), "" + x);
        }
        for (int x = 0 ; x < 8 ; x++) {
            Assertions.assertFalse(standardStateDetector.getPawnData().getPawnPaths(false).values().stream().map(l -> l.stream().map(p -> p.getLast()).toList()).flatMap(p -> p.stream()).toList().contains(new Coordinate(x, 0)), "" + x);
        }

//        Assertions.assertTrue(promotionMap.getPawnOrigins("white").containsKey(new Coordinate(0, 7)));
//        Assertions.assertFalse(promotionMap.getPawnOrigins("white").containsKey(new Coordinate(1, 7)));
        Assertions.assertFalse(promotionMap.getState());
    }

    @Test
    void testCollisionBothPawnsTheoreticalEachOneCaptureThreeOptions() {
        BoardInterface boardInterface = new BoardInterface(BoardBuilder.buildBoard("r1bqkbnr/p1pppppp/8/4q3/4Q3/8/P1PPPPPP/R1BQKBNR w KQkq - 0 1"));


        this.standardStateDetector.testState(boardInterface);
//        promotionMap.getPawnOrigins()
//        Assertions.assertEquals(1, promotionMap.getPawnOrigins("white").get(new Coordinate(0, 7)).size());
        
        
        
        // One of the below should be true, it doesn't matter which
        for (int x = 0 ; x < 8 ; x++) {
//            Assertions.assertFalse(promotionMap.getPawnOrigins("white").containsKey(new Coordinate(x, 7)), "" + x);
        }
        for (int x = 0 ; x < 8 ; x++) {
            Assertions.assertFalse(standardStateDetector.getPawnData().getPawnPaths(false).values().stream().map(l -> l.stream().map(p -> p.getLast()).toList()).flatMap(p -> p.stream()).toList().contains(new Coordinate(x, 0)), "" + x);
        }

//        Assertions.assertTrue(promotionMap.getPawnOrigins("white").containsKey(new Coordinate(0, 7)));
//        Assertions.assertFalse(promotionMap.getPawnOrigins("white").containsKey(new Coordinate(1, 7)));
        Assertions.assertFalse(promotionMap.getState());
    }

    @Test
    void testCollisionBothPawnsTheoreticalEachOneCaptureThreeOptionsPlusExtraCaptures() {
        BoardInterface boardInterface = new BoardInterface(BoardBuilder.buildBoard("r1bqkb1r/p1ppppp1/6p1/4q3/4Q3/6P1/P1PPPPP1/R1BQKB1R w KQkq - 0 1"));


        this.standardStateDetector.testState(boardInterface);
//        promotionMap.getPawnOrigins()
//        Assertions.assertEquals(1, promotionMap.getPawnOrigins("white").get(new Coordinate(0, 7)).size());
        
        
        
        // One of the below should be true, it doesn't matter which
        for (int x = 0 ; x < 8 ; x++) {
//            Assertions.assertFalse(promotionMap.getPawnOrigins("white").containsKey(new Coordinate(x, 7)), "" + x);
        }
        for (int x = 0 ; x < 8 ; x++) {
            System.out.println(standardStateDetector.getPawnData().getPawnPaths(false).values().stream().map(l -> l.stream().map(p -> p.getLast()).toList()).flatMap(p -> p.stream()).toList());
            Assertions.assertFalse(standardStateDetector.getPawnData().getPawnPaths(false).values().stream().map(l -> l.stream().map(p -> p.getLast()).toList()).flatMap(p -> p.stream()).toList().contains(new Coordinate(x, 0)), "" + x);
        }

//        Assertions.assertTrue(promotionMap.getPawnOrigins("white").containsKey(new Coordinate(0, 7)));
//        Assertions.assertFalse(promotionMap.getPawnOrigins("white").containsKey(new Coordinate(1, 7)));
        Assertions.assertFalse(promotionMap.getState());
    }

    @Test
    void testCaptureQuantitiesAmbiguousOrigins() {
        BoardInterface boardInterface = new BoardInterface(BoardBuilder.buildBoard("2b1k3/1pppp3/8/2B2p2/1B6/8/2PPPPPP/RNBQKBNR w - - 0 1"));


        this.standardStateDetector.testState(boardInterface);
//        Assertions.assertEquals(1, promotionMap.getPawnOrigins("white").get(new Coordinate(0, 7)).size());
//        Assertions.assertTrue(promotionMap.getPawnOrigins("white").containsKey(new Coordinate(0, 7)));
//        Assertions.assertFalse(promotionMap.getPawnOrigins("white").containsKey(new Coordinate(1, 7)));
        Assertions.assertTrue(promotionMap.getState());
    }
}

