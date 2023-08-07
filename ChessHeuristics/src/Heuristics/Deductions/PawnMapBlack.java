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
        super("black", pawnNumber, pieceNumber);
    }

    @Override
    public void update() {
        super.update("black");
    }

    @Override
    public int capturedPieces() {
        return super.capturedPieces("black");
    }

    @Override
    public Map<Coordinate, Integer> getCaptureSet() {
        return super.getCaptureSet("black");
    }
}
