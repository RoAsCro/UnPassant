package StandardChess;

public class Board {

    private final static int LENGTH = 8;

    private final Piece[][] board = new Piece[LENGTH][LENGTH];

    public Piece at(Coordinate coordinate) {
        return this.board[coordinate.getX()][coordinate.getY()];
    }

    public void place(Coordinate coordinate, Piece piece) {
        this.board[coordinate.getX()][coordinate.getY()] = piece;
    }

}
