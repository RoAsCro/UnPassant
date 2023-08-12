package SolveAlgorithm;

import StandardChess.ChessBoard;
import StandardChess.Coordinate;

import java.util.LinkedList;
import java.util.List;

public class SolverRunner implements Runnable {

    private int numberOfSolutions = 0;
    private Solver solver;
    private final ChessBoard board;
    private final Coordinate origin;
    private final String currentState;
    private final boolean any;
    private boolean finished = false;
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
        this.finished = true;
    }
    public List<String> getStates() {
        return this.states;
    }

    public boolean isFinished() {
        return finished;
    }


}
