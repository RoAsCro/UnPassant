package StandardChess.StandardPieces;

import StandardChess.Coordinate;

public class KnightStrategy extends AbstractStrategy{

    public KnightStrategy() {
        super("knight", 1);
    }

    @Override
    public boolean tryMove(Coordinate origin, Coordinate target) {
        return false;
    }

    @Override
    public Coordinate[] getMoves(Coordinate origin) {
        return new Coordinate[0];
    }
}
