package Heuristics;

import StandardChess.Coordinate;

import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedList;

/**
 * An ordered List of Coordinates extending java.util.LinkedList.
 * @author Roland Crompton
 */
public class Path extends LinkedList<Coordinate> {

    /**
     * Constructs of Path of the given Coordinates, in the order given.
     * @param coordinates the Coordinates to be included in the Path.
     * @return a Path with the given coordinates.
     */
    public static Path of(Coordinate ... coordinates) {
        return of(new Path(), coordinates);
    }

    /**
     * Constructs a Path of all the Coordinates in the give Collection, plus the given Coordinates. The new Path
     * will store the Coordinates from the Collection first, in the order they are returned by the given Collection's
     * Iterator, then the Coordinates in the order given.
     * @param path the Collection containing the Coordinates to be included in the Path.#
     * @param coordinates the Coordinates to be included in the Path
     * @return a Path with the given Coordinates
     */
    public static Path of(Collection<Coordinate> path, Coordinate ... coordinates) {
        Path path2 = new Path();
        path2.addAll(path);
        path2.addAll(Arrays.asList(coordinates));
        return path2;
    }

}
