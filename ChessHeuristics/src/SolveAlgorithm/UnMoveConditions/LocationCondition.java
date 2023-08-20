package SolveAlgorithm.UnMoveConditions;

import java.util.function.Predicate;
/**
 * Example condition for UnMoves testing whether a move is from one coordinate to another the given piece.
 */
public class LocationCondition implements Predicate<String> {
    /**The origin of the move*/
    private final String from;
    /**The target of the move*/
    private final String to;

    /**
     * Constructs a location condition testing if a move is from the given 'from' coordinate and to the given 'to'
     * coordinate. From should be the beginning of the move if the move were a forward move. The coordinates should be
     * formatted as two character Strings, the first character a letter a-h, the second a number 1-8. Either or both
     * coordinates can be given as "-" to be ignored.
     * @param from the coordinate moved from
     * @param to the coordinate moved to
     */
    public LocationCondition(String from, String to) {
        this.from = from;
        this.to = to;
    }

    /**
     * Returns true if the move is either from the 'from' coordinate or the 'from' coordinate is "-" AND
     * the move is to the 'to' coordinate or the 'to' coordinate is "-".
     * @param s the input argument
     * @return whether the move fulfils the movement conditions
     */
    @Override
    public boolean test(String s) {
        String move = s.split(":")[1];
        return move.length() == 0 ||  ((from.equals("-") || move.substring(1, 3).equals(from)) &&
                (to.equals("-") || move.substring(move.length() - 2).equals(to)));
    }
}
