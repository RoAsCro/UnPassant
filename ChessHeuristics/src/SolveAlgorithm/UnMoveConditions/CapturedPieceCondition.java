package SolveAlgorithm.UnMoveConditions;

import java.util.function.Predicate;

public class CapturedPieceCondition implements Predicate<String> {
    private char piece;

    public CapturedPieceCondition(char piece) {

        this.piece = piece;
    }
    @Override
    public boolean test(String s) {
        String move = s.split(":")[1];
        return piece == '-' || move.charAt(move.length() - 3) == piece;
    }
}
