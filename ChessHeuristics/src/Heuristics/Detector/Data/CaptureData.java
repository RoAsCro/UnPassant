package Heuristics.Detector.Data;

import Heuristics.Path;

public interface CaptureData {
    Path getNonPawnCaptures(boolean white);
    void setPawnsCapturedByPawns(boolean white, int pawnsCapturedByPawns);
    int getPawnsCapturedByPawns(boolean white);
    int pawnTakeablePieces(boolean white);
}
