import Heuristics.BoardInterface;
import StandardChess.ChessBoard;

public class CheckUtil {

    public static boolean check(BoardInterface board) {
        return !board.inCheck(board.getTurn().equals("white") ? "black" : "white");
    }
}
