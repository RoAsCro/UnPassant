package Heuristics.Deductions;

import Heuristics.BoardInterface;
import Heuristics.Observation;
import Heuristics.Path;
import StandardChess.BoardReader;
import StandardChess.Coordinate;
import StandardChess.Coordinates;

import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

public abstract class PawnMap extends AbstractDeduction{

    private Map<Coordinate, List<Path>> paths;
    private final Map<Coordinate, Path> pawnOrigins = new TreeMap<>(Comparator.comparingInt(Coordinate::hashCode));
    private final Map<Coordinate, Path> uncertainOrigins = new TreeMap<>(Comparator.comparingInt(Coordinate::hashCode));
    private final Map<Coordinate, Path> certainOrigins = new TreeMap<>(Comparator.comparingInt(Coordinate::hashCode));


    @Override
    public List<Observation> getObservations() {
        return null;
    }

    public boolean deduce(BoardInterface board, String colour) {
        rawMap(board, colour);
        reduce();
        return false;
    }

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

    private void reduce() {
        List<Coordinate> origins = this.pawnOrigins.entrySet().stream()
                .flatMap(f -> f.getValue().stream())
                .collect(Collectors.toSet())
                .stream().toList();
        if (!origins.isEmpty()) {
            List<Coordinate> originsTwo = new LinkedList<>(origins);
            int previous = 0;
            int current = -1;
            while (current != previous){
                previous = current;
                current = reduceIter(new HashSet<>(), originsTwo);
            }
        }
    }

    private int reduceIter(Set<Coordinate> set, List<Coordinate> origins) {
        if (!set.isEmpty()) {
            AtomicBoolean supersets = new AtomicBoolean(false);
            List<Coordinate> subsets = this.pawnOrigins.entrySet().stream()
                    .filter(entry -> {
                        if (entry.getValue().containsAll(set)) {
                            supersets.set(true);
                        }
                        return set.containsAll(entry.getValue());
                    })
                    .map(Map.Entry::getKey)
                    .toList();
            if (subsets.size() == set.size()) {
                removeCoords(set, subsets);
                return 1;
            }
            if (!supersets.get()) {
                return 0;
            }
        }
        int change = 0;
        for (Coordinate currentCoord : origins) {
            Set<Coordinate> newSet = new HashSet<>(set);
            newSet.add(currentCoord);
            List<Coordinate> newOrigins = new LinkedList<>(origins);
            newOrigins.remove(currentCoord);
            change += reduceIter(newSet, newOrigins);
        }
        return change;
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
