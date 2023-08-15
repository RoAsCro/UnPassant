package Heuristics;

import java.util.List;
import java.util.Map;

public class HeuristicsUtil {
    public static final List<String> PIECE_NAMES = List.of("pawn", "rook", "knight", "bishop", "queen", "king");
    public static final int MAX_PAWNS = 8;
    public static final int MAX_PIECES = 16;
    public static int FINAL_RANK_Y = 7;
    public static int FIRST_RANK_Y = 0;
    public static int BLACK_PAWN_Y = 6;
    public static int WHITE_PAWN_Y = 1;
    public static int BLACK_ESCAPE_Y = 5;
    public static int WHITE_ESCAPE_Y = 2;
    public static int Q_ROOK_X = 0;
    public static int K_ROOK_X = 7;
    public static int Q_KNIGHT_X = 1;
    public static int K_KNIGHT_X = 6;
    public static int Q_BISHOP_X = 2;
    public static int K_BISHOP_X = 5;
    public static int QUEEN_X = 3;
    public static int KING_X = 4;

    public final static Map<Integer, String> STANDARD_STARTS = Map.of(
            0, "rook", 1, "knight", 2, "bishop", 3, "queen", 4,
            "king", 5, "bishop", 6, "knight", 7, "rook"
    );

    public final static Map<String, String> PIECE_CODES = Map.of(
            "rook", "r", "knight", "n", "bishop", "b", "queen", "q",
            "king", "k"
    );
}
