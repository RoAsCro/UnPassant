package Heuristics;

import StandardChess.Coordinate;

import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedList;

/**
 * An ordered List of Coordinates extending java.util.LinkedList.
 */
public class Path extends LinkedList<Coordinate> {

    public static Path of(Coordinate ... coordinates) {
        return of(new Path(), coordinates);
    }

    public static Path of(Collection<Coordinate> path, Coordinate ... coordinates) {
        Path path2 = new Path();
        path2.addAll(path);
        path2.addAll(Arrays.asList(coordinates));
        return path2;
    }

}
