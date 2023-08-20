package StandardChess;

/**
 * A class for making UnMoves on a ChessBoard.
 * @author Roland Crompton
 */
public class UnMoveMaker {
    /**A flag indicating the next un move is a capture*/
    private boolean captureFlag = false;
    /**A flag indicating the next un move is an en passant*/
    private boolean enPassantFlag = false;
    /**A flag indicating the next un move is an en promotion*/
    private boolean promotionFlag = false;
    /**The Piece being un captured*/
    private Piece capturePiece = NullPiece.getInstance();
    /**The ChessBoard the un moves are made on*/
    private final ChessBoard board;

    /**
     * Constructs a new UnMoveMaker.
     * @param board the board on which the un moves are made
     */
    public UnMoveMaker(ChessBoard board) {
        this.board = board;
    }

    /**
     * Makes an un move on the board with the given origin and coordinate. If the move is not legal,
     * the move is not made, and false returned.
     * @param origin the starting Coordinate for the un move
     * @param target the end Coordinate for the un move
     * @return whether the move was successful
     */
    public boolean makeUnMove(Coordinate origin, Coordinate target) {
        if(!Coordinates.inBounds(origin) || !Coordinates.inBounds(target)) {
            return false;
        }
        Piece piece = this.board.at(origin);
        if (!this.board.getTurn().equals(piece.getColour())) {
            return false;
        }
        boolean isWhite = this.board.getTurn().equals("white");
        Coordinate ePLocation = board.getEnPassant();
        if (!ePLocation.equals(Coordinates.NULL_COORDINATE) && (!ePLocation.equals(origin)
                || !(Math.abs(origin.getY() - target.getY()) == 2))) {
            System.out.println(ePLocation);
            return false;
        }
        Coordinate captureLocation = origin;
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
            if (!this.enPassantFlag) {
                this.board.setEnPassant(Coordinates.NULL_COORDINATE);
            }
            if (this.captureFlag) {
                this.board.place(captureLocation, this.capturePiece);
            }
            return true;
        }
        return false;
    }

    /**
     * Sets the capture flag, true if a capture is to be unmade, false otherwise.
     * @param captureFlag whether a capture is to be made
     */
    public void setCaptureFlag(boolean captureFlag) {
        this.captureFlag = captureFlag;
    }

    /**
     * Sets the en passant flag, true if an en passant is to be unmade, false otherwise.
     * @param enPassantFlag whether an en passant is to be un made
     */
    public void setEnPassantFlag(boolean enPassantFlag) {
        this.enPassantFlag = enPassantFlag;
    }

    /**
     * Sets what piece is to be un captured.
     * @param piece the Piece to be un captured
     */
    public void setCapturePiece(Piece piece) {
        this.capturePiece = piece;
    }

    /**
     * Sets the promotion flag, true if a promotion is to be unmade, false otherwise.
     * @param promotionFlag whether a promotion is to be un made
     */
    public void setPromotionFlag(boolean promotionFlag) {
        this.promotionFlag = promotionFlag;
    }

}
