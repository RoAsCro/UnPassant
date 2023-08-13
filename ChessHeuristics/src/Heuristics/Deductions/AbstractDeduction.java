package Heuristics.Deductions;

import Heuristics.Deduction;
import Heuristics.StateDetector;

import java.util.Map;

public abstract class AbstractDeduction implements Deduction {

    protected static final int MAX_PAWNS = 8;
    protected static final int MAX_PIECES = 16;
    protected static int FINAL_RANK_Y = 7;
    protected static int FIRST_RANK_Y = 0;
    protected static int BLACK_PAWN_Y = 6;
    protected static int WHITE_PAWN_Y = 1;
    protected static int BLACK_ESCAPE_Y = 5;
    protected static int WHITE_ESCAPE_Y = 2;
    protected static int Q_ROOK_X = 0;
    protected static int K_ROOK_X = 7;
    protected static int Q_KNIGHT_X = 1;
    protected static int K_KNIGHT_X = 6;
    protected static int Q_BISHOP_X = 2;
    protected static int K_BISHOP_X = 5;
    protected static int QUEEN_X = 3;
    protected static int KING_X = 4;

    protected StateDetector detector;

    protected final static Map<Integer, String> STANDARD_STARTS = Map.of(
            0, "rook", 1, "knight", 2, "bishop", 3, "queen", 4,
            "king", 5, "bishop", 6, "knight", 7, "rook"
    );

    protected final static Map<String, String> PIECE_CODES = Map.of(
            "rook", "r", "knight", "n", "bishop", "b", "queen", "q",
            "king", "k"
    );

    protected Boolean state = true;

    @Override
    public String errorMessage(){
        return this.getClass() + " deductions failed.";
    }

    @Override
    public void registerDetector(StateDetector detector) {
        this.detector = detector;
    }

    @Override
    public Boolean getState() {
        return this.state;
    }

    @Override
    public void update() {

    }

}
