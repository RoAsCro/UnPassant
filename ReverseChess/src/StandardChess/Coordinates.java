package StandardChess;

public class Coordinates {

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







    public static Coordinate add(Coordinate origin, Coordinate target) {
        return new Coordinate(origin.getX() + target.getX(), origin.getY() + target.getY());
    }

}
