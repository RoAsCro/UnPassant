package Heuristics.Deductions;

import Heuristics.BoardInterface;
import Heuristics.Observation;
import Heuristics.Path;
import Heuristics.Pathfinder;
import StandardChess.Coordinate;
import StandardChess.StandardPieceFactory;

import java.util.*;
import java.util.function.Predicate;

public class PieceMap extends AbstractDeduction{
    private CombinedPawnMap pawnMap;

//    private PieceNumber = new PieceNumber

    private final Map<Coordinate, Map<Coordinate, Path>> startLocations = new TreeMap<>();
    public final Map<Coordinate, Path> startPiecePairs = new TreeMap<>();
    public final Map<Coordinate, List<Path>> pieceMap = new TreeMap<>();
    private final Map<Coordinate, Boolean> caged = new TreeMap<>();


    private final static Coordinate NULL_COORDINATE = new Coordinate(-1, -1);
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
            && !STANDARD_STARTS.get(path.getLast().getX()).equals("rook")
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


    public Map<Coordinate, Map<Coordinate, Path>> getStartLocations() {
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
        List<Coordinate> foundPieces = this.startPiecePairs.values().stream().flatMap(Collection::stream).toList();
        this.startLocations.entrySet().stream().forEach(entry -> {
            Path coordinatesToRemove = new Path();
            entry.getValue().keySet().stream().forEach(origin -> {
                String pieceName = STANDARD_STARTS.get(entry.getKey().getX());
                String pieceCode = pieceName.substring(0, 1).toUpperCase();
                if (findPath(board, pieceName, pieceCode, origin, entry.getKey(), 0).isEmpty()){
                    System.out.println("Key" + entry.getKey());
                    System.out.println("Or" + origin);
                    coordinatesToRemove.add(origin);
                }
            });
            coordinatesToRemove.forEach(coordinate -> entry.getValue().remove(coordinate));
        });
        this.startLocations.entrySet().stream()
                .forEach(entry -> {
                    String piece = STANDARD_STARTS.get(entry.getKey().getX());

                });
        board.getBoardFacts().getCoordinates("white", "bishop");

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
        Map<Coordinate, Path> candidatePaths = new TreeMap<>();
        Path pieces = new Path();
        if (cage) {
            this.caged.put(start, findPath(board, pieceName, pieceCode, start, new Coordinate(4, 4), escapeLocation).isEmpty());

        } else {
            for (Coordinate target : candidatePieces) {
                System.out.println(originX);

                System.out.println(target);
                if (pieceName.equals("bishop") && (start.getX() + start.getY()) % 2 != (target.getX() + target.getY()) % 2) {
                    continue;
                }
                Path foundPath = findPath(board, pieceName, pieceCode, start, target, escapeLocation);
                System.out.println(foundPath);
                if (!foundPath.isEmpty()) {
                    candidatePaths.put(target, foundPath);
                    pieces.add(target);
                }
            }
            this.startLocations.put(start, candidatePaths);
            this.startPiecePairs.put(start, pieces);
        }

    }

    private Path findPath(BoardInterface board, String pieceName,
                          String pieceCode, Coordinate start,
                          Coordinate target, int escapeLocation){
        return Pathfinder.findShortestPath(
                StandardPieceFactory.getInstance().getPiece(pieceCode),
                start,
                (b, c) -> c.equals(target) || (c.getY() >= 2 && c.getY() <= 5),
                board,
                this.pathConditions.get(pieceName));
    }

}
