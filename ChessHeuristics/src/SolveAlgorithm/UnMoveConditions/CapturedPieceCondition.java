package SolveAlgorithm.UnMoveConditions;

import java.util.function.Predicate;

/**
 * Example condition for UnMoves testing whether a move uncaptures a piece of the given type.
 * @author Roland Crompton
 */
public class CapturedPieceCondition implements Predicate<String> {
    /**The piece character checked*/
    private final char piece;

    /**
     * Takes a given character representing a piece to be checked against when testing UnMoves. Input '-'
     * to ignore this condition.
     * @param piece the character piece to be checked against
     */
    public CapturedPieceCondition(char piece) {
        this.piece = piece;
    }

    /**
     * Tests if a move contains the uncapture of the given piece character. Also true if the set character is '-'.
     * @param s the input argument
     * @return true if the piece character is '-' or if the move captures the given piece.
     */
    @Override
    public boolean test(String s) {
        String move = s.split(":")[1];
        return move.length() == 0 || piece == '-' || move.charAt(move.length() - 3) == piece;
    }
}
