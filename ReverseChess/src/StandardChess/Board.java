package StandardChess;

public class Board implements ChessBoard{

    private final static int LENGTH = 8;

    private final Piece[][] board = new Piece[LENGTH][LENGTH];

    private boolean castleBlackKing = false;
    private boolean castleBlackQueen = false;
    private boolean castleWhiteKing = false;
    private boolean castleWhiteQueen = false;
    private Coordinate enPassant = new Coordinate(-1, -1);
    private String turn = "white";

    @Override
    public Piece at(Coordinate coordinate) {
        return this.board[coordinate.getX()][coordinate.getY()];
    }

    @Override
    public String getTurn() {
        return turn;
    }

    @Override
    public void setTurn(String turn) {
        this.turn = turn;
    }

    @Override
    public void place(Coordinate coordinate, Piece piece) {
        this.board[coordinate.getX()][coordinate.getY()] = piece;
    }

    @Override
    public boolean canCastleWhiteKing() {
        return this.castleWhiteKing;
    }

    @Override
    public void setCastleWhiteKing(boolean castleWhiteKing) {
        this.castleWhiteKing = castleWhiteKing;
    }

    @Override
    public boolean canCastleWhiteQueen() {
        return this.castleWhiteQueen;
    }

    @Override
    public void setCastleWhiteQueen(boolean castleWhiteQueen) {
        this.castleWhiteQueen = castleWhiteQueen;
    }

    @Override
    public boolean canCastleBlackKing() {
        return this.castleBlackKing;
    }

    @Override
    public void setCastleBlackKing(boolean castleBlackKing) {
        this.castleBlackKing = castleBlackKing;
    }

    @Override
    public boolean canCastleBlackQueen() {
        return this.castleBlackQueen;
    }

    @Override
    public void setCastleBlackQueen(boolean castleBlackQueen) {
        this.castleBlackQueen = castleBlackQueen;
    }

    @Override
    public Coordinate getEnPassant() {
        return this.enPassant;
    }

    @Override
    public void setEnPassant(Coordinate coordinate) {
        this.enPassant = coordinate;
    }


}
