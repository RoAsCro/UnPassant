import StandardChess.Coordinate;
import StandardChess.NullPiece;
import StandardChess.StandardPieceFactory;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

public class GameTest {

    public String gameTest(String moves, String fen) {
        String[] moveArray = moves.split("/");
        Game game = new Game(fen);
        for (int i = moveArray.length - 1 ; i >= 0 ; i--){
            String[] components = moveArray[i].split(" ");
            game.setCaptureFlag(components[0].equals("x"));
            String capturePiece = components[4];

            game.setCapturePiece(capturePiece.equals("-")
                    ? NullPiece.getInstance()
                    : StandardPieceFactory.getInstance().getPiece(
                            game.getTurn().equals("black")
                            ? capturePiece.toUpperCase()
                            : capturePiece.toLowerCase()));
            boolean promo = !components[2].equals("-");
            game.setPromotionFlag(promo);
            Coordinate origin = new Coordinate(components[1].charAt(0)  - 97, components[1].charAt(1) - 49);
            Coordinate target = new Coordinate(components[3].charAt(0)  - 97, components[3].charAt(1) - 49);
            Assertions.assertTrue(game.makeMove(origin, target), moveArray[i] + game.getFen());
        }
        return game.getFen();
    }

    @Test
    public void testGameOne() {
        String FEN = gameTest("- g3 - g2 -/- a2 - f7 -/- f2 - g1 -/- b1 - a2 -/- e1 - e3 -/x e1+ - b1 q/x e1 - f2 q/x c3 - c5 r/x c3 - b2 r/- c7 - g7 -/- e2 - d4 -/- d5 - d6 -/- b2 - d2 -/- b5 - b6 -/- d2 - e1 -/- g6 - h7 -/- a4 - a3 -/x a4 - b5 p/- b6+ - b2 -/- h5 - g6 -/x a6 - b6 p/- g4 - h5 -/x a4 - a6 p/- h3 - g4 -/- a6 - a4 -/x h2 - h3 p/x h6+ - a6 p/- g2 - h2 -/- g6 - h6 -/- a7 - c7 -/- e3 - d2 -/- e7 - a7 -/- d2 - e3 -/- f2 - g2 -/- d4 - e2 -/- a7 - e7 -/- g4 - g3 -/- a2+ - a7 -/- c1 - d2 -/- e3 - f2 -/x f5 - g4 p/x f4 - e3 p/- h6 - g6 -/- a8 - a2 -/- f6 - f5 -/- f3 - e4 -/- f7 - f6 -/- g2 - f3 -/- f8 q f7 -/",
                "r4Q2/8/7R/3p4/3N1k2/2P5/6b1/2K5 w - - 0 1");
        Assertions.assertEquals("8/5qrk/pp1p3p/2r2p2/3NbP2/P1R1Q3/1P1R2PP/6K1 b - -", FEN);
    }

    @Test
    public void diagnose() {
        System.out.println(gameTest("x a4 - a6 p/- h3 - g4 -/- a6 - a4 -/x h2 - h3 p/x h6+ - a6 p/- g2 - h2 -/- g6 - h6 -/- a7 - c7 -/- e3 - d2 -/- e7 - a7 -/- d2 - e3 -/- f2 - g2 -/- d4 - e2 -/- a7 - e7 -/- g4 - g3 -/- a2+ - a7 -/- c1 - d2 -/- e3 - f2 -/x f5 - g4 p/x f4 - e3 p/- h6 - g6 -/- a8 - a2 -/- f6 - f5 -/- f3 - e4 -/- f7 - f6 -/- g2 - f3 -/- f8 q f7 -/",
                "r4Q2/8/7R/3p4/3N1k2/2P5/6b1/2K5 w - - 0 1"));
    }

}
