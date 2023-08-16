package Heuristics.Detector;

import Heuristics.BoardInterface;
import Heuristics.Path;
import StandardChess.Coordinate;

import java.util.Map;

/**
 * Provides a read-only interface for a StateDetector, providing the means to have a board state's
 * validity determined, and providing the information required to form StateConditions.
 */
public interface DetectorInterface {
    /**
     * Provides a Map of promotions made by the given player, in the format:
     * <></>
     * [s, [p, i]]
     * <></>
     * Where s is a string describing the piece's type, as well as whether it's light or dark if it's a bishop,
     * p is a Path containing the Coordinates of all pieces that may be promoted of that type,
     * and i is the number of those pieces that must be promoted.
     * @param white the player whose promotions are to be returned, true if white, false if black
     * @return a Map of promotions made by the given player
     */
    Map<String, Map<Path, Integer>> getPromotions(boolean white);

    Map<Coordinate, Path> getPawnMap(boolean white);

    Path getCages(boolean white);

    Path getPiecesNotCapturedByPawns(boolean white);

    boolean testState(BoardInterface boardInterface);

    boolean testState();

    boolean canCastle(boolean white);

    boolean getState();

    String getErrorMessage();
}
