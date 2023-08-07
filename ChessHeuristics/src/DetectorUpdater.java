import Heuristics.Deduction;
import Heuristics.Deductions.CombinedPawnMap;
import Heuristics.Deductions.PawnMap;
import Heuristics.Deductions.PawnMapBlack;
import Heuristics.Deductions.PawnMapWhite;
import Heuristics.Path;
import StandardChess.ChessBoard;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

public class DetectorUpdater {


    public static SolverImpossibleStateDetector update(ChessBoard board, String move, SolverImpossibleStateDetector detector) {


        // if move doesn't change pawn map
        PawnMapWhite pmw = null;
        PawnMapBlack pmb = null;
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

        List<Deduction> deductionList = new LinkedList<>();

        String[] splitFen = move.split(" ");
        String[] splitMove = move.split(":");
        String justMove = splitMove[1];
        if (!(justMove.length() < 4)) {
            boolean takenPiece = justMove.charAt(3) == 'x';
//            if (!takenPiece) {

                boolean movedPiece = justMove.charAt(0) == 'P';
            boolean white = splitFen[1].charAt(0) == 'w';
            if (!(takenPiece && movedPiece)) {
                if (takenPiece) {
                    if (!(justMove.charAt(4) == 'P')) {
                        if (white) {
                            deductionList.add(pmw);
                        } else {
                            deductionList.add(pmb);
                        }
                    }

                }
                else if (movedPiece) {
//                System.out.println("X");
                    //                    System.out.println("S");
                    if (white) {
                        deductionList.add(pmw);
                    } else {
                        deductionList.add(pmb);
                    }
                }else {
                    deductionList.add(pmw);
                    deductionList.add(pmb);
                    deductionList.add(cpm);
                }


            }

//            }
        }
//        System.out.println(pmw.getPawnOrigins());

//        System.out.println(deductionList);
        return StateDetectorFactory.getDetector(board, deductionList.toArray(new Deduction[0]));

    }

}
