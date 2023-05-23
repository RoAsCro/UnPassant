import StandardChess.*;

public class Game {
    private UnMoveMaker unMoveMaker;
    private ChessBoard board;

    public Game(String fen) {
        this.board = fen.equals("")
                ? BoardBuilder.buildBoard()
                : BoardBuilder.buildBoard(fen);;
        this.unMoveMaker = new UnMoveMaker(this.board);
    }

    public void setFEN(String fen) {
        this.board = BoardBuilder.buildBoard(fen);
        this.unMoveMaker = new UnMoveMaker(board);
    }

    public String getFen() {
        return this.board.getReader().toFEN();
    }

    public String getTurn(){
        return board.getTurn();
    }

    public boolean makeMove(Coordinate origin, Coordinate target) {
        if (this.unMoveMaker.makeUnMove(origin, target)) {
            this.board.setTurn(this.board.getTurn().equals("white") ? "black" : "white");
            return true;
        }
        return false;
    }

    public void setCaptureFlag(boolean captureFlag) {
        this.unMoveMaker.setCaptureFlag(captureFlag);
    }

    public void setEnPassantFlag(boolean enPassantFlag) {
        this.unMoveMaker.setEnPassantFlag(enPassantFlag);
    }

    public void setCapturePiece(Piece piece) {
        this.unMoveMaker.setCapturePiece(piece);
    }

    public void setPromotionFlag(boolean promotionFlag) {
        this.unMoveMaker.setPromotionFlag(promotionFlag);
    }

    public Piece at(Coordinate location) {
        return this.board.at(location);
    }

}
