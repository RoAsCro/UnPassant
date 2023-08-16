package SolveAlgorithm;

import Heuristics.BoardInterface;
import StandardChess.ChessBoard;

public class CheckUtil {

    public static boolean check(BoardInterface board) {
        return !board.inCheck(board.getTurn().equals("white") ? "black" : "white");
    }
    public static boolean eitherInCheck(BoardInterface board) {
//        board.getReader().toFEN().split();
        return (board.inCheck("white") || board.inCheck("black"));
    }

    public static void switchTurns(ChessBoard board) {
        board.setTurn(board.getTurn().equals("white") ? "black" : "white");
    }
}
