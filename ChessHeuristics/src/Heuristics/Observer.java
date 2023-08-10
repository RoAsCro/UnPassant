package Heuristics;

import StandardChess.Coordinate;

import java.util.Map;

/**
 * An observer stores observations about the board independent of deductions
 */
public interface Observer {

    public void put(String colour, String piece, Coordinate coordinate);
    public Path getCoordinates(String colour, String piece);

    public void runObservations(BoardInterface boardInterface);

    public Map<String, Path> getAllCoordinates(String colour);

    }
