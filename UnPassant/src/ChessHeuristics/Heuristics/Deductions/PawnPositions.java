package ChessHeuristics.Heuristics.Deductions;

import ChessHeuristics.Heuristics.BoardInterface;
import ChessHeuristics.Heuristics.HeuristicsUtil;
import ReverseChess.StandardChess.Coordinate;

import java.util.function.Predicate;

/**
 * PawnPositions is a deduction that checks that there are no pawn on the 1st or 8th ranks.
 * <p></p>
 * It will be set to false if this is the case.
 * @author Roland Crompton
 */
public class PawnPositions extends AbstractDeduction {
    /**A Predicate checking a given Coordinate is on the 1st or 8th rank */
    private final static Predicate<Coordinate> pawnCheck = c -> c.getY() == HeuristicsUtil.FIRST_RANK_Y
            || c.getY() ==  HeuristicsUtil.FINAL_RANK_Y;

    /**
     * Constructor setting the errorMessage to "Pawns in illegal position."
     */
    public PawnPositions() {
        super("Pawns in illegal position.");
    }

    /**
     * Checks if any pawns are on the 1st or 8th rank of the given board, setting this Deduction to false if they are.
     * @param board the board to be checked
     */
    @Override
    public void deduce(BoardInterface board) {
        this.state = board.getBoardFacts().getCoordinates(true, "pawn").stream().noneMatch(pawnCheck)
                && board.getBoardFacts().getCoordinates(false, "pawn").stream().noneMatch(pawnCheck);
    }
}
