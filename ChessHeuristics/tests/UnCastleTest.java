import Heuristics.BoardInterface;
import Heuristics.Deductions.*;
import Heuristics.Detector.Data.StandardCaptureData;
import Heuristics.Detector.Data.StandardPawnData;
import Heuristics.Detector.Data.StandardPieceData;
import Heuristics.Detector.Data.StandardPromotionData;
import Heuristics.Detector.StandardStateDetector;
import Heuristics.Detector.StateDetectorFactory;
import Heuristics.DetectorInterface;
import Heuristics.Deductions.UnCastle;
import StandardChess.BoardBuilder;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

public class UnCastleTest {

    StandardStateDetector detector;
//    PawnMap pmw;
//    PawnMap pmb;
    PieceMap pm;
    PromotionMap prm;
    UnCastle uc;
    PromotedPawnSquares pps;

    @BeforeEach
    public void setup() {
//        this.pmw = new PawnMap(true);
//        this.pmb = new PawnMap(false);
        CombinedPawnMap cpm = new CombinedPawnMap();
        this.pm = new PieceMap();
        CaptureLocations cl = new CaptureLocations();
        this.prm = new PromotionMap();
        this.pps = new PromotedPawnSquares();
        this.uc = new UnCastle();
        this.detector = new StandardStateDetector(new StandardPawnData(), new StandardCaptureData(), new StandardPromotionData(), new StandardPieceData(),
//                pmw,
//                pmb,
                cpm,
                pm,
                cl,
                prm,
                pps);
        this.uc.registerStateDetector(detector);
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
        this.detector.testState(new BoardInterface(BoardBuilder.buildBoard("rnbqkbn1/pppprppp/4p3/8/8/5PQ1/PPPPP1PP/RNB1KBNR w - - 0 1")));
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

        
        Assertions.assertFalse(booleans.get(0)[0]);
        Assertions.assertTrue(booleans.get(1)[0]);

    }

    @Test
    public void movedRook() {
        this.detector.testState(new BoardInterface(BoardBuilder.buildBoard("1nb1kb1r/rppp1ppp/p4p2/8/8/7P/PPPPPP1R/RNBQKBN1 w Qk - 0 1")));
        List<boolean[]> booleans = uc.hasMoved();
        
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

    @Test
    void r2() {
        System.out.println(this.detector.testState(new BoardInterface(BoardBuilder.buildBoard("r3kqR1/1p1pppp1/5B1P/6PN/4P2p/3Q1P2/2PK2pP/8 b q - 0 1"))));
        List<boolean[]> booleans = uc.hasMoved();

        Assertions.assertTrue(booleans.get(1)[1]);

    }

    @Test
    void failedInSolverTwo() {
        System.out.println(this.detector.testState(new BoardInterface(BoardBuilder.buildBoard("r3kr2/1pp2p2/1pn2npP/1Q1pp3/1b6/2N2NPP/1PPP1P2/R3K1R1 w Qq - 0 1"))));
        List<boolean[]> booleans = uc.hasMoved();
        Assertions.assertTrue(booleans.get(1)[0]);

    }

    @Test
    void failedInSolverThree() {
        System.out.println(this.detector.testState(new BoardInterface(BoardBuilder.buildBoard("r3kr2/1pp2p2/1pn2npP/1Q1pp3/1b6/2N2NPP/1PPP1P2/R3K1R1 w q - 0 1"))));
        List<boolean[]> booleans = uc.hasMoved();
        Assertions.assertTrue(booleans.get(1)[1]);

    }

    @Test
    void failedInSolverFour() {
        System.out.println(this.detector.testState(new BoardInterface(BoardBuilder.buildBoard("r7/pppk2pp/6p1/P3p2P/1NB1K3/p5P1/PP3PP1/R7 w - - 0 1"))));
        Assertions.assertTrue(this.detector.getPieceData().getKingMovement(false));
        DetectorInterface detectorInterface = StateDetectorFactory.getDetectorInterface(BoardBuilder.buildBoard("r7/pppk2pp/6p1/P3p2P/1NB1K3/p5P1/PP3PP1/R7 w - - 0 1"));
        detectorInterface.testState();
        Assertions.assertFalse(detectorInterface.canCastle(false));
    }




}
