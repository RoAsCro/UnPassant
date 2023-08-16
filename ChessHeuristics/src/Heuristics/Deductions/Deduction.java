package Heuristics.Deductions;


import Heuristics.BoardInterface;
import Heuristics.Detector.StateDetector;

public interface Deduction {

    void registerDetector(StateDetector detector);

    void deduce(BoardInterface board);

    boolean getState();

    String errorMessage();
    void update();
}
