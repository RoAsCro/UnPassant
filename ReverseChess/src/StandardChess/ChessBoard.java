package StandardChess;

public interface ChessBoard {
    final static int LENGTH = 8;

    public Piece at(Coordinate coordinate);

    public String getTurn();

    public void setTurn(String turn);

    public void place(Coordinate coordinate, Piece piece);

    public boolean canCastle(String pieceSide, String colour);

    public void setCastle(String pieceSide, String colour, boolean canCastle);
//
//    public boolean canCastleWhiteKing();
//
//    public void setCastleWhiteKing(boolean castleWhiteKing);
//
//    public boolean canCastleWhiteQueen();
//
//    public void setCastleWhiteQueen(boolean castleWhiteQueen);
//
//    public boolean canCastleBlackKing();
//
//    public void setCastleBlackKing(boolean castleBlackKing);
//
//    public boolean canCastleBlackQueen();
//
//    public void setCastleBlackQueen(boolean castleBlackQueen);

    Coordinate getEnPassant();

    void setEnPassant(Coordinate coordinate);

}
