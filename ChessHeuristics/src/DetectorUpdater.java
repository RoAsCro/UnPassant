import Heuristics.Deduction;
import Heuristics.Deductions.CombinedPawnMap;
import Heuristics.Deductions.PawnMap;
import Heuristics.Deductions.PawnMapBlack;
import Heuristics.Deductions.PawnMapWhite;

public class DetectorUpdater {


    public static void update(String move, SolverImpossibleStateDetector detector) {


        // if move doesn't change pawn map
        PawnMap pmw = null;
        PawnMap pmb = null;
        CombinedPawnMap cpm = null;
        for (Deduction d : detector.getDeductions()) {
            if (d instanceof PawnMapWhite p) {
                pmw = new PawnMapWhite(p);
            } else if (d instanceof PawnMapBlack p) {
                pmb = new PawnMapWhite(p);
            } else if (d instanceof CombinedPawnMap p) {
                cpm = p;
            }
        }
        cpm = new CombinedPawnMap(pmw, pmb, cpm);

    }

}
