package Heuristics.Detector;

import Heuristics.BoardInterface;
import Heuristics.Deductions.*;
import Heuristics.Detector.Data.StandardCaptureData;
import Heuristics.Detector.Data.StandardPawnData;
import Heuristics.Detector.Data.StandardPieceData;
import Heuristics.Detector.Data.StandardPromotionData;
import StandardChess.BoardBuilder;
import StandardChess.ChessBoard;

public class StateDetectorFactory {


    private static Deduction[] getDeductions() {
        CombinedPawnMap cpm;
        cpm = new CombinedPawnMap();
        PawnPositions pp = new PawnPositions();
        PieceMap pm = new PieceMap();
        CaptureLocations cl = new CaptureLocations();
        PromotionMap prm = new PromotionMap();
        PromotedPawnSquares pps = new PromotedPawnSquares();
        UnCastle uc = new UnCastle();
        return new Deduction[]{pp, cpm, pm, cl, prm, pps, uc};
    }

    public static StateDetector getDetector(ChessBoard board) {
        Deduction[] deductions = getDeductions();
        return new SolverImpossibleStateDetector(new StandardPawnData(), new StandardCaptureData(),
                new StandardPromotionData(), new StandardPieceData(), new BoardInterface(board), deductions);
    }

    public static StateDetector getDetector(String fen) {
        return getDetector(BoardBuilder.buildBoard(fen));
    }

    public static DetectorInterface getDetectorInterface(ChessBoard board) {
        return new StandardDetectorInterface(getDetector(board));
    }

    public static DetectorInterface getDetectorInterface(String fen) {
        return getDetectorInterface(BoardBuilder.buildBoard(fen));
    }

    public static StateDetector getDetector(String fen, Deduction... deductions) {
        return new SolverImpossibleStateDetector(new StandardPawnData(), new StandardCaptureData(),
                new StandardPromotionData(), new StandardPieceData(),
                new BoardInterface(BoardBuilder.buildBoard(fen)), deductions);
    }

    }
