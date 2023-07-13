import Heuristics.BoardInterface;
import Heuristics.Deductions.*;
import StandardChess.BoardBuilder;
import StandardChess.Coordinate;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class CaptureLocationTest {
    PawnMapWhite pawnMapWhite;
    PawnMapBlack pawnMapBlack;
    CombinedPawnMap combinedPawnMap;
    PieceMap pieceMap;

    CaptureLocations captureLocations;
    @BeforeEach
    void setup() {
        pawnMapWhite = new PawnMapWhite();
        pawnMapBlack = new PawnMapBlack();
        combinedPawnMap = new CombinedPawnMap(pawnMapWhite, pawnMapBlack);
        pieceMap = new PieceMap(combinedPawnMap);
        captureLocations = new CaptureLocations(pawnMapWhite, pawnMapBlack, pieceMap, combinedPawnMap);

    }

    @Test
    void testUntakeable() {
        BoardInterface boardInterface = new BoardInterface(BoardBuilder.buildBoard("rnbqk2r/pppppppp/8/8/2P5/8/2PPPPPP/RNBQKBNR w KQkq - 0 1"));

        pieceMap.getObservations().forEach(observation -> observation.observe(boardInterface));
        this.pieceMap.deduce(boardInterface);

        this.captureLocations.deduce(boardInterface);
        System.out.println(pawnMapWhite.getPawnOrigins());
        Assertions.assertEquals(1, pawnMapWhite.getPawnOrigins().get(new Coordinate(2, 3)).size());

    }

    @Test
    void testUntakeableWithRook() {
        BoardInterface boardInterface = new BoardInterface(BoardBuilder.buildBoard("rnbqk3/ppppppp1/7p/8/2P5/8/2PPPPPP/RNBQKBNR w KQq - 0 1"));

        pieceMap.getObservations().forEach(observation -> observation.observe(boardInterface));
        this.pieceMap.deduce(boardInterface);

        this.captureLocations.deduce(boardInterface);
        System.out.println(pawnMapWhite.getPawnOrigins());
        Assertions.assertEquals(1, pawnMapWhite.getPawnOrigins().get(new Coordinate(2, 3)).size());

    }

    @Test
    void testUntakeableCapturedRook() {
        BoardInterface boardInterface = new BoardInterface(BoardBuilder.buildBoard("2bqkbnr/pPpppppp/np6/8/8/8/PP1PPPPP/RNBQKBNR w KQk - 0 1"));

        pieceMap.getObservations().forEach(observation -> observation.observe(boardInterface));
        this.pieceMap.deduce(boardInterface);

        this.captureLocations.deduce(boardInterface);
        System.out.println(pawnMapWhite.getPawnOrigins());
        Assertions.assertEquals(1, pawnMapWhite.getPawnOrigins().get(new Coordinate(1, 6)).size());

    }

    @Test
    void testUntakeableRook() {
        BoardInterface boardInterface = new BoardInterface(BoardBuilder.buildBoard("1nbqkb1r/1pppppPp/p6n/8/8/8/PPPP1P1P/RNBQKBNR w KQk - 0 1"));

        pieceMap.getObservations().forEach(observation -> observation.observe(boardInterface));
        this.pieceMap.deduce(boardInterface);

        this.captureLocations.deduce(boardInterface);
        System.out.println(pawnMapWhite.getPawnOrigins());
        Assertions.assertEquals(1, pawnMapWhite.getPawnOrigins().get(new Coordinate(6, 6)).size());

    }
}
