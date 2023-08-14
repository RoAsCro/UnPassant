package Heuristics.Deductions;

import Heuristics.Path;
import Heuristics.StateDetector;
import StandardChess.Coordinate;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

public class UnCastle {

    private final static List<Integer> criticalXs = List.of(3, 4, 5);
    StateDetector stateDetector;
    boolean[] whiteData = new boolean[]{false, false, false};
    boolean[] blackData = new boolean[]{false, false, false};

    public void registerStateDetector(StateDetector stateDetector) {
        this.stateDetector =stateDetector;
    }
    public List<boolean[]> hasMoved() {

        this.blackData[0] = this.stateDetector.getKingMovement(false);
        this.blackData[1] = this.stateDetector.getRookMovement(false, true);
        this.blackData[2] = this.stateDetector.getRookMovement(false, false);
        this.whiteData[0] = this.stateDetector.getKingMovement(true);
        this.whiteData[1] = this.stateDetector.getRookMovement(true, true);
        this.whiteData[2] = this.stateDetector.getRookMovement(true, false);
        // Check rook posisitons

//        System.out.println(this.promotionMap.getState());
        if (this.stateDetector.getState()) {
            if (!this.blackData[0]) {
                checkPromotionMap(true);
            }
            if (!this.whiteData[0]) {
                checkPromotionMap(false);
            }
        }
        System.out.println("---");

        if (!this.blackData[0]) {
            this.stateDetector.getPromotionPaths(true)
                    .forEach(p -> checkPromotedPawns(true, p));
        }
        if (!this.whiteData[0]) {
            this.stateDetector.getPromotionPaths(false)
                    .forEach(p -> checkPromotedPawns(false, p));
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
        System.out.println("LLL");
        System.out.println(blackData[0]);
        this.stateDetector.setKingMovement(false, this.blackData[0]);
        this.stateDetector.setRookMovement(false, true, this.blackData[1]);
        this.stateDetector.setRookMovement(false, false, this.blackData[2]);
        this.stateDetector.setKingMovement(true, this.whiteData[0]);
        this.stateDetector.setRookMovement(true, true, this.whiteData[1]);
        this.stateDetector.setRookMovement(true, false, this.whiteData[2]);
        return List.of(this.whiteData, this.blackData);
    }

    private boolean[] bishops() {
        List<Coordinate> onPSquare = this.stateDetector.getPromotedPieceMap().entrySet()
                .stream()
                .filter(entry -> entry.getValue().contains(entry.getKey()))
                .map(Map.Entry::getKey)
                .toList();
        Path bishops = Path.of(this.stateDetector.getPromotionNumbers().entrySet()
                .stream()
                .filter(entry -> entry.getKey().startsWith("bi"))
                .flatMap(e -> e.getValue().keySet().stream().filter(Objects::nonNull).flatMap(Collection::stream))
                .filter(onPSquare::contains)
                .filter(c -> c.getX() == 3 || c.getX() == 5)
                .collect(Collectors.toSet()));
        boolean[] returnBooleans = new boolean[]{false, false};
        bishops.forEach(c -> {
            List<Coordinate> criticalCoords = List.of(new Coordinate(c.getX() + 1, Math.abs(c.getY() - 1)), new Coordinate(c.getX() - 1, Math.abs(c.getY() - 1)));
            if (this.stateDetector.getPawnOrigins(true).keySet().containsAll(criticalCoords)) {
                returnBooleans[0] = true;
            }
            if (this.stateDetector.getPawnOrigins(false).keySet().containsAll(criticalCoords)) {
                returnBooleans[1] = true;

            }
        });
        return returnBooleans;
    }

    private void checkPromotedPawns(boolean white, Path promotionPaths) {
        //System.out.println("??????????????");
        int y = white ? 6 : 1;
        int offSet = white ? 1 : -1;
        List<Coordinate> kingCoords = List.of(new Coordinate(3, y), new Coordinate(5, y), new Coordinate(4, y + offSet));
        List<Coordinate> rookCoords = List.of(new Coordinate(0, y + offSet), new Coordinate(7, y + offSet));
        boolean[] data = !white ? this.whiteData : this.blackData;
        System.out.println(promotionPaths);
        promotionPaths.forEach(c -> {
                if (kingCoords.contains(c)) {
                    data[0] = true;
                }
               if (rookCoords.get(0).equals(c)) {
                   data[1] = true;
               } else if (rookCoords.get(1).equals(c)) {
                   //System.out.println(white);
                   data[2] = true;
               }
        });
    }

    private void checkPromotionMap(boolean white) {
        int y = white ? 6 : 1;
        int offSet = white ? 1 : -1;
        Map<Coordinate, List<Path>> pawnPaths = this.stateDetector.getPawnPaths(white);
        List<Coordinate> criticalCoords = List.of(new Coordinate(3, y), new Coordinate(5, y), new Coordinate(4, y + offSet));
        List<Coordinate> rookCoords = List.of(new Coordinate(0, y + offSet), new Coordinate(7, y + offSet));
        boolean[] data = !white ? this.whiteData : this.blackData;
        pawnPaths.entrySet().stream()
                .filter(entry -> entry.getKey().getY() == 7  || entry.getKey().getY() == 0)
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
