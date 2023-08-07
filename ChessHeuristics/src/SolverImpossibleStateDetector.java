import Heuristics.BoardInterface;
import Heuristics.Deduction;
import Heuristics.Deductions.TestImpossibleStateDetector;
import Heuristics.Observations.PawnNumber;
import Heuristics.Observations.PieceNumber;

public class SolverImpossibleStateDetector extends TestImpossibleStateDetector {

    private final BoardInterface boardInterface;

    public SolverImpossibleStateDetector(PawnNumber pawnNumber, PieceNumber pieceNumber, BoardInterface board, Deduction... deductions) {
        super(pawnNumber, pieceNumber, deductions);
        this.boardInterface = board;
    }

    public boolean testState() {
        return testState(this.boardInterface);
    }

}
