package StandardChess.StandardPieces;

import StandardChess.Coordinate;
import StandardChess.Path;

public abstract class AbstractStrategy implements PieceStrategy{

    public final static int BOARD_LENGTH = 8;
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

    @Override
    public boolean tryMove(Coordinate origin, Coordinate target) {
        //Check collision...
        return true;
    }

}
