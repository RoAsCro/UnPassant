import StandardChess.*;

public class UnMoveMaker {
    private boolean captureFlag = false;
    private boolean enPassantFlag = false;
    private boolean promotionFlag = false;
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
        if (!this.board.getTurn().equals(piece.getColour().toLowerCase().substring(0, 1))) {
            return false;
        }
        if (this.enPassantFlag) {
            captureLocation = new Coordinate(origin.getX(), origin.getY() - (this.board.getTurn().equals("w") ? 1 : -1));
            if (!this.board.at(captureLocation).getType().equals("null")
                    || !piece.getType().equals("pawn")) {
                return false;
            }
        } else if (this.promotionFlag ) {
            if (origin.getY() != (this.board.getTurn().equals("w") ? ChessBoard.LENGTH - 1 : 0) || piece.getType().equals("king")) {
                return false;
            }
            piece = StandardPieceFactory.getInstance().getPiece("p");
        }
        if (piece.tryUnMove(origin, target, this.board)) {
            int xDiff = origin.getX() - target.getX();
            if (!this.captureFlag && piece.getType().equals("pawn") && xDiff != 0) {
                return false;
            } else if (this.captureFlag && piece.getType().equals("pawn") && xDiff == 0) {
                return false;
            }
            else if (this.captureFlag && piece.getType().equals("king") && Math.abs(xDiff) == 2) {
                return false;
            }
            piece.updateBoard(origin, target, board, true);
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

    public void setPromotionFlag(boolean promotionFlag) {
        this.promotionFlag = promotionFlag;
    }
}
