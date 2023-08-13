package SolveAlgorithm;

import Heuristics.BoardInterface;
import StandardChess.Coordinate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class StateLog {
    Map<Integer, Boolean> log = new HashMap<>();

    private final static Map<String, Integer> PIECE_CODES = Map.of(
            "rook", 15, "knight", 17, "bishop", 19, "queen", 23
    );

    public int test(BoardInterface boardInterface) {
//        System.out.println(log.containsKey(boardInterface));
       Boolean present = log.getOrDefault(boardInterface.hashCode(), null);
////        System.out.println(present);
//        System.out.println(boardInterface.getReader().toFEN() + " " + present);

        if (present == null) {
           return 0;
       }
       return present ? 1 : -1;
    }

    public void register(BoardInterface boardInterface, boolean state) {
//        System.out.println(log.size());
//        System.out.println(boardInterface.getReader().toFEN() + "?" + state);
//        System.out.println();
//        System.out.println("!!!!!!!!!!!!!!!");


        log.put(boardInterface.hashCode(), state);
    }
}
