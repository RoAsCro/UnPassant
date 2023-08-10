package Heuristics;


import java.util.List;

public interface Deduction {
    List<Observation> getObservations();

    public boolean deduce(BoardInterface board);

    public Boolean getState();
}
