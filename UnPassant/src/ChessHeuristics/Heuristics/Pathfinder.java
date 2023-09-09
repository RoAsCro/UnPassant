package ChessHeuristics.Heuristics;

import ReverseChess.StandardChess.Coordinate;
import ReverseChess.StandardChess.Coordinates;
import ReverseChess.StandardChess.Piece;

import java.util.LinkedList;
import java.util.List;
import java.util.function.BiPredicate;
import java.util.function.BinaryOperator;
import java.util.function.Predicate;

/**
 * A class with static methods for searching for Paths of pieces. A Path in this context is an ordered
 * List of Coordinates containing no two of the same Coordinate, each Coordinate a given piece's smallest possible move
 * away from the last.
 * @author Roland Crompton
 */
public class Pathfinder {
    /**The maximum recursive depth to which Pathfinding may go - there should not be situation in which
     * a Path needs to be longer than 17 Coordinates*/
    private static final int MAX_DEPTH = 17;

    /**
     * Find the shortest path from the given origin Coordinate up to the end condition using the given piece's moveset
     * on the given board.
     * @param piece the Piece whose moveset will be used
     * @param origin the starting Coordinate being pathed from
     * @param endCondition the condition at which the Path is considered complete
     * @param board the board on which the pathing is taking place
     * @return the shortest Path found, empty if none is found
     */
    public static Path findShortestPath(Piece piece, Coordinate origin,
                                        BiPredicate<BoardInterface, Coordinate> endCondition,
                                        BoardInterface board) {
        Path shortestPath = new Path();
        shortestPath = findPathIter(piece, origin, endCondition, shortestPath, board, MAX_DEPTH,
                true, p -> true, (c, d) -> true);
        return shortestPath;
    }

    /**
     * Find the shortest path from the given origin Coordinate up to the end condition using the given piece's moveset
     * on the given board. Additionally, takes a Path Predicate condition to check at every Coordinate on the Path.
     * @param piece the Piece whose moveset will be used
     * @param origin the starting Coordinate being pathed from
     * @param endCondition the condition at which the Path is considered complete
     * @param board the board on which the pathing is taking place
     * @param pathCondition the condition checked at every Coordinate on the path
     * @return the shortest Path found, empty if none is found
     */
    public static Path findShortestPath(Piece piece, Coordinate origin,
                                        BiPredicate<BoardInterface, Coordinate> endCondition,
                                        BoardInterface board, Predicate<Path> pathCondition) {
        Path shortestPath = new Path();
        shortestPath = findPathIter(piece, origin, endCondition, shortestPath, board, MAX_DEPTH, true,
                pathCondition, (c, d) -> true);
        return shortestPath;
    }

    /**
     * Find the shortest path from the given origin Coordinate up to the end condition using the given piece's moveset
     * on the given board. Additionally, takes a Path Predicate condition to check at every Coordinate on the Path,
     * and a Path BiPredicate for an additional condition on how the one Path is chosen over the other.
     * @param piece the Piece whose moveset will be used
     * @param origin the starting Coordinate being pathed from
     * @param endCondition the condition at which the Path is considered complete
     * @param board the board on which the pathing is taking place
     * @param pathCondition the condition checked at every Coordinate on the path
     * @param reductionCondition the additional condition on how one Path is chosen over another
     * @return the shortest Path found, empty if none is found
     */
    public static Path findShortestPath(Piece piece, Coordinate origin,
                                        BiPredicate<BoardInterface, Coordinate> endCondition,
                                        BoardInterface board, Predicate<Path> pathCondition,
                                        BiPredicate<Path, Path> reductionCondition) {

        Path shortestPath = new Path();
        shortestPath = findPathIter(piece, origin, endCondition,
                shortestPath, board, MAX_DEPTH,
                true, pathCondition, reductionCondition);
        return shortestPath;
    }

    /**
     * Recursively iterates across the board one Coordinate at a time
     * from the origin using the pieces moveset until the end condition is reached.
     * The path condition is a check on the Path performed at every iteration, i.e. on every coordinate passed over.
     * The 'shortest' boolean specifies whether the shortest or first Path should be found.
     * The reduction condition is an additional comparison that can be made between found paths to choose which
     * is returned.
     * A found Path will never contain the same Coordinate twice.
     * @param piece the piece whose moveset is to be used
     * @param origin the starting Coordinate
     * @param endCondition the condition to be satisfied for a Path to be considered valid
     * @param path the initial, likely empty, Path to be used to start the recursion
     * @param board the Board Interface to be iterated across
     * @param depth the maximum depth of the recursion
     * @param shortest whether the shortest Path satisfying the conditions is to be found
     * @param pathCondition the condition, checked at every iteration, that a Path must satisfy
     * @param reductionCondition the condition by which the method decides between two Paths that satisfy the
     *                           given conditions
     * @return a Path that satisfies all given conditions, or an empty Path if none can be found
     */
    private static Path findPathIter(Piece piece, Coordinate origin,
                                     BiPredicate<BoardInterface, Coordinate> endCondition, Path path,
                                     BoardInterface board, int depth,
                                     boolean shortest, Predicate<Path> pathCondition,
                                     BiPredicate<Path, Path> reductionCondition) {
        path.add(origin);
        if (endCondition.test(board, origin)) {
            return path;
        }

        Path shortestPath = new Path();
        Coordinate[] moves = piece.getMoves(origin);
        for (Coordinate target : moves) {
            if (depth != 0 && path.size() + 1 > depth) {
                shortestPath = Path.of();
                break;
            }
            Path conditionalPath = new Path();
            conditionalPath.addAll(path);
            conditionalPath.add(target);
            if (path.contains(target) || !Coordinates.inBounds(target) || !pathCondition.test(conditionalPath)) {
                continue;
            }
            Path currentPath = new Path();
            currentPath.addAll(path);
            Path testPath = findPathIter(piece, target, endCondition, currentPath, board, depth, shortest,
                    pathCondition, reductionCondition);
            if (testPath.size() != 0 && (shortestPath.isEmpty() || testPath.size() < shortestPath.size())) {
                depth = testPath.size();
                if (!shortest) {
                    return testPath;
                }
                if (shortestPath.isEmpty() || reductionCondition.test(testPath, shortestPath)) {
                    shortestPath = testPath;
                }

            }
        }
        return shortestPath;
    }

    /**
     * Find the shortest Path from the given origin Coordinate up to the end condition using the pawn's moveset
     * on the given board.
     * @param piece the Piece whose moveset will be used - this is purely for the pawn's colour
     * @param origin the starting Coordinate being pathed from
     * @param endCondition the condition at which the Path is considered complete
     * @param board the board on which the pathing is taking place
     * @param pathCondition the condition checked at every Coordinate on the path
     * @param reductionCondition the condition by which the method decides between two Paths that satisfy the
     *                           given conditions
     * @return the shortest Path found, empty if none is found
     */
    public static Path findShortestPawnPath(Piece piece, Coordinate origin,
                                        BiPredicate<BoardInterface, Coordinate> endCondition,
                                        BoardInterface board, Predicate<Path> pathCondition,
                                        BinaryOperator<Path> reductionCondition) {

        Path shortestPath = new Path();
        List<Path> possiblePaths = new LinkedList<>();
        findPawnPathIter(piece, origin, endCondition, shortestPath, board, possiblePaths, pathCondition);
        return possiblePaths.stream()
                .reduce(new Path(), reductionCondition);
    }
    /**
     * Find the all Paths from the given origin Coordinate up to the end condition using the pawn's moveset
     * on the given board.
     * @param piece the Piece whose moveset will be used - this is purely for the pawn's colour
     * @param origin the starting Coordinate being pathed from
     * @param endCondition the condition at which the Path is considered complete
     * @param board the board on which the pathing is taking place
     * @param pathCondition the condition checked at every Coordinate on the path
     * @return the shortest Path found, empty if none is found
     */
    public static List<Path> findAllPawnPaths(Piece piece, Coordinate origin,
                                            BiPredicate<BoardInterface, Coordinate> endCondition,
                                            BoardInterface board, Predicate<Path> pathCondition) {

        Path shortestPath = new Path();
        List<Path> possiblePaths = new LinkedList<>();
        findPawnPathIter(piece, origin, endCondition, shortestPath, board, possiblePaths, pathCondition);
        return possiblePaths;
    }
    /**
     * Recursively iterates across the board one Coordinate at a time
     * from the origin using the given pawns' moveset until the end condition is reached.
     * The path condition is a check on the Path performed at every iteration, i.e. on every coordinate passed over.
     * A found Path will never contain the same Coordinate twice.
     * @param piece the pawn whose moveset is to be used
     * @param origin the starting Coordinate
     * @param endCondition the condition to be satisfied for a Path to be considered valid
     * @param path the initial, likely empty, Path to be used to start the recursion
     * @param board the Board Interface to be iterated across
     * @param pathCondition the condition, checked at every iteration, that a Path must satisfy
     */
    private static void findPawnPathIter(Piece piece, Coordinate origin,
                                     BiPredicate<BoardInterface, Coordinate> endCondition, Path path,
                                     BoardInterface board,
                                     List<Path> possiblePaths,
                                         Predicate<Path> pathCondition) {
        path.add(origin);
        if (endCondition.test(board, origin)) {
            possiblePaths.add(path);
            return;
        }
        Coordinate[] moves = piece.getMoves(origin);
        for (Coordinate target : moves) {
            Path conditionalPath = new Path();
            conditionalPath.addAll(path);
            conditionalPath.add(target);
            if (!Coordinates.inBounds(target) || !pathCondition.test(conditionalPath)) {
                continue;
            }
            Path currentPath = new Path();
            currentPath.addAll(path);
            findPawnPathIter(piece, target, endCondition, currentPath, board, possiblePaths, pathCondition);
        }
    }

    /**
     * Tests if two Paths are exclusive. Two Paths are exclusive if a. one Path contains every Coordinate of the other
     * Path, or b. both Paths contain the other Path's tail AND Path p contains every sequential Coordinate of Path q
     *  from q's tail up to the Coordinate that intersects with p's tail AND q contains every sequential Coordinate of p
     *  from p's tail up to the Coordinate that intersects with q's tail.
     * @param pathOne the first Path being checked
     * @param pathTwo the second Path being checked
     * @return true if the Paths are exclusive
     */
    public static boolean pathsExclusive(Path pathOne, Path pathTwo) {
        return (pathsExclusiveHelper(pathOne, pathTwo) && pathsExclusiveHelper(pathTwo, pathOne))
                || pathOne.containsAll(pathTwo) || pathTwo.containsAll(pathOne);
    }

    /**
     * Tests whether the containing Path p contains every sequential Coordinate of the contained Path q from q's
     * tail up to the Coordinate that intersects with p's tail.
     * @param containedPath the first Path
     * @param containingPath the second Path
     * @return true if the length of containedPath from its tail
     * up to the tail of containingPath is inside containingPath
     */
    private static boolean pathsExclusiveHelper(Path containedPath, Path containingPath) {
        Coordinate target = containingPath.getLast();
        if (containedPath.contains(target)) {
            return containingPath.containsAll(containedPath.subList(containedPath.indexOf(target), containedPath.size()));
        }
        int y = target.getY();
        if (y == HeuristicsUtil.FIRST_RANK_Y || y == HeuristicsUtil.FINAL_RANK_Y && containingPath.size() > 2 && containedPath.size() > 2) {
            Coordinate secondTarget = containingPath.get(containingPath.size() - 2);
            if (containedPath.contains(secondTarget)) {
                return containingPath.containsAll(containedPath.subList(containedPath.indexOf(secondTarget),
                        containedPath.size() - 1));
            }
        }
        return false;
    }
}
