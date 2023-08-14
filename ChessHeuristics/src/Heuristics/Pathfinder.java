package Heuristics;

import StandardChess.Coordinate;
import StandardChess.Coordinates;
import StandardChess.Piece;

import java.util.LinkedList;
import java.util.List;
import java.util.function.BiPredicate;
import java.util.function.BinaryOperator;
import java.util.function.Predicate;

public class Pathfinder {

    private static final int MAX_DEPTH = 64;
    protected static int FINAL_RANK = 7;
    protected static int FIRST_RANK = 0;

    public static Path findShortestPath(Piece piece, Coordinate origin,
                                        BiPredicate<BoardInterface, Coordinate> endCondition,
                                        BoardInterface board) {
        Path shortestPath = new Path();
        shortestPath = findPathIter(piece, origin, endCondition, shortestPath, board, MAX_DEPTH, true, p -> true, (c, d) -> true);
        return shortestPath;
    }

    public static Path findShortestPath(Piece piece, Coordinate origin,
                                        BiPredicate<BoardInterface, Coordinate> endCondition,
                                        BoardInterface board, Predicate<Path> pathCondition) {
        Path shortestPath = new Path();
        shortestPath = findPathIter(piece, origin, endCondition, shortestPath, board, MAX_DEPTH, true, pathCondition, (c, d) -> true);
        return shortestPath;
    }
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
     * A found Path will never contain the same Coordinate twice
     * @param piece the piece whose moveset is to be used
     * @param origin the starting Coordinate
     * @param endCondition the condition to be satisfied for a Path to be considered valid
     * @param path the initial, likely empty, Path to be used to start the recursion
     * @param board the Board Interface to be iterated across
     * @param depth the current depth of the recursion
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
                shortestPath.addAll(path);
                shortestPath.add(target);
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
            Path testPath = findPathIter(piece, target, endCondition, currentPath, board, depth, shortest, pathCondition, reductionCondition);
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

    public static List<Path> findAllPawnPaths(Piece piece, Coordinate origin,
                                            BiPredicate<BoardInterface, Coordinate> endCondition,
                                            BoardInterface board, Predicate<Path> pathCondition) {

        Path shortestPath = new Path();
        List<Path> possiblePaths = new LinkedList<>();
        findPawnPathIter(piece, origin, endCondition, shortestPath, board, possiblePaths, pathCondition);
        return possiblePaths;
    }

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

    public static boolean pathsExclusive(Path pathOne, Path pathTwo) {
        return (pathsExclusiveHelper(pathOne, pathTwo) && pathsExclusiveHelper(pathTwo, pathOne))
                || pathOne.containsAll(pathTwo) || pathTwo.containsAll(pathOne);
    }

    /**
     *
     * @param containedPath
     * @param containingPath
     * @return true if the length of containedPath from its head
     * up to the head of containingPath is inside containingPath
     */
    private static boolean pathsExclusiveHelper(Path containedPath, Path containingPath) {
        Coordinate target = containingPath.getLast();
        if (containedPath.contains(target)) {
            return containingPath.containsAll(containedPath.subList(containedPath.indexOf(target), containedPath.size()));
        }
        int y = target.getY();
        if (y == FIRST_RANK || y == FINAL_RANK && containingPath.size() > 2 && containedPath.size() > 2) {
            Coordinate secondTarget = containingPath.get(containingPath.size() - 2);
            if (containedPath.contains(secondTarget)) {
                return containingPath.containsAll(containedPath.subList(containedPath.indexOf(secondTarget), containedPath.size() - 1));
            }
        }
        return false;
    }
}
