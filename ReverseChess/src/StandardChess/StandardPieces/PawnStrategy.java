package StandardChess.StandardPieces;

import StandardChess.Coordinate;

public class PawnStrategy extends CollisableStrategy {
    public PawnStrategy() {
        super("pawn", 1);
    }

    @Override
    public Coordinate[] getMoves(Coordinate origin) {
        return new Coordinate[0];
    }
}
