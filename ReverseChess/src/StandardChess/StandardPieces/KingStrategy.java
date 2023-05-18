package StandardChess.StandardPieces;

import StandardChess.ChessBoard;
import StandardChess.Coordinate;
import StandardChess.Coordinates;
import StandardChess.Piece;

public class KingStrategy extends AbstractStrategy{

    private static final Coordinate[] WHITE_COORDS = new Coordinate[]
            {Coordinates.WHITE_KING, Coordinates.WHITE_KING_ROOK, Coordinates.WHITE_QUEEN_ROOK};
    private static final Coordinate[] BLACK_COORDS = new Coordinate[]
            {Coordinates.BLACK_KING, Coordinates.BLACK_KING_ROOK, Coordinates.BLACK_QUEEN_ROOK};

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
        return ((normalMove(origin, target, yDiff) && super.tryUnMove(origin, target, board))
                || unCastleCheck(origin, target, board));
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

    private boolean unCastleCheck(Coordinate origin,Coordinate target, ChessBoard board) {
        String colour = board.at(origin).getColour();
        Coordinate[] coordinates = colour.equals("white")
                ? WHITE_COORDS
                : BLACK_COORDS;

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
        return board.at(
                        new Coordinate(origin.getX() - originTargetDiff, origin.getY()))
                .getType().equals("null")
                &&
                (origin.getX() - originTargetDiff * 2 > 7
                        || board.at(new Coordinate(origin.getX() - originTargetDiff * 2, origin.getY()))
                        .getType().equals("null"));

    }
}
