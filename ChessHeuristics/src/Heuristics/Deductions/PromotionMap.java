package Heuristics.Deductions;

import Heuristics.BoardInterface;
import Heuristics.Detector.StateDetectorFactory;
import Heuristics.Path;
import Heuristics.Detector.StateDetector;
import StandardChess.Coordinate;
import StandardChess.Coordinates;

import java.util.*;
import java.util.stream.Collectors;

import static Heuristics.HeuristicsUtil.*;

/**
 * The PromotionMap looks at every promoted piece on the board and determines whether there is a valid set of
 * pawn origins, promotion squares, and promoted pieces for each one, then adds these to the existing pawn
 * paths stored in the PawnData.
 * <></>
 * The state will be set to false if any promoted piece does not have a valid Path from a pawn origin
 * to a promotion square, then to the piece, without exceeding the maximum number of captures that may take place.
 * <></>
 * PromotionMap must only be run after the pawns and pieces have been mapped, promoted pieces determined and linked
 * to promotion squares, and the maximum number of captures possible by pawns determined, otherwise it will not be
 * accurate.
 */
public class PromotionMap extends AbstractDeduction {
    /**An arbitrarily high number for reducing claims*/
    private final static int HIGH_NUMBER = 99;
    /**An instance of PathFinderUtil*/
    private PathfinderUtil pathFinderUtil = new PathfinderUtil(detector);
    /**A List of Maps of Paths of Coordinates of set of promoted pieces and Paths of origins the may have come from*/
    private final List<Map<Path, Path>> claimsWhite = new LinkedList<>();
    /**A List of Maps of Paths of Coordinates of set of promoted pieces and Paths of origins the may have come from*/
    private final List<Map<Path, Path>> claimsBlack = new LinkedList<>();
    /**A Path of all potential pawn origins of promoted pieces*/
    private Path origins;
    /**A Path of all potential promotion squares of promoted pieces*/
    private Path targets;
    /**A Map of all Coordinates of promotion squares, and maps of Coordinates of pawn origins, and the number of
     * captures required to reach them*/
    private final Map<Coordinate, Map<Coordinate, Integer>> goalOrigins = new HashMap<>();

    /**
     * A constructor setting the error message to
     * "A promoted piece cannot reach its current location from an available pawn start."
     */
    public PromotionMap() {
        super("A promoted piece cannot reach its current location from an available pawn start.");
    }

    /**
     * Registers a StateDetector as specified in the documentation of interface Deduction and stores a reference to
     * an instance of a PathFinderUtil.
     * @param detector the StateDetector to be registered
     */
    @Override
    public void registerDetector(StateDetector detector) {
        super.registerDetector(detector);
        this.pathFinderUtil = new PathfinderUtil(detector);
    }

    /**
     * For every previously discovered promoted piece, deduces if it was possible for a missing pawn to have promoted
     * and then reached the location of the promoted piece, setting the state to false if not.
     * @param board the board whose information the deduction will draw from
     */
    @Override
    public void deduce(BoardInterface board) {
        if (this.detector.getPromotionData().getPromotedPieceMap().values().stream()
                .allMatch(AbstractCollection::isEmpty)) {
            this.state = true;
            return;
        }
        setOriginAndTargets();
        // Generate map of promotion squares and pawn eligible pawn origins
        setGoalOrigins(board);

        Map<Path, List<Path>> pieceSquareOriginWhite = new HashMap<>(mapPieceSquareOrigin(board, true));
        Map<Path, List<Path>> pieceSquareOriginBlack = new HashMap<>(mapPieceSquareOrigin(board, false));
        // Fail if a piece has no valid origin
        if (pieceSquareOriginWhite.containsValue(List.of()) || pieceSquareOriginBlack.containsValue(List.of())) {
            this.state = false;
            return;
        }
        // Fail if a set of pieces does not have enough valid origins
        // Note - this check might include the above check naturally
        if (!checkPromotionNumbers(pieceSquareOriginWhite, pieceSquareOriginBlack)) {
            this.state = false;
            return;
        }
        // The number of promotions each player has made
        int promotionsWhite = this.detector.getPromotionData().getPromotionNumbers().values().stream()
                .flatMap(map -> map.entrySet().stream())
                .filter(entry -> pieceSquareOriginWhite.containsKey(entry.getKey()))
                .filter(entry -> entry.getKey() != null)
                .map(entry -> entry.getKey().size() - entry.getValue())
                .reduce(Integer::sum)
                .orElse(0);
        int promotionsBlack = this.detector.getPromotionData().getPromotionNumbers().values().stream()
                .flatMap(map -> map.entrySet().stream())
                .filter(entry -> pieceSquareOriginBlack.containsKey(entry.getKey()))
                .filter(entry -> entry.getKey() != null)
                .map(entry -> entry.getKey().size() - entry.getValue())
                .reduce(Integer::sum)
                .orElse(0);
        this.origins = Path.of(this.goalOrigins.values()
                .stream()
                .flatMap(map -> map.keySet().stream())
                .collect(Collectors.toSet()));
        this.targets = Path.of(this.goalOrigins.keySet());
        // Fail if there are more promotions than valid origins
        if (promotionsWhite >  this.origins.stream().filter(coordinate -> coordinate.getY() == 1).toList().size()
                || promotionsBlack > this.origins.stream().filter(coordinate -> coordinate.getY() == 6).toList().size()) {
            this.state = false;
            return;
        }
        // Get a valid piece / origin set
        Map<Path, Integer> pathIntegerMap = this.detector.getPromotionData().getPromotionNumbers().values().stream()
                .flatMap(map -> map.entrySet().stream())
                .filter(entry -> entry.getKey() != null)
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
        Map<Coordinate, Path> pieceOriginWhite = pieceOrigin(pieceSquareOriginWhite, pathIntegerMap);
        Map<Coordinate, Path> pieceOriginBlack = pieceOrigin(pieceSquareOriginBlack, pathIntegerMap);

        // Fail if after reduction any piece does not have enough origins
        if (!(checkReductionOfPieceOrigins(true, board, pieceOriginWhite)
                && checkReductionOfPieceOrigins(false, board, pieceOriginBlack))) {
            this.state = false;
            return;
        }

        // Attempt to generate a valid combination of pieces, squares, and origins
        beginOriginClaim(pieceSquareOriginWhite, true, pathIntegerMap, board);
        beginOriginClaim(pieceSquareOriginBlack, false, pathIntegerMap, board);
        reduceClaims(true, pieceSquareOriginWhite, board);
        reduceClaims(false, pieceSquareOriginBlack, board);
        if ((this.claimsBlack.isEmpty() && promotionsBlack != 0) || (this.claimsWhite.isEmpty() && promotionsWhite != 0)) {
            this.state = false;
            return;
        }
        this.state = false;
        stateIterateStart(board);
    }

    private void beginOriginClaim(Map<Path, List<Path>> pieceSquareOrigin,
                                     boolean white, Map<Path, Integer> pathIntegerMap, BoardInterface board) {
        Path originPool = Path.of(this.origins
                .stream().filter(c -> c.getY() ==(white ? WHITE_PAWN_Y : BLACK_PAWN_Y)).toList());
        Map<Path, Path> claims = new HashMap<>();
        Map<Path, Path> pieceOrigin = pieceSquareOrigin.entrySet()
                .stream().collect(Collectors.toMap(Map.Entry::getKey, entry ->
                        Path.of(entry.getValue().stream()
                                .map(Path::getLast).collect(Collectors.toSet()).stream().toList())));
        originClaimIteratorHelperStarter(pathIntegerMap, originPool, pieceOrigin, claims, white);
    }

    private void setOriginAndTargets() {
        this.origins = Path.of(this.detector.getPawnData().getOriginFree(true).entrySet()
                .stream().filter(Map.Entry::getValue)
                .map(Map.Entry::getKey)
                .toList());
        this.origins.addAll(this.detector.getPawnData().getOriginFree(false).entrySet()
                .stream().filter(Map.Entry::getValue)
                .map(Map.Entry::getKey)
                .toList());
        this.targets = Path.of(this.detector.getPromotionData().getPromotedPieceMap().entrySet()
                .stream().filter(entry -> !entry.getValue().isEmpty())
                .map(Map.Entry::getKey)
                .toList());
    }

    private Map<Coordinate, Path> pieceOrigin(Map<Path, List<Path>> pieceSquareOrigin, Map<Path, Integer> pathIntegerMap) {
        return pieceSquareOrigin.entrySet()
                .stream()
                .flatMap(entry -> {
                    Map<Coordinate, Path> pieceOrigin = new HashMap<>();
                    Path allOrigins = Path.of(entry.getValue()
                            .stream().map(LinkedList::getLast)
                            .collect(Collectors.toSet()));
                    for (int i = 0 ; i < (entry.getKey().size() - pathIntegerMap.get(entry.getKey())) ; i++) {
                        pieceOrigin.put(entry.getKey().get(i), allOrigins);
                    }
                    return pieceOrigin.entrySet().stream();
                })
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    private boolean checkReductionOfPieceOrigins(boolean white, BoardInterface board, Map<Coordinate, Path> pieceOrigin) {
        TheoreticalPawnMap tPMW = new TheoreticalPawnMap(true);
        tPMW.registerDetector(StateDetectorFactory.getDetector(board.getReader().toFEN()));
        tPMW.reduce(pieceOrigin);
        return tPMW.getState();
    }

    private boolean checkPromotionNumbers(Map<Path, List<Path>> pieceSquareOriginWhite,
                                          Map<Path, List<Path>> pieceSquareOriginBlack) {
        Map<Path, Integer> promotionNumbers = this.detector.getPromotionData().getPromotionNumbers().values()
                .stream()
                .flatMap(map -> map.entrySet().stream())
                .filter(entry -> entry.getKey() != null)
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
        return pieceSquareOriginWhite.entrySet().stream()
                .noneMatch(entry ->
                        (entry.getKey().size() - promotionNumbers.get(entry.getKey()) >
                                entry.getValue().stream()
                                        .map(LinkedList::getLast)
                                        .collect(Collectors.toSet())
                                        .size()))
                &&
                pieceSquareOriginBlack.entrySet().stream()
                        .noneMatch(entry ->
                                (entry.getKey().size() - promotionNumbers.get(entry.getKey()) >
                                        entry.getValue().stream()
                                                .map(LinkedList::getLast)
                                                .collect(Collectors.toSet())
                                                .size()));
    }

    private void stateIterateStart(BoardInterface board) {
        List<List<Map<Path, Path>>> claimList = new LinkedList<>();
        if (!this.claimsWhite.isEmpty()) {
            claimList.add(this.claimsWhite);
        }
        if (!this.claimsBlack.isEmpty()) {
            claimList.add(this.claimsBlack);
        }
        stateIterate(board, claimList, new LinkedList<>());
    }
    private void stateIterate(BoardInterface board, List<List<Map<Path, Path>>> claimList, List<Map<Path, Path>> originList) {
        List<Map<Path, Path>> claims = claimList.get(0);
        List<List<Map<Path, Path>>> newClaims = new LinkedList<>(claimList);

        newClaims.remove(claims);

        for (Map<Path, Path> map : claims) {
            List<Map<Path, Path>> newOrigins = new LinkedList<>(originList);
            newOrigins.add(map);
            if (!newClaims.isEmpty()) {
                stateIterate(board, newClaims, newOrigins);
            } else {
                if (stateUpdate(board, newOrigins)){
                    return;
                }
            }
        }
    }

    private boolean stateUpdate(BoardInterface board, List<Map<Path, Path>> originList) {
        if (!originList.isEmpty()) {
            this.origins = new Path();
        }
        originList.forEach(map -> this.origins.addAll(map.values().stream().flatMap(Path::stream).toList()));
        PromotionPawnMap promotionPawnMapWhite = new PromotionPawnMap(true);
        PromotionPawnMap promotionPawnMapBlack = new PromotionPawnMap(false);
        PromotionCombinedPawnMap combinedPawnMap = new PromotionCombinedPawnMap(promotionPawnMapWhite, promotionPawnMapBlack);
        StateDetector stateDetector = StateDetectorFactory.getDetector(board.getReader().toFEN(), combinedPawnMap);
        combinedPawnMap.registerDetector(stateDetector);
        stateDetector.testState(board);
        if (promotionPawnMapBlack.state && promotionPawnMapWhite.state && combinedPawnMap.state) {
            this.detector.getPawnData().getPawnPaths(true).putAll(stateDetector.getPawnData().getPawnPaths(true));
            this.detector.getPawnData().getPawnPaths(false).putAll(stateDetector.getPawnData().getPawnPaths(false));
            this.state = true;
            return true;
        }
        return false;
    }


    private void originClaimIteratorHelperStarter(Map<Path, Integer> pathIntegerMap, Path originPool, Map<Path, Path> pieceOrigin, Map<Path, Path> claims, boolean white) {
        pieceOrigin.keySet().forEach(p -> claims.put(p, new Path()));
        boolean called = false;
        do {
            called = false;
            for (Map.Entry<Path, Path> entry : pieceOrigin.entrySet()) {
                Map<Path, Path> pieceOriginNew = pieceOrigin.entrySet().stream().collect(Collectors.toMap(Map.Entry::getKey, e -> Path.of(e.getValue())));
                Map<Path, Path> claimsNew = claims.entrySet().stream().collect(Collectors.toMap(Map.Entry::getKey, e -> Path.of(e.getValue())));
                if (originClaimIteratorHelper(pathIntegerMap, Path.of(originPool), pieceOriginNew, claimsNew, entry.getKey(), white)) {
                    called = true;
                }
            }
        } while (called);
    }


    private boolean originClaimIteratorHelper(Map<Path, Integer> pathIntegerMap, Path originPool, Map<Path, Path> pieceOrigin, Map<Path, Path> claims, Path current, boolean white) {
        if (originPool.isEmpty()) {
            return false;
        }
        List<Coordinate> claim = pieceOrigin.get(current).stream().filter(originPool::contains).toList();
        if (claim.isEmpty()) {
            return false;
        }
        boolean claimed = false;
        ///
        for (Coordinate coordinate : claim) {
            Map<Path, Path> newClaims = claims.entrySet()
                    .stream().collect(Collectors.toMap(Map.Entry::getKey,
                            e -> Path.of(new HashSet<>(e.getValue()))));
            newClaims.get(current).add(coordinate);
            Path originPoolNew = Path.of(originPool);
            originPoolNew.remove(coordinate);
            if (pieceOrigin.entrySet().stream().allMatch(innerEntry -> {
                Path key = innerEntry.getKey();
                return (key.size() - pathIntegerMap.get(key)) - newClaims.get(key).size() == 0;
            })) {
                List<Map<Path, Path>> allClaims = white ? this.claimsWhite : this.claimsBlack;
                if (!allClaims.contains(newClaims)) {
                    allClaims.add(newClaims);
                    claimed = true;
                }
                continue;
            }
            for (Map.Entry<Path, Path> entry : pieceOrigin.entrySet()) {
                Map<Path, Path> pieceOriginWhiteTwoNew = pieceOrigin.entrySet()
                        .stream().collect(Collectors.toMap(Map.Entry::getKey, e -> Path.of(e.getValue())));
                originClaimIteratorHelper(pathIntegerMap,
                        originPoolNew,
                        pieceOriginWhiteTwoNew, newClaims, entry.getKey(), white);
            }
        }
        return claimed;
    }
    private void reduceClaims(boolean white, Map<Path, List<Path>> pieceSquareOrigin, BoardInterface board) {
        int maxCaptures = (this.detector.getCaptureData().pawnTakeablePieces(white)
                - board.getBoardFacts().pieceNumbers(!white))
                - this.detector.getPawnData().minimumPawnCaptures(white);
        List<Map<Path, Path>> allClaims = white ? this.claimsWhite : this.claimsBlack;
        List<Map<Path, Path>> toRemove = allClaims.stream().filter(map -> {
            int max = map.entrySet().stream().map(entry -> {
                        Path key = entry.getKey();
                        Path value = entry.getValue();
                        return value.stream().map(c ->
                                        pieceSquareOrigin.get(key)
                                                .stream().filter(p -> p.getLast().equals(c))
                                                .map(p -> this.goalOrigins.get(p.getFirst()).get(p.getLast()))
                                                .reduce((i, j) -> i < j ? i : j)
                                                .orElse(HIGH_NUMBER))
                                .reduce(Integer::sum)
                                .orElse(HIGH_NUMBER);
                    }).reduce(Integer::sum)
                    .orElse(HIGH_NUMBER);
            return max > maxCaptures;
        }).toList();
        allClaims.removeAll(toRemove);
    }

    /**
     * Creates a map of each set Coordinates of promoted pieces, and a List of every size 2 Path that
     * is a valid combination of a promotion square and a free pawn origin.
     * A combination is valid if there are enough available captures for a pawn to path from the origin to the
     * promotion square.
     * Entries with empty Lists as values have no valid promotion square/pawn origin combinations
     * @param white whether the player whose promoted pieces being checked is white
     * @return a Map of combinations of promoted piece sets, and Paths containing promotion squares in the first
     * position and pawn origins in the last position
     */
    private Map<Path, List<Path>> mapPieceSquareOrigin(BoardInterface board, boolean white) {
        int maxCaptures = (this.detector.getCaptureData().pawnTakeablePieces(white) - (board.getBoardFacts().pieceNumbers(!white))) - this.detector.getPawnData().minimumPawnCaptures(white);
        // reduce goal/Origin sets that contain too many captures
        this.goalOrigins.entrySet().stream()
                .filter(entry -> entry.getKey().getY() == (white ? FINAL_RANK_Y : FIRST_RANK_Y)) // Correct colour
                .filter(entry -> {
                    Path removals = new Path();
                    entry.getValue().forEach((key, value) -> {
                        if (value > maxCaptures) {
                            removals.add(key);
                        }
                    });
                    removals.forEach(c -> entry.getValue().remove(c));
                    return entry.getValue().isEmpty();
                }) // Too many captures
                .map(Map.Entry::getKey)
                .toList()
                .forEach(this.goalOrigins::remove);

        // Link promoted piece with origin
        // <promoted piece set, [promotion square, origin]>
        Map<Path, List<Path>> pieceSquareOrigin = new HashMap<>();
        this.detector.getPromotionData().getPromotionNumbers().entrySet().stream()
                .filter(entry -> entry.getKey().charAt(entry.getKey().length()-1) == (white ? 'w' : 'b'))
                .forEach(entry -> entry.getValue().keySet()
                        .stream()
                        .filter(Objects::nonNull)
                        .forEach(path -> pieceSquareOrigin.putIfAbsent(path, new LinkedList<>())));
        this.goalOrigins.forEach((key, value) -> {
            Path pieces = this.detector.getPromotionData().getPromotedPieceMap().get(key);
            pieceSquareOrigin.keySet().stream()
                    .filter(path -> pieces.contains(path.getFirst()))
                    .forEach(piece -> value.keySet()
                            .forEach(origin -> pieceSquareOrigin.get(piece).add(Path.of(key, origin))));
        });
        return pieceSquareOrigin;
    }

    private void setGoalOrigins(BoardInterface board) {
        List<Path> forbiddenWhitePaths = this.detector.getPawnData().getPawnPaths(true).values().stream()
                .filter(list -> list.size() == 1)
                .map(list -> list.get(0))
                .toList();
        List<Path> forbiddenBlackPaths = this.detector.getPawnData().getPawnPaths(false).values().stream()
                .filter(list -> list.size() == 1)
                .map(list -> list.get(0))
                .toList();

        List<Coordinate> whiteOrigins = this.origins.stream().filter(coordinate -> coordinate.getY() == 1).toList();
        List<Coordinate> blackOrigins = this.origins.stream().filter(coordinate -> coordinate.getY() == 6).toList();
        List<Coordinate> whiteTargets = this.targets.stream().filter(coordinate -> coordinate.getY() == 7).toList();
        List<Coordinate> blackTargets = this.targets.stream().filter(coordinate -> coordinate.getY() == 0).toList();
        whiteTargets.forEach(coordinate -> whiteOrigins.forEach(coordinate1 -> setGoalOrigins(
                coordinate, board, forbiddenWhitePaths, forbiddenBlackPaths, coordinate1
        )));
        blackTargets.forEach(coordinate -> blackOrigins.forEach(coordinate1 -> setGoalOrigins(
                coordinate, board, forbiddenWhitePaths, forbiddenBlackPaths, coordinate1
        )));
    }

    private void setGoalOrigins(Coordinate origin,
                                BoardInterface board,
                                List<Path> forbiddenWhitePaths, List<Path> forbiddenBlackPaths,
                                Coordinate target) {
        Path shortest = new PathfinderUtil(this.detector).findShortestPawnPath(
                board, origin, 0, (b, c) -> target.equals(c),
                origin.getY() == 1 || origin.getY() == 0, false, origin.getY() == 1 || origin.getY() == 7
                        ? forbiddenBlackPaths
                        : forbiddenWhitePaths);

        if (!shortest.isEmpty()) {
            boolean white = origin.getY() == FINAL_RANK_Y;
            Path cagedCaptures = Path.of(this.detector.getCaptureData().getNonPawnCaptures(white).stream().filter(c -> c.getX() == K_ROOK_X || c.getX() == Q_ROOK_X).toList());
            if (cagedCaptures.size() > 0 && (white || origin.getY() == 0)) {
                int captureY = white ? BLACK_ESCAPE_Y : WHITE_ESCAPE_Y;
                int change = white ? 1 : -1;
                pathCagedCaptures(captureY, change, shortest, cagedCaptures, board, white);
            }
            this.goalOrigins.putIfAbsent(origin, new HashMap<>());
            this.goalOrigins.get(origin).put(target, PathfinderUtil.PATH_DEVIATION.apply(shortest));
        }

    }

    private void pathCagedCaptures(int y, int change, Path path, Path cagedCaptures, BoardInterface board, boolean white) {
        Coordinate c1 = path.stream().filter(c -> c.getY() == y).findAny().orElse(Coordinates.NULL_COORDINATE);
        Coordinate c2 = path.stream().filter(c -> c.getY() == y + change).findAny().orElse(Coordinates.NULL_COORDINATE);
        Coordinate c3 = path.stream().filter(c -> c.getY() == y + change * 2).findAny().orElse(Coordinates.NULL_COORDINATE);
        Path forChecking = new Path();
        if (c1.getX() != c2.getX()) {
            if (!c1.equals(Coordinates.NULL_COORDINATE) && !c2.equals(Coordinates.NULL_COORDINATE)) {
                forChecking.add(c2);
            }
        }
        if (c2.getX() != c3.getX()) {
            if (!c2.equals(Coordinates.NULL_COORDINATE) && !c3.equals(Coordinates.NULL_COORDINATE)) {
                forChecking.add(c3);
            }
        }

        for (Coordinate checkedCoord : forChecking) {
            Path forRemoval = new Path();
            for (Coordinate rookLocation : cagedCaptures) {
                if (!this.pathFinderUtil.findPiecePath(board, "rook", white ? "r" : "R", rookLocation, checkedCoord).isEmpty()) {
                    forRemoval.add(rookLocation);
                    break;
                }

            }
            if (!forRemoval.isEmpty()) {
                this.detector.getCaptureData().getNonPawnCaptures(white).removeAll(forRemoval);
                forRemoval.forEach(cagedCaptures::remove);
            }
        }
    }

    private class PromotionCombinedPawnMap extends CombinedPawnMap {
        public PromotionCombinedPawnMap(PromotionPawnMap white, PromotionPawnMap black) {
            super();
            this.whitePawnMap = white;
            this.blackPawnMap = black;
        }
        @Override
        public void deduce(BoardInterface boardInterface){
            super.deduce(boardInterface);
        }
    }

    private class PromotionPawnMap extends CombinedPawnMap.PawnMap {
        public PromotionPawnMap(boolean white) {
            super(white);
        }
        @Override
        public void deduce(BoardInterface board) {
//            if (this.maxPieces == MAX_PIECES) {
            this.detector.getCaptureData().getNonPawnCaptures(getColour())
                    .addAll(PromotionMap.this.detector.getCaptureData().getNonPawnCaptures(getColour()));
//            }
            super.deduce(board);
        }

        /**
         * Flips the theoretical pawns in the pawn / origin map, deleting those without an origin
         */
        private void flip(boolean direction, boolean white) {
            Map<Coordinate, Path> newOrigins = new HashMap<>();
            Path remove = new Path();
            getPawnOrigins().entrySet().stream()
                    .filter(entry -> {
                        Path value = entry.getValue();
                        if (value.isEmpty()) {
                            return true;
                        }
                        return (direction ? value.getFirst().getY() : entry.getKey().getY())
                                == (white ? FINAL_RANK_Y : FIRST_RANK_Y);})
                    .forEach(entry -> {
                        Path value = entry.getValue();
                        Coordinate key = entry.getKey();
                        if (value.isEmpty()) {
                            remove.add(key);
                        } else {
                            value.forEach(coordinate -> {
                                if (!newOrigins.containsKey(coordinate)) {
                                    newOrigins.put(coordinate, new Path());
                                }
                                newOrigins.get(coordinate).add(key);
                            });
                            remove.add(key);
                        }
                    });
            remove.forEach(coordinate -> getPawnOrigins().remove(coordinate));
            getPawnOrigins().putAll(newOrigins);
        }
        @Override
        protected void rawMap(BoardInterface board) {
            Map<Coordinate, Path> toAddOne = PromotionMap.this.detector.getPawnData().getPawnPaths(getColour()).entrySet().stream()
                    .collect(Collectors.toMap(Map.Entry::getKey, e -> Path.of(e.getValue().stream().map(LinkedList::getFirst).toList())));
            toAddOne.forEach((key, value) -> getPawnOrigins().put(key, Path.of(value)));
            Map<Coordinate, Path> toAddTwo = new HashMap<>();
            int y = (getColour() ? FINAL_RANK_Y : FIRST_RANK_Y);
            Path targetsFiltered = Path.of(origins.stream().filter(coordinate ->
                    coordinate.getY() == Math.abs(y - (BLACK_PAWN_Y))).toList());
            targets.stream().filter(coordinate -> coordinate.getY() == y)
                    .forEach(c -> toAddTwo.put(c, Path.of(targetsFiltered.stream()
                            .filter(cTwo -> Math.abs(c.getX() - cTwo.getX()) < K_ROOK_X).toList())));
            toAddTwo.entrySet().stream().filter(entry -> !entry.getValue().isEmpty())
                    .forEach(entry -> getPawnOrigins().put(entry.getKey(), entry.getValue()));
        }

        @Override
        protected void captures(Map<Coordinate, Path> origins) {
            flip(false, getColour());
            super.captures(origins);
            flip(true, getColour());
            Map<Coordinate, Integer> newCaptures = new HashMap<>();
            getPawnOrigins().entrySet().stream()
                    .filter(entry -> entry.getKey().getY() == (getColour() ? FINAL_RANK_Y : FIRST_RANK_Y))
                    .forEach(entry -> {
                        Coordinate entryKey = entry.getKey();
                        entry.getValue().forEach(coordinate -> {
                            int difference = Math.abs(entryKey.getX() - coordinate.getX());
                            if (newCaptures.containsKey(entryKey)) {
                                if (newCaptures.get(entryKey) <
                                        difference) {
                                    newCaptures.put(entryKey, difference);
                                }
                            }
                            newCaptures.put(entryKey, difference);
                        });

                    });
            getCaptureSet().putAll(newCaptures);
        }

        @Override
        protected boolean reduceIter(Set<Coordinate> set, List<Coordinate> origins) {
            flip(false, getColour());
            boolean reduction = super.reduceIter(set, origins);
            flip(true, getColour());
            return reduction;
        }
    }

    private class TheoreticalPawnMap extends CombinedPawnMap.PawnMap {
        public TheoreticalPawnMap(boolean white) {
            super(white);
//            this.maxPieces = PromotionMap.this.detector.getCaptureData().pawnTakeablePieces(white);
        }
        private void reduce(Map<Coordinate, Path> pawnOrigins) {
            getPawnOrigins().clear();
            getPawnOrigins().putAll(pawnOrigins);
            List<Coordinate> origins = getPawnOrigins().entrySet().stream()
                    .flatMap(f -> f.getValue().stream())
                    .collect(Collectors.toSet())
                    .stream().toList();
            if (!origins.isEmpty()) {
                List<Coordinate> originsTwo = new LinkedList<>(origins);
                boolean change = true;
                while (change){
                    change = reduceIter(new HashSet<>(), originsTwo);
                }
            }
        }
        @Override
        protected void reduceIterHelperStart(Map<Coordinate, Path> currentPawnOrigins) {}
    }

}
