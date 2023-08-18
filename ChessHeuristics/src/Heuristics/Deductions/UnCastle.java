package Heuristics.Deductions;

import Heuristics.BoardInterface;
import Heuristics.HeuristicsUtil;
import Heuristics.Path;
import StandardChess.Coordinate;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * UnCastle is a Deduction that determines whether promoted pieces must have displaced kings or rooks in the process
 * of promoting.
 * This Deduction's state will never be false.
 * UnCastle must only be run after pawns, pieces, promoted pieces, missing promoted pieces, and paths from pawn origins
 * to promotion squares have all been determined and mapped, otherwise its results will not be accurate.
 */
public class UnCastle extends AbstractDeduction{

    private final static List<Integer> criticalXs = List.of(3, 4, 5);
    boolean[] whiteData = new boolean[]{false, false, false};
    boolean[] blackData = new boolean[]{false, false, false};

    public UnCastle() {
        super("");
    }
//    @Override
//    public void registerDetector(StateDetector detector) {
//        super.registerDetector(detector);
//        System.out.println("G");
//    }

    @Override
    public void deduce(BoardInterface board) {
        hasMoved();
    }

    public List<boolean[]> hasMoved() {
        this.blackData[0] = this.detector.getPieceData().getKingMovement(false);
        this.blackData[1] = this.detector.getPieceData().getRookMovement(false, true);
        this.blackData[2] = this.detector.getPieceData().getRookMovement(false, false);
        this.whiteData[0] = this.detector.getPieceData().getKingMovement(true);
        this.whiteData[1] = this.detector.getPieceData().getRookMovement(true, true);
        this.whiteData[2] = this.detector.getPieceData().getRookMovement(true, false);

        if (!this.blackData[0]) {
            checkPromotionMap(true);
        }
        if (!this.whiteData[0]) {
            checkPromotionMap(false);
        }
        if (!(this.blackData[0] && this.whiteData[0])) {
            boolean[] bishops = bishops();
            if (bishops[0]) {
                this.whiteData[0] = true;
            }
            if (bishops[1]) {
                this.blackData[0] = true;
            }
        }

        if (this.blackData[1] && this.blackData[2]) {
            this.blackData[0] = true;
        }
        if (this.whiteData[1] && this.whiteData[2]) {
            this.whiteData[0] = true;
        }
        this.detector.getPieceData().setKingMovement(false, this.blackData[0]);
        this.detector.getPieceData().setRookMovement(false, true, this.blackData[1]);
        this.detector.getPieceData().setRookMovement(false, false, this.blackData[2]);
        this.detector.getPieceData().setKingMovement(true, this.whiteData[0]);
        this.detector.getPieceData().setRookMovement(true, true, this.whiteData[1]);
        this.detector.getPieceData().setRookMovement(true, false, this.whiteData[2]);

        return List.of(this.whiteData, this.blackData);
    }

    /**
     * Determines if a promoted bishop that is on a promotion square had to have put the opposing king in check
     * in the course of promoting. As it was not taken, the king must therefore have moved.
     * This uses the pattern of a bishop being on d1/8 or f1/8, with opposing pawns caging it in.
     * @return whether a promoted bishop on its promotion square put an opposing king in check.
     */
    private boolean[] bishops() {
        List<Coordinate> onPSquare = this.detector.getPromotionData().getPromotedPieceMap().entrySet()
                .stream()
                .filter(entry -> entry.getValue().contains(entry.getKey()))
                .map(Map.Entry::getKey)
                .toList();
        Path bishops = Path.of(this.detector.getPromotionData().getPromotionNumbers().entrySet()
                .stream()
                .filter(entry -> entry.getKey().startsWith("bi"))
                .flatMap(e -> e.getValue().keySet().stream().filter(Objects::nonNull).flatMap(Collection::stream))
                .filter(onPSquare::contains)
                .filter(c -> c.getX() == 3 || c.getX() == 5)
                .collect(Collectors.toSet()));
        boolean[] returnBooleans = new boolean[]{false, false};
        bishops.forEach(c -> {
            List<Coordinate> criticalCoords = List.of(new Coordinate(c.getX() + 1, Math.abs(c.getY() - 1)),
                    new Coordinate(c.getX() - 1, Math.abs(c.getY() - 1)));
            if (this.detector.getPawnData().getPawnPaths(true).keySet().containsAll(criticalCoords)) {
                returnBooleans[0] = true;
            }
            if (this.detector.getPawnData().getPawnPaths(false).keySet().containsAll(criticalCoords)) {
                returnBooleans[1] = true;

            }
        });
        return returnBooleans;
    }

    private void checkPromotionMap(boolean white) {
        int y = white ? 6 : 1;
        int offSet = white ? 1 : -1;
        Map<Coordinate, List<Path>> pawnPaths = this.detector.getPawnData().getPawnPaths(white);
        List<Coordinate> criticalCoords = List.of(new Coordinate(HeuristicsUtil.QUEEN_X, y), new Coordinate(5, y), new Coordinate(4, y + offSet));
        List<Coordinate> rookCoords = List.of(new Coordinate(0, y + offSet), new Coordinate(7, y + offSet));
        boolean[] data = !white ? this.whiteData : this.blackData;
        pawnPaths.entrySet().stream()
                .filter(entry -> entry.getKey().getY() == 7  || entry.getKey().getY() == 0)
//                .filter(entry -> entry.getValue().size() == 1)
                .forEach(entry -> {
                    entry.getValue().forEach(innerEntry -> {
                        if (innerEntry.stream().anyMatch(criticalCoords::contains)) {
                            data[0] = true;
                        }
                        innerEntry.forEach(c -> {
                            if (rookCoords.get(0).equals(c)) {
                                data[1] = true;
                            } else if (rookCoords.get(1).equals(c)) {
                                data[2] = true;
                            }
                        });
                    });

                });

    }

}
