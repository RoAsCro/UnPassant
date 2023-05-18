package StandardChess.StandardPieces;

import StandardChess.ChessBoard;
import StandardChess.Coordinate;

public interface PieceStrategy {

    String getName();
    boolean tryMove(Coordinate origin, Coordinate target, ChessBoard board);
    boolean tryUnMove(Coordinate origin, Coordinate target, ChessBoard board);
    Coordinate[] getMoves(Coordinate origin);

}
