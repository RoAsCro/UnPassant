package Heuristics.Deductions;

import Heuristics.BoardInterface;
import Heuristics.Deduction;
import Heuristics.Observations.PawnNumber;
import Heuristics.Observations.PieceNumber;

import java.util.Arrays;
import java.util.List;

public class ImpossibleStateDetector {

    private static int MAX_PAWNS = 8;
    private static int MAX_PIECES = 16;


    private PawnNumber pawnNumber;
    private PieceNumber pieceNumber;

    private List<Deduction> deductions;

    public ImpossibleStateDetector(PawnNumber pawnNumber, PieceNumber pieceNumber, Deduction ... deductions) {
        this.pawnNumber = pawnNumber;
        this.pieceNumber = pieceNumber;
        this.deductions = Arrays.stream(deductions).toList();
    }

    public boolean testState(BoardInterface board) {
        this.pieceNumber.observe(board);
        if (this.pieceNumber.getBlackPieces() > MAX_PIECES || this.pieceNumber.getWhitePieces() > MAX_PIECES) {
            return false;
        }

        this.pawnNumber.observe(board);
        if (this.pawnNumber.getBlackPawns() > MAX_PAWNS || this.pawnNumber.getWhitePawns() > MAX_PAWNS) {
            return false;
        }

        for (Deduction deduction : this.deductions) {
            deduction.deduce(board);
            if (!deduction.getState()) {
                return false;
            }
        }


        return true;
    }

}
