package StandardChess.StandardPieces;

import StandardChess.ChessBoard;
import StandardChess.Piece;
import StandardChess.Coordinate;

public class StandardPiece implements Piece {

    private PieceStrategy strategy;
    private final String colour;

    public StandardPiece(String colour, PieceStrategy strategy) {
        this.strategy = strategy;
        this.colour = colour;
    }

    @Override
    public String getColour() {
        return this.colour;
    }

    @Override
    public String getType() {
        return this.strategy.getName();
    }

    @Override
    public boolean tryMove(Coordinate origin, Coordinate target, ChessBoard board) {
        return this.strategy.tryMove(origin, target, board);
    }
    @Override
    public boolean tryUnMove(Coordinate origin, Coordinate target, ChessBoard board) {
        return this.strategy.tryUnMove(origin, target, board);
    }

    @Override
    public void updateBoard(Coordinate origin, Coordinate target, ChessBoard board, boolean unMove) {
        this.strategy.updateBoard(origin, target, board, unMove);
    }

    public Coordinate[] getMoves(Coordinate origin){
        return this.strategy.getMoves(origin);
    }

}
