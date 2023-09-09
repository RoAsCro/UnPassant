package ChessHeuristics.Heuristics.Deductions;

import ChessHeuristics.Heuristics.Detector.StateDetector;

/**
 * An abstract deduction implementing those methods whose implementation is
 * common to all deductions in UnPassant's the standard set.
 * @author Roland Crompton
 */
public abstract class AbstractDeduction implements Deduction {
    /**The Deduction's state, true by default, false if a board state is found to be impossible*/
    protected Boolean state = true;

    /**A stored reference to the Deduction's StateDetector*/
    protected StateDetector detector;

    /**A default error message*/
    protected String errorMessage = "Position not legal.";

    /**
     * A constructor setting the error message to the given String.
     * @param errorMessage the error message to be displayed when getErrorMessage is called
     */
    public AbstractDeduction(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    /**
     * Returns an error message String describing what is wrong with the board's state.
     * @return an error message String
     */
    @Override
    public String errorMessage(){
        return this.errorMessage;
    }
    /**
     * Returns the Deduction's current state. This should be true if the deduction has not yet been run, or if no
     * problems have been found in the board state, and false if the Deduction deduces that the board state
     * is not possible.
     * @return the state of the Deduction
     */
    @Override
    public boolean getState() {
        return this.state;
    }
    /**
     * Registers the StateDetector whose information is to be used in deductions, and in which the information
     * derived byt he Deduction will be stored.
     * @param detector the StateDetector to be registered
     */
    @Override
    public void registerDetector(StateDetector detector) {
        this.detector = detector;
    }
}
