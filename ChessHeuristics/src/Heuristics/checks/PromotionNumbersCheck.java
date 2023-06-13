package Heuristics.checks;

import Heuristics.BoardInterface;
import Heuristics.Check;

import java.util.Map;

public abstract class PromotionNumbersCheck implements Check {

    private static final Map<String, Integer> PIECE_NUMBERS = Map.of(
            "rook", 2, "bishop", 2, "queen", 1, "knight", 2
    );

    public Boolean check(BoardInterface boardInterface, boolean white) {
////        int pieceNumber = white ? boardInterface.getWhitePieceNumber() : boardInterface.getBlackPieceNumber();
//        int pawnNumber = white ? boardInterface.getWhitePawnNumber() : boardInterface.getBlackPawnNumber();
////        Map<String, Integer> pieces = white ? boardInterface.getWhitePieces() : boardInterface.getBlackPieces();
//        boolean eightPawns = pawnNumber == BoardInterface.MAX_PAWN_NUMBER;
////        boolean greaterThanEightNonPawns = pieceNumber - pawnNumber > BoardInterface.MAX_PAWN_NUMBER;
//        boolean greaterThanStartingNumber = false;
//        for (String piece : PIECE_NUMBERS.keySet()) {
////            if (pieces.get(piece) > PIECE_NUMBERS.get(piece)) {
////                greaterThanStartingNumber = true;
////            }
//        }
//
//        if (eightPawns && greaterThanStartingNumber) {
//            return false;
//        }
        return true;
    }

    private void makeTrue() {
    }
}
