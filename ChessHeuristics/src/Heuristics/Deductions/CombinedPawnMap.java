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

    public void generatePaths() {

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
        System.out.println(whiteCaptures);
        makeMaps(board, false);
        makeMaps(board, true);

        List<Map.Entry<Coordinate, List<Path>>> singlePathPawns = this.whitePaths.entrySet()
                .stream()
                .filter(entry -> entry.getValue().size() == 1)
                .toList();

        int freeCaptures = whiteCaptures - singlePathPawns.stream()
                .map(entry -> PATH_DEVIATION.apply(entry.getValue().get(0)))
                .reduce(Integer::sum)
                .orElse(0);

        return false;
    }

    private void makeMaps(BoardInterface board, boolean colour) {
        PawnMap player = colour
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
                                        StandardPieceFactory.getInstance().getPiece("P"),
                                        coordinate,
                                        (b, c) -> c.equals(entry.getKey()),
                                        board,
                                        p -> PATH_DEVIATION.apply(p) < captures,
                                        (p1, p2) -> PATH_DEVIATION.apply(p1) < PATH_DEVIATION.apply(p2)
                                );
                                paths.add(path);
                            });
                    if (colour) {
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
