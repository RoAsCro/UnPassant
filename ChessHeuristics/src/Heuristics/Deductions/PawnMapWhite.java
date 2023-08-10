package Heuristics.Deductions;

import Heuristics.BoardInterface;
import Heuristics.Observations.PawnNumber;
import Heuristics.Observations.PieceNumber;
import StandardChess.Coordinate;

import java.util.Map;

public class PawnMapWhite extends PawnMap{

    public PawnMapWhite(PawnMap pawnMap) {
        super(pawnMap);
    }
    public PawnMapWhite(PawnNumber pawnNumber, PieceNumber pieceNumber) {
        super("white", pawnNumber, pieceNumber);
        this.maxPieces = 15;
    }


    @Override
    public void update() {
        super.update("white");
    }

    @Override
    public int capturedPieces() {
        return super.capturedPieces("white");
    }

    @Override
    public Map<Coordinate, Integer> getCaptureSet() {
        return super.getCaptureSet("white");
    }
}
