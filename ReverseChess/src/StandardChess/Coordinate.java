package StandardChess;

public class Coordinate {

    private final static int BOARD_LENGTH = 8;
    private final int x;
    private final int y;


    public Coordinate(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    @Override
    public boolean equals(Object o) {
        return o instanceof Coordinate c
                && c.getX() == this.getX()
                && c.getY() == this.getY();
    }

    @Override
    public int hashCode() {
        return this.x + this.y * 9;
    }

    @Override
    public String toString() {
        return "(" + this.x + ", " + this.y + ")";
    }

}
