package Heuristics.Deductions;

import Heuristics.BoardInterface;
import Heuristics.Path;
import Heuristics.Detector.StateDetector;
import StandardChess.Coordinate;
import StandardChess.Coordinates;

import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static Heuristics.HeuristicsUtil.*;

public class PieceMap extends AbstractDeduction{

    // Get rid of the one usage of this
    private PiecePathFinderUtil pathFinderUtil;
    Predicate<Path> kingCollisionWhite = path -> !path.getLast().equals(Coordinates.WHITE_KING);
    Predicate<Path> kingCollisionBlack = path -> !path.getLast().equals(Coordinates.BLACK_KING);

    public PieceMap() {
        super("Illegal piece positions.");
    }
    @Override
    public void registerDetector(StateDetector detector) {
        super.registerDetector(detector);
        this.pathFinderUtil = new PiecePathFinderUtil(detector);
        for (int y  = 0 ; y < 8 ; y = y + 7) {
            for (int x = 0; x < 8; x++) {
                this.detector.getPromotedPieceMap().put(new Coordinate(x, y), new Path());
            }
        }
    }

    @Override
    public boolean deduce(BoardInterface board) {
        Arrays.stream(new int[]{2, 5, 4, 3, 0, 7}).forEach(x -> {
            findFromOrigin(board, x, true, false);
            findFromOrigin(board, x, false, false);

            findFromOrigin(board, x, true, true);
            findFromOrigin(board, x, false, true);
            configureCastling(board);
        });
        configureCastlingPartTwo(board);

        // For each start location, have each piece associated with it attempt to path to that start
        pathFromOtherDirection(board, this.detector.getStartLocations());

        // Check if King and Rooks have been displaced
        kingMovementUpdate(board);

        // Every piece for which there are more of the type associated with a start location than there are by default
        Map<String, Path> pieces = new TreeMap<>();
        Map<String, Integer> pieceNumber = new TreeMap<>();
//        Map<String, Integer> quantities = new TreeMap<>();

        for (int y  = 0 ; y < 8 ; y = y + 7) {
            for (int x = 0; x < 8; x++) {
                Coordinate origin = new Coordinate(x, y);
                if (x == 1 || x == 6) {
                    continue;
                }
                if (!this.detector.getCaged().get(origin)) {
                    Set<Coordinate> pieceLocations = this.detector.getStartLocations().get(origin).keySet();
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
        piecesTwo.put("knightw", board.getBoardFacts().getCoordinates(true, "knight"));
        pieceNumber.put("knightw", 2);
        piecesTwo.put("knightb", board.getBoardFacts().getCoordinates(false, "knight"));
        pieceNumber.put("knightb", 2);

        Map<String, Path> potentialPromotions = piecesTwo.entrySet().stream()
                .filter((entry) -> entry.getValue().size() > pieceNumber.get(entry.getKey()))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));



        findPromotionPaths(board, potentialPromotions);

        potentialPromotions.forEach((key, value) -> {
            Map<Path, Integer> toPut = new HashMap<Path, Integer>();
            toPut.put(value, pieceNumber.get(key));
//            if (value != null) {
                this.detector.getPromotionNumbers().put(key, toPut);
//            }
        });

        //system.out.println(this.promotionNumbers);

        Map<String, Path> certainPromotions = new TreeMap<>();
        Path foundPieces = Path.of(this.detector.getStartLocations().values().stream().map(Map::keySet).flatMap(Collection::stream).toList());
        Arrays.stream(new int[]{0, 2, 3}).forEach(x -> {
            String pieceName = STANDARD_STARTS.get(x);
            String bishopAddition = pieceName.charAt(0) == 'b' ? "b" : "";
            Path promotions = Path.of(board.getBoardFacts().getCoordinates(true, pieceName)
                    .stream()
                    .filter(coordinate -> !foundPieces.contains(coordinate))
                    .toList());
            certainPromotions.put(pieceName + bishopAddition + "w", promotions);
            promotions = Path.of(board.getBoardFacts().getCoordinates(false, pieceName)
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
            if (!this.detector.getPromotionNumbers().containsKey(key)) {
                this.detector.getPromotionNumbers().put(key, toPut);
            } else {
                this.detector.getPromotionNumbers().get(key).put(value, 0);
            }

        });

        // Map pieces to their promotion origins
        Path certainPromotionsPath = Path.of(certainPromotions.values().stream()
                        .filter(value -> !(value == null))
                .flatMap(Collection::stream).toList());
        this.detector.getPromotedPieceMap().entrySet().stream().forEach(entry -> {
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
                pieceName = pieceName.substring(0, pieceName.length() - (pieceName.charAt(0) == 'b' ? 2 : 1));
                String pieceCode = entry.getKey().getY() == 0
                        ? pieceName.substring(0, 1).toUpperCase()
                        : pieceName.substring(0, 1).toLowerCase();
                //system.out.println("FINDING PATH" + pieceName);
                if (this.pathFinderUtil.findPiecePath(board, pieceName, pieceCode, origin, entry.getKey()).isEmpty()){
                    //system.out.println("or" + origin);
                    //system.out.println("t" + entry.getKey());

                    coordinatesToRemove.add(origin);
                }
            });
            coordinatesToRemove.forEach(coordinate -> entry.getValue().remove(coordinate));
        });

        List<Coordinate> allPieces = new ArrayList<>(board.getBoardFacts().getAllCoordinates("white").entrySet().stream()
                .filter(entry -> !entry.getKey().equals("pawn"))
                .filter(entry -> !entry.getKey().equals("knight"))
                .flatMap(entry -> entry.getValue().stream()).toList());
        allPieces.addAll(board.getBoardFacts().getAllCoordinates("black").entrySet().stream()
                .filter(entry -> !entry.getKey().equals("pawn"))
                .filter(entry -> !entry.getKey().equals("knight"))
                .flatMap(entry -> entry.getValue().stream()).toList());
        List<Coordinate> accountedPieces = new ArrayList<>(this.detector.getPromotedPieceMap().values().stream().flatMap(Path::stream).toList());
        accountedPieces.addAll(this.detector.getStartLocations().values().stream().map(Map::keySet).flatMap(Set::stream).toList());
        //system.out.println(allPieces);
        //system.out.println(accountedPieces);

        boolean certainPromotionCheck = new HashSet<>(accountedPieces).containsAll(allPieces);

        int numberOfPotentialPromotions = potentialPromotions.size();
        int accountedPromotions = potentialPromotions.entrySet().stream().filter(entry -> {
            if (entry.getValue() != null) {
                int promotions = this.detector.getPromotionNumbers().get(entry.getKey()).get(entry.getValue());
                int potentiallyPromoted = 0;
                for (Coordinate coordinate : entry.getValue()) {
                    this.detector.getPromotedPieceMap().values().stream().flatMap(Path::stream).anyMatch(c -> c.equals(coordinate));
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
            if (!certainPromotionCheck) {
                this.errorMessage = "Not all promoted pieces can path from a promotion square.";
            } else {
                this.errorMessage = "Too many promotions.";
            }
            this.state = false;
        }

        return false;
    }

    private void configureCastling(BoardInterface board) {
//        System.out.println("tripped");
        if (board.canKingMove(true)) {
            this.detector.getCaged().put(Coordinates.WHITE_KING, true);
        }
        if (board.canKingMove(false)) {
            this.detector.getCaged().put(Coordinates.BLACK_KING, true);
        }
        if (board.canMove(true, true)) {
            this.detector.getCaged().put(Coordinates.WHITE_QUEEN_ROOK, true);
//            this.startLocations.get(Coordinates.WHITE_QUEEN_ROOK).keySet()
//                    .stream()
//                    .filter(c -> !c.equals(Coordinates.WHITE_QUEEN_ROOK))
//                    .collect(Collectors.toList())
//                    .forEach(c -> this.startLocations.get(Coordinates.WHITE_QUEEN_ROOK).remove(c));
//            this.startLocations.get(Coordinates.WHITE_KING_ROOK).keySet().remove(Coordinates.WHITE_QUEEN_ROOK);
        }
        if (board.canMove(true, false)) {
            this.detector.getCaged().put(Coordinates.WHITE_KING_ROOK, true);
//            this.startLocations.get(Coordinates.WHITE_KING_ROOK).keySet()
//                    .stream()
//                    .filter(c -> !c.equals(Coordinates.WHITE_KING_ROOK))
//                    .collect(Collectors.toList())
//                    .forEach(c -> this.startLocations.get(Coordinates.WHITE_KING_ROOK).remove(c));
        }
        if (board.canMove(false, true)) {
            this.detector.getCaged().put(Coordinates.BLACK_QUEEN_ROOK, true);
        }
        if (board.canMove(false, false)) {
            this.detector.getCaged().put(Coordinates.BLACK_KING_ROOK, true);
        }
//        System.out.println(this.caged);
    }
    private void configureCastlingPartTwo(BoardInterface board) {
//        Path rookCoords = Path.of(Coordinates.WHITE_QUEEN_ROOK, Coordinates.WHITE_KING_ROOK, Coordinates.BLACK_QUEEN_ROOK, Coordinates.BLACK_KING_ROOK);
        if (board.canMove(true, true)) {
            this.detector.getStartLocations().get(Coordinates.WHITE_QUEEN_ROOK).keySet()
                    .stream()
                    .filter(c -> !c.equals(Coordinates.WHITE_QUEEN_ROOK))
                    .toList()
                    .forEach(c -> this.detector.getStartLocations().get(Coordinates.WHITE_QUEEN_ROOK).remove(c));
            this.detector.getStartLocations().get(Coordinates.WHITE_KING_ROOK).keySet().remove(Coordinates.WHITE_QUEEN_ROOK);
        }
        if (board.canMove(true, false)) {
            this.detector.getStartLocations().get(Coordinates.WHITE_KING_ROOK).keySet()
                    .stream()
                    .filter(c -> !c.equals(Coordinates.WHITE_KING_ROOK))
                    .toList()
                    .forEach(c -> this.detector.getStartLocations().get(Coordinates.WHITE_KING_ROOK).remove(c));
            this.detector.getStartLocations().get(Coordinates.WHITE_QUEEN_ROOK).keySet().remove(Coordinates.WHITE_KING_ROOK);

        }
        if (board.canMove(false, true)) {
            this.detector.getStartLocations().get(Coordinates.BLACK_QUEEN_ROOK).keySet()
                    .stream()
                    .filter(c -> !c.equals(Coordinates.BLACK_QUEEN_ROOK))
                    .toList()
                    .forEach(c -> this.detector.getStartLocations().get(Coordinates.BLACK_QUEEN_ROOK).remove(c));
            this.detector.getStartLocations().get(Coordinates.BLACK_KING_ROOK).keySet().remove(Coordinates.BLACK_QUEEN_ROOK);
        }
        if (board.canMove(false, false)) {
            this.detector.getStartLocations().get(Coordinates.BLACK_KING_ROOK).keySet()
                    .stream()
                    .filter(c -> !c.equals(Coordinates.BLACK_KING_ROOK))
                    .toList()
                    .forEach(c -> this.detector.getStartLocations().get(Coordinates.BLACK_KING_ROOK).remove(c));
            this.detector.getStartLocations().get(Coordinates.BLACK_QUEEN_ROOK).keySet().remove(Coordinates.BLACK_KING_ROOK);
        }
    }


    private void kingMovementUpdate(BoardInterface board){

        // Set positioning
        String rook = "rook";
        if (!board.getBoardFacts().getCoordinates(true, rook).contains(Coordinates.WHITE_KING_ROOK)) {
            this.detector.setRookMovement(true, false, true);
        }
        if (!board.getBoardFacts().getCoordinates(true, rook).contains(Coordinates.WHITE_QUEEN_ROOK)) {
            this.detector.setRookMovement(true, true, true);

        }
        if (this.detector.getRookMovement(true, false) && this.detector.getRookMovement(true, true)) {
            this.detector.setKingMovement(true, true);
        }
        if (!board.getBoardFacts().getCoordinates(false, rook).contains(Coordinates.BLACK_KING_ROOK)) {
            this.detector.setRookMovement(false, false, true);

        }
        if (!board.getBoardFacts().getCoordinates(false, rook).contains(Coordinates.BLACK_QUEEN_ROOK)) {
            this.detector.setRookMovement(false, true, true);
        }
        if (this.detector.getRookMovement(false, false) && this.detector.getRookMovement(false, true)) {
            this.detector.setKingMovement(false, true);
        }
        // Does not account for the possibility that these pieces are promoted
        int minnimumCaptures = this.detector.minimumPawnCaptures(false);

        boolean allPiecesTakenByPawnsW = minnimumCaptures != 0 && minnimumCaptures == this.detector.pawnTakeablePieces(true) - board.getBoardFacts().pieceNumbers(true);
        minnimumCaptures = this.detector.minimumPawnCaptures(true);
        boolean allPiecesTakenByPawnsB = minnimumCaptures != 0 && minnimumCaptures == this.detector.pawnTakeablePieces(false) - board.getBoardFacts().pieceNumbers(false);

        // Check queen
        Coordinate currentQueen = new Coordinate(QUEEN_X, FIRST_RANK_Y);
        boolean white = true;
        boolean currentAllPieces = allPiecesTakenByPawnsW;
        for (int i = 0 ; i < 2 ; i++) {
            if ((white && !this.detector.getKingMovement(true)) || (!white && !this.detector.getKingMovement(false))) {
                Coordinate finalCurrentQueen = currentQueen;
                boolean finalWhite = white;
                if (this.detector.getStartLocations().containsKey(currentQueen) && ((this.detector.getStartLocations().get(currentQueen)
                        .keySet().stream().anyMatch(c -> disturbsKing(board, "queen", PIECE_CODES.get("queen"), finalCurrentQueen, c, finalWhite)))
                        || (this.detector.getStartLocations().get(currentQueen).isEmpty() && currentAllPieces && disturbsKing(board, "queen", PIECE_CODES.get("queen"), finalCurrentQueen, Coordinates.NULL_COORDINATE, white)))
                ) {
                    this.detector.setKingMovement(white, true);
                }
            }
            currentQueen = new Coordinate(QUEEN_X, FINAL_RANK_Y);
            white = false;
            currentAllPieces = allPiecesTakenByPawnsB;
        }
        // Check rooks
        if (!this.detector.getKingMovement(true)) {
            rookKingMovementUpdate(board, true, allPiecesTakenByPawnsW);
        }
        if (!this.detector.getKingMovement(false)) {
            rookKingMovementUpdate(board, false, allPiecesTakenByPawnsB);
        }
    }


    private void pathFromOtherDirection(BoardInterface board, Map<Coordinate, Map<Coordinate, Path>> paths) {
        paths.entrySet().stream().forEach(entry -> {
            Coordinate key = entry.getKey();
            Path coordinatesToRemove = new Path();
            String pieceName = STANDARD_STARTS.get(key.getX());
            String pieceCode = key.getY() == FIRST_RANK_Y
                    ? pieceName.substring(0, 1).toUpperCase()
                    : pieceName.substring(0, 1).toLowerCase();
            entry.getValue().keySet().forEach(origin -> {

                if (this.pathFinderUtil.findPiecePath(board, pieceName, pieceCode, origin, key).isEmpty()){
                    coordinatesToRemove.add(origin);
                }
            });
            //system.out.println("c" +coordinatesToRemove);
            coordinatesToRemove.forEach(coordinate -> entry.getValue().remove(coordinate));
        });
    }

    private void findPromotionPaths(BoardInterface board, Map<String, Path> potentialPromotions) {
        Map<String, Path> updatedPromotions = new TreeMap<>();
        this.detector.getPromotedPieceMap().entrySet().forEach(outerEntry -> {
            potentialPromotions.entrySet().forEach(entry -> {
                if ((outerEntry.getKey().getY() == 0 && entry.getKey().charAt(entry.getKey().length() - 1) == 'w') ||
                        (outerEntry.getKey().getY() == FINAL_RANK_Y && entry.getKey().charAt(entry.getKey().length() - 1) == 'b')) {
                    return;
                }
                String pieceName = entry.getKey().substring(0, entry.getKey().length() -
                        (entry.getKey().charAt(0) == 'b' ? 2 : 1));
                String pieceCodeTemp = PIECE_CODES.get(pieceName);
                String pieceCode = entry.getKey().charAt(entry.getKey().length() - 1) == 'w'
                        ? pieceCodeTemp.toUpperCase() :
                        pieceCodeTemp.toLowerCase();
                entry.getValue().stream()
                        .filter(coordinate -> {
                            Coordinate origin = outerEntry.getKey();
                            if (pieceCode.equalsIgnoreCase("b")
                                    && Coordinates.light(origin) != Coordinates.light(coordinate)){
                                return false;
                            }
                            Path path = this.pathFinderUtil.findPiecePath(board, pieceName, pieceCode, origin, coordinate);
                            if (path.isEmpty()) {
                                return false;
                            }
                            // First coordinate is never tested normally
                            return this.pathFinderUtil.firstRankCollision.test(Path.of(path.getFirst()));
                        })
                        .forEach(coordinate -> {
                            if (!updatedPromotions.containsKey(entry.getKey())) {
                                updatedPromotions.put(entry.getKey(), Path.of());
                            }
                            if (!updatedPromotions.get(entry.getKey()).contains(coordinate)){
                                updatedPromotions.get(entry.getKey()).add(coordinate);
                            }
                            this.detector.getPromotedPieceMap().get(outerEntry.getKey()).add(coordinate);
                        });
            });
        });
        potentialPromotions.keySet().forEach(name -> potentialPromotions.put(name, updatedPromotions.get(name)));
    }
    private void findFromOrigin(BoardInterface board, int originX, boolean white, boolean cage) {

        Coordinate start = new Coordinate(originX, white ? FIRST_RANK_Y : FINAL_RANK_Y);
        String pieceName = STANDARD_STARTS.get(originX);
        String pieceCode = pieceName.substring(0, 1);
        if (white){
            pieceCode = pieceCode.toUpperCase();
        }
        //
        List<Coordinate> candidatePieces = board.getBoardFacts().getCoordinates(white, pieceName);
        Map<Coordinate, Path> candidatePaths = new TreeMap<>();
        Path pieces = new Path();
        if (cage) {
            if (!(this.detector.getCaged().containsKey(start) && this.detector.getCaged().get(start))) {
                this.detector.getCaged().put(start, this.pathFinderUtil.findPiecePath(board, pieceName, pieceCode, start, Coordinates.NULL_COORDINATE).isEmpty());
            }
        } else {
            for (Coordinate target : candidatePieces) {

                if (originX == Coordinates.WHITE_KING.getX() && pieceName.equals("king")
                        && !target.equals(white ? Coordinates.WHITE_KING : Coordinates.BLACK_KING)) {
//                    System.out.println(target);
                    this.detector.setKingMovement(white, true);
                }
                if (pieceName.equals("bishop") && Coordinates.light(start) != Coordinates.light(target)) {
                    continue;
                }
                Path foundPath = this.pathFinderUtil.findPiecePath(board, pieceName, pieceCode, start, target);
                if (!foundPath.isEmpty()) {
                    candidatePaths.put(target, foundPath);
                    pieces.add(target);

                }

            }
            this.detector.getStartLocations().put(start, candidatePaths);
        }

    }

    private void rookKingMovementUpdate(BoardInterface board, boolean white, boolean allPiecesTakenByPawns) {
        String rook = "rook";
        Path rooks = board.getBoardFacts().getCoordinates(white, rook);
        Coordinate kingRook = white ? Coordinates.WHITE_KING_ROOK : Coordinates.BLACK_KING_ROOK;
        Coordinate queenRook = white ? Coordinates.WHITE_QUEEN_ROOK : Coordinates.BLACK_QUEEN_ROOK;
        if (allPiecesTakenByPawns && (!this.detector.getStartLocations().containsKey(kingRook) || (
                this.detector.getStartLocations().get(kingRook).isEmpty()
                        || (this.detector.getStartLocations().get(kingRook).size() == 1 &&
                        this.detector.getStartLocations().get(kingRook).containsKey(queenRook)))
        )) {
            rooks.add(Coordinates.NULL_COORDINATE);
        }
        else if (allPiecesTakenByPawns && (!this.detector.getStartLocations().containsKey(queenRook) || (
                this.detector.getStartLocations().get(queenRook).isEmpty()
                        || (this.detector.getStartLocations().get(queenRook).size() == 1 &&
                        this.detector.getStartLocations().get(queenRook).containsKey(kingRook)))
        ))  {
            rooks.add(Coordinates.NULL_COORDINATE);
        }
        boolean kk = (!this.detector.getStartLocations().containsKey(kingRook) || (
                this.detector.getStartLocations().get(kingRook).isEmpty()
                        || (
                        !this.detector.getStartLocations().get(kingRook).containsKey(kingRook))));
        boolean qq = (!this.detector.getStartLocations().containsKey(queenRook) || (
                this.detector.getStartLocations().get(queenRook).isEmpty()
                        || (
                        !this.detector.getStartLocations().get(queenRook).containsKey(queenRook))));
        boolean kingMoved = false;
        for (Coordinate c : rooks) {
            if (c.equals(queenRook) || c.equals(kingRook)) {
                continue;
            }
            if (kingMoved) {
                continue;
            }
            boolean king = c.equals(Coordinates.NULL_COORDINATE) || (this.detector.getStartLocations().containsKey(kingRook) && this.detector.getStartLocations().get(kingRook).containsKey(c));
            boolean queen = c.equals(Coordinates.NULL_COORDINATE) || (this.detector.getStartLocations().containsKey(queenRook) && this.detector.getStartLocations().get(queenRook).containsKey(c));

            if (qq && queen) {

                kingMoved = disturbsKing(board, rook, "r", queenRook, c, white);

            }
            else if (kk && king) {

                kingMoved = disturbsKing(board, rook, "r", kingRook, c, white);
            }

        }
        if (kingMoved) {
            this.detector.setKingMovement(white, true);
        }
    }

    private boolean disturbsKing(BoardInterface board, String pieceName,
                              String pieceCode, Coordinate start,
                              Coordinate target, boolean white) {

        if (white) {
            return !this.detector.getKingMovement(true) && this.pathFinderUtil.findPiecePath(board, pieceName, pieceCode, start, target, kingCollisionWhite).isEmpty();
        } else {
            return !this.detector.getKingMovement(false) && this.pathFinderUtil.findPiecePath(board, pieceName, pieceCode, start, target, kingCollisionBlack).isEmpty();
        }
    }

}
