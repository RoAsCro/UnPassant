package SolveAlgorithm;

import Heuristics.BoardInterface;
import Heuristics.Path;
import StandardChess.BoardBuilder;
import StandardChess.ChessBoard;
import StandardChess.Coordinate;
import StandardChess.Coordinates;

import java.util.LinkedList;
import java.util.List;

public class SovlverRunnerTwo implements Runnable {

    private boolean finished = false;
    Solver solver;
    private boolean legalFirst = false;
    private int additionalDepth = 2;
    private List<String> finalStates = new LinkedList<>();
    private List<String> newStates = new LinkedList<>();

    private boolean any;
    int currentDepth;
    private String state;
    int depth;
    private int numberOfSolutions = 100;

    SovlverRunnerTwo(Solver solver, boolean legalFirst, int additionalDepth, boolean any, int currentDepth, String state, int depth, int numberOfSolutions) {
        this.solver = solver;
        this.legalFirst = legalFirst;
        this.additionalDepth = additionalDepth;
        this.any = any;
        this.currentDepth = currentDepth;
        this.state = state;
        this.depth = depth;
        this.numberOfSolutions = numberOfSolutions;

    }


    public void statest(int currentDepth, String state, int depth) throws InterruptedException {
//        System.out.println("launched");
        String[] stateDescription = state.split(":");
        String currentState = stateDescription[0];
        ChessBoard currentBoard = BoardBuilder.buildBoard(currentState);
        LinkedList<String> states = new LinkedList<>();

        if (currentDepth < depth) {
            List<Coordinate> pieces = allPieces(currentBoard);
            List<SolverRunner> runnerPool = new LinkedList<>();
            for (Coordinate piece : pieces) {
                if (!currentBoard.getEnPassant().equals(Coordinates.NULL_COORDINATE)) {
                    if (!piece.equals(currentBoard.getEnPassant())) {
                        continue;
                    }
                }

                SolverRunner runner = new SolverRunner(this.solver, currentBoard, piece, state, any && currentDepth == depth - 1);
                runnerPool.add(runner);
                Thread thread = new Thread(runner);
                thread.start();
            }
            int sleepCount = 0;
            while (!runnerPool.isEmpty()) {
                sleepCount++;
                List<SolverRunner> finishedRunners = runnerPool.stream().filter(SolverRunner::isFinished).toList();
                List<String> newStates = finishedRunners.stream().flatMap(r -> r.getStates().stream()).toList();
                finishedRunners.forEach(runnerPool::remove);

                newStates.forEach(s ->
                        states.push(s.split(":")[0]
                                + ":"
                                + s.split(":")[1]
                                + (stateDescription.length > 1 ? (", "
                                + stateDescription[1]) : "")
                                + ":" + (currentDepth + 1)));
                if ((any && !states.isEmpty())  ||  (!any && states.size() >= numberOfSolutions && currentDepth + 1 == depth)) {
                    break;
                }
//                System.out.println("waiting " + sleepCount);
                Thread.sleep(100L * sleepCount);
            }

        } else {
            if (any) {
//                System.out.println("piece1");

                if (!legalFirst || this.solver.testState(currentBoard)) {
//                    System.out.println("piece");

                    states.add(currentState + ":" + stateDescription[1]);
                    finalStates.addAll(states);
                    finished = true;
                    return;
                }
            } else {
                if (this.additionalDepth == 0 || !this.solver.iterateTwo(currentState, this.additionalDepth, true).isEmpty()) {
                    states.add(currentState + ":" + stateDescription[1]);
                    finalStates.addAll(states);
                    finished = true;
                    return;
                }
            }
        }
        newStates.addAll(states);
//        System.out.println("finished");
        finished = true;
    }

    private List<Coordinate> allPieces(ChessBoard board) {
        return new BoardInterface(board).getBoardFacts().getAllCoordinates(board.getTurn())
                .values().stream().flatMap(Path::stream).toList();
    }

    @Override
    public void run() {
        try {
            statest(currentDepth, state, depth);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
    public  List<String> getFinalStates() {
        return finalStates;
    }
    public  List<String> getNewStates() {
        return newStates;
    }
    public boolean isFinished() {
        return finished;
    }
}
