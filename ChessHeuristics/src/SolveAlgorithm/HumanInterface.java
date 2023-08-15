package SolveAlgorithm;
import Game.Game;

import java.io.IOException;

public class HumanInterface extends Game {


    public static void main(String[] args) {
        while (true) {
            System.out.println("Provide a FEN");
            String fen = System.console().readLine();
            HumanInterface humanInterface;
            while (true) {
                try {
                    humanInterface = new HumanInterface(fen);
                    break;
                } catch (IllegalArgumentException e) {
                    System.out.println("FEN illegal");
                }
            }
            System.out.println("Current FEN:" + humanInterface.getFen());
        }
    }
    public HumanInterface(String fen) {
        super(fen);
    }

    private void makeBoard(String fen){

    }
}
