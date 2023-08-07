package Heuristics.Deductions;

import Heuristics.BoardInterface;
import Heuristics.Observation;
import Heuristics.Observations.PawnNumber;
import Heuristics.Observations.PieceNumber;
import Heuristics.Path;
import Heuristics.Pathfinder;
import StandardChess.Coordinate;
import StandardChess.StandardPieceFactory;

import java.util.*;
import java.util.stream.Collectors;

public class PromotionMap extends AbstractDeduction {

    PieceMap pieceMap;
    CombinedPawnMap pawnMap;
    PawnMapWhite pawnMapWhite;

    PawnMapBlack pawnMapBlack;

    private PieceNumber pieceNumber;
    private PawnNumber pawnNumber;


    CaptureLocations captureLocations;

    private Path captureSquaresWhite = new Path();
    private Path captureSquaresBlack = new Path();

    private List<Map<Path, Path>> claimsWhite = new LinkedList<>();
    private List<Map<Path, Path>> claimsBlack = new LinkedList<>();



    private int additionalCapturesWhite = 0;
    private int additionalCapturesBlack = 0;


    private Path origins;
    private Path targets;

    private PromotionPawnMapWhite promotionPawnMapWhite;
    private PromotionPawnMapBlack promotionPawnMapBlack;
    private CombinedPawnMap combinedPawnMap;

    private final Map<Coordinate, Integer> captureSet = new HashMap<>();

    // <Promotion square, <Origin square, Number of captures>>
    private final Map<Coordinate, Map<Coordinate, Integer>> goalOrigins = new HashMap<>();



    public PromotionMap(PieceMap pieceMap, CombinedPawnMap pawnMap, PawnMapWhite pawnMapWhite, PawnMapBlack pawnMapBlack, CaptureLocations captureLocations, PieceNumber pieceNumber, PawnNumber pawnNumber) {

        this.pieceNumber = pieceNumber;
        this.pawnNumber = pawnNumber;
        this.pieceMap = pieceMap;
        this.pawnMap = pawnMap;
        this.pawnMapWhite = pawnMapWhite;
        this.pawnMapBlack = pawnMapBlack;
        this.captureLocations = captureLocations;
        this.promotionPawnMapWhite = new PromotionPawnMapWhite();
        this.promotionPawnMapBlack = new PromotionPawnMapBlack();


    }


    @Override
    public List<Observation> getObservations() {
        return captureLocations.getObservations();
    }

    @Override
    public boolean deduce(BoardInterface board) {

//        this.captureLocations.deduce(board);

        //system.out.println("FIRSTOFALL" + this.pieceMap.getPromotedPieceMap());
        if (this.pieceMap.getPromotedPieceMap().values().stream().flatMap(Path::stream).toList().isEmpty()) {
            this.state = true;
            return true;
        }
        boolean priorCheck = this.pawnMapWhite.getState();


//        this.pawnMap.captures("white");
        this.origins = Path.of(this.pawnMapWhite.getOriginFree().entrySet()
                .stream().filter(Map.Entry::getValue)
                .map(Map.Entry::getKey)
                .toList());
        this.origins.addAll(this.pawnMapBlack.getOriginFree().entrySet()
                .stream().filter(Map.Entry::getValue)
                .map(Map.Entry::getKey)
                .toList());

        // May have duplicates
        this.targets = Path.of(this.pieceMap.getPromotedPieceMap().entrySet()
                .stream().filter(entry -> !entry.getValue().isEmpty())
//                        .filter(entry -> entry.getKey().getY() == 7)
                .map(Map.Entry::getKey)
                .toList());


        List<Path> forbiddenWhitePaths = this.pawnMap.getWhitePaths().values().stream()
                .filter(list -> list.size() == 1)
                .map(list -> list.get(0))
                .toList();
        List<Path> forbiddenBlackPaths = this.pawnMap.getBlackPaths().values().stream()
                .filter(list -> list.size() == 1)
                .map(list -> list.get(0))
                .toList();

//        reduce(this.origins, this.targets, board, forbiddenWhitePaths, forbiddenBlackPaths);
//        reduce(this.targets, this.origins, board, forbiddenWhitePaths, forbiddenBlackPaths);
        List<Coordinate> whiteOrigins = this.origins.stream().filter(coordinate -> coordinate.getY() == 1).toList();
        List<Coordinate> blackOrigins = this.origins.stream().filter(coordinate -> coordinate.getY() == 6).toList();
        List<Coordinate> whiteTargets = this.targets.stream().filter(coordinate -> coordinate.getY() == 7).toList();
        List<Coordinate> blackTargets = this.targets.stream().filter(coordinate -> coordinate.getY() == 0).toList();




        // Generate map of promotion squares and pawn eligible pawn origins
        List<Coordinate> finalWhiteOrigins = whiteOrigins;
        whiteTargets.forEach(coordinate -> finalWhiteOrigins.forEach(coordinate1 -> path(
                coordinate, board, forbiddenWhitePaths, forbiddenBlackPaths, coordinate1
        )));
        List<Coordinate> finalBlackOrigins = blackOrigins;
        blackTargets.forEach(coordinate -> finalBlackOrigins.forEach(coordinate1 -> path(
                coordinate, board, forbiddenWhitePaths, forbiddenBlackPaths, coordinate1
        )));

        Map<Path, List<Path>> pieceSquareOriginWhite = new HashMap<>(updates(true));
        Map<Path, List<Path>> pieceSquareOriginBlack = new HashMap<>(updates(false));
        // Fail if a piece has no valid origin
        //system.out.println("INFO");
        //system.out.println(additionalCapturesWhite);
        //system.out.println(additionalCapturesBlack);

        //system.out.println(origins);
        //system.out.println(targets);

        //system.out.println(goalOrigins);

        //system.out.println(pieceSquareOriginWhite);
        if (pieceSquareOriginWhite.containsValue(List.of()) || pieceSquareOriginBlack.containsValue(List.of())) {
            this.state = false;
            return false;
        }
        // Fail if a set of pieces does not have enough valid origins
        // Note - this check might include the above check naturally
        Map<Path, Integer> promotionNumbers = this.pieceMap.getPromotionNumbers().values()
                .stream()
                .flatMap(map -> map.entrySet().stream())
                .filter(entry -> entry.getKey() != null)
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
        if (pieceSquareOriginWhite.entrySet().stream()
                .anyMatch(entry ->
                        (entry.getKey().size() - promotionNumbers.get(entry.getKey()) >
                                entry.getValue().stream()
                                        .map(LinkedList::getLast)
                                        .collect(Collectors.toSet())
                                        .size()))
                ||
        pieceSquareOriginBlack.entrySet().stream()
                .anyMatch(entry ->
                        (entry.getKey().size() - promotionNumbers.get(entry.getKey()) >
                                entry.getValue().stream()
                                        .map(LinkedList::getLast)
                                        .collect(Collectors.toSet())
                                        .size()))) {

            this.state = false;
            return false;
        }

        int promotionsWhite = this.pieceMap.getPromotionNumbers().values().stream()
                .flatMap(map -> map.entrySet().stream())
                .filter(entry -> pieceSquareOriginWhite.containsKey(entry.getKey()))
                .filter(entry -> entry.getKey() != null)
                .map(entry -> entry.getKey().size() - entry.getValue())
                .reduce(Integer::sum)
                .orElse(0);
        int promotionsBlack = this.pieceMap.getPromotionNumbers().values().stream()
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
//        pieceSquareOriginWhite.entrySet().stream()


        // Fail if there are more promotions than valid origins
        //system.out.println("INFO2");
        //system.out.println(promotionsWhite);
        //system.out.println(whiteOrigins);
        //system.out.println(pieceSquareOriginBlack);


        if (promotionsWhite > whiteOrigins.size() || promotionsBlack > blackOrigins.size()) {
            this.state = false;
            return false;
        }
        // Get a valid piece / origin set

        Map<Path, Integer> pathIntegerMap = this.pieceMap.getPromotionNumbers().values().stream()
                .flatMap(map -> map.entrySet().stream())
                .filter(entry -> entry.getKey() != null)
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));


        Map<Coordinate, Path> pieceOriginWhite = pieceSquareOriginWhite.entrySet()
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
        Map<Coordinate, Path> pieceOriginBlack = pieceSquareOriginBlack.entrySet()
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

        //system.out.println("POW" + pieceOriginWhite);
        //system.out.println(pathIntegerMap);
        TheoreticalPawnMap tPMW = new TheoreticalPawnMap("white");
        tPMW.reduce(pieceOriginWhite);
        TheoreticalPawnMap tPMB = new TheoreticalPawnMap("black");
        tPMB.reduce(pieceOriginBlack);
        //system.out.println("POW" + pieceOriginWhite);

        // Fail if after reduction any piece does not have enough origins
        if (!(tPMW.state && tPMB.state)) {
            this.state = false;
            return false;
        }
        //system.out.println("TPTPTP" + tPMW.getPawnOrigins());



        Path originPool = Path.of(this.origins.stream().filter(c -> c.getY() == 1).toList());
        Map<Path, Path> claims = new HashMap<>();
        Map<Path, Path> pieceOriginWhiteTwo = pieceSquareOriginWhite.entrySet()
                .stream().collect(Collectors.toMap(Map.Entry::getKey, entry -> Path.of(entry.getValue().stream().map(Path::getLast).collect(Collectors.toSet()).stream().toList())));


        originClaimIteratorHelperStarter(0, pathIntegerMap, originPool, pieceOriginWhiteTwo, claims, true);

        originPool = Path.of(this.origins.stream().filter(c -> c.getY() == 6).toList());
        claims = new HashMap<>();
        Map<Path, Path> pieceOriginBlackTwo = pieceSquareOriginBlack.entrySet()
                .stream().collect(Collectors.toMap(Map.Entry::getKey, entry -> Path.of(entry.getValue().stream().map(Path::getLast).collect(Collectors.toSet()).stream().toList())));
        //system.out.println("POB22" + pathIntegerMap);
        originClaimIteratorHelperStarter(0, pathIntegerMap, originPool, pieceOriginBlackTwo, claims, false);


        //system.out.println("CLAIMS" + this.claimsWhite);
        //system.out.println("CLAIMS" + this.claimsBlack);

        reduceClaims(true, pieceSquareOriginWhite);
        reduceClaims(false, pieceSquareOriginBlack);

        //system.out.println("CLAIMS" + this.claimsWhite);
        //system.out.println("CLAIMS" + this.claimsBlack);







        //system.out.println("goasl " + this.goalOrigins);
        //system.out.println(this.pieceMap.getPromotedPieceMap());
        //system.out.println(pieceSquareOriginWhite);
        //system.out.println(this.pawnMapWhite.capturedPieces());
        //system.out.println(this.pawnMap.capturesTwo("white"));
        //system.out.println(this.captureLocations.getCagedCaptures(true));
//        //system.out.println(maxCaptures);





//



        //system.out.println("TARGETS = " + targets);
        //system.out.println("ORIGINS = " + origins);

        PromotionPawnMapTwo secondWhiteMap = new PromotionPawnMapTwo(this.pawnMapWhite);
        PromotionPawnMapTwo secondBlackMap = new PromotionPawnMapTwo(this.pawnMapBlack);
        //system.out.println(this.pieceMap.getPromotedPieceMap());
        //system.out.println(this.pieceMap.getPromotionNumbers());
        //system.out.println("CaptureSetNew"  +this.captureSet);
        //system.out.println("CaptureSetNew"  +this.pawnMapWhite.getCaptureSet());

        //system.out.println("CaptureSetNew"  +secondWhiteMap.getCaptureSet());



//        this.promotionPawnMapWhite.deduce(board);
//        //system.out.println(promotionPawnMapWhite.getPawnOrigins());
//        //system.out.println("Prior" + this.pawnMapWhite.getPawnOrigins());

        this.combinedPawnMap = new PromotionCombinedPawnMap(this.promotionPawnMapWhite, this.promotionPawnMapBlack);
        Path savedOrigin = this.origins;
        if (this.claimsBlack.isEmpty() && this.claimsWhite.isEmpty()) {
            this.state = false;
            return false;
        }
        this.state = false;
        stateIterateStart(board);
//        for (Map<Path, Path> blackClaim : this.claimsBlack) {
//            for (Map<Path, Path> whiteClaim : this.claimsWhite) {
////            CombinedPawnMap combinedPawnMap = new PromotionCombinedPawnMap(this.promotionPawnMapWhite, this.promotionPawnMapBlack);
////        //system.out.println("Prior" + this.pawnMapWhite.getPawnOrigins());
//                this.origins = Path.of(whiteClaim.values().stream().flatMap(Path::stream).toList());
//                this.origins.addAll(blackClaim.values().stream().flatMap(Path::stream).toList());
//                this.promotionPawnMapWhite = new PromotionPawnMapWhite();
//                this.promotionPawnMapBlack = new PromotionPawnMapBlack();
//                this.combinedPawnMap = new PromotionCombinedPawnMap(this.promotionPawnMapWhite, this.promotionPawnMapBlack);
//                this.combinedPawnMap.deduce(board);
//                //system.out.println("STESTE" + (this.promotionPawnMapBlack.state && this.promotionPawnMapWhite.state && combinedPawnMap.state));
//                if (this.promotionPawnMapBlack.state && this.promotionPawnMapWhite.state && combinedPawnMap.state) {
//                    this.state = true;
//                    break;
//                }
////        //system.out.println("Prior" + this.pawnMapWhite.getPawnOrigins());
//            }
//        }

//        this.promotionPawnMapWhite = new PromotionPawnMapWhite();
//                this.promotionPawnMapBlack = new PromotionPawnMapBlack();
//        this.origins = this.origins;
//        this.combinedPawnMap = new PromotionCombinedPawnMap(this.promotionPawnMapWhite, this.promotionPawnMapBlack);
//        this.combinedPawnMap.deduce(board);


        //system.out.println("Combined:");
        //system.out.println(savedOrigin);
        //system.out.println(this.origins);

        //system.out.println(combinedPawnMap.getWhitePaths());
        //system.out.println(this.promotionPawnMapWhite.getPawnOrigins());
        //system.out.println(combinedPawnMap.getBlackPaths());

        //system.out.println(this.pawnMapWhite.getPawnOrigins());
        //system.out.println("States:");
        //system.out.println(this.promotionPawnMapBlack.state);
        //system.out.println(this.promotionPawnMapWhite.state);
        //system.out.println(combinedPawnMap.state);
        //system.out.println(this.pawnMap.getState());
        //system.out.println(this.pawnMapWhite.getState());
        //system.out.println(this.pawnMapBlack.getState());
        //system.out.println(priorCheck);
//        this.state = this.promotionPawnMapBlack.state && this.promotionPawnMapWhite.state && combinedPawnMap.state;

        return false;
    }

    private boolean stateIterateStart(BoardInterface board) {
        List<List<Map<Path, Path>>> claimList = new LinkedList<>();
        if (!this.claimsWhite.isEmpty()) {
            claimList.add(this.claimsWhite);
        }
        if (!this.claimsBlack.isEmpty()) {
            claimList.add(this.claimsBlack);
        }
        //system.out.println(this.claimsWhite);
        //system.out.println(this.claimsBlack);

        //system.out.println(claimList);

        return stateIterate(board, claimList, new LinkedList<>());
    }
    private boolean stateIterate(BoardInterface board, List<List<Map<Path, Path>>> claimList, List<Map<Path, Path>> originList) {
        //system.out.println("passhere");
        //system.out.println(claimList);
        List<Map<Path, Path>> claims = claimList.get(0);
        List<List<Map<Path, Path>>> newClaims = new LinkedList<>(claimList);

        newClaims.remove(claims);

        for (Map<Path, Path> map : claims) {
            List<Map<Path, Path>> newOrigins = new LinkedList<>(originList);
            newOrigins.add(map);
            if (!newClaims.isEmpty()) {
                //system.out.println("itererating");
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
//            this.origins.addAll(blackClaim.values().stream().flatMap(Path::stream).toList());
        this.promotionPawnMapWhite = new PromotionPawnMapWhite();
        this.promotionPawnMapBlack = new PromotionPawnMapBlack();
        this.combinedPawnMap = new PromotionCombinedPawnMap(this.promotionPawnMapWhite, this.promotionPawnMapBlack);
        this.combinedPawnMap.deduce(board);
//            //system.out.println("STESTE" + (this.promotionPawnMapBlack.state && this.promotionPawnMapWhite.state && combinedPawnMap.state));
        if (this.promotionPawnMapBlack.state && this.promotionPawnMapWhite.state && combinedPawnMap.state) {
            this.state = true;
            return true;
        }
    return false;
    }


    private boolean originClaimIteratorHelperStarter(int j, Map<Path, Integer> pathIntegerMap, Path originPool,  Map<Path, Path> pieceOrigin, Map<Path, Path> claims, boolean white) {
        pieceOrigin.keySet().forEach(p -> claims.put(p, new Path()));
        for (int i = j ; i < 16 ;) {
            Iterator<Map.Entry<Path, Path>> iter = pieceOrigin.entrySet().iterator();

            boolean called = false;
            while (iter.hasNext()) {
                Map.Entry<Path, Path> entry = iter.next();
//                //system.out.println(pathIntegerMap);
                //system.out.println(i);

                if ((entry.getKey().size() - pathIntegerMap.get(entry.getKey())) + claims.get(entry.getKey()).size()
                        > entry.getValue().size() + i) {
                    continue;
                }

                Map<Path, Path> pieceOriginNew = pieceOrigin.entrySet().stream().collect(Collectors.toMap(Map.Entry::getKey, e -> Path.of(e.getValue())));
                Map<Path, Path> claimsNew = claims.entrySet().stream().collect(Collectors.toMap(Map.Entry::getKey, e -> Path.of(e.getValue())));
                if (!originClaimIteratorHelper(i, pathIntegerMap, Path.of(originPool), pieceOriginNew, claimsNew, entry.getKey(), white)) {
                } else {
                    called = true;
                }

            }
            if (!called) {
                i++;
            }
        }

        return false;
    }


        private boolean originClaimIteratorHelper(int j, Map<Path, Integer> pathIntegerMap, Path originPool,  Map<Path, Path> pieceOrigin, Map<Path, Path> claims, Path current, boolean white) {
//        //system.out.println(this.claims);
        if (originPool.isEmpty()) {
//            //system.out.println("empty");
            return false;
        }

        List<Coordinate> claim = pieceOrigin.get(current).stream().filter(originPool::contains).toList();
        if (claim.isEmpty()) {
//            //system.out.println("claimempty");

            return false;
        }

        boolean claimed = false;
        ///
        for (Coordinate coordinate : claim) {

            Map<Path, Path> newClaims = claims.entrySet().stream().collect(Collectors.toMap(Map.Entry::getKey, e -> Path.of(e.getValue())));

            newClaims.get(current).add(coordinate);
//            //system.out.println(newClaims.get(current).size() == 0);
//            //system.out.println(newClaims.get(current));
//            //system.out.println(coordinate);
//            if (newClaims.get(current).size() == 2) {
//                //system.out.println(newClaims);
//            }
//            //system.out.println(claims);
            Path originPoolNew = Path.of(originPool);
            originPoolNew.remove(coordinate);
//            pieceOriginWhiteTwo.entrySet().forEach(innerEntry -> //system.out.println(innerEntry.getKey().size() - pathIntegerMap.get(innerEntry.getKey()) - finalClaims.get(innerEntry.getKey()).size()));
//            //system.out.println((pieceOriginWhiteTwo.keySet().size() - pathIntegerMap.get(current)) + finalClaims.get(current).size());
            if (pieceOrigin.entrySet().stream().allMatch(innerEntry -> (innerEntry.getKey().size() - pathIntegerMap.get(innerEntry.getKey())) - newClaims.get(innerEntry.getKey()).size() == 0)) {
//                //system.out.println(newClaims);
                List<Map<Path, Path>> allClaims = white ? this.claimsWhite : this.claimsBlack;
                if (!allClaims.contains(newClaims)) {
                    allClaims.add(newClaims);
                    claimed = true;
                }
                continue;
            }
//            for (int i = j ; i < 17 ;) {
//                //system.out.println("inLopp" + coordinate);
//                boolean called = false;
                Iterator<Map.Entry<Path, Path>> iter = pieceOrigin.entrySet().iterator();
                while (iter.hasNext()) {
                    Map.Entry<Path, Path> entry = iter.next();
//                    if ((entry.getKey().size() - pathIntegerMap.get(entry.getKey())) + newClaims.get(entry.getKey()).size()
//                            > entry.getValue().size() + i) {
////                        //system.out.println("continuing...");
//                        continue;
//                    }


                    Map<Path, Path> pieceOriginWhiteTwoNew = pieceOrigin.entrySet().stream().collect(Collectors.toMap(Map.Entry::getKey, e -> Path.of(e.getValue())));
//                    //system.out.println("thisFar");
                    if (!originClaimIteratorHelper(j, pathIntegerMap, originPoolNew, pieceOriginWhiteTwoNew, newClaims, entry.getKey(), white)) {
                    } else {
//                        called = true;
                    }

                }
//                if (!called) {
//                    i++;
//                }
            }
//        }

        return claimed;
    }

    private void reduceClaims(boolean white, Map<Path, List<Path>> pieceSquareOrigin) {
        PawnMap playersPawnMap = white ? this.pawnMapWhite : this.pawnMapBlack;
        int maxCaptures = playersPawnMap.capturedPieces() - this.pawnMap.capturesTwo(white ? "white" : "black")
                + (white ? additionalCapturesWhite : additionalCapturesBlack)
                ;
//        //system.out.println("MAXP" + goalOrigins);
//        //system.out.println("MAXP" + pieceSquareOrigin);

        List<Map<Path, Path>> allClaims = white ? this.claimsWhite : this.claimsBlack;

        List<Map<Path, Path>> toRemove = allClaims.stream().filter(map -> {
            int max = map.entrySet().stream().map(entry -> {
                        Path key = entry.getKey();
                        Path value = entry.getValue();
                        return value.stream().map(c -> {
                                     int m = pieceSquareOrigin.get(key)
                                            .stream().filter(p -> p.getLast().equals(c))
                                            .map(p -> this.goalOrigins.get(p.getFirst()).get(p.getLast()))
                                            .reduce((i, j) -> i < j ? i : j)
                                            .orElse(10000);
//                                     //system.out.println("innermax" + m);
//                                    //system.out.println(c);
//                                    //system.out.println(entry);

                                    return m;
                                })
                                .reduce(Integer::sum)
                                .orElse(10000);

                    }).reduce(Integer::sum)
                    .orElse(10000);

//            //system.out.println("MCMC" + max);
//            //system.out.println(map);

            return max > maxCaptures;
        }).toList();
        allClaims.removeAll(toRemove);
//        //system.out.println(toRemove);
    }

    private Map<Path, List<Path>> updates(boolean white) {
        //system.out.println("goasl " + this.goalOrigins);
        PawnMap playersPawnMap = white ? this.pawnMapWhite : this.pawnMapBlack;
        int maxCaptures = playersPawnMap.capturedPieces() - this.pawnMap.capturesTwo(white ? "white" : "black")
                + (white ? additionalCapturesWhite : additionalCapturesBlack)
                ;
        //system.out.println(playersPawnMap.capturedPieces());
        //system.out.println(maxCaptures);
        int extraCaptures = this.captureLocations.getCagedCaptures(white).size();
        //system.out.println(extraCaptures);
        this.goalOrigins.entrySet().stream()
                .filter(entry -> entry.getKey().getY() == (white ? 7 : 0))
                .filter(entry -> {
                    Path removals = new Path();
                    entry.getValue().entrySet().forEach(entryTwo -> {
                        if (entryTwo.getValue() > maxCaptures) {
//                            Path cagedCaptures = this.captureLocations.getCagedCaptures(white);
//                            if (cagedCaptures.size() > 0) {
//                                int captureY;
//                                if (white) {
//                                    captureY = 5;
//                                } else {
//                                    captureY = 2;
//                                }
//                            }

                            removals.add(entryTwo.getKey());
                        }
                    });
                    removals.forEach(c -> entry.getValue().remove(c));
                    return entry.getValue().isEmpty();
                })
                .map(Map.Entry::getKey)
                .toList()
                .forEach(this.goalOrigins::remove);
        // }A
        // Link promoted piece with origin
        // <promoted piece type, [promotion square, origin]>
        // B{
        Map<Path, List<Path>> pieceSquareOrigin = new HashMap<>();
        this.pieceMap.getPromotionNumbers().entrySet().stream()
                .filter(entry -> entry.getKey().charAt(entry.getKey().length()-1) == (white ? 'w' : 'b'))
                .forEach(entry -> entry.getValue().keySet()
                        .stream()
                        .filter(Objects::nonNull)
                        .forEach(path -> pieceSquareOrigin.putIfAbsent(path, new LinkedList<>())));
        this.goalOrigins.entrySet().stream()
                .forEach(entry -> {

                    Path pieces = this.pieceMap.getPromotedPieceMap().get(entry.getKey());
                    pieceSquareOrigin.keySet().stream()
                            .filter(path -> pieces.contains(path.getFirst()))
                            .forEach(piece -> entry.getValue().keySet()
                                    .forEach(origin -> pieceSquareOrigin.get(piece).add(Path.of(entry.getKey(), origin))));
                });
        // } B
        return pieceSquareOrigin;
    }

    private void path(Coordinate origin,
                      BoardInterface board,
                      List<Path> forbiddenWhitePaths, List<Path> forbiddenBlackPaths,
                      Coordinate target) {

            Path shortest = Pathfinder.findShortestPawnPath(
                    StandardPieceFactory.getInstance().getPiece(origin.getY() == 1 || origin.getY() == 0 ? "P" : "p"),
                    origin,
                    (b, c) -> target.equals(c),
                    board,
                    p -> {
                        return true;
//                        Map<Coordinate, Path> map = coordinate.getY() == 1
//                                ? this.pawnMap.getSinglePath("white")
//                                : this.pawnMap.getSinglePath("black");
//                        return map.values().stream()
//                                .noneMatch(path -> Pathfinder.pathsExclusive(p, path));

                    },
                    (p1, p2) -> {
                        //system.out.println(
//                                p1 + " vs " + p2
//                        );
                        List<Path> forbiddenPaths = origin.getY() == 1 || origin.getY() == 7
                                ? forbiddenBlackPaths
                                : forbiddenWhitePaths;

                        boolean p1NotExclusive;
                        if (p1.isEmpty()) {
                            p1NotExclusive = true;
                        } else {
                            p1NotExclusive = forbiddenPaths
                                    .stream()
                                    .noneMatch(path ->
                                            // What is commented out below may greatly affect performance
                                            // now that it is commented out - however, it allows theoretical collision
                                            // Before reinstating, create new checks
//                                        p1.contains(entry.getKey()) &&
                                            Pathfinder.pathsExclusive(p1, path));
                        }
                        boolean p2NotExclusive = forbiddenPaths
                                .stream()
                                .noneMatch(path ->
                                        // See above before deleting
//                                    (p2.contains(entry.getKey()) || entry.getValue().get(0).contains(p2.getLast())) &&
                                        Pathfinder.pathsExclusive(p2, path));
                        if (!p1NotExclusive && !p2NotExclusive) {
                            return new Path();
                        }
                        if (!p1NotExclusive) {
                            return p2;
                        }
                        if (!p2NotExclusive) {
//                            //system.out.println("p2 exclusive");
                            return p1;
                        }


                        if (!p1.isEmpty()) {
                            int p1Deviation = CombinedPawnMap.PATH_DEVIATION.apply(p1);
                            int p2Deviation = CombinedPawnMap.PATH_DEVIATION.apply(p2);
//                            if (p1Deviation == p2Deviation && (origin.getY() == 1 || origin.getY() == 6)) {
////                                Path cagedCaptures = this.captureLocations.getCagedCaptures(origin.getY() == 1);
////                                if (cagedCaptures.size() > 0) {
////                                    int captureY = (origin.getY() == 1) ? 5 : 2;
////                                    int change = (origin.getY() == 1) ? 1 : -1;
////                                    int p1CC = pathCagedCaptures(captureY, change, p1);
////                                    int p2CC = pathCagedCaptures(captureY, change, p2);
////                                    if (p1CC > p2CC) {
////                                        return p1;
////                                    } else {
////                                        return p2;
////                                    }
////
////                                }
//                            }
                            if (p1Deviation < p2Deviation) {
                                return p1;
                            }
                        }
//                        //system.out.println("both not exclusive");
                        return p2;
                    });
            if (!shortest.isEmpty()) {
                boolean white = origin.getY() == 7;
                Path cagedCaptures = this.captureLocations.getCagedCaptures(white);
                //system.out.println("CC.Size1");
                //system.out.println(origin);
                if (cagedCaptures.size() > 0 && (white || origin.getY() == 0)) {
                    //system.out.println("CC.Size2");
                    int captureY = white ? 5 : 2;
                    int change = white ? 1 : -1;
                    pathCagedCaptures(captureY, change, shortest, cagedCaptures, board, white);
                }


                this.goalOrigins.putIfAbsent(origin, new HashMap<>());
                this.goalOrigins.get(origin).put(target, CombinedPawnMap.PATH_DEVIATION.apply(shortest));
            }

    }

    private void pathCagedCaptures(int y, int change, Path path, Path cagedCaptures, BoardInterface board, boolean white) {
        //system.out.println("Pathing...");
        //system.out.println(cagedCaptures);
        //system.out.println(path);
        //system.out.println(y);
        Coordinate c1 = path.stream().filter(c -> c.getY() == y).findAny().orElse(new Coordinate(-1, -1));
        Coordinate c2 = path.stream().filter(c -> c.getY() == y + change).findAny().orElse(new Coordinate(-1, -1));
        Coordinate c3 = path.stream().filter(c -> c.getY() == y + change * 2).findAny().orElse(new Coordinate(-1, -1));
        //system.out.println(c1 + ", " + c2 + ", " + c3);
        Path forChecking = new Path();
        if (c1.getX() != c2.getX()) {
            if (c1.getX() != -1 && c2.getX() != -1) {
                forChecking.add(c2);
            }
        }
        if (c2.getX() != c3.getX()) {
            if (c2.getX() != -1 && c3.getX() != -1) {
                forChecking.add(c3);
            }
        }

        for (Coordinate checkedCoord : forChecking) {
            Path forRemoval = new Path();
            for (Coordinate rookLocation : cagedCaptures) {
                //system.out.println("Path info");
                //system.out.println(rookLocation);
                //system.out.println(checkedCoord);
                //system.out.println(white ? "p" : "P");

                if (!pieceMap.findPath(board, "rook", white ? "r" : "R", rookLocation, checkedCoord).isEmpty()) {
                    //system.out.println("Pathed succ");
                    forRemoval.add(rookLocation);
                    break;
                } else {
                    //system.out.println("Unsucc");
                    //system.out.println(rookLocation);
                    //system.out.println(checkedCoord);
                }

            }
            if (!forRemoval.isEmpty()) {
                if (white) {
                    this.captureSquaresWhite.add(checkedCoord);
                    this.additionalCapturesWhite++;
                } else {
                    this.captureSquaresBlack.add(checkedCoord);
                    this.additionalCapturesBlack++;
                }
                forRemoval.forEach(cagedCaptures::remove);
            }
        }
    }


    public void reduce(Path starts, Path goals,
                       BoardInterface board,
                       List<Path> forbiddenWhitePaths, List<Path> forbiddenBlackPaths) {

        starts.stream().filter(coordinate -> {
                    Path shortest = Pathfinder.findShortestPawnPath(
                                    StandardPieceFactory.getInstance().getPiece(coordinate.getY() == 1 || coordinate.getY() == 0 ? "P" : "p"),
                                    coordinate,
                                    (b, c) -> goals.contains(c),
                                    board,
                                    p -> {
                                        return true;
//                        Map<Coordinate, Path> map = coordinate.getY() == 1
//                                ? this.pawnMap.getSinglePath("white")
//                                : this.pawnMap.getSinglePath("black");
//                        return map.values().stream()
//                                .noneMatch(path -> Pathfinder.pathsExclusive(p, path));

                                    },
                                    (p1, p2) -> {
                                        //system.out.println(
//                                                p1 + " vs " + p2
//                                        );
                                        List<Path> forbiddenPaths = coordinate.getY() == 1 || coordinate.getY() == 7
                                                ? forbiddenBlackPaths
                                                : forbiddenWhitePaths;

                                        //system.out.println("Fobbb" + forbiddenPaths);

                                        boolean p1NotExclusive;
                                        if (p1.isEmpty()) {
                                            p1NotExclusive = true;
                                        } else {
                                            p1NotExclusive = forbiddenPaths
                                                    .stream()
                                                    .noneMatch(path ->
                                                            // What is commented out below may greatly affect performance
                                                            // now that it is commented out - however, it allows theoretical collision
                                                            // Before reinstating, create new checks
//                                        p1.contains(entry.getKey()) &&
                                                            Pathfinder.pathsExclusive(p1, path));
                                        }
                                        boolean p2NotExclusive = forbiddenPaths
                                                .stream()
                                                .noneMatch(path ->
                                                        // See above before deleting
//                                    (p2.contains(entry.getKey()) || entry.getValue().get(0).contains(p2.getLast())) &&
                                                        Pathfinder.pathsExclusive(p2, path));
                                        if (!p1NotExclusive && !p2NotExclusive) {
                                            return new Path();
                                        }
                                        if (!p1NotExclusive) {
                                            return p2;
                                        }
                                        if (!p2NotExclusive) {
                                            //system.out.println("p2 exclusive");
                                            return p1;
                                        }

                                        if (!p1.isEmpty() && CombinedPawnMap.PATH_DEVIATION.apply(p1) < CombinedPawnMap.PATH_DEVIATION.apply(p2)) {
                                            return p1;
                                        }
                                        //system.out.println("both not exclusive");
                                        return p2;
                                    });
                    if (!shortest.isEmpty()) {
                        //system.out.println("Shortest" + shortest);
                        this.captureSet.put(coordinate, CombinedPawnMap.PATH_DEVIATION.apply(shortest));
                    }
                    return shortest.isEmpty();
                })
                .toList()
                .forEach(starts::remove);

    }

    public Map<Coordinate, Path> getPawnOrigins(String colour) {
        return colour.equals("white") ? this.promotionPawnMapWhite.getPawnOrigins() : this.pawnMapBlack.getPawnOrigins();
    }

    private class PromotionCombinedPawnMap extends CombinedPawnMap {

        Map<Coordinate, List<Path>> savedPaths = new HashMap<>();

        public PromotionCombinedPawnMap(PromotionPawnMap white, PromotionPawnMap black) {
            super(white, black);

        }
//        @Override
//        protected boolean exclude(BoardInterface board, boolean white) {
//            //system.out.println("FLIP 1" + getWhitePaths());
//            flip(getWhitePaths(), true);
//            flip(getBlackPaths(), true);
//            //system.out.println("FLIP 2" + getWhitePaths());
//
//
//            boolean returnValue = super.exclude(board, white);
//            //system.out.println("FLIP 3" + getWhitePaths());
//
//            flip(getWhitePaths(), false);
//            flip(getBlackPaths(), false);
//            //system.out.println("FLIP 4" + getWhitePaths());
//
//            return returnValue;
//        }

        private void flip(Map<Coordinate, List<Path>> paths, boolean direction) {
            if (direction) {
                this.savedPaths.clear();
                Map<Coordinate, List<Path>> newPaths = new HashMap<>();
                paths.entrySet().stream()
                        .filter(entry -> !entry.getValue().isEmpty())
                        .filter(entry -> {
                            int testInt = (entry.getKey().getY());
                            return (testInt == 7 || testInt == 0);
                        })
                        .forEach(entry -> {
                            if (!newPaths.containsKey(entry.getKey())) {
                                newPaths.put(entry.getKey(), new LinkedList<>());
                            }
                            Path newPath = Path.of(entry.getValue().get(0));
                            newPath.removeLast();
                            newPaths.get(entry.getKey()).add(newPath);
                        });

                newPaths.entrySet().forEach(entry -> {
                    this.savedPaths.put(entry.getKey(), paths.get(entry.getKey()));
                    paths.remove(entry.getKey());
                    paths.put(entry.getKey(), entry.getValue());
                });
            } else {
                this.savedPaths.forEach((key, value) -> {
                    List<Path> pathList = paths.get(key);
                    value.forEach(path -> {
                        for (int i = 0 ; i < pathList.size() ; i++) {
                            Path current = pathList.get(0);
                            if (new HashSet<>(path).containsAll(current)) {
                                current.add(path.getLast());
                                break;
                            }
                        }
                    });
                });
            }
        }
    }

    private class PromotionPawnMapTwo extends PawnMap {

        public PromotionPawnMapTwo(PawnMap pawnMap) {
            // DON'T JUST PUTALL
            super(pawnMap.colour, pawnMap.pawnNumber, pawnMap.pieceNumber);
            boolean white = this.colour.equals("white");

            Map<Coordinate, Path> toAddOne = (white ? pawnMapWhite.getPawnOrigins() : pawnMapBlack.getPawnOrigins());
            toAddOne.forEach((key, value) -> getPawnOrigins().put(key, Path.of(value)));

            getCaptureSet("").putAll(pawnMap.getCaptureSet(""));
            getOriginFree().putAll(pawnMap.getOriginFree());

            this.capturedPieces = pawnMap.capturedPieces;
            Map<Coordinate, Path> toAdd = new HashMap<>();
            Path targetsFiltered = Path.of(origins.stream().filter(coordinate -> coordinate.getY() == (white ? 1 : 6)).toList());
//            //system.out.println(targetsFiltered);
            targets.stream().filter(coordinate -> coordinate.getY() == (white ? 7 : 0))
                    .forEach(c -> toAdd.put(c, Path.of(targetsFiltered.stream()
                            .filter(cTwo -> Math.abs(c.getX() - cTwo.getX()) < 7).toList())));
            toAdd.entrySet().stream().filter(entry -> !entry.getValue().isEmpty())
                    .forEach(entry -> getPawnOrigins().put(entry.getKey(), entry.getValue()));
            getCaptureSet().putAll(PromotionMap.this.captureSet);

        }


        @Override
        public void update() {
            super.update(colour);
        }

        @Override
        public int capturedPieces() {
            return super.capturedPieces(colour);
        }

        @Override
        public Map<Coordinate, Integer> getCaptureSet() {
            return super.getCaptureSet(colour);
        }
    }

    private abstract class PromotionPawnMap extends PawnMap {
        public PromotionPawnMap(String colour, PawnNumber pawnNumber, PieceNumber pieceNumber) {
            super(colour, pawnNumber, pieceNumber);
        }
        @Override
        public boolean deduce(BoardInterface board) {
            //system.out.println("Deducing...");
            if (this.maxPieces == 16) {
                int subtrahend = colour.equals("white")
                        ? PromotionMap.this.pawnMapWhite.maxPieces + PromotionMap.this.additionalCapturesWhite
                        : PromotionMap.this.pawnMapBlack.maxPieces + PromotionMap.this.additionalCapturesBlack;
                //system.out.println("subtracting :" + subtrahend);
                //system.out.println("subtracting :" + maxPieces);
                updateMaxCapturedPieces(16 - subtrahend);
            }

            super.deduce(board);
//            flip(false);


            //system.out.println("Boop:"+getPawnOrigins());
            return false;
        }

        /**
         * Flips the theoretical pawns in the pawn / origin map, deleting those without an origin
         * @param direction
         * @param colour
         */
        private void flip(boolean direction, String colour) {
//            //system.out.println("Flipping..." + getPawnOrigins());
            Map<Coordinate, Path> newOrigins = new HashMap<>();
            Path remove = new Path();
            getPawnOrigins().entrySet().stream()
//                    .filter(entry -> !entry.getValue().isEmpty())

//                    .filter(entry -> !entry.getValue().isEmpty())
                    .filter(entry -> {
                        if (entry.getValue().isEmpty()) {
                            return true;
                        }
                        return (direction ? entry.getValue().getFirst().getY() : entry.getKey().getY())
                                == (colour.equals("white") ? 7 : 0);
                    })
                    .forEach(entry -> {
                        if (entry.getValue().isEmpty()) {
                            remove.add(entry.getKey());
                        } else {
                            entry.getValue().forEach(coordinate -> {
                                if (!newOrigins.containsKey(coordinate)) {
                                    newOrigins.put(coordinate, new Path());
                                }
                                newOrigins.get(coordinate).add(entry.getKey());
                                ;

                            });
                            remove.add(entry.getKey());
                        }
                    });
            remove.forEach(coordinate -> getPawnOrigins().remove(coordinate));
            getPawnOrigins().putAll(newOrigins);
//            //system.out.println(getPawnOrigins());


        }
        @Override
        protected void rawMap(BoardInterface board, String colour) {
            Boolean white = colour.equals("white");

            Map<Coordinate, Path> toAddOne = (white ? pawnMapWhite.getPawnOrigins() : pawnMapBlack.getPawnOrigins());

            toAddOne.forEach((key, value) -> getPawnOrigins().put(key, Path.of(value)));

            Map<Coordinate, Path> toAddTwo = new HashMap<>();
//            //system.out.println(origins);
//            //system.out.println(targets);
            Path targetsFiltered = Path.of(origins.stream().filter(coordinate -> coordinate.getY() == (white ? 1 : 6)).toList());
//            //system.out.println(targetsFiltered);
            targets.stream().filter(coordinate -> coordinate.getY() == (white ? 7 : 0))
                    .forEach(c -> toAddTwo.put(c, Path.of(targetsFiltered.stream()
                            .filter(cTwo -> Math.abs(c.getX() - cTwo.getX()) < 7).toList())));
//            //system.out.println(toAdd);
            toAddTwo.entrySet().stream().filter(entry -> !entry.getValue().isEmpty())
                    .forEach(entry -> getPawnOrigins().put(entry.getKey(), entry.getValue()));


        }

        @Override
        protected void captures(Map<Coordinate, Path> origins, String colour) {

            //system.out.println("captures  1" + getPawnOrigins());
            flip(false, colour);
            //system.out.println("captures  2" + getPawnOrigins());

            ////
            updateCaptureSet(colour);
//            //system.out.println("breaks" + this.pawnOrigins);

            origins.entrySet().stream()
                    .forEach(entry -> {
                        int x = entry.getKey().getX();
                        entry.getValue().removeAll(entry.getValue().stream()
                                .filter(coordinate -> Math.abs(x - coordinate.getX()) > this.capturedPieces + getCaptureSet().get(entry.getKey()))
                                .toList());
                    });
//            //system.out.println("breaks2" + this.pawnOrigins);
            ////
            //system.out.println("captures  3" + getPawnOrigins());

            flip(true, colour);




            //system.out.println("captures  4" + getPawnOrigins());
            Map<Coordinate, Integer> newCaptures = new HashMap<>();

            getPawnOrigins().entrySet().stream()
                    .filter(entry -> entry.getKey().getY()
                    == (colour.equals("white") ? 7 : 0))
                    .forEach(entry -> {
                        Coordinate entryKey = entry.getKey();
                        entry.getValue().stream().forEach(coordinate -> {

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

            //system.out.println("captures  5" + getPawnOrigins());


        }

        @Override
        protected boolean reduceIter(Set<Coordinate> set, List<Coordinate> origins) {
//            //system.out.println("reduce  1" + getPawnOrigins());

            flip(false, this.colour);
            boolean reduction = super.reduceIter(set, origins);
            flip(true, this.colour);
//            //system.out.println("reduce  2" + getPawnOrigins());

            return reduction;

        }


        }
    private class PromotionPawnMapWhite extends PromotionPawnMap {
        public PromotionPawnMapWhite() {
            super("white", PromotionMap.this.pawnNumber, PromotionMap.this.pieceNumber);
        }


        @Override
        public void update() {
            super.update("white");
        }

        @Override
        public int capturedPieces() {
            return super.capturedPieces("white");
        }

        @Override
        public Map<Coordinate, Integer> getCaptureSet() {
            return super.getCaptureSet("white");
        }

    }

    private class PromotionPawnMapBlack extends PromotionPawnMap {
        public PromotionPawnMapBlack() {
            super("black", PromotionMap.this.pawnNumber, PromotionMap.this.pieceNumber);

        }

        @Override
        public void update() {
            super.update("black");
        }

        @Override
        public int capturedPieces() {
            return super.capturedPieces("black");
        }

        @Override
        public Map<Coordinate, Integer> getCaptureSet() {
            return super.getCaptureSet("black");
        }

    }

    private class TheoreticalPawnMap extends PawnMap {

        public TheoreticalPawnMap(String colour) {
            super(colour, PromotionMap.this.pawnNumber, PromotionMap.this.pieceNumber);
            this.maxPieces = colour.equals("white")
                    ? PromotionMap.this.pawnMapWhite.maxPieces
                    : PromotionMap.this.pawnMapBlack.maxPieces;
            //system.out.println("MAXMAX" + maxPieces);

        }

        public boolean captures() {
            int savedMaxPieces = this.maxPieces;
            super.updateCaptureSet(this.colour);
            //system.out.println(this.maxPieces);
            //system.out.println(savedMaxPieces);
            //system.out.println(getPawnOrigins());
            return this.capturedPieces > 0;
        }

        public void reduce(Map<Coordinate, Path> pawnOrigins) {
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
//                    //system.out.println("change" + getPawnOrigins());
//                    captures(getPawnOrigins(), colour);

                    change = reduceIter(new HashSet<>(), originsTwo);

                }
            }
        }

        @Override
        protected boolean reduceIterHelperStart(Map<Coordinate, Path> map) {
            return false;
        }

        @Override
        public void update() {
            super.update(this.colour);
        }

        @Override
        public int capturedPieces() {
            return super.capturedPieces(this.colour);
        }

        @Override
        public Map<Coordinate, Integer> getCaptureSet() {
            return super.getCaptureSet(this.colour);
        }
    }

}
