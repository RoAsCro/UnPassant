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


    public CombinedPawnMap(PawnMap white, PawnMap black, CombinedPawnMap combinedPawnMap) {
        combinedPawnMap.whitePaths.forEach((k, v) -> {
            this.whitePaths.put(k,
                    v.stream().map(Path::of).toList());
        });
        combinedPawnMap.blackPaths.forEach((k, v) -> {
            this.blackPaths.put(k,
                    v.stream().map(Path::of).toList());
        });

        combinedPawnMap.singleWhitePaths.forEach((k, v) -> this.singleWhitePaths.put(k, Path.of(v)));
        combinedPawnMap.singleBlackPaths.forEach((k, v) -> this.singleBlackPaths.put(k, Path.of(v)));


        this.black = black;
        this.white = white;
    }

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

    /**
     * return the minimum number of captures made by pawns
     * @param colour
     * @return
     */
    public int capturesTwo(boolean white) {
        Map<Coordinate, List<Path>> player = white ? this.whitePaths : this.blackPaths;

        return player.values().stream().map(paths -> paths.stream().map(PATH_DEVIATION)
                        .reduce((integer, integer2) -> integer < integer2 ? integer : integer2)
                        .orElse(0)
                )
                .reduce(Integer::sum)
                .orElse(0);
    }

    /**
     * Returns the number of pieces the given player can capture:
     * the opposing player's maxPieces minus the number of pieces the opposing player has on the board
     * @param white
     * @return
     */
    public int capturablePieces(boolean white) {
        return (white ? this.black : this.white).capturablePieces();
    }

    @Override
    public List<Observation> getObservations() {
        List<Observation> observations = new LinkedList<>(this.black.getObservations());
        observations.addAll(this.white.getObservations());
        return observations;
    }

    @Override
    public boolean deduce(BoardInterface board) {
//        this.black.deduce(board);
//        this.white.deduce(board);

//        System.out.println(this.black.getPawnOrigins());
        boolean changed = true;
        boolean another = true;
        while (changed) {
            HashSet<List<Path>> startingWhite = new HashSet<>(this.whitePaths.values());
            HashSet<List<Path>> startingBlack = new HashSet<>(this.blackPaths.values());
//            Map<Coordinate, Path> startingPawnOriginsWhite = Map.copyOf(this.white.getPawnOrigins());
//            Map<Coordinate, Path> startingPawnOriginsBlack = Map.copyOf(this.black.getPawnOrigins());

            makeMaps(board, false);
            makeMaps(board, true);
//            System.out.println("Time1:" + ((System.nanoTime() - start1) / 1000));
            if ((!exclude(board, true) & !exclude(board, false))
//                    || (startingWhite.entrySet()
//                    .stream().allMatch(l -> {
//                        if (this.whitePaths.get(l.getKey()).size() < l.getValue().size()) {
//                            return false;
//                        }
//                        for (int i = 0 ; i < l.getValue().size() ; i++) {
//
//                            if (!l.getValue().equals(this.whitePaths.get(l.getKey()).get(i))) {
//                                return false;
//                            }
//                        }
//                        return true;
//                    })
//                    &&
////                    startingBlack.values().containsAll(this.blackPaths.values())
//                    startingBlack.entrySet()
//                    .stream().allMatch(l -> new HashSet<>(l.getValue()).containsAll(this.blackPaths.get(l.getKey())))
////                    && startingPawnOriginsWhite.values().containsAll(this.white.getPawnOrigins().values())
////                    && startingPawnOriginsBlack.values().containsAll(this.black.getPawnOrigins().values())
//            )
                    || (startingWhite.containsAll(new HashSet<>(this.whitePaths.values()))
                    && startingBlack.containsAll(new HashSet<>(this.blackPaths.values())))
            ) {
                if (!another) {
                    changed = false;
                } else {
                    another = false;
                }
            }

//            System.out.println("Time2:" + ((System.nanoTime() - start1)/ 1000000));



        }

//        System.out.println("Time2:" + ((System.nanoTime() - start)/ 10000));

        if (this.whitePaths.values().stream().anyMatch(List::isEmpty) || this.blackPaths.values().stream().anyMatch(List::isEmpty)
                || !this.white.getState() || ! this.black.getState()) {
            this.state = false;
        }

        return false;
    }

    public Path getSinglePath(String colour, Coordinate coordinate) {
//        if (coordinate.equals(new Coordinate(0, 2))) {
////            //system.out.println(this.singleWhitePaths);
//        }
        return (colour.equals("white") ? this.singleWhitePaths : this.singleBlackPaths).get(coordinate);
    }
    public Map<Coordinate, Path> getSinglePath(String colour) {
//        if (coordinate.equals(new Coordinate(0, 2))) {
////            //system.out.println(this.singleWhitePaths);
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
        //system.out.println("CPP" + checkedPlayerPaths);

        // Find every pawn of the opposing player with one origin and one possible path
        //system.out.println(opposingPlayerPaths);
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

        if (singleOriginPawns.isEmpty()) {
//            System.out.println("A" + ((System.nanoTime() - start1) / 1000000));
            checkedPlayer.update();
            return false;
        }

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
                                    //system.out.println(innerEntryKey);

                                    //system.out.println(innerEntry.getValue().get(0));
                                    return entry.getValue().get(0).contains(innerEntry.getValue().get(0).get(innerEntry.getValue().get(0).size() - 2));
                                }
                                return false;
                            })
                            .forEach(innerEntry -> {
                                //system.out.println("inner entry" + innerEntry);
                                innerEntry.getValue()
                                        .stream().filter(path -> Pathfinder.pathsExclusive(entry.getValue().get(0), path))
                                        .forEach(path -> {
                                            //system.out.println("checking..." + path);
                                            //system.out.println("entry..." + entry);

                                            Path toPut = makeExclusiveMaps(board, path, white, singleOriginPawns);
                                            if (toPut.isEmpty()) {
//                                                //system.out.println("path");
                                                toPut.add(path.getFirst());
                                                toPut.add(new Coordinate(-1, -1));
                                                toPut.add(innerEntry.getKey());
                                            } else {
//                                                //system.out.println(toPut);
                                            }
//                                            //system.out.println(toPut);
                                            newPaths.add(toPut);
//                                            //system.out.println(newPaths);
                                        });
                            });
                });



        List<Coordinate[]> forRemoval = new LinkedList<>();
        //system.out.println("paths " + newPaths);

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
//                //system.out.println("remove:" + path);

                forRemoval.add(new Coordinate[]{path.getLast(), path.getFirst()});
            }
        });


        //system.out.println("removing" + forRemoval);
        //system.out.println(checkedPlayer.getPawnOrigins());
        forRemoval.forEach(coordinates -> checkedPlayer.removeOrigins(coordinates[0], coordinates[1]));
        //system.out.println(checkedPlayer.getPawnOrigins());
//        //system.out.println("Updadting...");
        checkedPlayer.update();
//        //system.out.println("All paths 2: " + checkedPlayerPaths);
//        System.out.println("B" + ((System.nanoTime() - start1) / 1000000));



        return
                !forRemoval.isEmpty()
                ||
                        !newPaths.isEmpty()
//                        || removed[0]
                ;
    }

    private Path makeExclusiveMaps(BoardInterface board, Path path, boolean white, List<Map.Entry<Coordinate, List<Path>>> forbiddenPaths) {
//        //system.out.println(path);
        PawnMap player = !white
                ? this.black
                : this.white;

        //TODO current forbidden paths won't contain paths that are one coordinate long
//        //system.out.println("forbidden:" + forbiddenPaths);
        Path newPath = Pathfinder.findShortestPawnPath(StandardPieceFactory.getInstance().getPiece(!white ? "p" : "P"),
                path.getFirst(),
                (b, c) -> c.equals(path.getLast()),
                board,
                p -> PATH_DEVIATION.apply(p) <= player.getMaxCaptures(path.getLast()),
                (p1, p2) -> exclusion(forbiddenPaths, p1, p2)
                );
        //system.out.println("new path = " + newPath);
//        //system.out.println(PATH_DEVIATION.apply(newPath));
//        //system.out.println(player.getMaxCaptures(newPath.getLast()));


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
//                        if (this.whitePaths.containsKey(entry.getKey()) && this.whitePaths.get(entry.getKey()).containsAll(paths))
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

    public Path exclusion(List<Map.Entry<Coordinate, List<Path>>> forbiddenPaths, Path p1, Path p2) {
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
                //system.out.println("p2 exclusive");
                return p1;
            }

            if (!p1.isEmpty() && PATH_DEVIATION.apply(p1) < PATH_DEVIATION.apply(p2)) {
                return p1;
            }
            //system.out.println("both not exclusive");
            return p2;
    }

    public PawnMap getPawnMap(boolean white) {
        return white ? this.white : this.black;
    }
}
