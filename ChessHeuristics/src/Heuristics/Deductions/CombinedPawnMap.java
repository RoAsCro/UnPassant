package Heuristics.Deductions;

import Heuristics.BoardInterface;
import Heuristics.Observation;
import Heuristics.Path;
import Heuristics.Pathfinder;
import StandardChess.Coordinate;
import StandardChess.StandardPieceFactory;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

public class CombinedPawnMap extends AbstractDeduction {
    PawnMapWhite white;
    PawnMapBlack black;

    private final Map<Coordinate, List<Path>> whitePaths = new TreeMap<>();
    private final Map<Coordinate, List<Path>> blackPaths = new TreeMap<>();

    private static final Function<Path, Integer> PATH_DEVIATION = p -> p.stream()
            .reduce(new Coordinate(p.get(0).getX(), 0), (c, d) -> {
                if (c.getX() != d.getX()) {
                    return new Coordinate(d.getX(), c.getY() + 1);
                }
                return c;
            }).getY();


    public CombinedPawnMap(PawnMapWhite white, PawnMapBlack black) {
        this.black = black;
        this.white = white;
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

        int whiteCaptures = this.white.capturedPieces();
        makeMaps(board, false);
        makeMaps(board, true);
//        System.out.println(this.whitePaths.values());

//        System.out.println("FFFF" + this.white.getMaxCaptures(new Coordinate(3, 4)));
        List<Map.Entry<Coordinate, List<Path>>> singleOriginPawns = this.whitePaths.entrySet()
                .stream()
                .filter(entry -> entry.getValue().size() == 1 && !(entry.getValue().get(0).size() == 1))
                .filter(entry -> Pathfinder.findAllPawnPaths(
                        StandardPieceFactory.getInstance().getPiece("P"),
                        entry.getValue().get(0).getFirst(),
                        (b, c) -> c.equals(entry.getKey()),
                        board,
                        p -> PATH_DEVIATION.apply(p) <= this.white.getMaxCaptures(entry.getKey()))
                        .size() == 1)
                .toList();
        System.out.println("AOFIHWOFOAFWH");
        System.out.println(singleOriginPawns);
        Map<Coordinate, Path> newPaths = new TreeMap<>();
        singleOriginPawns
                .stream()
                .forEach(entry -> {
                    this.blackPaths.entrySet()
                            .stream()
                            .filter(innerEntry -> entry.getValue().get(0).contains(innerEntry.getKey()))
                            .forEach(innerEntry -> {
                                System.out.println("here");
                                innerEntry.getValue()
                                        .stream().filter(path -> Pathfinder.pathsExclusive(entry.getValue().get(0), path))
                                        .forEach(path -> {
                                            System.out.println("HERE");
                                            Path toPut = makeExclusiveMaps(board, path, true, singleOriginPawns);
                                            if (toPut.isEmpty()) {
                                                toPut.add(path.getFirst());
                                                toPut.add(new Coordinate(-1, -1));
                                            }
                                            newPaths.put(innerEntry.getKey(), toPut);
                                        });
                            });
                });

        System.out.println(newPaths);
        List<Coordinate[]> forRemoval = new LinkedList<>();
        newPaths.entrySet().stream().forEach(entry -> {
            List<Path> pathList = this.blackPaths.get(entry.getKey());
            System.out.println(pathList);
            Path toRemove = pathList
                    .stream().filter(path2 -> path2.getFirst() == entry.getValue().getFirst())
                    .findFirst()
                    .orElse(null);
            pathList.remove(toRemove);
            if (!(entry.getValue().getLast().getY() == -1)) {
                pathList.add(entry.getValue());
            } else {
                forRemoval.add(new Coordinate[]{entry.getKey(), entry.getValue().getFirst()});
            }
        });
        forRemoval.forEach(coordinates -> black.removeOrigins(coordinates[0], coordinates[1]));
        System.out.println("Updadting...");
        black.update("black");

        return false;
    }

    private Path makeExclusiveMaps(BoardInterface board, Path path, boolean black, List<Map.Entry<Coordinate, List<Path>>> forbiddenPaths) {
//        System.out.println(path);
        PawnMap player = black
                ? this.black
                : this.white;
        int captures = player.capturedPieces();
        System.out.println("captures" + captures);

        System.out.println(captures);
        Path newPath = Pathfinder.findShortestPawnPath(StandardPieceFactory.getInstance().getPiece(black ? "p" : "P"),
                path.getFirst(),
                (b, c) -> c.equals(path.getLast()),
                board,
                p -> PATH_DEVIATION.apply(p) <= player.getMaxCaptures(path.getLast()),
                (p1, p2) -> {
                    boolean p1NotExclusive;
                    if (p1.isEmpty()) {
                        p1NotExclusive = true;
                    } else {
                        p1NotExclusive = forbiddenPaths
                                .stream()
                                .noneMatch(entry -> p1.contains(entry.getKey())
                                        && Pathfinder.pathsExclusive(p1, entry.getValue().get(0)));
                    }
                    System.out.println(p1NotExclusive);
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
                        return p1;
                    }

                    if (!p1.isEmpty() && PATH_DEVIATION.apply(p1) < PATH_DEVIATION.apply(p2)) {
                        return p1;
                    }
                    return p2;
                }
                );
        System.out.println(path);
        System.out.println(newPath);
//        if (colour) {
//            this.whitePaths.get(newPath.getLast())
//                    .remove(path);
//            this.whitePaths.get(newPath.getLast())
//                    .add(newPath);
//        } else {
//            this.blackPaths.get(newPath.getLast())
//                    .remove(path);
//            this.blackPaths.get(newPath.getLast())
//                    .add(newPath);
//        }
        return newPath;
    }

    private void makeMaps(BoardInterface board, boolean white) {
        PawnMap player = white
                ? this.white
                : this.black;
        int captures = player.capturedPieces();
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
