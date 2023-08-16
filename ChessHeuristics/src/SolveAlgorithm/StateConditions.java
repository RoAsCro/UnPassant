package SolveAlgorithm;

import Heuristics.Detector.DetectorInterface;
import Heuristics.HeuristicsUtil;
import Heuristics.Path;
import StandardChess.Coordinate;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class StateConditions {
    public static class PromotionLimit extends StateCondition {
        private final int player;
        private final int number;
        private final List<String> pieces;

        public PromotionLimit(int player, int number, List<String> pieces) {
            this.player = player;
            this.number = number;
            this.pieces = pieces;
        }
        @Override
        public boolean test(DetectorInterface detectorInterface) {
            if (!super.test(detectorInterface)) {
                return false;
            }
            List<Map<String, Map<Path, Integer>>> maps = new LinkedList<>();
            if (player < 1) {
                maps.add(detectorInterface.getPromotions(true));
            } if (player > -1) {
                maps.add(detectorInterface.getPromotions(false));
            }
            int count = 0;
            for (Map<String, Map<Path, Integer>> map : maps) {
                count += map.entrySet().stream().filter(e -> pieces.contains(e.getKey().split(" ")[0]))
                        .map(e -> e.getValue().values().stream().reduce(Integer::sum).orElse(0))
                        .reduce(Integer::sum).orElse(0);
            }
            return count <= number;
        }
    }

    public static class NoPromotions extends PromotionLimit {
        public NoPromotions() {
            super(0, 0, HeuristicsUtil.PIECE_CODES.keySet().stream().toList());
        }
    }
}
