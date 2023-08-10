package Heuristics.Deductions;

import Heuristics.Observations.PawnNumber;
import Heuristics.Observations.PieceNumber;
import StandardChess.Coordinate;

import java.util.Map;

public class PawnMapWhite extends PawnMap{

    public PawnMapWhite(PawnMap pawnMap) {
        super(pawnMap);
    }
    public PawnMapWhite(PawnNumber pawnNumber, PieceNumber pieceNumber) {
        super(true, pawnNumber, pieceNumber);
    }


    @Override
    public void update() {
        super.update(true);
    }

    @Override
    public int capturedPieces() {
        return super.capturedPieces(true);
    }

    @Override
    public Map<Coordinate, Integer> getCaptureSet() {
        return super.getCaptureSet(true);
    }
}
