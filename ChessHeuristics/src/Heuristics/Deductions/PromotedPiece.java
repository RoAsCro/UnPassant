package Heuristics.Deductions;

import Heuristics.BoardInterface;
import Heuristics.Observation;
import StandardChess.Coordinate;

import java.util.List;

public class PromotedPiece extends AbstractDeduction {

    Coordinate location;

    public PromotedPiece(Coordinate location) {
        this.location = location;
    }

    @Override
    public List<Observation> getObservations() {
        return null;
    }

    @Override
    public boolean deduce(BoardInterface board) {
        return false;
    }

    public Coordinate getLocation() {
        return this.location;
    }


    @Override
    public String toString() {
        return this.location.toString();
    }

}
