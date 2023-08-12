package SolverShortcuts;

import StandardChess.Coordinate;

/**
 * A none-pawn piece untaken by a non-pawn on the greater board (1 < y < 6) can be untaken
 * anywhere else on the greater board by any other non-pawn with the same result
 */
public class GreaterBoardShortcut extends AbstractSolverShortcut {
    private String takenPiece = null;

    @Override
    public void set(Coordinate origin, Coordinate target, String movedPiece, String takenPiece, boolean promote, boolean whiteTurn) {
        if (test(origin, target, movedPiece, takenPiece, promote, whiteTurn)) {
            this.takenPiece = takenPiece;
        }
    }

    @Override
    public boolean match(Coordinate origin, Coordinate target, String movedPiece, String takenPiece, boolean promote, boolean whiteTurn) {
        if (test(origin, target, movedPiece, takenPiece, promote, whiteTurn)) {
            return takenPiece.equals(this.takenPiece);
        }
        return false;
    }

    @Override
    public boolean test(Coordinate origin, Coordinate target, String movedPiece, String takenPiece, boolean promote, boolean whiteTurn) {
        String pawn = "pawn";
        return !movedPiece.equals(pawn) && onGreaterBoard(origin) && !takenPiece.equals(pawn);
    }
}
