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

    private Path origins;
    private Path targets;

    private final PromotionPawnMapWhite promotionPawnMapWhite;
    private final PromotionPawnMapBlack promotionPawnMapBlack;

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

        System.out.println("Prior" + this.pawnMapWhite.getPawnOrigins());

//        this.captureLocations.deduce(board);

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
        System.out.println("Origun" + origins);
        System.out.println("Origun" + pawnMapWhite.getOriginFree());

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

//        pieceSquareOrigin.putAll(updates(false));


//        System.out.println("goasl " + this.goalOrigins);
//        int maxCaptures = this.pawnMapWhite.capturedPieces() - this.pawnMap.capturesTwo("white");
//        System.out.println(this.pawnMapWhite.capturedPieces());
//        System.out.println(maxCaptures);
//        int extraCaptures = this.captureLocations.getCagedCaptures(true).size();
//        System.out.println(extraCaptures);
//        this.goalOrigins.entrySet().stream()
//                .filter(entry -> {
//                    Path removals = new Path();
//                    entry.getValue().entrySet().forEach(entryTwo -> {
//                                if (entryTwo.getValue() > maxCaptures) {
//                                    removals.add(entryTwo.getKey());
//                                }
//                            });
//                    removals.forEach(c -> entry.getValue().remove(c));
//                    return entry.getValue().isEmpty();
//                        })
//                .map(Map.Entry::getKey)
//                .toList()
//                .forEach(this.goalOrigins::remove);
//        // }A
//        // Link promoted piece with origin
//        // <promoted piece type, [promotion square, origin]>
//        // B{
//        Map<Path, List<Path>> pieceSquareOrigin = new HashMap<>();
//        this.pieceMap.getPromotionNumbers().entrySet().stream()
//                .forEach(entry -> entry.getValue().keySet()
//                        .stream()
//                        .filter(Objects::nonNull)
//                        .forEach(path -> pieceSquareOrigin.putIfAbsent(path, new LinkedList<>())));
//        this.goalOrigins.entrySet().stream()
//                .forEach(entry -> {
//
//                    Path pieces = this.pieceMap.getPromotedPieceMap().get(entry.getKey());
//                    pieceSquareOrigin.keySet().stream()
//                            .filter(path -> pieces.contains(path.getFirst()))
//                            .forEach(piece -> entry.getValue().keySet()
//                                    .forEach(origin -> pieceSquareOrigin.get(piece).add(Path.of(entry.getKey(), origin))));
//                });
        // } B
        // Fail if a piece has no valid origin
        System.out.println("INFO");
        System.out.println(origins);
        System.out.println(targets);

        System.out.println(goalOrigins);

        System.out.println(pieceSquareOriginWhite);
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
        System.out.println("INFO2");
        System.out.println(promotionsWhite);
        System.out.println(whiteOrigins);
        System.out.println(pieceSquareOriginBlack);


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
                    for (int i = 0 ; i < pathIntegerMap.get(entry.getKey()) ; i++) {
                        pieceOrigin.put(entry.getKey().get(i), allOrigins);
                    }
                    return pieceOrigin.entrySet().stream();
                })
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
        Map<Coordinate, Path> pieceOriginBlack = pieceSquareOriginBlack.entrySet()
                .stream()
                .collect(Collectors.toMap(entry -> entry.getKey().getFirst(), entry -> Path.of(entry.getValue()
                        .stream().map(LinkedList::getLast).collect(Collectors.toSet()))));

        System.out.println("POW" + pieceOriginWhite);
        TheoreticalPawnMap tPMW = new TheoreticalPawnMap("white");
        tPMW.reduce(pieceOriginWhite);
        TheoreticalPawnMap tPMB = new TheoreticalPawnMap("black");
        tPMB.reduce(pieceOriginBlack);
        System.out.println("POW" + pieceOriginWhite);

        // Fail if after reduction any piece does not have enough origins
        if (!(tPMW.state && tPMB.state)) {
            this.state = false;
            return false;
        }
//        for (Map.Entry<Coordinate, Path> entry : pieceOriginWhite.entrySet()) {
//
//            if (promotionNumbers.entrySet().stream()
//                    .filter(innerEntry -> innerEntry.getKey().contains(entry.getKey()))
//                    .findAny().orElse(null)
//                    .getValue() > entry.getValue().size()){
//
//            }
//        }


        System.out.println("goasl " + this.goalOrigins);
        System.out.println(this.pieceMap.getPromotedPieceMap());
        System.out.println(pieceSquareOriginWhite);
        System.out.println(this.pawnMapWhite.capturedPieces());
        System.out.println(this.pawnMap.capturesTwo("white"));
        System.out.println(this.captureLocations.getCagedCaptures(true));
//        System.out.println(maxCaptures);





//



        System.out.println("TARGETS = " + targets);
        System.out.println("ORIGINS = " + origins);

        PromotionPawnMapTwo secondWhiteMap = new PromotionPawnMapTwo(this.pawnMapWhite);
        PromotionPawnMapTwo secondBlackMap = new PromotionPawnMapTwo(this.pawnMapBlack);
        System.out.println(this.pieceMap.getPromotedPieceMap());
        System.out.println(this.pieceMap.getPromotionNumbers());
        System.out.println("CaptureSetNew"  +this.captureSet);
        System.out.println("CaptureSetNew"  +this.pawnMapWhite.getCaptureSet());

        System.out.println("CaptureSetNew"  +secondWhiteMap.getCaptureSet());



//        this.promotionPawnMapWhite.deduce(board);
//        System.out.println(promotionPawnMapWhite.getPawnOrigins());
        System.out.println("Prior" + this.pawnMapWhite.getPawnOrigins());

        CombinedPawnMap combinedPawnMap = new PromotionCombinedPawnMap(this.promotionPawnMapWhite, this.promotionPawnMapBlack);
        System.out.println("Prior" + this.pawnMapWhite.getPawnOrigins());

        combinedPawnMap.deduce(board);
        System.out.println("Prior" + this.pawnMapWhite.getPawnOrigins());

        System.out.println("Combined:");
        System.out.println(combinedPawnMap.getWhitePaths());
        System.out.println(this.promotionPawnMapWhite.getPawnOrigins());
        System.out.println(combinedPawnMap.getBlackPaths());

        System.out.println(this.pawnMapWhite.getPawnOrigins());
        System.out.println("States:");
        System.out.println(this.promotionPawnMapBlack.state);
        System.out.println(this.promotionPawnMapWhite.state);
        System.out.println(combinedPawnMap.state);
        System.out.println(this.pawnMap.getState());
        System.out.println(this.pawnMapWhite.getState());
        System.out.println(this.pawnMapBlack.getState());
        System.out.println(priorCheck);






        this.state = this.promotionPawnMapBlack.state && this.promotionPawnMapWhite.state && combinedPawnMap.state;



//
//        this.promotionPawnMapWhite.deduce(board);

//        origins.forEach(origin -> targets.forEach(target -> Pathfinder.));
//        this.pawnMapWhite.getPawnOrigins()
        //        this.pieceMap.getPromotedPieceMap().
//        this.pawnMap.getMaxCaptures()

        return false;
    }

    private Map<Path, List<Path>> updates(boolean white) {
        System.out.println("goasl " + this.goalOrigins);
        PawnMap playersPawnMap = white ? this.pawnMapWhite : this.pawnMapBlack;
        int maxCaptures = playersPawnMap.capturedPieces() - this.pawnMap.capturesTwo(white ? "white" : "black");
        System.out.println(playersPawnMap.capturedPieces());
        System.out.println(maxCaptures);
        int extraCaptures = this.captureLocations.getCagedCaptures(white).size();
        System.out.println(extraCaptures);
        this.goalOrigins.entrySet().stream()
                .filter(entry -> entry.getKey().getY() == (white ? 7 : 0))
                .filter(entry -> {
                    Path removals = new Path();
                    entry.getValue().entrySet().forEach(entryTwo -> {
                        if (entryTwo.getValue() > maxCaptures) {
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
                        System.out.println(
                                p1 + " vs " + p2
                        );
                        List<Path> forbiddenPaths = origin.getY() == 1 || origin.getY() == 7
                                ? forbiddenBlackPaths
                                : forbiddenWhitePaths;

                        System.out.println("Fobbb" + forbiddenPaths);

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
                            System.out.println("p2 exclusive");
                            return p1;
                        }

                        if (!p1.isEmpty() && CombinedPawnMap.PATH_DEVIATION.apply(p1) < CombinedPawnMap.PATH_DEVIATION.apply(p2)) {
                            return p1;
                        }
                        System.out.println("both not exclusive");
                        return p2;
                    });
            if (!shortest.isEmpty()) {

                this.goalOrigins.putIfAbsent(origin, new HashMap<>());
                this.goalOrigins.get(origin).put(target, CombinedPawnMap.PATH_DEVIATION.apply(shortest));
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
                                        System.out.println(
                                                p1 + " vs " + p2
                                        );
                                        List<Path> forbiddenPaths = coordinate.getY() == 1 || coordinate.getY() == 7
                                                ? forbiddenBlackPaths
                                                : forbiddenWhitePaths;

                                        System.out.println("Fobbb" + forbiddenPaths);

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
                                            System.out.println("p2 exclusive");
                                            return p1;
                                        }

                                        if (!p1.isEmpty() && CombinedPawnMap.PATH_DEVIATION.apply(p1) < CombinedPawnMap.PATH_DEVIATION.apply(p2)) {
                                            return p1;
                                        }
                                        System.out.println("both not exclusive");
                                        return p2;
                                    });
                    if (!shortest.isEmpty()) {
                        System.out.println("Shortest" + shortest);
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
//            System.out.println("FLIP 1" + getWhitePaths());
//            flip(getWhitePaths(), true);
//            flip(getBlackPaths(), true);
//            System.out.println("FLIP 2" + getWhitePaths());
//
//
//            boolean returnValue = super.exclude(board, white);
//            System.out.println("FLIP 3" + getWhitePaths());
//
//            flip(getWhitePaths(), false);
//            flip(getBlackPaths(), false);
//            System.out.println("FLIP 4" + getWhitePaths());
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
//            System.out.println(targetsFiltered);
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

            super.deduce(board);
//            flip(false);


            System.out.println("Boop:"+getPawnOrigins());
            return false;
        }

        /**
         * Flips the theoretical pawns in the pawn / origin map, deleting those without an origin
         * @param direction
         * @param colour
         */
        private void flip(boolean direction, String colour) {
//            System.out.println("Flipping..." + getPawnOrigins());
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
//            System.out.println(getPawnOrigins());


        }
        @Override
        protected void rawMap(BoardInterface board, String colour) {
            Boolean white = colour.equals("white");

            Map<Coordinate, Path> toAddOne = (white ? pawnMapWhite.getPawnOrigins() : pawnMapBlack.getPawnOrigins());

            toAddOne.forEach((key, value) -> getPawnOrigins().put(key, Path.of(value)));

            Map<Coordinate, Path> toAddTwo = new HashMap<>();
//            System.out.println(origins);
//            System.out.println(targets);
            Path targetsFiltered = Path.of(origins.stream().filter(coordinate -> coordinate.getY() == (white ? 1 : 6)).toList());
//            System.out.println(targetsFiltered);
            targets.stream().filter(coordinate -> coordinate.getY() == (white ? 7 : 0))
                    .forEach(c -> toAddTwo.put(c, Path.of(targetsFiltered.stream()
                            .filter(cTwo -> Math.abs(c.getX() - cTwo.getX()) < 7).toList())));
//            System.out.println(toAdd);
            toAddTwo.entrySet().stream().filter(entry -> !entry.getValue().isEmpty())
                    .forEach(entry -> getPawnOrigins().put(entry.getKey(), entry.getValue()));


        }

        @Override
        protected void captures(Map<Coordinate, Path> origins, String colour) {

            System.out.println("captures  1" + getPawnOrigins());
            flip(false, colour);
            System.out.println("captures  2" + getPawnOrigins());

            ////
            updateCaptureSet(colour);
//            System.out.println("breaks" + this.pawnOrigins);

            origins.entrySet().stream()
                    .forEach(entry -> {
                        int x = entry.getKey().getX();
                        entry.getValue().removeAll(entry.getValue().stream()
                                .filter(coordinate -> Math.abs(x - coordinate.getX()) > this.capturedPieces + getCaptureSet().get(entry.getKey()))
                                .toList());
                    });
//            System.out.println("breaks2" + this.pawnOrigins);
            ////
            System.out.println("captures  3" + getPawnOrigins());

            flip(true, colour);




            System.out.println("captures  4" + getPawnOrigins());
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

            System.out.println("captures  5" + getPawnOrigins());


        }

        @Override
        protected boolean reduceIter(Set<Coordinate> set, List<Coordinate> origins) {
//            System.out.println("reduce  1" + getPawnOrigins());

            flip(false, this.colour);
            boolean reduction = super.reduceIter(set, origins);
            flip(true, this.colour);
//            System.out.println("reduce  2" + getPawnOrigins());

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
                    System.out.println("change" + getPawnOrigins());
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
