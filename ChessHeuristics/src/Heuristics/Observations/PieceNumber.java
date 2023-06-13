package Heuristics.Observations;

import Heuristics.BoardInterface;
import Heuristics.HeuristicsUtil;

public class PieceNumber extends AbstractObservation{

    private int whitePieces = 0;
    private int blackPieces = 0;

    @Override
    public void observe(BoardInterface board) {
        this.whitePieces = HeuristicsUtil.PIECE_NAMES.stream()
                .flatMap(s -> board.getBoardFacts().getCoordinates("white", s).stream())
                .toList()
                .size();
        this.blackPieces = HeuristicsUtil.PIECE_NAMES.stream()
                .flatMap(s -> board.getBoardFacts().getCoordinates("black", s).stream())
                .toList()
                .size();

    }
}
