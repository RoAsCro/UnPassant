import Games.Game;
import StandardChess.Coordinate;
import StandardChess.NullPiece;
import StandardChess.StandardPieceFactory;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

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
            if (moveArray[i].contains("x e5 - f6")) {
                game.setEnPassantFlag(true);
            }
            Coordinate origin = new Coordinate(components[1].charAt(0)  - 97, components[1].charAt(1) - 49);
            Coordinate target = new Coordinate(components[3].charAt(0)  - 97, components[3].charAt(1) - 49);
            Assertions.assertTrue(game.makeMove(target, origin), moveArray[i] + game.getFen());
            game.setEnPassantFlag(false);
        }
        return game.getFen();
    }

    @Test
    public void testGameOne() {
        //Sebastien Midoux vs Christopher Baumgartner
        //Reykjavik Open (2016), Reykjavik ISL, rd 5, Mar-11
        String FEN = gameTest("- e2 - e4 -/- e7 - e5 -/- d2 - d4 -/- g8 - f6 -/x d4 - e5 p/x f6 - e4 p/- f1 - c4 -/- d8 - h4 -/- d1 - f3 -/- d7 - d5 -/x c4 - d5 p/- f7 - f5 -/x e5 - f6 p/x e4 - f6 p/- f3 - e2 -/- f8 - e7 -/- g1 - f3 -/- h4 - b4 -/- c2 - c3 -/- b4 - b6 -/- d5 - b3 -/- b8 - c6 -/- b1 - a3 -/- c8 - g4 -/- c1 - e3 -/- b6 - a5 -/- e1 - c1 -/x e7 - a3 n/- e3 - c5 -/- c6 - e7 -/x e2 - e7 n",
                "r3k2r/ppp1Q1pp/5n2/q1B5/6b1/bBP2N2/PP3PPP/2KR3R w kq -");
        Assertions.assertTrue(FEN.contains("rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR"));
    }

//    @Test
//    public void diagnose() {
//        System.out.println(gameTest("x a4 - a6 p/- h3 - g4 -/- a6 - a4 -/x h2 - h3 p/x h6+ - a6 p/- g2 - h2 -/- g6 - h6 -/- a7 - c7 -/- e3 - d2 -/- e7 - a7 -/- d2 - e3 -/- f2 - g2 -/- d4 - e2 -/- a7 - e7 -/- g4 - g3 -/- a2+ - a7 -/- c1 - d2 -/- e3 - f2 -/x f5 - g4 p/x f4 - e3 p/- h6 - g6 -/- a8 - a2 -/- f6 - f5 -/- f3 - e4 -/- f7 - f6 -/- g2 - f3 -/- f8 q f7 -/",
//                "r4Q2/8/7R/3p4/3N1k2/2P5/6b1/2K5 w - - 0 1"));
//    }

}
