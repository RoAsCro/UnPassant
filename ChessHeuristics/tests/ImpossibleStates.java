import Heuristics.BoardInterface;
import Heuristics.Deductions.*;
import Heuristics.Observations.PawnNumber;
import Heuristics.Observations.PieceNumber;
import StandardChess.BoardBuilder;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class ImpossibleStates {

    PawnNumber pawnNumber;
    PieceNumber pieceNumber;
    ImpossibleStateDetector detector;

    @BeforeEach
    public void setup() {
        this.pawnNumber = new PawnNumber();
        this.pieceNumber = new PieceNumber();
        PawnMapWhite pmw = new PawnMapWhite(this.pawnNumber, this.pieceNumber);
        PawnMapBlack pmb = new PawnMapBlack(this.pawnNumber, this.pieceNumber);
        CombinedPawnMap cpm = new CombinedPawnMap(pmw, pmb);
        PieceMap pm = new PieceMap(cpm);
        this.detector = new ImpossibleStateDetector(pawnNumber, pieceNumber,
                pmw,
                pmb,
                cpm,
                pm);
    }

    public boolean test(String fen) {
        BoardInterface board = new BoardInterface(BoardBuilder.buildBoard(fen));
        return this.detector.testState(board);
    }

    @Test
    void defaultBoard() {
        Assertions.assertTrue(test("rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1"));

    }

    @Test
    void tooManyPawns() {
        Assertions.assertFalse(test("rnbqkbnr/pppppppp/8/8/8/6P1/PPPPPPPP/RNBQKBNR w KQkq - 0 1"));
    }

    @Test
    void tooManyPawnsFewerPieces() {
        Assertions.assertFalse(test("rnbqkbnr/pppppppp/8/6P1/8/7P/PPPPPP1P/RNBQKB2 w Qkq - 0 1"));
    }

    @Test
    void tooManyPawnsBlack() {
        Assertions.assertFalse(test("rnbqkb1r/pppppppp/6p1/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1"));
    }

    @Test
    void tooManyPieces() {
        Assertions.assertFalse(test("rnbqk3/ppppp3/8/6N1/6N1/7N/PPPPPP2/RNBQKBNR w KQq - 0 1"));
    }

    @Test
    void tooManyPiecesBlack() {
        Assertions.assertFalse(test("rnbqkbnr/p2ppppp/8/2nn4/1n6/8/P2PPPPP/R2QKBNR w KQkq - 0 1"));
    }

    @Test
    void impossiblePawnPosition() {
        Assertions.assertFalse(test("r1bqkb2/ppppppp1/8/8/8/7P/PPPP1PPP/RNBQKBNR w KQq - 0 1"));
    }

    @Test
    void possiblePawnPosition() {
        Assertions.assertTrue(test("r1bqkb2/ppppppp1/8/8/8/5P2/PPPP1PPP/RNBQKBNR w KQq - 0 1"));
    }

    @Test
    void impossiblePawnPositionTwo() {
        Assertions.assertFalse(test("3qkb2/3pppp1/8/8/8/P7/PP1PPPPP/RNBQKBNR w KQ - 0 1"));
    }

    @Test
    void impossiblePawnPositionThree() {
        Assertions.assertFalse(test("3qkb2/3pppp1/8/8/3P4/2P5/1P1PPPPP/RNBQKBNR w KQ - 0 1"));
    }

    @Test
    void possiblePawnPositionTwo() {
        Assertions.assertTrue(test("3qkb2/3pppp1/8/8/3P4/2P5/P2PPPPP/RNBQKBNR w KQ - 0 1"));
    }

    @Test
    void impossiblePawnPositionCaptures() {
        Assertions.assertFalse(test("rnbqkbnr/pppppppp/8/8/8/P7/P1PPPPPP/RNBQKBNR w KQkq - 0 1"));
    }

    @Test
    void impossiblePawnPositionCapturesTwo() {
        Assertions.assertFalse(test("r1bqkb1r/pppppppp/8/2P5/2P5/3P4/PP2P1PP/RNBQKBNR w KQkq - 0 1"));
    }

    @Test
    void impossibleMap() {
        Assertions.assertFalse(test("rnbqkbnr/1ppppppp/8/P7/p7/8/1PPPPPPP/RNBQKBNR w KQkq - 0 1"));
    }

    @Test
    void impossibleMapTwo() {
        Assertions.assertFalse(test("rnbqkbnr/2pppppp/8/P7/p7/8/1PPPPPPP/RNBQKBNR w KQkq - 0 1"));
    }

    @Test
    void possibleMap() {
        Assertions.assertTrue(test("r1bqkb1r/1ppppppp/8/P7/p7/8/1PPPPPPP/RNBQKBNR w KQkq - 0 1"));
    }

    @Test
    void impossibleMapThree() {
        Assertions.assertFalse(test("r1b1kb1r/pp1ppppp/8/4P3/P4p2/8/1PPPPPP1/2BQKB1R w Kkq - 0 1"));
    }

    @Test
    void possibleMapTwo() {
        Assertions.assertTrue(test("r1b1kb1r/pp1pp1pp/8/4P3/P4p2/8/1PPPPPP1/2BQKB1R w Kkq - 0 1"));
    }

    @Test
    void impossibleMapFour() {
        Assertions.assertFalse(test("4k2r/2Pppp1p/1p1p2p1/p7/8/1P6/PP1PPPP1/RNBQKB1R w KQk - 0 1"));
    }

    @Test
    void impossibleMapFive() {
        Assertions.assertFalse(test("rnbqkbnr/ppp1pppp/8/3P4/3p4/8/PPP1PP1P/RNBQKBNR w KQkq - 0 1"));
    }

//    @Test
//    void impossibleMapSix() {
//        Assertions.assertFalse(test("rnbqkbnr/pp2pppp/8/3P4/3p4/8/PPP1PP1P/RNBQKBNR w KQkq - 0 1"));
//    }

    @Test
    void impossibleMapSeven() {
        Assertions.assertFalse(test("rnbqkbnr/2p1pppp/P3p3/p7/8/8/1PPPPPPP/RNBQKB1R w KQkq - 0 1"));
    }

    @Test
    void possibleMapThree() {
        Assertions.assertTrue(test("rnbqkbnr/2p1pppp/4p3/p7/8/8/PPPPPPPP/RNBQKB1R w KQkq - 0 1"));
    }

    @Test
    void possibleMapFour() {
        Assertions.assertTrue(test("rnbqkbnr/2p1pppp/P3p3/p7/8/8/1PPPPPPP/R1BQKB1R w KQkq - 0 1"));
    }

    @Test
    void impossibleMapEight() {
        Assertions.assertFalse(test("rnbqkbnr/2pppppp/P7/p7/4p3/6P1/1PPPPP1P/2BQK2R w Kkq - 0 1"));
    }

    @Test
    void impossiblePieceMapBishop() {
        Assertions.assertFalse(test("rnbqkbnr/pppppppp/8/2B5/8/2P1P3/PP1PBPPP/RN1QK1NR w KQkq - 0 1"));
    }

    @Test
    void impossiblePieceMapBishopTwo() {
        Assertions.assertFalse(test("rnbqkbnr/pppppppp/8/5B2/8/8/PPPPPPPP/RNBQK1NR w KQkq - 0 1"));
    }

    @Test
    void impossiblePieceMapBishopThree() {
        Assertions.assertFalse(test("rn1qk1nr/1p1pp1p1/2p2b1p/2b5/p4p2/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1"));
    }

    @Test
    void impossiblePieceMapRook() {
        Assertions.assertFalse(test("rnbqkbnr/pppppppp/8/8/R6R/1P4PP/P1PPPP2/1NBQKBN1 w kq - 0 1"));
    }

    @Test
    void impossiblePieceMapRookTwo() {
        Assertions.assertFalse(test("rnbqkbnr/pppppppp/8/8/2P2PR1/8/PP1PP1PP/1NBQKBN1 w kq - 0 1"));
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


}
