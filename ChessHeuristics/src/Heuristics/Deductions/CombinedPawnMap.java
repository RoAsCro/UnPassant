package Heuristics.Deductions;

import Heuristics.BoardInterface;
import Heuristics.Observation;
import Heuristics.Path;
import Heuristics.Pathfinder;
import StandardChess.Coordinate;
import StandardChess.StandardPieceFactory;

import java.util.*;
import java.util.function.Function;

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
        // This is potentially coded incorrectly, see method below
        Map<Coordinate, List<Path>> player = colour.equals("white") ? this.whitePaths : this.blackPaths;

        return player.values().stream().map(paths -> paths.stream().map(PATH_DEVIATION)
                        .reduce((integer, integer2) -> integer > integer2 ? integer : integer2)
                        .orElse(0)
                )
                .reduce(Integer::sum)
                .orElse(0);
    }

    public int capturesTwo(String colour) {
        Map<Coordinate, List<Path>> player = colour.equals("white") ? this.whitePaths : this.blackPaths;

        return player.values().stream().map(paths -> paths.stream().map(PATH_DEVIATION)
                        .reduce((integer, integer2) -> integer < integer2 ? integer : integer2)
                        .orElse(0)
                )
                .reduce(Integer::sum)
                .orElse(0);
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
            Map<Coordinate, List<Path>> startingWhite = Map.copyOf(this.whitePaths);
            Map<Coordinate, List<Path>> startingBlack = Map.copyOf(this.blackPaths);
            Map<Coordinate, Path> startingPawnOriginsWhite = Map.copyOf(this.white.getPawnOrigins());
            Map<Coordinate, Path> startingPawnOriginsBlack = Map.copyOf(this.black.getPawnOrigins());


//            System.out.println("1A" + black.getMaxCaptures(new Coordinate(0, 4)));
            System.out.println("change");
            System.out.println(startingWhite);

            System.out.println(startingPawnOriginsWhite);
            System.out.println(startingPawnOriginsBlack);
            makeMaps(board, false);

            makeMaps(board, true);




            System.out.println(this.getWhitePaths());
            System.out.println(this.getBlackPaths());

            System.out.println("CCCCCC" + this.whitePaths);

            if ((!exclude(board, true) & !exclude(board, false))
                    || (startingWhite.values().containsAll(this.whitePaths.values())
                    && startingBlack.values().containsAll(this.blackPaths.values())
                    && startingPawnOriginsWhite.values().containsAll(this.white.getPawnOrigins().values())
                    && startingPawnOriginsBlack.values().containsAll(this.black.getPawnOrigins().values()))
            ) {
                changed = false;
            }

            System.out.println("CHANGES HERE: ");
            System.out.println(startingWhite);
            System.out.println(this.whitePaths);
            System.out.println(startingWhite.values().equals(this.whitePaths));
            System.out.println(startingBlack);
            System.out.println(this.blackPaths);
            System.out.println(startingBlack.values().equals(this.blackPaths));
            System.out.println(startingPawnOriginsWhite);
            System.out.println(this.white.getPawnOrigins());
            System.out.println(startingPawnOriginsBlack);
            System.out.println(this.black.getPawnOrigins());





        }
        System.out.println("CPM INFO:");

        System.out.println(white.getPawnOrigins());
        System.out.println(black.getPawnOrigins());
        System.out.println(black.getCaptureSet());
//        System.out.println(black.getMaxCaptures(new Coordinate(0, 4)));


        System.out.println(whitePaths);
        System.out.println(blackPaths);

        if (this.whitePaths.values().stream().anyMatch(List::isEmpty) || this.blackPaths.values().stream().anyMatch(List::isEmpty)
                || !this.white.state || ! this.black.state) {
            this.state = false;
        }

        return false;
    }

    public Path getSinglePath(String colour, Coordinate coordinate) {
//        if (coordinate.equals(new Coordinate(0, 2))) {
////            System.out.println(this.singleWhitePaths);
//        }
        return (colour.equals("white") ? this.singleWhitePaths : this.singleBlackPaths).get(coordinate);
    }
    public Map<Coordinate, Path> getSinglePath(String colour) {
//        if (coordinate.equals(new Coordinate(0, 2))) {
////            System.out.println(this.singleWhitePaths);
//        }
        return (colour.equals("white") ? this.singleWhitePaths : this.singleBlackPaths);
    }

    /**
     *
     *
     * @param board
     * @param white
     * @return whether or not there was a change
     */
    protected boolean exclude(BoardInterface board, boolean white) {
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
        System.out.println("CPP" + checkedPlayerPaths);

        // Find every pawn of the opposing player with one origin and one possible path
        System.out.println(opposingPlayerPaths);
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
        System.out.println("SOP" + singleOriginPawns);


        singleOriginPawns.forEach(entry -> (white ? singleBlackPaths : singleWhitePaths).put(entry.getKey(), entry.getValue().get(0)));


        List<Path> newPaths = new LinkedList<>();
        singleOriginPawns
                .stream()
                .forEach(entry -> {
                    checkedPlayerPaths.entrySet()
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
                                if (y2 == 7 || y2 == 0) {
                                    System.out.println(innerEntryKey);

                                    System.out.println(innerEntry.getValue().get(0));
                                    return entry.getValue().get(0).contains(innerEntry.getValue().get(0).get(innerEntry.getValue().get(0).size() - 2));
                                }
                                return false;
                            })
                            .forEach(innerEntry -> {
                                System.out.println("inner entry" + innerEntry);
                                innerEntry.getValue()
                                        .stream().filter(path -> Pathfinder.pathsExclusive(entry.getValue().get(0), path))
                                        .forEach(path -> {
                                            System.out.println("checking..." + path);
                                            System.out.println("entry..." + entry);

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
        System.out.println("paths " + newPaths);


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
        System.out.println("removing" + forRemoval);
        System.out.println(checkedPlayer.getPawnOrigins());
        forRemoval.forEach(coordinates -> checkedPlayer.removeOrigins(coordinates[0], coordinates[1]));
        System.out.println(checkedPlayer.getPawnOrigins());
//        System.out.println("Updadting...");
        checkedPlayer.update();
//        System.out.println("All paths 2: " + checkedPlayerPaths);

        return !forRemoval.isEmpty()
                || !newPaths.isEmpty()
                ;
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
                    System.out.println(
                            p1 + " vs " + p2
                    );
                    System.out.println(forbiddenPaths);

                    boolean p1NotExclusive;
                    if (p1.isEmpty()) {
                        p1NotExclusive = true;
                    } else {
                        p1NotExclusive = forbiddenPaths
                                .stream()
                                .noneMatch(entry ->
                                        // What is commented out below may greatly affect performance
                                        // now that it is commented out - however, it allows theoretical collision
                                        // Before reinstating, create new checks
//                                        p1.contains(entry.getKey()) &&
                                                Pathfinder.pathsExclusive(p1, entry.getValue().get(0)));
                    }
                    boolean p2NotExclusive = forbiddenPaths
                            .stream()
                            .noneMatch(entry ->
                                    // See above before deleting
//                                    (p2.contains(entry.getKey()) || entry.getValue().get(0).contains(p2.getLast())) &&
                                            Pathfinder.pathsExclusive(p2, entry.getValue().get(0)));
                    if (!p1NotExclusive && !p2NotExclusive) {
                        return new Path();
                    }
                    if (!p1NotExclusive) {
                        return p2;
                    }
                    if (!p2NotExclusive) {
                        System.out.println("p2 exclusive");
                        return p1;
                    }

                    if (!p1.isEmpty() && PATH_DEVIATION.apply(p1) < PATH_DEVIATION.apply(p2)) {
                        return p1;
                    }
                    System.out.println("both not exclusive");
                    return p2;
                }
                );
        System.out.println("new path = " + newPath);
//        System.out.println(PATH_DEVIATION.apply(newPath));
//        System.out.println(player.getMaxCaptures(newPath.getLast()));


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
                                if (!path.isEmpty()) {
                                    paths.add(path);
                                }
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
