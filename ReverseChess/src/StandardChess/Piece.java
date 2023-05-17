package StandardChess;

public interface Piece {

    String getColour();
    String getType();
    boolean tryMove(Coordinate origin, Coordinate target);
    public Coordinate[] getMoves(Coordinate origin);



}
