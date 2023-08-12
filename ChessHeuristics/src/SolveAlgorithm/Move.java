package SolveAlgorithm;

import StandardChess.Coordinate;

public record Move(
        String fen,
                    Coordinate origin,
                   StandardChess.Coordinate target,
                   String movedPiece,
                   String takenPiece,
                    boolean enPassant) {
}
