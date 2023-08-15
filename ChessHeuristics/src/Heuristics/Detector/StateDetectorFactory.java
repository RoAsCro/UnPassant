package Heuristics.Detector;

import Heuristics.BoardInterface;
import Heuristics.Deduction;
import Heuristics.Deductions.*;
import Heuristics.Detector.Data.StandardCaptureData;
import Heuristics.Detector.Data.StandardPawnData;
import Heuristics.Detector.Data.StandardPieceData;
import Heuristics.Detector.Data.StandardPromotionData;
import Heuristics.DetectorInterface;
import Heuristics.Observations.PawnNumber;
import Heuristics.Observations.PieceNumber;
import StandardChess.BoardBuilder;
import StandardChess.ChessBoard;

public class StateDetectorFactory {

    public static final int pmwPosition = 1;
    public static final int pmbPosition = 2;
    public static final int pmPosition = 4;
    public static final int prmPosition = 6;
    public static final int ppsPosition = 7;

    private static Deduction[] getDeductions() {
        CombinedPawnMap cpm;
//        System.out.println(Arrays.stream(deductions).toList());
//        System.out.println(pmw);
        cpm = new CombinedPawnMap();

//        System.out.println(pmw.getPawnOrigins());

        PawnPositions pp = new PawnPositions();
        PieceMap pm = new PieceMap();
        CaptureLocations cl = new CaptureLocations();
        PromotionMap prm = new PromotionMap();
        PromotedPawnSquares pps = new PromotedPawnSquares();
//        Deduction[] deductions1 = new Deduction[]{};
        return new Deduction[]{pp,

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
        Deduction[] deductions = getDeductions();
        UnCastle unCastle = new UnCastle();
        return new SolverImpossibleStateDetector(pawnNumber, pieceNumber, new StandardPawnData(), new StandardCaptureData(),
                new StandardPromotionData(), new StandardPieceData(), unCastle, new BoardInterface(board), deductions);
    }

    public static SolverImpossibleStateDetector getDetector(String fen) {
        return getDetector(BoardBuilder.buildBoard(fen));
    }

    public static DetectorInterface getDetectorInterface(ChessBoard board) {
        return new StandardDetectorInterface(getDetector(board));
    }

    public static DetectorInterface getDetectorInterface(String fen) {
        return getDetectorInterface(BoardBuilder.buildBoard(fen));
    }

    public static SolverImpossibleStateDetector getDetector(ChessBoard board, Deduction... deductions) {
        PawnNumber pawnNumber = new PawnNumber();
        PieceNumber pieceNumber = new PieceNumber();
        UnCastle unCastle = new UnCastle();

        return new SolverImpossibleStateDetector(pawnNumber, pieceNumber, new StandardPawnData(), new StandardCaptureData(),
                new StandardPromotionData(), new StandardPieceData(), unCastle,
                new BoardInterface(board), getDeductions());
    }

    }
