package StandardChess.StandardPieces;

import StandardChess.Board;
import StandardChess.ChessBoard;
import StandardChess.Coordinate;
import StandardChess.Piece;

import java.util.function.BiPredicate;

public class PawnStrategy extends AbstractStrategy {

    public PawnStrategy() {
        super("pawn");
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
        String colour = me.getColour();
        boolean noXChange = xCheck(xDiff, 0,
                target, false,
                board, colour);
        return (yCheck(colour, yDiff, 1)
                    && (
                        noXChange
                        ||
                        (xCheck(xDiff, 1,
                                target, true,
                                board, colour))
                        ||
                        (xCheck(xDiff, 1,
                                target, false,
                                board, colour)
                        && enPassantCheck(yDiff, target, colour, board))
                    )
                )
                ||
                (yCheck(colour, yDiff, 2)
                && noXChange)
                && super.tryMove(origin, target, board);
    }

    private boolean yCheck(String colour, int yDiff, int targetY) {
        int modifier = 1;
        if (colour.equals("white")) {
            modifier = -1;
        } else if (colour.equals("black")) {
            modifier = 1;
        }
        return yDiff == targetY * modifier;
    }

    private boolean captureCheck(Coordinate target,ChessBoard board, Piece me) {
        Piece captureTarget = board.at(target);
        return !captureTarget.getType().equals("null")
                && !captureTarget.getColour().equals(me.getColour());
    }

    private boolean xCheck(int xDiff, int requiredDiff,
                           Coordinate target, boolean notNull,
                           ChessBoard board, String notColour) {
        return xDiff == requiredDiff
                && (!board.at(target).getType().equals("null") == notNull)
                && (!board.at(target).getColour().equals(notColour));
    }

    private boolean enPassantCheck(int yDiff, Coordinate target, String colour, ChessBoard board) {
        Coordinate ePTarget = new Coordinate(target.getX(), target.getY() + yDiff);
        Piece targetPiece = board.at(ePTarget);
        return board.getEnPassant().equals(target)
                &&
                targetPiece.getType().equals("pawn")
                &&
                !targetPiece.getColour().equals(colour);
    }


}
