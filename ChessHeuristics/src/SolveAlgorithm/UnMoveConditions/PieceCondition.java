package SolveAlgorithm.UnMoveConditions;

import java.util.function.Predicate;

/**
 * Example condition for UnMoves testing whether a move moves a piece of the given type.
 * @author Roland Crompton
 */
public class PieceCondition implements Predicate<String> {
    /**A character representing the piece type*/
    private final char piece;

    /**
     * Creates a condition testing if a move moves a piece of the given type. Input '-' to ignore this condition.
     * @param piece a character representing the piece type
     */
    public PieceCondition(char piece) {
        this.piece = piece;
    }

    /**
     * Tests if the move contains the movement of a piece of the given type.
     * @param s the input argument
     * @return true if the piece character is '-' or if the move moves the given piece.
     */
    @Override
    public boolean test(String s) {
        String move = s.split(":")[1];
        return move.length() == 0 || move.charAt(0) == piece || piece == '-';
    }
}
