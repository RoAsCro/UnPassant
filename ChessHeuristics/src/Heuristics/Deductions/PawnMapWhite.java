package Heuristics.Deductions;

import Heuristics.BoardInterface;
import StandardChess.Coordinate;

import java.util.Map;

public class PawnMapWhite extends PawnMap{
    public PawnMapWhite() {
        super("white");
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
