import Heuristics.Observation;
import Heuristics.Observations.PawnNumber;
import Heuristics.Observations.PieceNumber;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class AbstractObservationTest {

    @Test
    void testHashCode() {
        Observation a = new PawnNumber();
        Observation b = new PieceNumber();
        Observation c = new PawnNumber();
        Observation d = new PieceNumber();
        Assertions.assertEquals(a.hashCode(), c.hashCode());
        Assertions.assertEquals(b.hashCode(), d.hashCode());
        Assertions.assertNotEquals(a.hashCode(), b.hashCode());
        Assertions.assertNotEquals(c.hashCode(), d.hashCode());
    }
}
