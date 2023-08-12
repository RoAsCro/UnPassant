import Heuristics.BoardInterface;
import Heuristics.Deductions.*;
import Heuristics.Observations.PawnNumber;
import Heuristics.Observations.PieceNumber;
import Heuristics.UnCastle;
import StandardChess.BoardBuilder;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class ImpossibleStates {

    PawnNumber pawnNumber;
    PieceNumber pieceNumber;
    TestImpossibleStateDetector detector;
    PieceMap pm;
    UnCastle uc;

    @BeforeEach
    public void setup() {
        this.pawnNumber = new PawnNumber();
        this.pieceNumber = new PieceNumber();
        PawnMapWhite pmw = new PawnMapWhite(this.pawnNumber, this.pieceNumber);
        PawnMapBlack pmb = new PawnMapBlack(this.pawnNumber, this.pieceNumber);
        CombinedPawnMap cpm = new CombinedPawnMap(pmw, pmb);
        this.pm = new PieceMap(cpm);
        CaptureLocations cl = new CaptureLocations(pmw, pmb, pm, cpm);
        PromotionMap prm = new PromotionMap(pm, cpm, pmw, pmb, cl, pieceNumber, pawnNumber);
        PromotedPawnSquares pps = new PromotedPawnSquares(pieceNumber, pm, prm, cl, cpm);
        this.uc = new UnCastle(pmw, pmb, pm, prm, pps);
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
    void defaultBoard() {
        Assertions.assertTrue(test("rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w - - 0 1"));

    }

    @Test
    void tooManyPawns() {
        Assertions.assertFalse(test("rnbqkbnr/pppppppp/8/8/8/6P1/PPPPPPPP/RNBQKBNR w - - 0 1"));
    }

    @Test
    void tooManyPawnsFewerPieces() {
        Assertions.assertFalse(test("rnbqkbnr/pppppppp/8/6P1/8/7P/PPPPPP1P/RNBQKB2 w - - 0 1"));
    }

    @Test
    void tooManyPawnsBlack() {
        Assertions.assertFalse(test("rnbqkb1r/pppppppp/6p1/8/8/8/PPPPPPPP/RNBQKBNR w - - 0 1"));
    }

    @Test
    void tooManyPieces() {
        Assertions.assertFalse(test("rnbqk3/ppppp3/8/6N1/6N1/7N/PPPPPP2/RNBQKBNR w - - 0 1"));
    }

    @Test
    void tooManyPiecesBlack() {
        Assertions.assertFalse(test("rnbqkbnr/p2ppppp/8/2nn4/1n6/8/P2PPPPP/R2QKBNR w - - 0 1"));
    }

    @Test
    void impossiblePawnPosition() {
        Assertions.assertFalse(test("r1bqkb2/ppppppp1/8/8/8/7P/PPPP1PPP/RNBQKBNR w - - 0 1"));
    }

    @Test
    void possiblePawnPosition() {
        Assertions.assertTrue(test("r1bqkb2/ppppppp1/8/8/8/5P2/PPPP1PPP/RNBQKBNR w - - 0 1"));
    }

    @Test
    void impossiblePawnPositionTwo() {
        Assertions.assertFalse(test("3qkb2/3pppp1/8/8/8/P7/PP1PPPPP/RNBQKBNR w - - 0 1"));
    }

    @Test
    void impossiblePawnPositionThree() {
        Assertions.assertFalse(test("3qkb2/3pppp1/8/8/3P4/2P5/1P1PPPPP/RNBQKBNR w - - 0 1"));
    }

    @Test
    void possiblePawnPositionTwo() {
        Assertions.assertTrue(test("3qkb2/3pppp1/8/8/3P4/2P5/P2PPPPP/RNBQKBNR w - - 0 1"));
    }

    @Test
    void impossiblePawnPositionCaptures() {
        Assertions.assertFalse(test("rnbqkbnr/pppppppp/8/8/8/P7/P1PPPPPP/RNBQKBNR w - - 0 1"));
    }

    @Test
    void impossiblePawnPositionCapturesTwo() {
        Assertions.assertFalse(test("r1bqkb1r/pppppppp/8/2P5/2P5/3P4/PP2P1PP/RNBQKBNR w - - 0 1"));
    }

    @Test
    void impossibleMap() {
        Assertions.assertFalse(test("rnbqkbnr/1ppppppp/8/P7/p7/8/1PPPPPPP/RNBQKBNR w - - 0 1"));
    }

    @Test
    void impossibleMapTwo() {
        Assertions.assertFalse(test("rnbqkbnr/2pppppp/8/P7/p7/8/1PPPPPPP/RNBQKBNR w - - 0 1"));
    }

    @Test
    void possibleMap() {
        Assertions.assertTrue(test("r1bqkb1r/1ppppppp/8/P7/p7/8/1PPPPPPP/RNBQKBNR w - - 0 1"));
    }

    @Test
    void impossibleMapThree() {
        Assertions.assertFalse(test("r1b1kb1r/pp1ppppp/8/4P3/P4p2/8/1PPPPPP1/2BQKB1R w - - 0 1"));
    }

    @Test
    void possibleMapTwo() {
        Assertions.assertTrue(test("r1b1kb1r/pp1pp1pp/8/4P3/P4p2/8/1PPPPPP1/2BQKB1R w - - 0 1"));
    }

    @Test
    void impossibleMapFour() {
        Assertions.assertFalse(test("4k2r/2Pppp1p/1p1p2p1/p7/8/1P6/PP1PPPP1/RNBQKB1R w - - 0 1"));
    }

    @Test
    void impossibleMapFive() {
        Assertions.assertFalse(test("rnbqkbnr/ppp1pppp/8/3P4/3p4/8/PPP1PP1P/RNBQKBNR w - - 0 1"));
    }

//    @Test
//    void impossibleMapSix() {
//        Assertions.assertFalse(test("rnbqkbnr/pp2pppp/8/3P4/3p4/8/PPP1PP1P/RNBQKBNR w KQkq - 0 1"));
//    }

    @Test
    void impossibleMapSeven() {
        Assertions.assertFalse(test("rnbqkbnr/2p1pppp/P3p3/p7/8/8/1PPPPPPP/RNBQKB1R w - - 0 1"));
    }

    @Test
    void possibleMapThree() {
        Assertions.assertTrue(test("rnbqkbnr/2p1pppp/4p3/p7/8/8/PPPPPPPP/RNBQKB1R w - - 0 1"));
    }

    @Test
    void possibleMapFour() {
        Assertions.assertTrue(test("rnbqkbnr/2p1pppp/P3p3/p7/8/8/1PPPPPPP/R1BQKB1R w - - 0 1"));
    }

    @Test
    void impossibleMapEight() {
        Assertions.assertFalse(test("rnbqkbnr/2pppppp/P7/p7/4p3/6P1/1PPPPP1P/2BQK2R w - - 0 1"));
    }

    @Test
    void impossiblePieceMapBishop() {
        Assertions.assertFalse(test("rnbqkbnr/pppppppp/8/2B5/8/2P1P3/PP1PBPPP/RN1QK1NR w - - 0 1"));
    }

    @Test
    void impossiblePieceMapBishopTwo() {
        Assertions.assertFalse(test("rnbqkbnr/pppppppp/8/5B2/8/8/PPPPPPPP/RNBQK1NR w - - 0 1"));
    }

    @Test
    void impossiblePieceMapBishopThree() {
        Assertions.assertFalse(test("rn1qk1nr/1p1pp1p1/2p2b1p/2b5/p4p2/8/PPPPPPPP/RNBQKBNR w - - 0 1"));
    }

    @Test
    void impossiblePieceMapRook() {
        Assertions.assertFalse(test("rnbqkbnr/pppppppp/8/8/R6R/1P4PP/P1PPPP2/1NBQKBN1 w - - 0 1"));
    }

    @Test
    void impossiblePieceMapRookTwo() {
        Assertions.assertFalse(test("rnbqkbnr/pppppppp/8/8/2P2PR1/8/PP1PP1PP/1NBQKBN1 w - - 0 1"));
    }

    @Test
    void impossiblePieceMapRookThree() {
        Assertions.assertFalse(test("rnbqkbnr/pppppppp/8/8/P1N5/5N2/1PPPPPPP/RRBQKB2 w - - 0 1"));
    }

    @Test
    void impossiblePieceMapRoyalty() {
        Assertions.assertFalse(test("rnbqkbnr/pppppppp/8/8/4QK2/8/PPPPPPPP/RNB2BNR w - - 0 1"));
    }

    @Test
    void impossiblePieceMapRoyaltyTwo() {
        Assertions.assertFalse(test("rnbqkbnr/pppppppp/8/8/3QK3/7P/PPPPPPP1/RNB2BNR w - - 0 1"));
    }

    @Test
    void promotionOne() {
        Assertions.assertFalse(test("rnbqkbnr/pppppppp/8/8/3P4/2P2PB1/PP2P1P1/RNBQKBNR w - - 0 1"));
    }

    @Test
    void promotionTwo() {
        Assertions.assertFalse(test("r1bqkb2/ppppppp1/7p/8/4R3/8/1PPPPP1P/RNBQKBNR w - - 0 1"));
    }

    @Test
    void promotionCaptureBishop() {
        Assertions.assertFalse(test("rnbqk1nr/pppppppp/8/8/8/2P5/P1PPPPPP/RNBQKBNR w - - 0 1"));
    }

    @Test
    void promotionCaptureRook() {
//        Assertions.assertFalse(test("rnbqkbnr/ppppp1pp/4p3/8/8/PPPP4/4PPPP/1NBQKBNR w Kkq - 0 1"));
        Assertions.assertFalse(test("rnbqkbnr/ppppp1pp/4p3/8/8/PPPPP3/5PPP/1NBQKBNR w - - 0 1"));
    }

    @Test
    void promotionCaptureRookTwo() {
        Assertions.assertFalse(test("rnbqkbnr/ppppp1pp/4p3/8/8/PPPPP3/4RPPP/1NBQKBN1 w - - 0 1"));
    }

    @Test
    void promotionCaptureRookThree() {
        Assertions.assertFalse(test("rnbqkbnr/ppppp1pp/4p3/8/8/PPPPPPPP/4R3/1NBQKBN1 w - - 0 1"));
    }

    @Test
    void promotionCaptureRookPossible() {
        Assertions.assertTrue(test("2bqkbnr/pPpppppp/np6/8/8/8/PP1PPPPP/RNBQKBNR w - - 0 1"));
    }

    @Test
    void badColourBishop() {
        Assertions.assertFalse(test("rn1qkbnr/p1pppppp/1p6/8/8/2P5/P1PPPPPP/RNBQKBNR w - - 0 1"));
    }

    @Test
    void badColourBishopTwo() {
        Assertions.assertFalse(test("rnbqkbnr/ppp1pppp/4p3/8/8/3P4/PPP1PPPP/RN1QKBNR w - - 0 1"));
    }

    @Test
    void badPromotion() {
        Assertions.assertFalse(test("rnbqkbnr/p1pppppp/8/1pQ5/8/8/1PPPPPPP/RNBQKBNR w - - 0 1"));
    }

    @Test
    void badPromotionLocation() {
        Assertions.assertFalse(test("rnbqkbn1/ppppppp1/6p1/2PB4/P2P4/5P2/1P2P1P1/R1BQKBNR w - - 0 1"));
    }



    @Test
    void test() {
        Assertions.assertTrue(test("rnbqkbn1/pppppp2/8/2PR4/P2P4/5PB1/1P2P3/RNBQKBNR w - - 0 1"));
    }

    @Test
    void test2() {
        Assertions.assertTrue(test("rnbqkbnr/1p2p3/2p2pb1/p2p4/3r4/8/PPPPPP2/RNBQKBN1 w - - 0 1"));
    }

    @Test
    void test3() {
        Assertions.assertTrue(test("rnbqkbnr/2pppppp/5P2/2P1P1P1/p2Q3P/1P1P4/8/RNBQKBNR w - - 0 1"));
    }

    @Test
    void test4() {
        Assertions.assertTrue(test("r1bqkbnr/2pppppp/8/8/p2Q4/8/1PPPPPPP/RNBQKBNR w - - 0 1"));
    }

    @Test
    void test5() {
        Assertions.assertTrue(test("r1bqkbnr/2pppppp/5P2/3P4/p1PQP1PP/1P6/8/RNBQKBNR w - - 0 1"));
    }

    @Test
    void test6() {
        Assertions.assertTrue(test("rnBqkb1r/pp1ppp2/8/5B1p/8/8/PP1PPP1P/RNBQKBNR w - - 0 1"));
    }

    @Test
    void test7() {
        Assertions.assertTrue(test("3qkbnr/pp1ppppp/2p5/5P1P/3QP3/2P1Q1P1/8/RNBQKBNR w - - 0 1"));
    }

    @Test
    void test7Simplified() {
        Assertions.assertTrue(test("3qkbnr/p2ppppp/bpp5/8/1n1Q4/8/PPP1PPPP/RNBQKBNR w - - 0 1"));
    }
    @Test
    void test7SimplifiedTwo() {
        Assertions.assertTrue(test("2bqkbnr/pp1ppppp/2p5/8/3Q4/8/PPP1PPPP/RNBQKBNR w - - 0 1"));
    }

    @Test
    void test7SimplifiedThree() {
        Assertions.assertTrue(test("2bqkbnr/p1pppppp/1p6/8/1n1Q4/8/1PPPPPPP/RNBQKBNR w - - 0 1"));
    }

    @Test
    void multiplePSquaresOneValidOrigin() {
        Assertions.assertFalse(test("3qkbnr/p1pppppp/p7/3Q4/3Q4/5br1/P1PPPPP1/RNBQKB1R w - - 0 1"));
    }

    @Test
    void promotionOnSameSquare() {
        Assertions.assertTrue(test("r1bqkbnr/p1pppppp/p7/4Q3/4Q3/8/P2PPPPP/RNBQKB1R w - - 0 1"));
    }
    @Test
    void noEscapeFromPromotionSquare() {
        Assertions.assertFalse(test("r1bqkbnr/p1pppppp/p7/4B3/8/8/P2PPPPP/RNBQKB1R w - - 0 1"));
    }

    @Test
    void promotionSquareUnreachable() {
        Assertions.assertFalse(test("r1bqkbnr/pp1ppppp/2p5/4B3/8/8/P1PPPPPP/RNBQKB1R w - - 0 1"));
    }

    @Test
    void singleCaptureThatMustHaveTakenPlace() {
        Assertions.assertFalse(test("rnb1kb1r/ppp2ppp/2p5/8/6Bq/4P3/PPP1pPPP/RNBQKB1R w - - 0 1"));
    }

    @Test
    void singleCaptureThatMustHaveTakenPlaceTwo() {
        Assertions.assertFalse(test("rnb1kb1r/ppp2ppp/P1p4P/7B/6Bq/8/1PP1pPP1/RNBQKB1R w - - 0 1"));
    }

    @Test
    void singleCaptureThatMustHaveTakenPlaceTwoValid() {
        Assertions.assertTrue(test("rnb1kb1r/ppp2ppp/P1p4P/7B/6Bq/8/1PP2PP1/RNBQKB1R w - - 0 1"));
    }


    @Test
    void oneValidOrigin() {
        Assertions.assertFalse(test("rnbqkbnr/ppp1pppp/2p5/6Q1/6Q1/8/1PP1PPPP/RNBQKB1R w - - 0 1"));
    }

    @Test
    void oneValidDifferentPieces() {
        Assertions.assertFalse(test("rnbqkbnr/ppp1pppp/2p5/6R1/6Q1/8/1PP1PPPP/RNBQKB1R w - - 0 1"));
    }

    @Test
    void multipleValidMultiplePiecesBadCombination() {
        Assertions.assertFalse(test("2bqkbn1/1ppppp1r/1p3p1p/2r2B2/5Q2/6B1/2PPPP1P/R1BQKB1R w - - 0 1"));
    }

    @Test
    void multipleValidMultiplePiecesBadCombinationTwo() {
        Assertions.assertFalse(test("1nb1kb2/1pp1ppp1/1pp1B1p1/Qrq1B1rQ/8/8/1PP2PP1/R1BQKB2 w - - 0 1"));
    }

    @Test
    void multipleValidMultiplePiecesBadCombinationTwoValid() {
        Assertions.assertTrue(test("1nb1kb2/1pp2pp1/1pp1Bpp1/Qrq1B1rQ/8/8/1PP2PP1/2BQKB2 w - - 0 1"));
    }

    @Test
    void multipleValidMultiplePiecesBadCombinationValid() {
        Assertions.assertTrue(test("2bqkb2/1ppppp1r/1p3p1p/2r2B2/5Q2/6B1/2PPPP2/R1BQKB1R w - - 0 1"));
    }

    @Test
    void multipleValidMultiplePiecesCombinationValid() {
        Assertions.assertFalse(test("2bqkb2/1ppppp1r/1p3p1p/2r2B2/5Q1Q/6B1/2PPPP2/R1BQKB1R w - - 0 1"));
    }

    @Test
    void multipleValidMultiplePiecesCombinationValidTwo() {
        Assertions.assertTrue(test("r1b1k3/2p1pp1p/1pp1pp2/3QqnQ1/2BB4/8/2P1PP1P/2BQKB2 w - - 0 1"));
    }

    @Test
    void twoValidOrigins() {
        Assertions.assertTrue(test("rnbqk2r/ppp1pppp/2p5/6Q1/6Q1/8/1PP2PPP/RNBQKB1R w - - 0 1"));
    }

    @Test
    void impossiblePromotionNumbers() {
        Assertions.assertFalse(test("2bqkbnr/1ppppppp/1pr5/2n5/1QQ5/8/2PPPPPP/RNBQKB1R w - - 0 1"));
    }

    @Test
    void impossiblePromotion() {
        Assertions.assertFalse(test("3qkbnQ/pp1ppppp/2p5/5P1P/3QP3/2P1Q1P1/8/RNBQKBNR w - - 0 1"));
    }

    @Test
    void wrongPieceTypes() {
        Assertions.assertFalse(test("rnbqkbnr/pppppppp/8/8/5P2/6P1/PPPPP2P/RNBQKBBR w - - 0 1"));
    }

    @Test
    void wrongPieceTypesKnight() {
        Assertions.assertFalse(test("rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNNQKBNR w - - 0 1"));
    }

    @Test
    void noPathOut() {
        Assertions.assertFalse(test("rnbqkb1r/ppppppp1/6p1/5B2/8/8/PPPPPPP1/RNBQKBNR w - - 0 1"));
    }

    @Test
    void cagedRookCapture() {
        Assertions.assertTrue(test("rnbqkbn1/pppppp2/6pp/8/4Q3/8/PPPPP1PP/RNBQKBNR w - - 0 1"));
    }
    @Test
    void cagedRookCaptureMirrorVertical() {
        Assertions.assertTrue(test("1nbqkbnr/2pppppp/pp6/8/4Q3/8/PP1PPPPP/RNBQKBNR w - - 0 1"));
    }

    @Test
    void cagedRookCaptureMirrorHorizontal() {
        Assertions.assertTrue(test("rnbqkbnr/ppppp1pp/4q3/8/8/6PP/PPPPPP2/RNBQKBN1 w - - 0 1"));
    }

    @Test
    void badPieceNumberMissing() {
        Assertions.assertFalse(test("rnbqkb1r/p1pppp1p/2p2p2/3Q4/3Q4/8/PP1PP1PP/R1BQKB1R w - - 0 1"));
    }
    @Test
    void promotionMapCombinedPawnMapWithCagedPieces() {
        // Later Checks Don't Account For Earlier Discovered Missing pawns
        Assertions.assertFalse(test("1nbqk2r/pppppp1p/5n2/5q2/5Q2/5N2/PPPPPP1P/RNBQK2R w - - 0 1"));
    }

    @Test
    void promotionMapCombinedPawnMapWithCagedPiecesCounterexample() {
        Assertions.assertFalse(test("rnbqk2r/pppppp1p/5n2/5q2/5Q2/5N2/PPPPPP1P/RNBQK2R w - - 0 1"));
    }

    @Test
    void promotionChangingBoardState() {
        //  Not supported
//        Assertions.assertFalse(test("2bqkb1r/1pp1ppp1/8/p2P1q2/3p1Q2/5P2/PPP2P2/RNBQKBNR w - - 0 1"));
    }

    @Test
    void liveTestBugInfiniteLoop() {
//        Assertions.assertTrue(test("B1K5/k2pQ3/1q6/2Q5/3PP3/B6P/8/7B w - - 0 1"));
    }

    @Test
    void pawnsOnepromotedOneDidNot() {
        Assertions.assertTrue(test("rnbqkbnr/2pppppp/8/P7/8/3R2P1/1PPPP1PP/2BQKBNR w Kkq - 0 1"));
    }

    @Test
    void r() {

        System.out.println(test("r3k3/8/8/8/8/8/5PP1/6bK b q - 0 1"));
        System.out.println(BoardBuilder.buildBoard("r3k3/8/8/8/8/8/5PP1/6bK b q - 0 1").canCastle("queen", "black"));
        System.out.println(BoardBuilder.buildBoard("r3k3/8/8/8/8/8/5PP1/6bK b q - 0 1").getReader().toFEN());

        System.out.println(this.pm.getCaged());


    } @Test
    void r2() {

        System.out.println(test("r3kqR1/1p1pppp1/5B1P/6PN/4P2p/3Q1P2/2PK2pP/8 b q - 0 1"));
//        System.out.println(test("r3kqR1/1p1pppp1/5B1P/6PN/4P2p/3Q1P2/2PK2pP/8 b q - 0 1"));

        System.out.println(this.pm.getPromotionNumbers());


    }


}
