package StandardChess;

public interface ChessBoard {
    public Piece at(Coordinate coordinate);

    public String getTurn();

    public void setTurn(String turn);

    public void place(Coordinate coordinate, Piece piece);

    public boolean canCastleWhiteKing();

    public void setCastleWhiteKing(boolean castleWhiteKing);

    public boolean canCastleWhiteQueen();

    public void setCastleWhiteQueen(boolean castleWhiteQueen);

    public boolean canCastleBlackKing();

    public void setCastleBlackKing(boolean castleBlackKing);

    public boolean canCastleBlackQueen();

    public void setCastleBlackQueen(boolean castleBlackQueen);

    Coordinate getEnPassant();

    void setEnPassant(Coordinate coordinate);

}
