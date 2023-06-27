package Heuristics.Deductions;

import Heuristics.BoardInterface;
import Heuristics.Observation;
import Heuristics.Path;
import Heuristics.Pathfinder;
import StandardChess.Coordinate;
import StandardChess.StandardPieceFactory;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.function.Predicate;

public class PieceMap extends AbstractDeduction{
    private CombinedPawnMap pawnMap;

//    private PieceNumber = new PieceNumber

    private final Map<Coordinate, List<Path>> startLocations = new TreeMap<>();
    private final Map<Coordinate, Boolean> caged = new TreeMap<>();


    private final static Map<Integer, String> STANDARD_STARTS = Map.of(
            0, "rook", 1, "knight", 2, "bishop", 3, "queen", 4,
            "king", 5, "bishop", 6, "knight", 7, "rook"
    );

    Predicate<Coordinate> secondRankCollision = coordinate -> !(coordinate.getY() == 1 && this.pawnMap.getWhitePaths().containsKey(coordinate));
    Predicate<Path> thirdRankCollision = path -> !(
                    path.getLast().getY() == 2
                    && pawnMap.getWhitePaths().containsKey(path.getLast())
                    && this.pawnMap.getSinglePath("white", path.getLast()) != null
                    && Pathfinder.pathsExclusive(this.pawnMap.getSinglePath("white", path.getLast()), Path.of(path, new Coordinate(-1, -1)))
    );
    Predicate<Path> firstRankCollision = path -> !(
            path.getLast().getY() == 0
            && this.startLocations.containsKey(path.getLast())
            && !this.startLocations.get(path.getLast()).isEmpty()
            && this.caged.containsKey(path.getLast())
            && this.caged.get(path.getLast()));

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
                    && firstRankCollision.test(path)

    );

    public PieceMap(CombinedPawnMap pawnMap) {
        this.pawnMap = pawnMap;
    }


    public Map<Coordinate, List<Path>> getStartLocations() {
        return this.startLocations;
    }
    public Map<Coordinate, Boolean> getCaged() {
        return this.caged;
    }
    @Override
    public List<Observation> getObservations() {
        return this.pawnMap.getObservations();
    }

    @Override
    public boolean deduce(BoardInterface board) {
        pawnMap.deduce(board);
//
        // Bishops
        findFromOrigin(board, 2, true, false);
        findFromOrigin(board, 2, true, true);
        findFromOrigin(board, 5, true, false);
        findFromOrigin(board, 5, true, true);
        // Royalty
        findFromOrigin(board, 3, true, false);
        findFromOrigin(board, 3, true, true);
        findFromOrigin(board, 4, true, false);
        findFromOrigin(board, 4, true, true);
        // Rooks
        findFromOrigin(board, 0, true, false);
        findFromOrigin(board, 0, true, true);
        findFromOrigin(board, 7, true, false);
        findFromOrigin(board, 0, true, true);


        String colour = "white";
        for (int x = 0; x < 8 ; x++) {

        }


        return false;
    }
    private void findFromOrigin(BoardInterface board, int originX, boolean white, boolean cage) {

        Coordinate start = new Coordinate(originX, white ? 0 : 7);
        String pieceName = STANDARD_STARTS.get(originX);
        int escapeLocation = white ? 2 : 5;
        String pieceCode = pieceName.substring(0, 1);
        if (white){
            pieceCode = pieceCode.toUpperCase();
        }
        //
        List<Coordinate> candidatePieces = board.getBoardFacts().getCoordinates(white ? "white" : "black", pieceName);
        System.out.println(candidatePieces);
        List<Path> candidatePaths = new LinkedList<>();
        if (cage) {
            this.caged.put(start, findPath(board, pieceName, pieceCode, start, new Coordinate(4, 4), escapeLocation).isEmpty());

        } else {
            for (Coordinate target : candidatePieces) {
                if (pieceName.equals("bishop") && (start.getX() + start.getY()) % 2 != (target.getX() + target.getY()) % 2) {
                    continue;
                }
                Path foundPath = findPath(board, pieceName, pieceCode, start, target, escapeLocation);
//                    Pathfinder.findShortestPath(
//                    StandardPieceFactory.getInstance().getPiece(pieceCode),
//                    start,
//                    (b, c) -> c.equals(target) || c.getY() == escapeLocation,
//                    board,
//                    this.pathConditions.get(pieceName));
                if (!foundPath.isEmpty()) {
                    candidatePaths.add(foundPath);
                }
            }
            this.startLocations.put(start, candidatePaths);
        }

    }

    private Path findPath(BoardInterface board, String pieceName,
                          String pieceCode, Coordinate start,
                          Coordinate target, int escapeLocation){
        return Pathfinder.findShortestPath(
                StandardPieceFactory.getInstance().getPiece(pieceCode),
                start,
                (b, c) -> c.equals(target) || c.getY() == escapeLocation,
                board,
                this.pathConditions.get(pieceName));
    }

}
