package Heuristics;

import StandardChess.BoardReader;
import StandardChess.ChessBoard;
import StandardChess.Coordinate;

import java.util.function.Predicate;

public class WholeBoardPredicate implements Predicate<BoardReader> {
    @Override
    public boolean test(BoardReader reader) {
        int currentX = reader.getCoord().getX();
        int currentY = reader.getCoord().getY();
        if (currentY >= ChessBoard.LENGTH){
            return false;
        }
        if (currentX >= ChessBoard.LENGTH){
            reader.to(new Coordinate(-1, currentY + 1));
        }
        return true;
    }
}
