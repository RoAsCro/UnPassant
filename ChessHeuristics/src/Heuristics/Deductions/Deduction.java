package Heuristics.Deductions;


import Heuristics.BoardInterface;
import Heuristics.Detector.StateDetector;

public interface Deduction {

    void registerDetector(StateDetector detector);

    public boolean deduce(BoardInterface board);

    public Boolean getState();

    String errorMessage();
    void update();
}
