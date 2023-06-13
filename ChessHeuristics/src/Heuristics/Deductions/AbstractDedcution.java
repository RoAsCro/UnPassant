package Heuristics.Deductions;

import Heuristics.Deduction;
import StandardChess.Coordinate;

import java.util.LinkedList;
import java.util.List;

public abstract class AbstractDedcution implements Deduction {

    protected static final int MAX_PAWNS = 8;

    Deduction or;
    Deduction xor;


    @Override
    public void addOr(Deduction deduction) {
        if (this.or == null) {
            this.or = deduction;
        } else {
            this.or.addOr(deduction);
        }
    }

    @Override
    public void addXor(Deduction deduction) {
        if (this.xor == null) {
            this.xor = deduction;
        } else {
            this.xor.addXor(deduction);
        }
    }

    @Override
    public List<Deduction> orList() {
        List<Deduction> list = new LinkedList<>();
        return orList(list);
    }
    @Override
    public List<Deduction> orList(List<Deduction> list) {
        list.add(this);
        if (this.or != null) {
            this.or.orList(list);
        }
        return list;
    }

}
