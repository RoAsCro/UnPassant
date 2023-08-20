package StandardChess;


import java.util.List;
import java.util.function.Consumer;
import java.util.function.Predicate;

/**
 * A BoardReader that works with a StandardBoard.
 * @author Roland Crompton
 */
public class StandardBoardReader implements BoardReader {
    /**The ASCII value of lowercase a*/
    private static final int ASCII_LOWERCASE_A = 97;
    /**A List of the directions a knight can move in*/
    private static final List<Coordinate> KNIGHT_DIRECTIONS = List.of(Coordinates.KNIGHT_DIRECTIONS);
    /**The standard directions a Piece can move in*/
    private static final List<Coordinate> STANDARD_DIRECTIONS = List.of(
            Coordinates.UP, Coordinates.DOWN, Coordinates.LEFT,
            Coordinates.RIGHT, Coordinates.UP_RIGHT, Coordinates.UP_LEFT,
            Coordinates.DOWN_LEFT, Coordinates.DOWN_RIGHT);
    /**The StandardBoard that this BoardReader iterates over*/
    private final StandardBoard board;
    /**The current Coordinate location of the BoardReader*/
    private Coordinate current = new Coordinate(0, 0);

    /**
     * Constructs a BoardReader over the given StandardBoard.
     * @param board the StandardBoard this BoardReader is to iterate over
     */
    public StandardBoardReader(StandardBoard board) {
        this.board = board;
    }

    /**
     * Outputs the ChessBoard as a FEN.
     * @return a FEN representation of the ChessBoard
     */
    @Override
    public String toFEN() {
        StringBuilder builder = new StringBuilder();
        for (int y = ChessBoard.LENGTH - 1; y >= 0; y--) {
            int[] counter = new int[1];
            counter[0] = 0;
            to(new Coordinate(0, y));
            nextWhile(Coordinates.RIGHT,
                    c -> hasNext(Coordinates.RIGHT),
                    p -> {
                String type = p.getType();
                if (type.equals("null")) {
                    counter[0]++;
                } else {
                    if (counter[0] != 0) {
                        builder.append(counter[0]);
                        counter[0] = 0;
                    }
                    String casedType = p.getColour().equals("white")
                            ? type.toUpperCase()
                            : type.toLowerCase();
                    builder.append(casedType.charAt(type.equals("knight")
                            ? 1
                            : 0));
                }});
            if (counter[0] != 0) {
                builder.append(counter[0]);
            }
            if (y != 0) {
                builder.append("/");
            }
        }

        builder.append(" ")
                .append(this.board.getTurn().charAt(0))
                .append(" ");

        String white = "white";
        String black = "black";
        String king = "king";
        String queen = "queen";
        if (!(this.board.canCastle(king, white) || this.board.canCastle(king, black)
                || this.board.canCastle(queen, white) || this.board.canCastle(queen, black))) {
            builder.append("-");
        } else {
            if (this.board.canCastle(king, white)) {
                builder.append("K");
            }
            if (this.board.canCastle(queen, white)) {
                builder.append("Q");
            }
            if (this.board.canCastle(king, black)) {
                builder.append("k");
            }
            if (this.board.canCastle(queen, black)) {
                builder.append("q");
            }
        }
        builder.append(" ");
        if (this.board.getEnPassant().equals(Coordinates.NULL_COORDINATE)) {
            builder.append("-");
        } else {
            builder.append((char) (this.board.getEnPassant().getX() + ASCII_LOWERCASE_A))
                    .append(this.board.getEnPassant().getY() + 1);
        }
        return builder.toString();
    }

    /**
     * Returns whether the specified Coordinate is in check.
     * @param kingLocation the Coordinate location of the king being checked
     * @return true if the king at the given location is in check
     */
    @Override
    public boolean inCheck(Coordinate kingLocation) {
        Predicate<Coordinate> canAttackKing =
                c -> this.board.at(this.current).tryMove(this.current, kingLocation, this.board);
        for (Coordinate currentDirection : STANDARD_DIRECTIONS) {
            to(Coordinates.add(kingLocation, currentDirection));
            nextWhile(currentDirection,
                    c -> hasNext(currentDirection)
                            && this.board.at(this.current).getType().equals("null")
            );
            if (canAttackKing.test(kingLocation)) {
                return true;
            }
        }
        for (Coordinate currentDirection : KNIGHT_DIRECTIONS) {
            to(Coordinates.add(kingLocation, currentDirection));
            if (canAttackKing.test(kingLocation)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Returns the current location of the BoardReader.
     * @return a Coordinate of the current location of the Board Reader
     */
    @Override
    public Coordinate getCoord() {
        return this.current;
    }

    /**
     * Gets the next Coordinate in the given direction.
     * @param direction the direction to get
     * @return the Coordinate in the given direction
     */
    private Coordinate getNext(Coordinate direction) {
        return Coordinates.add(this.current, direction);
    }

    /**
     * Moves Board reader to the next Coordinate in the given direction.
     * @param direction the direction to move the BoardReader in
     * @return the Piece at the new location of the BoardReader
     */
    @Override
    public Piece next(Coordinate direction) {
        this.current = getNext(direction);
        return this.board.at(this.current);
    }

    /**
     * Returns true if the next Coordinate in the given direction is on the ChessBoard.
     * @param direction the direction to check
     * @return true if the next Coordinate in the given direction is on the ChessBoard
     */
    @Override
    public boolean hasNext(Coordinate direction) {
        Coordinate nextCoord = getNext(direction);
        return Coordinates.inBounds(nextCoord);
    }

    /**
     * Moves Board reader to the next Coordinate in the given direction while the given condition remains
     * true.
     * @param direction the direction to move the BoardReader in
     * @param condition the condition that needs to remain true for the BoardReader to continue moving
     */
    @Override
    public void nextWhile(Coordinate direction, Predicate<Coordinate> condition) {
        nextWhile(direction, condition, p -> {});
    }

    /**
     * Moves Board reader to the next Coordinate in the given direction while the given condition remains
     * true for the next Coordinate, carrying out the given function at each Piece it crosses, starting with the
     * Coordinate the BoardReader is currently at.
     * @param direction the direction to move the BoardReader in
     * @param condition the condition that needs to remain true for the BoardReader to continue moving
     * @param function the function carried out on each Piece
     */
    @Override
    public void nextWhile(Coordinate direction, Predicate<Coordinate> condition, Consumer<Piece> function) {
        function.accept(this.board.at(this.current));
        while (condition.test(getNext(direction))) {
            next(direction);
            function.accept(this.board.at(this.current));
        }
    }

    /**
     * Moves Board reader across the whole board while the given condition remains true for the next Coordinate.
     * @param condition the condition that needs to remain true for the BoardReader to continue moving
     */
    @Override
    public void wholeBoard(Predicate<Coordinate> condition) {
        wholeBoard(condition, f -> {});
    }

    /**
     * Moves Board reader across the whole board  while the given condition remains true for the next Coordinate,
     * carrying out the given function at each Piece it crosses starting with the Coordinate it's currently at.
     * @param condition the condition that needs to remain true for the BoardReader to continue moving
     * @param function the function carried out on each Piece
     */
    @Override
    public void wholeBoard(Predicate<Coordinate> condition, Consumer<Piece> function) {
        for (int y = 0 ; y < ChessBoard.LENGTH ; y++) {
            nextWhile(Coordinates.RIGHT, c -> this.current.getX() < ChessBoard.LENGTH && condition.test(c), function);
        }
    }

    /**
     * Moves the BoardReader to the given Coordinate.
     * @param target the Coordinate to move the BoardReader
     * @return the Piece at the given location
     */
    @Override
    public Piece to(Coordinate target) {
        this.current = target;
        return this.board.at(this.current);
    }
}
