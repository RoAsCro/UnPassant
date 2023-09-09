package ReverseChess.StandardChess;

/**
 * An interface for ChessBoards providing methods for setting and getting data
 * about the chess board it represents.
 * @author Roland Crompton
 */
public interface ChessBoard {
    /**The number of squares long a chess board is*/
    int LENGTH = 8;

    /**
     * Returns the Piece at the given Coordinate. If there is no piece at that location,
     * returns an instance of NullPiece.
     * @param coordinate the Coordinate of the Piece
     * @return the Piece at the Coordinate
     */
    Piece at(Coordinate coordinate);

    /**
     * Returns the player whose turn it is as a String, "white" or "black"
     * @return the player whose turn it is
     */
    String getTurn();

    /**
     * Sets the player whose turn it is as a String, "white" or "black"
     * @param turn the player whose turn it is
     */
    void setTurn(String turn);

    /**
     * Returns the BoardReader for the ChessBoard.
     * @return this ChessBoard's BoardReader
     */
    BoardReader getReader();

    /**
     * Places the given Piece at the given Coordinate on the Board.
     * @param coordinate the Coordinate the Piece will be placed at
     * @param piece the Piece to place at the Coordinate
     */
    void place(Coordinate coordinate, Piece piece);

    /**
     * Removes the Piece currently at the given location.
     * @param coordinate the Coordinate of the Piece to remove
     */
    void remove(Coordinate coordinate);

    /**
     * Returns whether the king of the given colour can castle on the given side.
     * The colour should be "white" or "black", and the piece side should be "queen"
     * or "king".
     * <p></p>
     * In reverse play, this represents whether the king MUST castle on the given side.
     * @param pieceSide the castle side, "queen" or "king"
     * @param colour the colour of the king "white" or "black"
     * @return whether the king of the given colour can castle on the given side
     */
    boolean canCastle(String pieceSide, String colour);

    /**
     * Sets whether the king of the given colour can castle on the given side.
     * The colour should be "white" or "black", and the piece side should be "queen"
     * or "king".
     *  <p></p>
     *  In reverse play, this represents whether the king MUST castle on the given side.
     * @param pieceSide the castle side, "queen" or "king"
     * @param colour the colour of the king "white" or "black"
     * @param canCastle whether the side should be allowed to castle
     */
    void setCastle(String pieceSide, String colour, boolean canCastle);

    /**
     * In forward play, returns the location to which a pawn may move to capture en passant.
     * In reverse play, returns the location of the pawn which must move double move in reverse.
     * If this isn't set, returns a Null Coordinate.
     * @return the en passant Coordinate
     */
    Coordinate getEnPassant();

    /**
     * Sets the en passant Coordinate.
     * @param coordinate the Coordinate to set the en passant Coordinate to
     */
    void setEnPassant(Coordinate coordinate);

}
