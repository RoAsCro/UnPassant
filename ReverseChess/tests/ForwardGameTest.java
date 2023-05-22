import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class ForwardGameTest {
    TestGamePlayer tester = new TestGamePlayer();
    @Test
    public void testOne() {
        tester.play("e2e4 a7a6 d2d4 e7e6 a2a3 b7b6 f1d3 d7d6 f2f4 g7g6 g1f3 g8e7 e1g1 f8g7 c2c3 c7c5 c1e3 c8b7 b1d2 b8c6 d1e1 h7h6 a1d1 c5d4 c3d4 e8g8 e1g3 f7f5 e4f5 e7f5");
        Assertions.assertEquals("r2q1rk1/1b4b1/ppnpp1pp/5n2/3P1P2/P2BBNQ1/1P1N2PP/3R1RK1 w - -", tester.getBoard().getReader().toFEN());
    }
}
