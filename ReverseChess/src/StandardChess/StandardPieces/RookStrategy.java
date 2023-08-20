package StandardChess.StandardPieces;

import StandardChess.ChessBoard;
import StandardChess.Coordinate;
import StandardChess.Coordinates;

/**
 * An implementation of PieceStrategy for a bishop, describing its horizontal and vertical movement.
 */
public class RookStrategy extends AbstractStrategy{

    /**
     * Constructs the PieceStrategy, setting its moves to horizontal and vertical.
     */
    public RookStrategy() {
        super("rook",
                new Coordinate[]{
                        Coordinates.UP,
                        Coordinates.RIGHT,
                        Coordinates.DOWN,
                        Coordinates.LEFT});
    }

    /**
     * Overrides AbstractStrategy.tryMove(). Checks first that the target is perpendicular to the origin, then functions
     * identically to the method it overrides.
     * @param origin the start Coordinate
     * @param target the end Coordinate
     * @param board the board the move is attempting to be made on
     * @return whether the move can be made on the board
     */
    @Override
    public boolean tryMove(Coordinate origin, Coordinate target, ChessBoard board) {
        return PERPENDICULAR.test(origin, target)
                && super.tryMove(origin, target, board);
    }

    /**
     * Overrides AbstractStrategy.tryUnMove(). Checks first that the target is perpendicular to the origin,
     * then functions identically to the method it overrides.
     * @param origin the start Coordinate
     * @param target the end Coordinate
     * @param board the board the un move is attempting to be made on
     * @return whether the un move can be made on the board
     */
    @Override
    public boolean tryUnMove(Coordinate origin, Coordinate target, ChessBoard board) {
        return PERPENDICULAR.test(origin, target)
                && super.tryUnMove(origin, target, board);
    }
}
