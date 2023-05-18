package StandardChess.StandardPieces;

import StandardChess.ChessBoard;
import StandardChess.Coordinate;

public class BishopStrategy extends AbstractStrategy{


    public BishopStrategy() {
        super("bishop");
    }


    @Override
    public boolean tryMove(Coordinate origin, Coordinate target, ChessBoard board) {
        return DIAGONAL.test(origin, target) && super.tryMove(origin, target, board);
    }

    @Override
    public boolean tryUnMove(Coordinate origin, Coordinate target, ChessBoard board) {
        return DIAGONAL.test(origin, target) && super.tryUnMove(origin, target, board);
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
