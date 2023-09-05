package StandardChess.StandardPieces;

import StandardChess.ChessBoard;
import StandardChess.Coordinate;
import StandardChess.Coordinates;
import StandardChess.Piece;

import java.util.Arrays;
import java.util.function.BiPredicate;
import java.util.function.Predicate;

/**
 * An abstract implementation of PieceStrategy, implementing those methods common to most standard Strategies.
 * @author Roland Crompton
 */
public abstract class AbstractStrategy implements PieceStrategy{
    /**A test of whether two Coordinates are diagonal to each other*/
    protected final static BiPredicate<Coordinate, Coordinate> DIAGONAL =
            (c, d) -> Math.abs(c.getX() - d.getX())
                    == Math.abs(c.getY() - d.getY());
    /**A test of whether two Coordinates are either horizontal or vertical to each other from each other,
     * true if they are horizontal or vertical, false if not*/
    protected final static BiPredicate<Coordinate, Coordinate> PERPENDICULAR =
            (c, d) -> c.getX() == d.getX() || c.getY() == d.getY();
    /**A test of whether a Piece is a NullPiece, false if it is a NullPiece, true otherwise*/
    protected final static Predicate<Piece> NOT_NULL = p -> !p.getType().equals("null");
    /**This piece's moveset*/
    private final Coordinate[] moves;
    /**This piece's name, i.e. its type*/
    private final String name;

    /**
     * A constructor setting the piece strategy's type and moveset.
     * @param name the piece's type
     * @param moves the piece's moveset
     */
    public AbstractStrategy(String name, Coordinate[] moves) {
        this.moves = moves;
        this.name = name;
    }

    /**
     * Gets all possible moves one unit of movement away for the piece type from the given origin.
     * One unit of movement is the minimum distance a piece can travel using a given move. In most cases
     * this will be one square, but for example for knights this might be x + 2, y + 1.
     * Note this does not take into consideration any way the move may be illegal, e.g. if the move is out of bounds.
     * @param origin the start Coordinate of the moves
     * @param colour the colour of the piece as a String, "white" or "black"
     * @return an array containing all possible Coordinates this piece can move to
     */
    @Override
    public Coordinate[] getMoves(Coordinate origin, String colour) {
        return Arrays.stream(this.moves)
                .map(coordinate -> Coordinates.add(origin, coordinate))
                .toArray(Coordinate[]::new);
    }

    /**
     * Gets all possible un moves one unit of movement away for the piece type from the given origin.
     * One unit of movement is the minimum distance a piece can travel using a given move. In most cases
     * this will be one square, but for example for knights this might be x + 2, y + 1.
     * Note this does not take into consideration any way the move may be illegal, e.g. if the un move is out of bounds.
     * @param origin the start Coordinate of the moves
     * @param colour the colour of the piece as a String, "white" or "black"
     * @return an array containing all possible Coordinates this piece can un move to
     */
    @Override
    public Coordinate[] getUnMoves(Coordinate origin, String colour) {
        return getMoves(origin, colour);
    }

    /**
     * Returns the piece's name - this will be the piece's type.
     * @return the piece's name
     */
    @Override
    public String getType() {
        return this.name;
    }

    /**
     * A helper method for tryAndMove(), return the direction of target from the origin as a Coordinate containing
     * x and y values between -1 and 1.
     * @param origin the start Coordinate
     * @param target the end Coordinate
     * @return the Coordinate direction of the target from the origin
     */
    private Coordinate getDirection(Coordinate origin, Coordinate target) {
        Coordinate direction;
        int xDiff = (target.getX() - origin.getX());
        int yDiff = (target.getY() - origin.getY());
        int absDiff = Math.abs(xDiff) - Math.abs(yDiff);
        if (xDiff * yDiff * absDiff == 0) {
            direction = new Coordinate(Integer.compare(xDiff, 0),
                    Integer.compare(yDiff, 0));
        } else {
            direction = new Coordinate(xDiff, yDiff);
        }
        return direction;
    }

    /**
     * A helper method for the tryMove() and tryUnMove() methods. For every square between the origin and the target,
     * it ensures that square is empty. Once the target is reached, the given condition is tested on it, and if that
     * condition is true, false is returned.
     * @param origin the start Coordinate
     * @param target the end Coordinate
     * @param board the board the move is attempting to be made on
     * @param condition the condition to be tested at the target
     * @return false if the target fulfils the given condition, or any square between the origin and target
     * is occupied, true otherwise
     */
    private boolean tryAnyMove(Coordinate origin, Coordinate target, ChessBoard board,
                              BiPredicate<Piece, Piece> condition) {
        if (origin.equals(target)) {
            return false;
        }

        Coordinate direction = getDirection(origin, target);

        boolean finished = false;
        Coordinate currentCoord = origin;
        Piece piece = board.at(origin);
        while (!finished) {
            currentCoord = Coordinates.add(currentCoord, direction);
            Piece currentPiece = board.at(currentCoord);
            if (currentCoord.equals(target)) {
                if (condition.test(currentPiece, piece)) {
                    return false;
                }
                finished = true;
            }
            else if (NOT_NULL.test(currentPiece)) {
                return false;
            }
        }
        return true;
    }

    /**
     * Tries a move on a ChessBoard, starting at the origin, ending at the target. Ensures there's no pieces
     * between the origin and the target, and that the target is either unoccupied or contains a piece of the opposing
     * colour.
     * @param origin the start Coordinate
     * @param target the end Coordinate
     * @param board the board the move is attempting to be made on
     * @return whether the move can be made on the board
     */
    @Override
    public boolean tryMove(Coordinate origin, Coordinate target, ChessBoard board) {
        return tryAnyMove(origin, target, board,
                (p, q) -> p.getColour().equals(q.getColour()));
    }

    /**
     * Tries an un move on a ChessBoard, starting at the origin, ending at the target. Ensures there's no pieces
     * between the origin and the target, and that the target is empty.
     * @param origin the start Coordinate
     * @param target the end Coordinate
     * @param board the board the move is attempting to be made on
     * @return whether the move can be made on the board
     */
    @Override
    public boolean tryUnMove(Coordinate origin, Coordinate target, ChessBoard board) {
        return tryAnyMove(origin, target, board,
                (p, q) -> NOT_NULL.test(p));
    }

    /**
     * Updates the ChessBoard according to the given move or un move. By default, this updates the board's en passant
     * Coordinate to the null Coordinate
     * @param origin the start Coordinate
     * @param target the end Coordinate
     * @param board the board the move is attempting to be made on
     * @param unMove whether this is an un move
     */
    @Override
    public void updateBoard(Coordinate origin, Coordinate target, ChessBoard board, boolean unMove) {
        board.setEnPassant(Coordinates.NULL_COORDINATE);
    }


}
