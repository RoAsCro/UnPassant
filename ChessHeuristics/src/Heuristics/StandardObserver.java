package Heuristics;

import StandardChess.Coordinate;

import java.util.LinkedList;
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

    public void put(boolean white, String piece, Coordinate coordinate) {
        Path coordinates = white
                ? whitePieces.get(piece) : blackPieces.get(piece);
        if (!coordinates.contains(coordinate)) {
            coordinates.add(coordinate);
        }
    }

    public Path getCoordinates(boolean white, String piece) {
        return Path.of(white
                ? whitePieces.get(piece) : blackPieces.get(piece));
    }

    public Map<String, Path> getAllCoordinates(String colour) {
        return colour.equals("white")
                ? Map.copyOf(whitePieces)
                : Map.copyOf(blackPieces);
    }

    public int pieceNumbers(boolean white) {
        return (white ? whitePieces : blackPieces)
                .values().stream()
                .map(LinkedList::size)
                .reduce(Integer::sum)
                .orElse(0);
    }

    public int pawnNumber(boolean white) {
        return getCoordinates(white, "pawn").size();
    }

}
