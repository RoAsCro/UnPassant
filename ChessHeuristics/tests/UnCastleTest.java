import Heuristics.BoardInterface;
import Heuristics.Deductions.*;
import Heuristics.Observations.PawnNumber;
import Heuristics.Observations.PieceNumber;
import Heuristics.UnCastle;
import StandardChess.BoardBuilder;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class UnCastleTest {

    PawnNumber pawnNumber;
    PieceNumber pieceNumber;
    TestImpossibleStateDetector detector;
    PawnMapWhite pmw;
    PawnMapBlack pmb;
    PieceMap pm;
    PromotionMap prm;
    UnCastle uc;

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
        this.uc = new UnCastle(pmw, pmb, pm, prm);
        this.detector = new TestImpossibleStateDetector(pawnNumber, pieceNumber,
                pmw,
                pmb,
                cpm,
                pm,
                cl,
                prm);
    }

    @Test
    public void startingPosition() {
        this.detector.testState(new BoardInterface(BoardBuilder.buildBoard("rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1")));
        boolean[] booleans = uc.hasMoved();
        Assertions.assertFalse(booleans[0]);
        Assertions.assertFalse(booleans[1]);

    }

    @Test
    public void startingPositionMovedRookAndQueen() {
        this.detector.testState(new BoardInterface(BoardBuilder.buildBoard("rnbqkbn1/pppprppp/4p3/8/8/5PQ1/PPPPP1PP/RNB1KBNR w KQq - 0 1")));
        boolean[] booleans = uc.hasMoved();
        Assertions.assertTrue(booleans[0]);
        Assertions.assertTrue(booleans[1]);

    }

    @Test
    public void castlingCannotHaveTakenPlace() {
        this.detector.testState(new BoardInterface(BoardBuilder.buildBoard("rnbqkbnr/ppp1pppp/4p3/4Q3/8/8/PPP1PPPP/RNBQKB1R w KQkq - 0 1")));
        boolean[] booleans = uc.hasMoved();
        Assertions.assertTrue(booleans[0]);
        Assertions.assertFalse(booleans[1]);

    }

}
