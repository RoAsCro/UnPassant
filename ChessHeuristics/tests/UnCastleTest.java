import Heuristics.BoardInterface;
import Heuristics.Deductions.*;
import Heuristics.Observations.PawnNumber;
import Heuristics.Observations.PieceNumber;
import Heuristics.UnCastle;
import StandardChess.BoardBuilder;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

public class UnCastleTest {

    PawnNumber pawnNumber;
    PieceNumber pieceNumber;
    TestImpossibleStateDetector detector;
    PawnMapWhite pmw;
    PawnMapBlack pmb;
    PieceMap pm;
    PromotionMap prm;
    UnCastle uc;
    PromotedPawnSquares pps;

    @BeforeEach
    public void setup() {
        this.pawnNumber = new PawnNumber();
        this.pieceNumber = new PieceNumber();
        this.pmw = new PawnMapWhite(this.pawnNumber, this.pieceNumber);
        this.pmb = new PawnMapBlack(this.pawnNumber, this.pieceNumber);
        CombinedPawnMap cpm = new CombinedPawnMap(pmw, pmb);
        this.pm = new PieceMap(cpm);
        CaptureLocations cl = new CaptureLocations(pmw, pmb, pm, cpm);
        this.prm = new PromotionMap(pm, cpm, pmw, pmb, cl, pieceNumber, pawnNumber);
        this.pps = new PromotedPawnSquares(pieceNumber, pm, prm, cl, cpm);
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

    @Test
    public void startingPosition() {
        this.detector.testState(new BoardInterface(BoardBuilder.buildBoard("rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1")));
        List<boolean[]> booleans = uc.hasMoved();
        Assertions.assertFalse(booleans.get(0)[0]);
        Assertions.assertFalse(booleans.get(1)[0]);

    }

    @Test
    public void startingPositionMovedRookAndQueen() {
        this.detector.testState(new BoardInterface(BoardBuilder.buildBoard("rnbqkbn1/pppprppp/4p3/8/8/5PQ1/PPPPP1PP/RNB1KBNR w KQq - 0 1")));
        List<boolean[]> booleans = uc.hasMoved();
        Assertions.assertTrue(booleans.get(0)[0]);
        Assertions.assertTrue(booleans.get(1)[0]);

    }

    @Test
    public void castlingCannotHaveTakenPlace() {
        this.detector.testState(new BoardInterface(BoardBuilder.buildBoard("rnbqkbnr/ppp1pppp/4p3/4Q3/8/8/PPP1PPPP/RNBQKB1R w KQkq - 0 1")));
        List<boolean[]> booleans = uc.hasMoved();
        booleans.forEach(b -> {
            System.out.println(b[0]);
            System.out.println(b[1]);
            System.out.println(b[2]);
        });
        Assertions.assertFalse(booleans.get(0)[0]);
        Assertions.assertTrue(booleans.get(1)[0]);

    }

    @Test
    public void castlingCannotHaveTakenPlaceDueToPromotion() {
        this.detector.testState(new BoardInterface(BoardBuilder.buildBoard("rnb1kb1r/pppp1ppp/3p4/8/8/8/PPPPPP1P/RNBQKBNR w KQkq - 0 1")));
        List<boolean[]> booleans = uc.hasMoved();

        System.out.println(this.pps.getPromotionPaths(true));
        Assertions.assertFalse(booleans.get(0)[0]);
        Assertions.assertTrue(booleans.get(1)[0]);

    }

    @Test
    public void movedRook() {
        this.detector.testState(new BoardInterface(BoardBuilder.buildBoard("1nb1kb1r/rppp1ppp/p4p2/8/8/7P/PPPPPP1R/RNBQKBN1 w Qk - 0 1")));
        List<boolean[]> booleans = uc.hasMoved();
        System.out.println(this.pps.getPromotionPaths(true));
        Assertions.assertFalse(booleans.get(0)[1]);
        Assertions.assertTrue(booleans.get(0)[2]);

        Assertions.assertTrue(booleans.get(1)[1]);
        Assertions.assertFalse(booleans.get(1)[2]);

    }

    @Test
    public void promotionMovedRook() {
        this.detector.testState(new BoardInterface(BoardBuilder.buildBoard("rnbqkbnr/ppppppp1/6p1/8/5Q2/5R2/PPPPPPP1/RNBQKB2 w Qkq - 0 1")));
        List<boolean[]> booleans = uc.hasMoved();

        Assertions.assertFalse(booleans.get(0)[1]);
        Assertions.assertTrue(booleans.get(0)[2]);

        Assertions.assertFalse(booleans.get(1)[1]);
        Assertions.assertTrue(booleans.get(1)[2]);

    }

    @Test
    void promotionPastKingActual() {
        this.detector.testState(new BoardInterface(BoardBuilder.buildBoard("4k2r/8/8/2p5/8/5q2/2P1PPP1/3bK2R w - - 0 1")));
        List<boolean[]> booleans = uc.hasMoved();
        Assertions.assertTrue(booleans.get(0)[0]);

    }

    @Test
    void failedInSolver() {
        System.out.println(this.detector.testState(new BoardInterface(BoardBuilder.buildBoard("4k2Q/8/8/2p5/8/5q2/2P1PPPr/3bK2R b K - 0 1"))));
        List<boolean[]> booleans = uc.hasMoved();
        Assertions.assertTrue(booleans.get(1)[0]);

    }



}
