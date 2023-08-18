package SolveAlgorithm.UnMoveConditions;

import java.util.function.Predicate;

public class LocationCondition implements Predicate<String> {
    private final String from;
    private final String to;

    public LocationCondition(String from, String to) {

        this.from = from;
        this.to = to;
    }

    @Override
    public boolean test(String s) {
        String move = s.split(":")[1];
        return (from.equals("-") || move.substring(1, 3).equals(from)) &&
                (to.equals("-") || move.substring(move.length() - 2).equals(to));
    }
}
