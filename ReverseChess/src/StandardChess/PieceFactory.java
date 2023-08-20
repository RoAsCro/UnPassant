package StandardChess;

/**
 * An interface for a factory for Pieces.
 * @author Roland Crompton
 */
public interface PieceFactory {
    /**
     * Returns a Piece of the given type, p, r, n, b, q, or k.
     * @param type the type of Piece
     * @return a Piece of the given type
     */
    Piece getPiece(String type);
}
