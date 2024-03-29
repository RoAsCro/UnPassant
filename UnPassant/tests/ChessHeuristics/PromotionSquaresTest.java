package ChessHeuristics;

import ChessHeuristics.Heuristics.BoardInterface;
import ChessHeuristics.Heuristics.Deductions.*;
import ChessHeuristics.Heuristics.Detector.Data.StandardCaptureData;
import ChessHeuristics.Heuristics.Detector.Data.StandardPawnData;
import ChessHeuristics.Heuristics.Detector.Data.StandardPieceData;
import ChessHeuristics.Heuristics.Detector.Data.StandardPromotionData;
import ChessHeuristics.Heuristics.Detector.StandardStateDetector;
import ReverseChess.StandardChess.BoardBuilder;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class PromotionSquaresTest {

    StandardStateDetector detector;
    PromotedPawnSquares pps;

    @BeforeEach
    public void setup() {
        CombinedPawnMap cpm = new CombinedPawnMap();
        PieceMap pm = new PieceMap();
        CaptureLocations cl = new CaptureLocations();
        PromotionMap prm = new PromotionMap();
        this.pps = new PromotedPawnSquares();
        this.detector = new StandardStateDetector(
                new StandardPawnData(), new StandardCaptureData(), new StandardPromotionData(), new StandardPieceData(),
                cpm,
                pm,
                cl,
                prm,
                pps);
    }
    public boolean test(String fen) {
        BoardInterface board = new BoardInterface(BoardBuilder.buildBoard(fen));
        return this.detector.testState(board);
    }

    @Test
    void testDefaultBoard(){
        Assertions.assertTrue(test("rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w - - 0 1"));

    }
    @Test
    void testUnreachablePawn() {
        Assertions.assertFalse(test("rnbqk1nr/ppp1pp1p/2p2p2/8/8/8/1PPPPPPP/RNBQKB1R w - - 0 1"));
    }

    @Test
    void testReachablePawnWithEnoughCaptures() {
        Assertions.assertTrue(test("rnbqk1nr/pp1ppp1p/1p3p2/8/8/8/1PPPPPPP/RNBQKB1R w - - 0 1"));
    }

    @Test
    void testReachablePawn() {
        Assertions.assertTrue(test("rnbqk1nr/p1pppp1p/p4p2/8/8/8/1PPPPPPP/RNBQKB1R w - - 0 1"));
    }


    @Test
    void testUnReachableWrongDirectionTwo() {
        Assertions.assertFalse(test("rnbqkbnr/pppppp1p/8/8/8/4P3/PPPPP1PP/RNBQKBNR w - - 0 1"));
    }
    @Test
    void testUnReachableRightDirection() {
        Assertions.assertTrue(test("rnbqkbnr/pppppp1p/5p2/8/8/8/PPPPP1PP/RNBQKBNR w - - 0 1"));
    }

    @Test
    void extraCapture() {
        Assertions.assertTrue(test("rnbqkbnr/pppppp1p/8/4p3/8/8/PPPP1PPP/RNBQKB1R w - - 0 1"));
    }

    @Test
    void twoCaptures() {
        Assertions.assertTrue(test("rnbqkbnr/1ppppp1p/1p6/4p3/8/8/PPPP2PP/RNBQKB1R w - - 0 1"));
    }

    @Test
    void promotion() {
        Assertions.assertTrue(test("rnbqkbnr/1pp1pp2/1pp3p1/4p3/8/8/PPP3PP/R1BQKB1R w - - 0 1"));
    }

    @Test
    void promotionExtraMissingPawn() {
        Assertions.assertTrue(test("rnbqkbnr/ppp1ppp1/2p3p1/8/8/8/1PP1PPPP/R1BQKBNR w - - 0 1"));
        
    }

    @Test
    void promotionNoPath() {
        Assertions.assertFalse(test("rnbqkbnr/pppp1ppp/5p2/8/8/8/PPPPPP1P/RNBQKBNR w - - 0 1"));
        
    }
    @Test
    void promotionNoPathDueToCage() {
        Assertions.assertFalse(test("rn2kbnr/pppp1ppp/5p2/8/8/8/PP1PPPPP/RNBQKBNR w - - 0 1"));
        
    }

    @Test
    void promotionPathDueToCaged() {
        Assertions.assertTrue(test("1nbqkbnr/p1pp1ppp/1p3p2/8/8/8/PP1PPPPP/RNBQKBNR w - - 0 1"));
    }

    @Test
    void promotionNoPathDueToCagedOnOtherSide() {
        Assertions.assertFalse(test("rnbqkbn1/p1pppppp/p7/8/8/8/PP1PPPPP/RNBQKBNR w - - 0 1"));
        
    }
    @Test
    void promotionWithPath() {
        Assertions.assertTrue(test("rnb1kb1r/pppp1ppp/5p2/8/8/8/PP1PPPPP/RNBQKBNR w - - 0 1"));
        
    }





}
