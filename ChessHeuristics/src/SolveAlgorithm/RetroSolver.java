package SolveAlgorithm;

import StandardChess.ChessBoard;

import java.util.List;

/**
 * A class that searches through a given ChessBoard, looking for a number of valid sets of last moves up to a
 * specified depth,
 * and of a specified amount. Additional conditions can also be given to make during testing. The number of valid sets
 * of moves to be found, and any additional depth, are specified separately from construction.
 * @author Roland Crompton
 */
public interface RetroSolver {
    /**
     * Begins the process of solving a last n moves retrograde chess puzzle to the depth given. Return all
     * solutions found. The ChessBoard given should have its turn set to whichever colour is to un move next.
     * @param board the ChessBoard whose state is to be tested
     * @param depth the depth to which the puzzle will be solved, i.e. the number of moves back it will look
     *              before concluding a state is valid
     * @return all solutions found, but no more than the number set as the maximum number of solutions
     * @throws IllegalArgumentException if the Reader of the ChessBoard given does not produce a valid FEN
     */
    List<String> solve(ChessBoard board, int depth);
    /**
     * Sets the additional depth, the depth to which the Solver will go after it's reached the depth given
     * when solve() is called, searching instead for only one valid board state instead of as many as are set in the
     * number of solutions.
     * @param additionalDepth the additional depth for the Solver to  go to when solving
     */
    void setAdditionalDepth(int additionalDepth);
    /**
     * Sets the number of solutions for the Solver to try to find when solving. When this number is reached, it will
     * stop searching for solutions.
     * @param numberOfSolutions the number of solutions the solver will look for when solving
     */
    void setNumberOfSolutions(int numberOfSolutions);

}
