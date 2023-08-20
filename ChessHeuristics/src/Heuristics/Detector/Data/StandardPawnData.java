package Heuristics.Detector.Data;

import Heuristics.HeuristicsUtil;
import Heuristics.Path;
import StandardChess.Coordinate;

import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import static Heuristics.Deductions.PathfinderUtil.PATH_DEVIATION;
/**
 * An implementation of PawnData
 * @author Roland Crompton
 */
public class StandardPawnData implements PawnData {
    /**The location of black in the stored Lists*/
    private static final int BLACK = 1;
    /**The location of white in the stored Lists*/
    private static final int WHITE = 0;
    /**A List of Maps of pawn origins and whether they are free for each player*/
    private final List<Map<Coordinate, Boolean>> originFree
            = List.of(new TreeMap<>(), new TreeMap<>());
    /**A List of Maps of pawns and their Paths for each player*/
    private final List<Map<Coordinate, List<Path>>> pawnPaths
            = List.of(new TreeMap<>(), new TreeMap<>());

    /**
     * A constructor that initialises the originFree, populating with all pawn origins and setting them to true.
     */
    public StandardPawnData() {
        for (int i = 0; i <= HeuristicsUtil.K_ROOK_X; i++ ) {
            this.originFree.get(WHITE).put(new Coordinate(i, HeuristicsUtil.WHITE_PAWN_Y), true);
            this.originFree.get(BLACK).put(new Coordinate(i, HeuristicsUtil.BLACK_PAWN_Y), true);
        }
    }

    /**
     * Returns a Map of Coordinates of pawns of the given colour and a List of potential
     * Paths they took from pawn origins to their current location.
     * @param white the colour of the pawns, true if white, false if black
     * @return the Map of pawns and their Paths
     */
    @Override
    public Map<Coordinate, List<Path>> getPawnPaths(boolean white) {
        return this.pawnPaths.get(white ? WHITE : BLACK);
    }

    /**
     * Returns the sum of minimum number of captures all pawns of the given colour can make.
     * @param white the colour of the pawns, true if white, false if black
     * @return the sum of minimum number of captures all pawns of the given colour can make
     */
    @Override
    public int minimumPawnCaptures(boolean white) {
        Map<Coordinate, List<Path>> player = getPawnPaths(white);
        Path claimed = new Path();
        int[] size = new int[]{0};
        player.values().forEach(paths -> size[0] = size[0] + paths.stream()
                .filter(p -> !claimed.contains(p.getFirst()))
                .reduce((integer, integer2) -> PATH_DEVIATION.apply(integer)
                        < PATH_DEVIATION.apply(integer2) ? integer : integer2)
                .map(p -> {
                    claimed.add(p.getFirst());
                    return PATH_DEVIATION.apply(p);
                })
                .orElse(0)
        );
        return size[0];
    }

    /**
     * Returns a Map of Coordinates of pawn origins and a Boolean that will be true if that origin can have a pawn
     * besides those already stored in the pawnPaths originate from it.
     * @param white the colour of the pawn origins, true if white, false if black
     * @return a Map of pawn origins and whether they are free
     */
    @Override
    public Map<Coordinate, Boolean> getOriginFree(boolean white) {
        return this.originFree.get(white ? WHITE : BLACK);
    }
}
