package StandardChess;

import java.util.LinkedList;

public class Path {

    LinkedList<Coordinate> path = new LinkedList<>();

    public Path(Coordinate head) {
        this.path.add(head);
    }

    public Path (Coordinate head, Coordinate vector, int distance) {
        Coordinate current = head;
        path.add(head);
        for (int i = 0 ; i < distance ; i++) {
            current = new Coordinate(current.getX() + vector.getX(), current.getY() + vector.getY());
            path.add(current);
        }
    }

}
