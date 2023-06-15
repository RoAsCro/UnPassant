package StandardChess.StandardPieces;

import StandardChess.ChessBoard;
import StandardChess.Coordinate;
import StandardChess.Coordinates;
import StandardChess.Piece;

public class PawnStrategy extends AbstractStrategy {

    public PawnStrategy() {
        super("pawn",
                new Coordinate[]{
                        Coordinates.UP,
                        Coordinates.RIGHT,
                        Coordinates.DOWN,
                        Coordinates.LEFT,
                        Coordinates.UP_RIGHT,
                        Coordinates.DOWN_RIGHT,
                        Coordinates.DOWN_LEFT,
                        Coordinates.UP_LEFT});
    }

    @Override
    public Coordinate[] getMoves(Coordinate origin, String colour) {
        return colour.equals("white")
                ? new Coordinate[] {
                        Coordinates.add(origin, Coordinates.UP), Coordinates.add(origin, Coordinates.UP_RIGHT),
                Coordinates.add(origin, Coordinates.UP_LEFT)}
                : new Coordinate[] {
                Coordinates.add(origin, Coordinates.DOWN), Coordinates.add(origin, Coordinates.DOWN_RIGHT),
                Coordinates.add(origin, Coordinates.DOWN_LEFT)
        };
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
                                target, unMove == 1,
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
                (noXChange && doubleMoveCheck(origin, colour, yDiff, unMove));
    }

    @Override
    public boolean tryMove(Coordinate origin, Coordinate target, ChessBoard board) {
        return tryMoveGeneral(origin, target, board, 1)
                && super.tryMove(origin, target, board);
    }
    @Override
    public boolean tryUnMove(Coordinate origin, Coordinate target, ChessBoard board) {
        return target.getY() != ChessBoard.LENGTH - 1 && target.getY() != 0
                && tryMoveGeneral(origin, target, board, -1)
                && super.tryUnMove(origin, target, board);
    }

    @Override
    public void updateBoard(Coordinate origin, Coordinate target, ChessBoard board, boolean unMove) {
        int xDiff = Math.abs(origin.getX() - target.getX());
        int yDiff = origin.getY() - target.getY();
        String colour = board.at(origin).getColour();
        if (unMove) {

        } else {
            if (doubleMoveCheck(origin, colour, yDiff, 1)) {
                board.setEnPassant(new Coordinate(origin.getX(), origin.getY() - yDiff / 2));
            } else if (xCheck(xDiff, 1,
                    target, false,
                    board, colour)
                    && enPassantCheck(yDiff, target, colour, board)) {
                board.remove(new Coordinate(target.getX(), target.getY()+ yDiff));
            }
        }
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

    @Deprecated
    private boolean unPassantCheck(int yDiff, Coordinate origin, ChessBoard board) {
        Coordinate ePTarget = new Coordinate(origin.getX(), origin.getY() + yDiff);
        return board.at(ePTarget).getType().equals("null");
    }

    private boolean doubleMoveCheck(Coordinate origin, String colour, int yDiff, int unMove) {
        if (!yCheck(colour, yDiff, 2 * unMove)) {
            return false;
        }

        return (origin.getY() == 2 + yDiff / 2 && colour.equals("white"))
                || (origin.getY() == 5 + yDiff / 2 && colour.equals("black"));
    }

    //TODO promotion
    //TODO can't unmove to back rank
}
