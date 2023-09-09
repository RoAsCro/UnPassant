package ChessHeuristics.SolverAlgorithm.UnMoveConditions;


import java.util.function.Predicate;

/**
 * Example condition for UnMoves testing whether a move is made by the given player.
 * @author Roland Crompton
 */
public class PlayerCondition implements Predicate<String> {
    /**A character representing the player being checked*/
    private final char player;

    /**
     * Creates a condition testing if a move is given by a player of the given colour, 'w' for white, 'b' for black.
     * Can also be set to '-' to ignore this condition.
     * @param player a character representing the player being checked
     */
    public PlayerCondition(char player) {
        this.player = player;
    }

    /**
     * Tests if the given move is made by the player given at construction.
     * @param s the input argument
     * @return true if the player is '-' or if the move is made by the given player
     */
    @Override
    public boolean test(String s) {
        String move = s.split(" ")[1];
        return move.length() == 0 ||  move.charAt(0) != player || player == '-';
    }
}
