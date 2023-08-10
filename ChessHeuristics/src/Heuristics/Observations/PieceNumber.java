package Heuristics.Observations;

import Heuristics.BoardInterface;
import Heuristics.HeuristicsUtil;

public class PieceNumber extends AbstractObservation{

    private int whitePieces = 0;
    private int blackPieces = 0;

    @Override
    public void observe(BoardInterface board) {
        this.whitePieces = HeuristicsUtil.PIECE_NAMES.stream()
                .flatMap(s -> board.getBoardFacts().getCoordinates(true, s).stream())
                .toList()
                .size();
        this.blackPieces = HeuristicsUtil.PIECE_NAMES.stream()
                .flatMap(s -> board.getBoardFacts().getCoordinates(false, s).stream())
                .toList()
                .size();

    }

    public int getBlackPieces() {
        return blackPieces;
    }

    public int getWhitePieces() {
        return whitePieces;
    }
}
