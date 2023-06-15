package StandardChess.StandardPieces;

import StandardChess.ChessBoard;
import StandardChess.Coordinate;
import StandardChess.Coordinates;

public class QueenStrategy extends AbstractStrategy{
    public QueenStrategy() {
        super("queen",
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
    public boolean tryMove(Coordinate origin, Coordinate target, ChessBoard board) {
        return (DIAGONAL.test(origin, target) || PERPENDICULAR.test(origin, target))
                && super.tryMove(origin, target, board);
    }

    @Override
    public boolean tryUnMove(Coordinate origin, Coordinate target, ChessBoard board) {
        return (DIAGONAL.test(origin, target) || PERPENDICULAR.test(origin, target))
                && super.tryUnMove(origin, target, board);
    }

}
