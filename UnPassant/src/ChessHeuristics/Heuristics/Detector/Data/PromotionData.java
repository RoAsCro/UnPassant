package ChessHeuristics.Heuristics.Detector.Data;

import ChessHeuristics.Heuristics.Path;
import ReverseChess.StandardChess.Coordinate;

import java.util.Map;

/**
 * Stores data about promotions in a chess game. For use with a StateDetector.
 * @author Roland Crompton
 */
public interface PromotionData {
    /**
     * Retrieves a Map of Strings of piece types and Maps of Paths containing sets of potentially promoted pieces,
     * and the number of those pieces which are not necessarily promoted.
     * <p></p>
     * The String is formatted as the piece name in lowercase, followed by, if the piece is a bishop, 'l' or 'd'
     * indicating if the bishop is light or dark square respectively, followed by 'b' or 'w' indicating if the piece
     * is black or white respectively. For example: rookb, bishoplw, quuenw.
     * <p></p>
     * The pieces are divided into Paths representing set of pieces, these sets of pieces are those for whom
     * whether they are promoted is dependent on the Integer stored with them - i.e. there may be a Path
     * [(0, 4), (6, 3), (3, 5)] with an Integer 2; this means that of those three pieces, only one MUST be promoted.
     * @return a Map of names of pieces, sets of those pieces, and how many of those pieces do not need to be
     * promoted
     */
    Map<String, Map<Path, Integer>> getPromotionNumbers();

    /**
     * Returns a Map of promotion squares, i.e. Coordinates of y = 1 or 8, and Paths of the
     * Coordinates of pieces that may have promoted on those squares.
     * @return a Map of promotion squares and the pieces that may have promoted on them
     */
    Map<Coordinate, Path> getPromotedPieceMap();
}
