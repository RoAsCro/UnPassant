package StandardChess.StandardPieces;

import StandardChess.ChessBoard;
import StandardChess.Coordinate;
import StandardChess.Coordinates;

public class KnightStrategy extends AbstractStrategy{

    public KnightStrategy() {
        super("knight",
                Coordinates.KNIGHT_DIRECTIONS);
    }

    @Override
    public boolean tryMove(Coordinate origin, Coordinate target, ChessBoard board) {
        int xDiff = Math.abs(origin.getX() - target.getX());
        int yDiff = Math.abs(origin.getY() - target.getY());

        return ((xDiff == 2 && yDiff == 1) || (xDiff == 1 && yDiff == 2))
                && super.tryMove(origin, target, board);
    }

    @Override
    public boolean tryUnMove(Coordinate origin, Coordinate target, ChessBoard board) {
        int xDiff = Math.abs(origin.getX() - target.getX());
        int yDiff = Math.abs(origin.getY() - target.getY());

        return ((xDiff == 2 && yDiff == 1) || (xDiff == 1 && yDiff == 2))
                && super.tryUnMove(origin, target, board);
    }
}
