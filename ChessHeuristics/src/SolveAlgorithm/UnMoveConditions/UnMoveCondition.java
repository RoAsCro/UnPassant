package SolveAlgorithm.UnMoveConditions;

import java.util.function.Predicate;

public class UnMoveCondition implements Predicate<String>{

    private final boolean not;
    private final TimingCondition timing;
    private final PlayerCondition player;
    private final LocationCondition location;
    private final MovementCondition movement;
    private final PieceCondition piece;
    private final CapturedPieceCondition capture;

    public UnMoveCondition(int fromTime, int toTime, char player, String from, String to,
                           char piece1, char piece2, String how, boolean not) {
        this.timing = new TimingCondition(fromTime, toTime);
        this.player = new PlayerCondition(player);
        this.location = new LocationCondition(from, to);
        this.piece = new PieceCondition(piece1);
        this.capture = new CapturedPieceCondition(piece2);
        this.movement = new MovementCondition(how);
        this.not = not;
    }

    public boolean test(String move) {
        if (this.timing.and(this.player).test(move)) {
            if (this.location.and(this.piece.and(this.capture.and(this.movement))).test(move)) {
                return !this.not;
            }
            return this.not;
        }
        return true;
    }
}
