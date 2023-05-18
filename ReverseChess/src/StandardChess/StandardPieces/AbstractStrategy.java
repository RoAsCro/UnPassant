package StandardChess.StandardPieces;

import StandardChess.*;

import java.util.function.BiPredicate;

public abstract class AbstractStrategy implements PieceStrategy{

    protected final static int BOARD_LENGTH = 8;
    protected final static BiPredicate<Coordinate, Coordinate> DIAGONAL =
            (c, d) -> Math.abs(c.getX() - d.getX())
                    == Math.abs(c.getY() - d.getY());
    protected final static BiPredicate<Coordinate, Coordinate> PERPENDICULAR =
            (c, d) -> c.getX() == d.getX() || c.getY() == d.getY();

    private int speed;
    private String name;

    public AbstractStrategy(String name, int speed) {
        this.name = name;
        this.speed = speed;
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public boolean tryMove(Coordinate origin, Coordinate target, ChessBoard board) {
        Coordinate direction;
        int xDiff = (origin.getX() - target.getX());
        int yDiff = (origin.getY() - target.getY());
        int absDiff = Math.abs(xDiff) - Math.abs(yDiff);
        if (xDiff * yDiff * absDiff == 0) {
            direction = new Coordinate( ((int) (xDiff / (Math.abs(xDiff) - 0.4))),
                    ((int) (yDiff / (Math.abs(yDiff) - 0.4))));
        } else {
            direction = new Coordinate(xDiff, yDiff);
        }

        boolean finished = false;
        Coordinate currentCoord = origin;
        Piece piece = board.at(origin);
        while (!finished) {
            currentCoord = Coordinates.add(currentCoord, direction);
            Piece currentPiece = board.at(currentCoord);
            if (currentCoord.equals(target)) {
                if (currentPiece.getColour().equals(piece.getColour())) {
                    return false;
                }
                finished = true;
            }
            else if (!currentPiece.getType().equals("null")) {
                return false;
            }
        }
        return true;
    }


}