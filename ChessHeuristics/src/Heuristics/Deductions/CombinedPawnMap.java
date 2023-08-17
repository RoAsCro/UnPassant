package Heuristics.Deductions;

import Heuristics.BoardInterface;
import Heuristics.Detector.StateDetector;
import Heuristics.Path;
import StandardChess.BoardReader;
import StandardChess.Coordinate;
import StandardChess.Coordinates;

import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

import static Heuristics.HeuristicsUtil.FINAL_RANK_Y;
import static Heuristics.HeuristicsUtil.FIRST_RANK_Y;

/**
 * The CombinedPawnMap is a Deduction which forms a map of potential Paths from squares on the 2nd and 7th ranks to
 * pawns of the corresponding colour.
 * <></>
 * The state will be set to false if any pawn on the does not have a valid Path from an origin.
 */
public class CombinedPawnMap extends AbstractDeduction {
    /**A PawnMap of black pawns*/
    protected PawnMap blackPawnMap = new PawnMap(false);
    /**A PawnMap of white pawns*/
    protected PawnMap whitePawnMap = new PawnMap(true);
    /**Stores a reference to the black pawn Paths from the PawnData*/
    private Map<Coordinate, List<Path>> blackPawnPaths;
    /**Stores a reference to the white pawn Paths from the PawnData*/
    private Map<Coordinate, List<Path>> whitePawnPaths;

    /**
     * Standard constructor
     */
    public CombinedPawnMap(){
        super("Illegal pawn structure - pawns cannot reach their current position.");
    };

    /**
     * Registers a StateDetector as described in the Deduction interface.
     * @param detector the StateDetector from which data will be drawn and put
     */
    @Override
    public void registerDetector(StateDetector detector) {
        super.registerDetector(detector);
        this.blackPawnPaths = this.detector.getPawnData().getPawnPaths(true);
        this.whitePawnPaths = this.detector.getPawnData().getPawnPaths(false);
    }


    /**
     * Forms a map of the given board, creating Paths from pawn/origin combinations established by the individual
     * PawnMaps and makink sure none of them are exclusive with Paths of the other player's pawns.
     * @param board the board to be checked
     */
    @Override
    public void deduce(BoardInterface board) {
        this.whitePawnMap.registerDetector(this.detector);
        this.blackPawnMap.registerDetector(this.detector);
        this.whitePawnMap.deduce(board);
        if (!this.whitePawnMap.getState()) {
            this.errorMessage = this.whitePawnMap.errorMessage();
            this.state = false;
            return;
        }
        this.blackPawnMap.deduce(board);
        if (!this.blackPawnMap.getState()) {
            this.errorMessage = this.blackPawnMap.errorMessage();
            this.state = false;
            return;
        }
        boolean changed = true;
        boolean secondChange = true;
        while (changed) {
            HashSet<List<Path>> startingWhite = new HashSet<>(this.whitePawnPaths.values());
            HashSet<List<Path>> startingBlack = new HashSet<>(this.blackPawnPaths.values());

            makeMaps(board, false);
            makeMaps(board, true);
            if ((!exclude(board, true) & !exclude(board, false))
                    || (startingWhite.containsAll(new HashSet<>(this.whitePawnPaths.values()))
                    && startingBlack.containsAll(new HashSet<>(this.blackPawnPaths.values())))
            ) {
                if (!secondChange) {
                    changed = false;
                } else {
                    secondChange = false;
                }
            }
        }

        if (this.whitePawnPaths.values().stream().anyMatch(List::isEmpty)) {
            this.errorMessage = generateErrorMessage(this.whitePawnPaths);
            this.state = false;
        } else if (this.blackPawnPaths.values().stream().anyMatch(List::isEmpty)) {
            this.errorMessage = generateErrorMessage(this.blackPawnPaths);
            this.state = false;

        }
    }

    /**
     * Generates am error message specifying which has no valid path from an origin.
     * @param paths the Map of pawn paths to be used
     * @return am error message as a String
     */
    private String generateErrorMessage(Map<Coordinate, List<Path>> paths) {
        return "Illegal pawn structure - pawn at " + paths.entrySet()
                .stream().filter(e -> e.getValue().isEmpty())
                .map(Map.Entry::getKey)
                .findAny()
                .orElse(Coordinates.NULL_COORDINATE) + " cannot reach its position.";
    }

    /**
     * Returns all Coordinate/Path Maps of the opposing player to the given player that have only one possible origin and one
     * possible Path from that origin, not including those pawns currently on their origin.
     * @param board the board being checked
     * @param white the player whose opponent's pawns  are to be checked, true for white, false for black
     * @return the paths of the opposing player which have one origin and one possible Path
     */
    private Map<Coordinate, List<Path>> getSinglePathPawns(BoardInterface board, boolean white) {
        Map<Coordinate, List<Path>> opposingPlayerPaths = (!white ? this.blackPawnPaths : this.whitePawnPaths);
        PathfinderUtil pathFinderUtil = new PathfinderUtil(this.detector);
        // Find every pawn of the opposing player with one origin and one possible path
        List<Map.Entry<Coordinate, List<Path>>> singleOriginPawns = new ArrayList<>(opposingPlayerPaths.entrySet()
                .stream()
                .filter(entry -> entry.getValue().size() == 1 && !(entry.getValue().get(0).size() == 1))
                .filter(entry -> pathFinderUtil.findAllPawnPath(board, entry.getValue().get(0).getFirst(),
                                        getMaxCaptures(!white, entry.getKey()),
                                        (b, c) -> c.equals(entry.getKey()), !white)
                                .size() == 1)
                .toList());
        singleOriginPawns.addAll(opposingPlayerPaths.entrySet()
                .stream().filter(entry ->entry.getValue().size() == 1 && entry.getValue().get(0).size() == 1).toList());
        return singleOriginPawns.stream().collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    /**
     * Ensures that found pawn paths are not exclusive with existing pawn paths of the opposing player that have
     * only one possible path. It then returns whether there was a change to the pawn Paths.
     * @param board the board being checked
     * @param white the currently checked player's colour, true if white, false if black
     * @return whether there was a change
     */
    protected boolean exclude(BoardInterface board, boolean white) {
        // Find pawns of the opposing player with single paths
        Map<Coordinate, List<Path>> singleOriginPawns = getSinglePathPawns(board, white);
        Map<Coordinate, List<Path>> checkedPlayerPaths = this.detector.getPawnData().getPawnPaths(white);
        if (singleOriginPawns.isEmpty()) {
            updatePawnMaps();
            return false;
        }
        // Find which paths of the current player are exclusive with single-path pawns and replace those that are
        List<Path> newPaths = new LinkedList<>();
        singleOriginPawns.forEach((opposingPawn, opposingPaths) -> checkedPlayerPaths.entrySet()
                .stream()
                .filter(innerEntry -> !innerEntry.getValue().isEmpty())
                .filter(innerEntry -> {
                    // Find what opposing pawns are inside the pawn's Path and vice versa
                    Coordinate playerPawn = innerEntry.getKey();
                    Path opposingPawnPath = opposingPaths.get(0);
                    Path playerPath = innerEntry.getValue().get(0);
                    if (opposingPawnPath.contains(playerPawn)
                            && playerPath.contains(opposingPawn)) {
                        return true;
                    }
                    int y2 = playerPawn.getY();
                    //If a pawn is on the first or final rank, it can still be exclusive without both paths containing one another
                    if (y2 == FINAL_RANK_Y || y2 == FIRST_RANK_Y) {
                        return opposingPawnPath.contains(playerPath.get(playerPath.size() - 2));
                    }
                    return false;
                }).forEach(innerEntry -> {
                    innerEntry.getValue()
                            .stream().filter(path ->
                                    new PathfinderUtil(detector)
                                            .pathsExclusive(opposingPaths.get(0), path))
                            .forEach(path -> {
                                Path toPut = makeExclusiveMaps(board, path, white, singleOriginPawns);
                                if (toPut.isEmpty()) {
                                    toPut.add(path.getFirst());
                                    toPut.add(Coordinates.NULL_COORDINATE);
                                    toPut.add(innerEntry.getKey());
                                }
                                newPaths.add(toPut);
                            });
                }));
        // Remove those paths that cannot be remade after being found to be exclusive
        List<Coordinate[]> forRemoval = new LinkedList<>();
        newPaths.forEach(path -> {
            List<Path> pathList = checkedPlayerPaths.get(path.getLast());
            Path toRemove = pathList
                    .stream().filter(path2 -> path2.getFirst() == path.getFirst())
                    .findFirst()
                    .orElse(null);
            pathList.remove(toRemove);
            if (!(path.contains(Coordinates.NULL_COORDINATE))) {
                pathList.add(path);
            } else {
                forRemoval.add(new Coordinate[]{path.getLast(), path.getFirst()});
            }
        });
        forRemoval.forEach(coordinates -> (white ? this.whitePawnMap : this.blackPawnMap)
                .getPawnOrigins().get(coordinates[0]).remove(coordinates[1]));
        updatePawnMaps();
        return !forRemoval.isEmpty() || !newPaths.isEmpty();
    }

    /**
     * Updates the PawnMaps
     */
    protected void updatePawnMaps() {
        this.blackPawnMap.update();
        this.whitePawnMap.update();
    }

    /**
     * Attempts to create a path from the first Coordinate of the given Path and the last Coordinate of the
     * given Path that isn't exclusive with the given forbidden Paths.
     * @param board the board checked
     * @param path the Path whose first and Last Coordinates are to be used as the target and origin
     * @param white the player whose pawn is pathing, true if white, false if black
     * @param forbiddenPaths the Paths that the new Path cannot be exclusive with
     * @return the new Path, empty if none is found
     */
    private Path makeExclusiveMaps(BoardInterface board, Path path,
                                   boolean white, Map<Coordinate, List<Path>> forbiddenPaths) {
        Coordinate target = path.getLast();
        return new PathfinderUtil(detector).findShortestPawnPath(board,
                path.getFirst(), getMaxCaptures(white, target),
                (b, c) -> c.equals(target),
                white, true, forbiddenPaths.entrySet()
                        .stream().flatMap(e -> e.getValue().stream()).toList());
    }

    /**
     * Makes the pawn Paths independent of exclusion for the given player.
     * @param board the board being checked
     * @param white whether the player being checked, true if white, false if black
     */
    private void makeMaps(BoardInterface board, boolean white) {
        (white ? this.whitePawnMap : this.blackPawnMap)
                .getPawnOrigins().forEach((key, value) -> {
                    List<Path> paths = new LinkedList<>();
                    value.forEach(coordinate -> {
                        Path path = new PathfinderUtil(this.detector).findShortestPawnPath(
                                board, coordinate, getMaxCaptures(white, key),
                                (b, c) -> c.equals(key), white, true, new LinkedList<>()
                        );
                        if (!path.isEmpty()) {

                            paths.add(path);
                        }
                    });
                    this.detector.getPawnData().getPawnPaths(white).put(key, paths);
                });
    }

    /**
     * Returns the maximum number of captures a given pawn of the given player can make.
     * This is calculated as the number of captures any pawn can make in excess of their minimum captures
     * plus the minimum captures that pawn can make to reach their current location.
     * @param white the player whose pawn is being checked, true if white, false if black
     * @param coordinate the Coordinate of the pawn being checked
     * @return the maximum number of captures the given pawn can make
     */
    public int getMaxCaptures(boolean white, Coordinate coordinate) {
        return (white ? whitePawnMap : blackPawnMap).getCapturedPieces()
                + (white ? whitePawnMap : blackPawnMap).getCaptureSet().get(coordinate);
    }

    /**
     * The PawnMap is a Deduction that links squares on the 2nd or 7th ranks to pawns of the colour given at
     * instantiation. The state will be set to false if there is a pawn of the given colour that cannot be
     * linked to an origin.
     */
    public static class PawnMap extends AbstractDeduction{
        private final Map<Coordinate, Path> pawnOrigins = new TreeMap<>();
        private final Map<Coordinate, Integer> captureSet = new TreeMap<>();
        private List<Set<Coordinate>> sets = new LinkedList<>();
        private int opponentPieceNumbers = 0;
        private int capturedPieces;
        protected int maxPieces = 16;

        private boolean white;

        public PawnMap(Boolean white) {
            super("Illegal pawn structure.");
            this.white = white;
        }

        @Override
        public void deduce(BoardInterface board) {
            this.opponentPieceNumbers = board.getBoardFacts().pieceNumbers(!this.white);
            rawMap(board, this.white);
            reduce();
        }

        public void update() {
            reduce();
        }
        /**
         * Returns the max number of pieces pawns can capture. This is the standard number of pieces, 16,
         * minus the number of missing opposing pieces that cannot be taken by pawns according to what is currently
         * stored in the PawnData, minus the number of pieces the opposing player has remaining:
         * <></>
         * (16 - takeable pieces) - opponent's remaining pieces
         * @return the max number of pieces the pawns can collectively take
         */
        protected int capturedPieces() {
            return this.detector.getCaptureData().pawnTakeablePieces(this.white) - (this.opponentPieceNumbers);
        }

        /**
         * When pieces are accounted for elsewhere, the maxPieces needs to be updated
         * @param subtrahend the number of pieces that cannot be captured by pawns
         */
        @Deprecated
        public void updateMaxCapturedPieces(int subtrahend) {
            this.maxPieces -= subtrahend;
        }

        protected void rawMap(BoardInterface board, boolean white) {
            int start = Math.abs((white ? FIRST_RANK_Y : FINAL_RANK_Y) - 1);
            int increment = white ? 1 : -1;
            BoardReader reader = board.getReader();
            for (int y = 0; y < FINAL_RANK_Y - 1; y++) {
                reader.to(new Coordinate(0, start + y * increment));
                int finalY = y;
                reader.nextWhile(Coordinates.RIGHT, coordinate -> coordinate.getX() <= FINAL_RANK_Y, piece -> {
                    if (piece.getType().equals("pawn") && piece.getColour().equals(white ? "white"  : "black")) {
                        Coordinate pawn = reader.getCoord();
                        int potentialPaths = finalY * 2 + 1;
                        Path starts = new Path();
                        for (int j = 0 ; j < potentialPaths ; j++) {
                            int x = (pawn.getX() - finalY) + j;
                            if (x > FINAL_RANK_Y) {
                                break;
                            }
                            if (x < FIRST_RANK_Y) {
                                continue;
                            }
                            starts.add(new Coordinate(x, start));
                        }
                        pawnOrigins.put(pawn, starts);
                    }
                });
            }
            //Sys tem.out.println("?!?!");
            //Sys tem.out.println(this.detector.getPawnOrigins(white));
        }

        /**
         * Finds the number of opposing pieces missing x, the minimum number of pieces a given pawn can have taken y,
         * and the sum of all y's z,
         * then removes any origins from the sets of origins of a given pawn which require taking a minimum of
         * greater than (x - z) + y captures
         */
        protected void captures(Map<Coordinate, Path> origins) {
            updateCaptureSet();
            origins.forEach((key, value) -> {
                int x = key.getX();
                value.removeAll(value.stream()
                        .filter(coordinate -> Math.abs(x - coordinate.getX()) > this.capturedPieces + this.captureSet.get(key))
                        .toList());
            });
        }

        /**
         * Updates the player's captureSet - a Map of Coordinates of pawns and the minimum number of pieces that
         * pawn must take. This is the smallest difference between the pawn's current x and the x's if it's potential
         * origins in pawnOrigins.
         * <></>
         * If the sum of minimum captures exceeds the maximum number of pieces all pawns can take, the state is set
         * to false. If it does not, the remainder of this amount is set as the capturedPieces.
         */
        protected void updateCaptureSet() {
            int maxOffset = capturedPieces() -
                    pawnOrigins.entrySet().stream()
                            .map(entry -> {
                                int x = entry.getKey().getX();
                                Coordinate coordinate = entry.getValue().stream()
                                        .reduce((c1, c2) -> {
                                            int x1 = Math.abs(x - c1.getX());
                                            int x2 = Math.abs(x - c2.getX());
                                            if (x1 < x2) {
                                                return c1;
                                            }
                                            return c2;
                                        })
                                        .orElse(Coordinates.NULL_COORDINATE);
                                int minCaptures = Math.abs(x - coordinate.getX());
                                this.captureSet.put(entry.getKey(), minCaptures);
                                return minCaptures;})
                            .reduce(Integer::sum)
                            .orElse(0);
            if (maxOffset < 0) {
                this.errorMessage = "Too many pawn captures.";
                this.state = false;
            }
            setCapturedPieces(maxOffset);
        }
        private void reduce() {
            this.sets = new LinkedList<>();
            List<Coordinate> origins = pawnOrigins.entrySet().stream()
                    .flatMap(f -> f.getValue().stream())
                    .collect(Collectors.toSet())
                    .stream().toList();

            if (!origins.isEmpty()) {
                List<Coordinate> originsTwo = new LinkedList<>(origins);
                boolean change = true;
                while (change){
                    captures(pawnOrigins);
                    change = reduceIter(new HashSet<>(), originsTwo);

                }
            }
        }

        /**
         * Iterates through every combination of origins looking for set for which
         * there exists an equal number of pieces whose origin sets are a subset of it.
         * If such exists, no other piece may have any origin in that set as one of its possible origins.
         *
         */
        protected boolean reduceIter(Set<Coordinate> set, List<Coordinate> origins) {
            boolean change = false;
            if (!set.isEmpty()) {
                AtomicBoolean supersets = new AtomicBoolean(false);
                List<Coordinate> subsets = pawnOrigins.entrySet().stream()
                        .filter(entry -> {
                            Path path = entry.getValue();
                            if (path.stream().anyMatch(set::contains)) {
                                supersets.set(true);
                            }
                            // True if the current set of origins contains every origin in the piece being examined has
                            return set.containsAll(path);
                        })
                        .map(Map.Entry::getKey)
                        .toList();
                // If the number of subsets of the current set is the same as the number of origins in the set
                int subsetSize = subsets.size();
                int setSize = set.size();
                if (subsetSize > setSize) {
                    this.state = false;
                }

                if (subsetSize == setSize) {

                    // Check the total number of captures
                    Map<Coordinate, Path> map = new TreeMap<>();
                    subsets.forEach(coordinate -> map.put(coordinate, Path.of(pawnOrigins.get(coordinate))));
                    set.forEach(coordinate -> this.detector.getPawnData().getOriginFree(white).put(coordinate, false));
                    this.sets.add(set);
                    reduceIterHelperStart(map);
                    return removeCoords(set, subsets);
                }
                // If there does not exist a set that contains this set
                if (!supersets.get()) {
                    return false;
                }
            }
            for (Coordinate currentCoord : origins) {
                if (this.sets.stream().anyMatch(s -> s.contains(currentCoord) && s.size() <= set.size() + 1)) {
                    continue;
                }
                Set<Coordinate> newSet = new HashSet<>(set);
                newSet.add(currentCoord);
                List<Coordinate> newOrigins = new LinkedList<>(origins);
                newOrigins.remove(currentCoord);

                if (reduceIter(newSet, newOrigins)) {

                    change = true;
                    if(!set.isEmpty()) {
                        break;
                    }
                }

            }
            return change;
        }
        protected void reduceIterHelperStart(Map<Coordinate, Path> map) {
            Map<Coordinate, Path> removalMap = new TreeMap<>();
            List<Coordinate> remainingPawns = new LinkedList<>(map.keySet());

            int maxCaptures = capturedPieces();

            for (Coordinate currentPawn : remainingPawns) {
                List<Coordinate> newRemainingPawns = new LinkedList<>(remainingPawns);
                newRemainingPawns.remove(currentPawn);
                Path forRemoval = new Path();
                for (Coordinate currentOrigin : map.get(currentPawn)) {

                    int totalCaptures = Math.abs(currentPawn.getX() - currentOrigin.getX());
                    if (totalCaptures > maxCaptures) {
                        forRemoval.add(currentOrigin);
                        continue;
                    }
                    if (newRemainingPawns.isEmpty()) {
                        continue;
                    }
                    List<Coordinate> usedOrigins = new LinkedList<>();
                    usedOrigins.add(currentOrigin);

                    if (!reduceIterHelper(usedOrigins, newRemainingPawns, map, totalCaptures)) {
                        forRemoval.add(currentOrigin);
                    }
                }
                removalMap.put(currentPawn, forRemoval);
            }

            removalMap.forEach((key, value) -> pawnOrigins.get(key).removeAll(value));

        }

        /**
         * Checks there aren't mutually required capture amounts that exceed the maximum capture amount
         */
        private boolean reduceIterHelper(List<Coordinate> usedOrigins, List<Coordinate> remainingPawns,
                                         Map<Coordinate, Path> map, int totalCaptures) {
            int maxCaptures = capturedPieces();

            Coordinate currentPawn = remainingPawns.get(remainingPawns.size()-1);
            remainingPawns = new LinkedList<>(remainingPawns);
            remainingPawns.remove(currentPawn);
            for (Coordinate currentOrigin : map.get(currentPawn)) {

                int newTotalCaptures = Math.abs(currentPawn.getX() - currentOrigin.getX()) + totalCaptures;
                if (usedOrigins.contains(currentOrigin) ||
                        newTotalCaptures
                                > maxCaptures) {
                    continue;
                }
                if (remainingPawns.isEmpty()) {
                    return true;
                }
                List<Coordinate> usedOriginsTwo = new LinkedList<>(usedOrigins);
                usedOriginsTwo.add(currentOrigin);

                if (reduceIterHelper(usedOriginsTwo, remainingPawns, map, newTotalCaptures)) {
                    return true;
                }

            }
            return false;
        }

        /**
         *
         * @return true if something is removed
         */
        private boolean removeCoords(Set<Coordinate> forRemoval, List<Coordinate> ignore) {

            return !pawnOrigins.entrySet()
                    .stream()
                    .filter(entry -> !ignore.contains(entry.getKey()))
                    .filter(entry -> entry.getValue().removeAll(forRemoval))
                    .toList()
                    .isEmpty();
        }

        /**
         * Returns the total pieces any pawn can capture in excess of the sum of the minimum number of captures
         * all pawns of this colour can make.
         * @return the number of captures any pawn can make in excess of their minimum captures
         */
        private int getCapturedPieces() {
            return capturedPieces;
        }
        private void setCapturedPieces(int capturedPieces) {
            this.capturedPieces = capturedPieces;
        }
        public Map<Coordinate, Integer> getCaptureSet() {
            return this.captureSet;
        }
        public Map<Coordinate, Path> getPawnOrigins() {
            return this.pawnOrigins;
        }
        public boolean getColour() {
            return this.white;
        }


    }

}
