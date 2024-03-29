package ChessHeuristics.SolverAlgorithm;

import ChessHeuristics.Heuristics.Detector.DetectorInterface;
import ChessHeuristics.Heuristics.HeuristicsUtil;
import ChessHeuristics.Heuristics.Path;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * An example of some possible StateConditions.
 * @author Roland Crompton
 */
public class StateConditions {
    /**
     * A StateCondition that returns false if the specified players have more promoted pieces of the
     * specified types than the given number.
     */
    public static class PromotionLimit extends StateCondition {
        /**An integer representing the players being checked, -1 for black, 1 for white, 0 for both*/
        private final int player;
        /**The maximum number of pieces the given players can collectively have*/
        private final int number;

        /**
         * Constructs a StateCondition that returns false if the specified players have more promoted pieces of the
         * specified types than the given number. The player is an integer, -1 for black, 1 for white, 0 for both.
         * @param player the players being checked, -1 for black, 1 for white, 0 for both
         * @param number the maximum number of pieces the given players can collectively have
         * @param pieces the types of pieces included in the check
         */
        public PromotionLimit(int player, int number, List<String> pieces) {
            this.player = player;
            this.number = number;
        }

        /**
         * Returns false if the specified players have more promoted pieces of the
         * specified types than the given number.
         */
        @Override
        public boolean test(DetectorInterface detectorInterface) {
            if (!super.test(detectorInterface)) {
                return false;
            }
            List<Map<String, Map<Path, Integer>>> maps = new LinkedList<>();
            if (player < 1) {
                maps.add(detectorInterface.getPromotions(false));
            } if (player > -1) {
                maps.add(detectorInterface.getPromotions(true));
            }
            int count = 0;
            for (Map<String, Map<Path, Integer>> map : maps) {
                count += map.values().stream()
                        .map(pathIntegerMap -> pathIntegerMap.values().stream().reduce(Integer::sum).orElse(0))
                        .reduce(Integer::sum).orElse(0);
            }
            return count <= number;
        }
    }

    /**
     * A StateCondition that specifies that there may be no promotions made by any player.
     */
    public static class NoPromotions extends PromotionLimit {
        /**
         * Returns false if either player has a promoted piece.
         */
        public NoPromotions() {
            super(0, 0, HeuristicsUtil.PIECE_CODES.keySet().stream().toList());
        }
    }
}
