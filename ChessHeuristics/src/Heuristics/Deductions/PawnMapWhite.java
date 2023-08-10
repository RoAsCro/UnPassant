package Heuristics.Deductions;

import Heuristics.Observations.PawnNumber;
import Heuristics.Observations.PieceNumber;

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
}
