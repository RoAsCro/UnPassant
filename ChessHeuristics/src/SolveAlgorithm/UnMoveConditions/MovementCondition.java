package SolveAlgorithm.UnMoveConditions;

import java.util.function.Predicate;

public class MovementCondition implements Predicate<String> {
    private String movement;

    public MovementCondition(String movement) {

        this.movement = movement;
    }
    @Override
    public boolean test(String s) {
        return movement.equals("any") || s.split(":")[1].contains(movement);
    }
}
