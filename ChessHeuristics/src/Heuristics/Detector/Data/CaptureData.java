package Heuristics.Detector.Data;

import Heuristics.Path;

/**
 * Stores data about captures in a chess game. For use with a StateDetector.
 */
public interface CaptureData {
    /**
     * Retrieves a Path of Coordinates of pieces that cannot, as they are listed in the Path, have been captured
     * by any pawns of the opposing colour currently on the board.
     * @param white the colour of the pieces being retrieved, true if white, false if black
     * @return a Path coordinates of pieces of the given colour that cannot have been captured by opposing pawns
     */
    Path getNonPawnCaptures(boolean white);

    /**
     * Sets the number of pawns that have been captured by pawns of the given colour.
     * @param white the colour of the pawns doing the capturing, true if white, false if black
     * @param pawnsCapturedByPawns the number to set the pawns captured by pawns to
     */
    void setPawnsCapturedByPawns(boolean white, int pawnsCapturedByPawns);

    /**
     * Retrieves the number of pawns that have been captured by pawns of the given colour.
     * @param white the colour of the pawns doing the capturing, true if white, false if black
     * @return the number of pawns that have been captured by pawns of the given colour
     */
    int getPawnsCapturedByPawns(boolean white);

    /**
     * Returns the maximum number of pieces that pawns of the given colour can capture. This is calculated as
     *      * 16 - non-pawns stored in the nonPawnCaptures.
     * @param white the colour of the pawns doing the capturing, true if white, false if black
     * @return the maximum number of pieces that pawns of the given colour can captures
     */
    int pawnTakeablePieces(boolean white);
}
