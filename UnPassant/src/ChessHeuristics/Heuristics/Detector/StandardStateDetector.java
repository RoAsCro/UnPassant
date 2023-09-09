package ChessHeuristics.Heuristics.Detector;

import ChessHeuristics.Heuristics.BoardInterface;
import ChessHeuristics.Heuristics.Deductions.Deduction;
import ChessHeuristics.Heuristics.Detector.Data.CaptureData;
import ChessHeuristics.Heuristics.Detector.Data.PawnData;
import ChessHeuristics.Heuristics.Detector.Data.PieceData;
import ChessHeuristics.Heuristics.Detector.Data.PromotionData;
import ReverseChess.StandardChess.Coordinates;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import static ChessHeuristics.Heuristics.HeuristicsUtil.MAX_PAWNS;
import static ChessHeuristics.Heuristics.HeuristicsUtil.MAX_PIECES;

/**
 * An implementation of the StateDetector intended to work with Deductions to produce information about the legality
 * of a chess board state. This implementation requires that only one chess board be tested using a given instance
 * of StandardStateDetector. Attempting to use the same instance of StandardStateDetector to test two different
 * boards will not produce accurate results.
 * @author Roland Crompton
 */
public class StandardStateDetector implements StateDetector {
    /**The board being tested*/
    private BoardInterface board;
    /**The PawnData from which Deductions will draw and store information*/
    private final PawnData pawnData;
    /**The CaptureData from which Deductions will draw and store information*/
    private final CaptureData captureData;
    /**The Promotion from which Deductions will draw and store information*/
    private final PromotionData promotionData;
    /**The PieceData from which Deductions will draw and store information*/
    private final PieceData pieceData;
    /**A List of the Deductions this StateDetector will go through*/
    private final List<Deduction> deductions;
    /**A List of the Deductions that have completed deducing at least once*/
    private final List<Deduction> finishedDeductions = new LinkedList<>();
    /**Stores the current state of the legality of the chess board being tested*/
    private boolean state = false;
    /**The current error message to be used when a board is found not to be legal*/
    private String errorMessage;

    /**
     * A constructor taking instance of the four types of Data in which information about a chess board is to be
     * stored and retrieved, as well as any number of Deductions to be tested when testState() is called.
     * The Deductions all have this StateDetector registered with them.
     * The Deductions should be input in the order in which they are intended to be run.
     * @param pawnData the PawnData
     * @param captureData the CaptureData
     * @param promotionData the PromotionData
     * @param pieceData the PieceData
     * @param deductions the Deductions to be run through when testState() is called, given in the order they are to be
     *                   run
     */
    public StandardStateDetector(PawnData pawnData, CaptureData captureData,
                                 PromotionData promotionData, PieceData pieceData,
                                 Deduction ... deductions) {
        this.pawnData = pawnData;
        this.captureData = captureData;
        this.promotionData = promotionData;
        this.pieceData = pieceData;
        this.deductions = Arrays.stream(deductions).toList();
        this.deductions.forEach(d -> d.registerDetector(this));
    }

    /**
     * A constructor taking instance of the four types of Data in which information about a chess board is to be
     * stored and retrieved, a BoardInterface to be tested when testState() is called,
     * and any number of Deductions to be tested when testState() is called.
     * The Deductions all have this StateDetector registered with them.
     * The Deductions should be input in the order in which they are intended to be run.
     * @param pawnData the PawnData
     * @param captureData the CaptureData
     * @param promotionData the PromotionData
     * @param pieceData the PieceData
     * @param board the BoardInterface to be used when testing the legality of a board state
     * @param deductions the Deductions to be run through when testState() is called, given in the order they are to be
     *                   run
     */
    public StandardStateDetector(PawnData pawnData, CaptureData captureData,
                                 PromotionData promotionData, PieceData pieceData,
                                 BoardInterface board, Deduction ... deductions) {
        this(pawnData, captureData, promotionData, pieceData, deductions);
        this.board = board;
    }

    /**
     * Gets the legality of the board it's been given.
     * @return the legality of the board given, true if legal, false if not
     */
    @Override
    public boolean getState() {
        return this.state;
    }

    /**
     * Tests the legality of an already stored BoardInterface. First it is checked whether the
     * previous move was a player putting themselves in check, then whether the number of pieces of pawns of either
     * player exceeds the number that it's possible to  have in a chess game, then it runs through the Deductions
     * given at instantiation. For each, the deduce() method is called, and the state of that Deduction and all
     * previous deductions checked. If at any stage any Deduction's state is false, testing ceases, and false
     * returned.
     * Will also return false if a BoardInterface has not yet been stored.
     * @return the legality of a board state, true if legal, false otherwise,
     * or false if there is no stored BoardInterface
     */
    @Override
    public boolean testState() {
        return !(this.board == null) && testState(this.board);
    }

    /**
     * Tests the legality of a given BoardInterface. First it is checked whether the previous move was a player
     * putting themselves in check, then whether the number of pieces of pawns of either player
     * exceeds the number that it's possible to  have in a chess game, then it runs through the Deductions
     * given at instantiation. For each, the deduce() method is called, and the state of that Deduction and all
     * previous deductions checked. If at any stage any Deduction's state is false, testing ceases, and false
     * returned.
     * <p></p>
     * The board's turn should be the turn of the colour that would theoretically move next in an ordinary
     * chess game, or the turn of whoever just made an un-move.
     * @param board the BoardInterface representing the board to be checked
     * @return the legality of the board state, true if legal, false otherwise
     */
    @Override
    public boolean testState(BoardInterface board) {

        if (!board.inCheck(board.getTurn().equals("white") ? "black" : "white").equals(Coordinates.NULL_COORDINATE)) {
            this.state = false;
            this.errorMessage = "Player put themselves in check.";
            return false;
        }
        if (board.getBoardFacts().pieceNumbers(false) > MAX_PIECES
                || board.getBoardFacts().pieceNumbers(true) > MAX_PIECES) {
            this.state = false;
            this.errorMessage = "More than 16 pieces.";
            return false;
        }

        if (board.getBoardFacts().pawnNumber(false) > MAX_PAWNS
                || board.getBoardFacts().pawnNumber(true) > MAX_PAWNS) {
            this.errorMessage = "More than 8 pawns.";
            this.state = false;

            return false;
        }

        for (Deduction deduction : this.deductions) {

            deduction.deduce(board);
            if (!this.deductions.stream().allMatch(Deduction::getState)) {
                this.state = false;
                this.errorMessage = deduction.errorMessage();
                return false;

            }
            this.finishedDeductions.add(deduction);
        }
        this.state = true;

        return true;
    }

    /**
     * Calls deduce() on all Deductions previously completed.
     * @param boardInterface the board to be used by the deductions
     */
    @Override
    public void reTest(BoardInterface boardInterface) {
        this.finishedDeductions.forEach(d -> d.deduce(boardInterface));
    }

    /**
     * Return the PawnData stored in the StateDetector.
     * @return the PawnData
     */
    @Override
    public PawnData getPawnData() {
        return this.pawnData;
    }

    /**
     * Return the PieceData stored in the StateDetector.
     * @return the PieceData
     */
    @Override
    public PieceData getPieceData() {
        return this.pieceData;
    }

    /**
     * Return the PromotionData stored in the StateDetector.
     * @return the PromotionData
     */
    @Override
    public PromotionData getPromotionData() {
        return this.promotionData;
    }

    /**
     * Return the CaptureData stored in the StateDetector.
     * @return the CaptureData
     */
    @Override
    public CaptureData getCaptureData() {
        return this.captureData;
    }

    /**
     * Returns the current error message, describing why the board's state is false.
     * @return the current error message
     */
    @Override
    public String getErrorMessage() {
        return this.errorMessage;
    }
}
