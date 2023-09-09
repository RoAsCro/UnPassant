package ReverseChess.StandardChess;

/**
 * A utility class implementing methods for manipulating Coordinates and various useful Coordinates.
 * @author Roland Crompton
 */
public class Coordinates {
    /**The ASCII number of lowercase A*/
    public static final int LOWER_ASCII_A = 97;
    /**Stores a reference to the NullCoordinate*/
    public static final Coordinate NULL_COORDINATE = new NulCoordinate();
    /**The Coordinate of the black king*/
    public static final Coordinate BLACK_KING = new Coordinate(4, 7);
    /**The Coordinate of the black king rook*/
    public static final Coordinate BLACK_KING_ROOK = new Coordinate(7, 7);
    /**The Coordinate of the black queen rook*/
    public static final Coordinate BLACK_QUEEN_ROOK = new Coordinate(0, 7);
    /**The Coordinate of the white king*/
    public static final Coordinate WHITE_KING = new Coordinate(4, 0);
    /**The Coordinate of the white king rook*/
    public static final Coordinate WHITE_KING_ROOK = new Coordinate(7, 0);
    /**The Coordinate of the white queen rook*/
    public static final Coordinate WHITE_QUEEN_ROOK = new Coordinate(0, 0);
    /**The Coordinate representing the direction of up on a two-dimensional plane*/
    public static final Coordinate UP = new Coordinate(0, 1);
    /**The Coordinate representing the direction of down on a two-dimensional plane*/
    public static final Coordinate DOWN = new Coordinate(0, -1);
    /**The Coordinate representing the direction of left on a two-dimensional plane*/
    public static final Coordinate LEFT = new Coordinate(-1, 0);
    /**The Coordinate representing the direction of right on a two-dimensional plane*/
    public static final Coordinate RIGHT = new Coordinate(1, 0);
    /**The Coordinate representing the direction of up-right on a two-dimensional plane*/
    public static final Coordinate UP_RIGHT = new Coordinate(1, 1);
    /**The Coordinate representing the direction of down-right on a two-dimensional plane*/
    public static final Coordinate DOWN_RIGHT = new Coordinate(1, -1);
    /**The Coordinate representing the direction of down-left on a two-dimensional plane*/
    public static final Coordinate DOWN_LEFT = new Coordinate(-1, -1);
    /**The Coordinate representing the direction of up-left on a two-dimensional plane*/
    public static final Coordinate UP_LEFT = new Coordinate(-1, 1);
    /**An array of the Coordinates representing the direction a knight can travel on a two-dimensional plane*/
    public static final Coordinate[] KNIGHT_DIRECTIONS = new Coordinate[] {new Coordinate(1, 2),
            new Coordinate(1, -2), new Coordinate(-1, 2), new Coordinate(-1, -2),
            new Coordinate(2, 1), new Coordinate(-2, 1), new Coordinate(2, -1),
            new Coordinate(-2, -1)};

    /**The maximum size of a standard chess board*/
    public static final int MAX_SIZE = 8;
    /**The minimum size of a standard chess board*/
    public static final int MIN_SIZE = 0;

    /**
     * Returns whether the Coordinate is in bounds on a standard 8x8 chess board.
     * @param coordinate the Coordinate to check
     * @return whether the Coordinate is in bounds
     */
    public static boolean inBounds(Coordinate coordinate) {
        return coordinate.getX() < MAX_SIZE && coordinate.getX() >= MIN_SIZE
                && coordinate.getY() < MAX_SIZE && coordinate.getY() >= MIN_SIZE;
    }

    /**
     * Adds two Coordinates together, return a Coordinate with the sum of both Coordinate's x's and y's.
     * @param origin the first Coordinate
     * @param target the second Coordinate
     * @return the sum of the two Coordinates
     */
    public static Coordinate add(Coordinate origin, Coordinate target) {
        return new Coordinate(origin.getX() + target.getX(), origin.getY() + target.getY());
    }

    /**
     * Converts the Coordinate to a chess notation Coordinate as a String, with the x from a-h and the y from 1-8,
     * where 0 is converted to a for x, and 0 is converted to 1 for y.
     * @param coordinate the Coordinate to be converted
     * @return the String representing the Coordinate
     */
    public static String readableString(Coordinate coordinate) {
        return ((char) (coordinate.getX() + LOWER_ASCII_A)) + "" + ((int) (coordinate.getY() + 1));
    }

    /**
     * Checks if the Coordinate is a light square on a chess board, assuming that (0, 0) / a1 is dark.
     * @param coordinate the Coordinate being tested
     * @return true if the Coordinate of a light square, false otherwise
     */
    public static boolean light(Coordinate coordinate) {
        return (coordinate.getX() + coordinate.getY()) % 2 != 0;
    }

    /**
     * A null coordinate for use in place of no Coordinate. Two NullCoordinates are equal in the equals()
     * and hashCode() functions, though not necessarily not equal to another Coordinate if it is that Coordinate's
     * equal() being called.
     */
    private static class NulCoordinate extends Coordinate {

        /**
         * Constructs a NullCoordinate.
         */
        public NulCoordinate() {
            super(-1, -1);
        }

        /**
         * Checks if a given object is a NullCoordinate.
         * @param o the object checked against
         * @return true if the Object is a NullCoordinate, false otherwise
         */
        @Override
        public boolean equals(Object o) {
            return o instanceof NulCoordinate;
        }

        /**
         * Returns a hash code of 1, ensuring no standard Coordinate can have the same hash code.
         * @return a hash code of 1
         */
        @Override
        public int hashCode() {
            return 1;
        }

    }


}
