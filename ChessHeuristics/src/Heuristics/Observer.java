package Heuristics;

import StandardChess.Coordinate;

import java.util.LinkedList;
import java.util.Map;

/**
 * A helper class for BoardInterface storing derived information about a chess board.
 */
public class Observer {
    /**A Map of Strings of piece names and Paths of Coordinates of white pieces of that type*/
    private final Map<String, Path> whitePieces = Map.of(
            "rook", new Path(), "bishop", new Path(), "knight", new Path(),
            "king", new Path(), "queen", new Path(), "pawn", new Path()
    );
    /**A Map of Strings of piece names and Paths of Coordinates of black pieces of that type*/
    private final Map<String, Path> blackPieces = Map.of(
            "rook", new Path(), "bishop", new Path(), "knight", new Path(),
            "king", new Path(), "queen", new Path(), "pawn", new Path()
    );

    /**
     * Puts the given Coordinate in the Map of pieces of the given colour under the given piece name.
     * @param white the colour of the piece, true if white, false if black
     * @param piece the name of the piece type
     * @param coordinate the Coordinate of the piece
     */
    public void put(boolean white, String piece, Coordinate coordinate) {
        Path coordinates = white
                ? whitePieces.get(piece) : blackPieces.get(piece);
        if (!coordinates.contains(coordinate)) {
            coordinates.add(coordinate);
        }
    }

    /**
     * Return a Path of Coordinates of all pieces of the given colour and type.
     * @param white the colour of the piece, true if white, false if black
     * @param piece the name of the piece
     * @return the Coordinates of all pieces of the given colour and type
     */
    public Path getCoordinates(boolean white, String piece) {
        return Path.of(white
                ? whitePieces.get(piece) : blackPieces.get(piece));
    }

    /**
     * Gets the Coordinates of all pieces of the given colour as a Map of Strings of piece types and Paths of
     * Coordinates of the pieces of that type.
     * @param colour the colour of the pieces as a String, white or black
     * @return
     */
    public Map<String, Path> getAllCoordinates(String colour) {
        return colour.equals("white")
                ? Map.copyOf(whitePieces)
                : Map.copyOf(blackPieces);
    }

    /**
     * Returns the number of pieces of the given colour.
     * @param white the colour of the pieces being looked for, true if white, false if black
     * @return the number of pieces of the given colour on the board
     */
    public int pieceNumbers(boolean white) {
        return (white ? whitePieces : blackPieces)
                .values().stream()
                .map(LinkedList::size)
                .reduce(Integer::sum)
                .orElse(0);
    }

    /**
     * Returns the number of pawns of the given colour.
     * @param white the colour of the pawns being looked for, true if white, false if black
     * @return the number of pawns of the given colour on the board
     */
    public int pawnNumber(boolean white) {
        return getCoordinates(white, "pawn").size();
    }

}
