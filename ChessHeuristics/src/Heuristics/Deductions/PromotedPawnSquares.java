package Heuristics.Deductions;

import Heuristics.BoardInterface;
import Heuristics.Path;
import StandardChess.Coordinate;

import java.util.*;
import java.util.stream.Collectors;

import static Heuristics.Deductions.PiecePathFinderUtil.PATH_DEVIATION;
import static Heuristics.HeuristicsUtil.*;

/**
 * PromotedPawnSquares is a Deduction that, for every pawn that has promoted and seemingly no longer on the board,
 * creates Paths from pawn origins that are not already accounted for to promotion squares.
 * The state is set to false if a promoted-and-absent pawn has no valid Path from an origin to a promotion square.
 * PromotedPawnSquares must only be run after the pawns and pieces have been mapped, promoted pieces
 * determined and their Paths to their current location from pawn origins found,
 * and the maximum number of captures possible by pawns determined, as well as what missing pawns must have been
 * promoted, otherwise it will not be accurate.
 */
public class PromotedPawnSquares extends AbstractDeduction{
    public PromotedPawnSquares() {
        super("Promoted piece cannot reach a promotion square.");
    }

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
            List<Path> whitePaths = getCaptures(!white, tempPaths, board);

            if (whitePaths.size() < this.detector.getCaptureData().getPawnsCapturedByPawns(white)) {
                this.state = false;
                return;
            }
            //
            // If promotions start failing it's probably because of the bit commented out below
            // that method has been removed and the part of UnCastle that uses it too
            whitePaths.forEach(p -> {
                if (!this.detector.getPawnData().getPawnPaths(white).containsKey(p.getLast())) {
                    this.detector.getPawnData().getPawnPaths(white).put(p.getLast(), new LinkedList<>());
                }
                this.detector.getPawnData().getPawnPaths(white).get(p.getLast()).add(p);
            });
//            this.detector.getPromotionPaths(white).addAll(whitePaths);
            //
            String pawn = "pawn" + (white ? "w" : "b");
            whitePaths.forEach(path -> {
                if (!this.detector.getPromotionData().getPromotionNumbers().containsKey(pawn)) {
                    this.detector.getPromotionData().getPromotionNumbers().put(pawn, new HashMap<>());
                }
                this.detector.getPromotionData().getPromotionNumbers().get(pawn).put(path, 0);
            });
        }
    }

    private Path findEmptyOrigins(boolean white) {
        Path promotedPawns = Path.of(this.detector.getCaptureData().getNonPawnCaptures(white)
                .stream().filter(c -> c.getY() == (white ? WHITE_PAWN_Y : BLACK_PAWN_Y))
                .collect(Collectors.toList()));
        return Path.of(promotedPawns.stream().filter(c -> (this.detector.getPawnData().getPawnPaths(white))
                    .values().stream()
                    .flatMap(l -> l.stream().map(LinkedList::getFirst))
                    .noneMatch(c::equals))
                .toList());
    }

    private List<Path> pathToFinalRank(BoardInterface board, boolean notWhite, Path origins) {
        //System.out.println(origins);
        int captures = (this.detector.getCaptureData().pawnTakeablePieces(!notWhite) - board.getBoardFacts().pieceNumbers(notWhite))
                - (this.detector.getPawnData().minimumPawnCaptures(!notWhite));
        int enemyCaptures = (this.detector.getCaptureData().pawnTakeablePieces(notWhite) - board.getBoardFacts().pieceNumbers(!notWhite))
                - this.detector.getPawnData().minimumPawnCaptures(notWhite);
        List<Path> paths = new LinkedList<>();
        PiecePathFinderUtil pathFinderUtil = new PiecePathFinderUtil(this.detector);
        List<Map.Entry<Coordinate, List<Path>>> forbidden = this.detector.getPawnData().getPawnPaths(notWhite).entrySet()
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

    private List<Path> getCaptures(boolean notWhite, List<Path> paths, BoardInterface boardInterface) {
        int captures = (this.detector.getCaptureData().pawnTakeablePieces(!notWhite) - (boardInterface.getBoardFacts().pieceNumbers(notWhite)))
                - (this.detector.getPawnData().minimumPawnCaptures(!notWhite));
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
