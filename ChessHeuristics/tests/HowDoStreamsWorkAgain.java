import StandardChess.Coordinate;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class HowDoStreamsWorkAgain {

    @Test
    void test() {
        List<Coordinate> bishops = List.of(new Coordinate(0,0), new Coordinate(0,2));
        Map<Integer, List<Coordinate>> lightAndDarkSquares =
                bishops.stream().collect(Collectors.groupingBy(c -> (c.getY() + c.getX()) % 2));
        System.out.println(bishops.stream()
                .collect(Collectors.groupingBy(c -> (c.getY() + c.getX()) % 2)));
        lightAndDarkSquares.get(1).size();

    }
}
