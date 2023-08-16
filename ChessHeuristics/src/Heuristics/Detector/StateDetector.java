package Heuristics.Detector;

import Heuristics.BoardInterface;
import Heuristics.Detector.Data.CaptureData;
import Heuristics.Detector.Data.PawnData;
import Heuristics.Detector.Data.PieceData;
import Heuristics.Detector.Data.PromotionData;

public interface StateDetector {

    boolean getState();
    boolean testState();
    boolean testState(BoardInterface board);
    void reTest(BoardInterface boardInterface);
    String getErrorMessage();
    PawnData getPawnData();
    PieceData getPieceData();
    PromotionData getPromotionData();
    CaptureData getCaptureData();

}
