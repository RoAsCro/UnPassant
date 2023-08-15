package Heuristics.Deductions;

import Heuristics.Deduction;
import Heuristics.Detector.StateDetector;
import Heuristics.HeuristicsUtil;
import Heuristics.Path;
import StandardChess.Coordinate;

import java.util.List;
import java.util.stream.Stream;

import static Heuristics.HeuristicsUtil.*;

public abstract class AbstractDeduction implements Deduction {
    public static final Path pawnStarts =
            Path.of(Stream.iterate(-MAX_PAWNS, i -> i != -1 ?  i + 1 : i + 2).limit(MAX_PAWNS * 2).map(i -> new Coordinate(Math.abs(i) - 1, i < 0 ? WHITE_PAWN_Y : BLACK_PAWN_Y)).toList());
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
    public Boolean getState() {
        return this.state;
    }

    @Override
    public void update() {

    }

}
