import Heuristics.BoardInterface;
import Heuristics.Deduction;
import Heuristics.Deductions.TestImpossibleStateDetector;
import Heuristics.Observations.PawnNumber;
import Heuristics.Observations.PieceNumber;
import Heuristics.UnCastle;

public class SolverImpossibleStateDetector extends TestImpossibleStateDetector {

    private final BoardInterface boardInterface;
    private final UnCastle unCastle;

    public SolverImpossibleStateDetector(PawnNumber pawnNumber, PieceNumber pieceNumber, UnCastle unCastle, BoardInterface board, Deduction... deductions) {
        super(pawnNumber, pieceNumber, deductions);
        this.boardInterface = board;
        this.unCastle = unCastle;
    }

    public boolean testState() {
        return testState(this.boardInterface);
    }

    public boolean canCastle(boolean white) {
        return !unCastle.hasMoved().get(white ? 0 : 1)[0];
    }

}
