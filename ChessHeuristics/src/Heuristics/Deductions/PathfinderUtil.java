package Heuristics.Deductions;

import Heuristics.BoardInterface;
import Heuristics.Detector.StateDetector;
import Heuristics.Path;
import Heuristics.Pathfinder;
import StandardChess.Coordinate;
import StandardChess.Coordinates;
import StandardChess.StandardPieceFactory;

import java.util.List;
import java.util.Map;
import java.util.function.BiPredicate;
import java.util.function.Function;
import java.util.function.Predicate;

import static Heuristics.HeuristicsUtil.*;

/**
 * A utility class containing various methods as shortcuts to common usages of the Pathfinder.
 */
public class PathfinderUtil {
    /**A Path/Integer Function that takes a Path and returns the number of captures made on that Path*/
    public static final Function<Path, Integer> PATH_DEVIATION = p -> p.stream()
            .reduce(new Coordinate(p.get(0).getX(), 0), (c, d) -> {
                if (c.getX() != d.getX()) {
                    return new Coordinate(d.getX(), c.getY() + 1);
                }
                return c;
            }).getY();

    /**The StateDetector from which the PathfinderUtil draws its data*/
    private StateDetector detector;
    /**A Path Predicate that carries out various checks for collision on the 1st or 8th rank when pathing for a non-pawn
     * piece*/
    public Predicate<Path> firstRankCollision = path -> {
        Coordinate coordinate = path.getLast();
        return !(
                (coordinate.getY() == 0 || coordinate.getY() == 7)
                        && !STANDARD_STARTS.get(coordinate.getX()).equals("rook")
                        && this.detector.getPieceData().getPiecePaths().containsKey(coordinate)
                        && !this.detector.getPieceData().getPiecePaths().get(coordinate).isEmpty()
                        && this.detector.getPieceData().getCaged().containsKey(coordinate)
                        && this.detector.getPieceData().getCaged().get(coordinate));
    };
    /**A Path Predicate that carries out various checks for collision on the 2nd or 7th rank when pathing for a non-pawn
     * piece*/
    public Predicate<Coordinate> secondRankCollision = coordinate -> {
        int y = coordinate.getY();
        if ((y == WHITE_PAWN_Y || y == BLACK_PAWN_Y)) {
            Map<Coordinate, List<Path>> map = this.detector.getPawnData().getPawnPaths(y == 1);
            return !(map.containsKey(coordinate));
        }
        return true;
    };
    /**A Path Predicate that carries out various checks for collision on the 3rd or 6th rank when pathing for a non-pawn
     * piece*/
    public Predicate<Path> thirdRankCollision = path -> {
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
    /**A Map of Strings of piece names and the collision checks they need to make*/
    private final Map<String, Predicate<Path>> pathConditions = Map.of(
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
            "knight", path -> true
    );

    /**
     * A constructor setting the StateDetector.
     * @param detector the StateDetector to be used in pathfinding
     */
    public PathfinderUtil(StateDetector detector) {
        this.detector = detector;
    }

    /**
     Finds a path from the given origin using the given (non-pawn) piece's move set to the given target or to
     * 1 < y < 6. The pathfinder will use that piece's collision conditions as specified in
     * PathfinderUtil.pathConditions, without any additionally specified conditions
     * @param board the board being checked
     * @param pieceName the name of the piece pathing
     * @param pieceCode the code of the piece pathing
     * @param start the Coordinate on which the pathfinding starts
     * @param target the target Coordinate
     * @return the Path found, will be empty if no Path is found
     */
    public Path findPiecePath(BoardInterface board, String pieceName,
                              String pieceCode, Coordinate start,
                              Coordinate target) {
        return findPiecePath(board, pieceName, pieceCode, start, target, p -> true);
    }

    /**
     * Finds a path from the given origin using the given (non-pawn) piece's move set to the given target or to
     * 1 < y < 6. The pathfinder will use that piece's collision conditions as specified in
     * PathfinderUtil.pathConditions, as well as any additionally specified conditions
     *
     * @param board the board being checked
     * @param pieceName the name of the piece pathing
     * @param pieceCode the code of the piece pathing
     * @param start  the Coordinate on which the pathfinding starts
     * @param target the target Coordinate
     * @param additionalCondition any additional Path Predicate condition
     * @return the Path found, will be empty if no Path is found
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

    /**
     * Uses Pathfinder to find the shortest Path between the given origin and the end condition, without being
     * exclusive to  any of the given forbidden Paths.
     * If maxCaptures is true, this will be done without exceeding the maxDeviation.
     * @param board the board to be checked
     * @param origin the starting Coordinate for the pathing
     * @param maxDeviation the maximum number of captures the pathing pawn can make, if maxCaptures is true
     * @param endCondition the condition at which a Path is considered found
     * @param white the colour of the pathing pawn, true if white, false if black
     * @param maxCaptures true if the maxDeviation should be considered, false otherwise
     * @param forbiddenPaths the Paths with which the found Path cannot be exclusive
     * @return the shortest Path fitting the conditions, will be empty if no Path is found
     */
    public Path findShortestPawnPath(BoardInterface board, Coordinate origin, int maxDeviation,
                                     BiPredicate<BoardInterface, Coordinate> endCondition,
                                     boolean white, boolean maxCaptures,
                                     List<Path> forbiddenPaths) {
        List<Path> candidatePaths =  Pathfinder.findAllPawnPaths(
                StandardPieceFactory.getInstance().getPiece(!white ? "p" : "P"),
                origin,
                endCondition,
                board,
                p -> !maxCaptures || PATH_DEVIATION.apply(p) <= maxDeviation);
        Path shortest =  candidatePaths.stream().filter(p1 -> forbiddenPaths.stream().noneMatch(path ->
                Pathfinder.pathsExclusive(p1, path)))
                .map(p1 -> Path.of(p1, new Coordinate(-1, PATH_DEVIATION.apply(p1))))
                .reduce((p1, p2) -> p1.getLast().getY() < p2.getLast().getY() ? p1 : p2)
                .orElse(Path.of());
        if (!shortest.isEmpty()) {
            shortest.removeLast();
        }
        return shortest;
    }

    /**
     * Finds all possible paths for the pawn at the given origin that can reach the given end condition without
     * exceeding the maxDeviation.
     * @param board the board being checked
     * @param origin the Coordinate where the pathfinding starts
     * @param maxDeviation the maximum number of captures the Pawn can make
     * @param endCondition the condition at which point the Path is considered found
     * @param white the colour of the pathing pawn, true if white, false if black
     * @return a List of all Paths found using the given conditions, will be empty if none are found
     */
    public List<Path> findAllPawnPath(BoardInterface board, Coordinate origin, int maxDeviation,
                                     BiPredicate<BoardInterface, Coordinate> endCondition,
                                     boolean white) {

        return Pathfinder.findAllPawnPaths(StandardPieceFactory.getInstance().getPiece(!white ? "p" : "P"),
                origin,
                endCondition,
                board,
                p -> PATH_DEVIATION.apply(p) <= maxDeviation);
    }

    /**
     * Calls Pathfinder.pathsExclusive, checking if the two given Paths are exclusive by the criteria listed in the
     * documentation for Pathfinder.pathsExclusive. To help decouple Deductions from the Pathfinder.
     * @param path1 the first Path
     * @param path2 the second Path
     * @return true if the Paths are exclusive, false otherwise
     */
    public boolean pathsExclusive(Path path1, Path path2) {
        return Pathfinder.pathsExclusive(path1, path2);
    }

}
