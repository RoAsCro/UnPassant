import Heuristics.BoardInterface;
import Heuristics.Deduction;
import Heuristics.Deductions.*;
import Heuristics.Observations.PawnNumber;
import Heuristics.Observations.PieceNumber;
import Heuristics.UnCastle;
import StandardChess.BoardBuilder;
import StandardChess.ChessBoard;

public class StateDetectorFactory {

    public static final int pmwPosition = 1;
    public static final int pmbPosition = 2;
    public static final int pmPosition = 4;
    public static final int prmPosition = 6;
    public static final int ppsPosition = 7;

    private static Deduction[] getDeductions(PawnNumber pawnNumber, PieceNumber pieceNumber, Deduction... deductions) {
        PawnMapWhite pmw = null;
        PawnMapBlack pmb = null;
        CombinedPawnMap cpm = null;
//        System.out.println(Arrays.stream(deductions).toList());
        for (Deduction d : deductions) {
            if (d instanceof PawnMapWhite p) {
                pmw = new PawnMapWhite(p);
            } else if (d instanceof PawnMapBlack p) {
                pmb = new PawnMapBlack(p);
            } else if (d instanceof CombinedPawnMap p) {
                cpm = p;
            }
        }
//        System.out.println(pmw);
        pmw = pmw == null ? new PawnMapWhite(pawnNumber, pieceNumber) : pmw;
        pmb = pmb == null ? new PawnMapBlack(pawnNumber, pieceNumber) : pmb;
        cpm = cpm == null ? new CombinedPawnMap(pmw, pmb) : cpm;

//        System.out.println(pmw.getPawnOrigins());

        PawnPositions pp = new PawnPositions();
        PieceMap pm = new PieceMap(cpm);
        CaptureLocations cl = new CaptureLocations(pmw, pmb, pm, cpm);
        PromotionMap prm = new PromotionMap(pm, cpm, pmw, pmb, cl, pieceNumber, pawnNumber);
        PromotedPawnSquares pps = new PromotedPawnSquares(pieceNumber, pm, prm, cl, cpm);
//        Deduction[] deductions1 = new Deduction[]{};
        return new Deduction[]{pp,
                pmw,
                pmb,
                cpm,
                pm,
                cl,
                prm,
                pps
        };
    }

    public static SolverImpossibleStateDetector getDetector(ChessBoard board) {
        PawnNumber pawnNumber = new PawnNumber();
        PieceNumber pieceNumber = new PieceNumber();
        Deduction[] deductions = getDeductions(pawnNumber, pieceNumber);
        UnCastle unCastle = getUnCastle(deductions);
        return new SolverImpossibleStateDetector(pawnNumber, pieceNumber, unCastle, new BoardInterface(board), deductions);
    }

    public static SolverImpossibleStateDetector getDetector(String fen) {
        return getDetector(BoardBuilder.buildBoard(fen));
    }

    public static SolverImpossibleStateDetector getDetector(ChessBoard board, Deduction... deductions) {
        PawnNumber pawnNumber = new PawnNumber();
        PieceNumber pieceNumber = new PieceNumber();
        UnCastle unCastle = getUnCastle(deductions);

        return new SolverImpossibleStateDetector(pawnNumber, pieceNumber, unCastle,
                new BoardInterface(board), getDeductions(pawnNumber, pieceNumber, deductions));
    }

    private static UnCastle getUnCastle(Deduction... deductions) {
        return new UnCastle((PawnMap) deductions[pmwPosition],
                (PawnMap) deductions[pmbPosition],
                (PieceMap) deductions[pmPosition],
                (PromotionMap) deductions[prmPosition],
                (PromotedPawnSquares) deductions[ppsPosition]);
    }


    }
