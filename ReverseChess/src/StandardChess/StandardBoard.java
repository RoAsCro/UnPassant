package StandardChess;

import java.util.Map;
import java.util.TreeMap;

public class StandardBoard implements ChessBoard{

    private final Piece[][] board = new Piece[LENGTH][LENGTH];

    private Map<String, Boolean> castlingRights = new TreeMap<>();
    private Coordinate enPassant = new Coordinate(-1, -1);
    private String turn = "white";

    public StandardBoard() {
        castlingRights.put("blackking", false);
        castlingRights.put("blackqueen", false);
        castlingRights.put("whiteking", false);
        castlingRights.put("whitequeen", false);
    }


    @Override
    public Piece at(Coordinate coordinate) {
        int x = coordinate.getX();
        int y = coordinate.getY();

        return x < LENGTH && x >= 0 && y < LENGTH && y >= 0
                ? this.board[coordinate.getX()][coordinate.getY()]
                : NullPiece.getInstance();
    }

    @Override
    public String getTurn() {
        return turn;
    }

    @Override
    public BoardReader getReader() {
        return new StandardBoardReader(this);
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
    public void remove(Coordinate coordinate) {
        this.board[coordinate.getX()][coordinate.getY()] = NullPiece.getInstance();
    }

    @Override
    public boolean canCastle(String pieceSide, String colour) {
        return this.castlingRights.get(colour.toLowerCase() + pieceSide.toLowerCase());
    }

    @Override
    public void setCastle(String pieceSide, String colour, boolean canCastle) {
        this.castlingRights.replace(colour.toLowerCase() + pieceSide.toLowerCase(), canCastle);
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
