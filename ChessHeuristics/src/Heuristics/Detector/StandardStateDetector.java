package Heuristics.Detector;

import Heuristics.BoardInterface;
import Heuristics.Deduction;
import Heuristics.Deductions.UnCastle;
import Heuristics.Detector.Data.CaptureData;
import Heuristics.Detector.Data.PawnData;
import Heuristics.Detector.Data.PieceData;
import Heuristics.Detector.Data.PromotionData;
import Heuristics.Path;
import StandardChess.Coordinate;

import java.util.*;
import java.util.function.Function;

public class StandardStateDetector implements StateDetector {

    private static final int MAX_PAWNS = 8;
    private static final int MAX_PIECES = 16;
    private final UnCastle unCastle = new UnCastle();

    private BoardInterface board;
    private final PawnData pawnData;
    private final CaptureData captureData;
    private final PromotionData promotionData;
    private final PieceData pieceData;
    private final List<Deduction> deductions;
    private final List<Deduction> finishedDeductions = new LinkedList<>();
    public static final Function<Path, Integer> PATH_DEVIATION = p -> p.stream()
            .reduce(new Coordinate(p.get(0).getX(), 0), (c, d) -> {
                if (c.getX() != d.getX()) {
                    return new Coordinate(d.getX(), c.getY() + 1);
                }
                return c;
            }).getY();
    private boolean state = false;
    private String errorMessage;

    public StandardStateDetector(PawnData pawnData, CaptureData captureData, PromotionData promotionData, PieceData pieceData, Deduction ... deductions) {
        this.pawnData = pawnData;
        this.captureData = captureData;
        this.promotionData = promotionData;
        this.pieceData = pieceData;
        this.deductions = Arrays.stream(deductions).toList();
        this.deductions.forEach(d -> d.registerDetector(this));
    }

    public StandardStateDetector(PawnData pawnData, CaptureData captureData, PromotionData promotionData, PieceData pieceData, BoardInterface board, Deduction ... deductions) {
        this(pawnData, captureData, promotionData, pieceData, deductions);
        this.board = board;
    }

    @Override
    public boolean getState() {
        return this.state;
    }

    @Override
    public boolean testState() {
        return !(this.board == null) && testState(this.board);
    }
    @Override
    public boolean testState(BoardInterface board) {

        if (board.inCheck(board.getTurn().equals("white") ? "black" : "white")) {
            this.state = false;
            return false;
        }
        if (board.getBoardFacts().pieceNumbers(false) > MAX_PIECES
                || board.getBoardFacts().pieceNumbers(true) > MAX_PIECES) {
            this.state = false;

            return false;
        }

        if (board.getBoardFacts().pawnNumber(false) > MAX_PAWNS
                || board.getBoardFacts().pawnNumber(true) > MAX_PAWNS) {
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
        unCastle.registerStateDetector(this);
        unCastle.hasMoved();
        return true;
    }

    @Override
    public void reTest(BoardInterface boardInterface) {
        this.finishedDeductions.forEach(d -> d.deduce(boardInterface));
    }

    @Override
    public PawnData getPawnData() {
        return this.pawnData;
    }

    @Override
    public PieceData getPieceData() {
        return this.pieceData;
    }

    @Override
    public PromotionData getPromotionData() {
        return this.promotionData;
    }

    @Override
    public CaptureData getCaptureData() {
        return this.captureData;
    }

    @Override
    public String getErrorMessage() {
        return this.errorMessage;
    }
}
