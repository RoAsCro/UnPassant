package StandardChess;

import StandardChess.StandardPieces.*;

import java.util.Map;
import java.util.TreeMap;

public class StandardPieceFactory implements PieceFactory {


    private static Map<String, PieceStrategy> pieces = Map.of(
            "p", new PawnStrategy(),
            "r", new RookStrategy(),
            "n", new KnightStrategy(),
            "b", new BishopStrategy(),
            "q", new QueenStrategy(),
            "k", new KingStrategy());
    private static PieceFactory instance;

    public static PieceFactory getInstance() {
        if (instance == null) {
            instance = new StandardPieceFactory();
        }
        return instance;
    }

    @Override
    public Piece getPiece(String type) throws IllegalArgumentException {
        return new StandardPiece(Character.isLowerCase(type.charAt(0))
                ? "white"
                : "black",
                pieces.get(type.toLowerCase()));
    }

    public static void register(String key, PieceStrategy value) {
        pieces.put(key, value);
    }
}
