import Heuristics.BoardInterface;
import SolveAlgorithm.BoardComparator;
import StandardChess.BoardBuilder;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Hashtable;

public class BoardComparatorTest {

    @Test
    public void test1() {
        System.out.println(new BoardComparator().compare(
                new BoardInterface(BoardBuilder.buildBoard("7r/pppppKpP/6P1/8/1k6/8/n7/8 w - -")),
                new BoardInterface(BoardBuilder.buildBoard("7r/pppppKpP/6P1/8/4k3/8/n7/8 b - -"))));
    }

    @Test
    public void test2() {
        System.out.println(
                new BoardInterface(BoardBuilder.buildBoard("r3k2r/p1pp1ppp/1pn4n/1b2p1q1/1b6/8/PPPPPPPP/1NBQKBNR")).hashCode());
        System.out.println(new BoardInterface(BoardBuilder.buildBoard("r3k2r/p1pp1ppp/bpn3qn/4p3/1b6/8/PPPPPPPP/1NBQKBNR")).hashCode());
    }

    @Test
    public void test3() {
        System.out.println(
                new BoardInterface(BoardBuilder.buildBoard("r3k2r/p1pp1ppp/bp2p2n/n7/1b6/6q1/PPPPPPPP/1NBQKBNR")).hashCode());
        System.out.println(new BoardInterface(BoardBuilder.buildBoard("r3k2r/p1pp1ppp/bpn1p2n/8/1b6/3q4/PPPPPPPP/1NBQKBNR")).hashCode());
    }

    @Test
    public void test4() {
        System.out.println(
                new BoardInterface(BoardBuilder.buildBoard("r3k2r/p1pp1ppp/1pn4n/1b2p1q1/1b6/8/PPPPPPPP/1NBQKBNR")).hashCode());
        System.out.println(new BoardInterface(BoardBuilder.buildBoard("r3k2r/p1pp1ppp/bpn5/4p3/1b4nq/8/PPPPPPPP/1NBQKBNR")).hashCode());
        System.out.println(new BoardInterface(BoardBuilder.buildBoard("r3k2r/p1pp1ppp/1pn4n/1b2p3/1b5q/8/PPPPPPPP/1NBQKBNR ")).hashCode());

        Hashtable<Integer, Boolean> hash = new Hashtable<>();
        hash.put(new BoardInterface(BoardBuilder.buildBoard("r3k2r/p1pp1ppp/1pn4n/1b2p1q1/1b6/8/PPPPPPPP/1NBQKBNR")).hashCode(), true);
        System.out.println(hash.containsKey(new BoardInterface(BoardBuilder.buildBoard("r3k2r/p1pp1ppp/bpn5/4p3/1b4nq/8/PPPPPPPP/1NBQKBNR")).hashCode()));
        System.out.println(hash.size());




    }
}
