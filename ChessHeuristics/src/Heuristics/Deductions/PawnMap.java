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

    private int capturedPieces = 0;
    private String colour;

    List<Observation> observations = new ArrayList<>();
    PawnNumber pawnNumber;
    PieceNumber pieceNumber;

    public PawnMap(String colour) {
        this.colour = colour;
        PawnNumber pawnNumber = new PawnNumber();
        this.observations.add(pawnNumber);
        this.pawnNumber = pawnNumber;
        PieceNumber pieceNumber = new PieceNumber();
        this.observations.add(pieceNumber);
        this.pieceNumber = pieceNumber;
    }

    @Override
    public List<Observation> getObservations() {
        return this.observations;
    }

    public boolean deduce(BoardInterface board, String colour) {
        this.observations.forEach(observation -> observation.observe(board));
        rawMap(board, colour);
        reduce(colour);

        return false;
    }


    protected void update(String colour) {
        reduce(colour);
    }

    public abstract void update();

    public void removeOrigins(Coordinate piece, Coordinate origin) {
        this.pawnOrigins.get(piece).remove(origin);
    }

    protected int capturedPieces(String colour) {
        return 16 - (colour.equals("white")
                ? this.pieceNumber.getBlackPieces()
                : this.pieceNumber.getWhitePieces());
    }

    public abstract int capturedPieces();

    public int getMaxCaptures(Coordinate coordinate) {
//        System.out.println(this.captureSet);
//        System.out.println(coordinate);
        return this.capturedPieces + this.captureSet.get(coordinate);
    }

    protected Map<Coordinate, Integer> getCaptureSet(String colour) {
        return this.captureSet;
    }

    public abstract Map<Coordinate, Integer> getCaptureSet();

    private void rawMap(BoardInterface board, String colour) {
        int start = colour.equals("white") ? 1 : 6;
        int increment = colour.equals("white") ? 1 : -1;
        BoardReader reader = board.getReader();
        for (int y = 0 ; y < 6; y++) {
            reader.to(new Coordinate(0, start + y * increment));
            int finalY = y;
            reader.nextWhile(Coordinates.RIGHT, coordinate -> coordinate.getX() < 8, piece -> {
                if (piece.getType().equals("pawn") && piece.getColour().equals(colour)) {
                    Coordinate pawn = reader.getCoord();
                    int potentialPaths = finalY * 2 + 1;
                    Path starts = new Path();
                    for (int j = 0 ; j < potentialPaths ; j++) {
                        int x = (pawn.getX() - finalY) + j;
                        if (x > 7) {
                            break;
                        }
                        if (x < 0) {
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
     * @param colour
     */
    private void captures(Map<Coordinate, Path> origins, String colour) {
        updateCaptureSet(colour);
        origins.entrySet().stream()
                .forEach(entry -> {
                    int x = entry.getKey().getX();
                    entry.getValue().removeAll(entry.getValue().stream()
                            .filter(coordinate -> Math.abs(x - coordinate.getX()) > this.capturedPieces + this.captureSet.get(entry.getKey()))
                            .toList());
                });
    }

    private void updateCaptureSet(String colour) {
        int maxOffset = capturedPieces(colour) -
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
                                    .orElse(new Coordinate(0, 0));

                            int minCaptures = Math.abs(x - coordinate.getX());
                            captureSet.put(entry.getKey(), minCaptures);
                            return minCaptures;})
                        .reduce(Integer::sum)
                        .orElse(0);
        if (maxOffset < 0) {
            this.state = false;
        }
        this.capturedPieces = maxOffset;
    }
    private void reduce(String colour) {
        List<Coordinate> origins = this.pawnOrigins.entrySet().stream()
                .flatMap(f -> f.getValue().stream())
                .collect(Collectors.toSet())
                .stream().toList();
        if (!origins.isEmpty()) {
            List<Coordinate> originsTwo = new LinkedList<>(origins);
            int previous = 0;
            int current = -1;
            while (current != previous){
                captures(this.pawnOrigins, colour);
                previous = current;
                reduceIter(new HashSet<>(), originsTwo);
                current = this.pawnOrigins.values()
                        .stream()
                        .map(LinkedList::size)
                        .reduce(Integer::sum)
                        .orElse(0);
            }
        }
    }

    /**
     * Iterates through every combination of origins looking for set for which
     * there exists an equal number of pieces whose origin sets are a subset of it.
     * If such exists, no other piece may have any origin in that set as one of its possible origins.
     * @param set
     * @param origins
     */
    private void reduceIter(Set<Coordinate> set, List<Coordinate> origins) {
        if (!set.isEmpty()) {
            AtomicBoolean supersets = new AtomicBoolean(false);
            List<Coordinate> subsets = this.pawnOrigins.entrySet().stream()
                    .filter(entry -> {
                        if (entry.getValue().containsAll(set)) {
                            // True if the piece being examined contains every origin in the current set
                            supersets.set(true);
                        }
                        // True if the current set of origins contains every origin in the piece being examined has
                        return set.containsAll(entry.getValue());
                    })
                    .map(Map.Entry::getKey)
                    .toList();
            // If the number of subsets of the current set is the same as the number of origins in the set
            if (subsets.size() == set.size()) {
                // Check the total number of captures

                Map<Coordinate, Path> map = new TreeMap<>();
                subsets.forEach(coordinate -> map.put(coordinate, Path.of(this.pawnOrigins.get(coordinate))));
//                System.out.println(map);
                if (reduceIterHelperStart(map)) {
//                    System.out.println(map);
                }
//                System.out.println("removing");
                removeCoords(set, subsets);
                return;
            }
            // If there does not exist a set that contains this set
            if (!supersets.get()) {
                return;
            }
        }
        for (Coordinate currentCoord : origins) {
            Set<Coordinate> newSet = new HashSet<>(set);
            newSet.add(currentCoord);
            List<Coordinate> newOrigins = new LinkedList<>(origins);
            newOrigins.remove(currentCoord);
            reduceIter(newSet, newOrigins);
        }
    }
    private boolean reduceIterHelperStart(Map<Coordinate, Path> map) {
        boolean originsRemoved = false;
        Map<Coordinate, Path> removalMap = new TreeMap<>();
        List<Coordinate> remainingPawns = new LinkedList<>(map.keySet());

        int maxCaptures = capturedPieces(this.colour);

        for (Coordinate currentPawn : remainingPawns) {

//        Coordinate  = remainingPawns.get(remainingPawns.size()-1);
            List<Coordinate> newRemainingPawns = new LinkedList<>(remainingPawns);
            newRemainingPawns.remove(currentPawn);
            Path forRemoval = new Path();
            if (map.get(currentPawn) == null) {
                System.out.println(currentPawn);
            }
            for (Coordinate currentOrigin : map.get(currentPawn)) {
                List<Coordinate> usedOrigins = new LinkedList<>();

                int totalCaptures = Math.abs(currentPawn.getX() - currentOrigin.getX());
                if (totalCaptures > maxCaptures) {
                    forRemoval.add(currentOrigin);
                    continue;
                }
                if (newRemainingPawns.isEmpty()) {
                    continue;
                }
                usedOrigins.add(currentOrigin);

                if (!reduceIterHelper(usedOrigins, newRemainingPawns, map, totalCaptures)) {
                    forRemoval.add(currentOrigin);
                }
            }
            removalMap.put(currentPawn, forRemoval);
            if (!forRemoval.isEmpty()) {
                originsRemoved = true;
            }
        }

        removalMap.entrySet().stream().forEach(entry -> {
            this.pawnOrigins.get(entry.getKey()).removeAll(entry.getValue());
        });

        return originsRemoved;
    }

    /**
     * Checks there aren't mutually required capture amounts that exceed the maximum capture amount
     * @param map
     * @return
     */
    private boolean reduceIterHelper(List<Coordinate> usedOrigins, List<Coordinate> remainingPawns,
                                     Map<Coordinate, Path> map, int totalCaptures) {
        int maxCaptures = capturedPieces(this.colour);

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
                System.out.println("empty" + currentPawn);

                return true;
            }
            List<Coordinate> usedOriginsTwo = new LinkedList<>(usedOrigins);
            usedOriginsTwo.add(currentOrigin);

            if (reduceIterHelper(usedOriginsTwo, remainingPawns, map, newTotalCaptures)) {
                return true;
            }

        }
        System.out.println(totalCaptures);
        System.out.println(remainingPawns);
        System.out.println(usedOrigins);
        return false;
    }

    private void removeCoords(Set<Coordinate> forRemoval, List<Coordinate> ignore) {
        this.pawnOrigins.entrySet()
                .stream()
                .filter(entry -> !ignore.contains(entry.getKey()))
                .forEach(entry -> entry.getValue().removeAll(forRemoval));
    }

    public Map<Coordinate, Path> getPawnOrigins() {
        return this.pawnOrigins;
    }

}
