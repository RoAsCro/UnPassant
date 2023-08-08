package StandardChess;


import java.util.List;
import java.util.function.Consumer;
import java.util.function.Predicate;

/**
 * An
 *
 * @author Roland Crompton
 */
public class StandardBoardReader implements BoardReader {

    private static final int ASCII_LOWERCASE_A = 97;

    private static final List<Coordinate> STANDARD_DIRECTIONS = List.of(
            Coordinates.UP, Coordinates.DOWN, Coordinates.LEFT,
            Coordinates.RIGHT, Coordinates.UP_RIGHT, Coordinates.UP_LEFT,
            Coordinates.DOWN_LEFT, Coordinates.DOWN_RIGHT);
    private static final List<Coordinate> KNIGHT_DIRECTIONS = List.of(Coordinates.KNIGHT_DIRECTIONS);

    private final StandardBoard board;

    private Coordinate current = new Coordinate(0, 0);

    public StandardBoardReader(StandardBoard board) {
        this.board = board;
    }

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

    @Override
    public Coordinate getCoord() {
        return this.current;
    }

    private Coordinate getNext(Coordinate direction) {
        return Coordinates.add(this.current, direction);
    }

    @Override
    public Piece next(Coordinate direction) {
        this.current = getNext(direction);
        return this.board.at(this.current);
    }

    @Override
    public boolean hasNext(Coordinate direction) {
        Coordinate nextCoord = getNext(direction);
        int nextX = nextCoord.getX();
        int nextY = nextCoord.getY();
        return nextX < ChessBoard.LENGTH && nextX >= 0
                && nextY < ChessBoard.LENGTH && nextY >= 0;
    }

    @Override
    public void nextWhile(Coordinate direction, Predicate<Coordinate> condition) {
        nextWhile(direction, condition, p -> {});
    }

    @Override
    public void nextWhile(Coordinate direction, Predicate<Coordinate> condition, Consumer<Piece> function) {
        function.accept(this.board.at(this.current));
        while (condition.test(getNext(direction))) {
            next(direction);
            function.accept(this.board.at(this.current));
        }
    }

    @Override
    public void wholeBoard(Predicate<Coordinate> condition) {
        wholeBoard(condition, f -> {});
    }

    @Override
    public void wholeBoard(Predicate<Coordinate> condition, Consumer<Piece> function) {
        for (int y = 0 ; y < ChessBoard.LENGTH ; y++) {
            nextWhile(Coordinates.RIGHT, c -> this.current.getX() < ChessBoard.LENGTH && condition.test(c), function);
        }
    }

    @Override
    public Piece to(Coordinate target) {

        this.current = target;
        return this.board.at(this.current);
    }


}
