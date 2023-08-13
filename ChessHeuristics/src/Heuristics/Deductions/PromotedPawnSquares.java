package Heuristics.Deductions;

import Heuristics.BoardInterface;
import Heuristics.Observation;
import Heuristics.Observations.PieceNumber;
import Heuristics.Path;
import Heuristics.Pathfinder;
import StandardChess.Coordinate;
import StandardChess.StandardPieceFactory;

import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class PromotedPawnSquares extends AbstractDeduction{
    private PromotionMap promotionMap;
    private CombinedPawnMap combinedPawnMap;
    private List<Path> whitePaths = new LinkedList<>();
    private List<Path> blackPaths = new LinkedList<>();
    private boolean promotionMapInUse = false;
    public PromotedPawnSquares(PieceNumber pieceNumber, PieceMap pieceMap, PromotionMap promotionMap, CaptureLocations cl, CombinedPawnMap combinedPawnMap) {
        this.promotionMap = promotionMap;
        this.combinedPawnMap = combinedPawnMap;
    }

    public PromotedPawnSquares() {
    }

    @Override
    public List<Observation> getObservations() {
        return null;
    }

    @Override
    public boolean deduce(BoardInterface board) {
        this.promotionMapInUse = this.promotionMap.getPromotionCombinedPawnMap() == null;
        Path emptyWhiteOrigins = findEmptyOrigins(true);
        Path emptyBlackOrigins = findEmptyOrigins(false);

        if (!emptyWhiteOrigins.isEmpty()) {
            System.out.println("w-----------w");
            //System.out.println(emptyWhiteOrigins);

            whitePaths = pathToFinalRank(board, false, emptyWhiteOrigins);
            whitePaths = getCaptures(false, whitePaths);
            //System.out.println(whitePaths);

            if (whitePaths.size() < this.detector.getPawnsCapturedByPawns(true)) {
                this.state = false;
                return false;
            }
        }

        if (!emptyBlackOrigins.isEmpty()) {
            System.out.println("b-----------b");
            //System.out.println(emptyBlackOrigins);

//            System.out.println(emptyBlackOrigins);
            blackPaths = pathToFinalRank(board, true, emptyBlackOrigins);
            //System.out.println(blackPaths);
            //System.out.println(blackPaths.size());
            blackPaths = getCaptures(true, blackPaths);

            if (blackPaths.size() < this.detector.getPawnsCapturedByPawns(false)) {
                this.state = false;
                return false;
            }
            // TODO put into promotions
        }

        return false;
    }

    private Path findEmptyOrigins(boolean white) {
        Path promotedPawns = this.detector.getPromotedPawns(white);
        System.out.println("promotedPawns");

        System.out.println(promotedPawns);
        return Path.of(promotedPawns.stream().filter(c -> (!this.promotionMap.getPromotionPawnMap(white).getPawnOrigins().isEmpty()
                        ? this.promotionMap.getPromotionPawnMap(white).getPawnOrigins()
                        : this.detector.getPawnOrigins(white))
                    .values()
                    .stream().flatMap(Path::stream)
                    .noneMatch(c::equals))
                .toList());
    }

    private List<Path> pathToFinalRank(BoardInterface board, boolean notWhite, Path origins) {

        CombinedPawnMap combinedPawnMap = this.promotionMapInUse
                ? this.combinedPawnMap
                : this.promotionMap.getPromotionCombinedPawnMap();
        System.out.println(this.promotionMapInUse);
        int captures = (this.detector.pawnTakeablePieces(!notWhite) - (!notWhite ? this.detector.getPieceNumber().getBlackPieces() : this.detector.getPieceNumber().getWhitePieces()))
                - combinedPawnMap.minimumCaptures(!notWhite);
        int enemyCaptures = (this.detector.pawnTakeablePieces(notWhite) - (notWhite ? this.detector.getPieceNumber().getBlackPieces() : this.detector.getPieceNumber().getWhitePieces()))
                - combinedPawnMap.minimumCaptures(notWhite);
        List<Path> paths = new LinkedList<>();

        List<Map.Entry<Coordinate, List<Path>>> forbidden = (notWhite ? combinedPawnMap.getWhitePaths() : combinedPawnMap.getBlackPaths()).entrySet()
                .stream()
                .filter(entry -> entry.getValue().size() == 1)
                .filter(entry -> Pathfinder.findAllPawnPaths(
                                StandardPieceFactory.getInstance().getPiece(notWhite ? "p" : "P"),
                                entry.getValue().get(0).getFirst(),
                                (b, c) -> c.equals(entry.getKey()),
                                board,
                                p -> CombinedPawnMap.PATH_DEVIATION.apply(p) <= enemyCaptures)
                        .size() == 1)
                .toList();
//        int currentCaptures = 0;
//        System.out.println("XD");
//        System.out.println(forbidden);
        for (Coordinate origin : origins) {
            //System.out.println(origin);

            Path shortest = Pathfinder.findShortestPawnPath(
                    StandardPieceFactory.getInstance().getPiece(notWhite ? "p" : "P"),
                    origin,
                    (b, c) -> c.getY() == (notWhite ? FIRST_RANK_Y : FINAL_RANK_Y),
                    board,
                    p -> CombinedPawnMap.PATH_DEVIATION.apply(p) <= captures,
                    (p1, p2) -> combinedPawnMap.exclusion(forbidden, p1, p2));
            if (shortest.isEmpty()) {
                continue;
            }
//            currentCaptures += CombinedPawnMap.PATH_DEVIATION.apply(shortest);
//            if (currentCaptures > captures) {
//                return new LinkedList<>();
//            }
            paths.add(shortest);
        }
        return paths;
    }

    private List<Path> getCaptures(boolean notWhite, List<Path> paths) {
        CombinedPawnMap combinedPawnMap = this.promotionMapInUse
                ? this.combinedPawnMap
                : this.promotionMap.getPromotionCombinedPawnMap();
        int captures = (this.detector.pawnTakeablePieces(!notWhite) - (!notWhite ? this.detector.getPieceNumber().getBlackPieces() : this.detector.getPieceNumber().getWhitePieces()))
                - combinedPawnMap.minimumCaptures(!notWhite);
        paths.sort(Comparator.comparingInt(CombinedPawnMap.PATH_DEVIATION::apply));
        int currentCaptures = 0;
        List<Path> legalPaths = new LinkedList<>();
        for (Path path : paths) {
            currentCaptures += CombinedPawnMap.PATH_DEVIATION.apply(path);
            if (currentCaptures <= captures) {
                legalPaths.add(path);
            } else {
                return legalPaths;
            }
        }
        return legalPaths;
    }

    public List<Path> getPromotionPaths(boolean white) {
        return white ? this.whitePaths : this.blackPaths;
    }

//    private boolean[] bishops() {
//        List<Coordinate> onPSquare = this.pieceMap.getPromotedPieceMap().entrySet()
//                .stream()
//                .filter(entry -> entry.getValue().contains(entry.getKey()))
//                .map(Map.Entry::getKey)
//                .toList();
//        System.out.println(onPSquare);
//        Path bishops = Path.of(this.pieceMap.getPromotionNumbers().entrySet()
//                .stream()
//                .filter(entry -> entry.getKey().startsWith("bi"))
//                .flatMap(e -> e.getValue().keySet().stream().filter(Objects::nonNull).flatMap(Collection::stream))
//                .filter(onPSquare::contains)
//                .filter(c -> c.getX() == 3 || c.getX() == 5)
//                .collect(Collectors.toSet()));
//        boolean[] returnBooleans = new boolean[]{false, false};
//        bishops.forEach(c -> {
//            List<Coordinate> criticalCoords = List.of(new Coordinate(c.getX() + 1, Math.abs(c.getY() - 1)), new Coordinate(c.getX() - 1, Math.abs(c.getY() - 1)));
//            if (this.combinedPawnMap.getPawnMap(true).getPawnOrigins().keySet().containsAll(criticalCoords)) {
//                returnBooleans[0] = true;
//            }
//            if (this.combinedPawnMap.getPawnMap(true).getPawnOrigins().keySet().containsAll(criticalCoords)) {
//                returnBooleans[1] = true;
//
//            }
//        });
//        return returnBooleans;
//    }

}
