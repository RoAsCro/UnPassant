package Heuristics.Detector.Data;

import Heuristics.HeuristicsUtil;
import Heuristics.Path;

import java.util.List;

import static Heuristics.HeuristicsUtil.MAX_PIECES;

/**
 * An implementation of CaptureData.
 * @author Roland Crompton
 */
public class StandardCaptureData implements CaptureData {
    /**The location of black in the stored arrays and Lists*/
    private static final int BLACK = 1;
    /**The location of white in the stored arrays and Lists*/
    private static final int WHITE = 0;
    /**Stores for each colour the maximum number of pieces that pawns of the given colour can captures*/
    private final int[] pawnsCapturedByPawns = new int[]{0, 0};

    /**A List of Paths of Coordinates of pieces that cannot, as they are listed in the Path, have been captured
     * by any pawns of the opposing colour currently on the board*/
    private final List<Path> nonPawnCaptures = List.of(new Path(), new Path());

    /**
     * Retrieves a Path of Coordinates of pieces that cannot, as they are listed in the Path, have been captured
     * by any pawns of the opposing colour currently on the board.
     * @param white the colour of the pieces being retrieved, true if white, false if black
     * @return a Path coordinates of pieces of the given colour that cannot have been captured by opposing pawns
     */
    @Override
    public Path getNonPawnCaptures(boolean white) {
        return this.nonPawnCaptures.get(white ? WHITE : BLACK);
    }
    /**
     * Sets the number of pawns that have been captured by pawns of the given colour.
     * @param white the colour of the pawns doing the capturing, true if white, false if black
     * @param pawnsCapturedByPawns the number to set the pawns captured by pawns to
     */
    @Override
    public void setPawnsCapturedByPawns(boolean white, int pawnsCapturedByPawns) {
        this.pawnsCapturedByPawns[white ? WHITE : BLACK] = pawnsCapturedByPawns;

    }
    /**
     * Retrieves the number of pawns that have been captured by pawns of the given colour.
     * @param white the colour of the pawns doing the capturing, true if white, false if black
     * @return the number of pawns that have been captured by pawns of the given colour
     */
    @Override
    public int getPawnsCapturedByPawns(boolean white) {
        return pawnsCapturedByPawns[white ? WHITE : BLACK];
    }
    /**
     * Returns the maximum number of pieces that pawns of the given colour can capture. This is calculated as
     * 16 - non-pawns stored in the nonPawnCaptures.
     * @param white the colour of the pawns doing the capturing, true if white, false if black
     * @return the maximum number of pieces that pawns of the given colour can captures
     */
    @Override
    public int pawnTakeablePieces(boolean white) {
        return MAX_PIECES - getNonPawnCaptures(white)
                .stream().filter(c -> c.getY() != (white ? HeuristicsUtil.WHITE_PAWN_Y : HeuristicsUtil.BLACK_PAWN_Y))
                .toList()
                .size();
    }
}
