package Heuristics;


public interface Deduction {

    void registerDetector(StateDetector detector);

    public boolean deduce(BoardInterface board);

    public Boolean getState();

    String errorMessage();
    void update();
}
