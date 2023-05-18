package StandardChess;

public class Coordinates {

    public static final Coordinate BLACK_KING = new Coordinate(4, 7);
    public static final Coordinate BLACK_KING_ROOK = new Coordinate(7, 7);
    public static final Coordinate BLACK_QUEEN_ROOK = new Coordinate(0, 7);
    public static final Coordinate WHITE_KING = new Coordinate(4, 0);
    public static final Coordinate WHITE_KING_ROOK = new Coordinate(7, 0);
    public static final Coordinate WHITE_QUEEN_ROOK = new Coordinate(0, 0);

    public static Coordinate add(Coordinate origin, Coordinate target) {
        return new Coordinate(origin.getX() + target.getX(), origin.getY() + target.getY());
    }

}
