package Heuristics.Deductions;

import Heuristics.BoardInterface;
import Heuristics.Observation;
import Heuristics.Observations.PieceNumber;
import Heuristics.Path;
import Heuristics.Pathfinder;
import StandardChess.BoardReader;
import StandardChess.Coordinate;
import StandardChess.Coordinates;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

public class PromotionMap extends AbstractDeduction {

    PieceMap pieceMap;
    CombinedPawnMap pawnMap;
    PawnMapWhite pawnMapWhite;

    PawnMapBlack pawnMapBlack;

    PieceNumber pieceNumber;

    CaptureLocations captureLocations;

    private Path origins;
    private Path targets;

    private PawnMapWhiteTwo promotionPawnMap = new PawnMapWhiteTwo();


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
        // May have duplicates
        this.targets = Path.of(this.pieceMap.getPromotedPieceMap().entrySet()
                .stream().filter(entry -> !entry.getValue().isEmpty())
                .map(Map.Entry::getKey)
                .toList());

        int maxCaptures = this.pawnMapWhite.capturedPieces() - this.pawnMap.captures("white");
        this.promotionPawnMap.deduce(board);
        System.out.println(promotionPawnMap.getPawnOrigins());

//        origins.forEach(origin -> targets.forEach(target -> Pathfinder.));
//        this.pawnMapWhite.getPawnOrigins()
        //        this.pieceMap.getPromotedPieceMap().
//        this.pawnMap.getMaxCaptures()

        return false;
    }

    public Map<Coordinate, Path> getPawnOrigins(String colour) {
        return this.promotionPawnMap.getPawnOrigins();
    }

    private class PawnMapWhiteTwo extends PawnMap {

        public PawnMapWhiteTwo() {
            super("white");
        }

        @Override
        public boolean deduce(BoardInterface board) {
            return super.deduce(board, "white");
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

        @Override
        protected void rawMap(BoardInterface board, String colour) {
            getPawnOrigins().putAll(pawnMapWhite.getPawnOrigins());
            Map<Coordinate, Path> toAdd = new HashMap<>();
            origins.forEach(c -> toAdd.put(c, Path.of(targets.stream()
                    .filter(cTwo -> Math.abs(c.getX() - cTwo.getX()) < 7).toList())));
            toAdd.entrySet().stream().filter(entry -> !entry.getValue().isEmpty())
                    .forEach(entry -> getPawnOrigins().put(entry.getKey(), entry.getValue()));

            System.out.println(getPawnOrigins());
        }

        @Override
        protected void captures(Map<Coordinate, Path> origins, String colour) {
            super.captures(origins, colour);
            Map<Coordinate, Path> newOrigins = new HashMap<>();
            Path remove = new Path();
            getPawnOrigins().entrySet().stream().filter(entry -> entry.getValue().getFirst().getY() != 1)
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

        }

    }
}
