package Heuristics.Detector;

import Heuristics.BoardInterface;
import Heuristics.Detector.Data.CaptureData;
import Heuristics.Detector.Data.PawnData;
import Heuristics.Detector.Data.PieceData;
import Heuristics.Detector.Data.PromotionData;

/**
 *  The StateDetector detects whether a given board state is legal, storing the information derived about the board
 *  in various Data objects, the PawnData for information about pawns, the PieceData for information about non-pawn
 *  pieces, the CaptureData for information about captures, and the PromotionData for information about promotions.
 *  This interface is intended for use with Deductions, going through them sequentially to establish facts about,
 *  and test the legality of, a board state.
 *  @author Roland Crompton
 */
public interface StateDetector {

    /**
     * Gets the legality of the board it's been given.
     * @return the legality of the board given, true if legal, false if not
     */
    boolean getState();

    /**
     * Test the state of the board stored inside the StateDetector, returning its legality.
     * @return true if the board is legal, false otherwise
     */
    boolean testState();
    /**
     * Test the state of the given board, returning its legality.
     * @return true if the board is legal, false otherwise
     */
    boolean testState(BoardInterface board);

    /**
     * Rerun the deductions already completed.
     * @param boardInterface the board to be used by the deductions
     */
    void reTest(BoardInterface boardInterface);

    /**
     * Returns the current error message, describing why the board's state is false.
     * @return the current error message
     */
    String getErrorMessage();

    /**
     * Return the PawnData stored in the StateDetector.
     * @return the PawnData
     */
    PawnData getPawnData();
    /**
     * Return the PieceData stored in the StateDetector.
     * @return the PieceData
     */
    PieceData getPieceData();
    /**
     * Return the PromotionData stored in the StateDetector.
     * @return the PromotionData
     */
    PromotionData getPromotionData();
    /**
     * Return the CaptureData stored in the StateDetector.
     * @return the CaptureData
     */
    CaptureData getCaptureData();

}
