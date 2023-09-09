package ChessHeuristics.Heuristics.Detector.Data;

import ChessHeuristics.Heuristics.Path;
import ReverseChess.StandardChess.Coordinate;

import java.util.Map;
import java.util.TreeMap;

/**
 * An implementation of PieceData.
 * @author Roland Crompton
 */
public class StandardPieceData implements PieceData {
    /**The location of black in the stored arrays and Lists*/
    private static final int BLACK = 1;
    /**The location of white in the stored arrays and Lists*/
    private static final int WHITE = 0;
    /**The location of the king in the kingRookMovement array*/
    private static final int KING = 0;
    /**The location of the kingside rook in the kingRookMovement array*/
    private static final int K_ROOK = 2;
    /**The location of the queenside rook in the kingRookMovement array*/
    private static final int Q_ROOK = 1;

    /**An array of arrays storing whether kings and rooks have moved*/
    private final boolean[][] kingRookMovement = new boolean[][]{{false, false, false}, {false, false, false}};

    /**A List of Maps of Coordinates of non-pawn origins and a Boolean of whether they are caged for each player*/
    private final Map<Coordinate, Boolean> caged = new TreeMap<>();
    /**a Map of piece origins, pieces that may have come from them, and Paths from that origin to that piece*/
    private final Map<Coordinate, Map<Coordinate, Path>> piecePaths = new TreeMap<>();


    /**
     * Return whether the king of the given colour has moved.
     * @param white the colour of the king, true if white, false if black
     * @return whether the king of the given colour has moved
     */
    @Override
    public boolean getKingMovement(boolean white) {
        return kingRookMovement[white ? WHITE : BLACK][KING];
    }
    /**
     * Sets whether a king of the given colour has moved.
     * @param white the colour of the king, true if white, false if black
     */
    @Override
    public void setKingMovement(boolean white, boolean moved) {
        this.kingRookMovement[white ? WHITE : BLACK][KING] = moved;
    }
    /**
     * Return whether the rook of the given colour and location has moved.
     * @param white the colour of the rook, true if white, false if black
     * @param queen the side of the rook, true if queenside, false if kingside
     * @return whether the king of the given colour has moved
     */
    @Override
    public boolean getRookMovement(boolean white, boolean queen) {
        return kingRookMovement[white ? WHITE : BLACK][queen ? Q_ROOK : K_ROOK];
    }
    /**
     * Set whether the rook of the given colour and location has moved.
     * @param white the colour of the rook, true if white, false if black
     * @param queen the side of the rook, true if queenside, false if kingside
     */
    @Override
    public void setRookMovement(boolean white, boolean queen, boolean moved) {
        this.kingRookMovement[white ? WHITE : BLACK][queen ? Q_ROOK : K_ROOK] = moved;
    }
    /**
     * Retrieves a Map of Coordinates of non-pawn origins and a Boolean of whether they are caged.
     * @return a Map of Coordinates of non-pawn origins and a Boolean of whether they are caged
     */
    @Override
    public Map<Coordinate, Boolean> getCaged() {
        return this.caged;
    }
    /**
     * Retrieves a Map of the Coordinates of all non-pawn piece origins besides that of knights, and a Map of
     * Coordinates of non-pawn pieces of the corresponding type that can be pathed both to and from that origin,
     * and a potential Path from the origin to that piece
     * @return a Map of piece origins, pieces that may have come from them, and Paths from that origin to that piece
     */
    @Override
    public Map<Coordinate, Map<Coordinate, Path>> getPiecePaths() {
        return this.piecePaths;
    }
}
