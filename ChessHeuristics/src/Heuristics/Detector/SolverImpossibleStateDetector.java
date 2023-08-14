package Heuristics.Detector;

import Heuristics.BoardInterface;
import Heuristics.Deduction;
import Heuristics.Deductions.UnCastle;
import Heuristics.Observations.PawnNumber;
import Heuristics.Observations.PieceNumber;
import StandardChess.Coordinate;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class SolverImpossibleStateDetector extends StandardStateDetector {


    public SolverImpossibleStateDetector(PawnNumber pawnNumber, PieceNumber pieceNumber, UnCastle unCastle, BoardInterface board, Deduction... deductions) {
        super(pawnNumber, pieceNumber, board, deductions);

    }


    public Map<String, List<Coordinate>> getPromotions() {
        return getPromotionNumbers()
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
