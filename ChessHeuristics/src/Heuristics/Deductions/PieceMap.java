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

/**
 * The PieceMap is a Deductions which attempts to form Paths between squares on the 1st and 8th ranks and
 * non-pawn pieces of the corresponding colour. In the process of doing so, it determines which pieces and which
 * squares are caged, and which pieces are promoted, and whether the king has been displaced by rook and queen
 * movement.
 * <></>
 * For the purposes of the PieceMap, two Coordinates are considered to be able to be able to path to one another
 * if they can both either a. path to the other Coordinate, or b. path to a Coordinate(x, y) such that 2 < y < 7.
 * These Paths must also not be exclusive, as defined in the documentation of class Pathfinder, with any pawn Path.
 * <></>
 * The state is set to false if there is a piece on the board that cannot path to a corresponding origin or
 * promotion square, or if there are more promotions than available pawns.
 * <></>
 * PieceMap must only run deduce() after the pawns have been mapped, otherwise its results will not be
 * accurate.
 */
public class PieceMap extends AbstractDeduction{
    /**A Path containing standard rook coordinates for use in various methods */
    private final static Path ROOK_COORDS = Path.of(Coordinates.WHITE_QUEEN_ROOK,
            Coordinates.WHITE_KING_ROOK, Coordinates.BLACK_QUEEN_ROOK, Coordinates.BLACK_KING_ROOK);
    /**A Path predicate checking collision with the white king**/
    private static final Predicate<Path> kingCollisionWhite = path -> !path.getLast().equals(Coordinates.WHITE_KING);
    /**A Path predicate checking collision with the black king**/
    private static final Predicate<Path> kingCollisionBlack = path -> !path.getLast().equals(Coordinates.BLACK_KING);
    /**A string of the word "knight" to avoid repeated use of a literal String*/
    private static final String KNIGHT = "knight";
    /**A Map of Coordinates of start locations and whether they are caged*/
    private Map<Coordinate, Boolean> caged;
    /**A Map of Coordinates of promotion squares and a Path listing the Coordinates of pieces that
     * can path to and from that square**/
    private Map<Coordinate, Path> promotedPieceMap;
    /**A Map of Coordinates of start locations, and Maps of Coordinates of corresponding pieces that
     * can path to and from those start locations, and paths from those start locations to those pieces*/
    private Map<Coordinate, Map<Coordinate, Path>> pieceMap;
    /** Stores a reference to PathFinderUtil to avoid repeated instantiation */
    private PathfinderUtil pathFinderUtil;

    /**
     * Instantiates a PieceMap with the standard error message
     */
    public PieceMap() {
        super("Illegal piece positions.");
    }

    /**
     * Registers a StateDetector as described in the Deduction interface.
     * @param detector the StateDetector from which data will be drawn and put
     */
    @Override
    public void registerDetector(StateDetector detector) {
        super.registerDetector(detector);
        this.pathFinderUtil = new PathfinderUtil(detector);
        this.pieceMap = this.detector.getPieceData().getPiecePaths();
        this.caged = this.detector.getPieceData().getCaged();
        this.promotedPieceMap = this.detector.getPromotionData().getPromotedPieceMap();
    }

    /**
     * Finds and puts paths between pieces and their start locations, promoted pieces and their promotion squares,
     * determines if start locations are caged, and whether as a result kings have moved.
     *
     * @param board the board to be tested
     */
    @Override
    public void deduce(BoardInterface board) {
        Arrays.stream(new int[]{2, 5, 4, 3, 0, 7}).forEach(x -> {
            findFromOrigin(board, x, true, false);
            findFromOrigin(board, x, false, false);

            findFromOrigin(board, x, true, true);
            findFromOrigin(board, x, false, true);
            configureCastling(board);
        });
        configureCastlingPartTwo(board);

        // For each start location, have each piece associated with it attempt to path to that start
        pathFromOtherDirection(board, this.pieceMap);

        // Check if King and Rooks have been displaced
        kingMovementUpdate(board);
        kingDisplaced(board);

        Map<String, Path> pieces = new TreeMap<>();
        Map<String, Integer> pieceNumber = new TreeMap<>();
        makePieceNumberMap(board, pieces, pieceNumber);
        // Every piece for which there are more of the type associated with a start location than there are by default
        Map<String, Path> potentialPromotions = pieces.entrySet().stream()
                .filter((entry) -> entry.getValue().size() > pieceNumber.get(entry.getKey()))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

        // Find paths for potential promotions, then updat the PromotionNumbers with the data
        findPromotionPaths(board, potentialPromotions);
        potentialPromotions.forEach((key, value) -> {
            Map<Path, Integer> toPut = new HashMap<>();
            toPut.put(value, pieceNumber.get(key));
            this.detector.getPromotionData().getPromotionNumbers().put(key, toPut);
        });

        // Find certain promotions
        Path foundPieces = Path.of(this.pieceMap.values()
                .stream().map(Map::keySet).flatMap(Collection::stream).toList());
        Map<String, Path> certainPromotions = getCertainPromotions(board, foundPieces);
        findPromotionPaths(board, certainPromotions);
        // Update the PromotionData
        certainPromotions.forEach((key, value) -> {
            Map<Path, Integer> toPut = new HashMap<>();
            toPut.put(value, 0);
            if (!this.detector.getPromotionData().getPromotionNumbers().containsKey(key)) {
                this.detector.getPromotionData().getPromotionNumbers().put(key, toPut);
            } else {
                this.detector.getPromotionData().getPromotionNumbers().get(key).put(value, 0);
            }
        });
        // Map pieces to their promotion origins
        pathToEnd(board, certainPromotions, potentialPromotions, foundPieces);

        boolean certainPromotionCheck = allPiecesAccountedFor(board);
        if (!certainPromotionCheck){
            this.errorMessage =
                    "There are pieces cannot that reach their current location from an origin or a promotion square.";
            this.state = false;
            return;
        }
        // There are not more promotions than available missing pawns
        int numberOfPotentialPromotions = potentialPromotions.size();
        int accountedPromotions = potentialPromotions.entrySet().stream().filter(entry -> {
            Path promotedPieces = entry.getValue();
            if (promotedPieces != null) {
                int promotions = this.detector.getPromotionData()
                        .getPromotionNumbers()
                        .get(entry.getKey()).get(promotedPieces);
                int potentiallyPromoted = 0;
                for (Coordinate coordinate : promotedPieces) {
                    // TODO what does this do?
                    this.promotedPieceMap
                            .values().stream().flatMap(Path::stream).anyMatch(c -> c.equals(coordinate));
                    potentiallyPromoted++;
                }
                return potentiallyPromoted >= promotedPieces.size() - promotions;
            }
            return false;})
                .toList()
                .size();
        if (accountedPromotions != numberOfPotentialPromotions){
            this.errorMessage = "Too many promotions.";
            this.state = false;
        }
    }

    /**
     * Checks that every piece on the board is loaded onto the PromotionData or the PieceData
     * @param board the board to check
     * @return whether every piece is accounted for as an original piece or a promoted piece
     */
    private boolean allPiecesAccountedFor(BoardInterface board) {
        String pawn = "pawn";
        List<Coordinate> allPieces = new ArrayList<>(board.getBoardFacts().getAllCoordinates("white").entrySet().stream()
                .filter(entry -> !entry.getKey().equals(pawn))
                .filter(entry -> !entry.getKey().equals(KNIGHT))
                .flatMap(entry -> entry.getValue().stream()).toList());
        allPieces.addAll(board.getBoardFacts().getAllCoordinates("black").entrySet().stream()
                .filter(entry -> !entry.getKey().equals(pawn))
                .filter(entry -> !entry.getKey().equals(KNIGHT))
                .flatMap(entry -> entry.getValue().stream()).toList());
        List<Coordinate> accountedPieces = new ArrayList<>(this.detector.getPromotionData()
                .getPromotedPieceMap().values().stream().flatMap(Path::stream).toList());
        accountedPieces.addAll(this.detector.getPieceData()
                .getPiecePaths().values().stream().map(Map::keySet).flatMap(Set::stream).toList());
        return new HashSet<>(accountedPieces).containsAll(allPieces);
    }


    /**
     * Forms a Path to the final 1st or 8th ranks from promoted pieces, removing those Coordinates from the
     * given promotion Maps that cannot path there
     * @param board the board to check
     * @param certainPromotions the certain promotion Map
     * @param potentialPromotions the potential promotion Map
     * @param foundPieces the pieces found on the board
     */
    private void pathToEnd(BoardInterface board, Map<String, Path> certainPromotions,
                           Map<String, Path> potentialPromotions, Path foundPieces) {
        Path certainPromotionsPath = Path.of(certainPromotions.values().stream()
                .filter(value -> !(value == null))
                .flatMap(Collection::stream).toList());
        this.promotedPieceMap.entrySet().forEach(entry -> {
            Coordinate key = entry.getKey();
            Path coordinatesToRemove = new Path();
            entry.getValue().forEach(origin -> {
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
                String pieceCode = key.getY() == 0
                        ? pieceName.substring(0, 1).toUpperCase()
                        : pieceName.substring(0, 1).toLowerCase();
                if (this.pathFinderUtil.findPiecePath(board, pieceName, pieceCode, origin, key).isEmpty()){
                    coordinatesToRemove.add(origin);
                }
            });
            coordinatesToRemove.forEach(coordinate -> entry.getValue().remove(coordinate));
        });
    }

    /**
     * Return a String/Path Map of certain promotions. The String is the piece's type plus an additional
     * character indicating if it's white or black. If it is a bishop, the second-to-last character will
     * indicate if it is light- or dark- square. The Path contains the Coordinates of all certainly promoted
     * pieces of that type.
     * @param board the board to be checked
     * @param foundPieces pieces on the board
     * @return a Map of certain promotions
     */
    private Map<String, Path> getCertainPromotions(BoardInterface board, Path foundPieces) {
        Map<String, Path> certainPromotions = new TreeMap<>();
        getCertainPromotionsHelper(board, true, certainPromotions, foundPieces);
        getCertainPromotionsHelper(board, false, certainPromotions, foundPieces);
        return certainPromotions;
    }
    /**
     * A helper method for getCertainPromotion(), populating the given Map according to the specifications
     * in the documentation of getCertainPromotion().
     * @param board the board to be checked
     * @param white the colour of the current player being checked
     * @param certainPromotions the Map to be populated
     * @param foundPieces pieces found on the board
     */
    private void getCertainPromotionsHelper(BoardInterface board, boolean white,
                                      Map<String, Path> certainPromotions, Path foundPieces) {
        Arrays.stream(new int[]{Q_ROOK_X, Q_BISHOP_X, QUEEN_X}).forEach(x -> {
            String pieceName = STANDARD_STARTS.get(x);
            int y = white ? FIRST_RANK_Y : FINAL_RANK_Y;
            boolean bishopAddition = pieceName.charAt(0) == 'b';
            Path promotions = Path.of(board.getBoardFacts().getCoordinates(white, pieceName)
                    .stream()
                    .filter(coordinate -> !foundPieces.contains(coordinate))
                    .toList());
            promotions.forEach(c -> {
                String name = pieceName +
                        (bishopAddition ? ((c.getX() + c.getY()) % 2 == 0 ? "d" : "l") : "") +
                        (white ? "w" : "b");
                if (!certainPromotions.containsKey(name)) {
                    certainPromotions.put(name, new Path());
                }
                certainPromotions.get(name).add(c);

            });
        });
    }

    /**
     * Populates the given String/Path Map and the given String/Integer Map with pieces on the given board
     * that have potentially been promoted. The String/Path map will contain the names of piece types + a character
     * 'w' or 'b' describing their colour and a Path containing the Coordinates of pieces of that type.
     * The String/Integer Map will contain the same, but with a number representing the quantity of the piece that there
     * is.
     * @param board the board to be checked
     * @param pieces the String/Path Map to be populated
     * @param pieceNumber the String/Integer Map to be populated
     */
    private void makePieceNumberMap(BoardInterface board, Map<String, Path> pieces, Map<String, Integer> pieceNumber) {
        for (int y  = 0 ; y < 8 ; y = y + 7) {
            for (int x = 0; x < 8; x++) {
                Coordinate origin = new Coordinate(x, y);
                if (x == Q_KNIGHT_X || x == K_KNIGHT_X) {
                    continue;
                }
                if (!this.caged.get(origin)) {
                    Set<Coordinate> pieceLocations = this.detector.getPieceData()
                            .getPiecePaths().get(origin).keySet();
                    String name = STANDARD_STARTS.get(x);
                    if (name.equals("bishop")) {
                        name = name +
                                ((x + y) % 2 == 0 ? "d" : "l");
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

        String cKnight = "knightw";
        piecesTwo.put(cKnight, board.getBoardFacts().getCoordinates(true, KNIGHT));
        pieceNumber.put(cKnight, 2);
        cKnight = "knightb";
        piecesTwo.put(cKnight, board.getBoardFacts().getCoordinates(false, KNIGHT));
        pieceNumber.put(cKnight, 2);
        pieces.clear();
        pieces.putAll(piecesTwo);
    }

    /**
     * Marks the kings and rooks as caged if they're not permitted to move due to castling restrictions.
     * @param board the board to check
     */
    private void configureCastling(BoardInterface board) {
        if (board.canKingMove(true)) {
            this.caged.put(Coordinates.WHITE_KING, true);
        }
        if (board.canKingMove(false)) {
            this.caged.put(Coordinates.BLACK_KING, true);
        }
        ROOK_COORDS.forEach((r) -> {
            if (board.canMove(r.getY() == FIRST_RANK_Y, r.getX() == Q_ROOK_X)) {
                this.caged.put(r, true);
            }
        });
    }

    /**
     * Updates the castling so that rooks pathing to one another are not accounted for
     * @param board the board to check
     */
    private void configureCastlingPartTwo(BoardInterface board) {

        ROOK_COORDS.forEach((r) -> {
            if (board.canMove(r.getY() == FIRST_RANK_Y, r.getX() == Q_ROOK_X)) {
                this.pieceMap.get(r).keySet()
                        .stream()
                        .filter(c -> !c.equals(r))
                        .toList()
                        .forEach(c -> this.pieceMap.get(r).remove(c));
                this.pieceMap.get(new Coordinate(Math.abs(r.getX() - K_ROOK_X), r.getY())).keySet().remove(r);
            }
        });
    }

    /**
     * Sets it so that rooks that have moved are marked as moved, and kings whose rooks have both moved are
     * marked as moved.
     * @param board the board to check
     */
    private void kingMovementUpdate(BoardInterface board){
        String rook = "rook";
        ROOK_COORDS.stream().forEach(r -> {
            if (!board.getBoardFacts().getCoordinates(r.getY() == FIRST_RANK_Y, rook)
                    .contains(r)) {
                this.detector.getPieceData().setRookMovement(r.getY() == FIRST_RANK_Y, r.getX() == Q_ROOK_X,
                        true);
            }
        });
        if (this.detector.getPieceData().getRookMovement(true, false)
                && this.detector.getPieceData().getRookMovement(true, true)) {
            this.detector.getPieceData().setKingMovement(true, true);
        }
        if (this.detector.getPieceData().getRookMovement(false, false)
                && this.detector.getPieceData().getRookMovement(false, true)) {
            this.detector.getPieceData().setKingMovement(false, true);
        }

    }

    /**
     * Checks if a king must have been disaplced by the movement of a rook or queen
     * @param board the board to be checked
     */
    private void kingDisplaced(BoardInterface board) {
        // Does not account for the possibility that these pieces are promoted
        int minnimumCaptures = this.detector.getPawnData().minimumPawnCaptures(false);

        boolean allPiecesTakenByPawnsW = minnimumCaptures != 0 && minnimumCaptures ==
                this.detector.getCaptureData().pawnTakeablePieces(true)
                        - board.getBoardFacts().pieceNumbers(true);
        minnimumCaptures = this.detector.getPawnData().minimumPawnCaptures(true);
        boolean allPiecesTakenByPawnsB = minnimumCaptures != 0 && minnimumCaptures ==
                this.detector.getCaptureData().pawnTakeablePieces(false)
                        - board.getBoardFacts().pieceNumbers(false);

        // Check queen
        Coordinate currentQueen = new Coordinate(QUEEN_X, FIRST_RANK_Y);
        boolean white = true;
        boolean currentAllPieces = allPiecesTakenByPawnsW;
        for (int i = 0 ; i < 2 ; i++) {
            if ((white && !this.detector.getPieceData().getKingMovement(true))
                    || (!white && !this.detector.getPieceData().getKingMovement(false))) {
                Coordinate finalCurrentQueen = currentQueen;
                boolean finalWhite = white;
                if (this.pieceMap.containsKey(currentQueen) && ((this.pieceMap.get(currentQueen)
                        .keySet().stream().anyMatch(c -> disturbsKing(board, "queen", PIECE_CODES.get("queen"), finalCurrentQueen, c, finalWhite)))
                        || (this.pieceMap.get(currentQueen).isEmpty() && currentAllPieces && disturbsKing(board, "queen", PIECE_CODES.get("queen"), finalCurrentQueen, Coordinates.NULL_COORDINATE, white)))
                ) {
                    this.detector.getPieceData().setKingMovement(white, true);
                }
            }
            currentQueen = new Coordinate(QUEEN_X, FINAL_RANK_Y);
            white = false;
            currentAllPieces = allPiecesTakenByPawnsB;
        }
        // Check rooks
        if (!this.detector.getPieceData().getKingMovement(true)) {
            rookKingMovementUpdate(board, true, allPiecesTakenByPawnsW);
        }
        if (!this.detector.getPieceData().getKingMovement(false)) {
            rookKingMovementUpdate(board, false, allPiecesTakenByPawnsB);
        }
    }

    /**
     * Checks for each of the given set of piece-Paths from origins that that piece is capable of pathing back to it.
     * @param board the board to be checked
     * @param paths the Coordinate/(Coordinate/Path)Map Map of piece locations, origins, and paths from that origin
     */
    private void pathFromOtherDirection(BoardInterface board, Map<Coordinate, Map<Coordinate, Path>> paths) {
        paths.entrySet().forEach(entry -> {
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
            coordinatesToRemove.forEach(coordinate -> entry.getValue().remove(coordinate));
        });
    }

    /**
     * Updates the given String/Path Map, removing those Coordinates on the Paths that cannot path to a promotion
     * square
     * @param board the board being checked
     * @param potentialPromotions the String/Path Map of piece names and Coordinates of potentially promoted pieces
     */
    private void findPromotionPaths(BoardInterface board, Map<String, Path> potentialPromotions) {
        Map<String, Path> updatedPromotions = new TreeMap<>();
        this.promotedPieceMap.forEach((origin, value) ->
                potentialPromotions.forEach((key, value1) -> {
                    int y = origin.getY();
            if ((y == FIRST_RANK_Y && key.charAt(key.length() - 1) == 'w') ||
                    (y == FINAL_RANK_Y && key.charAt(key.length() - 1) == 'b')) {
                return;
            }
            String pieceName = key.substring(0, key.length() -
                    (key.charAt(0) == 'b' ? 2 : 1));
            String pieceCodeTemp = PIECE_CODES.get(pieceName);
            String pieceCode = key.charAt(key.length() - 1) == 'w'
                    ? pieceCodeTemp.toUpperCase() :
                    pieceCodeTemp.toLowerCase();
            value1.stream()
                    .filter(coordinate -> {
                        if (pieceCode.equalsIgnoreCase("b")
                                && Coordinates.light(origin) != Coordinates.light(coordinate)) {
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
                        if (!updatedPromotions.containsKey(key)) {
                            updatedPromotions.put(key, Path.of());
                        }
                        if (!updatedPromotions.get(key).contains(coordinate)) {
                            updatedPromotions.get(key).add(coordinate);
                        }
                        this.promotedPieceMap.get(origin).add(coordinate);
                    });
        }));
        potentialPromotions.keySet().forEach(name -> potentialPromotions.put(name, updatedPromotions.get(name)));
    }

    /**
     * Forms paths from an origin to pieces of the appropriate type, or determines if that origin is caged
     * @param board the board to be checked
     * @param originX the x coordinate of the origin
     * @param white the player's colour, true if white, false if black
     * @param cage whether the method is checking for cages, true if so, false if otherwise
     */
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
            if (!(this.caged.containsKey(start) &&
                    this.caged.get(start))) {
                this.caged
                        .put(start,this.pathFinderUtil
                                .findPiecePath(board, pieceName, pieceCode, start, Coordinates.NULL_COORDINATE)
                                .isEmpty());
            }
        } else {
            for (Coordinate target : candidatePieces) {
                if (originX == Coordinates.WHITE_KING.getX() && pieceName.equals("king")
                        && !target.equals(white ? Coordinates.WHITE_KING : Coordinates.BLACK_KING)) {
                    this.detector.getPieceData().setKingMovement(white, true);
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
            this.pieceMap.put(start, candidatePaths);
        }

    }

    /**
     * Checks if a king has been displaced by rooks' movement
     * @param board the board to be checked
     * @param white the player's colour, true if white, false if black
     * @param allPiecesTakenByPawns whether all pieces that have been taken have been taken by pawns
     */
    private void rookKingMovementUpdate(BoardInterface board, boolean white, boolean allPiecesTakenByPawns) {
        String rook = "rook";
        Path rooks = board.getBoardFacts().getCoordinates(white, rook);
        Coordinate kingRook = white ? Coordinates.WHITE_KING_ROOK : Coordinates.BLACK_KING_ROOK;
        Coordinate queenRook = white ? Coordinates.WHITE_QUEEN_ROOK : Coordinates.BLACK_QUEEN_ROOK;
        if (allPiecesTakenByPawns && (!this.pieceMap.containsKey(kingRook) || (
                this.pieceMap.get(kingRook).isEmpty()
                        || (this.pieceMap.get(kingRook).size() == 1 &&
                        this.pieceMap.get(kingRook).containsKey(queenRook)))
        )) {
            rooks.add(Coordinates.NULL_COORDINATE);
        }
        else if (allPiecesTakenByPawns && (!this.pieceMap.containsKey(queenRook) || (
                this.pieceMap.get(queenRook).isEmpty()
                        || (this.pieceMap.get(queenRook).size() == 1 &&
                        this.pieceMap.get(queenRook).containsKey(kingRook)))
        ))  {
            rooks.add(Coordinates.NULL_COORDINATE);
        }
        boolean kk = (!this.pieceMap.containsKey(kingRook) || (
                this.pieceMap.get(kingRook).isEmpty()
                        || (
                        !this.pieceMap.get(kingRook).containsKey(kingRook))));
        boolean qq = (!this.pieceMap.containsKey(queenRook) || (
                this.pieceMap.get(queenRook).isEmpty()
                        || (
                        !this.pieceMap.get(queenRook).containsKey(queenRook))));
        boolean kingMoved = false;
        for (Coordinate c : rooks) {
            if (c.equals(queenRook) || c.equals(kingRook)) {
                continue;
            }
            if (kingMoved) {
                continue;
            }
            boolean king = c.equals(Coordinates.NULL_COORDINATE)
                    || (this.pieceMap.containsKey(kingRook)
                    && this.pieceMap.get(kingRook).containsKey(c));
            boolean queen = c.equals(Coordinates.NULL_COORDINATE)
                    || (this.pieceMap.containsKey(queenRook)
                    && this.pieceMap.get(queenRook).containsKey(c));

            if (qq && queen) {
                kingMoved = disturbsKing(board, rook, "r", queenRook, c, white);
            }
            else if (kk && king) {
                kingMoved = disturbsKing(board, rook, "r", kingRook, c, white);
            }
        }
        if (kingMoved) {
            this.detector.getPieceData().setKingMovement(white, true);
        }
    }

    private boolean disturbsKing(BoardInterface board, String pieceName,
                              String pieceCode, Coordinate start,
                              Coordinate target, boolean white) {
        return !this.detector.getPieceData().getKingMovement(white)
                && this.pathFinderUtil.findPiecePath(board, pieceName, pieceCode, start, target,
                white ? kingCollisionWhite : kingCollisionBlack).isEmpty();
    }

}
