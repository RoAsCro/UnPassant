package StandardChess;

public interface Piece {

    String getColour();
    String getType();
    boolean tryMove(Coordinate origin, Coordinate target, ChessBoard board);
    boolean tryUnMove(Coordinate origin, Coordinate target, ChessBoard board);
    void updateBoard(Coordinate origin, Coordinate target, ChessBoard board, boolean unMove);

    public Coordinate[] getMoves(Coordinate origin);



}
