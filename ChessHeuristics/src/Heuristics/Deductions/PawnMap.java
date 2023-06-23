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

    private Map<Coordinate, List<Path>> paths;
    private final Map<Coordinate, Path> pawnOrigins = new TreeMap<>();
    List<Observation> observations = new ArrayList<>();
    PawnNumber pawnNumber;
    PieceNumber pieceNumber;

    public PawnMap() {
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
        rawMap(board, colour);
        reduce(colour);

        return false;
    }

    protected int capturedPieces(String colour) {
        return 16 - (colour.equals("white")
                ? this.pieceNumber.getBlackPieces()
                : this.pieceNumber.getWhitePieces());
    }

    public abstract int capturedPieces();

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

    private void captures(String colour) {
        int maxOffset = capturedPieces(colour) -
                this.pawnOrigins.entrySet().stream()
                    .map(entry -> {
                        int x = entry.getKey().getX();
                        return entry.getValue().stream()
                                .map(coordinate -> Math.abs(x - coordinate.getX()))
                                .reduce((i, j) -> {
                                    if (i < j) {
                                        return i;
                                    }
                                    return j;
                                })
                                .orElse(0);
                    })
                    .reduce(Integer::sum)
                    .orElse(0);
//        int offSet = maxOffset;
        this.pawnOrigins.entrySet().stream()
                .forEach(entry -> {
                    int x = entry.getKey().getX();
                    entry.getValue().removeAll(entry.getValue().stream()
                            .filter(coordinate -> Math.abs(x - coordinate.getX()) > maxOffset)
                            .toList());
                });
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
                captures(colour);
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

    private void reduceIter(Set<Coordinate> set, List<Coordinate> origins) {
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
                return;
            }
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
