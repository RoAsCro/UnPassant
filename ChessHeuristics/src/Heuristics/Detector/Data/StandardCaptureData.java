package Heuristics.Detector.Data;

import Heuristics.HeuristicsUtil;
import Heuristics.Path;

import static Heuristics.HeuristicsUtil.MAX_PIECES;

public class StandardCaptureData implements CaptureData {

    private static final int WHITE = 0;
    private static final int BLACK = 1;
    private Path[] nonPawnCaptures = new Path[]{new Path(), new Path()};
    private int[] pawnsCapturedByPawns = new int[]{0, 0};


    @Override
    public Path getNonPawnCaptures(boolean white) {
        return this.nonPawnCaptures[white ? WHITE : BLACK];
    }

    @Override
    public void setPawnsCapturedByPawns(boolean white, int pawnsCapturedByPawns) {
        this.pawnsCapturedByPawns[white ? WHITE : BLACK] = pawnsCapturedByPawns;

    }

    @Override
    public int getPawnsCapturedByPawns(boolean white) {
        return pawnsCapturedByPawns[white ? WHITE : BLACK];
    }

    @Override
    public int pawnTakeablePieces(boolean white) {
        return MAX_PIECES - getNonPawnCaptures(white)
                .stream().filter(c -> c.getY() != (white ? HeuristicsUtil.WHITE_PAWN_Y : HeuristicsUtil.BLACK_PAWN_Y))
                .toList()
                .size();
    }
}
