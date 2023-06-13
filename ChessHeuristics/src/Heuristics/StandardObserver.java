package Heuristics;

import StandardChess.Coordinate;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class StandardObserver {
    private final Map<String, LinkedList<Coordinate>> whitePieces = Map.of(
            "rook", new LinkedList<>(), "bishop", new LinkedList<>(), "knight", new LinkedList<>(),
            "king", new LinkedList<>(), "queen", new LinkedList<>(), "pawn", new LinkedList<>()
    );
    private final Map<String, LinkedList<Coordinate>> blackPieces = Map.of(
            "rook", new LinkedList<>(), "bishop", new LinkedList<>(), "knight", new LinkedList<>(),
            "king", new LinkedList<>(), "queen", new LinkedList<>(), "pawn", new LinkedList<>()
    );

    public void put(String colour, String piece, Coordinate coordinate) {
        LinkedList<Coordinate> coordinates = colour.equals("white")
                ? whitePieces.get(piece) : blackPieces.get(piece);
        if (!coordinates.contains(coordinate)) {
            coordinates.add(coordinate);
        }
    }

    public List<Coordinate> getCoordinates(String colour, String piece) {
        return colour.equals("white")
                ? whitePieces.get(piece) : blackPieces.get(piece);
    }



}
