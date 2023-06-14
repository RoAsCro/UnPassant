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
        shortestPath = shortestPathIter(piece, origin, endCondition, shortestPath, board, MAX_DEPTH, true);
        return shortestPath;
    }

    public static Path findFirstPath(Piece piece, Coordinate origin,
                                     BiPredicate<BoardInterface, Coordinate> endCondition,
                                     BoardInterface board) {

        Path shortestPath = new Path();
        shortestPath = shortestPathIter(piece, origin, endCondition, shortestPath, board, MAX_DEPTH, false);
        return shortestPath;
    }

    private static Path shortestPathIter(Piece piece, Coordinate origin,
                                         BiPredicate<BoardInterface, Coordinate> endCondition, Path path,
                                         BoardInterface board, int depth,
                                         boolean shortest) {
            System.out.println(path);
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
            if (path.contains(target) || !Coordinates.inBounds(target)) {
                continue;
            }
            Path currentPath = new Path();
            currentPath.addAll(path);
            Path testPath = shortestPathIter(piece, target, endCondition, currentPath, board, depth, shortest);
            if (shortestPath.isEmpty() || testPath.size() < shortestPath.size()) {
                depth = MAX_DEPTH;
                if (testPath.size() != 0) {
                    depth = testPath.size();
                    if (!shortest) {
                        return testPath;
                    }
                }
                shortestPath = testPath;
            }
        }
        return shortestPath;
    }
}
