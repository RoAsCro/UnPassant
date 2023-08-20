package StandardChess;

/**
 * An interface for a class representing a piece on a chess board.
 */
public interface Piece {

    /**
     * Returns the piece's colour as a String, "white" or "black".
     * @return the piece's colour as a String, "white" or "black"
     */
    String getColour();

    /**
     * Returns the piece's type. This will be all lowercase.
     * @return the piece's type
     */
    String getType();

    /**
     * Tries a move on a ChessBoard, starting at the origin, ending at the target.
     * @param origin the start Coordinate
     * @param target the end Coordinate
     * @param board the board the move is attempting to be made on
     * @return whether the move can be made on the board
     */
    boolean tryMove(Coordinate origin, Coordinate target, ChessBoard board);

    /**
     * Tries an un move on a ChessBoard, starting at the origin, ending at the target.
     * @param origin the start Coordinate
     * @param target the end Coordinate
     * @param board the board the move is attempting to be made on
     * @return whether the move can be made on the board
     */
    boolean tryUnMove(Coordinate origin, Coordinate target, ChessBoard board);

    /**
     * Updates the ChessBoard according to the given move or un move.
     * @param origin the start Coordinate
     * @param target the end Coordinate
     * @param board the board the move is attempting to be made on
     * @param unMove whether this is an un move
     */
    void updateBoard(Coordinate origin, Coordinate target, ChessBoard board, boolean unMove);

    /**
     * Gets all possible moves one unit of movement away for the piece type from the given origin.
     * One unit of movement is the minimum distance a piece can travel using a given move. In most cases
     * this will be one square, but for example for knights this might be x + 2, y + 1.
     * Note this does not take into consideration any way the move may be illegal, e.g. if the move is out of bounds.
     * @param origin the start Coordinate of the moves
     * @return an array containing all possible Coordinates this piece can move to
     */
    Coordinate[] getMoves(Coordinate origin);

    /**
     * Gets all possible un moves one unit of movement away for the piece type from the given origin.
     * One unit of movement is the minimum distance a piece can travel using a given move. In most cases
     * this will be one square, but for example for knights this might be x + 2, y + 1.
     * Note this does not take into consideration any way the move may be illegal, e.g. if the un move is out of bounds.
     * @param origin the start Coordinate of the moves
     * @return an array containing all possible Coordinates this piece can un move to
     */
    Coordinate[] getUnMoves(Coordinate origin);



}
