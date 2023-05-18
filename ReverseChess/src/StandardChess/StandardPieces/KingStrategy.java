package StandardChess.StandardPieces;

import StandardChess.ChessBoard;
import StandardChess.Coordinate;

public class KingStrategy extends AbstractStrategy{
    public KingStrategy() {
        super("king");
    }

    public boolean normalMove(Coordinate origin, Coordinate target, int yDiff) {
        return (DIAGONAL.test(origin, target) || PERPENDICULAR.test(origin, target))
                && Math.abs(origin.getX() - target.getX()) <= 1
                && yDiff <= 1;
    }

    @Override
    public boolean tryMove(Coordinate origin, Coordinate target, ChessBoard board) {
        int yDiff = Math.abs(origin.getY() - target.getY());
        return ((normalMove(origin, target, yDiff))
                ||
                (yDiff == 0
                && castleCheck(origin, target, board)))
                && super.tryMove(origin, target, board);
    }
    @Override
    public boolean tryUnMove(Coordinate origin, Coordinate target, ChessBoard board) {
        int yDiff = Math.abs(origin.getY() - target.getY());
        return (((DIAGONAL.test(origin, target) || PERPENDICULAR.test(origin, target))
                && Math.abs(origin.getX() - target.getX()) <= 1
                && yDiff <= 1)
                ||
                (yDiff == 0
                        && castleCheck(origin, target, board)))
                && super.tryUnMove(origin, target, board);
    }

    @Override
    public Coordinate[] getMoves(Coordinate origin) {
        return new Coordinate[0];
    }

    private boolean castleCheck(Coordinate origin, Coordinate target, ChessBoard board) {
        String colour = board.at(origin).getColour();
        int xDiff = origin.getX() - target.getX();
        if (colour.equals("white")) {
            if (xDiff == 2) {
                return board.canCastleWhiteQueen();
            }
            if (xDiff == -2) {
                return board.canCastleWhiteKing();
            }
        }
        if (colour.equals("black")) {
            if (xDiff == 2) {
                return board.canCastleBlackQueen();
            }
            if (xDiff == -2) {
                return board.canCastleBlackKing();
            }
        }
        return false;

    }
}
