package StandardChess;

public class MoveMaker {

    private ChessBoard board;

    public MoveMaker(ChessBoard board) {
        this.board = board;
    }

    public boolean makeMove(Coordinate origin, Coordinate target) {
        Piece piece = this.board.at(origin);
        if (piece.tryMove(origin, target, this.board)) {
            this.board.place(target, piece);
            this.board.remove(origin);
            return true;
        }
        return false;
    }

}
