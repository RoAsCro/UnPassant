package StandardChess;

import java.util.Map;
import java.util.TreeMap;

public class StandardPieceFactory implements PieceFactory {


    private static Map<String, Piece> pieces = new TreeMap<>();
    private static PieceFactory instance;

    public static PieceFactory getInstance() {
        if (instance == null) {
            instance = new StandardPieceFactory();
        }
        return instance;
    }

    @Override
    public Piece getPiece(String type) throws IllegalArgumentException {
        return pieces.get(type);
    }

    public static void register(String key, Piece value) {
        pieces.put(key, value);
    }
}
