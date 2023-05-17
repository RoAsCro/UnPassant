package StandardChess.StandardPieces;

import StandardChess.Coordinate;

public class RookStrategy extends CollisableStrategy{
    public RookStrategy() {
        super("rook", BOARD_LENGTH);
    }

    @Override
    public Coordinate[] getMoves(Coordinate origin) {
        return new Coordinate[0];
    }
}
