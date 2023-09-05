package Heuristics.Detector;

import Heuristics.BoardInterface;
import Heuristics.HeuristicsUtil;
import Heuristics.Path;
import StandardChess.Coordinate;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * An implementation of DetectorInterface, implementing all its methods plus a human-readable toString method.
 * <p></p>
 * StandardDetectorInterface is meant to work specifically with the implementation of StandardStateDetector.
 * @author Roland Crompton
 */
public class StandardDetectorInterface implements DetectorInterface {
    /**The StateDetector the DetectorInterface represents*/
    private final StandardStateDetector detector;

    /**
     * A constructor that takes a StateDetector as the StateDetector the DetectorInterface will represent.
     * @param detector the StateDetector to be represented
     */
    public StandardDetectorInterface(StandardStateDetector detector) {
        this.detector = detector;
    }

    /**
     * Returns whether, according to the StateDetector, the king of the given colour can castle on the given side.
     * @param white the colour of the king true if white, false if black
     * @param queen the side of the rook, true if queenside, false if kingside
     * @return whether the king can castle on the specified side
     */
    @Override
    public boolean canCastle(boolean white, boolean queen) {
        if (this.detector.getPieceData().getKingMovement(white)) {
            return false;
        }
        return !this.detector.getPieceData().getRookMovement(white, queen);
    }

    /**
     * Provides a Map of promotions made by the given player, in the format:
     * <p></p>
     * [s, [p, i]]
     * <p></p>
     * Where s is a String describing the piece's type, as well as whether it's light or dark if it's a bishop,
     * p is a Path containing the Coordinates of all pieces that may be promoted of that type,
     * and i is the number of those pieces that must be promoted.
     * For promoted pieces of unknown type and location, a potential promotion square is listed instead.
     * @param white the player whose promotions are to be returned, true if white, false if black
     * @return a Map of promotions made by the given player
     */
    @Override
    public Map<String, Map<Path, Integer>> getPromotions(boolean white) {
        List<Map.Entry<String, Map<Path, Integer>>> entryList =
                detector.getPromotionData().getPromotionNumbers().entrySet().stream()
                .filter(e -> e.getKey().endsWith(white ? "w" : "b"))
                .filter(e -> !e.getValue().isEmpty())
                .toList();
        entryList = entryList.stream().filter(e -> !e.getValue().isEmpty()).toList();
        return entryList.stream()
                .collect(Collectors.toMap(e -> {
                    String name = e.getKey();
                    name = name.substring(0, name.length()-1);
                    if (name.startsWith("bishop")) {
                        name = name.substring(0, name.length() - 1) + (name.endsWith("l") ? " (light)" : " (dark)");
                    } else if (name.startsWith("pawn")) {
                        name = "Unknown (promotion squares)";
                    }
                    return name;
                }, e ->
                    e.getValue()
                            .entrySet().stream()
                            .filter(e1 -> e1.getKey() != null)
                            .filter(e1 -> !e1.getKey().equals(new Path()))
                            .collect(Collectors.toMap(e1 -> Path.of(e1.getKey()),
                                    e1 -> e1.getKey().size() - e1.getValue()))));
    }

    /**
     * Provides a Map of Coordinates of pawns of the given colour on the board and potential Paths from origins
     * they took to reach those Coordinates.
     * @param white the colour of the pawns, true if white, false if black
     * @return a Map of pawns and their potential Paths
     */
    @Override
    public Map<Coordinate, Path> getPawnMap(boolean white) {
        return this.detector.getPawnData().getPawnPaths(white).entrySet()
                .stream().collect(Collectors.toMap(Map.Entry::getKey,
                        e -> Path.of(e.getValue().stream().map(Path::getFirst).toList())));
    }

    /**
     * Returns a Path of Coordinates of non-pawn piece origins of the given colour which are caged.
     * @param white the colour of the pieces, true if white, false if black
     * @return a Path of Coordinates of non-pawn piece origins of the given colour which are caged
     */
    @Override
    public Path getCages(boolean white){
        return Path.of(this.detector.getPieceData().getCaged().entrySet()
                .stream()
                .filter(Map.Entry::getValue)
                .filter(e -> e.getKey().getY() ==(white ? HeuristicsUtil.FIRST_RANK_Y : HeuristicsUtil.FINAL_RANK_Y))
                .map(Map.Entry::getKey)
                .toList());
    }
    /**
     * Returns a Path of Coordinates of missing pieces of the given colour that were not captured
     * by pawns of the opposing colour.
     * @param white the colour of the pieces captured, true if white, false if black
     * @return a Path of Coordinates of missing pieces of the given colour that were not captured
     * by pawns of the opposing colour
     */
    @Override
    public Path getPiecesNotCapturedByPawns(boolean white) {
        return Path.of(this.detector.getCaptureData().getNonPawnCaptures(white));
    }

    /**
     * Has the StateDetector test the state of the given board interface and return whether it is valid.
     * @param boardInterface the board to be checked
     * @return whether the state is valid
     */
    @Override
    public boolean testState(BoardInterface boardInterface) {
        return this.detector.testState(boardInterface);
    }

    /**
     * Has the StateDetector test the state of the stored board interface and return whether it is valid.
     * @return whether the state is valid
     */
    @Override
    public boolean testState() {
        return this.detector.testState();
    }

    /**
     * Returns the current state of the StateDetector. Will be false if a board has been tested and found invalid.
     * @return the current state of the StateDetector
     */
    @Override
    public boolean getState() {
        return this.detector.getState();
    }
    /**
     * Returns the StateDetector's current error message.
     * @return the StateDetector's current error message
     */
    @Override
    public String getErrorMessage() {
        return this.detector.getErrorMessage();
    }

    /**
     * Converts the information stored in the StateDetector to a human-readable String.
     * @return a human-readable sString of the information in the StateDetector
     */
    @Override
    public String toString() {

        StringBuilder stringBuilder = new StringBuilder();

        if (!this.detector.getState()) {
            stringBuilder.append("Board is not valid: ").append(this.detector.getErrorMessage()).append("\n");
        }

        stringBuilder.append("Pawn origins:\n");
        boolean white = true;
        for (int i = 0 ; i < 2 ; i++) {
            stringBuilder.append(white ? "White" : "Black").append(":\n");
            getPawnMap(white).forEach((key, value) -> {
                stringBuilder.append(key).append(": ");
                stringBuilder.append(value.stream()
                        .map(Coordinate::toString).collect(Collectors.joining(", ")));
                stringBuilder.append("\n");
            });
            stringBuilder.append("\n");
            white = false;
        }

        stringBuilder.append("Cages:\n");
        white = true;
        for (int i = 0 ; i < 2 ; i++) {
            stringBuilder.append(white ? "White" : "Black").append(":\n");
            stringBuilder.append(getCages(white).stream()
                    .map(Coordinate::toString)
                    .collect(Collectors.joining(", ")));
            stringBuilder.append("\n");
            white = false;
        }
        stringBuilder.append("\n");

        stringBuilder.append("Promotions:\n");
        white = true;
        for (int i = 0 ; i < 2 ; i++) {
            stringBuilder.append(white ? "White" : "Black").append(":\n");
            getPromotions(white).forEach((key, value) -> {
                stringBuilder.append(key).append(":\n");
                stringBuilder.append(value.entrySet()
                        .stream().map(e1 -> e1.getKey() + ", " + e1.getValue() + "\n")
                        .collect(Collectors.joining(", ")));
            });
            stringBuilder.append("\n");
            white =false;
        }

        stringBuilder.append("Castling rights :\n")
                .append("White:\n")
                .append("Queenside - ").append(canCastle(true, true)).append("; ")
                .append("Kingside - ").append(canCastle(true, false))
                .append("\nBlack:\n")
                .append("Queenside - ").append(canCastle(false, true)).append("; ")
                .append("Kingside - ").append(canCastle(false, false));
         return stringBuilder.toString();
    }
}
