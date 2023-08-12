package SolveAlgorithm.SolverShortcuts;

import SolveAlgorithm.Move;
import java.util.Objects;

/**
 * A none-pawn piece untaken by a non-pawn on the greater board (1 < y < 6) can be untaken
 * anywhere else on the greater board by any other non-pawn with the same result
 */
public class GreaterBoardShortcut extends AbstractSolverShortcut {

    @Override
    public boolean match(Move moveOne, Move moveTwo) {
        return test(moveOne) && test(moveTwo) && Objects.equals(moveOne.takenPiece(), moveTwo.takenPiece());
    }

    @Override
    public boolean test(Move move) {
        String pawn = "pawn";
        return !move.movedPiece().equals(pawn) && onGreaterBoard(move.origin()) && !move.takenPiece().equals(pawn);
    }
}
