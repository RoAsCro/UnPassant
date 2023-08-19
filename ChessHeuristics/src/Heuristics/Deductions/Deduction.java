package Heuristics.Deductions;


import Heuristics.BoardInterface;
import Heuristics.Detector.StateDetector;

/**
 * An Interface for a Class that deduces information about a chess board from the position of pieces and the various
 * data stored in a StateDetector, in turn storing the information deduced in the data classes of that Detector.
 * <></>
 * A Deduction has a state, representing whether, according to its deductions, the board it's been given is reachable
 * according to the rules of Chess.
 * <></>
 * Before running its deduce() method, a StateDetector must be registered in the Deduction. A Deductions will also
 * often require that other information be derived before it can produce accurate results.
 */
public interface Deduction {
    /**
     * Registers the StateDetector whose information is to be used in deductions, and in which the information
     * derived byt he Deduction will be stored.
     * @param detector the StateDetector to be registered
     */
    void registerDetector(StateDetector detector);

    /**
     * Runs the deduction that the Deduction is intended to make, storing the information derived in the
     * registered StateDetector. This will require that a StateDetector first be registered.
     * @param board the board whose information the deduction will draw from
     */
    void deduce(BoardInterface board);

    /**
     * Returns the Deduction's current state. This should be true if the deduction has not yet been run, or if no
     * problems have been found in the board state, and false if the Deduction deduces that the board state
     * is not possible.
     * @return the state of the Deduction
     */
    boolean getState();

    /**
     * Returns an error message String describing what is wrong with the board's state.
     * @return an error message String
     */
    String errorMessage();
}
