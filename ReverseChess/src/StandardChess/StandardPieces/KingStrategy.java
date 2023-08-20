package StandardChess.StandardPieces;

import StandardChess.ChessBoard;
import StandardChess.Coordinate;
import StandardChess.Coordinates;
import StandardChess.Piece;

/**
 * An implementation of PieceStrategy for a bishop, describing its movement, additional moves, and how it updates the
 * board after it moves.
 */
public class KingStrategy extends AbstractStrategy{
    /**The castle Coordinates if this king is black*/
    private static final Coordinate[] BLACK_COORDS = new Coordinate[]
            {Coordinates.BLACK_KING, Coordinates.BLACK_KING_ROOK, Coordinates.BLACK_QUEEN_ROOK};
    /**The castle Coordinates if this king is white*/
    private static final Coordinate[] WHITE_COORDS = new Coordinate[]
            {Coordinates.WHITE_KING, Coordinates.WHITE_KING_ROOK, Coordinates.WHITE_QUEEN_ROOK};

    /**
     * Constructs the PieceStrategy, setting its moves to vertical, horizontal, and diagonal.
     */
    public KingStrategy() {
        super("king",
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

    /**
     * Returns a set of castling Coordinates describing the default locations of the king and two rooks
     * depending on the colour String given.
     * @param colour the king's colour as a String, "white" or "black"
     * @return an array of this piece's castle Coordinates
     */
    private Coordinate[] getCoordinateSet(String colour) {
        return colour.equals("white")
                ? WHITE_COORDS
                : BLACK_COORDS;
    }

    /**
     * A helper method for tryMove() and tryUnMove, checking that the target is either both perpendicular or diagonal to
     * the origin and one square away.
     * @param origin the start Coordinate
     * @param target the end Coordinate
     * @param yDiff the y difference between the origin and target
     * @return whether the origin and target fulfil the criteria for a normal king move
     */
    private boolean normalMove(Coordinate origin, Coordinate target, int yDiff) {
        return (DIAGONAL.test(origin, target) || PERPENDICULAR.test(origin, target))
                && Math.abs(origin.getX() - target.getX()) <= 1
                && yDiff <= 1;
    }

    /**
     * Overrides AbstractStrategy.tryMove(). Checks first that the target is either both perpendicular or diagonal to
     * the origin and one square away, or is a valid castle. Then functions identically to the method it overrides.
     * @param origin the start Coordinate
     * @param target the end Coordinate
     * @param board the board the move is attempting to be made on
     * @return whether the move can be made on the board
     */
    @Override
    public boolean tryMove(Coordinate origin, Coordinate target, ChessBoard board) {
        int yDiff = Math.abs(origin.getY() - target.getY());
        return ((normalMove(origin, target, yDiff))
                ||
                (yDiff == 0
                && castleCheck(origin, target, board)))
                && super.tryMove(origin, target, board);
    }

    /**
     * Overrides AbstractStrategy.tryUnMove(). Checks first that the target is either both perpendicular or diagonal to
     * the origin and one square away, or is a valid un castle. Then functions identically to the method it overrides.
     * @param origin the start Coordinate
     * @param target the end Coordinate
     * @param board the board the un move is attempting to be made on
     * @return whether the un move can be made on the board
     */
    @Override
    public boolean tryUnMove(Coordinate origin, Coordinate target, ChessBoard board) {
        int yDiff = Math.abs(origin.getY() - target.getY());
        return ((normalMove(origin, target, yDiff) && super.tryUnMove(origin, target, board))
                || unCastleCheck(origin, target, board));
    }

    /**
     * Overrides AbstractStrategy.updateBoard(). Ensures the rook is correctly moved and replaced after castling,
     * the functions identically to the method is overrides.
     * @param origin the start Coordinate
     * @param target the end Coordinate
     * @param board the board the move is attempting to be made on
     * @param unMove whether this is an un move
     */
    @Override
    public void updateBoard(Coordinate origin, Coordinate target, ChessBoard board, boolean unMove) {
        int xDiff = origin.getX() - target.getX();
        boolean castle = Math.abs(xDiff) == 2;
        Piece king = board.at(origin);
        if (unMove) {
            if (castle) {
                Coordinate rookLocation = new Coordinate(target.getX() + xDiff / 2, target.getY());
                Coordinate placement = new Coordinate(origin.getX() + (xDiff > 0 ? xDiff / 2 : xDiff),
                        origin.getY());
                board.place(placement, board.at(rookLocation));
                board.remove(rookLocation);
                board.setCastle(placement.getX() == 0 ? "queen" : "king", placement.getY() == 0 ? "white" : "black",
                        true);
            }
        } else {
            board.setCastle("king", king.getColour(), false);
            board.setCastle("queen", king.getColour(), false);
            if (castle) {
                Coordinate rookLocation = new Coordinate(target.getX() - (xDiff < 0 ? xDiff / 2 : xDiff),
                        target.getY());
                board.place(new Coordinate(origin.getX() - xDiff / 2, origin.getY()), board.at(rookLocation));
                board.remove(rookLocation);
            }
        }
        super.updateBoard(origin, target, board, unMove);
    }

    /**
     * Checks if a move is a valid castle.
     * @param origin the start Coordinate
     * @param target the end Coordinate
     * @param board the board the move is attempting to be made on
     * @return true if the move is a valid castle
     */
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

    /**
     * Checks if a move is a valid un castle.
     * @param origin the start Coordinate
     * @param target the end Coordinate
     * @param board the board the move is attempting to be made on
     * @return true if the move is a valid un castle
     */
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
                target.getX() - originTargetDiff,
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
     * This should only be called if all other castling checks have been carried out. Checks whether the king will
     * be castling or un castling through check.
     * @param origin the start Coordinate
     * @param direction an integer describing the direction the king is castling in
     * @param board the board the move is attempting to be made on
     * @return true if the king is castling through check, false otherwise
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
