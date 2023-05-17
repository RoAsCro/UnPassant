package StandardChess.StandardPieces;

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
    public boolean tryMove(Coordinate origin, Coordinate target) {
        return false;
    }
}
