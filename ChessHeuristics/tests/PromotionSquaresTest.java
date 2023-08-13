import Heuristics.BoardInterface;
import Heuristics.Deductions.*;
import Heuristics.Observations.PawnNumber;
import Heuristics.Observations.PieceNumber;
import StandardChess.BoardBuilder;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class PromotionSquaresTest {

    PawnNumber pawnNumber;
    PieceNumber pieceNumber;
    TestImpossibleStateDetector detector;
    PromotedPawnSquares pps;

    @BeforeEach
    public void setup() {
        this.pawnNumber = new PawnNumber();
        this.pieceNumber = new PieceNumber();
        PawnMapWhite pmw = new PawnMapWhite();
        PawnMapBlack pmb = new PawnMapBlack();
        CombinedPawnMap cpm = new CombinedPawnMap();
        PieceMap pm = new PieceMap();
        CaptureLocations cl = new CaptureLocations();
        PromotionMap prm = new PromotionMap(pm, cpm, pmw, pmb, cl, pieceNumber, pawnNumber);
        this.pps = new PromotedPawnSquares(pieceNumber, pm, prm, cl, cpm);
        this.detector = new TestImpossibleStateDetector(pawnNumber, pieceNumber,
                pmw,
                pmb,
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
    void testUnReachableWrongDirection() {
        //This scenario is not covered by the current implementation
//        Assertions.assertFalse(test("rnbqkbnr/pppppp1p/8/4P3/8/8/PPPPP1PP/RNBQKBNR w - - 0 1"));
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
        System.out.println(this.pps.getPromotionPaths(true));
    }

    @Test
    void promotionExtraMissingPawn() {
        Assertions.assertTrue(test("rnbqkbnr/ppp1ppp1/2p3p1/8/8/8/1PP1PPPP/R1BQKBNR w - - 0 1"));
        System.out.println(this.pps.getPromotionPaths(true));
    }

    @Test
    void promotionNoPath() {
        Assertions.assertFalse(test("rnbqkbnr/pppp1ppp/5p2/8/8/8/PPPPPP1P/RNBQKBNR w - - 0 1"));
        System.out.println(this.pps.getPromotionPaths(true));
    }
    @Test
    void promotionNoPathDueToCage() {
        Assertions.assertFalse(test("rn2kbnr/pppp1ppp/5p2/8/8/8/PP1PPPPP/RNBQKBNR w - - 0 1"), (this.pps.getPromotionPaths(true).toString()));
        System.out.println(this.pps.getPromotionPaths(true));
    }

    @Test
    void promotionPathDueToCaged() {
        Assertions.assertTrue(test("1nbqkbnr/p1pp1ppp/1p3p2/8/8/8/PP1PPPPP/RNBQKBNR w - - 0 1"), (this.pps.getPromotionPaths(true).toString()));
        System.out.println(this.pps.getPromotionPaths(true));
    }

    @Test
    void promotionNoPathDueToCagedOnOtherSide() {
        Assertions.assertFalse(test("rnbqkbn1/p1pppppp/p7/8/8/8/PP1PPPPP/RNBQKBNR w - - 0 1"), (this.pps.getPromotionPaths(true).toString()));
        System.out.println(this.pps.getPromotionPaths(true));
    }
    @Test
    void promotionWithPath() {
        Assertions.assertTrue(test("rnb1kb1r/pppp1ppp/5p2/8/8/8/PP1PPPPP/RNBQKBNR w - - 0 1"));
        System.out.println(this.pps.getPromotionPaths(true));
    }





}
