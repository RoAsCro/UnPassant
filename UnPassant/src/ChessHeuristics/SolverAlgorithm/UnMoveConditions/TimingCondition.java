package ChessHeuristics.SolverAlgorithm.UnMoveConditions;

import java.util.function.Predicate;

/**
 * Example condition for UnMoves testing whether a move is made at the given time.
 * @author Roland Crompton
 */
public class TimingCondition implements Predicate<String> {
    /**The inclusive ply from which the condition is true*/
    private final int from;
    /**The non-inclusive time up to which the condition is true*/
    private final int to;

    /**
     * Creates a condition testing if the move takes place after the 'from' number, inclusive, and before the 'to'
     * number, not inclusive. The number represents the backwards ply, i.e., the first un-ply or un-movement of
     * a single piece is 0, the second is 1, and so on.
     * @param from the inclusive ply from which the condition is true
     * @param to the non-inclusive time up to which the condition is true
     */
    public TimingCondition(int from, int to) {
        this.from = from;
        this.to = to;
    }

    /**
     * Tests if a move takes place between the given times.
     * @param s the input argument
     * @return true if the move takes place between the given times
     */
    @Override
    public boolean test(String s) {
        int ply = Integer.parseInt(s.split(":")[2]);
        return ply >= from && ply < to;
    }
}
