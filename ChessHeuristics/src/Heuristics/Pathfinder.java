package Heuristics;

import StandardChess.Coordinate;
import StandardChess.Coordinates;
import StandardChess.Piece;

import java.util.function.BiPredicate;
import java.util.function.Predicate;

public class Pathfinder {

    private static int MAX_DEPTH = 64;

    public static Path findShortestPath(Piece piece, Coordinate origin,
                                        BiPredicate<BoardInterface, Coordinate> endCondition,
                                        BoardInterface board) {
        Path shortestPath = new Path();
        shortestPath = findPathIter(piece, origin, endCondition, shortestPath, board, MAX_DEPTH, true, p -> true);
        return shortestPath;
    }

    public static Path findShortestPath(Piece piece, Coordinate origin,
                                        BiPredicate<BoardInterface, Coordinate> endCondition,
                                        BoardInterface board, Predicate<Path> pathCondition) {
        Path shortestPath = new Path();
        shortestPath = findPathIter(piece, origin, endCondition, shortestPath, board, MAX_DEPTH, true, pathCondition);
        return shortestPath;
    }

    public static Path findFirstPath(Piece piece, Coordinate origin,
                                     BiPredicate<BoardInterface, Coordinate> endCondition,
                                     BoardInterface board) {

        Path shortestPath = new Path();
        shortestPath = findPathIter(piece, origin, endCondition, shortestPath, board, MAX_DEPTH, false, p -> true);
        return shortestPath;
    }

    private static Path findPathIter(Piece piece, Coordinate origin,
                                     BiPredicate<BoardInterface, Coordinate> endCondition, Path path,
                                     BoardInterface board, int depth,
                                     boolean shortest, Predicate<Path> pathCondition) {
        path.add(origin);
        if (endCondition.test(board, origin)) {
            return path;
        }
        Path shortestPath = new Path();

        Coordinate[] moves = piece.getMoves(origin);
        for (Coordinate target : moves) {
//            System.out.println(path);
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
            Path testPath = findPathIter(piece, target, endCondition, currentPath, board, depth, shortest, pathCondition);
            if (testPath.size() != 0 && (shortestPath.isEmpty() || testPath.size() < shortestPath.size())) {
                depth = testPath.size();
                if (!shortest) {
                    return testPath;
                }
//                System.out.println(testPath);
                shortestPath = testPath;
            }
        }
        return shortestPath;
    }
}
