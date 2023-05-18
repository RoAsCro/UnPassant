package StandardChess.StandardPieces;

import StandardChess.ChessBoard;
import StandardChess.Coordinate;

public class RookStrategy extends CollisableStrategy{
    public RookStrategy() {
        super("rook", BOARD_LENGTH);
    }

    @Override
    public boolean tryMove(Coordinate origin, Coordinate target, ChessBoard board) {
        return PERPENDICULAR.test(origin, target)
                && super.tryMove(origin, target, board);
    }

    @Override
    public Coordinate[] getMoves(Coordinate origin) {
        return new Coordinate[0];
    }
}
