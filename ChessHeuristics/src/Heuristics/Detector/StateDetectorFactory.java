package Heuristics.Detector;

import Heuristics.BoardInterface;
import Heuristics.Deduction;
import Heuristics.Deductions.*;
import Heuristics.Detector.Data.StandardCaptureData;
import Heuristics.Detector.Data.StandardPawnData;
import Heuristics.Detector.Data.StandardPieceData;
import Heuristics.Detector.Data.StandardPromotionData;
import Heuristics.DetectorInterface;
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
        cpm = new CombinedPawnMap();
        PawnPositions pp = new PawnPositions();
        PieceMap pm = new PieceMap();
        CaptureLocations cl = new CaptureLocations();
        PromotionMap prm = new PromotionMap();
        PromotedPawnSquares pps = new PromotedPawnSquares();
        return new Deduction[]{pp, cpm, pm, cl, prm, pps};
    }

    public static SolverImpossibleStateDetector getDetector(ChessBoard board) {
        Deduction[] deductions = getDeductions();
        UnCastle unCastle = new UnCastle();
        return new SolverImpossibleStateDetector(new StandardPawnData(), new StandardCaptureData(),
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

    public static SolverImpossibleStateDetector getDetector(String fen, Deduction... deductions) {
        UnCastle unCastle = new UnCastle();

        return new SolverImpossibleStateDetector(new StandardPawnData(), new StandardCaptureData(),
                new StandardPromotionData(), new StandardPieceData(), unCastle,
                new BoardInterface(BoardBuilder.buildBoard(fen)), deductions);
    }

    }
