package Heuristics.Deductions;

import Heuristics.BoardInterface;
import Heuristics.Path;
import StandardChess.BoardReader;
import StandardChess.Coordinate;
import StandardChess.Coordinates;

import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

public abstract class PawnMap extends AbstractDeduction{
    private List<Set<Coordinate>> sets = new LinkedList<>();
    protected int maxPieces = 16;

    protected boolean white;

    public PawnMap(Boolean white) {
        super("Illegal pawn structure.");
        this.white = white;
    }

    @Override
    public boolean deduce(BoardInterface board) {
        rawMap(board, this.white);


        reduce(this.white);
//        if (this.maxPieces - capturedPieces)
        return false;
    }

    protected void update(boolean white) {
        reduce(white);
    }
    @Override
    public abstract void update();
    /**
     * Returns the max number of pieces minus the number of pieces the opponent is missing
     * @return the max number of pieces minus the number of pieces the opponent is missing
     */
    protected int capturedPieces(boolean white) {
        return this.detector.pawnTakeablePieces(white) - (white
                ? this.detector.getPieceNumber().getBlackPieces()
                : this.detector.getPieceNumber().getWhitePieces());
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
                    this.detector.getPawnOrigins(white).put(pawn, starts);
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
    protected void captures(Map<Coordinate, Path> origins, boolean white) {
        updateCaptureSet(white);
        origins.forEach((key, value) -> {
            int x = key.getX();
            value.removeAll(value.stream()
                    .filter(coordinate -> Math.abs(x - coordinate.getX()) > this.detector.getCapturedPieces(white) + this.detector.getCaptureSet(white).get(key))
                    .toList());
        });
    }

    protected void updateCaptureSet(boolean white) {
        int maxOffset = capturedPieces(white) -
                this.detector.getPawnOrigins(white).entrySet().stream()
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
                            this.detector.getCaptureSet(white).put(entry.getKey(), minCaptures);
                            return minCaptures;})
                        .reduce(Integer::sum)
                        .orElse(0);
        if (maxOffset < 0) {
            this.errorMessage = "Too many pawn captures.";
            this.state = false;
        }
        this.detector.setCapturedPieces(white, maxOffset);
    }
    private void reduce(boolean white) {
        this.sets = new LinkedList<>();
        List<Coordinate> origins = this.detector.getPawnOrigins(white).entrySet().stream()
                .flatMap(f -> f.getValue().stream())
                .collect(Collectors.toSet())
                .stream().toList();

        if (!origins.isEmpty()) {
            List<Coordinate> originsTwo = new LinkedList<>(origins);
            boolean change = true;
            while (change){
                captures(this.detector.getPawnOrigins(white), white);
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
            List<Coordinate> subsets = this.detector.getPawnOrigins(white).entrySet().stream()
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
                subsets.forEach(coordinate -> map.put(coordinate, Path.of(this.detector.getPawnOrigins(white).get(coordinate))));
                set.forEach(coordinate -> this.detector.getOriginFree(white).put(coordinate, false));
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

        removalMap.forEach((key, value) -> this.detector.getPawnOrigins(white).get(key).removeAll(value));

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

        return !this.detector.getPawnOrigins(white).entrySet()
                .stream()
                .filter(entry -> !ignore.contains(entry.getKey()))
                .filter(entry -> entry.getValue().removeAll(forRemoval))
                .toList()
                .isEmpty();
    }
}
