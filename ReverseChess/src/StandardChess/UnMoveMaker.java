package StandardChess;

import StandardChess.*;

public class UnMoveMaker {
    private boolean captureFlag = false;
    private boolean enPassantFlag = false;
    private boolean promotionFlag = false;
    private boolean castleFlag = false;
    private boolean kRookFlag = false;
    private boolean qRookFlag = false;

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
        if(!Coordinates.inBounds(origin) || !Coordinates.inBounds(target)) {
            return false;
        }
        Piece piece = this.board.at(origin);
        Coordinate captureLocation = origin;
        if (!this.board.getTurn().equals(piece.getColour())) {
            System.out.println("turn");
            return false;
        }
        boolean isWhite = this.board.getTurn().equals("white");
        if (this.enPassantFlag) {
            captureLocation = new Coordinate(origin.getX(), origin.getY() - (isWhite ? 1 : -1));
            if (!this.board.at(captureLocation).getType().equals("null")
                    || !piece.getType().equals("pawn")) {

                return false;
            }
        } else if (this.promotionFlag ) {
            if (origin.getY() != (isWhite ? ChessBoard.LENGTH - 1 : 0) || piece.getType().equals("king")) {
                return false;
            }
            piece = StandardPieceFactory.getInstance().getPiece(isWhite ? "P" : "p");
        }
//        System.out.println("trying");
//        System.out.println(target);
//        System.out.println(origin);

        if (piece.tryUnMove(origin, target, this.board)) {
//            System.out.println("passing");

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
            if (this.enPassantFlag) {
//                System.out.println();
                this.board.setEnPassant(captureLocation);
            }
            if (this.captureFlag) {
                this.board.place(captureLocation, this.capturePiece);
            }
//            System.out.println(board.getReader().toFEN());
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

    public void setCastleFlag(boolean castleFlag) {
        this.castleFlag = castleFlag;
    }

    public void setkRookFlag(boolean kRookFlag) {
        this.kRookFlag = kRookFlag;
    }

    public void setqRookFlag(boolean qRookFlag) {
        this.qRookFlag = qRookFlag;
    }
}