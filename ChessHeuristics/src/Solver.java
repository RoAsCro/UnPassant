import Heuristics.BoardInterface;
import Heuristics.Deductions.ImpossibleStateDetector;
import Heuristics.Observations.PawnNumber;
import Heuristics.Observations.PieceNumber;
import StandardChess.BoardBuilder;
import StandardChess.ChessBoard;
import StandardChess.Coordinate;

import java.util.LinkedList;

public class Solver {

    String originalBoard;
    LinkedList<String> fens;
    UnMoveMaker mover;

    public void solve(BoardInterface boardInterface, int depth) {
        this.originalBoard = boardInterface.getReader().toFEN();
    }

    public boolean makeMove(BoardInterface boardInterface, Coordinate origin, Coordinate target) {
        ChessBoard board = BoardBuilder.buildBoard(boardInterface.getReader().toFEN());
        UnMoveMaker moveMaker = new UnMoveMaker(board);
        return moveMaker.makeUnMove(origin, target) && StateDetectorFactory.getDetector().testState(new BoardInterface(board));
    }

}
