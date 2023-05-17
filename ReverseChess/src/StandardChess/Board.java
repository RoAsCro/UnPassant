package StandardChess;

public class Board {

    private final static int LENGTH = 8;

    private final Piece[][] board = new Piece[LENGTH][LENGTH];

    private boolean castleBlackKing = false;
    private boolean castleBlackQueen = false;
    private boolean castleWhiteKing = false;
    private boolean castleWhiteQueen = false;
    private String turn = "white";


    public Piece at(Coordinate coordinate) {
        return this.board[coordinate.getX()][coordinate.getY()];
    }

    public String getTurn() {
        return turn;
    }

    public void setTurn(String turn) {
        this.turn = turn;
    }

    public void place(Coordinate coordinate, Piece piece) {
        this.board[coordinate.getX()][coordinate.getY()] = piece;
    }


    public boolean canCastleWhiteKing() {
        return this.castleWhiteKing;
    }

    public void setCastleWhiteKing(boolean castleWhiteKing) {
        this.castleWhiteKing = castleWhiteKing;
    }

    public boolean canCastleWhiteQueen() {
        return this.castleWhiteQueen;
    }

    public void setCastleWhiteQueen(boolean castleWhiteQueen) {
        this.castleWhiteQueen = castleWhiteQueen;
    }

    public boolean canCastleBlackKing() {
        return this.castleBlackKing;
    }

    public void setCastleBlackKing(boolean castleBlackKing) {
        this.castleBlackKing = castleBlackKing;
    }

    public boolean canCastleBlackQueen() {
        return this.castleBlackQueen;
    }

    public void setCastleBlackQueen(boolean castleBlackQueen) {
        this.castleBlackQueen = castleBlackQueen;
    }

}
