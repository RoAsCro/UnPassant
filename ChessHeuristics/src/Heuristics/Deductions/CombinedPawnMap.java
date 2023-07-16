package Heuristics.Deductions;

import Heuristics.BoardInterface;
import Heuristics.Observation;
import Heuristics.Path;
import Heuristics.Pathfinder;
import StandardChess.Coordinate;
import StandardChess.StandardPieceFactory;

import java.util.*;
import java.util.function.BiPredicate;
import java.util.function.Function;
import java.util.function.Predicate;

public class CombinedPawnMap extends AbstractDeduction {
    PawnMap white;
    PawnMap black;

    private final Map<Coordinate, List<Path>> whitePaths = new TreeMap<>();
    private final Map<Coordinate, List<Path>> blackPaths = new TreeMap<>();

    private final Map<Coordinate, Path> singleWhitePaths = new TreeMap<>();
    private final Map<Coordinate, Path> singleBlackPaths = new TreeMap<>();
    public static final Function<Path, Integer> PATH_DEVIATION = p -> p.stream()
            .reduce(new Coordinate(p.get(0).getX(), 0), (c, d) -> {
                if (c.getX() != d.getX()) {
                    return new Coordinate(d.getX(), c.getY() + 1);
                }
                return c;
            }).getY();


    public CombinedPawnMap(PawnMap white, PawnMap black) {
        this.black = black;
        this.white = white;
    }

    public int captures(String colour) {
        Map<Coordinate, List<Path>> player = colour.equals("white") ? this.whitePaths : this.blackPaths;

        return player.values().stream().map(paths -> paths.stream().map(PATH_DEVIATION)
                        .reduce((integer, integer2) -> integer > integer2 ? integer : integer2)
                        .orElse(0)
                )
                .reduce(Integer::sum)
                .orElse(0);
//    PATH_DEVIATION
    }

    @Override
    public List<Observation> getObservations() {
        List<Observation> observations = new LinkedList<>(this.black.getObservations());
        observations.addAll(this.white.getObservations());
        return observations;
    }

    @Override
    public boolean deduce(BoardInterface board) {
        this.black.deduce(board);
        this.white.deduce(board);
        boolean changed = true;
        while (changed) {
//            System.out.println("change");
            makeMaps(board, false);
            makeMaps(board, true);
            if (!exclude(board, true) & !exclude(board, false)) {
                changed = false;
            }

        }
        return false;
    }

    public Path getSinglePath(String colour, Coordinate coordinate) {
//        if (coordinate.equals(new Coordinate(0, 2))) {
////            System.out.println(this.singleWhitePaths);
//        }
        return (colour.equals("white") ? this.singleWhitePaths : this.singleBlackPaths).get(coordinate);
    }

    /**
     *
     *
     * @param board
     * @param white
     * @return whether or not there was a change
     */
    private boolean exclude(BoardInterface board, boolean white) {
        PawnMap checkedPlayer = white
                ? this.white
                : this.black;
        PawnMap opposingPlayer = white
                ? this.black
                : this.white;
        Map<Coordinate, List<Path>> checkedPlayerPaths = white
                ? this.whitePaths
                : this.blackPaths;

        Map<Coordinate, List<Path>> opposingPlayerPaths = white
                ? this.blackPaths
                : this.whitePaths;
        // Find every pawn of the opposing player with one origin and one possible path
        List<Map.Entry<Coordinate, List<Path>>> singleOriginPawns = opposingPlayerPaths.entrySet()
                .stream()
                .filter(entry -> entry.getValue().size() == 1 && !(entry.getValue().get(0).size() == 1))
                .filter(entry -> Pathfinder.findAllPawnPaths(
                                StandardPieceFactory.getInstance().getPiece(white ? "p" : "P"),
                                entry.getValue().get(0).getFirst(),
                                (b, c) -> c.equals(entry.getKey()),
                                board,
                                p -> PATH_DEVIATION.apply(p) <= opposingPlayer.getMaxCaptures(entry.getKey()))
                        .size() == 1)
                .toList();
        singleOriginPawns.forEach(entry -> (white ? singleBlackPaths : singleWhitePaths).put(entry.getKey(), entry.getValue().get(0)));

        List<Path> newPaths = new LinkedList<>();
        singleOriginPawns
                .stream()
                .forEach(entry -> {
                    checkedPlayerPaths.entrySet()
                            .stream()
                            .filter(innerEntry -> entry.getValue().get(0).contains(innerEntry.getKey()))
                            .forEach(innerEntry -> {
                                innerEntry.getValue()
                                        .stream().filter(path -> Pathfinder.pathsExclusive(entry.getValue().get(0), path))
                                        .forEach(path -> {
//                                            System.out.println("checking..." + path);
                                            Path toPut = makeExclusiveMaps(board, path, white, singleOriginPawns);
                                            if (toPut.isEmpty()) {
//                                                System.out.println("path");
                                                toPut.add(path.getFirst());
                                                toPut.add(new Coordinate(-1, -1));
                                                toPut.add(innerEntry.getKey());
                                            } else {
//                                                System.out.println(toPut);
                                            }
//                                            System.out.println(toPut);
                                            newPaths.add(toPut);
//                                            System.out.println(newPaths);
                                        });
                            });
                });
        List<Coordinate[]> forRemoval = new LinkedList<>();
//        System.out.println("paths " + newPaths);


        newPaths.stream().forEach(path -> {
            List<Path> pathList = checkedPlayerPaths.get(path.getLast());
            Path toRemove = pathList
                    .stream().filter(path2 -> path2.getFirst() == path.getFirst())
                    .findFirst()
                    .orElse(null);
            pathList.remove(toRemove);
            if (!(path.contains(new Coordinate(-1, -1)))) {
                pathList.add(path);
            } else {
//                System.out.println("remove:" + path);

                forRemoval.add(new Coordinate[]{path.getLast(), path.getFirst()});
            }
        });
//        System.out.println(forRemoval);
        forRemoval.forEach(coordinates -> checkedPlayer.removeOrigins(coordinates[0], coordinates[1]));
//        System.out.println("Updadting...");
        checkedPlayer.update();
//        System.out.println("All paths 2: " + checkedPlayerPaths);

        return !forRemoval.isEmpty();
    }

    private Path makeExclusiveMaps(BoardInterface board, Path path, boolean white, List<Map.Entry<Coordinate, List<Path>>> forbiddenPaths) {
//        System.out.println(path);
        PawnMap player = !white
                ? this.black
                : this.white;

        //TODO current forbidden paths won't contain paths that are one coordinate long
//        System.out.println("forbidden:" + forbiddenPaths);
        Path newPath = Pathfinder.findShortestPawnPath(StandardPieceFactory.getInstance().getPiece(!white ? "p" : "P"),
                path.getFirst(),
                (b, c) -> c.equals(path.getLast()),
                board,
                p -> PATH_DEVIATION.apply(p) <= player.getMaxCaptures(path.getLast()),
                (p1, p2) -> {
//                    System.out.println(
//                            p1 + " vs " + p2
//                    );
                    boolean p1NotExclusive;
                    if (p1.isEmpty()) {
                        p1NotExclusive = true;
                    } else {

                        p1NotExclusive = forbiddenPaths
                                .stream()
                                .noneMatch(entry -> p1.contains(entry.getKey())
                                        && Pathfinder.pathsExclusive(p1, entry.getValue().get(0)));
                    }
                    boolean p2NotExclusive = forbiddenPaths
                            .stream()
                            .noneMatch(entry -> p2.contains(entry.getKey())
                                    && Pathfinder.pathsExclusive(p2, entry.getValue().get(0)));
                    if (!p1NotExclusive && !p2NotExclusive) {
                        return new Path();
                    }
                    if (!p1NotExclusive) {
                        return p2;
                    }
                    if (!p2NotExclusive) {
//                        System.out.println("Confirmed");
                        return p1;
                    }

                    if (!p1.isEmpty() && PATH_DEVIATION.apply(p1) < PATH_DEVIATION.apply(p2)) {
                        return p1;
                    }
                    return p2;
                }
                );

        return newPath;
    }

    private void makeMaps(BoardInterface board, boolean white) {
        PawnMap player = white
                ? this.white
                : this.black;
        player.getPawnOrigins().entrySet()
                .stream()
                .forEach(entry -> {
                    List<Path> paths = new LinkedList<>();
                    entry.getValue().stream()
                            .forEach(coordinate -> {
                                Path path = Pathfinder.findShortestPath(
                                        StandardPieceFactory.getInstance().getPiece(white ? "P" : "p"),
                                        coordinate,
                                        (b, c) -> c.equals(entry.getKey()),
                                        board,
                                        p -> PATH_DEVIATION.apply(p) <= player.getMaxCaptures(entry.getKey()),
                                        (p1, p2) -> PATH_DEVIATION.apply(p1) < PATH_DEVIATION.apply(p2)
                                );
                                paths.add(path);
                            });
                    if (white) {
                        this.whitePaths.put(entry.getKey(), paths);
                    } else {
                        this.blackPaths.put(entry.getKey(), paths);
                    }
                });
    }


    public Map<Coordinate, List<Path>> getWhitePaths() {
        return this.whitePaths;
    }

    public Map<Coordinate, List<Path>> getBlackPaths() {
        return this.blackPaths;
    }
}
