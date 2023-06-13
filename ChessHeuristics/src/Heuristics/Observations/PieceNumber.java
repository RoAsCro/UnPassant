package Heuristics.Observations;

import Heuristics.BoardInterface;

import java.util.List;

public class PieceNumber extends AbstractObservation{

    private static final List<String> PIECE_NAMES = List.of(
            "rook", "bishop", "queen", "knight", "king", "pawn"
    );

    private int whitePieces = 0;
    private int blackPieces = 0;

    @Override
    public void observe(BoardInterface board) {
        this.whitePieces = PIECE_NAMES.stream()
                .flatMap(s -> board.getBoardFacts().getCoordinates("white", s).stream())
                .toList()
                .size();
        this.blackPieces = PIECE_NAMES.stream()
                .flatMap(s -> board.getBoardFacts().getCoordinates("black", s).stream())
                .toList()
                .size();

    }
}
