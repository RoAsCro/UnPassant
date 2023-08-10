package Heuristics.Deductions;

import Heuristics.BoardInterface;
import Heuristics.Observations.PawnNumber;
import Heuristics.Observations.PieceNumber;
import StandardChess.Coordinate;

import java.util.Map;

public class PawnMapBlack extends PawnMap{

    public PawnMapBlack(PawnMap pawnMap) {
        super(pawnMap);
    }
    public PawnMapBlack(PawnNumber pawnNumber, PieceNumber pieceNumber) {
        super(false, pawnNumber, pieceNumber);
    }

    @Override
    public void update() {
        super.update(false);
    }

    @Override
    public int capturedPieces() {
        return super.capturedPieces(false);
    }

    @Override
    public Map<Coordinate, Integer> getCaptureSet() {
        return super.getCaptureSet(false);
    }
}
