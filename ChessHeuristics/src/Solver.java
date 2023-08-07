import Heuristics.BoardInterface;
import StandardChess.ChessBoard;
import StandardChess.Coordinate;
import StandardChess.StandardPieceFactory;

import java.util.LinkedList;

public class Solver {

    String originalBoard;
    LinkedList<String> fens;
    UnMoveMaker mover;
    boolean turnIsWhite;

    public void solve(ChessBoard board, int depth) {
        this.originalBoard = board.getReader().toFEN();
    }

    public boolean makeMove(ChessBoard board, Coordinate origin, Coordinate target, String piece) {
        UnMoveMaker moveMaker = new UnMoveMaker(board);
        if (!piece.equals("")) {
            moveMaker.setCaptureFlag(true);
            moveMaker.setCapturePiece(StandardPieceFactory.getInstance()
                    .getPiece(board.getTurn().equals("white") ? piece.toLowerCase() : piece.toUpperCase()));
        }
        return moveMaker.makeUnMove(origin, target) && StateDetectorFactory.getDetector(board).testState();
    }

}
