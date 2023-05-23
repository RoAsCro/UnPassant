import StandardChess.ChessBoard;
import StandardChess.Coordinate;
import StandardChess.Piece;

public class MoveMaker {

    private ChessBoard board;

    public MoveMaker(ChessBoard board) {
        this.board = board;
    }

    public boolean makeMove(Coordinate origin, Coordinate target) {
        Piece piece = this.board.at(origin);
        if (piece.tryMove(origin, target, this.board)) {
            piece.updateBoard(origin, target, this.board, false);
            this.board.place(target, piece);
            this.board.remove(origin);
            return true;
        }
        return false;
    }

}