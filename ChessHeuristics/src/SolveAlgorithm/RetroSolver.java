package SolveAlgorithm;

import StandardChess.ChessBoard;

import java.util.List;

/**
 * A class that searches through a given ChessBoard, looking for a number of valid sets of last moves up to a
 * specified depth,
 * and of a specified amount. Additional conditions can also be given to make during testing. The number of valid sets
 * of moves to be found, and any additional depth, are specified separately from construction.
 * <p></p>
 * If support for additional criteria is implemented, there should be two kinds implemented.
 * <p>1. un-move criteria - criteria tested against the moves made.</p>
 * <p>2. board state criteria - criteria tested after a board state is tested by a StateDetector.</p>
 * Un-Move criteria should test a String of the format: f:m:i
 * <p>Where:</p>
 * <p>f is the fen of the board state after the move.</p>
 * <p>m is the move in  the format: patcb  ; where p is a character representing the moved piece, a is the
 * coordinate (e.g. a1, c8, etc) of the start of the move were this a regular chess move and not an unmove,
 * t is the type of move (-, x, e.p., O-O, or O-O-O), c is the captured piece,
 * and b is the coordinate of the end of the move</p>
 * <p>i is the un-ply number, starting at 0</p>
 * <p></p>
 * Board state criteria should test a DetectorInterface. Details about how information about a board state is retrieved
 * from a DetectorInterface is detailed in the interface's documentation.
 * @author Roland Crompton
 */
public interface RetroSolver {
    /**
     * Begins the process of solving a last n moves retrograde chess puzzle to the depth given. Return all
     * solutions found. The ChessBoard given should have its turn set to whichever colour is to un move next.
     * @param FEN the FEN of the chess board whose state is to be tested
     * @param depth the depth to which the puzzle will be solved, i.e. the number of moves back it will look
     *              before concluding a state is valid
     * @return all solutions found, but no more than the number set as the maximum number of solutions
     * @throws IllegalArgumentException if the Reader of the ChessBoard given does not produce a valid FEN
     */
    List<String> solve(String FEN, int depth);
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
