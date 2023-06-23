import Heuristics.BoardInterface;
import Heuristics.Deductions.CombinedPawnMap;
import Heuristics.Deductions.PawnMapBlack;
import Heuristics.Deductions.PawnMapWhite;
import Heuristics.Observation;
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
    void testWhiteExclusive(){
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
    }

    @Test
    void testWhiteExclusive(){
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
    }
}
