package SolveAlgorithm;

import Heuristics.Detector.DetectorInterface;
import java.util.function.Predicate;

public abstract class StateCondition implements Predicate<DetectorInterface> {
    @Override
    public boolean test(DetectorInterface detectorInterface) {
        return detectorInterface.getState();
    }


}
