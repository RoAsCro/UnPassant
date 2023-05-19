package StandardChess;

import java.util.function.Consumer;
import java.util.function.Predicate;

public interface BoardReader {

    public String toFEN();

    public boolean inCheck(String colour);

    public Piece next(Coordinate direction);

    public boolean hasNext(Coordinate direction);

    public void nextWhile(Coordinate direction, Predicate<Coordinate> condition);

    public void nextWhile(Coordinate direction, Predicate<Coordinate> condition, Consumer<Piece> function);

    public Piece to(Coordinate target);

}
