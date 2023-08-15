package Heuristics.Detector.Data;

import Heuristics.Detector.Data.PawnData;
import Heuristics.Path;
import StandardChess.Coordinate;

import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import static Heuristics.Detector.StandardStateDetector.PATH_DEVIATION;

public class StandardPawnData implements PawnData {
    private static final int WHITE = 0;
    private static final int BLACK = 1;
    private Map<Coordinate, Boolean>[] originFree = new Map[]{new TreeMap<Coordinate, Boolean>(), new TreeMap<Coordinate, Boolean>()};
    private Map<Coordinate, List<Path>>[] pawnPaths = new Map[]{new TreeMap<Coordinate, List<Path>>(), new TreeMap<Coordinate, List<Path>>()};

    public StandardPawnData() {
        for (int i = 0; i < 8 ; i++ ) {
            originFree[WHITE].put(new Coordinate(i, 1), true);
            originFree[BLACK].put(new Coordinate(i, 6), true);
        }
    }

    @Override
    public Map<Coordinate, List<Path>> getPawnPaths(boolean white) {
        return pawnPaths[white ? WHITE : BLACK];
    }

    @Override
    public int minimumPawnCaptures(boolean white) {
        Map<Coordinate, List<Path>> player = getPawnPaths(white);
        Path claimed = new Path();
        int[] size = new int[]{0};
        // With the claimed clause this will not work 100% of the time
        player.values().stream().forEach(paths -> {
                    size[0] = size[0] + paths.stream()
                            .filter(p -> !claimed.contains(p.getFirst()))
                            .reduce((integer, integer2) -> PATH_DEVIATION.apply(integer) < PATH_DEVIATION.apply(integer2) ? integer : integer2)
                            .map(p -> {
                                claimed.add(p.getFirst());
                                return PATH_DEVIATION.apply(p);
                            })
                            .orElse(0);
                }
        );
        return size[0];
    }

    @Override
    public Map<Coordinate, Boolean> getOriginFree(boolean white) {
        return originFree[white ? WHITE : BLACK];
    }
}
