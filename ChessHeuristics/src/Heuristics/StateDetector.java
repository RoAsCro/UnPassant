package Heuristics;

import Heuristics.Observations.PawnNumber;
import Heuristics.Observations.PieceNumber;

public interface StateDetector {
    public PieceNumber getPieceNumber();

    PawnNumber getPawnNumber();

    int pawnTakeablePieces(boolean white);

    void setPawnTakeablePieces(boolean white, int subtrahend);
}
