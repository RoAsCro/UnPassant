package Heuristics.Observations;

import Heuristics.Observation;

public abstract class AbstractObservation implements Observation {

    @Override
    public boolean equals(Object o) {
        return o instanceof AbstractObservation && o.hashCode() == this.hashCode();
    }

    @Override
    public int hashCode() {
        return this.getClass().hashCode();
    }
}
