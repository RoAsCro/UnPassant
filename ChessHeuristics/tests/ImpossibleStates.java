import Heuristics.BoardInterface;
import Heuristics.Deductions.ImpossibleStateDetector;
import Heuristics.Deductions.PawnMapBlack;
import Heuristics.Deductions.PawnMapWhite;
import Heuristics.Observations.PawnNumber;
import Heuristics.Observations.PieceNumber;
import StandardChess.BoardBuilder;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class ImpossibleStates {

    PawnNumber pawnNumber;
    PieceNumber pieceNumber;
    ImpossibleStateDetector detector;

    @BeforeEach
    public void setup() {
        this.pawnNumber = new PawnNumber();
        this.pieceNumber = new PieceNumber();
        this.detector = new ImpossibleStateDetector(pawnNumber, pieceNumber, new PawnMapWhite(new PawnNumber(), new PieceNumber()), new PawnMapBlack(new PawnNumber(), new PieceNumber()));
    }

    public boolean test(String fen) {
        BoardInterface board = new BoardInterface(BoardBuilder.buildBoard(fen));
        return this.detector.testState(board);
    }

    @Test
    void defaultBoard() {
        Assertions.assertTrue(test("rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1"));

    }

    @Test
    void tooManyPawns() {
        Assertions.assertFalse(test("rnbqkbnr/pppppppp/8/8/8/6P1/PPPPPPPP/RNBQKBNR w KQkq - 0 1"));
    }

    @Test
    void tooManyPawnsFewerPieces() {
        Assertions.assertFalse(test("rnbqkbnr/pppppppp/8/6P1/8/7P/PPPPPP1P/RNBQKB2 w Qkq - 0 1"));
    }

    @Test
    void tooManyPawnsBlack() {
        Assertions.assertFalse(test("rnbqkb1r/pppppppp/6p1/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1"));
    }

    @Test
    void tooManyPieces() {
        Assertions.assertFalse(test("rnbqk3/ppppp3/8/6N1/6N1/7N/PPPPPP2/RNBQKBNR w KQq - 0 1"));
    }

    @Test
    void tooManyPiecesBlack() {
        Assertions.assertFalse(test("rnbqkbnr/p2ppppp/8/2nn4/1n6/8/P2PPPPP/R2QKBNR w KQkq - 0 1"));
    }

    @Test
    void tooImpossiblePawnPosition() {
        Assertions.assertFalse(test("r1bqkb2/ppppppp1/8/8/8/7P/PPPP1PPP/RNBQKBNR w KQq - 0 1"));
    }


}
