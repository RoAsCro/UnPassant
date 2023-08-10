package Heuristics.Deductions;

import Heuristics.BoardInterface;
import Heuristics.Observation;
import Heuristics.Path;
import Heuristics.Pathfinder;
import StandardChess.BoardBuilder;
import StandardChess.Coordinate;
import StandardChess.Coordinates;
import StandardChess.StandardPieceFactory;

import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class PieceMap extends AbstractDeduction{

    private boolean whiteKingMoved = false;
    private boolean blackKingMoved = false;
    private boolean bQRook = false;
    private boolean bKRook = false;
    private boolean wQRook = false;
    private boolean wKRook = false;


    private CombinedPawnMap pawnMap;

//    private PieceNumber = new PieceNumber

    private final Map<Coordinate, Map<Coordinate, Path>> startLocations = new TreeMap<>();
    // Consider deleting
    public final Map<Coordinate, Path> startPiecePairs = new TreeMap<>();
    private final Map<Coordinate, Boolean> caged = new TreeMap<>();

    private final Map<Coordinate, Path> promotedPieceMap;

    // <Type of piece, <potentially promoted pieces, how many are may not be promoted>
    private final Map<String, Map<Path, Integer>> promotionNumbers = new TreeMap<>();



    Predicate<Coordinate> secondRankCollision = coordinate -> {
        int y = coordinate.getY();
        if ((y == 1 || y == 6)) {
            Map<Coordinate, List<Path>> map = y == 1 ? this.pawnMap.getWhitePaths() : this.pawnMap.getBlackPaths();
            if ((map.containsKey(coordinate))) {
                //system.out.println("OGG" + coordinate);
            }
            return !(map.containsKey(coordinate));
        }
        return true;
    };

    Predicate<Path> thirdRankCollision = path -> {
        int y = path.getLast().getY();
        if (y == FIRST_RANK + 2 || y == FINAL_RANK - 2) {
            boolean white = y == FIRST_RANK + 2;
            Path toCheck = this.pawnMap.getSinglePath(white, path.getLast());
            Map<Coordinate, List<Path>> map = white ? this.pawnMap.getWhitePaths() : this.pawnMap.getBlackPaths();
            return !(
                    map.containsKey(path.getLast())
                    && toCheck != null
                    && Pathfinder.pathsExclusive(toCheck, Path.of(path, Coordinates.NULL_COORDINATE)));
        }
        return true;
    };
    Predicate<Path> firstRankCollision = path ->{
//        if (path.getFirst().equals(new Coordinate(0, 7))) {
//            //system.out.println("ptt" + path);
//            if (!(
//                    (path.getLast().getY() == 0 || path.getLast().getY() == 7)
//                            && !STANDARD_STARTS.get(path.getLast().getX()).equals("rook")
//                            && this.startLocations.containsKey(path.getLast())
//                            && !this.startLocations.get(path.getLast()).isEmpty()
//                            && this.caged.containsKey(path.getLast())
//                            && this.caged.get(path.getLast()))) {
//                //system.out.println("ptt" + path);
//            }
//        }
        return !(
                (path.getLast().getY() == 0 || path.getLast().getY() == 7)
                        && !STANDARD_STARTS.get(path.getLast().getX()).equals("rook")
                        && this.startLocations.containsKey(path.getLast())
                        && !this.startLocations.get(path.getLast()).isEmpty()
                        && this.caged.containsKey(path.getLast())
                        && this.caged.get(path.getLast()));
    };

    Predicate<Path> kingCollisionWhite = path -> !path.getLast().equals(Coordinates.WHITE_KING);
    Predicate<Path> kingCollisionBlack = path -> !path.getLast().equals(Coordinates.BLACK_KING);

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
//        pawnMap.deduce(board);
        // bishops -> royalty -> rooks
        Arrays.stream(new int[]{2, 5, 3, 4, 0, 7}).forEach(x -> {
            findFromOrigin(board, x, true, false);
            findFromOrigin(board, x, true, true);
            findFromOrigin(board, x, false, false);
            findFromOrigin(board, x, false, true);
        });


        // For each start location, have each piece associated with it attempt to path to that start
        pathFromOtherDirection(board, this.startLocations);

        // Check if King and Rooks have been displaced
        kingMovementUpdate(board);

        // Every piece for which there are more of the type associated with a start location than there are by default
        //system.out.println("HERERE");
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
        //system.out.println("PP" + potentialPromotions);
        potentialPromotions.forEach((key, value) -> {
            Map<Path, Integer> toPut = new HashMap<Path, Integer>();
            toPut.put(value, pieceNumber.get(key));
//            if (value != null) {
                this.promotionNumbers.put(key, toPut);
//            }
        });
        //system.out.println(this.promotionNumbers);

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
        //system.out.println("THISONE" + this.startPiecePairs);
        //system.out.println("THISONE" + certainPromotions);
        findPromotionPaths(board, certainPromotions);
        //system.out.println("THISONE" + certainPromotions);
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
                //system.out.println(pieceName);

                pieceName = pieceName.substring(0, pieceName.length() - (pieceName.charAt(0) == 'b' ? 2 : 1));
                String pieceCode = entry.getKey().getY() == 0
                        ? pieceName.substring(0, 1).toUpperCase()
                        : pieceName.substring(0, 1).toLowerCase();
                //system.out.println("FINDING PATH" + pieceName);
                if (findPath(board, pieceName, pieceCode, origin, entry.getKey()).isEmpty()){
                    //system.out.println("or" + origin);
                    //system.out.println("t" + entry.getKey());

                    coordinatesToRemove.add(origin);
                }
            });
            coordinatesToRemove.forEach(coordinate -> entry.getValue().remove(coordinate));
        });
        //system.out.println("PM INFO:");
        //system.out.println(this.startLocations);
        //system.out.println(this.caged);
        //system.out.println(this.promotedPieceMap);
        //system.out.println(this.promotionNumbers);

        //system.out.println(certainPromotions);
        //system.out.println(potentialPromotions);
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
        //system.out.println(allPieces);
        //system.out.println(accountedPieces);

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

        //system.out.println("AP" + accountedPromotions);
        //system.out.println("NPP" + numberOfPotentialPromotions);


        if (!certainPromotionCheck || accountedPromotions != numberOfPotentialPromotions){
            this.state = false;
        }

        return false;
    }

    private void kingMovementUpdate(BoardInterface board){

        // Set positioning
        if (!board.getBoardFacts().getCoordinates("white", "rook").contains(Coordinates.WHITE_KING_ROOK)) {
            this.wKRook = true;
        }
        if (!board.getBoardFacts().getCoordinates("white", "rook").contains(Coordinates.WHITE_QUEEN_ROOK)) {
            this.wQRook = true;
        }
        if (this.wKRook && this.wQRook) {
            this.whiteKingMoved = true;
        }
        if (!board.getBoardFacts().getCoordinates("black", "rook").contains(Coordinates.BLACK_KING_ROOK)) {
            this.bKRook = true;
        }
        if (!board.getBoardFacts().getCoordinates("black", "rook").contains(Coordinates.BLACK_QUEEN_ROOK)) {
            this.bQRook = true;
        }
        if (this.bKRook && this.bQRook) {
            this.blackKingMoved = true;
        }
        // Does not account for the possibility that these pieces are promoted
        boolean allPiecesTakenByPawnsW = this.pawnMap.capturesTwo(false) == this.pawnMap.capturablePieces(false);
        boolean allPiecesTakenByPawnsB = this.pawnMap.capturesTwo(true) == this.pawnMap.capturablePieces(true);

        // Check queen
        Coordinate currentQueen = new Coordinate(3, 0);
        boolean white = true;
        boolean currentAllPieces = allPiecesTakenByPawnsW;
        for (int i = 0 ; i < 2 ; i++) {
            if ((white && !this.whiteKingMoved) || (!white && !this.blackKingMoved)) {
                Coordinate finalCurrentQueen = currentQueen;
                boolean finalWhite = white;
                if (this.startLocations.containsKey(currentQueen) && ((this.startLocations.get(currentQueen)
                        .keySet().stream().anyMatch(c -> disturbsKing(board, "queen", PIECE_CODES.get("queen"), finalCurrentQueen, c, finalWhite)))
                        || (this.startLocations.get(currentQueen).isEmpty() && currentAllPieces && disturbsKing(board, "queen", PIECE_CODES.get("queen"), finalCurrentQueen, Coordinates.NULL_COORDINATE, white)))
                ) {
                    if (white) {
                        this.whiteKingMoved = true;
                    } else {
                        this.blackKingMoved = true;
                    }
                }
            }
            currentQueen = new Coordinate(3, 7);
            white = false;
            currentAllPieces = allPiecesTakenByPawnsB;
        }
        // Check rooks
        if (!this.whiteKingMoved) {
            rookKingMovementUpdate(board, true, allPiecesTakenByPawnsW);
        }
        if (!this.blackKingMoved) {
            rookKingMovementUpdate(board, false, allPiecesTakenByPawnsB);
        }
    }


    private void pathFromOtherDirection(BoardInterface board, Map<Coordinate, Map<Coordinate, Path>> paths) {
        paths.entrySet().stream().forEach(entry -> {
            Coordinate key = entry.getKey();
            Path coordinatesToRemove = new Path();
            String pieceName = STANDARD_STARTS.get(key.getX());
            String pieceCode = key.getY() == 0
                    ? pieceName.substring(0, 1).toUpperCase()
                    : pieceName.substring(0, 1).toLowerCase();
            entry.getValue().keySet().stream().forEach(origin -> {

                if (findPath(board, pieceName, pieceCode, origin, key).isEmpty()){
                    coordinatesToRemove.add(origin);
                }
            });
            //system.out.println("c" +coordinatesToRemove);
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

                if (originX == Coordinates.WHITE_KING.getX() && pieceName.equals("king")
                        && !target.equals(white ? Coordinates.WHITE_KING : Coordinates.BLACK_KING)) {
//                    System.out.println(target);
                    if (white) {
                        this.whiteKingMoved = true;
                    } else {
                        this.blackKingMoved = true;
                    }
                }
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

    private void rookKingMovementUpdate(BoardInterface board, boolean white, boolean allPiecesTakenByPawns) {

        // This makes no sense
        // If a rook is caged and taken by a visible pawn, that;ll actually cover the scenario on page 45
        Path rooks = board.getBoardFacts().getCoordinates(white ? "white" : "black", "rook");
        Coordinate kingRook = white ? Coordinates.WHITE_KING_ROOK : Coordinates.BLACK_KING_ROOK;
        Coordinate queenRook = white ? Coordinates.WHITE_QUEEN_ROOK : Coordinates.BLACK_QUEEN_ROOK;

        if (allPiecesTakenByPawns && (!this.startLocations.containsKey(kingRook) || (
                this.startLocations.get(kingRook).isEmpty()
                        || (this.startLocations.get(kingRook).size() == 1 &&
                        this.startLocations.get(kingRook).containsKey(queenRook)))
        )) {
            rooks.add(Coordinates.NULL_COORDINATE);
        }
        else if (allPiecesTakenByPawns && (!this.startLocations.containsKey(queenRook) || (
                this.startLocations.get(queenRook).isEmpty()
                        || (this.startLocations.get(queenRook).size() == 1 &&
                        this.startLocations.get(queenRook).containsKey(kingRook)))
        ))  {
            rooks.add(Coordinates.NULL_COORDINATE);
        }
        boolean kk = (!this.startLocations.containsKey(kingRook) || (
                this.startLocations.get(kingRook).isEmpty()
                        || (
                        !this.startLocations.get(kingRook).containsKey(kingRook))));
        boolean qq = (!this.startLocations.containsKey(queenRook) || (
                this.startLocations.get(queenRook).isEmpty()
                        || (
                        !this.startLocations.get(queenRook).containsKey(queenRook))));
        boolean kingMoved = false;
        for (Coordinate c : rooks) {
            if (c.equals(queenRook) || c.equals(kingRook)) {
                continue;
            }
            if (kingMoved) {
                continue;
            }
            boolean king = c.equals(Coordinates.NULL_COORDINATE) || (this.startLocations.containsKey(kingRook) && this.startLocations.get(kingRook).containsKey(c));
            boolean queen = c.equals(Coordinates.NULL_COORDINATE) || (this.startLocations.containsKey(queenRook) && this.startLocations.get(queenRook).containsKey(c));
            if (qq && queen) {
                kingMoved = disturbsKing(board, "rook", "r", queenRook, c, white);
            }
            else if (kk && king) {
                kingMoved = disturbsKing(board, "rook", "r", kingRook, c, white);
            }
        }
        if (kingMoved) {
            if (white)  {
                this.whiteKingMoved = true;
            } else {
                this.blackKingMoved = true;
            }
        }
    }

    private boolean disturbsKing(BoardInterface board, String pieceName,
                              String pieceCode, Coordinate start,
                              Coordinate target, boolean white) {
        if (white) {
            return !this.whiteKingMoved && findPath(board, pieceName, pieceCode, start, target, kingCollisionWhite).isEmpty();
        } else {
            return !this.blackKingMoved && findPath(board, pieceName, pieceCode, start, target, kingCollisionBlack).isEmpty();
        }
    }
    public Path findPath(BoardInterface board, String pieceName,
                         String pieceCode, Coordinate start,
                         Coordinate target) {
        return findPath(board, pieceName, pieceCode, start, target, p -> true);
    }

    private Path findPath(BoardInterface board, String pieceName,
                          String pieceCode, Coordinate start,
                          Coordinate target,
                         Predicate<Path> additionalCondition){
        //system.out.println(Pathfinder.findShortestPath(
//                StandardPieceFactory.getInstance().getPiece(pieceCode),
//                start,
//                (b, c) -> c.equals(target) || (c.getY() >= 2 && c.getY() <= 5),
//                board,
//                this.pathConditions.get(pieceName)));
        //system.out.println("PIECEIS" + StandardPieceFactory.getInstance().getPiece(pieceCode).getType());

        return Pathfinder.findShortestPath(
                StandardPieceFactory.getInstance().getPiece(pieceCode),
                start,
                (b, c) -> c.equals(target) || (c.getY() >= 2 && c.getY() <= 5),
                board,
                p -> this.pathConditions.get(pieceName).test(p) && additionalCondition.test(p));
    }

    public Map<Coordinate, Path> getPromotedPieceMap(){
        return this.promotedPieceMap;
    }

    public boolean getKingMovement(boolean white) {
        return white ? this.whiteKingMoved : this.blackKingMoved;
    }

    public boolean getRookMovement(boolean white, boolean kingside) {
        return white ? (kingside ? this.wKRook : this.wQRook) : (kingside ? this.bKRook : this.bQRook);
    }


}
