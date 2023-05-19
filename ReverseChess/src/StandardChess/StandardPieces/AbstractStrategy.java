package StandardChess.StandardPieces;

import StandardChess.*;

import java.util.function.BiPredicate;
import java.util.function.Predicate;

public abstract class AbstractStrategy implements PieceStrategy{

    protected final static Predicate<Piece> NOT_NULL = p -> !p.getType().equals("null");
    protected final static BiPredicate<Coordinate, Coordinate> DIAGONAL =
            (c, d) -> Math.abs(c.getX() - d.getX())
                    == Math.abs(c.getY() - d.getY());
    protected final static BiPredicate<Coordinate, Coordinate> PERPENDICULAR =
            (c, d) -> c.getX() == d.getX() || c.getY() == d.getY();

    private String name;

    public AbstractStrategy(String name) {
        this.name = name;
    }

    @Override
    public String getName() {
        return this.name;
    }

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

    public boolean tryAnyMove(Coordinate origin, Coordinate target, ChessBoard board,
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

    @Override
    public boolean tryMove(Coordinate origin, Coordinate target, ChessBoard board) {
        return tryAnyMove(origin, target, board,
                (p, q) -> p.getColour().equals(q.getColour()));
    }

    @Override
    public boolean tryUnMove(Coordinate origin, Coordinate target, ChessBoard board) {
        return tryAnyMove(origin, target, board,
                (p, q) -> NOT_NULL.test(p));
    }

    @Override
    public void updateBoard(Coordinate origin, Coordinate target, ChessBoard board, boolean unMove) {
        board.setEnPassant(new Coordinate(-1, -1));
    }


}
