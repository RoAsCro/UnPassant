package StandardChess.StandardPieces;

import StandardChess.ChessBoard;
import StandardChess.Coordinate;
import StandardChess.Coordinates;

public class RookStrategy extends AbstractStrategy{
    public RookStrategy() {
        super("rook",
                new Coordinate[]{
                        Coordinates.UP,
                        Coordinates.RIGHT,
                        Coordinates.DOWN,
                        Coordinates.LEFT});
    }

    @Override
    public boolean tryMove(Coordinate origin, Coordinate target, ChessBoard board) {
        return PERPENDICULAR.test(origin, target)
                && super.tryMove(origin, target, board);
    }

    @Override
    public boolean tryUnMove(Coordinate origin, Coordinate target, ChessBoard board) {
        return PERPENDICULAR.test(origin, target)
                && super.tryUnMove(origin, target, board);
    }
}
