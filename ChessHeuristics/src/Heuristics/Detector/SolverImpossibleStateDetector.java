package Heuristics.Detector;

import Heuristics.BoardInterface;
import Heuristics.Deduction;
import Heuristics.Deductions.UnCastle;
import Heuristics.Detector.Data.CaptureData;
import Heuristics.Detector.Data.PawnData;
import Heuristics.Detector.Data.PieceData;
import Heuristics.Detector.Data.PromotionData;
import StandardChess.Coordinate;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class SolverImpossibleStateDetector extends StandardStateDetector {


    public SolverImpossibleStateDetector(PawnData pawnData, CaptureData captureData, PromotionData promotionData, PieceData pieceData, BoardInterface board, Deduction... deductions) {
        super(pawnData,captureData, promotionData, pieceData, board, deductions);

    }


    public Map<String, List<Coordinate>> getPromotions() {
        return getPromotionData().getPromotionNumbers()
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
