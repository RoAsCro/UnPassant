package StandardChess;

import StandardChess.StandardPieces.*;
import java.util.Map;

public class StandardPieceFactory implements PieceFactory {


    private Map<String, PieceStrategy> pieces = Map.of(
            "p", new PawnStrategy(),
            "r", new RookStrategy(),
            "n", new KnightStrategy(),
            "b", new BishopStrategy(),
            "q", new QueenStrategy(),
            "k", new KingStrategy());
    private static PieceFactory instance;

    private StandardPieceFactory(){}

    public static PieceFactory getInstance() {
        if (instance == null) {
            instance = new StandardPieceFactory();
        }
        return instance;
    }

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

    public void register(String key, PieceStrategy value) {
        this.pieces.put(key, value);
    }
}