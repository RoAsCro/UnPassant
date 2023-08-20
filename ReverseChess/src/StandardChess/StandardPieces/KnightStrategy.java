package StandardChess.StandardPieces;

import StandardChess.ChessBoard;
import StandardChess.Coordinate;
import StandardChess.Coordinates;

/**
 * An implementation of PieceStrategy for a bishop, describing its movement.
 * @author Roland Crompton
 */
public class KnightStrategy extends AbstractStrategy{

    /**
     * Constructs the PieceStrategy, setting its moves to the knight directions.
     */
    public KnightStrategy() {
        super("knight",
                Coordinates.KNIGHT_DIRECTIONS);
    }

    /**
     * Overrides AbstractStrategy.tryMove(). Checks first that the target is (x+2, y+1) or (x+1, y+2) from the origin,
     * then functions identically to the method it overrides.
     * @param origin the start Coordinate
     * @param target the end Coordinate
     * @param board the board the move is attempting to be made on
     * @return whether the move can be made on the board
     */
    @Override
    public boolean tryMove(Coordinate origin, Coordinate target, ChessBoard board) {
        int xDiff = Math.abs(origin.getX() - target.getX());
        int yDiff = Math.abs(origin.getY() - target.getY());

        return ((xDiff == 2 && yDiff == 1) || (xDiff == 1 && yDiff == 2))
                && super.tryMove(origin, target, board);
    }

    /**
     * Overrides AbstractStrategy.tryUnMove(). Checks first that the target is (x+2, y+1) or (x+1, y+2) from the origin,
     * then functions identically to the method it overrides.
     * @param origin the start Coordinate
     * @param target the end Coordinate
     * @param board the board the un move is attempting to be made on
     * @return whether the un move can be made on the board
     */
    @Override
    public boolean tryUnMove(Coordinate origin, Coordinate target, ChessBoard board) {
        int xDiff = Math.abs(origin.getX() - target.getX());
        int yDiff = Math.abs(origin.getY() - target.getY());

        return ((xDiff == 2 && yDiff == 1) || (xDiff == 1 && yDiff == 2))
                && super.tryUnMove(origin, target, board);
    }
}
