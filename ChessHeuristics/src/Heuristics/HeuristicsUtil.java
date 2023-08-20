package Heuristics;

import java.util.Map;

/**
 * A utility class storing various commonly used bits of data in making deductions about chess boards as static
 * constants.
 * @author Roland Crompton
 */
public class HeuristicsUtil {
    /**The standard maximum number of pawns a player can have*/
    public static final int MAX_PAWNS = 8;
    /**The standard maximum number of pieces a player can have*/
    public static final int MAX_PIECES = 16;
    /**The y Coordinate of the 8th rank*/
    public static int FINAL_RANK_Y = 7;
    /**The y Coordinate of the 1st rank*/
    public static int FIRST_RANK_Y = 0;
    /**The y Coordinate of the 7th rank*/
    public static int BLACK_PAWN_Y = 6;
    /**The y Coordinate of the 2nd rank*/
    public static int WHITE_PAWN_Y = 1;
    /**The y Coordinate of the 6th rank*/
    public static int BLACK_ESCAPE_Y = 5;
    /**The y Coordinate of the 3rd rank*/
    public static int WHITE_ESCAPE_Y = 2;
    /**The x Coordinate of the queen rook*/
    public static int Q_ROOK_X = 0;
    /**The x Coordinate of the king rook*/
    public static int K_ROOK_X = 7;
    /**The x Coordinate of the queen knight*/
    public static int Q_KNIGHT_X = 1;
    /**The x Coordinate of the king knight*/
    public static int K_KNIGHT_X = 6;
    /**The x Coordinate of the queen bishop*/
    public static int Q_BISHOP_X = 2;
    /**The x Coordinate of the king bishop*/
    public static int K_BISHOP_X = 5;
    /**The x Coordinate of the queen*/
    public static int QUEEN_X = 3;
    /**The x Coordinate of the king*/
    public static int KING_X = 4;

    /**A Map of Integers representing x coordinates and Strings of the pieces that start there*/
    public final static Map<Integer, String> STANDARD_STARTS = Map.of(
            0, "rook", 1, "knight", 2, "bishop", 3, "queen", 4,
            "king", 5, "bishop", 6, "knight", 7, "rook"
    );

    /**A Map of Strings of piece names and their corresponding code Strings for use with ReverseChess classes*/
    public final static Map<String, String> PIECE_CODES = Map.of(
            "rook", "r", "knight", "n", "bishop", "b", "queen", "q",
            "king", "k", "pawn", "p"
    );
}
