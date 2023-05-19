package StandardChess.StandardPieces;

import StandardChess.ChessBoard;
import StandardChess.Coordinate;
import StandardChess.Piece;

public class PawnStrategy extends AbstractStrategy {

    public PawnStrategy() {
        super("pawn");
    }

    @Override
    public Coordinate[] getMoves(Coordinate origin) {
        return new Coordinate[0];
    }


    private boolean tryMoveGeneral(Coordinate origin, Coordinate target, ChessBoard board, int unMove) {
        int xDiff = Math.abs(origin.getX() - target.getX());

        int yDiff = origin.getY() - target.getY();
        Piece me = board.at(origin);
        String colour = me.getColour();
        boolean noXChange = xCheck(xDiff, 0,
                target, false,
                board, colour);
        return (yCheck(colour, yDiff, unMove)
                && (
                // Normal move
                noXChange
                        ||
                        // Capture
                        (xCheck(xDiff, 1,
                                target, true,
                                board, colour))
                        ||
                        // En Passant
                        (xCheck(xDiff, 1,
                                target, false,
                                board, colour)
                                && enPassantCheck(yDiff, target, colour, board))
        )
        )
                ||
                // Double move
                (yCheck(colour, yDiff, 2 * unMove)
                        && noXChange && doubleMoveCheck(origin, colour))
                        && super.tryMove(origin, target, board);
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
                        // Normal move
                        noXChange
                        ||
                        // Capture
                        (xCheck(xDiff, 1,
                                target, true,
                                board, colour))
                        ||
                        // En Passant
                        (xCheck(xDiff, 1,
                                target, false,
                                board, colour)
                        && enPassantCheck(yDiff, target, colour, board))
                    )
                )
                ||
                // Double move
                (yCheck(colour, yDiff, 2)
                && noXChange && doubleMoveCheck(origin, colour))
                && super.tryMove(origin, target, board);
    }
    @Override
    public boolean tryUnMove(Coordinate origin, Coordinate target, ChessBoard board) {
        int xDiff = Math.abs(origin.getX() - target.getX());

        int yDiff = origin.getY() - target.getY();
        Piece me = board.at(origin);
        String colour = me.getColour();
        boolean noXChange = xCheck(xDiff, 0,
                target, false,
                board, colour);
        return true;
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
        return NOT_NULL.test(captureTarget)
                && !captureTarget.getColour().equals(me.getColour());
    }

    private boolean xCheck(int xDiff, int requiredDiff,
                           Coordinate target, boolean notNull,
                           ChessBoard board, String notColour) {
        return xDiff == requiredDiff
                && (NOT_NULL.test(board.at(target)) == notNull)
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

    private boolean doubleMoveCheck(Coordinate origin, String colour) {
        return (origin.getY() == 1 && colour.equals("white"))
                || (origin.getY() == 6 && colour.equals("black"));
    }

    //TODO promotion
}
