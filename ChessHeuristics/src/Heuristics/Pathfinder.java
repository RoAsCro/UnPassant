package Heuristics;

import StandardChess.Coordinate;
import StandardChess.Coordinates;
import StandardChess.Piece;

import java.util.function.BiPredicate;
import java.util.function.Predicate;

public class Pathfinder {
    public static Path findShortestPath(Piece piece, Coordinate origin,
                                        BiPredicate<BoardInterface, Coordinate> endCondition,
                                        BoardInterface board) {
        Path shortestPath = new Path();
        shortestPath = shortestPathIter(piece, origin, endCondition, shortestPath, board, -1);
        return shortestPath;
    }

    private static Path shortestPathIter(Piece piece, Coordinate origin,
                                         BiPredicate<BoardInterface, Coordinate> endCondition, Path path,
                                         BoardInterface board, int depth) {
        path.add(origin);
        if (endCondition.test(board, origin)) {
            return path;
        }
        Path shortestPath = new Path();

        Coordinate[] moves = piece.getMoves(origin);
        for (Coordinate target : moves) {
            if (depth != -1 && path.size() + 1 > depth) {
                shortestPath.addAll(path);
                shortestPath.add(target);
                break;
            }
            if (path.contains(target) || !Coordinates.inBounds(target)) {
                continue;
            }
            Path currentPath = new Path();
            currentPath.addAll(path);
            Path testPath = shortestPathIter(piece, target, endCondition, currentPath, board, depth);
            if (shortestPath.isEmpty() || testPath.size() < shortestPath.size()) {
                depth = testPath.size();
                shortestPath = testPath;
            }
        }
        return shortestPath;
    }
}
