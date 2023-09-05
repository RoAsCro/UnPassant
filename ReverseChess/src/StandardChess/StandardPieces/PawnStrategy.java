package StandardChess.StandardPieces;

import StandardChess.ChessBoard;
import StandardChess.Coordinate;
import StandardChess.Coordinates;
import StandardChess.Piece;

/**
 * An implementation of PieceStrategy for a bishop, describing its movement, additional moves, and how it updates the
 * board after it moves.
 * @author Roland Crompton
 */
public class PawnStrategy extends AbstractStrategy {

    /**
     * Constructs the PieceStrategy, setting its moves to vertical, horizontal, and diagonal.
     */
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

    /**
     * Overrides AbstractStrategy.getMoves(), returning a different moveset depending on the colour given.
     * @param origin the start Coordinate of the moves
     * @param colour the colour of the piece as a String, "white" or "black"
     * @return an array containing all possible Coordinates this piece can move to
     */
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

    /**
     * Overrides AbstractStrategy.getUnMoves(), returning a different moveset depending on the colour given.
     * @param origin the start Coordinate of the moves
     * @param colour the colour of the piece as a String, "white" or "black"
     * @return an array containing all possible Coordinates this piece can un move to
     */
    @Override
    public Coordinate[] getUnMoves(Coordinate origin, String colour) {
        return getMoves(origin, colour.equals("white") ? "black" : "white");
    }

    /**
     * A helper method for tryMove() and tryUnMove(). Checks that the move is either a valid normal, a capture,
     * an en passant, or a double move.
     * @param origin the start Coordinate
     * @param target the end Coordinate
     * @param board the board the move is attempting to be made on
     * @param unMove a multiplier describing whether this is an un move, 1 for forward move, -1 for un move
     * @return whether this is a valid pawn move
     */
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
                        (xCheck(xDiff, 1, target, unMove == 1, board, colour))
                        ||
                        // En Passant
                        (xCheck(xDiff, 1, target, false, board, colour)
                                && enPassantCheck(yDiff, target, colour, board))))
                ||
                // Double move
                (noXChange && doubleMoveCheck(origin, colour, yDiff, unMove));
    }

    /**
     * Overrides AbstractStrategy.tryMove(). Checks first that the target is both one y forward or backward
     * (depending on colour), then either on the same x, or that a capture is taking place, or an en passant,
     * or a double move, then functions identically to the method it overrides.
     * @param origin the start Coordinate
     * @param target the end Coordinate
     * @param board the board the move is attempting to be made on
     * @return whether the move can be made on the board
     */
    @Override
    public boolean tryMove(Coordinate origin, Coordinate target, ChessBoard board) {
        return tryMoveGeneral(origin, target, board, 1)
                && super.tryMove(origin, target, board);
    }
    /**
     * Overrides AbstractStrategy.tryUnMove(). Checks first that the target is both one y forward or backward
     * (depending on colour), then either on the same x, or that an un capture is taking place, or an un passant,
     * or a double move, then functions identically to the method it overrides.
     * @param origin the start Coordinate
     * @param target the end Coordinate
     * @param board the board the un move is attempting to be made on
     * @return whether the  un move can be made on the board
     */
    @Override
    public boolean tryUnMove(Coordinate origin, Coordinate target, ChessBoard board) {
        return target.getY() != ChessBoard.LENGTH - 1 && target.getY() != 0
                && tryMoveGeneral(origin, target, board, -1)
                && super.tryUnMove(origin, target, board);
    }

    /**
     * Overrides AbstractStrategy.updateBoard(). For a forward move,
     * if a double move is made, sets the en passant Coordinate on the ChessBoard to the square between the origin and
     * the target; if an en passant is made, removes th piece between the origin and the target.
     * For an un move,
     * if a capture is made, the en passant Coordinate is set to the Coordinate of where the un captured pawn
     * would be if this were an en passant - this relies on a separate class to correct it if this is wrong ;
     * all other moves remove the en passant Coordinate
     * @param origin the start Coordinate
     * @param target the end Coordinate
     * @param board the board the move is attempting to be made on
     * @param unMove whether this is an un move
     */
    @Override
    public void updateBoard(Coordinate origin, Coordinate target, ChessBoard board, boolean unMove) {
        int xDiff = Math.abs(origin.getX() - target.getX());
        int yDiff = origin.getY() - target.getY();
        String colour = board.at(origin).getColour();
        if (unMove) {
            if (xDiff != 0 && NOT_NULL.test(board.at(origin))) {
                board.setEnPassant(new Coordinate(origin.getX(), origin.getY() - yDiff));
            } else {
                super.updateBoard(origin, target, board, unMove);
            }

        } else {
            if (doubleMoveCheck(origin, colour, yDiff, 1)) {
                board.setEnPassant(new Coordinate(origin.getX(), origin.getY() - yDiff / 2));
            } else if (xCheck(xDiff, 1,
                    target, false,
                    board, colour)
                    && enPassantCheck(yDiff, target, colour, board)) {
                super.updateBoard(origin, target, board, unMove);
                board.remove(new Coordinate(target.getX(), target.getY()+ yDiff));
            } else {
                super.updateBoard(origin, target, board, unMove);

            }
        }
    }

    /**
     * Checks that the given y-difference is the target y times the appropriate modifier for if "black" or "white" is
     * input.
     * @param colour the colour of the pawn, "white" or "black"
     * @param yDiff the y difference between a target and an origin
     * @param targetY the y of the target
     * @return if the y-difference is the target y times the appropriate modifier for if "black" or "white" is
     *      input
     */
    private boolean yCheck(String colour, int yDiff, int targetY) {
        int modifier = 1;
        if (colour.equals("white")) {
            modifier = -1;
        } else if (colour.equals("black")) {
            modifier = 1;
        }
        return yDiff == targetY * modifier;
    }

    /**
     * Tests that all are true: the x difference is the difference needed; the Piece at the target is either null
     * or not null, depending on the value of "null"; the Piece at the target is not the colour given.
     * @param xDiff the x difference
     * @param requiredDiff the required x difference
     * @param target the Coordinate location of the target
     * @param notNull whether the target should not be null, true if it should not be null, false if it should
     * @param board the board being checked
     * @param notColour the colour the piece should not be
     * @return whether all the above conditions are true
     */
    private boolean xCheck(int xDiff, int requiredDiff,
                           Coordinate target, boolean notNull,
                           ChessBoard board, String notColour) {
        return xDiff == requiredDiff
                && (NOT_NULL.test(board.at(target)) == notNull)
                && (!board.at(target).getColour().equals(notColour));
    }

    /**
     * Checks if an en passant can take place.
     * @param yDiff the y difference between an origin and target
     * @param target the Coordinate location of the target
     * @param colour the colour of the pawn, "white" or "black"
     * @param board the board being checked
     * @return true if an en passant can take place
     */
    private boolean enPassantCheck(int yDiff, Coordinate target, String colour, ChessBoard board) {
        Coordinate ePTarget = new Coordinate(target.getX(), target.getY() + yDiff);
        Piece targetPiece = board.at(ePTarget);
        return board.getEnPassant().equals(target)
                &&
                targetPiece.getType().equals("pawn")
                &&
                !targetPiece.getColour().equals(colour);
    }

    /**
     * Checks the move is an appropriate double move.
     * @param origin the Coordinate location of the origin
     * @param colour the colour of the pawn, "white" or "black"
     * @param yDiff the y difference between an origin and target
     * @param unMove a multiplier describing whether this is an un move, 1 for forward move, -1 for un move
     * @return if this is a valid double move
     */
    private boolean doubleMoveCheck(Coordinate origin, String colour, int yDiff, int unMove) {
        if (!yCheck(colour, yDiff, 2 * unMove)) {
            return false;
        }

        return (origin.getY() == 2 + yDiff / 2 && colour.equals("white"))
                || (origin.getY() == 5 + yDiff / 2 && colour.equals("black"));
    }
}
