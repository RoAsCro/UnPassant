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
                        Coordinate origin = new Coordinate(x, start);
                        if (this.certainOrigins.entrySet().stream()
                                .noneMatch(e -> e.getValue().contains(origin))) {
                            starts.add(origin);
                        }
                    }

                    if (starts.size() == 0) {
                        this.state = false;
                    }
                    Optional<Map.Entry<Coordinate, Path>> entryOptional = this.pawnOrigins.entrySet().stream()
                            .filter(e -> e.getValue().equals(starts))
                            .findAny();
                    if (entryOptional.isPresent() || starts.size() == 1) {
                        this.certainOrigins.put(pawn, starts);
                        if (entryOptional.isPresent()) {
                            Map.Entry<Coordinate, Path> entry = entryOptional.get();
                            this.certainOrigins.put(entry.getKey(), entry.getValue());
//                            this.uncertainOrigins.remove(entry.getKey());
                        }
                    }
//                    else {
//                        this.uncertainOrigins.put(pawn, starts);
//                    }
                    this.pawnOrigins.put(pawn, starts);

                }
            });

//            for (int i = 0 ; i < 8 ; i++) {
//                List<Map.Entry<Coordinate, Path>> newlyCertain = new LinkedList<>();
//                this.uncertainOrigins.entrySet()
//                        .stream()
//                        .forEach(entry -> {
//                            List<Coordinate> forRemoval = new LinkedList<>();
//                            entry.getValue()
//                                    .stream()
//                                    .forEach(coordinate -> {
//                                        if (this.certainOrigins.entrySet()
//                                                .stream()
//                                                .anyMatch(e -> e.getValue().contains(coordinate))) {
//                                            forRemoval.add(coordinate);
//                                        }
//                                    });
//                            entry.getValue().removeAll(forRemoval);
//
//                            List<Map.Entry<Coordinate, Path>> list = this.pawnOrigins.entrySet().stream()
//                                    .filter(e -> e.getValue().equals(entry.getValue()))
//                                    .toList();
//
//                            if (list.size() > 1 || entry.getValue().size() == 1) {
//                                list.stream()
//                                        .forEach(otherEntry -> {
//                                            this.certainOrigins.put(otherEntry.getKey(), otherEntry.getValue());
//                                            newlyCertain.add(otherEntry);
//                                        });
//                            }
//                        });
//                newlyCertain.forEach(entry -> uncertainOrigins.remove(entry.getKey()));
//
//            }
            findMapTwo();
        }

        return false;
    }

    private void findMapTwo() {
        System.out.println("ITERSTART");
        List<Coordinate> origins = this.pawnOrigins.entrySet().stream()
                .flatMap(f -> f.getValue().stream())
                .collect(Collectors.toSet())
                .stream().toList();
        if (!origins.isEmpty()) {
            Coordinate firstCoord = origins.get(0);
//            Set<Coordinate> toSearch = new HashSet<>();
//            toSearch.add(firstCoord);
            List<Coordinate> originsTwo = new LinkedList<>(origins);
            originsTwo.remove(firstCoord);
            int previous = 0;
            int current = -1;
            while (current != previous){
                previous = current;
                current = findMapTwoIter(new HashSet<>(), originsTwo);
            }
        }
    }

    private int findMapTwoIter(Set<Coordinate> set, List<Coordinate> origins) {
        if (!set.isEmpty()) {
            System.out.println(set);
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
                System.out.println("A");
                return 1;
            }
            if (!supersets.get()) {
                System.out.println("B");
                return 0;
            }
        }
        int change = 0;
        for (Coordinate currentCoord : origins) {
            System.out.println(currentCoord);
            Set<Coordinate> newSet = new HashSet<>();
            newSet.addAll(set);
            newSet.add(currentCoord);
            List<Coordinate> newOrigins = new LinkedList<>();
            newOrigins.addAll(origins);
            newOrigins.remove(currentCoord);
            change += findMapTwoIter(newSet, newOrigins);
        }
        return change;
    }

    private void removeCoords(Set<Coordinate> forRemoval, List<Coordinate> ignore) {
        this.pawnOrigins.entrySet()
                .stream()
                .filter(entry -> !ignore.contains(entry.getKey()))
                .forEach(entry -> entry.getValue().removeAll(forRemoval));
    }

//    private void findMap(Map.Entry<Coordinate, Path> mainEntry, List<Coordinate> toSearch) {
//
//        List<Coordinate> toSearchNext = new LinkedList<>();
//        List<Coordinate> newlyCertain = new LinkedList<>();
//        List<Coordinate> intersections = new LinkedList<>();
//        List<Coordinate> subsets = this.uncertainOrigins.entrySet()
//                .stream()
//                .filter(secondEntry -> toSearch.contains(secondEntry.getKey()))
//                .filter(secondEntry -> {
//                    if (secondEntry.getValue().stream().anyMatch(c -> {mainEntry.getValue().})){
//                        intersections.add(secondEntry.getKey());
//                    }
//                    boolean subset = mainEntry.getValue().containsAll(secondEntry.getValue());
//                    if (!subset) {
//                        toSearchNext.add(secondEntry.getKey());
//                    }
//                    return subset;})
//                .map(Map.Entry::getKey)
//                .toList();
//        if (subsets.size() == mainEntry.getValue().size()) {
//            newlyCertain.addAll(subsets);
//        } else
//        newlyCertain.forEach(coordinate -> {
//            this.certainOrigins.put(coordinate, uncertainOrigins.get(coordinate));
//            this.uncertainOrigins.remove(coordinate);
//        });
//    }

    public Map<Coordinate, Path> getPawnOrigins() {
        return this.pawnOrigins;
    }

}
