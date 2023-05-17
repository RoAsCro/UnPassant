package StandardChess.StandardPieces;

import StandardChess.Coordinate;

public class BishopStrategy extends CollisableStrategy{


    public BishopStrategy() {
        super("bishop", BOARD_LENGTH);
    }

    @Override
    public boolean tryMove(Coordinate origin, Coordinate target) {
        return Math.abs(origin.getX() - target.getX()) == Math.abs(origin.getY() - target.getY())
                && super.tryMove(origin, target);
    }

    @Override
    public Coordinate[] getMoves(Coordinate origin) {
        int xPlus = origin.getX() + 1;
        int xMinus = origin.getX() - 1;
        int yPlus = origin.getY() + 1;
        int yMinus = origin.getY() - 1;
        return new Coordinate[]{new Coordinate(xPlus, yPlus),
                new Coordinate(xMinus, yMinus),
                new Coordinate(xMinus, yPlus),
                new Coordinate(xPlus, yMinus)};
    }

}
