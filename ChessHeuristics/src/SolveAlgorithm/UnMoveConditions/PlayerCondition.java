package SolveAlgorithm.UnMoveConditions;


import java.util.function.Predicate;

public class PlayerCondition implements Predicate<String> {
    private char player;

    public PlayerCondition(char player) {
        this.player = player;
    }
    @Override
    public boolean test(String s) {
        return s.split(" ")[1].charAt(0) == player || player == '-';
    }
}
