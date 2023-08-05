import Heuristics.BoardInterface;
import Heuristics.Deductions.CombinedPawnMap;
import Heuristics.Deductions.PawnMapBlack;
import Heuristics.Deductions.PawnMapWhite;
import Heuristics.Observation;
import Heuristics.Observations.PawnNumber;
import Heuristics.Observations.PieceNumber;
import Heuristics.Path;
import StandardChess.BoardBuilder;
import StandardChess.Coordinate;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

public class PawnMapTest {

    @Test
    void testPawnMaps() {
        BoardInterface board = new BoardInterface(BoardBuilder.buildBoard("4k3/p1Pp4/p3p1p1/5p1p/5P1P/P3P1P1/P1pP4/4K3 w - - 0 1"));
        PawnMapWhite map = new PawnMapWhite(new PawnNumber(), new PieceNumber());
        map.deduce(board);
        System.out.println(map.getPawnOrigins());

        PawnMapBlack blackMap = new PawnMapBlack(new PawnNumber(), new PieceNumber());
        blackMap.deduce(board);
        Assertions.assertTrue(map.getState());
        Assertions.assertTrue(blackMap.getState());

    }

    @Test
    void testPawnMapsPawnPositions() {
        BoardInterface board = new BoardInterface(BoardBuilder.buildBoard("4k3/7p/P6p/P6p/P6p/P6p/P7/4K3 w - - 0 1"));
        PawnMapWhite map = new PawnMapWhite(new PawnNumber(), new PieceNumber());
        map.deduce(board);
        System.out.println(map.getPawnOrigins());
        map.getPawnOrigins().entrySet().stream().forEach(entry ->{
            Assertions.assertEquals(1, entry.getValue().size());
        });

        PawnMapBlack blackMap = new PawnMapBlack(new PawnNumber(), new PieceNumber());
        blackMap.deduce(board);
        System.out.println(blackMap.getPawnOrigins());
        blackMap.getPawnOrigins().entrySet().stream().forEach(entry ->{
            Assertions.assertEquals(1, entry.getValue().size());
        });
        Assertions.assertTrue(map.getState());
        Assertions.assertTrue(blackMap.getState());
    }

    @Test
    void testPawnMapsTwo() {
        BoardInterface board = new BoardInterface(BoardBuilder.buildBoard("r3k2r/p2p4/1pp5/2p5/2P5/1PP5/P2P4/4K3 w kq - 0 1"));
//        board = new BoardInterface(BoardBuilder.buildBoard("r3k2r/p7/1pp5/2p5/2P5/1PP5/P7/4K3 w kq - 0 1"));
        PawnMapWhite map = new PawnMapWhite(new PawnNumber(), new PieceNumber());
        map.deduce(board);
        System.out.println(map.getPawnOrigins());
        Assertions.assertEquals(1, map.getPawnOrigins().get(new Coordinate(2, 3)).size());

        PawnMapBlack blackMap = new PawnMapBlack(new PawnNumber(), new PieceNumber());
        blackMap.deduce(board);
        System.out.println(blackMap.getPawnOrigins());
        Assertions.assertEquals(1, blackMap.getPawnOrigins().get(new Coordinate(2, 4)).size());
        Assertions.assertTrue(map.getState());
        Assertions.assertTrue(blackMap.getState());
    }

    @Test
    void testPawnMapsThree() {
        BoardInterface board = new BoardInterface(BoardBuilder.buildBoard("4k3/5p2/p2pp3/1p6/1p6/1PPPP3/2P2PPP/4K3 w - - 0 1"));
        PawnMapWhite map = new PawnMapWhite(new PawnNumber(), new PieceNumber());
        map.deduce(board);
        System.out.println(map.getPawnOrigins());
        Assertions.assertEquals(1, map.getPawnOrigins().get(new Coordinate(1, 2)).size());
        Assertions.assertEquals(1, map.getPawnOrigins().get(new Coordinate(2, 1)).size());
        Assertions.assertEquals(1, map.getPawnOrigins().get(new Coordinate(2, 2)).size());
        Assertions.assertEquals(2, map.getPawnOrigins().get(new Coordinate(3, 2)).size());
        Assertions.assertEquals(2, map.getPawnOrigins().get(new Coordinate(4, 2)).size());
        Assertions.assertEquals(1, map.getPawnOrigins().get(new Coordinate(5, 1)).size());
        Assertions.assertEquals(1, map.getPawnOrigins().get(new Coordinate(7, 1)).size());
        Assertions.assertEquals(1, map.getPawnOrigins().get(new Coordinate(6, 1)).size());
        Assertions.assertTrue(map.getState());
    }

    @Test
    void testPawnMapFour() {
        BoardInterface board = new BoardInterface(BoardBuilder.buildBoard("4k3/p4p2/3ppp2/1p6/1p6/1PPPP3/2P2PPP/4K3 w - - 0 1"));
        PawnMapBlack blackMap = new PawnMapBlack(new PawnNumber(), new PieceNumber());
        blackMap.deduce(board);
        System.out.println(blackMap.getPawnOrigins());
        Assertions.assertEquals(1, blackMap.getPawnOrigins().get(new Coordinate(0, 6)).size());
        Assertions.assertEquals(3, blackMap.getPawnOrigins().get(new Coordinate(1, 4)).size());
        Assertions.assertEquals(4, blackMap.getPawnOrigins().get(new Coordinate(1, 3)).size());
        Assertions.assertEquals(3, blackMap.getPawnOrigins().get(new Coordinate(3, 5)).size());
        Assertions.assertEquals(2, blackMap.getPawnOrigins().get(new Coordinate(4, 5)).size());
        Assertions.assertEquals(1, blackMap.getPawnOrigins().get(new Coordinate(5, 5)).size());
        Assertions.assertTrue(blackMap.getState());

    }

    @Test
    void testPawnMapFive() {
        BoardInterface board = new BoardInterface(BoardBuilder.buildBoard("4k3/p4p2/3ppp2/1p6/1p6/2PPPPP1/2P4P/4K3 w - - 0 1"));
        PawnMapWhite whiteMap = new PawnMapWhite(new PawnNumber(), new PieceNumber());
        whiteMap.deduce(board);
        System.out.println(whiteMap.getPawnOrigins());
        Assertions.assertEquals(1, whiteMap.getPawnOrigins().get(new Coordinate(2, 2)).size());
        Assertions.assertEquals(new Coordinate(1, 1), whiteMap.getPawnOrigins().get(new Coordinate(2, 2)).get(0));
        Assertions.assertTrue(whiteMap.getState());

    }

    @Test
    void testCaptures() {
        BoardInterface board = new BoardInterface(BoardBuilder.buildBoard("rnbqkbnr/pppppppp/8/8/8/1P6/3PPPPP/RNBQKBNR w KQkq - 0 1"));
        PawnMapWhite whiteMap = new PawnMapWhite(new PawnNumber(), new PieceNumber());
        for (Observation o : whiteMap.getObservations()) {
            o.observe(board);
        }
        whiteMap.deduce(board);
        Assertions.assertEquals(1, whiteMap.getPawnOrigins().get(new Coordinate(1, 2)).size());
        Assertions.assertEquals(new Coordinate(1, 1), whiteMap.getPawnOrigins().get(new Coordinate(1, 2)).get(0));
        Assertions.assertTrue(whiteMap.getState());
    }

    @Test
    void testCapturesTwo() {
        BoardInterface board = new BoardInterface(BoardBuilder.buildBoard("rnbqkbnr/pppppppp/5P2/2P5/P3P3/1P1P2PP/8/RNBQKBNR w KQkq - 0 1"));
        PawnMapWhite whiteMap = new PawnMapWhite(new PawnNumber(), new PieceNumber());
        for (Observation o : whiteMap.getObservations()) {
            o.observe(board);
        }
        whiteMap.deduce(board);
        System.out.println(whiteMap.getPawnOrigins());
        whiteMap.getPawnOrigins().entrySet().stream().forEach(entry ->{
            Assertions.assertEquals(1, entry.getValue().size());
            Assertions.assertEquals(new Coordinate(entry.getKey().getX(), 1), entry.getValue().get(0));
        });

        Assertions.assertTrue(whiteMap.getState());


    }

    @Test
    void testContradictoryCaptures() {
        BoardInterface board = new BoardInterface(BoardBuilder.buildBoard("rnbqkbnr/pppp2pp/8/8/P7/5P2/1P1PPP1P/RNBQKBNR w KQkq - 0 1"));
        PawnMapWhite whiteMap = new PawnMapWhite(new PawnNumber(), new PieceNumber());
        for (Observation o : whiteMap.getObservations()) {
            o.observe(board);
        }
        whiteMap.deduce(board);

        System.out.println(whiteMap.getPawnOrigins());
        Assertions.assertEquals(1, whiteMap.getPawnOrigins().get(new Coordinate(0, 3)).size());
        Assertions.assertEquals(new Coordinate(0, 1), whiteMap.getPawnOrigins().get(new Coordinate(0, 3)).get(0));
        Assertions.assertTrue(whiteMap.getState());

    }

    @Test
    void testCapturesThree() {
        BoardInterface board = new BoardInterface(BoardBuilder.buildBoard("r2qkb1r/p1pppppp/1p6/8/4P3/8/PP1PPPPP/RNBQKBNR w KQkq - 0 1"));
        PawnMapWhite whiteMap = new PawnMapWhite(new PawnNumber(), new PieceNumber());
        for (Observation o : whiteMap.getObservations()) {
            o.observe(board);
        }
        whiteMap.deduce(board);

        System.out.println(whiteMap.getPawnOrigins());
        Assertions.assertEquals(1, whiteMap.getPawnOrigins().get(new Coordinate(4, 3)).size());
//        Assertions.assertEquals(new Coordinate(0, 1), whiteMap.getPawnOrigins().get(new Coordinate(0, 3)).get(0));
        Assertions.assertTrue(whiteMap.getState());

    }

    @Test
    void testCapturesFour() {
        BoardInterface board = new BoardInterface(BoardBuilder.buildBoard("r2qkb1r/p1pppppp/1p6/8/4P3/8/PP1PPP1P/RNBQKBNR w KQkq - 0 1"));
        PawnMapWhite whiteMap = new PawnMapWhite(new PawnNumber(), new PieceNumber());
        for (Observation o : whiteMap.getObservations()) {
            o.observe(board);
        }
        whiteMap.deduce(board);

        System.out.println(whiteMap.getPawnOrigins());
        Assertions.assertEquals(2, whiteMap.getPawnOrigins().get(new Coordinate(4, 3)).size());
        Assertions.assertTrue(whiteMap.getState());

    }

    @Test
    void testCapturesFive() {
        BoardInterface board = new BoardInterface(BoardBuilder.buildBoard("r2qkb1r/p1pppppp/1p6/5P2/8/8/PP1PPP1P/RNBQKBNR w KQkq - 0 1"));
        PawnMapWhite whiteMap = new PawnMapWhite(new PawnNumber(), new PieceNumber());
        for (Observation o : whiteMap.getObservations()) {
            o.observe(board);
        }
        whiteMap.deduce(board);

        System.out.println(whiteMap.getPawnOrigins());
        Assertions.assertEquals(2, whiteMap.getPawnOrigins().get(new Coordinate(5, 4)).size());
//        Assertions.assertEquals(new Coordinate(0, 1), whiteMap.getPawnOrigins().get(new Coordinate(0, 3)).get(0));
        Assertions.assertTrue(whiteMap.getState());

    }

    @Test
    void testCapturesSix() {
        BoardInterface board = new BoardInterface(BoardBuilder.buildBoard("r2qkb1r/p1pppppp/1p6/8/2P2P2/8/1P1PPPP1/RNBQKBNR w KQkq - 0 1"));
        PawnMapWhite whiteMap = new PawnMapWhite(new PawnNumber(), new PieceNumber());
        for (Observation o : whiteMap.getObservations()) {
            o.observe(board);
        }
        whiteMap.deduce(board);

        System.out.println(whiteMap.getPawnOrigins());
        Assertions.assertEquals(1, whiteMap.getPawnOrigins().get(new Coordinate(2, 3)).size());
        Assertions.assertEquals(1, whiteMap.getPawnOrigins().get(new Coordinate(5, 3)).size());
//        Assertions.assertEquals(new Coordinate(0, 1), whiteMap.getPawnOrigins().get(new Coordinate(0, 3)).get(0));
        Assertions.assertTrue(whiteMap.getState());

    }

    @Test
    void testCapturesSeven() {
        BoardInterface board = new BoardInterface(BoardBuilder.buildBoard("r2qkb1r/p1pppppp/1p6/8/2P2P2/8/1P1PP1P1/RNBQKBNR w KQkq - 0 1"));
        PawnMapWhite whiteMap = new PawnMapWhite(new PawnNumber(), new PieceNumber());
        for (Observation o : whiteMap.getObservations()) {
            o.observe(board);
        }
        whiteMap.deduce(board);

        System.out.println(whiteMap.getPawnOrigins());
        Assertions.assertEquals(2, whiteMap.getPawnOrigins().get(new Coordinate(2, 3)).size());
        Assertions.assertEquals(2, whiteMap.getPawnOrigins().get(new Coordinate(5, 3)).size());
//        Assertions.assertEquals(new Coordinate(0, 1), whiteMap.getPawnOrigins().get(new Coordinate(0, 3)).get(0));
        Assertions.assertTrue(whiteMap.getState());

    }

    @Test
    void testCapturesEight() {
        BoardInterface board = new BoardInterface(BoardBuilder.buildBoard("rnbqkb1r/pppppppp/8/8/8/6PP/PPPPPP2/RNBQKBNR w KQkq - 0 1"));
        PawnMapWhite whiteMap = new PawnMapWhite(new PawnNumber(), new PieceNumber());
        for (Observation o : whiteMap.getObservations()) {
            o.observe(board);
        }
        whiteMap.deduce(board);

        System.out.println(whiteMap.getPawnOrigins());
        Assertions.assertEquals(1, whiteMap.getPawnOrigins().get(new Coordinate(6, 2)).size());
        Assertions.assertEquals(1, whiteMap.getPawnOrigins().get(new Coordinate(7, 2)).size());
        Assertions.assertTrue(whiteMap.getState());

    }

    @Test
    void testCapturesNine() {
        BoardInterface board = new BoardInterface(BoardBuilder.buildBoard("rnbqkb1r/pppppppp/8/8/8/PPPPPPPP/8/RNBQKBNR w KQkq - 0 1"));
        PawnMapWhite whiteMap = new PawnMapWhite(new PawnNumber(), new PieceNumber());
        for (Observation o : whiteMap.getObservations()) {
            o.observe(board);
        }
        whiteMap.deduce(board);

        System.out.println(whiteMap.getPawnOrigins());
        for (int i = 0 ; i < 8 ; i++) {
            System.out.println(i);
            Assertions.assertEquals(1, whiteMap.getPawnOrigins().get(new Coordinate(i, 2)).size());
        }
        Assertions.assertTrue(whiteMap.getState());

    }


    @Test
    void p() {

        //NOT A TEST
        BoardInterface board = new BoardInterface(BoardBuilder.buildBoard("4k3/3ppp2/8/p2PP2p/8/8/1PPP1PP1/RNBQKBNR w KQ - 0 1"));
//        board = new BoardInterface(BoardBuilder.buildBoard("r3k2r/p7/1pp5/2p5/2P5/1PP5/P7/4K3 w kq - 0 1"));
        PawnMapWhite map = new PawnMapWhite(new PawnNumber(), new PieceNumber());
        map.deduce(board);
        System.out.println(map.getPawnOrigins());
        Map<Coordinate, Path> mape = new HashMap<>();
        mape.putAll(map.getPawnOrigins());
        mape.clear();
        System.out.println(map.getPawnOrigins());
        System.out.println(map.capturedPieces());
        System.out.println(map.getCaptureSet());



////        System.out.println(map.getPawnOrigins().get(new Coordinate(1, 2)).hashCode());
////        System.out.println(map.getPawnOrigins().get(new Coordinate(2, 2)).equals(map.getPawnOrigins().get(new Coordinate(1, 2))));
//
//        PawnMapWhite map2 = new PawnMapWhite(new PawnNumber(), new PieceNumber());
//        map2.deduce(board);
//
//
//
//        PawnMapBlack blackMap = new PawnMapBlack(new PawnNumber(), new PieceNumber());
//        blackMap.deduce(board);
//        System.out.println(blackMap.getPawnOrigins());
////        System.out.println(map.getMaxCaptures(new Coordinate(2, 3)));
//
//        System.out.println(map.getCaptureSet());
//        CombinedPawnMap combinedPawnMap = new CombinedPawnMap(map, blackMap);
//        combinedPawnMap.deduce(board);
//        System.out.println(combinedPawnMap.getWhitePaths());
////        combinedPawnMap.getWhitePaths().entrySet().stream().forEach(entry -> {
////            entry.getValue().stream().reduce((path, path2) ->
////                    path.stream()
////                            .reduce(path.get(0), ((coordinate, coordinate2) -> {coordinate.})))
////        });
//        System.out.println(combinedPawnMap.captures("white"));
//        System.out.println(combinedPawnMap.captures("black"));
//        System.out.println(map.getPawnOrigins());
//        System.out.println(blackMap.getPawnOrigins());
//        System.out.println(map.getOriginFree());
//        System.out.println(blackMap.getOriginFree());
//        System.out.println(map.capturedPieces());
//        System.out.println(blackMap.capturedPieces());
//        System.out.println(map.getCaptureSet());
//        System.out.println(combinedPawnMap.getSinglePath("white", new Coordinate(0, 4)));



    }

}
