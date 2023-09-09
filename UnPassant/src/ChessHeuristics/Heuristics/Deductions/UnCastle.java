package ChessHeuristics.Heuristics.Deductions;

import ChessHeuristics.Heuristics.BoardInterface;
import ChessHeuristics.Heuristics.HeuristicsUtil;
import ChessHeuristics.Heuristics.Path;
import ReverseChess.StandardChess.Coordinate;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import static ChessHeuristics.Heuristics.HeuristicsUtil.*;

/**
 * UnCastle is a Deduction that determines whether promoted pieces must have displaced kings or rooks in the process
 * of promoting.
 * <p></p>
 * This Deduction's state will never be false.
 * <p></p>
 * UnCastle must only be run after pawns, pieces, promoted pieces, missing promoted pieces, and paths from pawn origins
 * to promotion squares have all been determined and mapped, otherwise its results will not be accurate.
 * @author Roland Crompton
 */
public class UnCastle extends AbstractDeduction{
    /**The array location of the king's movement*/
    private final static int KING = 0;
    /**The array location of the king rook's movement*/
    private final static int KING_ROOK = 2;
    /**The array location of the queen rook's movement*/
    private final static int QUEEN_ROOK = 1;
    /**Stores the currently discovered castling data for black, values are true if the corresponding piece has moved*/
    private final boolean[] blackData = new boolean[]{false, false, false};
    /**Stores the currently discovered castling data for white, values are true if the corresponding piece has moved*/
    private final boolean[] whiteData = new boolean[]{false, false, false};

    /**
     * A constructor setting the error message to blank.
     */
    public UnCastle() {
        super("");
    }

    /**
     * Looks over the previously acquired board data and checks whether, as a consequence of all that's been found,
     * a rook or king will have been forced to move, and update the PieceData accordingly.
     * @param board the board whose information the deduction will draw from
     */
    @Override
    public void deduce(BoardInterface board) {
        hasMoved();
    }

    /**
     * Looks over the previously acquired board data and checks whether, as a consequence of all that's been found,
     * a rook or king will have been forced to move, and update the PieceData accordingly.
     * @return a List of boolean arrays for each player representing whether that player's king, queen rook, and
     * king rook respectively must have moved during the game
     */
    public List<boolean[]> hasMoved() {
        this.blackData[KING] = this.detector.getPieceData().getKingMovement(false);
        this.blackData[QUEEN_ROOK] = this.detector.getPieceData().getRookMovement(false, true);
        this.blackData[KING_ROOK] = this.detector.getPieceData().getRookMovement(false, false);
        this.whiteData[KING] = this.detector.getPieceData().getKingMovement(true);
        this.whiteData[QUEEN_ROOK] = this.detector.getPieceData().getRookMovement(true, true);
        this.whiteData[KING_ROOK] = this.detector.getPieceData().getRookMovement(true, false);

        if (!this.blackData[KING]) {
            checkPromotionMap(true);
        }
        if (!this.whiteData[KING]) {
            checkPromotionMap(false);
        }
        if (!(this.blackData[KING] && this.whiteData[KING])) {
            boolean[] bishops = bishops();
            if (bishops[0]) {
                this.whiteData[KING] = true;
            }
            if (bishops[1]) {
                this.blackData[KING] = true;
            }
        }

        if (this.blackData[QUEEN_ROOK] && this.blackData[KING_ROOK]) {
            this.blackData[KING] = true;
        }
        if (this.whiteData[QUEEN_ROOK] && this.whiteData[KING_ROOK]) {
            this.whiteData[KING] = true;
        }
        this.detector.getPieceData().setKingMovement(false, this.blackData[KING]);
        this.detector.getPieceData().setRookMovement(false, true, this.blackData[QUEEN_ROOK]);
        this.detector.getPieceData().setRookMovement(false, false, this.blackData[KING_ROOK]);
        this.detector.getPieceData().setKingMovement(true, this.whiteData[KING]);
        this.detector.getPieceData().setRookMovement(true, true, this.whiteData[QUEEN_ROOK]);
        this.detector.getPieceData().setRookMovement(true, false, this.whiteData[KING_ROOK]);

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
                .filter(c -> c.getX() == HeuristicsUtil.QUEEN_X || c.getX() == HeuristicsUtil.K_BISHOP_X)
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

    /**
     * Checks the PawnData's pawnPaths to see if any have at any point stood on the king or one of the rooks'
     * starting squares of the opposing player, or either of the two squares at diagonals from  the king's starting
     * square, meaning, as this will put the king in check, and as the pawn must either be on the board or
     * promoted to be in the pawnPaths, the king moved.
     * @param white the colour of the pawns being checked, true if white, false if black
     */
    private void checkPromotionMap(boolean white) {
        int y = white ? HeuristicsUtil.BLACK_PAWN_Y : HeuristicsUtil.WHITE_PAWN_Y;
        int offSet = white ? 1 : -1;
        Map<Coordinate, List<Path>> pawnPaths = this.detector.getPawnData().getPawnPaths(white);
        List<Coordinate> criticalCoords = List.of(new Coordinate(HeuristicsUtil.QUEEN_X, y),
                new Coordinate(HeuristicsUtil.K_BISHOP_X, y),
                new Coordinate(HeuristicsUtil.KING_X, y + offSet));
        List<Coordinate> rookCoords = List.of(new Coordinate(Q_ROOK_X, y + offSet),
                new Coordinate(K_ROOK_X, y + offSet));
        boolean[] data = !white ? this.whiteData : this.blackData;
        pawnPaths.entrySet().stream()
                .filter(entry -> entry.getKey().getY() == FINAL_RANK_Y  || entry.getKey().getY() == FIRST_RANK_Y)
                .forEach(entry -> entry.getValue().forEach(innerEntry -> {
                    if (innerEntry.stream().anyMatch(criticalCoords::contains)) {
                        data[KING] = true;
                    }
                    innerEntry.forEach(c -> {
                        if (rookCoords.get(0).equals(c)) {
                            data[QUEEN_ROOK] = true;
                        } else if (rookCoords.get(1).equals(c)) {
                            data[KING_ROOK] = true;
                        }
                    });
                }));
    }

}
