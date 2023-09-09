package ReverseChess.StandardChess;

import java.util.Comparator;

/**
 * A class representing a coordinate or direction on a two-dimensional plane.
 * Implements Comparable.
 * @author Roland Crompton
 */
public class Coordinate implements Comparable<Coordinate> {

    /**The Coordinate's x coordinate*/
    private final int x;
    /**The Coordinate's y coordinate*/
    private final int y;

    /**
     * Constructs a Coordinate with the given x or y Coordinate
     * @param x the x coordinate
     * @param y the y coordinate
     */
    public Coordinate(int x, int y) {
        this.x = x;
        this.y = y;
    }

    /**
     * Returns the x coordinate.
     * @return the x coordinate
     */
    public int getX() {
        return x;
    }

    /**
     * Returns the y coordinate.
     * @return the y coordinate
     */
    public int getY() {
        return y;
    }

    /**
     * Implements equals(). Two Coordinates are equal if they have the same
     * x and y coordinate.
     * @param o the object checked against
     * @return false if the object is not a Coordinate, true if the Coordinate
     * is has the same x and y
     */
    @Override
    public boolean equals(Object o) {
        return o instanceof Coordinate c
                && c.getX() == this.getX()
                && c.getY() == this.getY();
    }

    /**
     * Creates a hash code from the x and y as x + (y * 9).
     * @return a hash code made from the x and y
     */
    @Override
    public int hashCode() {
        return this.x + this.y * (ChessBoard.LENGTH + 1);
    }

    /**
     * A human-readable string representing the Coordinate in the format
     * (x, y).
     * @return a human-readable string representing the Coordinate
     */
    @Override
    public String toString() {
        return "(" + this.x + ", " + this.y + ")";
    }

    /**
     * Implements Comparator.compareTo() for Coordinate using the hash code.
     * @param o the Coordinate to be compared.
     * @return 0 if the Coordinates are the same
     */
    @Override
    public int compareTo(Coordinate o) {
        return Comparator.comparingInt(Coordinate::hashCode).compare(this, o);
    }
}
