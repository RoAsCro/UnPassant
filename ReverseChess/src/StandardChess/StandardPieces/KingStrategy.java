package StandardChess.StandardPieces;

import StandardChess.Coordinate;

public class KingStrategy extends CollisableStrategy{
    public KingStrategy() {
        super("king", 1);
    }

    @Override
    public Coordinate[] getMoves(Coordinate origin) {
        return new Coordinate[0];
    }
}
