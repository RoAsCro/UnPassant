package SolveAlgorithm.UnMoveConditions;

import java.util.function.Predicate;

public class TimingCondition implements Predicate<String> {
    private final int from;
    private final int to;

    public TimingCondition(int from, int to) {
        this.from = from;
        this.to = to;
    }
    @Override
    public boolean test(String s) {
        int ply = Integer.parseInt(s.split(":")[2]);
        return ply >= from && ply < to;
    }
}
