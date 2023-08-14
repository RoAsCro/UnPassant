package SolveAlgorithm;

import Heuristics.BoardInterface;
import Heuristics.Deduction;
import Heuristics.Deductions.PieceMap;
import Heuristics.Deductions.TestImpossibleStateDetector;
import Heuristics.Observations.PawnNumber;
import Heuristics.Observations.PieceNumber;
import Heuristics.Deductions.UnCastle;
import StandardChess.Coordinate;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class SolverImpossibleStateDetector extends TestImpossibleStateDetector {

    private final BoardInterface boardInterface;
    private final UnCastle unCastle;

    public SolverImpossibleStateDetector(PawnNumber pawnNumber, PieceNumber pieceNumber, UnCastle unCastle, BoardInterface board, Deduction... deductions) {
        super(pawnNumber, pieceNumber, deductions);
        this.boardInterface = board;
        this.unCastle = unCastle;
        this.unCastle.registerStateDetector(this);
    }

    public boolean testState() {
        return testState(this.boardInterface);
    }

    public boolean canCastle(boolean white) {
        return !unCastle.hasMoved().get(white ? 0 : 1)[0];
    }

    public Map<String, List<Coordinate>> getPromotions() {
        return ((PieceMap) getDeductions().get(StateDetectorFactory.pmPosition)).getPromotionNumbers()
                .entrySet().stream()
                .filter(entry -> !entry.getValue().isEmpty())
                .collect(Collectors.toMap(Map.Entry::getKey, (k) -> k.getValue().entrySet()
                        .stream()
                        .filter(entry -> entry.getKey() != null)
                        .flatMap(en -> en.getKey().stream()).toList()));
//                )
//                .toList()));
    }

}
