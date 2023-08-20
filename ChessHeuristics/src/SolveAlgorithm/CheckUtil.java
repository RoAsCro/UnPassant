package SolveAlgorithm;

import Heuristics.BoardInterface;
import Heuristics.Detector.DetectorInterface;
import StandardChess.ChessBoard;

/**
 * A utility class with static methods to assist in checking if kings are in check.
 */
public class CheckUtil {

    /**
     * Checks if the colour whose turn it is not is in check.
     * @param board the board being checked
     * @return true if not in check, false otherwise
     */
    public static boolean check(BoardInterface board) {
        return !board.inCheck(board.getTurn().equals("white") ? "black" : "white");
    }

    /**
     * Checks if either player is in check
     * @param board the board being checked
     * @return true if either player is in check, false otherwise
     */
    public static boolean eitherInCheck(BoardInterface board) {
        return (board.inCheck("white") || board.inCheck("black"));
    }

    /**
     * Changes whose turn it is in the given ChessBoard.
     * @param board the board whose turn is being changed
     */
    public static void switchTurns(ChessBoard board) {
        board.setTurn(board.getTurn().equals("white") ? "black" : "white");
    }

    /**
     * Checks whether all the castling parameters set on the given ChessBoard match the castling parameters
     * of the given DetectorInterface
     * @param board the board being checked
     * @param detector the DetectorInterface being checked
     * @return true if the castling parameters match, false otherwise
     */
    public static boolean castleCheck(ChessBoard board, DetectorInterface detector) {
        boolean white = true;
        for (int i = 0 ; i < 2 ; i++) {
            String piece = "king";
            for (int j = 0 ; j < 2 ; j++) {
                if (board.canCastle(piece, white ? "white" : "black")) {
                    if (!detector.canCastle(white, piece.equals("queen"))) {
                        return false;
                    }
                }
                piece = "queen";
            }
            white = false;
        }
        return true;
    }
}
