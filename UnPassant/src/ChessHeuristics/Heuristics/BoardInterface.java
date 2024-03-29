package ChessHeuristics.Heuristics;



import ReverseChess.StandardChess.*;

import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * An interface for ChessBoards to use with the Heuristics package, providing read-only access to information
 * and containing additional information and functions to assist in making deductions about board states.
 * @author Roland Crompton
 */
public class BoardInterface {
    /**The ChessBoard this BoardInterface wraps, initialised at instantiation*/
    private final ChessBoard board;
    /**An observer providing facts about the board state derived from the information standardly available from a
     * ChessBoard*/
    private final Observer observer = new Observer();
    /**The Coordinate of the black king, found at instantiation*/
    private Coordinate blackKing;
    /**The Coordinate of the white king, found at instantiation*/
    private Coordinate whiteKing;
    /**The board's hash code for checking against baords of equivalent legality.*/
    private final int hash;


    /**
     * A constructor taking a ChessBoard that this BoardInterface will represent.
     * Additionally, locates kings and pieces to be stored in the Observer, as well as calculating and storing the
     * hash code.
     * @param board the ChessBoard this BoardInterface will represent
     */
    public BoardInterface(ChessBoard board) {
        this.board = board;
        findKings();
        findPieces();
        correctCastling();
        this.hash = hashInit();
    }

    /**
     * Corrects bad Castling parameters given by a FEN. If a rook is not at their start location, castling for
     * that rook  is set to false.
     */
    private void correctCastling() {
        Map<Coordinate,String> rookCoords = Map.of(Coordinates.WHITE_QUEEN_ROOK, "tt",
                Coordinates.WHITE_KING_ROOK, "tf",
                Coordinates.BLACK_QUEEN_ROOK, "ft", Coordinates.BLACK_KING_ROOK, "ff");
        rookCoords.forEach((c, s) -> {
            boolean white = s.charAt(0) == 't';
            boolean queen = s.charAt(1) == 't';
            Piece rook = board.at(c);
            if (canCastle(white, queen)) {
                if (!rook.getType().equals("rook") || !rook.getColour().equals(white ? "white" : "black")) {
                    board.setCastle(queen ? "queen" : "king", white ? "white" : "black", false);
                }
            }
        });
    }

    /**
     * A function for iterating over the board depending on a given Coordinate Predicate condition to continue
     * iterating and carrying out a Coordinate Function at each Coordinate.
     * @param condition the condition for continuing
     * @param function the function carried out on the board
     */
    private void iterateOverBoard(Predicate<Coordinate> condition, Consumer<Coordinate> function) {
        BoardReader reader = this.board.getReader();
        reader.to(new Coordinate(0, 0));
        reader.nextWhile(Coordinates.RIGHT,
                c -> new WholeBoardPredicate().test(reader) && condition.test(c),
                p -> function.accept(reader.getCoord()));
    }

    /**
     * Finds every piece on the board and stores their locations in the Observer.
     */
    private void findPieces() {
        iterateOverBoard(c -> true, c -> {
            String type = this.board.at(c).getType();
            String colour = this.board.at(c).getColour();
            if (!type.equals("null")) {
                observer.put(colour.equals("white"), type, c);
            }
        });
    }

    /**
     * Find the kings and stores their locations.
     */
    private void findKings() {
        iterateOverBoard(c -> true, c -> {
            Piece p = this.board.at(c);
            if (p.getType().equals("king")) {
                if (p.getColour().equals("white")) {
                    this.whiteKing = c;
                } else {
                    this.blackKing = c;
                }
            }
        });
    }

    /**
     * Returns a String representing which colour's turn it is.
     * @return the colour as a String of which colour's turn it is, white if white, black if black
     */
    public String getTurn() {
        return this.board.getTurn();
    }

    /**
     * Returns an Observer, an object containing derived facts about the board.
     * @return this BoardInterface's Observer
     */
    public Observer getBoardFacts() {
        return this.observer;
    }

    /**
     * Returns the BoardReader that the ChessBoard this BoardInterface represents returns
     * @return the ChessBoard's BoardReader
     */
    public BoardReader getReader() {
        return board.getReader();
    }

    /**
     * Returns whether the specified Coordinate is in check. If the king is in check, it will return the
     * Coordinate of the checking piece. Otherwise, it will return the NullCoordinate.
     * @param player the colour whose king is being checked, white or black
     * @return the Coordinate of the checking piece, or the NullCoordinate if not in check.
     */
    public Coordinate inCheck(String player) {
        Coordinate king = player.equals("white") ? this.whiteKing : this.blackKing;
        if (king == null) {
            findKings();
            king = player.equals("white") ? this.whiteKing : this.blackKing;
            if (king == null) {
                return Coordinates.NULL_COORDINATE;
            }
        }
        return this.board.getReader().inCheck(king);
    }

    /**
     * Returns whether the king of the given colour can castle on at least one side.
     * For a retrograde board in UnPassant, a piece being marked as able to castle means it has never moved.
     * @param white the colour of the king, true if white, false if black
     * @return whether the king can castle on at least one side
     */
    public boolean canKingCastle(boolean white) {
        return canCastle(white, true) || canCastle(white, false);
    }

    /**
     * Returns whether the rook of the given colour and side can castle.
     * For a retrograde board in UnPassant, a piece being marked as able to castle means it has never moved.
     * @param white the colour of the rook, true if white, false if black
     * @param queenSide the side of the rook, true if queenside, false if kingside
     * @return whether the rook can castle
     */
    public boolean canCastle(boolean white, boolean queenSide) {
        return this.board.canCastle(queenSide ? "queen" : "king", white ? "white" : "black");
    }

    /**
     * Returns the BoardInterface's hash code. This is calculated based on:
     * <p>The position of pawns</p>
     * <p>The position of pieces in ranks 1, 2, 7, and 8</p>
     * <p>The number of each type of piece for each colour</p>
     * <p></p>
     * Two boards with all the above criteria the same will have the same hash code.
     * @return the hash code
     */
    @Override
    public int hashCode() {
        return this.hash;
    }

    /**
     * Checks if two boards have equivalent legality. Two boards have equivalent legality if they have the same:
     * <p>Position of pawns</p>
     * <p>Position of pieces in ranks 1, 2, 7, and 8</p>
     * <p>Number of each type of piece for each colour</p>
     * <p></p>
     * @return true if two boards have an equivalent legality
     */
    @Override
    public boolean equals(Object o) {
        if (o instanceof BoardInterface b) {
            return this.hashCode() == b.hashCode();
        }
        return false;
    }

    /**
     * Calculates the hash code.
     * @return the hash code
     */
    public int hashInit() {
        String[] FENArray = getReader().toFEN().split(" ")[0].split("/");
        String smallFEN = FENArray[0] + FENArray[1] + FENArray[6] + FENArray[7];
        String string = (this.observer.getCoordinates(true, "pawn")
                .stream().map(Coordinate::toString).collect(Collectors.joining()) +
                ":" +
                this.observer.getCoordinates(false, "pawn")
                        .stream().map(Coordinate::toString).collect(Collectors.joining()) +
                smallFEN +
                hashHelper(this.observer.getCoordinates(true, "bishop")
                        .stream().filter(Coordinates::light)
                        .toList()) +
                hashHelper(this.observer.getCoordinates(true, "bishop")
                        .stream().filter(c -> !Coordinates.light(c))
                        .toList()) +
                hashHelper(this.observer.getCoordinates(false, "bishop")
                        .stream().filter(Coordinates::light)
                        .toList()) +
                hashHelper(this.observer.getCoordinates(false, "bishop")
                        .stream().filter(c -> !Coordinates.light(c))
                        .toList()) +
                hashHelper(this.observer.getCoordinates(true, "rook")) +
                hashHelper(this.observer.getCoordinates(false, "rook")) +
                hashHelper(this.observer.getCoordinates(true, "queen")) +
                hashHelper(this.observer.getCoordinates(false, "queen")) +
                hashHelper(this.observer.getCoordinates(true, "king")) +
                hashHelper(this.observer.getCoordinates(false, "king")) +
                hashHelper(this.observer.getCoordinates(true, "knight")) +
                hashHelper(this.observer.getCoordinates(false, "knight")));
        return string.hashCode();
    }

    /**
     * Helper method for hashInit(). Converts the size of Lists of Coordinates into base 12,
     * where decimal 10 = a, and decimal 11 = b.
     * @param path the List of Coordinates whose size is to be converted. Should not be larger than size 11
     * @return a string representing the size of the List in base 12
     */
    public String hashHelper(List<Coordinate> path) {
        return switch (path.size()) {
            case 10 -> "a";
            case 11 -> "b";
            default -> "" + path.size();
        };
    }
}
