package Heuristics;

import Heuristics.BoardInterface;
import Heuristics.Deductions.*;
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
    private PromotedPawnSquares promotedPawnSquares;
    boolean[] whiteData = new boolean[]{false, false, false};
    boolean[] blackData = new boolean[]{false, false, false};

    public UnCastle(PawnMap pawnMapWhite, PawnMap pawnMapBlack, PieceMap pieceMap, PromotionMap promotionMap, PromotedPawnSquares promotedPawnSquares) {

        this.pawnMapWhite = pawnMapWhite;
        this.pawnMapBlack = pawnMapBlack;
        this.pieceMap = pieceMap;
        this.promotionMap = promotionMap;
        this.promotedPawnSquares = promotedPawnSquares;
    }

    public List<boolean[]> hasMoved() {

        this.blackData[0] = this.pieceMap.getKingMovement(false);
        this.blackData[1] = this.pieceMap.getRookMovement(false, false);
        this.blackData[2] = this.pieceMap.getRookMovement(false, true);
        this.whiteData[0] = this.pieceMap.getKingMovement(true);
        this.whiteData[1] = this.pieceMap.getRookMovement(true, false);
        this.whiteData[2] = this.pieceMap.getRookMovement(true, true);
        // Check rook posisitons

//        System.out.println(this.promotionMap.getState());
        if (this.promotionMap.getState() && this.promotionMap.getPromotionCombinedPawnMap() != null) {
            if (!this.blackData[0]) {
                checkPromotionMap(true);
            }
            if (!this.whiteData[0]) {
                checkPromotionMap(false);
            }
        }

        if (!this.blackData[0]) {

            this.promotedPawnSquares.getPromotionPaths(true)
                    .forEach(p -> checkPromotedPawns(true, p));
        }
        if (!this.whiteData[0]) {
            this.promotedPawnSquares.getPromotionPaths(false)
                    .forEach(p -> checkPromotedPawns(false, p));
        }

        return List.of(this.whiteData, this.blackData);
    }

    private void checkPromotedPawns(boolean white, Path promotionPaths) {
        int y = white ? 6 : 1;
        int offSet = white ? 1 : -1;
        List<Coordinate> kingCoords = List.of(new Coordinate(3, y), new Coordinate(5, y), new Coordinate(4, y + offSet));
        List<Coordinate> rookCoords = List.of(new Coordinate(0, y + offSet), new Coordinate(7, y + offSet));


        boolean[] data = !white ? this.whiteData : this.blackData;
        promotionPaths.forEach(c -> {
                if (kingCoords.contains(c)) {
                    data[0] = true;
                }
               if (rookCoords.get(0).equals(c)) {
                   data[1] = true;
               } else if (rookCoords.get(1).equals(c)) {
                   data[2] = true;
               }
        });
    }

    private void checkPromotionMap(boolean white) {
        PawnMap pawnMap = white ? this.pawnMapWhite : this.pawnMapBlack;
        PawnMap promoPawnMap = this.promotionMap.getPromotionPawnMap(white) ;
        int y = white ? 6 : 1;
        int offSet = white ? 1 : -1;
        Map<Coordinate, List<Path>> pawnPaths = white ? this.promotionMap.getPromotionCombinedPawnMap().getWhitePaths()
                : this.promotionMap.getPromotionCombinedPawnMap().getBlackPaths();
        List<Map.Entry<Coordinate, Path>> list = promoPawnMap.getPawnOrigins().entrySet()
                .stream().filter(entry -> !pawnMap.getPawnOrigins().containsKey(entry.getKey())).toList();
        List<Coordinate> criticalCoords = List.of(new Coordinate(3, y), new Coordinate(5, y), new Coordinate(4, y + offSet));
        List<Coordinate> rookCoords = List.of(new Coordinate(0, y + offSet), new Coordinate(7, y + offSet));

        boolean[] data = !white ? this.whiteData : this.blackData;
        pawnPaths.entrySet().stream()
                .filter(entry -> !pawnMap.getPawnOrigins().containsKey(entry.getKey()))
                .filter(entry -> entry.getValue().size() == 1)
                .forEach(entry -> {
                    entry.getValue().stream().forEach(innerEntry -> {
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
