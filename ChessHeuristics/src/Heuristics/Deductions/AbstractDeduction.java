package Heuristics.Deductions;

import Heuristics.Deduction;
import Heuristics.Detector.StateDetector;

public abstract class AbstractDeduction implements Deduction {
    protected String errorMessage = "Position not legal.";

    protected StateDetector detector;

    protected Boolean state = true;

    public AbstractDeduction(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    @Override
    public String errorMessage(){
        return this.errorMessage;
    }

    @Override
    public void registerDetector(StateDetector detector) {
        this.detector = detector;
    }

    @Override
    public Boolean getState() {
        return this.state;
    }

    @Override
    public void update() {

    }

}
