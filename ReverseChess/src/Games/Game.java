package Games;

import StandardChess.*;

import java.util.LinkedList;

public class Game {
    private UnMoveMaker unMoveMaker;
    private ChessBoard board;

    private final LinkedList<String> undoStates = new LinkedList<>();
    private final LinkedList<String> redoStates = new LinkedList<>();


    public Game(String fen) throws IllegalArgumentException {
        this.board = fen.equals("")
                ? BoardBuilder.buildBoard()
                : BoardBuilder.buildBoard(fen);;
        this.unMoveMaker = new UnMoveMaker(this.board);
    }

    public void setFEN(String fen) {
        if (trySetFEN(fen)) {
            this.redoStates.clear();
            this.undoStates.clear();
        }
    }

    private boolean trySetFEN(String fen) {
        try {
            this.board = BoardBuilder.buildBoard(fen);
            this.unMoveMaker = new UnMoveMaker(board);
            return true;
        } catch (IllegalArgumentException e) {
            // TODO
            return false;
        }
    }

    public String getFen() {
        return this.board.getReader().toFEN();
    }

    public String getTurn(){
        return board.getTurn();
    }

    public boolean makeMove(Coordinate origin, Coordinate target) {
        String state = this.board.getReader().toFEN();
        if (this.unMoveMaker.makeUnMove(origin, target)) {
            this.board.setTurn(this.board.getTurn().equals("white") ? "black" : "white");
            this.redoStates.clear();
            this.undoStates.add(state);
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

    public void redo() {
        if (!this.redoStates.isEmpty()) {
            this.undoStates.add(this.board.getReader().toFEN());
            trySetFEN((this.redoStates.pollLast()));
        }
    }

    public void undo() {
        if (!this.undoStates.isEmpty()) {
            this.redoStates.add(this.board.getReader().toFEN());
            trySetFEN(this.undoStates.pollLast());
        }
    }

}
