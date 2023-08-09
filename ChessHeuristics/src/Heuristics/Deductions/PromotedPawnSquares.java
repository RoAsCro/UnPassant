package Heuristics.Deductions;

import Heuristics.BoardInterface;
import Heuristics.Observation;
import Heuristics.Observations.PieceNumber;
import Heuristics.Path;
import Heuristics.Pathfinder;
import StandardChess.Coordinate;
import StandardChess.Coordinates;
import StandardChess.StandardPieceFactory;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class PromotedPawnSquares extends AbstractDeduction{
    private PieceNumber pieceNumber;
    private final PromotionMap promotionMap;
    private final CaptureLocations cl;
    private CombinedPawnMap combinedPawnMap;
    private List<Path> whitePaths = new LinkedList<>();
    private List<Path> blackPaths = new LinkedList<>();
    private boolean promotionMapInUse = false;
    public PromotedPawnSquares(PieceNumber pieceNumber, PromotionMap promotionMap, CaptureLocations cl, CombinedPawnMap combinedPawnMap) {
        this.pieceNumber = pieceNumber;
        this.promotionMap = promotionMap;
        this.cl = cl;
        this.combinedPawnMap = combinedPawnMap;
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
            whitePaths = pathToFinalRank(board, false, emptyWhiteOrigins);
            if (whitePaths.size() < emptyWhiteOrigins.size()) {
                this.state = false;
                return false;
            }
        }
        if (!emptyBlackOrigins.isEmpty()) {
            blackPaths = pathToFinalRank(board, true, emptyBlackOrigins);
            if (blackPaths.size() < emptyBlackOrigins.size()) {
                this.state = false;
                return false;
            }
        }
        return false;
    }

    private Path findEmptyOrigins(boolean white) {
        Path promotedPawns = this.cl.getPromotedPawns(white);
        return Path.of(promotedPawns.stream().filter(c -> (!this.promotionMap.getPromotionPawnMap(white).getPawnOrigins().isEmpty()
                        ? this.promotionMap.getPromotionPawnMap(white)
                        : this.combinedPawnMap.getPawnMap(white)).getPawnOrigins()
                    .values()
                    .stream().flatMap(Path::stream)
                    .noneMatch(c::equals))
                .toList());
    }

    private List<Path> pathToFinalRank(BoardInterface board, boolean notWhite, Path origins) {

        CombinedPawnMap combinedPawnMap = this.promotionMapInUse
                ? this.combinedPawnMap
                : this.promotionMap.getPromotionCombinedPawnMap();
        int captures = (combinedPawnMap.getPawnMap(!notWhite).maxPieces - (!notWhite ? pieceNumber.getBlackPieces() : pieceNumber.getWhitePieces()))
                - combinedPawnMap.capturesTwo(notWhite ? "black" : "white");
//        System.out.println("PTR");
//        System.out.println(notWhite);
//        System.out.println(combinedPawnMap.getPawnMap(notWhite).maxPieces);
//        System.out.println(combinedPawnMap.getPawnMap(!notWhite).maxPieces);

        int enemyCaptures = (combinedPawnMap.getPawnMap(notWhite).maxPieces - (notWhite ? pieceNumber.getBlackPieces() : pieceNumber.getWhitePieces()))
                - combinedPawnMap.capturesTwo(!notWhite ? "white" : "black");
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
        int currentCaptures = 0;
//        System.out.println("XD");
//        System.out.println(forbidden);
        for (Coordinate origin : origins) {
            Path shortest = Pathfinder.findShortestPawnPath(
                    StandardPieceFactory.getInstance().getPiece(notWhite ? "p" : "P"),
                    origin,
                    (b, c) -> c.getY() == (notWhite ? 0 : 7),
                    board,
                    p -> CombinedPawnMap.PATH_DEVIATION.apply(p) <= captures,
                    (p1, p2) -> combinedPawnMap.exclusion(forbidden, p1, p2));
            if (shortest.isEmpty()) {
                return new LinkedList<>();
            }
            currentCaptures += CombinedPawnMap.PATH_DEVIATION.apply(shortest);
            if (currentCaptures > captures) {
                return new LinkedList<>();
            }
            paths.add(shortest);
        }
        return paths;
    }

    public List<Path> getPromotionPaths(boolean white) {
        return white ? this.whitePaths : this.blackPaths;
    }

}
