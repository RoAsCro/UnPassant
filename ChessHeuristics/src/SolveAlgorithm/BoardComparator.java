package SolveAlgorithm;

import Heuristics.BoardInterface;
import StandardChess.ChessBoard;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

public class BoardComparator implements Comparator<BoardInterface> {

    private final static List<String> PIECE_CODES = List.of(
            "rook", "knight", "bishop", "queen"
    );
    public int compare(BoardInterface o1, BoardInterface o2) {
        boolean equal = o1.getBoardFacts().getCoordinates(true, "pawn").equals(o2.getBoardFacts().getCoordinates(true, "pawn"))
                &&
                o1.getBoardFacts().getCoordinates(false, "pawn").equals(o2.getBoardFacts().getCoordinates(false, "pawn"))
                &&
                PIECE_CODES.stream().allMatch(piece ->
                        o1.getBoardFacts().getCoordinates(false, piece).size() == o2.getBoardFacts().getCoordinates(false, piece).size()
                                &&
                                o1.getBoardFacts().getCoordinates(true, piece).size() == o2.getBoardFacts().getCoordinates(true, piece).size());

//        System.out.println("-----------");
//        System.out.println(o1.getReader().toFEN());
//        System.out.println(o2.getReader().toFEN());

        if (equal) {
            String[] o1FEN = o1.getReader().toFEN().split(" ");
            String[] o2FEN = o2.getReader().toFEN().split(" ");
            String[] o1Board = o1FEN[0].split("/");
            String[] o2Board = o2FEN[0].split("/");
            String o1CriticalRegion = o1Board[0] + o1Board[1] + o1Board[6] + o1Board[7];
            String o2CriticalRegion = o2Board[0] + o2Board[1] + o2Board[6] + o2Board[7];
            equal = o1CriticalRegion.equals(o2CriticalRegion) && o1FEN[2].equals(o2FEN[2]);
        }
        int comparison = equal ? 0 : o1.hashCode() - o2.hashCode();
//        System.out.println(equal);
        return comparison;
    }
}
