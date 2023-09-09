package ReverseChess.StandardChess;

import ReverseChess.StandardChess.StandardPieces.*;

import java.util.Map;

/**
 * A singleton factory for StandardPieces. PieceStrategies of the same type are
 * all a single instance.
 * @author Roland Crompton
 */
public class StandardPieceFactory implements PieceFactory {
    /**Stores the instance of the StandardPieceFactory*/
    private static PieceFactory instance;

    /**A Map storing instances to PieceStrategies*/
    private final Map<String, PieceStrategy> pieces = Map.of(
            "p", new PawnStrategy(),
            "r", new RookStrategy(),
            "n", new KnightStrategy(),
            "b", new BishopStrategy(),
            "q", new QueenStrategy(),
            "k", new KingStrategy());

    /**
     * Privatises the constructor to ensure it remains a singleton.
     */
    private StandardPieceFactory(){}

    /**
     * Gets an instance of the StandardPieceFactory, and creates one if none exists.
     * @return the instance of StandardPieceFactory
     */
    public static PieceFactory getInstance() {
        if (instance == null) {
            instance = new StandardPieceFactory();
        }
        return instance;
    }

    /**
     * Returns a Piece of the given type, p, r, n, b, q, or k.
     * @param type the type of Piece
     * @return a Piece of the given type
     * @throws IllegalArgumentException if the given type is not one stored in the factory
     */
    @Override
    public Piece getPiece(String type) throws IllegalArgumentException {
        if (!this.pieces.containsKey(type.toLowerCase())) {
            throw new IllegalArgumentException();
        }
        return new StandardPiece(Character.isLowerCase(type.charAt(0))
                ? "black"
                : "white",
                this.pieces.get(type.toLowerCase()));
    }

}
