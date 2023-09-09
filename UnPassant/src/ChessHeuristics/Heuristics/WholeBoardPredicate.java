package ChessHeuristics.Heuristics;


import ReverseChess.StandardChess.BoardReader;
import ReverseChess.StandardChess.ChessBoard;
import ReverseChess.StandardChess.Coordinate;

import java.util.function.Predicate;

/**
 * A Predicate for iterating over a whole chessboard.
 * @author Roland Crompton
 */
public class WholeBoardPredicate implements Predicate<BoardReader> {
    /**
     * A Predicate for iterating over a whole chessboard. Moves the BoardReader to the beginning of the next row
     * if the end of the row is reached, and returns false only when the y is greater than the board length.
     * @param reader the input argument
     * @return false if the y is greater than the length of the board
     */
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
