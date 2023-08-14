package Heuristics.Deductions;

import Heuristics.BoardInterface;
import StandardChess.Coordinate;

import java.util.function.Predicate;

public class PawnPositions extends AbstractDeduction {
    private final static Predicate<Coordinate> pawnCheck = c -> c.getY() == 0 || c.getY() == 7;

    @Override
    public boolean deduce(BoardInterface board) {
        this.state = board.getBoardFacts().getCoordinates(true, "pawn").stream().noneMatch(pawnCheck)
                && board.getBoardFacts().getCoordinates(false, "pawn").stream().noneMatch(pawnCheck);
        return false;
    }
}
