package SolveAlgorithm;

import Heuristics.Detector.DetectorInterface;

import java.util.function.Predicate;

/**
 * An abstract class for testing DetectorInterfaces during the course of solving a retrograde chess problem.
 */
public abstract class StateCondition implements Predicate<DetectorInterface> {
    /**
     * Tests the state of the DetectorInterface.
     * @param detectorInterface the input argument
     * @return true if the DetectorInterface's state is true, false otherwise
     */
    @Override
    public boolean test(DetectorInterface detectorInterface) {
        return detectorInterface.getState();
    }
}
