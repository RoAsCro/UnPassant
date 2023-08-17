package Heuristics.Deductions;

import Heuristics.Detector.StateDetector;
import Heuristics.Path;
import StandardChess.Coordinate;

import java.util.stream.Stream;

import static Heuristics.HeuristicsUtil.*;

/**
 * An abstract deduction implementing those methods whose implementation is
 * common to all deductions in UnPassant's the standard set.
 */
public abstract class AbstractDeduction implements Deduction {
    public static final Path pawnStarts =
            Path.of(Stream.iterate(-MAX_PAWNS, i -> i != -1 ?  i + 1 : i + 2).limit(MAX_PAWNS * 2)
                    .map(i -> new Coordinate(Math.abs(i) - 1, i < 0 ? WHITE_PAWN_Y : BLACK_PAWN_Y)).toList());
    protected String errorMessage = "Position not legal.";

    protected StateDetector detector;

    protected Boolean state = true;

    public AbstractDeduction(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    @Override
    public String errorMessage(){
        return this.errorMessage;
    }

    @Override
    public void registerDetector(StateDetector detector) {
        this.detector = detector;
    }

    @Override
    public boolean getState() {
        return this.state;
    }

}
