package StandardChess;

public class Coordinates {

    public static Coordinate add(Coordinate origin, Coordinate target) {
        return new Coordinate(origin.getX() + target.getX(), origin.getY() + target.getY());
    }

}
