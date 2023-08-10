package Heuristics.Deductions;

import Heuristics.BoardInterface;
import Heuristics.Observation;
import Heuristics.Observations.PawnNumber;
import Heuristics.Observations.PieceNumber;
import Heuristics.Path;
import StandardChess.BoardReader;
import StandardChess.Coordinate;
import StandardChess.Coordinates;

import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

public abstract class PawnMap extends AbstractDeduction{

    private final Map<Coordinate, Path> pawnOrigins = new TreeMap<>();
    private final Map<Coordinate, Integer> captureSet = new TreeMap<>();
    private final Map<Coordinate, Boolean> originFree = new TreeMap<>();

    private List<Set<Coordinate>> sets = new LinkedList<>();

    protected int capturedPieces = 0;

    protected int maxPieces = 16;

    protected boolean white;

    List<Observation> observations = new ArrayList<>();
    PawnNumber pawnNumber;
    PieceNumber pieceNumber;

    public PawnMap(PawnMap pawnMap) {
        this.white = pawnMap.white;
        this.observations = pawnMap.observations;
        this.pawnNumber = pawnMap.pawnNumber;
        this.pieceNumber = pawnMap.pieceNumber;
        pawnMap.pawnOrigins.forEach((k, v) -> this.pawnOrigins.put(k, Path.of(v)));
        this.captureSet.putAll(pawnMap.captureSet);
        this.originFree.putAll(pawnMap.originFree);
        this.capturedPieces = pawnMap.capturedPieces;
        this.maxPieces = pawnMap.maxPieces;
        this.sets = pawnMap.sets;
    }

    public PawnMap(Boolean white, PawnNumber pawnNumber, PieceNumber pieceNumber) {
        this.white = white;
        this.observations.add(pawnNumber);
        this.pawnNumber = pawnNumber;
        this.observations.add(pieceNumber);
        this.pieceNumber = pieceNumber;
        for (int i = 0; i < 8 ; i++ ) {
            this.originFree.put(new Coordinate(i, Math.abs((this.white ? FIRST_RANK_Y : FINAL_RANK_Y) - 1)), true);
        }
    }

    @Override
    public List<Observation> getObservations() {
        return this.observations;
    }

    @Override
    public boolean deduce(BoardInterface board) {
        this.observations.forEach(observation -> observation.observe(board));
        rawMap(board, this.white);


        reduce(this.white);
//        if (this.maxPieces - capturedPieces)

        return false;
    }

    private int capturablePieces(boolean white) {
        return this.maxPieces - (white ? this.pieceNumber.getWhitePieces() : this.pieceNumber.getBlackPieces());
    }

    public int capturablePieces() {
        return capturablePieces(this.white);
    }



    protected void update(boolean white) {
        reduce(white);
    }

    public abstract void update();

    public void removeOrigins(Coordinate piece, Coordinate origin) {
        this.pawnOrigins.get(piece).remove(origin);
    }

    /**
     * Returns the max number of pieces minus the number of pieces the opponent is missing
     * @return the max number of pieces minus the number of pieces the opponent is missing
     */
    protected int capturedPieces(boolean white) {
        return this.maxPieces - (white
                ? this.pieceNumber.getBlackPieces()
                : this.pieceNumber.getWhitePieces());
    }

    public Map<Coordinate, Boolean> getOriginFree() {
        return this.originFree;
    }

    /**
     * When pieces are accounted for elsewhere, the maxPieces needs to be updated
     * @param subtrahend the number of pieces that cannot be captured by pawns
     */
    public void updateMaxCapturedPieces(int subtrahend) {
        this.maxPieces -= subtrahend;
    }

    public abstract int capturedPieces();

    public int getMaxCaptures(Coordinate coordinate) {
        return this.capturedPieces + this.captureSet.get(coordinate);
    }

    protected Map<Coordinate, Integer> getCaptureSet() {
        return this.captureSet;
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
                    this.pawnOrigins.put(pawn, starts);
                }
            });
        }
    }

    /**
     * Finds the number of opposing pieces missing x, the minimum number of pieces a given pawn can have taken y,
     * and the sum of all y's z,
     * then removes any origins from the sets of origins of a given pawn which require taking a minimum of
     * greater than (x - z) + y captures
     */
    protected void captures(Map<Coordinate, Path> origins, boolean white) {
        updateCaptureSet(white);
//        System.out.println("breaks" + this.pawnOrigins);

        origins.forEach((key, value) -> {
            int x = key.getX();
            value.removeAll(value.stream()
                    .filter(coordinate -> Math.abs(x - coordinate.getX()) > this.capturedPieces + this.captureSet.get(key))
                    .toList());
        });
//        System.out.println("breaks2" + this.pawnOrigins);

    }

    protected void updateCaptureSet(boolean white) {
        int maxOffset = capturedPieces(white) -
                this.pawnOrigins.entrySet().stream()
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
            this.state = false;
        }
        this.capturedPieces = maxOffset;

    }
    private void reduce(boolean white) {
        this.sets = new LinkedList<>();
        List<Coordinate> origins = this.pawnOrigins.entrySet().stream()
                .flatMap(f -> f.getValue().stream())
                .collect(Collectors.toSet())
                .stream().toList();
        if (!origins.isEmpty()) {
            List<Coordinate> originsTwo = new LinkedList<>(origins);
            boolean change = true;
            while (change){

//                this.sets = ;

                captures(this.pawnOrigins, white);
//                long s = System.currentTimeMillis();
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
            List<Coordinate> subsets = this.pawnOrigins.entrySet().stream()
                    .filter(entry -> {
                        Path path = entry.getValue();
                        if (path.stream().anyMatch(set::contains)) {
                            // True if the piece being examined contains every origin in the current set
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
//                return false;
            }

            if (subsetSize == setSize) {

                // Check the total number of captures

                Map<Coordinate, Path> map = new TreeMap<>();
                subsets.forEach(coordinate -> map.put(coordinate, Path.of(this.pawnOrigins.get(coordinate))));
                set.forEach(coordinate -> this.originFree.put(coordinate, false));
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

        int maxCaptures = capturedPieces(this.white);

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

        removalMap.forEach((key, value) -> this.pawnOrigins.get(key).removeAll(value));

    }

    /**
     * Checks there aren't mutually required capture amounts that exceed the maximum capture amount
     */
    private boolean reduceIterHelper(List<Coordinate> usedOrigins, List<Coordinate> remainingPawns,
                                     Map<Coordinate, Path> map, int totalCaptures) {
        int maxCaptures = capturedPieces(this.white);

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
        return !this.pawnOrigins.entrySet()
                .stream()
                .filter(entry -> !ignore.contains(entry.getKey()))
                .filter(entry -> entry.getValue().removeAll(forRemoval))
                .toList()
                .isEmpty();
    }

    public Map<Coordinate, Path> getPawnOrigins() {
        return this.pawnOrigins;
    }

}
