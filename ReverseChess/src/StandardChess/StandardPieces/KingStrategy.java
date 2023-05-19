package StandardChess.StandardPieces;

import StandardChess.*;

public class KingStrategy extends AbstractStrategy{

    private static final Coordinate[] WHITE_COORDS = new Coordinate[]
            {Coordinates.WHITE_KING, Coordinates.WHITE_KING_ROOK, Coordinates.WHITE_QUEEN_ROOK};
    private static final Coordinate[] BLACK_COORDS = new Coordinate[]
            {Coordinates.BLACK_KING, Coordinates.BLACK_KING_ROOK, Coordinates.BLACK_QUEEN_ROOK};

    public KingStrategy() {
        super("king");
    }

    private Coordinate[] getCoordinateSet(String colour) {
        return colour.equals("white")
                ? WHITE_COORDS
                : BLACK_COORDS;
    }

    private boolean normalMove(Coordinate origin, Coordinate target, int yDiff) {
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
        return ((normalMove(origin, target, yDiff) && super.tryUnMove(origin, target, board))
                || unCastleCheck(origin, target, board));
    }

    @Override
    public void updateBoard(Coordinate origin, Coordinate target, ChessBoard board, boolean unMove) {
        int xDiff = origin.getX() - target.getX();
        Piece king = board.at(origin);
        if (unMove) {

        } else {
            board.setCastle("king", king.getColour(), false);
            board.setCastle("queen", king.getColour(), false);
            if (Math.abs(xDiff) == 2) {
                Coordinate rookLocation = new Coordinate(target.getX() - (xDiff < 0 ? xDiff / 2 : xDiff), target.getY());
                board.place(new Coordinate(origin.getX() - xDiff / 2, origin.getY()), board.at(rookLocation));
                board.remove(rookLocation);
            }
        }
    }

    @Override
    public Coordinate[] getMoves(Coordinate origin) {
        return new Coordinate[0];
    }

    private boolean castleCheck(Coordinate origin, Coordinate target, ChessBoard board) {
        String colour = board.at(origin).getColour();
        int xDiff = origin.getX() - target.getX();
        if (Math.abs(xDiff) != 2) {
            return false;
        }
        for (int i = target.getX() ; i < ChessBoard.LENGTH - 1 && i > 0 ; i -= xDiff / 2) {
            if (!board.at(new Coordinate(i, target.getY())).getType().equals("null")) {
                return false;
            }
        }
        String side = xDiff > 0 ? "queen" : "king";
        return board.canCastle(side, colour) && !castleCheckCheck(origin, -(xDiff/2), board);

    }

    private boolean unCastleCheck(Coordinate origin,Coordinate target, ChessBoard board) {
        String colour = board.at(origin).getColour();
        Coordinate[] coordinates = getCoordinateSet(colour);

        // Nothing at target location and target location is king's origin
        if (!(target.equals(coordinates[0]) && board.at(target).getType().equals("null"))) {
            return false;
        }
        // Starting location is valid
        if (!(origin.equals(new Coordinate(coordinates[1].getX() - 1, coordinates[1].getY()))
                || origin.equals(new Coordinate(coordinates[2].getX() + 2, coordinates[2].getY())))) {
            return false;
        }

        int originTargetDiff = ((target.getX() - origin.getX()) / 2);
        Piece rook = board.at(new Coordinate(
                target.getX() + originTargetDiff,
                        origin.getY()));

        // There is a rook of the correct colour between king and king's origin
        if (!(rook.getType().equals("rook")
                && rook.getColour().equals(colour))) {
            return false;
        }
        // There are no pieces blocking the castle
        if (!(board.at(
                        new Coordinate(origin.getX() - originTargetDiff, origin.getY()))
                .getType().equals("null")
                &&
                (origin.getX() - originTargetDiff * 2 > ChessBoard.LENGTH - 1
                        || board.at(new Coordinate(origin.getX() - originTargetDiff * 2, origin.getY()))
                        .getType().equals("null")))) {
            return false;

        }
        return !castleCheckCheck(origin, originTargetDiff, board);
    }

    /**
     * This should only be called if all other castling checks have been carried out
     * @param origin
     * @param direction
     * @param board
     * @return
     */
    private boolean castleCheckCheck(Coordinate origin, int direction, ChessBoard board) {
        Piece king = board.at(origin);
        board.remove(origin);
        Coordinate checkedSquare = new Coordinate(origin.getX() + direction, origin.getY());
        Piece rook = board.at(checkedSquare);
        board.place(checkedSquare, king);
        boolean inCheck = board.getReader().inCheck(checkedSquare);
        board.place(origin, king);
        board.place(checkedSquare, rook);
        return inCheck;
    }
}
