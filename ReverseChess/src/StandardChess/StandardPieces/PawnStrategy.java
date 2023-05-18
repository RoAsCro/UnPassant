package StandardChess.StandardPieces;

import StandardChess.ChessBoard;
import StandardChess.Coordinate;
import StandardChess.Piece;

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
        Piece me = board.at(origin);
        return yCheck(me, yDiff)
                    &&
                ((xDiff == 0 && board.at(target).getType().equals("null"))
                || (xDiff == 1
                && captureCheck(target, board, me)))
                && super.tryMove(origin, target, board);
    }

    private boolean yCheck(Piece me, int yDiff) {
        return (me.getColour().equals("white") && yDiff == -1)
                || (me.getColour().equals("black") && yDiff == 1);
    }

    private boolean captureCheck(Coordinate target,ChessBoard board, Piece me) {
        Piece captureTarget = board.at(target);
        return !captureTarget.getType().equals("null")
                && !captureTarget.getColour().equals(me.getColour());
    }

}
