package Heuristics.Deductions;

import Heuristics.BoardInterface;
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
 * pawns of the corresponding colour. The state will be set to false if any pawn on the does not have a valid Path
 * from an origin.
 */
public class CombinedPawnMap extends AbstractDeduction {

    protected PawnMap whitePawnMap = new PawnMap(true);
    protected PawnMap blackPawnMap = new PawnMap(false);

    public CombinedPawnMap(){
        super("Illegal pawn structure - pawns cannot reach their current position.");
    };

    @Override
    public boolean deduce(BoardInterface board) {
        this.whitePawnMap.registerDetector(this.detector);
        this.blackPawnMap.registerDetector(this.detector);
        this.whitePawnMap.deduce(board);
        if (!this.whitePawnMap.getState()) {
            this.errorMessage = this.whitePawnMap.errorMessage();
            this.state = false;
            return false;
        }
        this.blackPawnMap.deduce(board);
        if (!this.blackPawnMap.getState()) {
            this.errorMessage = this.blackPawnMap.errorMessage();
            this.state = false;
            return false;
        }
        boolean changed = true;
        boolean another = true;
        while (changed) {
            HashSet<List<Path>> startingWhite = new HashSet<>(this.detector.getPawnData().getPawnPaths(true).values());
            HashSet<List<Path>> startingBlack = new HashSet<>(this.detector.getPawnData().getPawnPaths(false).values());

            makeMaps(board, false);
            makeMaps(board, true);
            if ((!exclude(board, true) & !exclude(board, false))
                    || (startingWhite.containsAll(new HashSet<>(this.detector.getPawnData().getPawnPaths(true).values()))
                    && startingBlack.containsAll(new HashSet<>(this.detector.getPawnData().getPawnPaths(false).values())))
            ) {
                if (!another) {
                    changed = false;
                } else {
                    another = false;
                }
            }
        }

        if (this.detector.getPawnData().getPawnPaths(true).values().stream().anyMatch(List::isEmpty)) {
            this.errorMessage = generateErrorMessage(this.detector.getPawnData().getPawnPaths(true));
            this.state = false;
        } else if (this.detector.getPawnData().getPawnPaths(false).values().stream().anyMatch(List::isEmpty)) {
            this.errorMessage = generateErrorMessage(this.detector.getPawnData().getPawnPaths(false));
            this.state = false;

        }
        return false;
    }

    private String generateErrorMessage(Map<Coordinate, List<Path>> paths) {
        return "Illegal pawn structure - pawn at " + paths.entrySet()
                .stream().filter(e -> e.getValue().isEmpty())
                .map(Map.Entry::getKey)
                .findAny()
                .orElse(Coordinates.NULL_COORDINATE)
                .toString() + " cannot reach its position.";
    }


    /**
     *
     * @return whether or not there was a change
     */
    protected boolean exclude(BoardInterface board, boolean white) {
        Map<Coordinate, List<Path>> checkedPlayerPaths = this.detector.getPawnData().getPawnPaths(white);

        Map<Coordinate, List<Path>> opposingPlayerPaths = this.detector.getPawnData().getPawnPaths(!white);
        PiecePathFinderUtil pathFinderUtil = new PiecePathFinderUtil(this.detector);
        // Find every pawn of the opposing player with one origin and one possible path
        List<Map.Entry<Coordinate, List<Path>>> singleOriginPawns = new ArrayList<>(opposingPlayerPaths.entrySet()
                .stream()
                .filter(entry -> entry.getValue().size() == 1 && !(entry.getValue().get(0).size() == 1))
//                .filter(e -> {
//                    int deviation = PiecePathFinderUtil.PATH_DEVIATION.apply(e.getValue().get(0));
//                    return (
////                            deviation >= getMaxCaptures(!white, e.getKey()) ||
//                            deviation >= Math.abs(e.getKey().getY() - e.getValue().get(0).getFirst().getY()) ||
//                            deviation == 0);
//                } )
                    .filter(entry ->
                            pathFinderUtil.findAllPawnPath(board, entry.getValue().get(0).getFirst(), getMaxCaptures(!white, entry.getKey()),
                                    (b, c) -> c.equals(entry.getKey()), !white)
                            .size() == 1)
                .toList());
        singleOriginPawns.addAll(opposingPlayerPaths.entrySet()
                .stream().filter(entry ->entry.getValue().size() == 1 && entry.getValue().get(0).size() == 1).toList());
        if (singleOriginPawns.isEmpty()) {
            // New implementation may hugely impact performance here
            updateP();

            return false;
        }
        List<Path> newPaths = new LinkedList<>();
        singleOriginPawns.forEach(entry -> checkedPlayerPaths.entrySet()
                        .stream()
                        .filter(innerEntry -> !innerEntry.getValue().isEmpty())
                        .filter(innerEntry -> {
                            Coordinate entryKey = entry.getKey();
                            Coordinate innerEntryKey = innerEntry.getKey();
                            if (entry.getValue().get(0).contains(innerEntryKey)
                                    || innerEntry.getValue().get(0).contains(entryKey)) {
                                return true;
                            }
                            int y2 = innerEntryKey.getY();
                            if (y2 == FINAL_RANK_Y || y2 == FIRST_RANK_Y) {
                                return entry.getValue().get(0).contains(innerEntry.getValue().get(0).get(innerEntry.getValue().get(0).size() - 2));
                            }
                            return false;
                        })
                        .forEach(innerEntry -> {
                            //system.out.println("inner entry" + innerEntry);
                            innerEntry.getValue()
                                    .stream().filter(path ->
                                            new PiecePathFinderUtil(detector)
                                                    .pathsExclusive(entry.getValue().get(0), path))
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



        List<Coordinate[]> forRemoval = new LinkedList<>();
        newPaths.stream().forEach(path -> {
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
        updateP();
        return !forRemoval.isEmpty() || !newPaths.isEmpty();
    }

    protected void updateP() {
        this.blackPawnMap.update();
        this.whitePawnMap.update();
    }

    private Path makeExclusiveMaps(BoardInterface board, Path path, boolean white, List<Map.Entry<Coordinate, List<Path>>> forbiddenPaths) {

        Coordinate target = path.getLast();
        return new PiecePathFinderUtil(detector).findShortestPawnPath(board,
                path.getFirst(), getMaxCaptures(white, target),
                (b, c) -> c.equals(target),
                white, true, forbiddenPaths.stream().flatMap(e -> e.getValue().stream()).toList());
    }

    private void makeMaps(BoardInterface board, boolean white) {
        (white ? this.whitePawnMap : this.blackPawnMap)
                .getPawnOrigins().entrySet()
                .stream()
                .forEach(entry -> {
                    List<Path> paths = new LinkedList<>();
                    entry.getValue().stream()
                            .forEach(coordinate -> {
                                Path path =
                                        new PiecePathFinderUtil(this.detector).findShortestPawnPath(
                                                board, coordinate, getMaxCaptures(white, entry.getKey()),
                                                (b, c) -> c.equals(entry.getKey()), white, true, new LinkedList<>()
                                );
                                if (!path.isEmpty()) {

                                    paths.add(path);
                                }
                            });
                    this.detector.getPawnData().getPawnPaths(white).put(entry.getKey(), paths);
                });
    }

    public int getMaxCaptures(boolean white, Coordinate coordinate) {
        return (white ? whitePawnMap : blackPawnMap).getCapturedPieces() + (white ? whitePawnMap : blackPawnMap).getCaptureSet().get(coordinate);
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
        public boolean deduce(BoardInterface board) {
            this.opponentPieceNumbers = board.getBoardFacts().pieceNumbers(!this.white);
            rawMap(board, this.white);
            reduce();
            return false;
        }

        public void update() {
            reduce();
        }
        /**
         * Returns the max number of pieces minus the number of pieces the opponent is missing
         * @return the max number of pieces minus the number of pieces the opponent is missing
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

                                //Sys tem.out.println(entry.getKey());

                                int minCaptures = Math.abs(x - coordinate.getX());
                                //Sys tem.out.println(minCaptures);
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
        public int getCapturedPieces() {
            return capturedPieces;
        }
        public void setCapturedPieces(int capturedPieces) {
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
