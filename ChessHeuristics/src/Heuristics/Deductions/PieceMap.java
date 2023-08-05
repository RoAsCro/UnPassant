package Heuristics.Deductions;

import Heuristics.BoardInterface;
import Heuristics.Observation;
import Heuristics.Path;
import Heuristics.Pathfinder;
import StandardChess.BoardBuilder;
import StandardChess.Coordinate;
import StandardChess.StandardPieceFactory;

import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class PieceMap extends AbstractDeduction{
    private CombinedPawnMap pawnMap;

//    private PieceNumber = new PieceNumber

    private final Map<Coordinate, Map<Coordinate, Path>> startLocations = new TreeMap<>();
    // Consider deleting
    public final Map<Coordinate, Path> startPiecePairs = new TreeMap<>();
    private final Map<Coordinate, List<Path>> pieceMap = new TreeMap<>();
    private final Map<Coordinate, Boolean> caged = new TreeMap<>();

    private final Map<Coordinate, Path> promotedPieceMap;

    // <Type of piece, <potentially promoted pieces, how many are may not be promoted>
    private final Map<String, Map<Path, Integer>> promotionNumbers = new TreeMap<>();


    private final static Coordinate NULL_COORDINATE = new Coordinate(-1, -1);
    private final static Map<Integer, String> STANDARD_STARTS = Map.of(
            0, "rook", 1, "knight", 2, "bishop", 3, "queen", 4,
            "king", 5, "bishop", 6, "knight", 7, "rook"
    );

    Predicate<Coordinate> secondRankCollision = coordinate -> {
        int y = coordinate.getY();
        if ((y == 1 || y == 6)) {
            Map<Coordinate, List<Path>> map = y == 1 ? this.pawnMap.getWhitePaths() : this.pawnMap.getBlackPaths();
            if ((map.containsKey(coordinate))) {
                System.out.println("OGG" + coordinate);
            }
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
    Predicate<Path> firstRankCollision = path ->{
        if (path.getFirst().equals(new Coordinate(0, 7))) {
            System.out.println("ptt" + path);
            if (!(
                    (path.getLast().getY() == 0 || path.getLast().getY() == 7)
                            && !STANDARD_STARTS.get(path.getLast().getX()).equals("rook")
                            && this.startLocations.containsKey(path.getLast())
                            && !this.startLocations.get(path.getLast()).isEmpty()
                            && this.caged.containsKey(path.getLast())
                            && this.caged.get(path.getLast()))) {
                System.out.println("ptt" + path);
            }
        }
        return !(
                (path.getLast().getY() == 0 || path.getLast().getY() == 7)
                        && !STANDARD_STARTS.get(path.getLast().getX()).equals("rook")
                        && this.startLocations.containsKey(path.getLast())
                        && !this.startLocations.get(path.getLast()).isEmpty()
                        && this.caged.containsKey(path.getLast())
                        && this.caged.get(path.getLast()));
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

    public Map<String, Map<Path, Integer>> getPromotionNumbers() {
        return this.promotionNumbers;
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
        pathFromOtherDirection(board, this.startLocations);

        // Every piece for which there are more of the type associated with a start location than there are by default
        System.out.println("HERERE");
        Map<String, Path> pieces = new TreeMap<>();
        Map<String, Integer> pieceNumber = new TreeMap<>();
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

        findPromotionPaths(board, potentialPromotions);
        System.out.println("PP" + potentialPromotions);
        potentialPromotions.forEach((key, value) -> {
            Map<Path, Integer> toPut = new HashMap<Path, Integer>();
            toPut.put(value, pieceNumber.get(key));
//            if (value != null) {
                this.promotionNumbers.put(key, toPut);
//            }
        });
        System.out.println(this.promotionNumbers);

        Map<String, Path> certainPromotions = new TreeMap<>();
        Path foundPieces = Path.of(this.startLocations.values().stream().map(Map::keySet).flatMap(Collection::stream).toList());
        Arrays.stream(new int[]{0, 2, 3}).forEach(x -> {
            String pieceName = STANDARD_STARTS.get(x);
            String bishopAddition = pieceName.charAt(0) == 'b' ? "b" : "";
            Path promotions = Path.of(board.getBoardFacts().getCoordinates("white", pieceName)
                    .stream()
                    .filter(coordinate -> !foundPieces.contains(coordinate))
                    .toList());
            certainPromotions.put(pieceName + bishopAddition + "w", promotions);
            promotions = Path.of(board.getBoardFacts().getCoordinates("black", pieceName)
                    .stream()
                    .filter(coordinate -> !foundPieces.contains(coordinate))
                    .toList());
            certainPromotions.put(pieceName + bishopAddition + "b", promotions);
        });
        System.out.println("THISONE" + this.startPiecePairs);
        System.out.println("THISONE" + certainPromotions);
        findPromotionPaths(board, certainPromotions);
        System.out.println("THISONE" + certainPromotions);
        certainPromotions.forEach((key, value) -> {
            Map<Path, Integer> toPut = new HashMap<>();
            toPut.put(value, 0);
            if (!this.promotionNumbers.containsKey(key)) {
                this.promotionNumbers.put(key, toPut);
            } else {
                this.promotionNumbers.get(key).put(value, 0);
            }
//            Map<Path, Integer> map =
//            if (!(map == null)) {
//                map.put(value, 1);
//            }
        });
//        pathFromOtherDirection(board, this.promotedPieceMap);
        // Map pieces to their promotion origins
        Path certainPromotionsPath = Path.of(certainPromotions.values().stream()
                        .filter(value -> !(value == null))
                .flatMap(Collection::stream).toList());
        this.promotedPieceMap.entrySet().stream().forEach(entry -> {
            Path coordinatesToRemove = new Path();
            entry.getValue().stream().forEach(origin -> {
                String pieceName;
                Map<String, Path> stringOrigin;
                if (certainPromotionsPath.contains(origin)) {
                    stringOrigin = certainPromotions;
                } else if (foundPieces.contains(origin)) {
                    stringOrigin = potentialPromotions;
                } else {
                    stringOrigin = Map.of("knightw", Path.of(origin));
                }
                pieceName = stringOrigin.entrySet()
                        .stream()
                        .filter(innerEntry -> !(innerEntry.getValue() == null))
                        .filter(innerEntry -> innerEntry.getValue().contains(origin))
                        .map(Map.Entry::getKey)
                        .findAny()
                        .orElse("");
                System.out.println(pieceName);

                pieceName = pieceName.substring(0, pieceName.length() - (pieceName.charAt(0) == 'b' ? 2 : 1));
                String pieceCode = entry.getKey().getY() == 0
                        ? pieceName.substring(0, 1).toUpperCase()
                        : pieceName.substring(0, 1).toLowerCase();
                System.out.println("FINDING PATH" + pieceName);
                if (findPath(board, pieceName, pieceCode, origin, entry.getKey()).isEmpty()){
                    System.out.println("or" + origin);
                    System.out.println("t" + entry.getKey());

                    coordinatesToRemove.add(origin);
                }
            });
            coordinatesToRemove.forEach(coordinate -> entry.getValue().remove(coordinate));
        });
        // TODO Check certainPromotions and potentialPromotions VS this.promotedPieceMap and this.promotionNumbers
        System.out.println("PM INFO:");
        System.out.println(this.startLocations);
        System.out.println(this.caged);
        System.out.println(this.promotedPieceMap);
        System.out.println(this.promotionNumbers);

        System.out.println(certainPromotions);
        System.out.println(potentialPromotions);
        List<Coordinate> allPieces = new ArrayList<>(board.getBoardFacts().getAllCoordinates("white").entrySet().stream()
                .filter(entry -> !entry.getKey().equals("pawn"))
                .filter(entry -> !entry.getKey().equals("knight"))
                .flatMap(entry -> entry.getValue().stream()).toList());
        allPieces.addAll(board.getBoardFacts().getAllCoordinates("black").entrySet().stream()
                .filter(entry -> !entry.getKey().equals("pawn"))
                .filter(entry -> !entry.getKey().equals("knight"))
                .flatMap(entry -> entry.getValue().stream()).toList());
        List<Coordinate> accountedPieces = new ArrayList<>(this.promotedPieceMap.values().stream().flatMap(Path::stream).toList());
        accountedPieces.addAll(this.startLocations.values().stream().map(Map::keySet).flatMap(Set::stream).toList());
        System.out.println(allPieces);
        System.out.println(accountedPieces);

        boolean certainPromotionCheck = new HashSet<>(accountedPieces).containsAll(allPieces);

        int numberOfPotentialPromotions = potentialPromotions.size();

        int accountedPromotions = potentialPromotions.entrySet().stream().filter(entry -> {
            if (entry.getValue() != null) {
                int promotions = this.promotionNumbers.get(entry.getKey()).get(entry.getValue());
                int potentiallyPromoted = 0;
                for (Coordinate coordinate : entry.getValue()) {
                    this.promotedPieceMap.values().stream().flatMap(Path::stream).anyMatch(c -> c.equals(coordinate));
                    potentiallyPromoted++;
                }
                if (potentiallyPromoted < entry.getValue().size() - promotions) {
                    return false;
                }
                return true;
            }
            return false;})
                .toList()
                .size();

        System.out.println("AP" + accountedPromotions);
        System.out.println("NPP" + numberOfPotentialPromotions);


        if (!certainPromotionCheck || accountedPromotions != numberOfPotentialPromotions){
            this.state = false;
        }

        return false;
    }


    private void pathFromOtherDirection(BoardInterface board, Map<Coordinate, Map<Coordinate, Path>> paths) {
        paths.entrySet().stream().forEach(entry -> {
            Path coordinatesToRemove = new Path();
            String pieceName = STANDARD_STARTS.get(entry.getKey().getX());
            String pieceCode = entry.getKey().getY() == 0
                    ? pieceName.substring(0, 1).toUpperCase()
                    : pieceName.substring(0, 1).toLowerCase();
            entry.getValue().keySet().stream().forEach(origin -> {

                if (findPath(board, pieceName, pieceCode, origin, entry.getKey()).isEmpty()){
                    coordinatesToRemove.add(origin);
                }
            });
            System.out.println("c" +coordinatesToRemove);
            coordinatesToRemove.forEach(coordinate -> entry.getValue().remove(coordinate));
        });
    }

    private void findPromotionPaths(BoardInterface board, Map<String, Path> potentialPromotions) {
        Map<String, Path> updatedPromotions = new TreeMap<>();
        this.promotedPieceMap.entrySet().stream().forEach(outerEntry -> {
            potentialPromotions.entrySet().stream().forEach(entry -> {
                if ((outerEntry.getKey().getY() == 0 && entry.getKey().charAt(entry.getKey().length() - 1) == 'w') ||
                        (outerEntry.getKey().getY() == 7 && entry.getKey().charAt(entry.getKey().length() - 1) == 'b')) {
                    return;
                }
                String pieceName = entry.getKey().substring(0, entry.getKey().length() -
                        (entry.getKey().charAt(0) == 'b' ? 2 : 1));
                String pieceCodeTemp = entry.getKey().charAt(1) == 'n' ? "n" : pieceName.substring(0, 1);
                String pieceCode = entry.getKey().charAt(entry.getKey().length() - 1) == 'w'
                        ? pieceCodeTemp.toUpperCase() :
                        pieceCodeTemp.toLowerCase();
                entry.getValue().stream()
                        .filter(coordinate -> {
                            Coordinate origin = outerEntry.getKey();
                            if (pieceCode.equalsIgnoreCase("b")
                                    && (origin.getX() + origin.getY()) % 2 != (coordinate.getX() + coordinate.getY()) % 2){
                                return false;
                            }
                            Path path = findPath(board, pieceName, pieceCode, origin, coordinate);
                            if (path.isEmpty()) {
                                return false;
                            }
                            // First coordinate is never tested normally
                            return this.firstRankCollision.test(Path.of(path.getFirst()));
                        })
                        .forEach(coordinate -> {
                            if (!updatedPromotions.containsKey(entry.getKey())) {
                                updatedPromotions.put(entry.getKey(), Path.of());
                            }
                            if (!updatedPromotions.get(entry.getKey()).contains(coordinate)){
                                updatedPromotions.get(entry.getKey()).add(coordinate);
                            }
                            this.promotedPieceMap.get(outerEntry.getKey()).add(coordinate);
                        });
            });
        });
        potentialPromotions.keySet().forEach(name -> potentialPromotions.put(name, updatedPromotions.get(name)));
    }
    private void findFromOrigin(BoardInterface board, int originX, boolean white, boolean cage) {

        Coordinate start = new Coordinate(originX, white ? 0 : 7);
        String pieceName = STANDARD_STARTS.get(originX);
        String pieceCode = pieceName.substring(0, 1);
        if (white){
            pieceCode = pieceCode.toUpperCase();
        }
        //
        List<Coordinate> candidatePieces = board.getBoardFacts().getCoordinates(white ? "white" : "black", pieceName);
        Map<Coordinate, Path> candidatePaths = new TreeMap<>();
        Path pieces = new Path();
        if (cage) {
            this.caged.put(start, findPath(board, pieceName, pieceCode, start, new Coordinate(4, 4)).isEmpty());
        } else {
            for (Coordinate target : candidatePieces) {
                if (pieceName.equals("bishop") && (start.getX() + start.getY()) % 2 != (target.getX() + target.getY()) % 2) {
                    continue;
                }
                Path foundPath = findPath(board, pieceName, pieceCode, start, target);
                if (!foundPath.isEmpty()) {
                    candidatePaths.put(target, foundPath);
                    pieces.add(target);
                }
            }
            this.startLocations.put(start, candidatePaths);
            this.startPiecePairs.put(start, pieces);
        }

    }

    public Path findPath(BoardInterface board, String pieceName,
                          String pieceCode, Coordinate start,
                          Coordinate target){
        System.out.println(Pathfinder.findShortestPath(
                StandardPieceFactory.getInstance().getPiece(pieceCode),
                start,
                (b, c) -> c.equals(target) || (c.getY() >= 2 && c.getY() <= 5),
                board,
                this.pathConditions.get(pieceName)));
        System.out.println("PIECEIS" + StandardPieceFactory.getInstance().getPiece(pieceCode).getType());

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
