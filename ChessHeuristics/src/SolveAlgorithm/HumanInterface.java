package SolveAlgorithm;
import Games.Game;
import Heuristics.Detector.DetectorInterface;
import Heuristics.Detector.StateDetectorFactory;

public class HumanInterface extends Game {
    static String fen = "B1b1kb2/1p1pp1p1/8/3B4/5b2/3Q4/1P1PP1P1/2B1KB1b w - - 0 1";

    public static void main(String[] args) {
//        while (true) {
            HumanInterface humanInterface;
            humanInterface = new HumanInterface(fen);

            System.out.println("Current FEN:" + humanInterface.getFen());
            DetectorInterface detector = StateDetectorFactory.getDetectorInterface(fen);
            detector.testState();
            System.out.println(detector);
//        }
    }
    public HumanInterface(String fen) {
        super(fen);
    }

    private void makeBoard(String fen){

    }
}
