package StandardChess.StandardPieces;

import StandardChess.Coordinate;

public interface PieceStrategy {

    public String getName();
    public boolean tryMove(Coordinate origin, Coordinate target);
    public Coordinate[] getMoves(Coordinate origin);

}
