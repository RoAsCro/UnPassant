package Heuristics.Deductions;

import Heuristics.BoardInterface;
import Heuristics.Observation;
import Heuristics.Path;
import StandardChess.BoardReader;
import StandardChess.Coordinate;
import StandardChess.Coordinates;

import java.util.*;

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
                            j = j + finalY - 1;
                            continue;
                        }
                        Coordinate origin = new Coordinate(x, start);
//                        System.out.println(pawn + " : " + origin);
                        if (this.certainOrigins.entrySet().stream()
                                .noneMatch(e -> e.getValue().contains(origin))) {
//                            System.out.println("Success");
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
                            this.uncertainOrigins.remove(entry.getKey());
                        }
                    } else {
                        this.uncertainOrigins.put(pawn, starts);
                    }

//                            .forEach(e -> {
//                                if (e.getValue().equals(starts)) {
//                                    this.certainOrigins.put(pawn, starts);
//                                    this.certainOrigins.put(e.getKey(), e.getValue());
//                                }
//                            })
                    ;
                    System.out.println("-------" + pawn + "--------");
                    this.pawnOrigins.put(pawn, starts);

                }
            });
            for (int i = 0 ; i < 8 ; i++) {
                System.out.println("-------------" + i + "-------------------");
                List<Map.Entry<Coordinate, Path>> newlyCertain = new LinkedList<>();
                this.uncertainOrigins.entrySet()
                        .stream()
                        .forEach(entry -> {
                            System.out.println(entry.getKey() + ":");

//                                System.out.println(":::" + );
                            List<Coordinate> forRemoval = new LinkedList<>();
                            entry.getValue()
                                    .stream()
                                    .forEach(coordinate -> {
                                        System.out.println(coordinate);
                                        if (this.certainOrigins.entrySet()
                                                .stream()
                                                .anyMatch(e -> e.getValue().contains(coordinate))) {
                                            System.out.println("Matches");
                                            forRemoval.add(coordinate);
                                        }
                                    });
                            entry.getValue().removeAll(forRemoval);

                            List<Map.Entry<Coordinate, Path>> list = this.pawnOrigins.entrySet().stream()
                                    .filter(e -> e.getValue().equals(entry.getValue()))
                                    .toList();

                            if (list.size() > 1 || entry.getValue().size() == 1) {
                                list.stream()
                                        .forEach(otherEntry -> {
                                            this.certainOrigins.put(otherEntry.getKey(), otherEntry.getValue());
                                            newlyCertain.add(otherEntry);
                                        });
                            }
                        });
                newlyCertain.forEach(entry -> uncertainOrigins.remove(entry.getKey()));

            }

        }

        return false;
    }

    public Map<Coordinate, Path> getPawnOrigins() {
        return this.pawnOrigins;
    }

}
