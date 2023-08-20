package StandardChess.StandardPieces;

import StandardChess.ChessBoard;
import StandardChess.Coordinate;
import StandardChess.Coordinates;

/**
 * An implementation of PieceStrategy for a bishop, describing its diagonal movement.
 */
public class BishopStrategy extends AbstractStrategy{

    /**
     * Constructs the PieceStrategy, setting its moves to diagonals.
     */
    public BishopStrategy() {
        super("bishop", new Coordinate[]{
                Coordinates.UP_LEFT,
                Coordinates.UP_RIGHT,
                Coordinates.DOWN_RIGHT,
                Coordinates.DOWN_LEFT
        });
    }

    /**
     * Overrides AbstractStrategy.tryMove(). Checks first that the target is diagonal to the origin, then functions
     * identically to the method it overrides.
     * @param origin the start Coordinate
     * @param target the end Coordinate
     * @param board the board the move is attempting to be made on
     * @return whether the move can be made on the board
     */
    @Override
    public boolean tryMove(Coordinate origin, Coordinate target, ChessBoard board) {
        return DIAGONAL.test(origin, target) && super.tryMove(origin, target, board);
    }

    /**
     * Overrides AbstractStrategy.tryUnMove(). Checks first that the target is diagonal to the origin, then functions
     * identically to the method it overrides.
     * @param origin the start Coordinate
     * @param target the end Coordinate
     * @param board the board the un move is attempting to be made on
     * @return whether the un move can be made on the board
     */
    @Override
    public boolean tryUnMove(Coordinate origin, Coordinate target, ChessBoard board) {
        return DIAGONAL.test(origin, target) && super.tryUnMove(origin, target, board);
    }

}
