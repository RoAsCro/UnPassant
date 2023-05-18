package StandardChess.StandardPieces;

import StandardChess.ChessBoard;
import StandardChess.Coordinate;

public class PawnStrategy extends CollisableStrategy {
    public PawnStrategy() {
        super("pawn", 1);
    }

    @Override
    public Coordinate[] getMoves(Coordinate origin) {
        return new Coordinate[0];
    }

    @Override
    public boolean tryMove(Coordinate origin, Coordinate target, ChessBoard board) {
        return super.tryMove(origin, target, board);
    }
}
