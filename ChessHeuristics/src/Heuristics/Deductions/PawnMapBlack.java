package Heuristics.Deductions;

import Heuristics.Observations.PawnNumber;
import Heuristics.Observations.PieceNumber;

public class PawnMapBlack extends PawnMap{

    public PawnMapBlack(PawnMap pawnMap) {
        super(pawnMap);
    }
    public PawnMapBlack() {
        super(false);
    }

    @Override
    public void update() {
        super.update(false);
    }

    @Override
    public int capturedPieces() {
        return super.capturedPieces(false);
    }


}
