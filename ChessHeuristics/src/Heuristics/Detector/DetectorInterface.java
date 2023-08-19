package Heuristics.Detector;

import Heuristics.BoardInterface;
import Heuristics.Path;
import StandardChess.Coordinate;

import java.util.Map;

/**
 * Provides a read-only interface for a StateDetector, providing the means to have a board state's
 * validity determined by a StateDetector, and providing the information required to form StateConditions.
 */
public interface DetectorInterface {
    /**
     * Provides a Map of promotions made by the given player, in the format:
     * <></>
     * [s, [p, i]]
     * <></>
     * Where s is a String describing the piece's type, as well as whether it's light or dark if it's a bishop,
     * p is a Path containing the Coordinates of all pieces that may be promoted of that type,
     * and i is the number of those pieces that must be promoted.
     * @param white the player whose promotions are to be returned, true if white, false if black
     * @return a Map of promotions made by the given player
     */
    Map<String, Map<Path, Integer>> getPromotions(boolean white);

    /**
     * Provides a Map of Coordinates of pawns of the given colour on the board and potential Paths from origins
     * they took to reach those Coordinates.
     * @param white the colour of the pawns, true if white, false if black
     * @return a Map of pawns and their potential Paths
     */
    Map<Coordinate, Path> getPawnMap(boolean white);

    /**
     * Returns a Path of Coordinates of non-pawn piece origins of the given colour which are caged.
     * @param white the colour of the pieces, true if white, false if black
     * @return a Path of Coordinates of non-pawn piece origins of the given colour which are caged
     */

    Path getCages(boolean white);

    /**
     * Returns a Path of Coordinates of missing pieces of the given colour that were not captured
     * by pawns of the opposing colour.
     * @param white the colour of the pieces captured, true if white, false if black
     * @return a Path of Coordinates of missing pieces of the given colour that were not captured
     * by pawns of the opposing colour
     */
    Path getPiecesNotCapturedByPawns(boolean white);

    /**
     * Has the StateDetector test the state of the given board interface and return whether it is valid.
     * @param boardInterface the board to be checked
     * @return whether the state is valid
     */
    boolean testState(BoardInterface boardInterface);
    /**
     * Has the StateDetector test the state of the stored board interface and return whether it is valid.
     * @return whether the state is valid
     */
    boolean testState();

    /**
     * Returns whether, according to the StateDetector, the king of the given colour can castle on the given side.
     * @param white the colour of the king true if white, false if black
     * @param queen the side of the rook, true if queenside, false if kingside
     * @return whether the king can castle on the specified side
     */
    boolean canCastle(boolean white, boolean queen);

    /**
     * Returns the current state of the StateDetector. Will be false if a board has been tested and found invalid.
     * @return the current state of the StateDetector
     */
    boolean getState();

    /**
     * Returns the StateDetector's current error message.
     * @return the StateDetector's current error message
     */
    String getErrorMessage();
}
