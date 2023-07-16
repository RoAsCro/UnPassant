package Heuristics.Deductions;

import Heuristics.BoardInterface;
import Heuristics.Observation;
import Heuristics.Observations.PieceNumber;
import Heuristics.Path;
import StandardChess.Coordinate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

        this.pawnMap.captures("white");
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

        CombinedPawnMap combinedPawnMap = new CombinedPawnMap(this.promotionPawnMapWhite, this.promotionPawnMapBlack);
        combinedPawnMap.deduce(board);
        System.out.println(combinedPawnMap.getWhitePaths());
        System.out.println(combinedPawnMap.getBlackPaths());

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

    private abstract class PromotionPawnMap extends PawnMap {
        public PromotionPawnMap(String colour) {
            super(colour);
        }
        @Override
        public boolean deduce(BoardInterface board) {
            super.deduce(board);
            flip(false);
            return false;
        }

        private void flip(boolean direction) {
            System.out.println("Flipping..." + getPawnOrigins());
            Map<Coordinate, Path> newOrigins = new HashMap<>();
            Path remove = new Path();
            getPawnOrigins().entrySet().stream().filter(entry -> (direction ? entry.getValue().getFirst().getY() : entry.getKey().getY()) == 7)
                    .forEach(entry -> {
                        entry.getValue().forEach(coordinate -> {
                            if (!newOrigins.containsKey(coordinate)) {
                                newOrigins.put(coordinate, new Path());
                            }
                            newOrigins.get(coordinate).add(entry.getKey());;

                        });
                        remove.add(entry.getKey());
                    });
            remove.forEach(coordinate -> getPawnOrigins().remove(coordinate));
            getPawnOrigins().putAll(newOrigins);
            System.out.println(getPawnOrigins());

        }
        @Override
        protected void rawMap(BoardInterface board, String colour) {
            Boolean white = colour.equals("white");
            getPawnOrigins().putAll(white ? pawnMapWhite.getPawnOrigins() : pawnMapBlack.getPawnOrigins());
            Map<Coordinate, Path> toAdd = new HashMap<>();
            Path targetsFiltered = Path.of(targets.stream().filter(coordinate -> coordinate.getY() == (white ? 7 : 0)).toList());
            origins.stream().filter(coordinate -> coordinate.getY() == (white ? 1 : 6))
                    .forEach(c -> toAdd.put(c, Path.of(targetsFiltered.stream()
                    .filter(cTwo -> Math.abs(c.getX() - cTwo.getX()) < 7).toList())));
            toAdd.entrySet().stream().filter(entry -> !entry.getValue().isEmpty())
                    .forEach(entry -> getPawnOrigins().put(entry.getKey(), entry.getValue()));
        }

        @Override
        protected void captures(Map<Coordinate, Path> origins, String colour) {
            super.captures(origins, colour);
            flip(true);
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
