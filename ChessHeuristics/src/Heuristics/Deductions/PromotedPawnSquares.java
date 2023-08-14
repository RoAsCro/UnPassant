package Heuristics.Deductions;

import Heuristics.BoardInterface;
import Heuristics.Path;
import StandardChess.Coordinate;

import java.util.*;

import static Heuristics.Deductions.PiecePathFinderUtil.PATH_DEVIATION;

public class PromotedPawnSquares extends AbstractDeduction{
    public PromotedPawnSquares() {}

    @Override
    public boolean deduce(BoardInterface board) {
        Path emptyWhiteOrigins = findEmptyOrigins(true);
        Path emptyBlackOrigins = findEmptyOrigins(false);

        putPaths(emptyWhiteOrigins, board, true);
        putPaths(emptyBlackOrigins, board, false);
        return false;
    }

    private void putPaths(Path emptyOrigins, BoardInterface board, boolean white)  {
        if (!emptyOrigins.isEmpty()) {

            List<Path> tempPaths = pathToFinalRank(board, !white, emptyOrigins);
            List<Path> whitePaths = getCaptures(!white, tempPaths);

            if (whitePaths.size() < this.detector.getPawnsCapturedByPawns(white)) {
                this.state = false;
                return;
            }
            this.detector.getPromotionPaths(white).addAll(whitePaths);
            String pawn = "pawn" + (white ? "w" : "b");
            whitePaths.forEach(path -> {
                if (!this.detector.getPromotionNumbers().containsKey(pawn)) {
                    this.detector.getPromotionNumbers().put(pawn, new HashMap<>());
                }
                this.detector.getPromotionNumbers().get(pawn).put(path, 0);
            });
        }
    }

    private Path findEmptyOrigins(boolean white) {
        Path promotedPawns = this.detector.getPromotedPawns(white);
        return Path.of(promotedPawns.stream().filter(c -> (this.detector.getPawnOrigins(white))
                    .values()
                    .stream().flatMap(Path::stream)
                    .noneMatch(c::equals))
                .toList());
    }

    private List<Path> pathToFinalRank(BoardInterface board, boolean notWhite, Path origins) {
        System.out.println(origins);
        int captures = (this.detector.pawnTakeablePieces(!notWhite) - (!notWhite ? this.detector.getPieceNumber().getBlackPieces() : this.detector.getPieceNumber().getWhitePieces()))
                - (this.detector.minimumPawnCaptures(!notWhite));
        int enemyCaptures = (this.detector.pawnTakeablePieces(notWhite) - (notWhite ? this.detector.getPieceNumber().getBlackPieces() : this.detector.getPieceNumber().getWhitePieces()))
                - this.detector.minimumPawnCaptures(notWhite);
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
