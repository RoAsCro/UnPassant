package Heuristics.Detector;

import Heuristics.BoardInterface;
import Heuristics.DetectorInterface;
import Heuristics.Path;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class StandardDetectorInterface implements DetectorInterface {

    private StandardStateDetector detector;

    StandardDetectorInterface(StandardStateDetector detector) {
        this.detector = detector;
    }
    @Override
    public Map<String, Map<Path, Integer>> getPromotions(boolean white) {
        List<Map.Entry<String, Map<Path, Integer>>> entryList = detector.getPromotionNumbers().entrySet()
                .stream()
                .filter(e -> e.getKey().endsWith(white ? "w" : "b"))
                .filter(e -> !e.getValue().isEmpty())
                .filter(e -> !e.getValue().containsKey(null))
                .toList();
        return entryList.stream()
                .collect(Collectors.toMap(e -> {
                    String name = e.getKey();
                    name = name.substring(0, name.length()-1);
                    if (name.startsWith("bishop")) {
                        name = name.substring(0, name.length() - 1) + (name.endsWith("w") ? " (light)" : " (dark)");
                    }
                    return name;
                }, e ->
                    e.getValue().entrySet().stream()
                            .filter(e1 -> e1.getKey() != null)
                            .filter(e1 -> e1.getKey() != new Path())
                            .collect(Collectors.toMap(e1 -> Path.of(e1.getKey()), e1 -> e1.getKey().size() - e1.getValue()))));
    }

    @Override
    public boolean testState(BoardInterface boardInterface) {
        return this.detector.testState(boardInterface);
    }

    @Override
    public boolean testState() {
        return this.detector.testState();
    }

    @Override
    public boolean canCastle(boolean white) {
        return !(this.detector.getKingMovement(white) || (this.detector.getRookMovement(white, true) && this.detector.getRookMovement(white, false)));
    }

    @Override
    public String toString() {
        return this.detector.toString();
    }
}
