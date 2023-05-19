package StandardChess;

public interface ChessBoard {
    final static int LENGTH = 8;

    public Piece at(Coordinate coordinate);

    public String getTurn();

    public BoardReader getReader();

    public void setTurn(String turn);

    public void place(Coordinate coordinate, Piece piece);

    public void remove(Coordinate coordinate);

    public boolean canCastle(String pieceSide, String colour);

    public void setCastle(String pieceSide, String colour, boolean canCastle);

    Coordinate getEnPassant();

    void setEnPassant(Coordinate coordinate);

}
