package Heuristics;


import java.util.List;

public interface Deduction {

    void registerDetector(StateDetector detector);

    List<Observation> getObservations();

    public boolean deduce(BoardInterface board);

    public Boolean getState();

    String errorMessage();
    void update();
}
