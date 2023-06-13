package Heuristics;


import StandardChess.Coordinate;

import java.util.List;

public interface Deduction {
    List<Observation> getObservations();

    public boolean deduce(BoardInterface board);

    void addOr(Deduction deduction);

    void addXor(Deduction deduction);

    List<Deduction> orList();

    List<Deduction> orList(List<Deduction> list);

    public Boolean getState();
}
