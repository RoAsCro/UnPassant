package ChessHeuristics.SolverAlgorithm.UnMoveConditions;

import java.util.function.Predicate;
/**
 * Example condition for UnMoves testing whether the move is of the given type, a normal move, a capture, or an en
 * passant.
 * @author Roland Crompton
 */
public class MovementCondition implements Predicate<String> {
    /**A String representing the type of move being checked for*/
    private final String movement;

    /**
     * Creates a movement condition testing if the move is a normal move, "-",  a capture, "x", a castle, "O-O" or
     * "O-O-O" depending on if the castle is king- or queenside, or an en passant, "e.p.". Otherwise, the string may be
     * "any" to ignore this condition.
     * @param movement a String representing the type of move being checked for
     */
    public MovementCondition(String movement) {
        this.movement = movement;
    }

    /**
     * Checks if the move is of the type given at construction.
     * @param s the input argument
     * @return whether the move contains the given move type, or true if the given move type is "any"
     */
    @Override
    public boolean test(String s) {
        String move = s.split(":")[1];
        return move.length() == 0 || (this.movement.equals("any") ||
                (move.charAt(3) == this.movement.charAt(0) && move.contains(movement)));
    }
}
