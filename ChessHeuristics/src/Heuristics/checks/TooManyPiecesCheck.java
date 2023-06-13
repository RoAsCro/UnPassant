package Heuristics.checks;

import Heuristics.BoardInterface;
import Heuristics.Check;

import java.util.List;

public class TooManyPiecesCheck implements Check {

    private static final List<String> PIECE_NAMES = List.of(
            "rook", "bishop", "queen", "knight", "king", "pawn"
    );
    @Override
    public Boolean check(BoardInterface boardInterface) {
        int whitePieceNumber = PIECE_NAMES.stream()
                .flatMap(s -> boardInterface.getBoardFacts().getCoordinates("white", s).stream())
                .toList()
                .size();
        int blackPieceNumber = PIECE_NAMES.stream()
                .flatMap(s -> boardInterface.getBoardFacts().getCoordinates("black", s).stream())
                .toList()
                .size();

        return whitePieceNumber <= BoardInterface.MAX_PIECE_NUMBER
                && blackPieceNumber <= BoardInterface.MAX_PIECE_NUMBER;
    }
}
