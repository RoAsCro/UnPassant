package Heuristics.Deductions;

import Heuristics.BoardInterface;
import Heuristics.Observation;
import Heuristics.Observations.PieceNumber;
import Heuristics.Path;
import StandardChess.Coordinate;

import java.util.*;

public class PromotionMap extends AbstractDeduction {

    PieceMap pieceMap;
    CombinedPawnMap pawnMap;
    PawnMapWhite pawnMapWhite;

    PawnMapBlack pawnMapBlack;

    PieceNumber pieceNumber;

    CaptureLocations captureLocations;

    private Path origins;
    private Path targets;

    private final PromotionPawnMapWhite promotionPawnMapWhite = new PromotionPawnMapWhite();
    private final PromotionPawnMapBlack promotionPawnMapBlack = new PromotionPawnMapBlack();



    public PromotionMap(PieceMap pieceMap, CombinedPawnMap pawnMap, PawnMapWhite pawnMapWhite, PawnMapBlack pawnMapBlack, CaptureLocations captureLocations) {
        this.pieceMap = pieceMap;
        this.pawnMap = pawnMap;
        this.pawnMapWhite = pawnMapWhite;
        this.pawnMapBlack = pawnMapBlack;
        this.captureLocations = captureLocations;

    }


    @Override
    public List<Observation> getObservations() {
        return null;
    }

    @Override
    public boolean deduce(BoardInterface board) {
        this.captureLocations.deduce(board);

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

        int maxCaptures = this.pawnMapWhite.capturedPieces() - this.pawnMap.captures("white");
//        this.promotionPawnMapWhite.deduce(board);
//        System.out.println(promotionPawnMapWhite.getPawnOrigins());
        CombinedPawnMap combinedPawnMap = new PromotionCombinedPawnMap(this.promotionPawnMapWhite, this.promotionPawnMapBlack);
        combinedPawnMap.deduce(board);
        System.out.println(combinedPawnMap.getWhitePaths());
        System.out.println(combinedPawnMap.getBlackPaths());
//
//        this.promotionPawnMapWhite.deduce(board);

//        origins.forEach(origin -> targets.forEach(target -> Pathfinder.));
//        this.pawnMapWhite.getPawnOrigins()
        //        this.pieceMap.getPromotedPieceMap().
//        this.pawnMap.getMaxCaptures()

        return false;
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

    private abstract class PromotionPawnMap extends PawnMap {
        public PromotionPawnMap(String colour) {
            super(colour);
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
            getPawnOrigins().putAll(white ? pawnMapWhite.getPawnOrigins() : pawnMapBlack.getPawnOrigins());
            Map<Coordinate, Path> toAdd = new HashMap<>();
//            System.out.println(origins);
//            System.out.println(targets);
            Path targetsFiltered = Path.of(origins.stream().filter(coordinate -> coordinate.getY() == (white ? 1 : 6)).toList());
//            System.out.println(targetsFiltered);
            targets.stream().filter(coordinate -> coordinate.getY() == (white ? 7 : 0))
                    .forEach(c -> toAdd.put(c, Path.of(targetsFiltered.stream()
                            .filter(cTwo -> Math.abs(c.getX() - cTwo.getX()) < 7).toList())));
//            System.out.println(toAdd);
            toAdd.entrySet().stream().filter(entry -> !entry.getValue().isEmpty())
                    .forEach(entry -> getPawnOrigins().put(entry.getKey(), entry.getValue()));
//            System.out.println("GGG" + getPawnOrigins());
        }

        @Override
        protected void captures(Map<Coordinate, Path> origins, String colour) {
//            System.out.println("captures  1" + getPawnOrigins());
            flip(false, colour);
//            System.out.println("captures  2" + getPawnOrigins());


            super.captures(origins, colour);
//            System.out.println("captures  3" + getPawnOrigins());

            flip(true, colour);
//            System.out.println("captures  4" + getPawnOrigins());
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

        }
        @Override
        protected boolean reduceIter(Set<Coordinate> set, List<Coordinate> origins) {
            flip(false, this.colour);
            boolean reduction = super.reduceIter(set, origins);
            flip(true, this.colour);
            return reduction;
        }


        }
    private class PromotionPawnMapWhite extends PromotionPawnMap {
        public PromotionPawnMapWhite() {
            super("white");
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
            super("black");
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
}
