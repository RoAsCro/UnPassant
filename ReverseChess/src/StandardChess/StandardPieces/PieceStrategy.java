package StandardChess.StandardPieces;

import StandardChess.ChessBoard;
import StandardChess.Coordinate;

public interface PieceStrategy {

    public String getName();
    public boolean tryMove(Coordinate origin, Coordinate target, ChessBoard board);
    public Coordinate[] getMoves(Coordinate origin);

}
