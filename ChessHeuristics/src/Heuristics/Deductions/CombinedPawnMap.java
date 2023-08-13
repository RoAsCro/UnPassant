package Heuristics.Deductions;

import Heuristics.BoardInterface;
import Heuristics.Observation;
import Heuristics.Path;
import Heuristics.Pathfinder;
import StandardChess.Coordinate;
import StandardChess.Coordinates;
import StandardChess.StandardPieceFactory;

import java.util.*;
import java.util.function.Function;

public class CombinedPawnMap extends AbstractDeduction {
    private PawnMap white;
    private PawnMap black;
    public static final Function<Path, Integer> PATH_DEVIATION = p -> p.stream()
            .reduce(new Coordinate(p.get(0).getX(), 0), (c, d) -> {
                if (c.getX() != d.getX()) {
                    return new Coordinate(d.getX(), c.getY() + 1);
                }
                return c;
            }).getY();


    public CombinedPawnMap(PawnMap white, PawnMap black, CombinedPawnMap combinedPawnMap) {
        this.black = black;
        this.white = white;
    }

    public CombinedPawnMap(PawnMap white, PawnMap black) {
        this.black = black;
        this.white = white;
    }
    public CombinedPawnMap(){};

    /**
     * return the minimum number of captures made by pawns
     */
    public int minimumCaptures(boolean white) {
        Map<Coordinate, List<Path>> player = this.detector.getPawnPaths(white);
        Path claimed = new Path();
        int[] size = new int[]{0};
        // With the claimed clause this will not work 100% of the time
        player.values().stream().forEach(paths -> {
                    size[0] = size[0] + paths.stream()
                            .filter(p -> !claimed.contains(p.getFirst()))
                            .reduce((integer, integer2) -> PATH_DEVIATION.apply(integer) < PATH_DEVIATION.apply(integer2) ? integer : integer2)
                            .map(p -> {
                                claimed.add(p.getFirst());
                                return PATH_DEVIATION.apply(p);
                            })
                            .orElse(0);
                }
        );
        return size[0];
    }

    /**
     * Returns the number of pieces the given player can capture:
     * the opposing player's maxPieces minus the number of pieces the opposing player has on the board
     */
    public int capturablePieces(boolean white) {
        return this.detector.pawnTakeablePieces(white) - (white ? this.detector.getPieceNumber().getWhitePieces() : this.detector.getPieceNumber().getBlackPieces());
    }

    @Override
    public List<Observation> getObservations() {
        return new LinkedList<>();
    }

    @Override
    public boolean deduce(BoardInterface board) {
        boolean changed = true;
        boolean another = true;
        while (changed) {
            HashSet<List<Path>> startingWhite = new HashSet<>(this.detector.getPawnPaths(true).values());
            HashSet<List<Path>> startingBlack = new HashSet<>(this.detector.getPawnPaths(false).values());

            makeMaps(board, false);
            makeMaps(board, true);
            if ((!exclude(board, true) & !exclude(board, false))
                    || (startingWhite.containsAll(new HashSet<>(this.detector.getPawnPaths(true).values()))
                    && startingBlack.containsAll(new HashSet<>(this.detector.getPawnPaths(false).values())))
            ) {
                if (!another) {
                    changed = false;
                } else {
                    another = false;
                }
            }
        }

        if (this.detector.getPawnPaths(true).values().stream().anyMatch(List::isEmpty) || this.detector.getPawnPaths(false).values().stream().anyMatch(List::isEmpty)) {
            this.state = false;
        }
//        System.out.println(minimumCaptures(true));
//        System.out.println(this.whitePaths);
//        System.out.println(this.blackPaths);

        return false;
    }

    public Path getSinglePath(boolean white, Coordinate coordinate) {
        return this.detector.getSinglePawnPaths(white).get(coordinate);
    }

    /**
     *
     * @return whether or not there was a change
     */
    protected boolean exclude(BoardInterface board, boolean white) {
//        PawnMap checkedPlayer = white
//                ? this.white
//                : this.black;
//        PawnMap opposingPlayer = white
//                ? this.black
//                : this.white;
        Map<Coordinate, List<Path>> checkedPlayerPaths = this.detector.getPawnPaths(white);

        Map<Coordinate, List<Path>> opposingPlayerPaths = this.detector.getPawnPaths(!white);

        // Find every pawn of the opposing player with one origin and one possible path
        List<Map.Entry<Coordinate, List<Path>>> singleOriginPawns = new ArrayList<>(opposingPlayerPaths.entrySet()
                .stream()
                .filter(entry -> entry.getValue().size() == 1 && !(entry.getValue().get(0).size() == 1))
                .filter(entry -> Pathfinder.findAllPawnPaths(
                                StandardPieceFactory.getInstance().getPiece(white ? "p" : "P"),
                                entry.getValue().get(0).getFirst(),
                                (b, c) -> c.equals(entry.getKey()),
                                board,
                                p -> PATH_DEVIATION.apply(p) <= this.detector.getMaxCaptures(!white, entry.getKey()))
                        .size() == 1)
                .toList());
        singleOriginPawns.addAll(opposingPlayerPaths.entrySet()
                .stream().filter(entry ->entry.getValue().size() == 1 && entry.getValue().get(0).size() == 1).toList());
//        System.out.println(singleOriginPawns);

        if (singleOriginPawns.isEmpty()) {
            // New implementation may hugely impact performance here
            this.detector.update();
            return false;
        }

        singleOriginPawns.forEach(entry -> this.detector.getSinglePawnPaths(!white).put(entry.getKey(), entry.getValue().get(0)));


        List<Path> newPaths = new LinkedList<>();
        singleOriginPawns
                .forEach(entry -> checkedPlayerPaths.entrySet()
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
                            if (y2 == FINAL_RANK_Y || y2 == FIRST_RANK_Y) {
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
                                            toPut.add(path.getFirst());
                                            toPut.add(Coordinates.NULL_COORDINATE);
                                            toPut.add(innerEntry.getKey());
                                        }
                                        newPaths.add(toPut);
                                    });
                        }));



        List<Coordinate[]> forRemoval = new LinkedList<>();
        //system.out.println("paths " + newPaths);

        newPaths.stream().forEach(path -> {
            List<Path> pathList = checkedPlayerPaths.get(path.getLast());
            Path toRemove = pathList
                    .stream().filter(path2 -> path2.getFirst() == path.getFirst())
                    .findFirst()
                    .orElse(null);
            pathList.remove(toRemove);
            if (!(path.contains(Coordinates.NULL_COORDINATE))) {
                pathList.add(path);
            } else {
//                //system.out.println("remove:" + path);

                forRemoval.add(new Coordinate[]{path.getLast(), path.getFirst()});
            }
        });


        //system.out.println("removing" + forRemoval);
        //system.out.println(checkedPlayer.getPawnOrigins());
        //Checked player
        forRemoval.forEach(coordinates -> this.detector.getPawnOrigins(white).get(coordinates[0]).remove(coordinates[1]));
        //system.out.println(checkedPlayer.getPawnOrigins());
//        //system.out.println("Updadting...");
        this.detector.update();
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
//        PawnMap player = !white
//                ? this.black
//                : this.white;

        //TODO current forbidden paths won't contain paths that are one coordinate long
//        //system.out.println("forbidden:" + forbiddenPaths);
        Path newPath = Pathfinder.findShortestPawnPath(StandardPieceFactory.getInstance().getPiece(!white ? "p" : "P"),
                path.getFirst(),
                (b, c) -> c.equals(path.getLast()),
                board,
                p -> PATH_DEVIATION.apply(p) <= this.detector.getMaxCaptures(white, path.getLast()),
                (p1, p2) -> exclusion(forbiddenPaths, p1, p2)
                );
        //system.out.println("new path = " + newPath);
//        //system.out.println(PATH_DEVIATION.apply(newPath));
//        //system.out.println(player.getMaxCaptures(newPath.getLast()));


        return newPath;
    }

    private void makeMaps(BoardInterface board, boolean white) {
//        PawnMap player = white
//                ? this.white
//                : this.black;
        this.detector.getPawnOrigins(white).entrySet()
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
                                        p -> PATH_DEVIATION.apply(p) <= this.detector.getMaxCaptures(white, entry.getKey()),
                                        (p1, p2) -> PATH_DEVIATION.apply(p1) < PATH_DEVIATION.apply(p2)
                                );
                                if (!path.isEmpty()) {
                                    paths.add(path);
                                }
                            });
                    this.detector.getPawnPaths(white).put(entry.getKey(), paths);
                });
    }


    public Map<Coordinate, List<Path>> getWhitePaths() {
        return this.detector.getPawnPaths(true);
    }

    public Map<Coordinate, List<Path>> getBlackPaths() {
        return this.detector.getPawnPaths(false);
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
