package StandardChess.StandardPieces;

import StandardChess.Coordinate;

public class QueenStrategy extends CollisableStrategy{
    public QueenStrategy() {
        super("queen", BOARD_LENGTH);
    }

    @Override
    public boolean tryMove(Coordinate origin, Coordinate target) {
        return DIAGONAL.test(origin, target) || PERPENDICULAR.test(origin, target);
    }

    @Override
    public Coordinate[] getMoves(Coordinate origin) {
        return new Coordinate[0];
    }
}
