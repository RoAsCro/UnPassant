package Heuristics;

import Heuristics.BoardInterface;
import Heuristics.Deductions.CombinedPawnMap;
import Heuristics.Deductions.PawnMap;
import Heuristics.Deductions.PieceMap;
import Heuristics.Deductions.PromotionMap;
import Heuristics.Path;
import StandardChess.Coordinate;
import StandardChess.Coordinates;

import java.util.List;
import java.util.Map;

public class UnCastle {

    private final static List<Integer> criticalXs = List.of(3, 4, 5);
    private PawnMap pawnMapWhite;
    private PawnMap pawnMapBlack;
    private PieceMap pieceMap;
    private PromotionMap promotionMap;

    public UnCastle(PawnMap pawnMapWhite, PawnMap pawnMapBlack, PieceMap pieceMap, PromotionMap promotionMap) {

        this.pawnMapWhite = pawnMapWhite;
        this.pawnMapBlack = pawnMapBlack;
        this.pieceMap = pieceMap;
        this.promotionMap = promotionMap;
    }

    public boolean[] hasMoved() {
        boolean whiteKing = this.pieceMap.getKingMovement(true);
        boolean blackKing = this.pieceMap.getKingMovement(false);
        // Check rook posisitons



//        System.out.println(this.promotionMap.getState());
        if (this.promotionMap.getState() && this.promotionMap.getPromotionCombinedPawnMap() != null) {
//            System.out.println("III");
            if (!whiteKing) {
                whiteKing = checkPromotionMap(true);
            }
            if (!blackKing) {
                blackKing = checkPromotionMap(false);
            }
        }
        return new boolean[]{whiteKing, blackKing};
    }

    private boolean checkPromotionMap(boolean white) {
        PawnMap pawnMap = white ? this.pawnMapWhite : this.pawnMapBlack;
        PawnMap promoPawnMap = this.promotionMap.getPromotionPawnMap(white) ;
        int y = white ? 6 : 1;
        int offSet = white ? 1 : -1;
        Map<Coordinate, List<Path>> pawnPaths = white ? this.promotionMap.getPromotionCombinedPawnMap().getWhitePaths()
                : this.promotionMap.getPromotionCombinedPawnMap().getBlackPaths();
        List<Map.Entry<Coordinate, Path>> list = promoPawnMap.getPawnOrigins().entrySet()
                .stream().filter(entry -> !pawnMap.getPawnOrigins().containsKey(entry.getKey())).toList();
        List<Coordinate> criticalCoords = List.of(new Coordinate(3, y), new Coordinate(5, y), new Coordinate(4, y + offSet));
//        System.out.println("k");
        return (pawnPaths.entrySet().stream()
                .filter(entry -> !pawnMap.getPawnOrigins().containsKey(entry.getKey()))
                .filter(entry -> entry.getValue().size() == 1)
                .anyMatch(entry -> {
                    return entry.getValue().stream().anyMatch(innerEntry -> innerEntry.stream().anyMatch(criticalCoords::contains));
                }));
    }

}
