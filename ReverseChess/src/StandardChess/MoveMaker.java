package StandardChess;

import java.util.stream.Stream;

public class MoveMaker {

    public boolean tryMove(Coordinate origin, Coordinate target, Piece piece, Board board) {
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
        return piece.tryMove(origin, target);
    }

}
