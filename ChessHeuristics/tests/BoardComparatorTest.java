import Heuristics.BoardInterface;
import SolveAlgorithm.BoardComparator;
import StandardChess.BoardBuilder;
import org.junit.jupiter.api.Test;

public class BoardComparatorTest {

    @Test
    public void test1() {
        System.out.println(new BoardComparator().compare(
                new BoardInterface(BoardBuilder.buildBoard("7r/pppppKpP/6P1/8/1k6/8/n7/8 w - -")),
                new BoardInterface(BoardBuilder.buildBoard("7r/pppppKpP/6P1/8/4k3/8/n7/8 b - -"))));
    }

    @Test
    public void test2() {
        System.out.println(new BoardComparator().compare(
                new BoardInterface(BoardBuilder.buildBoard("7r/pppppKpP/6P1/3k4/8/8/n7/8 b - -")),
                new BoardInterface(BoardBuilder.buildBoard("7r/pppppKpP/6P1/8/4k3/8/n7/8 w - -"))));
    }

    @Test
    public void test3() {
        System.out.println(new BoardComparator().compare(
                new BoardInterface(BoardBuilder.buildBoard("r3k2r/pbpp1ppp/4p3/6q1/1bnn4/5N2/1PPPPPPP/1NBQKB1R b Kkq ")),
                new BoardInterface(BoardBuilder.buildBoard("r3k2r/pbpp1ppp/4p3/6q1/1bnn3N/8/1PPPPPPP/1NBQKB1R w Kkq -"))));
    }
}
