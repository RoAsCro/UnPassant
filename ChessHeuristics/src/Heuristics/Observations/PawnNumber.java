package Heuristics.Observations;

import Heuristics.BoardInterface;

public class PawnNumber extends AbstractObservation {

    private int blackPawns = 0;
    private int whitePawns = 0;

    @Override
    public void observe(BoardInterface board) {
        this.blackPawns = board.getBoardFacts().getCoordinates("black", "pawn").size();
        this.whitePawns = board.getBoardFacts().getCoordinates("white", "pawn").size();

    }

    public int getBlackPawns() {
        return blackPawns;
    }

    public int getWhitePawns() {
        return whitePawns;
    }
}
