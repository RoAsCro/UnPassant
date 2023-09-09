package ReverseChess.StandardChess;

import java.util.function.Consumer;
import java.util.function.Predicate;

/**
 * An interface for a class for iterating across a ChessBoard.
 * @author Roland Crompton
 */
public interface BoardReader {

    /**
     * Outputs the ChessBoard as a FEN.
     * @return a FEN representation of the ChessBoard
     */
    String toFEN();

    /**
     * Returns whether the specified Coordinate is in check. If the king is in check, it will return the
     * Coordinate of the checking piece. Otherwise, it will return the NullCoordinate.
     * @param kingLocation the Coordinate location of the king being checked
     * @return the Coordinate of the checking piece, or the NullCoordinate if not in check.
     */
    Coordinate inCheck(Coordinate kingLocation);

    /**
     * Returns the current location of the BoardReader.
     * @return a Coordinate of the current location of the Board Reader
     */
    Coordinate getCoord();

    /**
     * Moves Board reader to the next Coordinate in the given direction.
     * @param direction the direction to move the BoardReader in
     * @return the Piece at the new location of the BoardReader
     */
    Piece next(Coordinate direction);

    /**
     * Returns true if the next Coordinate in the given direction is on the ChessBoard.
     * @param direction the direction to check
     * @return true if the next Coordinate in the given direction is on the ChessBoard
     */
    boolean hasNext(Coordinate direction);

    /**
     * Moves Board reader to the next Coordinate in the given direction while the given condition remains
     * true.
     * @param direction the direction to move the BoardReader in
     * @param condition the condition that needs to remain true for the BoardReader to continue moving
     */
    void nextWhile(Coordinate direction, Predicate<Coordinate> condition);

    /**
     * Moves Board reader to the next Coordinate in the given direction while the given condition remains
     * true for the next Coordinate, carrying out the given function at each Piece it crosses, starting with the
     * Coordinate the BoardReader is currently at.
     * @param direction the direction to move the BoardReader in
     * @param condition the condition that needs to remain true for the BoardReader to continue moving
     * @param function the function carried out on each Piece
     */
    void nextWhile(Coordinate direction, Predicate<Coordinate> condition, Consumer<Piece> function);

    /**
     * Moves Board reader across the whole board while the given condition remains true for the next Coordinate.
     * @param condition the condition that needs to remain true for the BoardReader to continue moving
     */
    void wholeBoard(Predicate<Coordinate> condition);

    /**
     * Moves Board reader across the whole board  while the given condition remains true for the next Coordinate,
     * carrying out the given function at each Piece it crosses starting with the Coordinate it's currently at.
     * @param condition the condition that needs to remain true for the BoardReader to continue moving
     * @param function the function carried out on each Piece
     */
    void wholeBoard(Predicate<Coordinate> condition, Consumer<Piece> function);

    /**
     * Moves the BoardReader to the given Coordinate.
     * @param target the Coordinate to move the BoardReader
     * @return the Piece at the given location
     */
    Piece to(Coordinate target);

}
