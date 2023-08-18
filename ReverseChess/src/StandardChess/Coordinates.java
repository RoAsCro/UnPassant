package StandardChess;

public class Coordinates {

    public static final int LOWER_ASCII_A = 97;

    public static final Coordinate NULL_COORDINATE = new NulCoordinate();

    public static final Coordinate BLACK_KING = new Coordinate(4, 7);
    public static final Coordinate BLACK_KING_ROOK = new Coordinate(7, 7);
    public static final Coordinate BLACK_QUEEN_ROOK = new Coordinate(0, 7);
    public static final Coordinate WHITE_KING = new Coordinate(4, 0);
    public static final Coordinate WHITE_KING_ROOK = new Coordinate(7, 0);
    public static final Coordinate WHITE_QUEEN_ROOK = new Coordinate(0, 0);
    public static final Coordinate UP = new Coordinate(0, 1);
    public static final Coordinate DOWN = new Coordinate(0, -1);
    public static final Coordinate LEFT = new Coordinate(-1, 0);
    public static final Coordinate RIGHT = new Coordinate(1, 0);
    public static final Coordinate UP_RIGHT = new Coordinate(1, 1);
    public static final Coordinate DOWN_RIGHT = new Coordinate(1, -1);
    public static final Coordinate DOWN_LEFT = new Coordinate(-1, -1);
    public static final Coordinate UP_LEFT = new Coordinate(-1, 1);
    public static final Coordinate[] KNIGHT_DIRECTIONS = new Coordinate[] {new Coordinate(1, 2),
            new Coordinate(1, -2), new Coordinate(-1, 2), new Coordinate(-1, -2),
            new Coordinate(2, 1), new Coordinate(-2, 1), new Coordinate(2, -1),
            new Coordinate(-2, -1)};

    public static final int MAX_SIZE = 8;
    public static final int MIN_SIZE = 0;




    public static boolean inBounds(Coordinate coordinate) {
        return coordinate.getX() < MAX_SIZE && coordinate.getX() >= MIN_SIZE
                && coordinate.getY() < MAX_SIZE && coordinate.getY() >= MIN_SIZE;
    }



    public static Coordinate add(Coordinate origin, Coordinate target) {
        return new Coordinate(origin.getX() + target.getX(), origin.getY() + target.getY());
    }

    public static String readableString(Coordinate coordinate) {
        return ((char) (coordinate.getX() + LOWER_ASCII_A)) + "" + ((int) (coordinate.getY() + 1));
    }

    public static boolean light(Coordinate coordinate) {
        return (coordinate.getX() + coordinate.getY()) % 2 != 0;
    }


    private static class NulCoordinate extends Coordinate {

        public NulCoordinate() {
            super(-1, -1);
        }

        @Override
        public boolean equals(Object o) {
            return o instanceof NulCoordinate;
        }

    };


}
