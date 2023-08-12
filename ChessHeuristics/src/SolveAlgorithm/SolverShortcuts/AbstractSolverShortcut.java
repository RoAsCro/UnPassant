package SolveAlgorithm.SolverShortcuts;

import StandardChess.Coordinate;
import SolveAlgorithm.Move;

public abstract class AbstractSolverShortcut {
    protected static int FINAL_RANK_Y = 7;
    protected static int FIRST_RANK_Y = 0;
    protected static int BLACK_PAWN_Y = 6;
    protected static int WHITE_PAWN_Y = 1;
    protected static int BLACK_ESCAPE_Y = 5;
    protected static int WHITE_ESCAPE_Y = 2;
    public abstract boolean match(Move moveOne, Move moveTwo);
    public abstract boolean test(Move move);

    protected boolean onGreaterBoard(Coordinate c) {
        return c.getY() >= WHITE_ESCAPE_Y && c.getY() <= BLACK_ESCAPE_Y;
    }


}
