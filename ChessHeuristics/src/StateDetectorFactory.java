import Heuristics.BoardInterface;
import Heuristics.Deduction;
import Heuristics.Deductions.*;
import Heuristics.Observations.PawnNumber;
import Heuristics.Observations.PieceNumber;
import StandardChess.BoardBuilder;
import StandardChess.ChessBoard;

import java.util.Arrays;

public class StateDetectorFactory {

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
        return new Deduction[]{pp, pmw, pmb, pm, cpm, cl, prm};
    }

    public static SolverImpossibleStateDetector getDetector(ChessBoard board) {
        PawnNumber pawnNumber = new PawnNumber();
        PieceNumber pieceNumber = new PieceNumber();
        return new SolverImpossibleStateDetector(pawnNumber, pieceNumber, new BoardInterface(board), getDeductions(pawnNumber, pieceNumber));
    }

    public static SolverImpossibleStateDetector getDetector(String fen) {
        return getDetector(BoardBuilder.buildBoard(fen));
    }

    public static SolverImpossibleStateDetector getDetector(ChessBoard board, Deduction... deductions) {
        PawnNumber pawnNumber = new PawnNumber();
        PieceNumber pieceNumber = new PieceNumber();
        return new SolverImpossibleStateDetector(pawnNumber, pieceNumber,
                new BoardInterface(board), getDeductions(pawnNumber, pieceNumber, deductions));
    }


    }
