import Heuristics.BoardInterface;
import Heuristics.Deductions.PromotedPieces;
import StandardChess.BoardBuilder;
import org.junit.jupiter.api.Test;

public class PromotionNumbersTest {

    @Test
    void testPromotedPieceTree() {
        BoardInterface board =
                new BoardInterface(BoardBuilder.buildBoard("rnbqkbnr/p2ppppp/8/8/3R4/5B2/2PPPPPP/RNBQKBNR w KQkq - 0 1"));
        PromotedPieces p = new PromotedPieces();
        p.deduce(board);
        System.out.println(p.getPromotedPieces()
                .keySet()
                .stream()
                .flatMap(c -> p.getPromotedPieces().get(c).orList().stream())
                .map(d -> {PromotedPieces.PromotedPiece piece = (PromotedPieces.PromotedPiece) d;
                    return piece.getLocation();})
                .toList()
        );
        System.out.println(p.getPromotedPieces());

    }

}
