import Heuristics.BoardInterface;
import Heuristics.Deductions.PromotedPieces;
import Heuristics.Deductions.WhitePromotedPieces;
import StandardChess.BoardBuilder;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

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
        Assertions.assertEquals(4, testHelper(fen).getPromotedPieces().size());
    }

    @Test
    void testNumbersUncertainPromotions() {
        String fen = "rnbqkbnr/p2ppppp/8/4B1R1/2BB2R1/3RBB2/2N2PPP/4K3 w kq - 0 1";
        System.out.println(testHelper(fen).getPromotedPieces());

        Assertions.assertEquals(5, testHelper(fen).getPromotedPieces().size());
    }

    @Test
    void testNumbersQueen() {
        String fen = "rnbqkbnr/p2ppppp/8/8/8/1Q6/PQPPPPPP/4K3 w kq - 0 1";
        System.out.println(testHelper(fen).getPromotedPieces());
        Assertions.assertEquals(1, testHelper(fen).getPromotedPieces().size());
    }



}
