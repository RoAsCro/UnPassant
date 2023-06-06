package Heuristics.checks;

import Heuristics.BoardInterface;
import Heuristics.Check;

public class TooManyPiecesCheck implements Check {
    @Override
    public Boolean check(BoardInterface boardInterface) {
        System.out.println(boardInterface.getWhitePieceNumber());
        return boardInterface.getBlackPieceNumber() <= BoardInterface.MAX_PIECE_NUMBER
                && boardInterface.getWhitePieceNumber() <= BoardInterface.MAX_PIECE_NUMBER;
    }
}
