package Heuristics.Deductions;

import Heuristics.BoardInterface;
import Heuristics.Path;
import Heuristics.Pathfinder;
import Heuristics.Detector.StateDetector;
import StandardChess.Coordinate;
import StandardChess.Coordinates;
import StandardChess.StandardPieceFactory;

import java.util.List;
import java.util.Map;
import java.util.function.BiPredicate;
import java.util.function.Function;
import java.util.function.Predicate;

import static Heuristics.HeuristicsUtil.*;

public class PathfinderUtil {
    private StateDetector detector;

    public static final Function<Path, Integer> PATH_DEVIATION = p -> p.stream()
            .reduce(new Coordinate(p.get(0).getX(), 0), (c, d) -> {
                if (c.getX() != d.getX()) {
                    return new Coordinate(d.getX(), c.getY() + 1);
                }
                return c;
            }).getY();

    public Predicate<Coordinate> secondRankCollision = coordinate -> {
        int y = coordinate.getY();
        if ((y == WHITE_PAWN_Y || y == BLACK_PAWN_Y)) {
            Map<Coordinate, List<Path>> map = this.detector.getPawnData().getPawnPaths(y == 1);
            return !(map.containsKey(coordinate));
        }
        return true;
    };

    Predicate<Path> thirdRankCollision = path -> {
        int y = path.getLast().getY();
        if (y == WHITE_ESCAPE_Y || y == BLACK_ESCAPE_Y) {
            boolean white = y == WHITE_ESCAPE_Y;
            Path toCheck = this.detector.getPawnData().getPawnPaths(white).entrySet()
                    .stream().filter(e -> e.getKey().getY() == (white ? WHITE_ESCAPE_Y : BLACK_ESCAPE_Y))
                    .filter(e -> e.getValue().size() == 1)
                    .filter(e -> e.getValue().get(0).contains(path.getLast()))
                    .map(e -> e.getValue().get(0))
                    .findAny().orElse(null);
            Map<Coordinate, List<Path>> map = this.detector.getPawnData().getPawnPaths(white);
            return !(
                    map.containsKey(path.getLast())
                            && toCheck != null
                            && Pathfinder.pathsExclusive(toCheck, Path.of(path, Coordinates.NULL_COORDINATE)));
        }
        return true;
    };
    Predicate<Path> firstRankCollision = path -> {
        Coordinate coordinate = path.getLast();
        return !(
                (coordinate.getY() == 0 || coordinate.getY() == 7)
                        && !STANDARD_STARTS.get(coordinate.getX()).equals("rook")
                        && this.detector.getPieceData().getPiecePaths().containsKey(coordinate)
                        && !this.detector.getPieceData().getPiecePaths().get(coordinate).isEmpty()
                        && this.detector.getPieceData().getCaged().containsKey(coordinate)
                        && this.detector.getPieceData().getCaged().get(coordinate));
    };

    private final Map<String, Predicate<Path>> pathConditions = Map.of(
            //
            "bishop", path ->
                    secondRankCollision.test(path.getLast())
                            && thirdRankCollision.test(path),
            "rook", path ->
                    secondRankCollision.test(path.getLast())
                            && thirdRankCollision.test(path)
                            && firstRankCollision.test(path),
            "queen", path -> secondRankCollision.test(path.getLast())
                    && thirdRankCollision.test(path)
                    && firstRankCollision.test(path),
            "king", path -> secondRankCollision.test(path.getLast())
                    && thirdRankCollision.test(path)
                    && firstRankCollision.test(path),
            "knight", path -> true //TODO promoted corner knight pattern?

    );

    public PathfinderUtil(StateDetector detector) {
        this.detector = detector;
    }


    public Path findPiecePath(BoardInterface board, String pieceName,
                              String pieceCode, Coordinate start,
                              Coordinate target) {
        return findPiecePath(board, pieceName, pieceCode, start, target, p -> true);
    }

    /**
     * Finds a path from the given origin using the given (non-pawn) piece's move set to the given target or to 1 < y < 6
     * The pathfinder will use that piece's collision conditions as specified in PathfinderUtil.pathConditions
     * as well as any addtionally specified conditions
     *
     * @param board
     * @param pieceName
     * @param pieceCode
     * @param start
     * @param target
     * @param additionalCondition
     * @return
     */
    public Path findPiecePath(BoardInterface board, String pieceName,
                              String pieceCode, Coordinate start,
                              Coordinate target,
                              Predicate<Path> additionalCondition) {
        return Pathfinder.findShortestPath(
                StandardPieceFactory.getInstance().getPiece(pieceCode),
                start,
                (b, c) -> c.equals(target) || (c.getY() >= WHITE_ESCAPE_Y && c.getY() <= BLACK_ESCAPE_Y),
                board,
                p -> this.pathConditions.get(pieceName).test(p) && additionalCondition.test(p));
    }

    public Path findShortestPawnPath(BoardInterface board, Coordinate origin, int maxDeviation,
                                     BiPredicate<BoardInterface, Coordinate> endCondition,
                                     boolean white, boolean maxCaptures,
                                     List<Path> forbiddenPaths) {

        return Pathfinder.findShortestPawnPath(StandardPieceFactory.getInstance().getPiece(!white ? "p" : "P"),
                origin,
                endCondition,
                board,
                p -> !maxCaptures || PATH_DEVIATION.apply(p) <= maxDeviation,
                (p1, p2) -> exclusion(forbiddenPaths, p1, p2)
        );
    }

    public List<Path> findAllPawnPath(BoardInterface board, Coordinate origin, int maxDeviation,
                                     BiPredicate<BoardInterface, Coordinate> endCondition,
                                     boolean white) {

        return Pathfinder.findAllPawnPaths(StandardPieceFactory.getInstance().getPiece(!white ? "p" : "P"),
                origin,
                endCondition,
                board,
                p -> PATH_DEVIATION.apply(p) <= maxDeviation);
    }




    public Path exclusion(List<Path> forbiddenPaths, Path p1, Path p2) {
        boolean p1NotExclusive;
        if (p1.isEmpty()) {
            p1NotExclusive = true;
        } else {
            p1NotExclusive = forbiddenPaths
                    .stream()
                    .noneMatch(path ->
                            // What is commented out below may greatly affect performance
                            // now that it is commented out - however, it allows theoretical collision
                            // Before reinstating, create new checks
//                                        p1.contains(entry.getKey()) &&
                            Pathfinder.pathsExclusive(p1, path));
        }
        boolean p2NotExclusive = forbiddenPaths
                .stream()
                .noneMatch(path ->
                        // See above before deleting
//                                    (p2.contains(entry.getKey()) || entry.getValue().get(0).contains(p2.getLast())) &&
                        Pathfinder.pathsExclusive(p2, path));
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

    public boolean pathsExclusive(Path path1, Path path2) {
        return Pathfinder.pathsExclusive(path1, path2);
    }

}