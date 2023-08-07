import Heuristics.BoardInterface;
import Heuristics.Deduction;
import Heuristics.Deductions.*;
import Heuristics.Observations.PawnNumber;
import Heuristics.Observations.PieceNumber;
import StandardChess.BoardBuilder;
import StandardChess.ChessBoard;

public class StateDetectorFactory {

    private static Deduction[] getDeductions(PawnNumber pawnNumber, PieceNumber pieceNumber) {
        PawnMapWhite pmw = new PawnMapWhite(pawnNumber, pieceNumber);
        PawnMapBlack pmb = new PawnMapBlack(pawnNumber, pieceNumber);
        CombinedPawnMap cpm = new CombinedPawnMap(pmw, pmb);
        PieceMap pm = new PieceMap(cpm);
        CaptureLocations cl = new CaptureLocations(pmw, pmb, pm, cpm);
        PromotionMap prm = new PromotionMap(pm, cpm, pmw, pmb, cl, pieceNumber, pawnNumber);
        return new Deduction[]{pmw, pmb, cpm, cl, prm};
    }

    public static SolverImpossibleStateDetector getDetector(ChessBoard board) {
        PawnNumber pawnNumber = new PawnNumber();
        PieceNumber pieceNumber = new PieceNumber();
        return new SolverImpossibleStateDetector(pawnNumber, pieceNumber, new BoardInterface(board), getDeductions(pawnNumber, pieceNumber));
    }

    public static SolverImpossibleStateDetector getDetector(String fen) {
        return getDetector(BoardBuilder.buildBoard(fen));
    }

}
