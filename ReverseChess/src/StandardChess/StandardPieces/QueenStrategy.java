package StandardChess.StandardPieces;

import StandardChess.ChessBoard;
import StandardChess.Coordinate;

public class QueenStrategy extends CollisableStrategy{
    public QueenStrategy() {
        super("queen", BOARD_LENGTH);
    }

    @Override
    public boolean tryMove(Coordinate origin, Coordinate target, ChessBoard board) {
        return DIAGONAL.test(origin, target) || PERPENDICULAR.test(origin, target)
                && super.tryMove(origin, target, board);
    }

    @Override
    public Coordinate[] getMoves(Coordinate origin) {
        return new Coordinate[0];
    }
}