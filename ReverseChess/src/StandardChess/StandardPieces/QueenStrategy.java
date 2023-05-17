package StandardChess.StandardPieces;

import StandardChess.Coordinate;

public class QueenStrategy extends CollisableStrategy{
    public QueenStrategy() {
        super("queen", BOARD_LENGTH);
    }

    @Override
    public Coordinate[] getMoves(Coordinate origin) {
        return new Coordinate[0];
    }
}
