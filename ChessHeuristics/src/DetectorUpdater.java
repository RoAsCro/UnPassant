import Heuristics.Deduction;
import Heuristics.Deductions.CombinedPawnMap;
import Heuristics.Deductions.PawnMap;
import Heuristics.Deductions.PawnMapBlack;
import Heuristics.Deductions.PawnMapWhite;
import Heuristics.Path;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

public class DetectorUpdater {


    public static SolverImpossibleStateDetector update(String move, SolverImpossibleStateDetector detector) {


        // if move doesn't change pawn map
        PawnMap pmw = null;
        PawnMap pmb = null;
        CombinedPawnMap cpm = null;
        for (Deduction d : detector.getDeductions()) {
            if (d instanceof PawnMapWhite p) {
                pmw = new PawnMapWhite(p);
            } else if (d instanceof PawnMapBlack p) {
                pmb = new PawnMapBlack(p);
            } else if (d instanceof CombinedPawnMap p) {
                cpm = p;
            }
        }
        cpm = new CombinedPawnMap(pmw, pmb, cpm);
        String[] splitFen = move.split(" ");
        String justMove = move.split(":")[1];

        boolean movedPiece = justMove.charAt(0) == 'P';
        boolean takenPiece = justMove.charAt(4) == 'P';
        List<Deduction> deductionList = new LinkedList<>();
        if (movedPiece || takenPiece) {
            boolean white = splitFen[1].charAt(0) == 'w';
            if(movedPiece && !takenPiece) {
                if (white) {
                    deductionList.add(pmw);
                } else {
                    deductionList.add(pmb);
                }
            } else if (!movedPiece) {
                if (white) {
                    deductionList.add(pmb);
                } else {
                    deductionList.add(pmw);
                }
            }
        } else {
            deductionList.add(pmw);
            deductionList.add(pmb);
            deductionList.add(cpm);
        }

        return StateDetectorFactory.getDetector(move.split(":")[0], deductionList.toArray(new Deduction[0]));

    }

}
