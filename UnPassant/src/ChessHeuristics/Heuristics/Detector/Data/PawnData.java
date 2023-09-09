package ChessHeuristics.Heuristics.Detector.Data;

import ChessHeuristics.Heuristics.Path;
import ReverseChess.StandardChess.Coordinate;

import java.util.List;
import java.util.Map;

/**
 * Stores data about pawns in a chess game. For use with a StateDetector.
 * @author Roland Crompton
 */
public interface PawnData {
    /**
     * Returns a Map of Coordinates of pawns of the given colour and a List of potential
     * Paths they took from pawn origins to their current location.
     * @param white the colour of the pawns, true if white, false if black
     * @return the Map of pawns and their Paths
     */
    Map<Coordinate, List<Path>> getPawnPaths(boolean white);

    /**
     * Returns the sum of minimum number of captures all pawns of the given colour can make.
     * @param white the colour of the pawns, true if white, false if black
     * @return the sum of minimum number of captures all pawns of the given colour can make
     */
    int minimumPawnCaptures(boolean white);

    /**
     * Returns a Map of Coordinates of pawn origins and a Boolean that will be true if that origin can have a pawn
     * besides those already stored in the pawnPaths originate from it.
     * @param white the colour of the pawn origins, true if white, false if black
     * @return a Map of pawn origins and whether they are free
     */
    Map<Coordinate, Boolean> getOriginFree(boolean white);
}
