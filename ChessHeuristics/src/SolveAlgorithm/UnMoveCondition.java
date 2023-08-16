package SolveAlgorithm;

import java.util.function.Predicate;

public class UnMoveCondition implements Predicate<String>{


    private boolean when1;
    private int when2;
    private int when3;
    private char who;
    private String from;
    private final String to;
    private final char what1;
    private final char what2;
    private final String how;
    private final boolean not;

    public UnMoveCondition(boolean when1, int when2, int when3, char who, String from, String to,
                           char what1, char what2, String how, boolean not) {

        this.when1 = when1;
        this.when2 = when2;
        this.when3 = when3;
        this.who = who;
        this.from = from;
        this.to = to;
        this.what1 = what1;
        this.what2 = what2;
        this.how = how;
        this.not = not;
    }

    public boolean test(String move) {
        String[] splits = move.split(":");
        int turn = Integer.parseInt(splits[2]);
        String movement = splits[1];
        if ((this.who != '-' && splits[1].charAt(0) != this.who) ||
                (!this.when1 && (turn < this.when2 || turn >= this.when3)) ||
                (!this.from.equals("-") && !movement.substring(1, 3).equals(this.from)) ||
                (!this.to.equals("-") && !movement.endsWith(this.to)) ||
                (this.what1 != '-' && movement.charAt(0) != this.what1) ||
                (this.what2 != '-' && movement.substring(movement.length()-2,
                        movement.length()-1).charAt(0) != this.what2) ||
                (!movement.contains(this.how))) {
            return this.not;
        }
        return !this.not;
    }
}
