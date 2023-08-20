package StandardChess;

/**
 * A singleton null piece to use as a placeholder for Pieces when a reference to a Piece is needed
 * but a Piece may be absent, such as a blank space on a ChessBoard.
 * @author Roland Crompton
 */
public class NullPiece implements Piece{
    /**The current instance of NullPiece*/
    private static Piece instance;

    /**
     * A private constructor ensuring NullPiece is a singleton.
     */
    private NullPiece(){}

    /**
     * Returns the instance of NullPiece. If none exists, one is made.
     * @return an instance of NullPiece
     */
    public static Piece getInstance() {
        return instance != null
                ? instance
                : (instance = new NullPiece());
    }

    /**
     * Returns a String reading "null".
     * @return a String reading "null"
     */
    @Override
    public String getColour() {
        return "null";
    }

    /**
     * Returns a String reading "null".
     * @return a String reading "null"
     */
    @Override
    public String getType() {
        return "null";
    }

    /**
     * Return false regardless fo input.
     * @param origin the start Coordinate
     * @param target the end Coordinate
     * @param board the board the move is attempting to be made on
     * @return false
     */
    @Override
    public boolean tryMove(Coordinate origin, Coordinate target, ChessBoard board) {
        return false;
    }

    /**
     * Return false regardless fo input.
     * @param origin the start Coordinate
     * @param target the end Coordinate
     * @param board the board the unn move is attempting to be made on
     * @return false
     */
    @Override
    public boolean tryUnMove(Coordinate origin, Coordinate target, ChessBoard board) {
        return false;
    }

    /**
     * Does nothing.
     * @param origin the start Coordinate
     * @param target the end Coordinate
     * @param board the board the move is attempting to be made on
     * @param unMove whether this is an un move
     */
    @Override
    public void updateBoard(Coordinate origin, Coordinate target, ChessBoard board, boolean unMove) {}

    /**
     * Returns an empty array.
     * @param origin the start Coordinate of the moves
     * @return an empty array
     */
    @Override
    public Coordinate[] getMoves(Coordinate origin) {
        return new Coordinate[0];
    }

    /**
     * Returns an empty array.
     * @param origin the start Coordinate of the moves
     * @return an empty array
     */
    @Override
    public Coordinate[] getUnMoves(Coordinate origin) {
        return new Coordinate[0];
    }
}
