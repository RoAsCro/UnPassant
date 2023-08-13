package Heuristics.Deductions;

import Heuristics.BoardInterface;
import Heuristics.Deduction;
import Heuristics.Observations.PawnNumber;
import Heuristics.Observations.PieceNumber;
import Heuristics.Path;
import Heuristics.StateDetector;
import StandardChess.Coordinate;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class TestImpossibleStateDetector implements StateDetector {

    private static final int MAX_PAWNS = 8;
    private static final int MAX_PIECES = 16;

    private static final int WHITE = 0;
    private static final int BLACK = 1;


    private final PawnNumber pawnNumber;
    private final PieceNumber pieceNumber;

    //PawnMap stuff
    private int[] piecesTakableByPawns = new int[]{MAX_PIECES, MAX_PIECES};
    private final Map<Coordinate, Integer>[] captureSet = new Map[]{new TreeMap<Coordinate, Path>(), new TreeMap<Coordinate, Path>()};
    private Map<Coordinate, Path>[] pawnOrigins = new Map[]{new TreeMap<Coordinate, Path>(), new TreeMap<Coordinate, Path>()};
    private List<Deduction> deductions;
    private Map<Coordinate, Boolean>[] originFree = new Map[]{new TreeMap<Coordinate, Boolean>(), new TreeMap<Coordinate, Boolean>()};
    private int[] capturedPieces = new int[]{0, 0};

    public TestImpossibleStateDetector(PawnNumber pawnNumber, PieceNumber pieceNumber, Deduction ... deductions) {
        this.pawnNumber = pawnNumber;
        this.pieceNumber = pieceNumber;
        this.deductions = Arrays.stream(deductions).toList();
        this.deductions.forEach(d -> d.registerDetector(this));
        for (int i = 0; i < 8 ; i++ ) {
            originFree[WHITE].put(new Coordinate(i, 1), true);
            originFree[BLACK].put(new Coordinate(i, 6), true);
        }
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
            System.out.println("X");


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
    public PieceNumber getPieceNumber() {
        return this.pieceNumber;
    }

    // Pawn Map Stuff
    @Override
    public int pawnTakeablePieces(boolean white) {
        return this.piecesTakableByPawns[white ? WHITE : BLACK];
    }

    @Override
    public void setPawnTakeablePieces(boolean white, int subtrahend) {
        this.piecesTakableByPawns[white ? WHITE : BLACK] -= subtrahend;
    }

    @Override
    public Map<Coordinate, Integer> getCaptureSet(boolean white) {
        return this.captureSet[white ? WHITE : BLACK];
    }

    @Override
    public Map<Coordinate, Path> getPawnOrigins(boolean white) {
        return pawnOrigins[white ? WHITE : BLACK];
    }
    @Override
    public Map<Coordinate, Boolean> getOriginFree(boolean white) {
        return this.originFree[white ? WHITE : BLACK];
    }

    @Override
    public int getCapturedPieces(boolean white) {
        return capturedPieces[white ? WHITE : BLACK];
    }

    @Override
    public void setCapturedPieces(boolean white, int capturedPieces) {
        this.capturedPieces[white ? WHITE : BLACK] = capturedPieces;
    }
}
