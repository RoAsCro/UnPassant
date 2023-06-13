import Heuristics.BoardInterface;
import Heuristics.Deduction;
import Heuristics.Deductions.PromotedPiece;
import Heuristics.Deductions.PromotedPieces;
import Heuristics.Deductions.WhitePromotedPieces;
import Heuristics.Observation;
import StandardChess.BoardBuilder;
import StandardChess.Coordinate;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.stream.Collectors;

public class PromotionNumbersTest {

    PromotedPieces testHelper(String fen) {
        BoardInterface board =
                new BoardInterface(BoardBuilder.buildBoard(fen));
        PromotedPieces p = new WhitePromotedPieces();
        p.getObservations().stream().forEach(o -> o.observe(board));
        p.deduce(board);
        return p;
    }

    @Test
    void testNumbersCertainPromotions() {
        String fen = "rnbqkbnr/p2ppppp/8/4B1R1/2BB2R1/3RBB2/4PPPP/4K3 w kq - 0 1";
        Assertions.assertEquals(4, testHelper(fen).getAndSet().getPieces().size());
    }

    @Test
    void testNumbersUncertainPromotions() {
        String fen = "rnbqkbnr/p2ppppp/8/4B1R1/2BB2R1/3RBB2/2N2PPP/4K3 w kq - 0 1";
        System.out.println(testHelper(fen).getAndSet());
        Assertions.assertEquals(5, testHelper(fen).getAndSet().getPieces().size());
    }


}
