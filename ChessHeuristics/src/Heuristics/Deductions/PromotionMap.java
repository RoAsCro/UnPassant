package Heuristics.Deductions;

import Heuristics.BoardInterface;
import Heuristics.Detector.Data.StandardCaptureData;
import Heuristics.Detector.Data.StandardPawnData;
import Heuristics.Detector.Data.StandardPieceData;
import Heuristics.Detector.Data.StandardPromotionData;
import Heuristics.Detector.StandardStateDetector;
import Heuristics.Observations.PawnNumber;
import Heuristics.Observations.PieceNumber;
import Heuristics.Path;
import Heuristics.Detector.StateDetector;
import StandardChess.Coordinate;
import StandardChess.Coordinates;

import java.util.*;
import java.util.stream.Collectors;

import static Heuristics.HeuristicsUtil.*;

public class PromotionMap extends AbstractDeduction {

    private final static int HIGH_NUMBER = 99;
    private PiecePathFinderUtil pathFinderUtil = new PiecePathFinderUtil(detector);

    private boolean inDepth = true;

    private final List<Map<Path, Path>> claimsWhite = new LinkedList<>();
    private final List<Map<Path, Path>> claimsBlack = new LinkedList<>();

    @Deprecated
    private int additionalCapturesWhite = 0;
    @Deprecated
    private int additionalCapturesBlack = 0;

    private Path origins;
    private Path targets;

    private PromotionPawnMap promotionPawnMapWhite;
    private PromotionPawnMap promotionPawnMapBlack;
    private PromotionCombinedPawnMap combinedPawnMap;

    // <Promotion square, <Origin square, Number of captures>>
    private final Map<Coordinate, Map<Coordinate, Integer>> goalOrigins = new HashMap<>();
//    private final Map<>

    public PromotionMap() {
        super("A promoted piece cannot reach its current location from an open pawn start");
        this.promotionPawnMapWhite = new PromotionPawnMap(true);
        this.promotionPawnMapBlack = new PromotionPawnMap(false);
    }


    @Override
    public void registerDetector(StateDetector detector) {
        super.registerDetector(detector);
        this.pathFinderUtil = new PiecePathFinderUtil(detector);
//        this.promotionPawnMapWhite.registerDetector(this.detector);
//        this.promotionPawnMapBlack.registerDetector(this.detector);
    }

    @Override
    public boolean deduce(BoardInterface board) {
        // Replace
        if (this.detector.getPromotionData().getPromotedPieceMap().values().stream().allMatch(AbstractCollection::isEmpty)) {
            this.state = true;
            return true;
        }
        this.origins = Path.of(this.detector.getPawnData().getOriginFree(true).entrySet()
                .stream().filter(Map.Entry::getValue)
                .map(Map.Entry::getKey)
                .toList());
        this.origins.addAll(this.detector.getPawnData().getOriginFree(false).entrySet()
                .stream().filter(Map.Entry::getValue)
                .map(Map.Entry::getKey)
                .toList());
        // May have duplicates
        this.targets = Path.of(this.detector.getPromotionData().getPromotedPieceMap().entrySet()
                .stream().filter(entry -> !entry.getValue().isEmpty())
//                        .filter(entry -> entry.getKey().getY() == 7)
                .map(Map.Entry::getKey)
                .toList());

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

        // FOR PERFORMANCE
        if ((whiteOrigins.size() == 7 && whiteTargets.size() == 7)) {
            this.state = true;
            return true;
        }
        if ((blackOrigins.size() == 7 && blackTargets.size() == 7)) {
            this.state = true;
            return true;
        }

        // Generate map of promotion squares and pawn eligible pawn origins
        List<Coordinate> finalWhiteOrigins = whiteOrigins;
        whiteTargets.forEach(coordinate -> finalWhiteOrigins.forEach(coordinate1 -> path(
                coordinate, board, forbiddenWhitePaths, forbiddenBlackPaths, coordinate1
        )));
        List<Coordinate> finalBlackOrigins = blackOrigins;
        blackTargets.forEach(coordinate -> finalBlackOrigins.forEach(coordinate1 -> path(
                coordinate, board, forbiddenWhitePaths, forbiddenBlackPaths, coordinate1
        )));

        Map<Path, List<Path>> pieceSquareOriginWhite = new HashMap<>(updates(board, true));
        Map<Path, List<Path>> pieceSquareOriginBlack = new HashMap<>(updates(board, false));
        // Fail if a piece has no valid origin
        if (pieceSquareOriginWhite.containsValue(List.of()) || pieceSquareOriginBlack.containsValue(List.of())) {
            this.state = false;
            return false;
        }
        // Fail if a set of pieces does not have enough valid origins
        // Note - this check might include the above check naturally
        if (!checkPromotionNumbers(pieceSquareOriginWhite, pieceSquareOriginBlack)) {
            this.state = false;
            return false;
        }

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

        whiteOrigins = this.origins.stream().filter(coordinate -> coordinate.getY() == 1).toList();
        blackOrigins = this.origins.stream().filter(coordinate -> coordinate.getY() == 6).toList();
        this.targets = Path.of(this.goalOrigins.keySet());

        // Fail if there are more promotions than valid origins
        if (promotionsWhite > whiteOrigins.size() || promotionsBlack > blackOrigins.size()) {
            this.state = false;
            return false;
        }
        // Get a valid piece / origin set
        Map<Path, Integer> pathIntegerMap = this.detector.getPromotionData().getPromotionNumbers().values().stream()
                .flatMap(map -> map.entrySet().stream())
                .filter(entry -> entry.getKey() != null)
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
        Map<Coordinate, Path> pieceOriginWhite = pieceOrigin(pieceSquareOriginWhite, pathIntegerMap);
        Map<Coordinate, Path> pieceOriginBlack = pieceOrigin(pieceSquareOriginBlack, pathIntegerMap);

        TheoreticalPawnMap tPMW = new TheoreticalPawnMap(true);
        tPMW.registerDetector(new StandardStateDetector(new PawnNumber(), new PieceNumber(), new StandardPawnData(), new StandardCaptureData(), new StandardPromotionData(), new StandardPieceData(), tPMW));
        tPMW.reduce(pieceOriginWhite);
        TheoreticalPawnMap tPMB = new TheoreticalPawnMap(false);
        tPMB.registerDetector(new StandardStateDetector(new PawnNumber(), new PieceNumber(), new StandardPawnData(),new StandardCaptureData(), new StandardPromotionData(), new StandardPieceData(), tPMB));
        tPMB.reduce(pieceOriginBlack);
        // Fail if after reduction any piece does not have enough origins
        if (!(tPMW.state && tPMB.state)) {
            this.state = false;
            return false;
        }

        this.combinedPawnMap = new PromotionCombinedPawnMap(this.promotionPawnMapWhite, this.promotionPawnMapBlack);
        this.combinedPawnMap.registerDetector(this.detector);
        if (inDepth) {

            Path originPool = Path.of(this.origins.stream().filter(c -> c.getY() == 1).toList());
            Map<Path, Path> claims = new HashMap<>();
            Map<Path, Path> pieceOriginWhiteTwo = pieceSquareOriginWhite.entrySet()
                    .stream().collect(Collectors.toMap(Map.Entry::getKey, entry -> Path.of(entry.getValue().stream().map(Path::getLast).collect(Collectors.toSet()).stream().toList())));

            //System.out.println("origin claim start" + pieceOriginWhiteTwo);
            originClaimIteratorHelperStarter(pathIntegerMap, originPool, pieceOriginWhiteTwo, claims, true);

            originPool = Path.of(this.origins.stream().filter(c -> c.getY() == 6).toList());
            claims = new HashMap<>();
            Map<Path, Path> pieceOriginBlackTwo = pieceSquareOriginBlack.entrySet()
                    .stream().collect(Collectors.toMap(Map.Entry::getKey, entry -> Path.of(entry.getValue().stream().map(Path::getLast).collect(Collectors.toSet()).stream().toList())));

            originClaimIteratorHelperStarter(pathIntegerMap, originPool, pieceOriginBlackTwo, claims, false);
            reduceClaims(true, pieceSquareOriginWhite, board);
            reduceClaims(false, pieceSquareOriginBlack, board);


            if ((this.claimsBlack.isEmpty() && promotionsBlack != 0) || (this.claimsWhite.isEmpty() && promotionsWhite != 0)) {
                this.state = false;
                return false;
            }
            this.state = false;
            stateIterateStart(board);
        } else {
            this.promotionPawnMapWhite.deduce(board);
            this.promotionPawnMapBlack.deduce(board);
            this.combinedPawnMap.deduce(board);
            this.state = this.promotionPawnMapWhite.getState() && this.promotionPawnMapBlack.state && this.combinedPawnMap.getState();
            throw new RuntimeException();

        }
//        System.out.println(this.combinedPawnMap.getWhitePaths());
        return false;
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

    private boolean stateIterateStart(BoardInterface board) {
        List<List<Map<Path, Path>>> claimList = new LinkedList<>();
        if (!this.claimsWhite.isEmpty()) {
            claimList.add(this.claimsWhite);
        }
        if (!this.claimsBlack.isEmpty()) {
            claimList.add(this.claimsBlack);
        }
//        System.out.println(claimList);
        return stateIterate(board, claimList, new LinkedList<>());
    }

    private boolean stateIterate(BoardInterface board, List<List<Map<Path, Path>>> claimList, List<Map<Path, Path>> originList) {
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
                    return true;
                }
            }
        }
        return false;
    }



    private boolean stateUpdate(BoardInterface board, List<Map<Path, Path>> originList) {
        if (!originList.isEmpty()) {
            this.origins = new Path();
        }
        originList.forEach(map -> this.origins.addAll(map.values().stream().flatMap(Path::stream).toList()));
        this.promotionPawnMapWhite = new PromotionPawnMap(true);
        this.promotionPawnMapBlack = new PromotionPawnMap(false);
        this.combinedPawnMap = new PromotionCombinedPawnMap(promotionPawnMapWhite, promotionPawnMapBlack);
        StateDetector stateDetector = new StandardStateDetector(new PawnNumber(), new PieceNumber(), new StandardPawnData(),new StandardCaptureData(), new StandardPromotionData(), new StandardPieceData(), combinedPawnMap);
//        this.promotionPawnMapWhite.registerDetector(stateDetector);
//        this.promotionPawnMapBlack.registerDetector(stateDetector);
        this.combinedPawnMap.registerDetector(stateDetector);;
        stateDetector.testState(board);
        if (this.promotionPawnMapBlack.state && this.promotionPawnMapWhite.state && this.combinedPawnMap.state) {
//            this.detector.getPawnOrigins(true).putAll(stateDetector.getPawnOrigins(true));
//            this.detector.getPawnOrigins(false).putAll(stateDetector.getPawnOrigins(false));
            // NEW
            this.detector.getPawnData().getPawnPaths(true).putAll(stateDetector.getPawnData().getPawnPaths(true));
            this.detector.getPawnData().getPawnPaths(false).putAll(stateDetector.getPawnData().getPawnPaths(false));
            // NEW

            this.state = true;
            //System.out.println("GGGG");
            //System.out.println(originList);
            //System.out.println(this.targets);
            return true;
        }
        return false;
    }


    private boolean originClaimIteratorHelperStarter(Map<Path, Integer> pathIntegerMap, Path originPool, Map<Path, Path> pieceOrigin, Map<Path, Path> claims, boolean white) {
        pieceOrigin.keySet().forEach(p -> claims.put(p, new Path()));
        for (int i = 0; i < 1 ;) {
            Iterator<Map.Entry<Path, Path>> iter = pieceOrigin.entrySet().iterator();
            boolean called = false;
            while (iter.hasNext()) {
                Map.Entry<Path, Path> entry = iter.next();
                Map<Path, Path> pieceOriginNew = pieceOrigin.entrySet().stream().collect(Collectors.toMap(Map.Entry::getKey, e -> Path.of(e.getValue())));
                Map<Path, Path> claimsNew = claims.entrySet().stream().collect(Collectors.toMap(Map.Entry::getKey, e -> Path.of(e.getValue())));
                if (originClaimIteratorHelper(pathIntegerMap, Path.of(originPool), pieceOriginNew, claimsNew, entry.getKey(), white)) {
                    called = true;
                }
            }
            if (!called) {
                i++;
            }
        }

        return false;
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

            Map<Path, Path> newClaims = claims.entrySet().stream().collect(Collectors.toMap(Map.Entry::getKey, e -> Path.of(new HashSet<>(e.getValue()))));

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
                Map<Path, Path> pieceOriginWhiteTwoNew = pieceOrigin.entrySet().stream().collect(Collectors.toMap(Map.Entry::getKey, e -> Path.of(e.getValue())));
                originClaimIteratorHelper(pathIntegerMap, originPoolNew, pieceOriginWhiteTwoNew, newClaims, entry.getKey(), white);
            }
        }
        return claimed;
    }
    private void reduceClaims(boolean white, Map<Path, List<Path>> pieceSquareOrigin, BoardInterface board) {
        int maxCaptures = (this.detector.getCaptureData().pawnTakeablePieces(white) - board.getBoardFacts().pieceNumbers(!white)) - this.detector.getPawnData().minimumPawnCaptures(white)
//                + (white ? additionalCapturesWhite : additionalCapturesBlack)
                ;

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
//        //system.out.println(toRemove);
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
    private Map<Path, List<Path>> updates(BoardInterface board, boolean white) {
        int maxCaptures = (this.detector.getCaptureData().pawnTakeablePieces(white) - (board.getBoardFacts().pieceNumbers(!white))) - this.detector.getPawnData().minimumPawnCaptures(white)
//                + (white ? additionalCapturesWhite : additionalCapturesBlack)
                ;
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

    private void path(Coordinate origin,
                      BoardInterface board,
                      List<Path> forbiddenWhitePaths, List<Path> forbiddenBlackPaths,
                      Coordinate target) {
        Path shortest = new PiecePathFinderUtil(this.detector).findShortestPawnPath(
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
            this.goalOrigins.get(origin).put(target, PiecePathFinderUtil.PATH_DEVIATION.apply(shortest));
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
//            cagedCaptures = Path.of(cagedCaptures.stream().filter(c -> c.getX() == Q_ROOK_X || c.getX() == K_ROOK_X).toList());
            for (Coordinate rookLocation : cagedCaptures) {
                if (!this.pathFinderUtil.findPiecePath(board, "rook", white ? "r" : "R", rookLocation, checkedCoord).isEmpty()) {
                    forRemoval.add(rookLocation);
                    break;
                }

            }
            if (!forRemoval.isEmpty()) {
                this.detector.getCaptureData().getNonPawnCaptures(white).removeAll(forRemoval);
                if (white) {
                    this.additionalCapturesWhite++;
                } else {
                    this.additionalCapturesBlack++;
                }
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
        public boolean deduce(BoardInterface boardInterface){
            return super.deduce(boardInterface);
        }
    }

    private class PromotionPawnMap extends CombinedPawnMap.PawnMap {
        public PromotionPawnMap(boolean white) {
            super(white);
        }
        @Override
        public boolean deduce(BoardInterface board) {
            if (this.maxPieces == MAX_PIECES) {
                int subtrahend = white
                        ? PromotionMap.this.detector.getCaptureData().pawnTakeablePieces(true) + PromotionMap.this.additionalCapturesWhite
                        : PromotionMap.this.detector.getCaptureData().pawnTakeablePieces(false) + PromotionMap.this.additionalCapturesBlack;
                System.out.println("-----------");
                System.out.println(subtrahend);
                System.out.println(PromotionMap.this.detector.getCaptureData().getNonPawnCaptures(white));
                this.detector.getCaptureData().getNonPawnCaptures(white).addAll(PromotionMap.this.detector.getCaptureData().getNonPawnCaptures(white));
            }
            super.deduce(board);
            return false;
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
        protected void rawMap(BoardInterface board, boolean white) {
            Map<Coordinate, Path> toAddOne = PromotionMap.this.detector.getPawnData().getPawnPaths(white).entrySet().stream()
                    .collect(Collectors.toMap(e -> e.getKey(), e -> Path.of(e.getValue().stream().map(p -> p.getFirst()).toList())));
            toAddOne.forEach((key, value) -> getPawnOrigins().put(key, Path.of(value)));
            Map<Coordinate, Path> toAddTwo = new HashMap<>();
            int y = (white ? FINAL_RANK_Y : FIRST_RANK_Y);
            Path targetsFiltered = Path.of(origins.stream().filter(coordinate -> coordinate.getY() == Math.abs(y - (BLACK_PAWN_Y))).toList());
            targets.stream().filter(coordinate -> coordinate.getY() == y)
                    .forEach(c -> toAddTwo.put(c, Path.of(targetsFiltered.stream()
                            .filter(cTwo -> Math.abs(c.getX() - cTwo.getX()) < K_ROOK_X).toList())));
            toAddTwo.entrySet().stream().filter(entry -> !entry.getValue().isEmpty())
                    .forEach(entry -> getPawnOrigins().put(entry.getKey(), entry.getValue()));
        }

        @Override
        protected void captures(Map<Coordinate, Path> origins) {
            flip(false, white);
            super.captures(origins);
            flip(true, white);
            Map<Coordinate, Integer> newCaptures = new HashMap<>();
            getPawnOrigins().entrySet().stream()
                    .filter(entry -> entry.getKey().getY()
                            == (white ? FINAL_RANK_Y : FIRST_RANK_Y))
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
            //System.out.println("reduce  1" + detector.getPawnOrigins(white));

            flip(false, this.white);
            boolean reduction = super.reduceIter(set, origins);
            flip(true, this.white);
            //System.out.println("reduce  2" + detector.getPawnOrigins(white));

            return reduction;

        }

    }

    private class TheoreticalPawnMap extends CombinedPawnMap.PawnMap {
        public TheoreticalPawnMap(boolean white) {
            super(white);
            this.maxPieces = PromotionMap.this.detector.getCaptureData().pawnTakeablePieces(white);
            //system.out.println("MAXMAX" + maxPieces);

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
        protected void reduceIterHelperStart(Map<Coordinate, Path> map) {}

        @Override
        public void update() {
            super.update();
        }

    }

}
