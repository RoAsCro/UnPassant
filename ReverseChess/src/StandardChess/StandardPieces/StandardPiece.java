package StandardChess.StandardPieces;

import StandardChess.ChessBoard;
import StandardChess.Coordinate;
import StandardChess.Piece;

/**
 * An implementation of Piece that uses PieceStrategies to determine its behaviour.
 * @author Roland Crompton
 */
public class StandardPiece implements Piece {
    /**Stores the PieceStrategy*/
    private final PieceStrategy strategy;
    /**Stores the Piece's colour as a String*/
    private final String colour;

    /**
     * Constructs a new Piece with the given colour and strategy.
     * @param colour a String representing the Piece's colour, "white" or "black"
     * @param strategy the Piece's Strategy that determines its behaviour
     */
    public StandardPiece(String colour, PieceStrategy strategy) {
        this.strategy = strategy;
        this.colour = colour;
    }

    /**
     * Returns the Piece's colour.
     * @return the Piece's colour, "white" or "black"
     */
    @Override
    public String getColour() {
        return this.colour;
    }

    /**
     * Returns the name of the PieceStrategy representing the Piece's type.
     * @return the Piece's type
     */
    @Override
    public String getType() {
        return this.strategy.getName();
    }

    /**
     * Tries a move on a ChessBoard, starting at the origin, ending at the target using the
     * tryMove() method of the PieceStrategy.
     * @param origin the start Coordinate
     * @param target the end Coordinate
     * @param board the board the move is attempting to be made on
     * @return whether the move can be made on the board
     */
    @Override
    public boolean tryMove(Coordinate origin, Coordinate target, ChessBoard board) {
        return this.strategy.tryMove(origin, target, board);
    }

    /**
     * Tries an un move on a ChessBoard, starting at the origin, ending at the target using the
     * tryUnMove() method of the PieceStrategy.
     * @param origin the start Coordinate
     * @param target the end Coordinate
     * @param board the board the move is attempting to be made on
     * @return whether the move can be made on the board
     */
    @Override
    public boolean tryUnMove(Coordinate origin, Coordinate target, ChessBoard board) {
        return this.strategy.tryUnMove(origin, target, board);
    }

    /**
     * Updates the ChessBoard according to the given move or un move using the
     * updateBoard() method of the PieceStrategy.
     * @param origin the start Coordinate
     * @param target the end Coordinate
     * @param board the board the move is attempting to be made on
     * @param unMove whether this is an un move
     */
    @Override
    public void updateBoard(Coordinate origin, Coordinate target, ChessBoard board, boolean unMove) {
        this.strategy.updateBoard(origin, target, board, unMove);
    }

    /**
     * Gets all possible moves one unit of movement away for the piece type from the given origin.
     * One unit of movement is the minimum distance a piece can travel using a given move. In most cases
     * this will be one square, but for example for knights this might be x + 2, y + 1.
     * Note this does not take into consideration any way the move may be illegal, e.g. if the move is out of bounds.
     * @param origin the start Coordinate of the moves
     * @return an array containing all possible Coordinates this piece can move to
     */
    @Override
    public Coordinate[] getMoves(Coordinate origin){
        return this.strategy.getMoves(origin, this.colour);
    }

    /**
     * Gets all possible un moves one unit of movement away for the piece type from the given origin.
     * One unit of movement is the minimum distance a piece can travel using a given move. In most cases
     * this will be one square, but for example for knights this might be x + 2, y + 1.
     * Note this does not take into consideration any way the move may be illegal, e.g. if the un move is out of bounds.
     * @param origin the start Coordinate of the moves
     * @return an array containing all possible Coordinates this piece can un move to
     */
    @Override
    public Coordinate[] getUnMoves(Coordinate origin){
        return this.strategy.getUnMoves(origin, this.colour);
    }

}
