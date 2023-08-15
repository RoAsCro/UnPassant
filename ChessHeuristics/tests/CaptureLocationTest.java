import Heuristics.BoardInterface;
import Heuristics.Deductions.*;
import Heuristics.Detector.Data.StandardCaptureData;
import Heuristics.Detector.Data.StandardPawnData;
import Heuristics.Detector.Data.StandardPieceData;
import Heuristics.Detector.Data.StandardPromotionData;
import Heuristics.Detector.StandardStateDetector;
import Heuristics.Observations.PawnNumber;
import Heuristics.Observations.PieceNumber;
import StandardChess.BoardBuilder;
import StandardChess.Coordinate;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class CaptureLocationTest {
//    PawnMap pawnMapWhite;
//    PawnMap pawnMapBlack;
    CombinedPawnMap combinedPawnMap;
    PieceMap pieceMap;
    StandardStateDetector standardStateDetector;

    CaptureLocations captureLocations;
    @BeforeEach
    void setup() {
        PawnNumber pawnNumber = new PawnNumber();
        PieceNumber pieceNumber = new PieceNumber();
//        pawnMapWhite = new PawnMap(true);
//        pawnMapBlack = new PawnMap(false);
        combinedPawnMap = new CombinedPawnMap();
        pieceMap = new PieceMap();
        captureLocations = new CaptureLocations();
        this.standardStateDetector = new StandardStateDetector(pawnNumber, pieceNumber, new StandardPawnData(),new StandardCaptureData(), new StandardPromotionData(), new StandardPieceData(),
                combinedPawnMap, pieceMap, captureLocations);

    }

    @Test
    void testUntakeable() {
        BoardInterface boardInterface = new BoardInterface(BoardBuilder.buildBoard("rnbqk2r/pppppppp/8/8/2P5/8/2PPPPPP/RNBQKBNR w - - 0 1"));

         
//        this.pawnMapWhite.deduce(boardInterface);
//        this.pawnMapBlack.deduce(boardInterface);
//        this.combinedPawnMap.deduce(boardInterface);
        
        this.standardStateDetector.testState(boardInterface);
        
        Assertions.assertEquals(1, standardStateDetector.getPawnData().getPawnPaths(true).get(new Coordinate(2, 3)).size());
        Assertions.assertTrue(this.captureLocations.getState());

    }

    @Test
    void testUntakeableBlack() {
        BoardInterface boardInterface = new BoardInterface(BoardBuilder.buildBoard("rnbqkbnr/ppppp2p/8/4p3/8/8/PPPPPPPP/RNBQK2R w - - 0 1"));

        this.standardStateDetector.testState(boardInterface);
        //system.out.println(pawnMapBlack.getPawnOrigins());
        Assertions.assertEquals(1, standardStateDetector.getPawnData().getPawnPaths(false).get(new Coordinate(4, 4)).size());
        Assertions.assertTrue(this.captureLocations.getState());


    }

    @Test
    void testUntakeableWithRook() {
        BoardInterface boardInterface = new BoardInterface(BoardBuilder.buildBoard("rnbqk3/ppppppp1/7p/8/2P5/8/2PPPPPP/RNBQKBNR w - - 0 1"));

        
        

        this.standardStateDetector.testState(boardInterface);
        //system.out.println(pawnMapWhite.getPawnOrigins());
        Assertions.assertEquals(1, standardStateDetector.getPawnData().getPawnPaths(true).get(new Coordinate(2, 3)).size());
        Assertions.assertTrue(this.captureLocations.getState());


    }

    @Test
    void testUntakeableWithRookBlack() {
        BoardInterface boardInterface = new BoardInterface(BoardBuilder.buildBoard("rnbqkbnr/ppppp2p/8/4p3/8/7P/PPPPPPP1/RNBQK3 w - - 0 1"));

        
        

        this.standardStateDetector.testState(boardInterface);
        //system.out.println(pawnMapBlack.getPawnOrigins());
        Assertions.assertEquals(1, standardStateDetector.getPawnData().getPawnPaths(false).get(new Coordinate(4, 4)).size());

    }

    @Test
    void testUntakeableCapturedRook() {
        BoardInterface boardInterface = new BoardInterface(BoardBuilder.buildBoard("2bqkbnr/pPpppppp/np6/8/8/8/PP1PPPPP/RNBQKBNR w - - 0 1"));

        
        

        this.standardStateDetector.testState(boardInterface);
        //system.out.println(pawnMapWhite.getPawnOrigins());
        Assertions.assertEquals(1, standardStateDetector.getPawnData().getPawnPaths(true).get(new Coordinate(1, 6)).size());

    }

    @Test
    void testUntakeableCapturedRookBlack() {
        BoardInterface boardInterface = new BoardInterface(BoardBuilder.buildBoard("rnbqkbnr/pp1ppppp/8/8/8/NP6/PpPPPPPP/2BQKBNR w - - 0 1"));

        
        

        this.standardStateDetector.testState(boardInterface);
        //system.out.println(pawnMapBlack.getPawnOrigins());
        Assertions.assertEquals(1, standardStateDetector.getPawnData().getPawnPaths(false).get(new Coordinate(1, 1)).size());

    }

    @Test
    void testUntakeableRook() {
        BoardInterface boardInterface = new BoardInterface(BoardBuilder.buildBoard("1nbqkb1r/1pppppPp/p6n/8/8/8/PPPP1P1P/RNBQKBNR w - - 0 1"));

        
        

        this.standardStateDetector.testState(boardInterface);
        //system.out.println(pawnMapWhite.getPawnOrigins());
        Assertions.assertEquals(1, standardStateDetector.getPawnData().getPawnPaths(true).get(new Coordinate(6, 6)).size());

    }

    @Test
    void testUntakeableRookBlack() {
        BoardInterface boardInterface = new BoardInterface(BoardBuilder.buildBoard("rnbqkbnr/pppp1p1p/8/8/8/P6N/1PPPPPpP/1NBQKB1R w - - 0 1"));

        
        

        this.standardStateDetector.testState(boardInterface);
        //system.out.println(pawnMapBlack.getPawnOrigins());
        Assertions.assertEquals(1, standardStateDetector.getPawnData().getPawnPaths(false).get(new Coordinate(6, 1)).size());

    }

    @Test
    void testBishopColoursOne() {
        BoardInterface boardInterface = new BoardInterface(BoardBuilder.buildBoard("rn1qkb1r/ppp1pppp/3p4/8/3P4/8/P1PPPPPP/RNBQKBNR w - - 0 1"));

        
        

        this.standardStateDetector.testState(boardInterface);
        //system.out.println(pawnMapWhite.getPawnOrigins());
        //system.out.println(combinedPawnMap.getWhitePaths());
        Assertions.assertFalse(standardStateDetector.getState());
    }

    @Test
    void testBishopColoursOneBlack() {
        BoardInterface boardInterface = new BoardInterface(BoardBuilder.buildBoard("rnbqkbnr/p1pppppp/8/3p4/8/3P4/PPP1PPPP/RN1QKB1R w - - 0 1"));

        
        

        this.standardStateDetector.testState(boardInterface);
        //system.out.println(pawnMapBlack.getPawnOrigins());
        //system.out.println(combinedPawnMap.getBlackPaths());
        Assertions.assertFalse(standardStateDetector.getState());

    }

    @Test
    void testBishopColoursTwo() {
        BoardInterface boardInterface = new BoardInterface(BoardBuilder.buildBoard("rnbqk2r/pppppp1p/6p1/8/4P3/8/PPPPPP1P/RNBQKBNR w - - 0 1"));

        
        

        this.standardStateDetector.testState(boardInterface);
        //system.out.println(pawnMapWhite.getPawnOrigins());
        //system.out.println(combinedPawnMap.getWhitePaths());
        Assertions.assertFalse(standardStateDetector.getState());

    }

    @Test
    void testBishopColoursThree() {
        BoardInterface boardInterface = new BoardInterface(BoardBuilder.buildBoard("rnbqk2r/pppppp1p/2P3p1/PP6/8/5P2/3PPP1P/RNBQKBNR w - - 0 1"));

        
        

        this.standardStateDetector.testState(boardInterface);
        //system.out.println(pawnMapWhite.getPawnOrigins());
        //system.out.println(combinedPawnMap.getWhitePaths());
        Assertions.assertEquals(1, standardStateDetector.getPawnData().getPawnPaths(true).get(new Coordinate(5, 2)).size());

    }

    @Test
    void testRookOnePawnBetweenTwo() {
        BoardInterface boardInterface = new BoardInterface(BoardBuilder.buildBoard("2bqkb2/p5Pp/1pppppp1/2n2n2/8/5P2/PPPPPP2/RNBQKBNR w - - 0 1"));

        
        

        this.standardStateDetector.testState(boardInterface);
        //system.out.println(pawnMapWhite.getPawnOrigins());
        //system.out.println(combinedPawnMap.getWhitePaths());
        Assertions.assertFalse(standardStateDetector.getState());
//        Assertions.assertFalse(pawnMapWhite.getState());


    }

    @Test
    void testRookOnePawnBetweenTwoTwo() {
        BoardInterface boardInterface = new BoardInterface(BoardBuilder.buildBoard("2bqk3/p5Pp/1pppppp1/2n2n2/8/5P2/PPPPP3/RNBQKBNR w - - 0 1"));

        
        

        this.standardStateDetector.testState(boardInterface);
        //system.out.println(pawnMapWhite.getPawnOrigins());
        //system.out.println(combinedPawnMap.getWhitePaths());
        Assertions.assertEquals(1, standardStateDetector.getPawnData().getPawnPaths(true).get(new Coordinate(5, 2)).size());
//        Assertions.assertTrue(pawnMapWhite.getState());


    }

    @Test
    void testRookBug() {
        BoardInterface boardInterface = new BoardInterface(BoardBuilder.buildBoard("3qk1nr/pp1ppppp/2p5/8/5P2/8/2P1PP2/RNBQKBNR w - - 0 1"));

        
        

        this.standardStateDetector.testState(boardInterface);
        //system.out.println(pawnMapWhite.getPawnOrigins());
        //system.out.println(combinedPawnMap.getWhitePaths());
        Assertions.assertEquals(1, standardStateDetector.getPawnData().getPawnPaths(true).get(new Coordinate(5, 3)).size());
//        Assertions.assertTrue(pawnMapWhite.getState());


    }

    @Test
    void testBishopColoursTwoBlack() {
        BoardInterface boardInterface = new BoardInterface(BoardBuilder.buildBoard("rnbqkbnr/pppppp1p/8/4p3/8/6P1/PPPPPP1P/RNBQK2R w - - 0 1"));

        
        

        this.standardStateDetector.testState(boardInterface);
        //system.out.println(pawnMapBlack.getPawnOrigins());
        //system.out.println(combinedPawnMap.getBlackPaths());
        Assertions.assertFalse(standardStateDetector.getState());

    }

    @Test
    void test() {
        BoardInterface boardInterface = new BoardInterface(BoardBuilder.buildBoard("3qkbnr/pp1ppppp/2p5/5P1P/3QP3/2P1Q1P1/8/RNBQKBNR w - - 0 1"));

        
//        this.pieceMap.deduce(boardInterface);

        this.standardStateDetector.testState(boardInterface);

        //system.out.println(this.pawnMapWhite.getPawnOrigins());
        //system.out.println(this.pawnMapWhite.getState());
    }
}
