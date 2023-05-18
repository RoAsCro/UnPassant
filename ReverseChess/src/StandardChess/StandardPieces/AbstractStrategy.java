package StandardChess.StandardPieces;

import StandardChess.Coordinate;

import java.util.function.BiPredicate;

public abstract class AbstractStrategy implements PieceStrategy{

    protected final static int BOARD_LENGTH = 8;
    protected final static BiPredicate<Coordinate, Coordinate> DIAGONAL =
            (c, d) -> Math.abs(c.getX() - d.getX())
                    == Math.abs(c.getY() - d.getY());
    protected final static BiPredicate<Coordinate, Coordinate> PERPENDICULAR =
            (c, d) -> c.getX() == d.getX() || c.getY() == d.getY();

    private int speed;
    private String name;

    public AbstractStrategy(String name, int speed) {
        this.name = name;
        this.speed = speed;
    }

    @Override
    public String getName() {
        return this.name;
    }


}
