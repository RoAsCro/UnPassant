package ChessHeuristics;

import ChessHeuristics.Heuristics.BoardInterface;
import ChessHeuristics.Heuristics.Deductions.CombinedPawnMap;
import ChessHeuristics.Heuristics.Detector.Data.StandardCaptureData;
import ChessHeuristics.Heuristics.Detector.Data.StandardPawnData;
import ChessHeuristics.Heuristics.Detector.Data.StandardPieceData;
import ChessHeuristics.Heuristics.Detector.Data.StandardPromotionData;
import ChessHeuristics.Heuristics.Detector.StandardStateDetector;
import ReverseChess.StandardChess.BoardBuilder;
import ReverseChess.StandardChess.Coordinate;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class PawnMapTest {
    CombinedPawnMap.PawnMap pawnMapWhite;
    CombinedPawnMap.PawnMap pawnMapBlack;
    StandardStateDetector standardStateDetector;
    @BeforeEach
    void setup() {
        this.pawnMapWhite = new CombinedPawnMap.PawnMap(true);
        this.pawnMapBlack = new CombinedPawnMap.PawnMap(false);
        standardStateDetector = new StandardStateDetector(new StandardPawnData(), new StandardCaptureData(),
                new StandardPromotionData(), new StandardPieceData(), pawnMapWhite, pawnMapBlack);
    }

    @Test
    void testPawnMaps() {
        BoardInterface board = new BoardInterface(
                BoardBuilder.buildBoard("4k3/p1Pp4/p3p1p1/5p1p/5P1P/P3P1P1/P1pP4/4K3 w - - 0 1"));

        this.standardStateDetector.testState(board);
        System.out.println(this.pawnMapWhite.getPawnOrigins());
        this.standardStateDetector.testState(board);
        System.out.println(this.pawnMapBlack.getPawnOrigins());
        Assertions.assertTrue(this.pawnMapWhite.getState());
        Assertions.assertTrue(this.pawnMapBlack.getState());

    }

    @Test
    void testPawnMapsPawnPositions() {
        BoardInterface board = new BoardInterface(
                BoardBuilder.buildBoard("4k3/7p/P6p/P6p/P6p/P6p/P7/4K3 w - - 0 1"));
        this.standardStateDetector.testState(board);
        System.out.println(this.pawnMapWhite.getPawnOrigins());
        this.pawnMapWhite.getPawnOrigins().forEach((key, value) -> Assertions.assertEquals(1, value.size()));

        this.standardStateDetector.testState(board);
        System.out.println(this.pawnMapBlack.getPawnOrigins());
        this.pawnMapBlack.getPawnOrigins().forEach((key, value) -> Assertions.assertEquals(1, value.size()));
        Assertions.assertTrue(this.pawnMapWhite.getState());
        Assertions.assertTrue(this.pawnMapBlack.getState());
    }

    @Test
    void testPawnMapsTwo() {
        BoardInterface board = new BoardInterface(
                BoardBuilder.buildBoard("r3k2r/p2p4/1pp5/2p5/2P5/1PP5/P2P4/4K3 w kq - 0 1"));
        this.standardStateDetector.testState(board);
        Assertions.assertEquals(1, this.pawnMapWhite.getPawnOrigins().get(new Coordinate(2, 3)).size());

        
        this.standardStateDetector.testState(board);
        Assertions.assertEquals(1, this.pawnMapBlack.getPawnOrigins().get(new Coordinate(2, 4)).size());
        Assertions.assertTrue(this.pawnMapWhite.getState());
        Assertions.assertTrue(this.pawnMapBlack.getState());
    }

    @Test
    void testPawnMapsThree() {
        BoardInterface board = new BoardInterface(
                BoardBuilder.buildBoard("4k3/5p2/p2pp3/1p6/1p6/1PPPP3/2P2PPP/4K3 w - - 0 1"));
        this.standardStateDetector.testState(board);
        Assertions.assertEquals(1, this.pawnMapWhite.getPawnOrigins().get(new Coordinate(1, 2)).size());
        Assertions.assertEquals(1, this.pawnMapWhite.getPawnOrigins().get(new Coordinate(2, 1)).size());
        Assertions.assertEquals(1, this.pawnMapWhite.getPawnOrigins().get(new Coordinate(2, 2)).size());
        Assertions.assertEquals(2, this.pawnMapWhite.getPawnOrigins().get(new Coordinate(3, 2)).size());
        Assertions.assertEquals(2, this.pawnMapWhite.getPawnOrigins().get(new Coordinate(4, 2)).size());
        Assertions.assertEquals(1, this.pawnMapWhite.getPawnOrigins().get(new Coordinate(5, 1)).size());
        Assertions.assertEquals(1, this.pawnMapWhite.getPawnOrigins().get(new Coordinate(7, 1)).size());
        Assertions.assertEquals(1, this.pawnMapWhite.getPawnOrigins().get(new Coordinate(6, 1)).size());
        Assertions.assertTrue(this.pawnMapWhite.getState());
    }

    @Test
    void testPawnMapFour() {
        BoardInterface board = new BoardInterface(
                BoardBuilder.buildBoard("4k3/p4p2/3ppp2/1p6/1p6/1PPPP3/2P2PPP/4K3 w - - 0 1"));
        
        this.standardStateDetector.testState(board);
        Assertions.assertEquals(1, this.pawnMapBlack.getPawnOrigins().get(new Coordinate(0, 6)).size());
        Assertions.assertEquals(3, this.pawnMapBlack.getPawnOrigins().get(new Coordinate(1, 4)).size());
        Assertions.assertEquals(4, this.pawnMapBlack.getPawnOrigins().get(new Coordinate(1, 3)).size());
        Assertions.assertEquals(3, this.pawnMapBlack.getPawnOrigins().get(new Coordinate(3, 5)).size());
        Assertions.assertEquals(2, this.pawnMapBlack.getPawnOrigins().get(new Coordinate(4, 5)).size());
        Assertions.assertEquals(1, this.pawnMapBlack.getPawnOrigins().get(new Coordinate(5, 5)).size());
        Assertions.assertTrue(this.pawnMapBlack.getState());

    }

    @Test
    void testPawnMapFive() {
        BoardInterface board = new BoardInterface(
                BoardBuilder.buildBoard("4k3/p4p2/3ppp2/1p6/1p6/2PPPPP1/2P4P/4K3 w - - 0 1"));
        this.standardStateDetector.testState(board);
        Assertions.assertEquals(1, this.pawnMapWhite.getPawnOrigins().get(new Coordinate(2, 2)).size());
        Assertions.assertEquals(new Coordinate(1, 1), this.pawnMapWhite.getPawnOrigins().get(new Coordinate(2, 2)).get(0));
        Assertions.assertTrue(this.pawnMapWhite.getState());

    }

    @Test
    void testCaptures() {
        BoardInterface board = new BoardInterface(
                BoardBuilder.buildBoard("rnbqkbnr/pppppppp/8/8/8/1P6/3PPPPP/RNBQKBNR w KQkq - 0 1"));
        
        this.standardStateDetector.testState(board);
        Assertions.assertEquals(1, this.pawnMapWhite.getPawnOrigins().get(new Coordinate(1, 2)).size());
        Assertions.assertEquals(new Coordinate(1, 1),
                this.pawnMapWhite.getPawnOrigins().get(new Coordinate(1, 2)).get(0));
        Assertions.assertTrue(this.pawnMapWhite.getState());
    }

    @Test
    void testCapturesTwo() {
        BoardInterface board = new BoardInterface(
                BoardBuilder.buildBoard("rnbqkbnr/pppppppp/5P2/2P5/P3P3/1P1P2PP/8/RNBQKBNR w KQkq - 0 1"));
        
        this.standardStateDetector.testState(board);
        this.pawnMapWhite.getPawnOrigins().forEach((key, value) -> {
            Assertions.assertEquals(1, value.size());
            Assertions.assertEquals(new Coordinate(key.getX(), 1), value.get(0));
        });

        Assertions.assertTrue(this.pawnMapWhite.getState());


    }

    @Test
    void testContradictoryCaptures() {
        BoardInterface board = new BoardInterface(
                BoardBuilder.buildBoard("rnbqkbnr/pppp2pp/8/8/P7/5P2/1P1PPP1P/RNBQKBNR w KQkq - 0 1"));
        
        this.standardStateDetector.testState(board);

        System.out.println(this.pawnMapWhite.getPawnOrigins());
        Assertions.assertEquals(1, this.pawnMapWhite.getPawnOrigins().get(new Coordinate(0, 3)).size());
        Assertions.assertEquals(new Coordinate(0, 1), this.pawnMapWhite
                .getPawnOrigins().get(new Coordinate(0, 3)).get(0));
        Assertions.assertTrue(this.pawnMapWhite.getState());

    }

    @Test
    void testCapturesThree() {
        BoardInterface board = new BoardInterface(
                BoardBuilder.buildBoard("r2qkb1r/p1pppppp/1p6/8/4P3/8/PP1PPPPP/RNBQKBNR w KQkq - 0 1"));
        
        this.standardStateDetector.testState(board);

        System.out.println(this.pawnMapWhite.getPawnOrigins());
        Assertions.assertEquals(1, this.pawnMapWhite.getPawnOrigins().get(new Coordinate(4, 3)).size());
        Assertions.assertTrue(this.pawnMapWhite.getState());

    }

    @Test
    void testCapturesFour() {
        BoardInterface board = new BoardInterface(
                BoardBuilder.buildBoard("r2qkb1r/p1pppppp/1p6/8/4P3/8/PP1PPP1P/RNBQKBNR w KQkq - 0 1"));
        
        this.standardStateDetector.testState(board);

        System.out.println(this.pawnMapWhite.getPawnOrigins());
        Assertions.assertEquals(2, this.pawnMapWhite.getPawnOrigins().get(new Coordinate(4, 3)).size());
        Assertions.assertTrue(this.pawnMapWhite.getState());

    }

    @Test
    void testCapturesFive() {
        BoardInterface board = new BoardInterface(
                BoardBuilder.buildBoard("r2qkb1r/p1pppppp/1p6/5P2/8/8/PP1PPP1P/RNBQKBNR w KQkq - 0 1"));
        
        this.standardStateDetector.testState(board);

        Assertions.assertEquals(2, this.pawnMapWhite.getPawnOrigins().get(new Coordinate(5, 4)).size());
        Assertions.assertTrue(this.pawnMapWhite.getState());

    }

    @Test
    void testCapturesSix() {
        BoardInterface board = new BoardInterface(
                BoardBuilder.buildBoard("r2qkb1r/p1pppppp/1p6/8/2P2P2/8/1P1PPPP1/RNBQKBNR w KQkq - 0 1"));
        
        this.standardStateDetector.testState(board);

        System.out.println(this.pawnMapWhite.getPawnOrigins());
        Assertions.assertEquals(1, this.pawnMapWhite.getPawnOrigins().get(new Coordinate(2, 3)).size());
        Assertions.assertEquals(1, this.pawnMapWhite.getPawnOrigins().get(new Coordinate(5, 3)).size());
        Assertions.assertTrue(this.pawnMapWhite.getState());

    }

    @Test
    void testCapturesSeven() {
        BoardInterface board = new BoardInterface(
                BoardBuilder.buildBoard("r2qkb1r/p1pppppp/1p6/8/2P2P2/8/1P1PP1P1/RNBQKBNR w KQkq - 0 1"));
        
        this.standardStateDetector.testState(board);

        System.out.println(this.pawnMapWhite.getPawnOrigins());
        Assertions.assertEquals(2, this.pawnMapWhite.getPawnOrigins().get(new Coordinate(2, 3)).size());
        Assertions.assertEquals(2, this.pawnMapWhite.getPawnOrigins().get(new Coordinate(5, 3)).size());
        Assertions.assertTrue(this.pawnMapWhite.getState());

    }

    @Test
    void testCapturesEight() {
        BoardInterface board = new BoardInterface(
                BoardBuilder.buildBoard("rnbqkb1r/pppppppp/8/8/8/6PP/PPPPPP2/RNBQKBNR w KQkq - 0 1"));
        
        this.standardStateDetector.testState(board);

        System.out.println(this.pawnMapWhite.getPawnOrigins());
        Assertions.assertEquals(1, this.pawnMapWhite.getPawnOrigins().get(new Coordinate(6, 2)).size());
        Assertions.assertEquals(1, this.pawnMapWhite.getPawnOrigins().get(new Coordinate(7, 2)).size());
        Assertions.assertTrue(this.pawnMapWhite.getState());

    }

    @Test
    void testCapturesNine() {
        BoardInterface board = new BoardInterface(
                BoardBuilder.buildBoard("rnbqkb1r/pppppppp/8/8/8/PPPPPPPP/8/RNBQKBNR w KQkq - 0 1"));
        
        this.standardStateDetector.testState(board);

        System.out.println(this.pawnMapWhite.getPawnOrigins());
        for (int i = 0 ; i < 8 ; i++) {
            System.out.println(i);
            Assertions.assertEquals(1, this.pawnMapWhite.getPawnOrigins().get(new Coordinate(i, 2)).size());
        }
        Assertions.assertTrue(this.pawnMapWhite.getState());
    }

}
