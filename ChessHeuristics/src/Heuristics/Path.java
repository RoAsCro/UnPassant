package Heuristics;

import StandardChess.Coordinate;

import java.util.Arrays;
import java.util.LinkedList;

public class Path extends LinkedList<Coordinate> {

    public static Path of(Coordinate ... coordinates) {
        Path path = new Path();
        path.addAll(Arrays.asList(coordinates));
        return path;
    }

}
