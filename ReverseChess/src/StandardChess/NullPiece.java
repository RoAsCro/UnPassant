package StandardChess;

public class NullPiece implements Piece{

    private static Piece instance;

    private NullPiece(){}

    public static Piece getInstance() {
        return instance != null
                ? instance
                : (instance = new NullPiece());
    }

    @Override
    public String getColour() {
        return "null";
    }

    @Override
    public String getType() {
        return "null";
    }

    @Override
    public boolean tryMove(Coordinate origin, Coordinate target, ChessBoard board) {
        return false;
    }

    @Override
    public boolean tryUnMove(Coordinate origin, Coordinate target, ChessBoard board) {
        return false;
    }

    @Override
    public void updateBoard(Coordinate origin, Coordinate target, ChessBoard board, boolean unMove) {

    }

    @Override
    public Coordinate[] getMoves(Coordinate origin) {
        return new Coordinate[0];
    }

    @Override
    public Coordinate[] getUnMoves(Coordinate origin) {
        return new Coordinate[0];
    }
}
