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
        int xDiff = Math.abs(origin.getX() - target.getX());
        int yDiff = origin.getY() - target.getY();
        return ((board.at(origin).getColour().equals("white") && yDiff == -1)
                || (board.at(origin).getColour().equals("black") && yDiff == 1))
                    &&
                (xDiff == 0)
                && super.tryMove(origin, target, board);
    }
}
