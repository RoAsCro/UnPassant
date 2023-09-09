package ChessHeuristics.Heuristics.Detector.Data;

import ChessHeuristics.Heuristics.Path;
import ReverseChess.StandardChess.Coordinate;

import java.util.Map;

/**
 * Stores data about non-pawn pieces in a chess game. For use with a StateDetector.
 * @author Roland Crompton
 */
public interface PieceData {
    /**
     * Retrieves a Map of Coordinates of non-pawn origins and a Boolean of whether they are caged.
     * @return a Map of Coordinates of non-pawn origins and a Boolean of whether they are caged
     */
    Map<Coordinate, Boolean> getCaged();

    /**
     * Retrieves a Map of the Coordinates of all non-pawn piece origins besides that of knights, and a Map of
     * Coordinates of non-pawn pieces of the corresponding type that can be pathed both to and from that origin,
     * and a potential Path from the origin to that piece
     * @return a Map of piece origins, pieces that may have come from them, and Paths from that origin to that piece
     */
    Map<Coordinate, Map<Coordinate, Path>> getPiecePaths();

    /**
     * Return whether the king of the given colour has moved.
     * @param white the colour of the king, true if white, false if black
     * @return whether the king of the given colour has moved
     */
    boolean getKingMovement(boolean white);

    /**
     * Sets whether a king of the given colour has moved.
     * @param white the colour of the king, true if white, false if black
     */
    void setKingMovement(boolean white, boolean moved);
    /**
     * Return whether the rook of the given colour and location has moved.
     * @param white the colour of the rook, true if white, false if black
     * @param queen the side of the rook, true if queenside, false if kingside
     * @return whether the king of the given colour has moved
     */
    boolean getRookMovement(boolean white, boolean queen);

    /**
     * Set whether the rook of the given colour and location has moved.
     * @param white the colour of the rook, true if white, false if black
     * @param queen the side of the rook, true if queenside, false if kingside
     */
    void setRookMovement(boolean white, boolean queen, boolean moved);
}
