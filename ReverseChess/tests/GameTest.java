import StandardChess.Coordinate;
import StandardChess.NullPiece;
import StandardChess.StandardPieceFactory;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

public class GameTest {

    public void gameTest(String moves, String fen) {
        String[] moveArray = moves.split("/");
        Game game = new Game(fen);
        for (int i = moveArray.length - 1 ; i >= 0 ; i--){
            String[] components = moveArray[i].split(" ");
            game.setCaptureFlag(components[0].equals("x"));
            game.setCapturePiece(components[4].equals("-")
                    ? NullPiece.getInstance()
                    : StandardPieceFactory.getInstance().getPiece(components[4]));
            boolean promo = !components[2].equals("-");
            game.setPromotionFlag(promo);
            Coordinate origin = new Coordinate(components[1].charAt(0)  - 97, components[1].charAt(1) - 49);
            Coordinate target = new Coordinate(components[3].charAt(0)  - 97, components[3].charAt(1) - 49);
            Assertions.assertTrue(game.makeMove(origin, target), moveArray[i] + game.getFen());
        }
    }

    @Test
    public void testGameOne() {
        gameTest("- g3 - g2 -/- a2 - f7 -/- f2 - g1 -/- b1 - a2 -/- e1 - e3 -/x e1+ - b1 -/x e1 - f2 q/x c3 - c5 r/x c3 - b2 r/- c7 - g7 -/- e2 - d4 -/- d5 - d6 -/- b2 - d2 -/- b5 - b6 -/- d2 - e1 -/- g6 - h7 -/- a4 - a3 -/x a4 - b5 p/- b6+ - b6 -/- h5 - g6 -/x a6 - b6 p/- g4 - h5 -/x a4 - a6 p/- h3 - g4 -/- a6 - a4 -/x h2 - h3 p/x h6+ - a6 p/- g2 - h2 -/- g6 - h6 -/- a7 - c7 -/- e3 - d2 -/- e7 - a7 -/- d2 - e3 -/- f2 - g2 -/- d4 - d4 -/- a7 - e7 -/- g4 - g3 -/- a2+ - a7 -/- c1 - d2 -/- e3 - f2 -/x f5 - g4 p/x f4 - e3 p/- h6 - g6 -/- a8 - a2 -/- f6 - f5 -/- f3 - e4 -/- f7 - f6 -/- g2 - f3 -/- f8 q f7 -/",
                "r4Q2/8/7R/3pk3/3N4/2P5/6b1/2K5 w - - 0 1");
    }

}
