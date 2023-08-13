package Heuristics.Deductions;

import Heuristics.BoardInterface;
import Heuristics.Path;
import Heuristics.Pathfinder;
import Heuristics.StateDetector;
import StandardChess.Coordinate;
import StandardChess.Coordinates;
import StandardChess.StandardPieceFactory;

import java.util.List;
import java.util.Map;
import java.util.function.Predicate;

public class PiecePathFinderUtil {

    protected static int FINAL_RANK_Y = 7;
    protected static int FIRST_RANK_Y = 0;
    protected static int BLACK_PAWN_Y = 6;
    protected static int WHITE_PAWN_Y = 1;
    protected static int BLACK_ESCAPE_Y = 5;
    protected static int WHITE_ESCAPE_Y = 2;

    private StateDetector detector;

    public Predicate<Coordinate> secondRankCollision = coordinate -> {
        int y = coordinate.getY();
        if ((y == WHITE_PAWN_Y || y == BLACK_PAWN_Y)) {
            Map<Coordinate, List<Path>> map = this.detector.getPawnPaths(y == 1);
            return !(map.containsKey(coordinate));
        }
        return true;
    };

    Predicate<Path> thirdRankCollision = path -> {
        int y = path.getLast().getY();
        if (y == WHITE_ESCAPE_Y || y == BLACK_ESCAPE_Y) {
            boolean white = y == WHITE_ESCAPE_Y;
            Path toCheck = this.detector.getSinglePawnPaths(white).get(path.getLast());
            Map<Coordinate, List<Path>> map = this.detector.getPawnPaths(white);
            return !(
                    map.containsKey(path.getLast())
                            && toCheck != null
                            && Pathfinder.pathsExclusive(toCheck, Path.of(path, Coordinates.NULL_COORDINATE)));
        }
        return true;
    };
    Predicate<Path> firstRankCollision = path ->{
        Coordinate coordinate = path.getLast();
        return !(
                (coordinate.getY() == 0 || coordinate.getY() == 7)
                        && !AbstractDeduction.STANDARD_STARTS.get(coordinate.getX()).equals("rook")
                        && this.detector.getStartLocations().containsKey(coordinate)
                        && !this.detector.getStartLocations().get(coordinate).isEmpty()
                        && this.detector.getCaged().containsKey(coordinate)
                        && this.detector.getCaged().get(coordinate));
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
            "knight", path -> true

    );

    public PiecePathFinderUtil(StateDetector detector) {
        this.detector = detector;
    }


    public Path findPath(BoardInterface board, String pieceName,
                         String pieceCode, Coordinate start,
                         Coordinate target) {
        return findPath(board, pieceName, pieceCode, start, target, p -> true);
    }

    public Path findPath(BoardInterface board, String pieceName,
                          String pieceCode, Coordinate start,
                          Coordinate target,
                          Predicate<Path> additionalCondition){
        return Pathfinder.findShortestPath(
                StandardPieceFactory.getInstance().getPiece(pieceCode),
                start,
                (b, c) -> c.equals(target) || (c.getY() >= WHITE_ESCAPE_Y && c.getY() <= BLACK_ESCAPE_Y),
                board,
                p -> this.pathConditions.get(pieceName).test(p) && additionalCondition.test(p));
    }
}
