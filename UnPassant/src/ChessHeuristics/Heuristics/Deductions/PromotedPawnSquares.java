package ChessHeuristics.Heuristics.Deductions;

import ChessHeuristics.Heuristics.BoardInterface;
import ChessHeuristics.Heuristics.HeuristicsUtil;
import ChessHeuristics.Heuristics.Path;
import ReverseChess.StandardChess.Coordinate;

import java.util.*;
import java.util.function.BiPredicate;
import java.util.stream.Collectors;

import static ChessHeuristics.Heuristics.Deductions.PathfinderUtil.PATH_DEVIATION;

/**
 * PromotedPawnSquares is a Deduction that, for every pawn that has promoted and seemingly no longer on the board,
 * creates Paths from pawn origins that are not already accounted for to promotion squares.
 * <p></p>
 * The state is set to false if a promoted-and-absent pawn has no valid Path from an origin to a promotion square.
 * <p></p>
 * PromotedPawnSquares must only be run after the pawns and pieces have been mapped, promoted pieces
 * determined and their Paths to their current location from pawn origins found,
 * and the maximum number of captures possible by pawns determined, as well as what missing pawns must have been
 * promoted, otherwise it will not be accurate.
 * @author Roland Crompton
 */
public class PromotedPawnSquares extends AbstractDeduction{
    /**A predicate testing that a given Coordinate is either on the white or black pawn y, depending on whether
     * the Boolean is true or false, true if white, false if black*/
    private static final BiPredicate<Coordinate, Boolean> PAWN_PREDICATE =
            (c, b) -> c.getY() == (b ? HeuristicsUtil.WHITE_PAWN_Y : HeuristicsUtil.BLACK_PAWN_Y);

    /**
     * A constructor setting the error message to "Promoted piece cannot reach a promotion square as a pawn."
     */
    public PromotedPawnSquares() {
        super("Promoted piece cannot reach a promotion square as a pawn.");
    }

    /**
     * For every pawn that promoted to a piece not currently on the board, finds a path between a pawn origin
     * and a promotion square, then stores these paths in the PawnData's pawnPaths.
     * @param board the board whose information the deduction will draw from
     */
    @Override
    public void deduce(BoardInterface board) {
        Path emptyWhiteOrigins = findEmptyOrigins(true);
        Path emptyBlackOrigins = findEmptyOrigins(false);
//        System.out.println(emptyBlackOrigins);
//        System.out.println(emptyWhiteOrigins);
        putPaths(emptyWhiteOrigins, board, true);
        putPaths(emptyBlackOrigins, board, false);
    }

    /**
     * Finds Paths from the given pawn origins of the given player to the corresponding final rank,
     * performs the necessary checks on these paths, then, if there is not enough paths to create enough
     * promoted pieces to satisfy the number of pawns captured by pawns in the capture data, sets the state to false.
     * Otherwise, the StateDetector's data are populated with the new information, removing the pawns for which
     * paths were found from the CaptureData, adds the new Paths to PawnData, and adds the pawn to the PromotionData
     * as having promoted.
     * @param emptyOrigins a Path of Coordinates of pawn origins to be checked
     * @param board the board to be checked
     * @param white the colour of the pawns being checked, true if white, false if black
     */
    private void putPaths(Path emptyOrigins, BoardInterface board, boolean white)  {
        int captures = (this.detector.getCaptureData().pawnTakeablePieces(white)
                - board.getBoardFacts().pieceNumbers(!white))
                - (this.detector.getPawnData().minimumPawnCaptures(white));
        if (!emptyOrigins.isEmpty()) {
            List<Path> tempPaths = pathToFinalRank(board, white, emptyOrigins, captures);
            List<Path> pathsInUse = getCaptures(tempPaths, captures);

            if (pathsInUse.size() < this.detector.getCaptureData().getPawnsCapturedByPawns(white)) {
                this.state = false;
                return;
            }
            List<Coordinate> coordsToIgnore = pathsInUse.stream().map(Path::getFirst).toList();
            Path nonPawnCaptures = this.detector.getCaptureData().getNonPawnCaptures(white);
            nonPawnCaptures.stream().filter(c -> PAWN_PREDICATE.test(c, white))
                    .filter(c -> !coordsToIgnore.contains(c))
                            .toList()
                            .forEach(nonPawnCaptures::remove);

            Map<Coordinate, List<Path>> pawnPaths = this.detector.getPawnData().getPawnPaths(white);
            pathsInUse.forEach(p -> {
                if (!pawnPaths.containsKey(p.getLast())) {
                    pawnPaths.put(p.getLast(), new LinkedList<>());
                }
                pawnPaths.get(p.getLast()).add(p);
            });

            String pawn = "pawn" + (white ? "w" : "b");
            Map<String, Map<Path, Integer>> promotionNumbers = this.detector.getPromotionData().getPromotionNumbers();
            pathsInUse.forEach(path -> {
                if (!promotionNumbers.containsKey(pawn)) {
                    promotionNumbers.put(pawn, new HashMap<>());
                }
                promotionNumbers.get(pawn).put(Path.of(path.getFirst()), 0);
            });
        }
    }

    /**
     * Finds those pawn origins of the given player which are both stored in the CaptureData as not being able
     * to be captured by pawns and are listed as free origins in the PawnData, and return them in a Path.
     * @param white the player's colour, true if white, false if black
     * @return a Path of the Coordinates of the found pawn origins
     */
    private Path findEmptyOrigins(boolean white) {
        Path promotedPawns = Path.of(this.detector.getCaptureData().getNonPawnCaptures(white)
                .stream().filter(c -> PAWN_PREDICATE.test(c, white))
                .collect(Collectors.toList()));

        Map<Coordinate, List<Path>> pawnPaths = this.detector.getPawnData().getPawnPaths(white);
        return promotedPawns;
    }

    /**
     * Finds a valid Path from the given Path of pawn origins for the given player. A valid Path is one that neither
     * exceeds the maximum number of captures a pawn can make, nor is exclusive with any Paths of the opposing
     * player that are the Path a pawn can take to reach its location. Returns a List of the Paths found.
     * @param board the board being checked
     * @param white the colour of the pawns being checked, true if white, false if black
     * @param origins the Path of coordinates of pawn origins being checked
     * @param captures the maximum number of captures the given pawns may make
     * @return a List of the Paths found
     */
    private List<Path> pathToFinalRank(BoardInterface board, boolean white, Path origins, int captures) {
        int enemyCaptures = (this.detector.getCaptureData().pawnTakeablePieces(!white)
                - board.getBoardFacts().pieceNumbers(white))
                - this.detector.getPawnData().minimumPawnCaptures(!white);
        List<Path> paths = new LinkedList<>();
        PathfinderUtil pathFinderUtil = new PathfinderUtil(this.detector);
        List<Map.Entry<Coordinate, List<Path>>> forbidden = this.detector.getPawnData().getPawnPaths(!white).entrySet()
                .stream()
                .filter(entry -> entry.getValue().size() == 1)
                .filter(entry -> {
                    Coordinate origin = entry.getValue().get(0).getFirst();
                            return pathFinderUtil.findAllPawnPath(board, origin, enemyCaptures,
                                    (b, c) -> c.equals(entry.getKey()), white).size() == 1;
                        })
                .toList();

        List<Path> forbiddenList = forbidden.stream().flatMap(e -> e.getValue().stream()).toList();
        int y = !white ? HeuristicsUtil.FIRST_RANK_Y : HeuristicsUtil.FINAL_RANK_Y;
        for (Coordinate origin : origins) {
            Path shortest = pathFinderUtil.findShortestPawnPath(board, origin, captures,
                            (b, c) -> c.getY() == y,
                            white, false, forbiddenList);
            if (shortest.isEmpty()) {
                continue;
            }
            paths.add(shortest);
        }
        return paths;
    }

    /**
     * For each Path in the given List of Paths, sorts them by the number of captures made, then
     * adds the number of captures made on that Path to a running total, then when the running total
     * would exceed the maximum captures given, returns a List of all those Paths already counted.
     * @param paths the pawn Paths to be checked
     * @param captures the maximum number of captures that can be made on then Paths
     * @return the largest possible List of Paths whose capture total does not exceed the given maximum captures
     */
    private List<Path> getCaptures(List<Path> paths, int captures) {
        paths.sort(Comparator.comparingInt(PATH_DEVIATION::apply));
        int currentCaptures = 0;
        List<Path> legalPaths = new LinkedList<>();
        for (Path path : paths) {
            currentCaptures += PATH_DEVIATION.apply(path);
            if (currentCaptures <= captures) {
                legalPaths.add(path);
            } else {
                return legalPaths;
            }
        }
        return legalPaths;
    }

}
