package SolverShortcuts;

import StandardChess.Coordinate;

public abstract class AbstractSolverShortcut {
    protected static int FINAL_RANK_Y = 7;
    protected static int FIRST_RANK_Y = 0;
    protected static int BLACK_PAWN_Y = 6;
    protected static int WHITE_PAWN_Y = 1;
    protected static int BLACK_ESCAPE_Y = 5;
    protected static int WHITE_ESCAPE_Y = 2;
    public abstract void set(Coordinate origin, Coordinate target, String movedPiece, String takenPiece, boolean promote, boolean whiteTurn);
    public abstract boolean match(Coordinate origin, Coordinate target, String movedPiece, String takenPiece, boolean promote, boolean whiteTurn);
    public abstract boolean test(Coordinate origin, Coordinate target, String movedPiece, String takenPiece, boolean promote, boolean whiteTurn);

    protected boolean onGreaterBoard(Coordinate c) {
        return c.getY() >= WHITE_ESCAPE_Y && c.getY() <= BLACK_ESCAPE_Y;
    }


}
