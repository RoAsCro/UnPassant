package Heuristics.Deductions;

import Heuristics.Deduction;

import java.util.LinkedList;
import java.util.List;

public abstract class AbstractDeduction implements Deduction {

    protected static final int MAX_PAWNS = 8;

    protected Boolean state = true;

    @Override
    public Boolean getState() {
        return this.state;
    }

}
