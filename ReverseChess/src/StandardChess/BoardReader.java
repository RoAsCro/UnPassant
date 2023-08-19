package StandardChess;

import java.util.function.Consumer;
import java.util.function.Predicate;

public interface BoardReader {

    String toFEN();

    boolean inCheck(Coordinate kingLocation);

    Coordinate getCoord();

    Piece next(Coordinate direction);

    boolean hasNext(Coordinate direction);

    void nextWhile(Coordinate direction, Predicate<Coordinate> condition);

    void nextWhile(Coordinate direction, Predicate<Coordinate> condition, Consumer<Piece> function);

    void wholeBoard(Predicate<Coordinate> condition);

    void wholeBoard(Predicate<Coordinate> condition, Consumer<Piece> function);

    Piece to(Coordinate target);

}
