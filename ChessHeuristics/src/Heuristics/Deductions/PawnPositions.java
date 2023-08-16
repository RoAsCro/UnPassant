package Heuristics.Deductions;

import Heuristics.BoardInterface;
import StandardChess.Coordinate;

import java.util.function.Predicate;

/**
 * PawnPositions is a deduction that checks that there are no pawn on the 1st or 8th ranks.
 * It will be set to false if this is the case.
 */
public class PawnPositions extends AbstractDeduction {
    private final static Predicate<Coordinate> pawnCheck = c -> c.getY() == 0 || c.getY() == 7;

    public PawnPositions() {
        super("Pawns in illegal position.");
    }

    @Override
    public boolean deduce(BoardInterface board) {
        this.state = board.getBoardFacts().getCoordinates(true, "pawn").stream().noneMatch(pawnCheck)
                && board.getBoardFacts().getCoordinates(false, "pawn").stream().noneMatch(pawnCheck);
        return false;
    }
}
