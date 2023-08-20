package SolveAlgorithm.UnMoveConditions;

import java.util.function.Predicate;

/**
 * An example condition testing whether the given conditions are fulfilled. Specifically, will return true if the
 * tested move does NOT take place during the timing and location conditions, or if it does and fulfills all the other
 * conditions, otherwise false. The latter part of the condition is inverted is 'not' is true.
 * @author Roland Crompton
 */
public class UnMoveCondition implements Predicate<String>{

    private final boolean not;
    private final TimingCondition timing;
    private final PlayerCondition player;
    private final LocationCondition location;
    private final MovementCondition movement;
    private final PieceCondition piece;
    private final CapturedPieceCondition capture;

    /**
     * Creates a condition that returns true if the tested move does NOT take place during the timing and
     * location conditions, or if it does and fulfills all the other conditions,
     * otherwise false. The latter part of the condition is inverted is 'not' is true.
     * @param fromTime the inclusive ply from which the condition is true
     * @param toTime the non-inclusive time up to which the condition is true
     * @param player a character representing the player being checked
     * @param from the coordinate moved from
     * @param to the coordinate moved to
     * @param piece1 a character representing the piece type
     * @param piece2 the character piece to be checked against
     * @param how a String representing the type of move being checked for
     * @param not whether to invert the conditions
     */
    public UnMoveCondition(int fromTime, int toTime, char player, String from, String to,
                           char piece1, char piece2, String how, boolean not) {
        this.timing = new TimingCondition(fromTime, toTime);
        this.player = new PlayerCondition(player);
        this.location = new LocationCondition(from, to);
        this.piece = new PieceCondition(piece1);
        this.capture = new CapturedPieceCondition(piece2);
        this.movement = new MovementCondition(how);
        this.not = not;
    }

    /**
     * True if the timing and location conditions are not both fulfilled.
     * True if the timing and location conditions are both fulfilled, as well as every other condition,
     * and 'not' is false, and false under the same conditions if 'not' is true.
     * False if the timing and location conditions are both fulfilled and one of the other conditions is not fulfilled
     * and 'not' is false, and true under the same conditions if 'not' is true.
     * @param move the input argument
     * @return the result according to the above conditions
     */
    public boolean test(String move) {
        if (this.timing.and(this.player).test(move)) {
            if (this.location.and(this.piece.and(this.capture.and(this.movement))).test(move)) {
                return !this.not;
            }
            return this.not;
        }
        return true;
    }
}
