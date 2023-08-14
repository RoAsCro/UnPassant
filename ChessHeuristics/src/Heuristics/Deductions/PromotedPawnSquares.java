package Heuristics.Deductions;

import Heuristics.BoardInterface;
import Heuristics.Path;
import StandardChess.Coordinate;

import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import static Heuristics.Deductions.PiecePathFinderUtil.PATH_DEVIATION;

public class PromotedPawnSquares extends AbstractDeduction{
    private List<Path> whitePaths = new LinkedList<>();
    private List<Path> blackPaths = new LinkedList<>();
    private boolean promotionMapInUse = false;
    public PromotedPawnSquares() {}

    @Override
    public boolean deduce(BoardInterface board) {
        this.promotionMapInUse = true;
        Path emptyWhiteOrigins = findEmptyOrigins(true);
        Path emptyBlackOrigins = findEmptyOrigins(false);

        if (!emptyWhiteOrigins.isEmpty()) {
            System.out.println("w-----------w");
            //System.out.println(emptyWhiteOrigins);

            List<Path> tempWhitePaths = pathToFinalRank(board, false, emptyWhiteOrigins);
            whitePaths = getCaptures(false, tempWhitePaths);
            //System.out.println(whitePaths);

            if (whitePaths.size() < this.detector.getPawnsCapturedByPawns(true)) {
                this.state = false;
                return false;
            }
            this.detector.getPromotionPaths(true).addAll(whitePaths);
        }

        if (!emptyBlackOrigins.isEmpty()) {
            System.out.println("b-----------b");
            //System.out.println(emptyBlackOrigins);

//            System.out.println(emptyBlackOrigins);
            List<Path> tempBlackPaths = pathToFinalRank(board, true, emptyBlackOrigins);
            //System.out.println(blackPaths);
            //System.out.println(blackPaths.size());
            blackPaths = getCaptures(true, tempBlackPaths);

            if (blackPaths.size() < this.detector.getPawnsCapturedByPawns(false)) {
                this.state = false;
                return false;
            }
            this.detector.getPromotionPaths(false).addAll(blackPaths);

            // TODO put into promotions
        }

        return false;
    }

    private Path findEmptyOrigins(boolean white) {
        Path promotedPawns = this.detector.getPromotedPawns(white);
        System.out.println("promotedPawns");
        System.out.println(promotedPawns);
        return Path.of(promotedPawns.stream().filter(c -> (this.detector.getPawnOrigins(white))
                    .values()
                    .stream().flatMap(Path::stream)
                    .noneMatch(c::equals))
                .toList());
    }

    private List<Path> pathToFinalRank(BoardInterface board, boolean notWhite, Path origins) {
        System.out.println(origins);
        int captures = (this.detector.pawnTakeablePieces(!notWhite) - (!notWhite ? this.detector.getPieceNumber().getBlackPieces() : this.detector.getPieceNumber().getWhitePieces()))
                -
                (this.detector.minimumPawnCaptures(!notWhite));
        int enemyCaptures = (this.detector.pawnTakeablePieces(notWhite) - (notWhite ? this.detector.getPieceNumber().getBlackPieces() : this.detector.getPieceNumber().getWhitePieces()))
                -
                (
//                        this.promotionMapInUse ?
                                this.detector.minimumPawnCaptures(notWhite)
//                                : this.promotionMap.getPromotionCombinedPawnMap().minimumCaptures(notWhite)
                );
        List<Path> paths = new LinkedList<>();
        PiecePathFinderUtil pathFinderUtil = new PiecePathFinderUtil(this.detector);
        List<Map.Entry<Coordinate, List<Path>>> forbidden = this.detector.getPawnPaths(notWhite).entrySet()
                .stream()
                .filter(entry -> entry.getValue().size() == 1)
                .filter(entry -> {
                    Coordinate origin = entry.getValue().get(0).getFirst();
                            return pathFinderUtil.findAllPawnPath(board, origin, enemyCaptures,
                                    (b, c) -> c.equals(entry.getKey()), !notWhite).size() == 1;
                        })
                .toList();


//        int currentCaptures = 0;
//        System.out.println("XD");
//        System.out.println(forbidden);
        List<Path> forbiddenList = forbidden.stream().flatMap(e -> e.getValue().stream()).toList();
        for (Coordinate origin : origins) {
            //System.out.println(origin);

            Path shortest =
                    pathFinderUtil.findShortestPawnPath(board, origin, captures,
                            (b, c) -> c.getY() == (notWhite ? FIRST_RANK_Y : FINAL_RANK_Y),
                            !notWhite, false, forbiddenList);
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
        int captures = (this.detector.pawnTakeablePieces(!notWhite) - (!notWhite ? this.detector.getPieceNumber().getBlackPieces() : this.detector.getPieceNumber().getWhitePieces()))
                -
                (
//                        this.promotionMapInUse ?
                        this.detector.minimumPawnCaptures(!notWhite)
//                        : this.promotionMap.getPromotionCombinedPawnMap().minimumCaptures(!notWhite)
                );
        paths.sort(Comparator.comparingInt(PATH_DEVIATION::apply));
        int currentCaptures = 0;
        List<Path> legalPaths = new LinkedList<>();
        for (Path path : paths) {
            currentCaptures += PATH_DEVIATION.apply(path);
            if (currentCaptures <= captures) {
                legalPaths.add(path);
            } else {
                return legalPaths;
            }
        }
        return legalPaths;
    }

}
