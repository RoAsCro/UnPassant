import Heuristics.Detector.DetectorInterface;
import Heuristics.Detector.StateDetectorFactory;
import Heuristics.Path;
import StandardChess.Coordinate;
import StandardChess.Coordinates;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;

public class StandardDetectorInterfaceTest {

    @Test
    public void cagesReadOnly() {
        DetectorInterface detectorInterface = StateDetectorFactory
                .getDetectorInterface("r2qk2r/ppp2p2/2np1np1/2b1p3/N5B1/5NP1/PP1PPP2/R1B2RK1 w - - 0 1");
        detectorInterface.testState();
        detectorInterface.getCages(true).remove(new Coordinate(2, 0));
        Assertions.assertEquals(Path.of(new Coordinate(0, 0), new Coordinate(2, 0)), detectorInterface.getCages(true));
    }

    @Test
    public void pawnPathsReadOnly() {
        DetectorInterface detectorInterface = StateDetectorFactory
                .getDetectorInterface("k7/8/8/8/8/8/P7/K7 w - - 0 1");
        detectorInterface.testState();
        detectorInterface.getPawnMap(true).remove(new Coordinate(0, 1));
        Assertions.assertTrue(detectorInterface.getPawnMap(true).containsKey(new Coordinate(0, 1)));
    }

    @Test
    public void promotionsReadOnly() {
        DetectorInterface detectorInterface = StateDetectorFactory
                .getDetectorInterface("rnbqkbnr/ppppppp1/8/8/8/7N/PPPPPPP1/RNBQKBNR w KQkq - 0 1");
        detectorInterface.testState();
        detectorInterface.getPromotions(true).remove("knight");
        Assertions.assertTrue(detectorInterface.getPromotions(true).containsKey("knight"));
        detectorInterface.getPromotions(true).get("knight").put(Path.of(Coordinates.NULL_COORDINATE), 0);
        Assertions.assertFalse(detectorInterface.getPromotions(true).get("knight").containsKey(Path.of(Coordinates.NULL_COORDINATE)));
        Path path = Path.of(new Coordinate(1, 0), new Coordinate(6, 0), new Coordinate(7, 2));
        detectorInterface.getPromotions(true).get("knight").remove(path);
        Assertions.assertTrue(detectorInterface.getPromotions(true).get("knight").containsKey(path));
        detectorInterface.getPromotions(true).get("knight").keySet().forEach(k -> k.remove(new Coordinate(1, 0)));
        Assertions.assertTrue(detectorInterface.getPromotions(true).get("knight").containsKey(path));

    }


}
