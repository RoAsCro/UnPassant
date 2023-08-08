package StandardChess.StandardPieces;

import StandardChess.ChessBoard;
import StandardChess.Coordinate;

public interface PieceStrategy {

    String getName();
    boolean tryMove(Coordinate origin, Coordinate target, ChessBoard board);
    boolean tryUnMove(Coordinate origin, Coordinate target, ChessBoard board);
    void updateBoard(Coordinate origin, Coordinate target, ChessBoard board, boolean unMove);

    Coordinate[] getMoves(Coordinate origin, String colour);
    Coordinate[] getUnMoves(Coordinate origin, String colour);

}
