import StandardChess.ChessBoard;
import StandardChess.Coordinate;

import java.util.LinkedList;
import java.util.List;

public class SolverRunner implements Runnable {

    private Solver solver;
    private final ChessBoard board;
    private final Coordinate origin;
    private final String currentState;
    private final boolean any;
    private List<String> states = new LinkedList<>();

    SolverRunner(Solver solver, ChessBoard board, Coordinate origin, String currentState, boolean any) {

        this.solver = solver;
        this.board = board;
        this.origin = origin;
        this.currentState = currentState;
        this.any = any;
    }
    @Override
    public void run() {
        this.states = this.solver.iterateThroughMoves(this.board, this.origin, this.currentState, this.any);
    }
    public List<String> getStates() {
        return this.states;
    }

}
