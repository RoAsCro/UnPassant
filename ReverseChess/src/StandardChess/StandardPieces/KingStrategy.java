package StandardChess.StandardPieces;

import StandardChess.ChessBoard;
import StandardChess.Coordinate;

public class KingStrategy extends CollisableStrategy{
    public KingStrategy() {
        super("king", 1);
    }

    @Override
    public boolean tryMove(Coordinate origin, Coordinate target, ChessBoard board) {
        return (DIAGONAL.test(origin, target) || PERPENDICULAR.test(origin, target))
                && Math.abs(origin.getX() - target.getX()) <= 1
                && Math.abs(origin.getY() - target.getY()) <= 1
                && super.tryMove(origin, target, board);
    }

    @Override
    public Coordinate[] getMoves(Coordinate origin) {
        return new Coordinate[0];
    }
}
