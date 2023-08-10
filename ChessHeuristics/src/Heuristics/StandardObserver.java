package Heuristics;

import StandardChess.Coordinate;

import java.util.Map;

public class StandardObserver {
    private final Map<String, Path> whitePieces = Map.of(
            "rook", new Path(), "bishop", new Path(), "knight", new Path(),
            "king", new Path(), "queen", new Path(), "pawn", new Path()
    );
    private final Map<String, Path> blackPieces = Map.of(
            "rook", new Path(), "bishop", new Path(), "knight", new Path(),
            "king", new Path(), "queen", new Path(), "pawn", new Path()
    );

    public void put(String colour, String piece, Coordinate coordinate) {
        Path coordinates = colour.equals("white")
                ? whitePieces.get(piece) : blackPieces.get(piece);
        if (!coordinates.contains(coordinate)) {
            coordinates.add(coordinate);
        }
    }

    public Path getCoordinates(String colour, String piece) {
        return Path.of(colour.equals("white")
                ? whitePieces.get(piece) : blackPieces.get(piece));
    }

    public Map<String, Path> getAllCoordinates(String colour) {
        return colour.equals("white")
                ? Map.copyOf(whitePieces)
                : Map.copyOf(blackPieces);
    }


}
