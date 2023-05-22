import StandardChess.ChessBoard;
import StandardChess.Coordinate;
import StandardChess.NullPiece;
import StandardChess.Piece;

public class UnMoveMaker {
    private boolean captureFlag = false;
    private boolean enPassantFlag = false;
    private Piece capturePiece = NullPiece.getInstance();

    private ChessBoard board;
    private static final Coordinate nullCoordinate = new Coordinate(-1, -1);

    public UnMoveMaker(ChessBoard board) {
        this.board = board;
    }

    public boolean makeUnMove(Coordinate origin, Coordinate target) {
        return makeUnMoveHelper(origin, target);
    }

    private boolean makeUnMoveHelper(Coordinate origin, Coordinate target) {
        Piece piece = this.board.at(origin);
        Coordinate captureLocation = origin;
        if (this.enPassantFlag) {
            captureLocation = new Coordinate(origin.getX(), origin.getY() - (this.board.getTurn().equals("w") ? 1 : -1));
            if (!this.board.at(captureLocation).getType().equals("null")) {
                return false;
            }
        }
        if (piece.tryUnMove(origin, target, this.board)) {
            if (!this.captureFlag && piece.getType().equals("pawn") && origin.getX() != target.getX()) {
                return false;
            }
            this.board.remove(origin);
            this.board.place(target, piece);
            if (this.captureFlag) {
                this.board.place(captureLocation, this.capturePiece);
            }
            return true;
        }
        return false;
    }

    public void setCaptureFlag(boolean captureFlag) {
        this.captureFlag = captureFlag;
    }

    public void setEnPassantFlag(boolean enPassantFlag) {
        this.enPassantFlag = enPassantFlag;
    }

    public void setCapturePiece(Piece piece) {
        this.capturePiece = piece;
    }
}
