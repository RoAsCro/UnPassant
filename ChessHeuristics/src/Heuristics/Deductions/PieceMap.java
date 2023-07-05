package Heuristics.Deductions;

import Heuristics.BoardInterface;
import Heuristics.Observation;
import Heuristics.Path;
import Heuristics.Pathfinder;
import StandardChess.Coordinate;
import StandardChess.StandardPieceFactory;

import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class PieceMap extends AbstractDeduction{
    private CombinedPawnMap pawnMap;

//    private PieceNumber = new PieceNumber

    private final Map<Coordinate, Map<Coordinate, Path>> startLocations = new TreeMap<>();
    public final Map<Coordinate, Path> startPiecePairs = new TreeMap<>();
    private final Map<Coordinate, List<Path>> pieceMap = new TreeMap<>();
    private final Map<Coordinate, Boolean> caged = new TreeMap<>();

    private final Map<Coordinate, Path> promotedPieceMap;


    private final static Coordinate NULL_COORDINATE = new Coordinate(-1, -1);
    private final static Map<Integer, String> STANDARD_STARTS = Map.of(
            0, "rook", 1, "knight", 2, "bishop", 3, "queen", 4,
            "king", 5, "bishop", 6, "knight", 7, "rook"
    );

    Predicate<Coordinate> secondRankCollision = coordinate -> {
        int y = coordinate.getY();
        if ((y == 1 || y == 6)) {
            Map<Coordinate, List<Path>> map = y == 1 ? this.pawnMap.getWhitePaths() : this.pawnMap.getBlackPaths();
            return !(map.containsKey(coordinate));
        }
        return true;
    };

    Predicate<Path> thirdRankCollision = path -> {
        int y = path.getLast().getY();
        if (y == 2 || y == 5) {
            String colour = y == 2 ? "white" : "black";
            Path toCheck = this.pawnMap.getSinglePath(colour, path.getLast());
            Map<Coordinate, List<Path>> map = y == 2 ? this.pawnMap.getWhitePaths() : this.pawnMap.getBlackPaths();
            return !(
                    map.containsKey(path.getLast())
                    && toCheck != null
                    && Pathfinder.pathsExclusive(toCheck, Path.of(path, NULL_COORDINATE)));
        }
        return true;
    };
    Predicate<Path> firstRankCollision = path -> !(
            (path.getLast().getY() == 0 || path.getLast().getY() == 7)
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
                    && firstRankCollision.test(path),
            "knight", path -> true

    );

    public PieceMap(CombinedPawnMap pawnMap) {
        this.pawnMap = pawnMap;
        this.promotedPieceMap = new TreeMap<>();
        for (int y  = 0 ; y < 8 ; y = y + 7) {
            for (int x = 0; x < 8; x++) {
                this.promotedPieceMap.put(new Coordinate(x, y), new Path());
            }
        }
//        this.caged.put(new Coordinate(1, 0), false);
//        this.caged.put(new Coordinate(1, 7), false);
//        this.caged.put(new Coordinate(6, 0), false);
//        this.caged.put(new Coordinate(6, 7), false);

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
        // bishops -> royalty -> rooks
        Arrays.stream(new int[]{2, 5, 3, 4, 0, 7}).forEach(x -> {
            findFromOrigin(board, x, true, false);
            findFromOrigin(board, x, true, true);
            findFromOrigin(board, x, false, false);
            findFromOrigin(board, x, false, true);
        });

        // For each start location, have each piece associated with it attempt to path to that start
        this.startLocations.entrySet().stream().forEach(entry -> {
            Path coordinatesToRemove = new Path();
            entry.getValue().keySet().stream().forEach(origin -> {
                String pieceName = STANDARD_STARTS.get(entry.getKey().getX());
                String pieceCode = pieceName.substring(0, 1).toUpperCase();
                if (findPath(board, pieceName, pieceCode, origin, entry.getKey(), 0).isEmpty()){
                    coordinatesToRemove.add(origin);
                }
            });
            coordinatesToRemove.forEach(coordinate -> entry.getValue().remove(coordinate));
        });

        // Every piece for which there are more of the type associated with a start location than there are by default
        System.out.println("HERERE");
        Map<String, Path> pieces = new TreeMap<>();
        Map<String, Integer> pieceNumber = new TreeMap<>();
        int rookNumber = 0;
//        Map<String, Integer> quantities = new TreeMap<>();
        for (int y  = 0 ; y < 8 ; y = y + 7) {
            for (int x = 0; x < 8; x++) {
                Coordinate origin = new Coordinate(x, y);
                if (x == 1 || x == 6) {
                    continue;
                }
                if (!this.caged.get(origin)) {
                    Set<Coordinate> pieceLocations = this.startLocations.get(origin).keySet();
                    String name = STANDARD_STARTS.get(x);
                    if (name.equals("bishop")) {
                        name = name + x;
                    }
                    if (y == 0) {
                        name = name + "w";
                    } else {
                        name = name + "b";
                    }

                    if (pieces.containsKey(name)) {
                        pieces.get(name).addAll(pieceLocations);
                        pieceNumber.put(name, pieceNumber.get(name) + 1);
                    } else {
                        pieces.put(name, Path.of(pieceLocations));
                        pieceNumber.put(name, 1);
                    }
                }
            }
        }
        Map<String, Path> piecesTwo = new TreeMap<>();
        pieces.forEach((key, value) -> piecesTwo.put(key,
                Path.of(value.stream().distinct().toList())));
        piecesTwo.put("knightw", board.getBoardFacts().getCoordinates("white", "knight"));
        pieceNumber.put("knightw", 2);
        piecesTwo.put("knightb", board.getBoardFacts().getCoordinates("black", "knight"));
        pieceNumber.put("knightb", 2);

        Map<String, Path> potentialPromotions = piecesTwo.entrySet().stream()
                .filter((entry) -> entry.getValue().size() > pieceNumber.get(entry.getKey()))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

        this.promotedPieceMap.entrySet().stream().forEach(outerEntry -> {
            potentialPromotions.entrySet().stream().forEach(entry -> {
                if ((outerEntry.getKey().getY() == 0 && entry.getKey().charAt(entry.getKey().length() - 1) == 'w') ||
                        (outerEntry.getKey().getY() == 7 && entry.getKey().charAt(entry.getKey().length() - 1) == 'b')) {
                    return;
                }
                String pieceName = entry.getKey().substring(0, entry.getKey().length() -
                        (entry.getKey().charAt(0) == 'b' ? 2 : 1));
                System.out.println(pieceName);
                String pieceCodeTemp = entry.getKey().charAt(1) == 'n' ? "n" : pieceName.substring(0, 1);
                String pieceCode = entry.getKey().charAt(entry.getKey().length() - 1) == 'w'
                        ? pieceCodeTemp.toUpperCase() :
                        pieceCodeTemp.toLowerCase();
                entry.getValue().stream()
                        .filter(coordinate -> !findPath(board, pieceName, pieceCode, outerEntry.getKey(), coordinate, 0).isEmpty())
                        .forEach(coordinate -> this.promotedPieceMap.get(outerEntry.getKey()).add(coordinate));

            });
        });

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

    public Map<Coordinate, Path> getPromotedPieceMap(){
        return this.promotedPieceMap;
    }

}
