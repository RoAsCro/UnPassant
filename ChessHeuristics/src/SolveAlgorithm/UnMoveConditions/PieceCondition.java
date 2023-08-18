package SolveAlgorithm.UnMoveConditions;

import java.util.function.Predicate;

public class PieceCondition implements Predicate<String> {
    private char piece;

    public PieceCondition(char piece) {

        this.piece = piece;
    }
    @Override
    public boolean test(String s) {
        return s.split(":")[1].charAt(0) == piece || piece == '-';
    }
}
