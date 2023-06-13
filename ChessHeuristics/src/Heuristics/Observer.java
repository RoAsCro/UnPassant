package Heuristics;

import StandardChess.Coordinate;

import java.util.List;

/**
 * An observer stores observations about the board independent of deductions
 */
public interface Observer {

    public void put(String colour, String piece, Coordinate coordinate);
    public List<Coordinate> getCoordinates(String colour, String piece);

    public void runObservations(BoardInterface boardInterface);

    }
