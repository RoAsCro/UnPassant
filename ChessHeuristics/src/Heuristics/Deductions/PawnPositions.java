package Heuristics.Deductions;

import Heuristics.BoardInterface;
import Heuristics.Observation;
import StandardChess.Coordinate;

import java.util.LinkedList;
import java.util.List;
import java.util.function.Predicate;

public class PawnPositions extends AbstractDeduction {
    private final static Predicate<Coordinate> pawnCheck = c -> c.getY() == 0 || c.getY() == 7;

    @Override
    public List<Observation> getObservations() {
        return new LinkedList<>();
    }

    @Override
    public boolean deduce(BoardInterface board) {
        this.state = board.getBoardFacts().getCoordinates("white", "pawn").stream().noneMatch(pawnCheck)
                && board.getBoardFacts().getCoordinates("black", "pawn").stream().noneMatch(pawnCheck);
        return false;
    }
}
