package Heuristics.Deductions;

import Heuristics.BoardInterface;
import Heuristics.Deduction;
import Heuristics.Observations.PawnNumber;
import Heuristics.Observations.PieceNumber;
import Heuristics.StateDetector;

import java.util.Arrays;
import java.util.List;

public class TestImpossibleStateDetector implements StateDetector {

    private static final int MAX_PAWNS = 8;
    private static final int MAX_PIECES = 16;

    private static final int WHITE = 0;
    private static final int BLACK = 1;


    private final PawnNumber pawnNumber;
    private final PieceNumber pieceNumber;

    private int[] piecesTakableByPawns = new int[]{MAX_PIECES, MAX_PIECES}

    private List<Deduction> deductions;

    public TestImpossibleStateDetector(PawnNumber pawnNumber, PieceNumber pieceNumber, Deduction ... deductions) {
        this.pawnNumber = pawnNumber;
        this.pieceNumber = pieceNumber;
        this.deductions = Arrays.stream(deductions).toList();
        this.deductions.forEach(d -> d.registerDetector(this));
    }



    public boolean testState(BoardInterface board) {

        this.pieceNumber.observe(board);
        if (board.inCheck(board.getTurn().equals("white") ? "black" : "white")) {
//            System.out.println("Eh1");

            return false;
        }
        if (this.pieceNumber.getBlackPieces() > MAX_PIECES || this.pieceNumber.getWhitePieces() > MAX_PIECES) {
//            System.out.println("Eh2");

            return false;
        }

        this.pawnNumber.observe(board);
        if (this.pawnNumber.getBlackPawns() > MAX_PAWNS || this.pawnNumber.getWhitePawns() > MAX_PAWNS) {
//            System.out.println("Eh3");

            return false;
        }

        for (Deduction deduction : this.deductions) {


            deduction.deduce(board);
            if (!deduction.getState()) {
//                System.out.println(deduction);

                return false;
            }
        }
        return true;
    }

    public List<Deduction> getDeductions() {
        return this.deductions;
    }

    public PawnNumber getPawnNumber() {
        return this.pawnNumber;
    }

    @Override
    public int pawnTakeablePieces(boolean white) {
        return this.piecesTakableByPawns[white ? WHITE : BLACK];
    }

    @Override
    public void setPawnTakeablePieces(boolean white, int subtrahend) {
        this.piecesTakableByPawns[white ? WHITE : BLACK] -= subtrahend;
    }

    public PieceNumber getPieceNumber() {
        return this.pieceNumber;
    }
}
