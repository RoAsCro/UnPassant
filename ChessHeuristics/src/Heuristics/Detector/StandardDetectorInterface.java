package Heuristics.Detector;

import Heuristics.BoardInterface;
import Heuristics.HeuristicsUtil;
import Heuristics.Path;
import StandardChess.Coordinate;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class StandardDetectorInterface implements DetectorInterface {

    private StandardStateDetector detector;

    public StandardDetectorInterface(StandardStateDetector detector) {
        this.detector = detector;
    }

    @Override
    public Map<String, Map<Path, Integer>> getPromotions(boolean white) {
        System.out.println(detector.getPromotionData().getPromotionNumbers());
        List<Map.Entry<String, Map<Path, Integer>>> entryList =
                detector.getPromotionData().getPromotionNumbers().entrySet().stream()
                .filter(e -> e.getKey().endsWith(white ? "w" : "b"))
                .filter(e -> !e.getValue().isEmpty())
                .toList();
        entryList = entryList.stream().filter(e -> !e.getValue().isEmpty()).toList();
        return entryList.stream()
                .collect(Collectors.toMap(e -> {
                    String name = e.getKey();
                    name = name.substring(0, name.length()-1);
                    if (name.startsWith("bishop")) {
                        name = name.substring(0, name.length() - 1) + (name.endsWith("l") ? " (light)" : " (dark)");
                    }
                    return name;
                }, e ->
                    e.getValue()
                            .entrySet().stream()
                            .filter(e1 -> e1.getKey() != null)
                            .filter(e1 -> !e1.getKey().equals(new Path()))
                            .collect(Collectors.toMap(e1 -> Path.of(e1.getKey()), e1 -> e1.getKey().size() - e1.getValue()))
                            ));
    }

    @Override
    public Map<Coordinate, Path> getPawnMap(boolean white) {
        return this.detector.getPawnData().getPawnPaths(white).entrySet()
                .stream().collect(Collectors.toMap(Map.Entry::getKey,
                        e -> Path.of(e.getValue().stream().map(Path::getFirst).toList())));
    }

    @Override
    public Path getCages(boolean white){
        return Path.of(this.detector.getPieceData().getCaged().entrySet()
                .stream()
                .filter(Map.Entry::getValue)
                .filter(e -> e.getKey().getY() ==(white ? HeuristicsUtil.FIRST_RANK_Y : HeuristicsUtil.FINAL_RANK_Y))
                .map(Map.Entry::getKey)
                .toList());
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
        return !(this.detector.getPieceData().getKingMovement(white) || (this.detector.getPieceData().getRookMovement(white, true) && this.detector.getPieceData().getRookMovement(white, false)));
    }

    @Override
    public boolean getState() {
        return this.detector.getState();
    }

    @Override
    public String toString() {
        if (!this.detector.getState()) {
            return this.detector.getErrorMessage();
        }

        StringBuilder stringBuilder = new StringBuilder();

        stringBuilder.append("Pawn origins:\n");
        boolean white = true;
        for (int i = 0 ; i < 2 ; i++) {
            stringBuilder.append(white ? "White" : "Black").append(":\n");
            getPawnMap(white).forEach((key, value) -> {
                stringBuilder.append(key).append(": ");
                stringBuilder.append(value.stream()
                        .map(Coordinate::toString).collect(Collectors.joining(", ")));
                stringBuilder.append("\n");
            });
            stringBuilder.append("\n");
            white = false;
        }

        stringBuilder.append("Cages:\n");
        white = true;
        for (int i = 0 ; i < 2 ; i++) {
            stringBuilder.append(white ? "White" : "Black").append(":\n");
            stringBuilder.append(getCages(white).stream()
                    .map(Coordinate::toString)
                    .collect(Collectors.joining(", ")));
            stringBuilder.append("\n");
            white = false;
        }
        stringBuilder.append("\n");

        stringBuilder.append("Promotions:\n");
        white = true;
        for (int i = 0 ; i < 2 ; i++) {
            stringBuilder.append(white ? "White" : "Black").append(":\n");
            getPromotions(white).forEach((key, value) -> {
                stringBuilder.append(key).append(":\n");
                stringBuilder.append(value.entrySet()
                        .stream().map(e1 -> e1.getKey() + ", " + e1.getValue() + "\n")
                        .collect(Collectors.joining(", ")));
            });
            stringBuilder.append("\n");
            white =false;
        }



         return stringBuilder.toString();
    }
}
