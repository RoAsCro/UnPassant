import Heuristics.BoardInterface;
import Heuristics.Deductions.CombinedPawnMap;
import Heuristics.Deductions.PawnMapBlack;
import Heuristics.Deductions.PawnMapWhite;
import Heuristics.Observation;
import Heuristics.Path;
import Heuristics.Pathfinder;
import StandardChess.BoardBuilder;
import StandardChess.Coordinate;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class CombinedPawnMapTest {

    @Test
    void testWhitePawnPaths(){
        BoardInterface board = new BoardInterface(BoardBuilder.buildBoard("4k3/7p/7p/P6p/P6p/P7/P7/4K3 w - - 0 1"));
        PawnMapWhite map = new PawnMapWhite();
        PawnMapBlack blackMap = new PawnMapBlack();
        for (Observation o : map.getObservations()) {
            o.observe(board);
        }
        for (Observation o : blackMap.getObservations()) {
            o.observe(board);
        }
        CombinedPawnMap combinedPawnMap = new CombinedPawnMap(map, blackMap);
        combinedPawnMap.deduce(board);
        System.out.println(map.getPawnOrigins());
        System.out.println(combinedPawnMap.getWhitePaths());
        System.out.println(combinedPawnMap.getBlackPaths());
    }

    @Test
    void testWhitePawnPathsTwo(){
        BoardInterface board = new BoardInterface(BoardBuilder.buildBoard("4k3/7p/7p/7p/2P4p/8/PP1P4/4K3 w - - 0 1"));
        PawnMapWhite map = new PawnMapWhite();
        PawnMapBlack blackMap = new PawnMapBlack();
        for (Observation o : map.getObservations()) {
            o.observe(board);
        }
        for (Observation o : blackMap.getObservations()) {
            o.observe(board);
        }
        CombinedPawnMap combinedPawnMap = new CombinedPawnMap(map, blackMap);
        combinedPawnMap.deduce(board);
        System.out.println(map.getPawnOrigins());
        System.out.println(combinedPawnMap.getWhitePaths());
    }

    @Test
    void testWhiteExclusivePawnPaths(){
        BoardInterface board = new BoardInterface(BoardBuilder.buildBoard("rnbqkbnr/2pppppp/8/P7/p7/8/1PPPPPPP/RNBQKB1R w KQkq - 0 1"));
        PawnMapWhite map = new PawnMapWhite();
        PawnMapBlack blackMap = new PawnMapBlack();
        for (Observation o : map.getObservations()) {
            o.observe(board);
        }
        for (Observation o : blackMap.getObservations()) {
            o.observe(board);
        }
        CombinedPawnMap combinedPawnMap = new CombinedPawnMap(map, blackMap);
        combinedPawnMap.deduce(board);
        System.out.println(map.getPawnOrigins().get(new Coordinate(0, 4)));
        System.out.println(combinedPawnMap.getWhitePaths().get(new Coordinate(0, 4)));
        System.out.println(blackMap.getPawnOrigins().get(new Coordinate(0, 3)));
        System.out.println(combinedPawnMap.getBlackPaths().get(new Coordinate(0, 3)));
        Assertions.assertEquals(1, blackMap.getPawnOrigins().get(new Coordinate(0, 3)).size());
    }

    @Test
    void testWhiteMultiOrigin(){
        BoardInterface board = new BoardInterface(BoardBuilder.buildBoard("rnbqkbnr/8/8/8/8/P7/8/RNBQKB1R w KQkq - 0 1"));
        PawnMapWhite map = new PawnMapWhite();
        PawnMapBlack blackMap = new PawnMapBlack();
        for (Observation o : map.getObservations()) {
            o.observe(board);
        }
        for (Observation o : blackMap.getObservations()) {
            o.observe(board);
        }
        CombinedPawnMap combinedPawnMap = new CombinedPawnMap(map, blackMap);
        combinedPawnMap.deduce(board);
        System.out.println(map.getPawnOrigins().get(new Coordinate(0, 2)));
        System.out.println(combinedPawnMap.getWhitePaths().get(new Coordinate(0, 2)));
    }

    @Test
    void testExclusive(){
        BoardInterface board = new BoardInterface(BoardBuilder.buildBoard("rnbqkbnr/1ppppppp/8/P7/p7/8/1PPPPPPP/R1BQKB1R w KQkq - 0 1"));
        PawnMapWhite map = new PawnMapWhite();
        PawnMapBlack blackMap = new PawnMapBlack();

        for (Observation o : map.getObservations()) {
            o.observe(board);
        }
        for (Observation o : blackMap.getObservations()) {
            o.observe(board);
        }
        CombinedPawnMap combinedPawnMap = new CombinedPawnMap(map, blackMap);
        combinedPawnMap.deduce(board);
        System.out.println(Pathfinder.pathsExclusive(combinedPawnMap.getBlackPaths().get(new Coordinate(0, 3)).get(0), combinedPawnMap.getWhitePaths().get(new Coordinate(0, 4)).get(0)));
        System.out.println(blackMap.getPawnOrigins().get(new Coordinate(0, 3)));
        System.out.println(combinedPawnMap.getBlackPaths().get(new Coordinate(0, 3)));
        System.out.println(map.getPawnOrigins().get(new Coordinate(0, 4)));
        System.out.println(combinedPawnMap.getWhitePaths().get(new Coordinate(0, 4)));
        Assertions.assertTrue(combinedPawnMap.getBlackPaths().get(new Coordinate(0, 3)).get(0).contains(new Coordinate(1, 4)));
    }

    @Test
    void testExclusiveTwo(){
        BoardInterface board = new BoardInterface(BoardBuilder.buildBoard("rnbqkbnr/2pppppp/8/P7/p7/8/1PPPPPPP/R1BQKBNR w KQkq - 0 1"));
        PawnMapWhite map = new PawnMapWhite();
        PawnMapBlack blackMap = new PawnMapBlack();

        for (Observation o : map.getObservations()) {
            o.observe(board);
        }
        for (Observation o : blackMap.getObservations()) {
            o.observe(board);
        }
        CombinedPawnMap combinedPawnMap = new CombinedPawnMap(map, blackMap);
        combinedPawnMap.deduce(board);
        System.out.println(blackMap.getPawnOrigins().get(new Coordinate(0, 3)));
        System.out.println(combinedPawnMap.getBlackPaths().get(new Coordinate(0, 3)));
        System.out.println(map.getPawnOrigins().get(new Coordinate(0, 4)));
        System.out.println(combinedPawnMap.getWhitePaths().get(new Coordinate(0, 4)));
        Assertions.assertEquals(1, blackMap.getPawnOrigins().get(new Coordinate(0, 3)).size());
    }

    @Test
    void testExclusiveThree(){
        BoardInterface board = new BoardInterface(BoardBuilder.buildBoard("r1b1kb1r/pp1pp1pp/8/4P3/P4p2/8/1PPPPPP1/2BQKB1R w Kkq - 0 1"));
        PawnMapWhite map = new PawnMapWhite();
        PawnMapBlack blackMap = new PawnMapBlack();

        for (Observation o : map.getObservations()) {
            o.observe(board);
        }
        for (Observation o : blackMap.getObservations()) {
            o.observe(board);
        }
        CombinedPawnMap combinedPawnMap = new CombinedPawnMap(map, blackMap);
        combinedPawnMap.deduce(board);
        System.out.println(combinedPawnMap.getBlackPaths().get(new Coordinate(5, 3)));
        System.out.println(combinedPawnMap.getWhitePaths().get(new Coordinate(4, 4)));
        Assertions.assertEquals(1, blackMap.getPawnOrigins().get(new Coordinate(5, 3)).size());
    }

    @Test
    void testExclusiveFour(){
        BoardInterface board = new BoardInterface(BoardBuilder.buildBoard("rnbqkbnr/1ppppppp/8/P7/p7/8/2PPPPPP/R1BQKBNR w KQkq - 0 1"));
        PawnMapWhite map = new PawnMapWhite();
        PawnMapBlack blackMap = new PawnMapBlack();

        for (Observation o : map.getObservations()) {
            o.observe(board);
        }
        for (Observation o : blackMap.getObservations()) {
            o.observe(board);
        }
        CombinedPawnMap combinedPawnMap = new CombinedPawnMap(map, blackMap);
        combinedPawnMap.deduce(board);
        System.out.println(blackMap.getPawnOrigins().get(new Coordinate(0, 3)));
        System.out.println(combinedPawnMap.getBlackPaths().get(new Coordinate(0, 3)));
        System.out.println(map.getPawnOrigins().get(new Coordinate(0, 4)));
        System.out.println(combinedPawnMap.getWhitePaths().get(new Coordinate(0, 4)));
        Path path = new Path();
        for (int i = 6 ; i > 2 ; i--) {
            path.add(new Coordinate(0, i));
        }
        Assertions.assertFalse(combinedPawnMap.getBlackPaths().get(new Coordinate(0, 3)).contains(path));
    }

    @Test
    void testExclusiveFive(){
        BoardInterface board = new BoardInterface(BoardBuilder.buildBoard("r1bqkbnr/1ppppppp/8/P7/p7/8/2PPPPPP/R1BQKBNR w KQkq - 0 1"));
        PawnMapWhite map = new PawnMapWhite();
        PawnMapBlack blackMap = new PawnMapBlack();

        for (Observation o : map.getObservations()) {
            o.observe(board);
        }
        for (Observation o : blackMap.getObservations()) {
            o.observe(board);
        }
        CombinedPawnMap combinedPawnMap = new CombinedPawnMap(map, blackMap);
        combinedPawnMap.deduce(board);
        System.out.println(blackMap.getPawnOrigins().get(new Coordinate(0, 3)));
        System.out.println(combinedPawnMap.getBlackPaths().get(new Coordinate(0, 3)));
        System.out.println(map.getPawnOrigins().get(new Coordinate(0, 4)));
        System.out.println(combinedPawnMap.getWhitePaths().get(new Coordinate(0, 4)));
        Path path = new Path();
        for (int i = 6 ; i > 2 ; i--) {
            path.add(new Coordinate(0, i));
        }
        System.out.println(path);
        // If this fails, it may be due to implementations changing
        Assertions.assertTrue(combinedPawnMap.getBlackPaths().get(new Coordinate(0, 3)).contains(path));
    }

    @Test
    void testExclusiveSix(){
        BoardInterface board = new BoardInterface(BoardBuilder.buildBoard("r1bqk2r/ppp1pppp/8/3PP3/3p4/8/PPP1PPP1/2B1K2R w Kkq - 0 1"));
        PawnMapWhite map = new PawnMapWhite();
        PawnMapBlack blackMap = new PawnMapBlack();

        for (Observation o : map.getObservations()) {
            o.observe(board);
        }
        for (Observation o : blackMap.getObservations()) {
            o.observe(board);
        }
        CombinedPawnMap combinedPawnMap = new CombinedPawnMap(map, blackMap);
        combinedPawnMap.deduce(board);
        System.out.println(combinedPawnMap.getBlackPaths().get(new Coordinate(3, 3)));
        System.out.println(combinedPawnMap.getWhitePaths().get(new Coordinate(4, 4)));
        System.out.println(combinedPawnMap.getWhitePaths().get(new Coordinate(3, 4)));
        Assertions.assertTrue(combinedPawnMap.getBlackPaths().get(new Coordinate(3, 3)).get(0).contains(new Coordinate(2, 4)));
    }

    @Test
    void testExclusiveSeven(){
        BoardInterface board = new BoardInterface(BoardBuilder.buildBoard("rnbqkbnr/ppp1pppp/8/3P4/3p4/8/PPP1PPPP/R1BQKB1R w KQkq - 0 1"));
        PawnMapWhite map = new PawnMapWhite();
        PawnMapBlack blackMap = new PawnMapBlack();

        for (Observation o : map.getObservations()) {
            o.observe(board);
        }
        for (Observation o : blackMap.getObservations()) {
            o.observe(board);
        }
        CombinedPawnMap combinedPawnMap = new CombinedPawnMap(map, blackMap);
        combinedPawnMap.deduce(board);
        System.out.println(combinedPawnMap.getBlackPaths().get(new Coordinate(3, 3)));
        System.out.println(combinedPawnMap.getWhitePaths().get(new Coordinate(3, 4)));
        Assertions.assertFalse(combinedPawnMap.getBlackPaths().get(new Coordinate(3, 3)).get(0).contains(new Coordinate(3, 4)));
    }

    @Test
    void testExclusiveEight(){
        BoardInterface board = new BoardInterface(BoardBuilder.buildBoard("r1bqkb1r/1ppppppp/8/P7/p7/8/1PPPPPPP/R1BQKBNR w KQkq - 0 1"));
        PawnMapWhite map = new PawnMapWhite();
        PawnMapBlack blackMap = new PawnMapBlack();

        for (Observation o : map.getObservations()) {
            o.observe(board);
        }
        for (Observation o : blackMap.getObservations()) {
            o.observe(board);
        }
        CombinedPawnMap combinedPawnMap = new CombinedPawnMap(map, blackMap);
        combinedPawnMap.deduce(board);
        System.out.println(blackMap.getPawnOrigins().get(new Coordinate(0, 3)));
        System.out.println(combinedPawnMap.getBlackPaths().get(new Coordinate(0, 3)));
        System.out.println(map.getPawnOrigins().get(new Coordinate(0, 4)));
        System.out.println(combinedPawnMap.getWhitePaths().get(new Coordinate(0, 4)));
        Path path = new Path();
        Assertions.assertTrue(combinedPawnMap.getBlackPaths().get(new Coordinate(0, 3)).get(0).contains(new Coordinate(0, 4)));
//        Assertions.assertTrue(combinedPawnMap.getBlackPaths().get(new Coordinate(0, 3)).get(0).contains(new Coordinate(0, 4)));

    }

    @Test
    void testExclusiveNine(){
        //Two captures, one has been claimed
        BoardInterface board = new BoardInterface(BoardBuilder.buildBoard("rnbqkbnr/2p1pppp/P3p3/p7/8/8/1PPPPPPP/R1BQKB1R w KQkq - 0 1"));
        PawnMapWhite map = new PawnMapWhite();
        PawnMapBlack blackMap = new PawnMapBlack();

        for (Observation o : map.getObservations()) {
            o.observe(board);
        }
        for (Observation o : blackMap.getObservations()) {
            o.observe(board);
        }
        CombinedPawnMap combinedPawnMap = new CombinedPawnMap(map, blackMap);
        combinedPawnMap.deduce(board);
        System.out.println(blackMap.getPawnOrigins().get(new Coordinate(0, 4)));
        System.out.println(combinedPawnMap.getBlackPaths().get(new Coordinate(0, 4)));
        Assertions.assertEquals(1, blackMap.getPawnOrigins().get(new Coordinate(0, 4)).size());

    }


}
