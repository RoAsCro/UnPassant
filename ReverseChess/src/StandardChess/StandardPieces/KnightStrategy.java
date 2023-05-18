package StandardChess.StandardPieces;

import StandardChess.ChessBoard;
import StandardChess.Coordinate;

public class KnightStrategy extends AbstractStrategy{

    public KnightStrategy() {
        super("knight", 1);
    }

    @Override
    public boolean tryMove(Coordinate origin, Coordinate target, ChessBoard board) {
        int xDiff = Math.abs(origin.getX() - target.getX());
        int yDiff = Math.abs(origin.getY() - target.getY());

        return ((xDiff == 2 && yDiff == 1) || (xDiff == 1 && yDiff == 2))
                && super.tryMove(origin, target, board);
    }

    @Override
    public Coordinate[] getMoves(Coordinate origin) {
        return new Coordinate[0];
    }
}
