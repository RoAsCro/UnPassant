package Heuristics.Detector;

import Heuristics.BoardInterface;
import Heuristics.Deductions.*;
import Heuristics.Detector.Data.StandardCaptureData;
import Heuristics.Detector.Data.StandardPawnData;
import Heuristics.Detector.Data.StandardPieceData;
import Heuristics.Detector.Data.StandardPromotionData;
import StandardChess.BoardBuilder;
import StandardChess.ChessBoard;

/**
 * A factory providing methods for instantiating StateDetectors and DetectorInterfaces
 * without the calling class having to be aware of what implementation
 * of StateDetector, its data storage objects, or its Deductions, are being used.
 * @author Roland Crompton
 */
public class StateDetectorFactory {

    /**
     * Returns the standard set of Deductions for use in a StateDetector.
     * @return the standard set of Deductions
     */
    private static Deduction[] getDeductions() {
        CombinedPawnMap cpm = new CombinedPawnMap();
        PawnPositions pp = new PawnPositions();
        PieceMap pm = new PieceMap();
        CaptureLocations cl = new CaptureLocations();
        PromotionMap prm = new PromotionMap();
        PromotedPawnSquares pps = new PromotedPawnSquares();
        UnCastle uc = new UnCastle();
        return new Deduction[]{pp, cpm, pm, cl, prm, pps, uc};
    }

    /**
     * Returns a StateDetector that uses the given BoardInterface and a standard set of Deductions.
     * @param board the BoardInterface to be checked
     * @return a new instance of StateDetector using the given board
     */
    public static StateDetector getDetector(ChessBoard board) {
        Deduction[] deductions = getDeductions();
        return new StandardStateDetector(new StandardPawnData(), new StandardCaptureData(),
                new StandardPromotionData(), new StandardPieceData(), new BoardInterface(board), deductions);
    }

    /**
     * Returns a StateDetector that uses a standard set of Deductions.
     * @return a new instance of StateDetector
     */
    public static StateDetector getDetector(String fen) {
        return getDetector(BoardBuilder.buildBoard(fen));
    }

    /**
     * Returns a StateDetector that uses the given BoardInterface and only those Deductions given as parameters.
     * @param fen the FEN String to be checked
     * @param deductions the Deductions to  be included in the new StateDetector
     * @return a new instance of StateDetector using only those Deductions given as parameters
     */
    public static StateDetector getDetector(String fen, Deduction... deductions) {
        return new StandardStateDetector(new StandardPawnData(), new StandardCaptureData(),
                new StandardPromotionData(), new StandardPieceData(),
                new BoardInterface(BoardBuilder.buildBoard(fen)), deductions);
    }

    /**
     * Returns a DetectorInterface that uses the given BoardInterface and a standard set of Deductions.
     * @param board the BoardInterface to be checked
     * @return a new instance of DetectorInterface using the given board
     */
    public static DetectorInterface getDetectorInterface(ChessBoard board) {
        return new StandardDetectorInterface((StandardStateDetector) getDetector(board));
    }

    /**
     * Returns a DetectorInterface that uses the given FEN String to make a board and a standard set of Deductions.
     * @param fen the FEN String to be checked
     * @return a new instance of DetectorInterface using the given board
     */
    public static DetectorInterface getDetectorInterface(String fen) {
        return getDetectorInterface(BoardBuilder.buildBoard(fen));
    }

    }
