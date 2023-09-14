package ReverseChess;

import ReverseChess.StandardChess.BoardBuilder;
import ReverseChess.StandardChess.ChessBoard;
import ReverseChess.StandardChess.Coordinate;
import org.junit.jupiter.api.Assertions;

import java.util.Arrays;

public class TestGamePlayer {

    public ChessBoard board = BoardBuilder.buildBoard();
    public MoveMaker moveMaker = new MoveMaker(board);

    public void play(String game) {
        String[] moves = game.split(" ");
        Arrays.stream(moves).forEach(s -> {
            Coordinate origin = new Coordinate(s.charAt(0) - 97, s.charAt(1) - 49);
            Coordinate target = new Coordinate(s.charAt(2) - 97, s.charAt(3) - 49);
            System.out.println("origin = " + origin);
            System.out.println("target = " + target);
            Assertions.assertTrue(moveMaker.makeMove(origin, target));
        });
    }



    public ChessBoard getBoard() {
        return board;
    }


}
