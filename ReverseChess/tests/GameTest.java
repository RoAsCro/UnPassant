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

    @Test
    public void testGameTwo() {
        //Alec Elias Sigurdarson vs Sindri Snaer Kristofersson
        //Reykjavik Open (2014), Reykjavik ISL, rd 6, Mar-08
        String FEN = gameTest("- e2 - e4 -/- e7 - e5 -/- g1 - f3 -/- b8 - c6 -/- f1 - b5 -/- a7 - a6 -/- b5 - a4 -/- f8 - c5 -/- e1 - g1 -/- d7 - d6 -/- c2 - c3 -/- g8 - f6 -/- d2 - d4 -/- c5 - b6 -/- d4 - d5 -/- c8 - d7 -/x d5 - c6 n/x b7 - c6 p/- a4 - c2 -/- e8 - g8 -/- h2 - h3 -/- d6 - d5 -/x e4 - d5 p/x c6 - d5 p/x f3 - e5 p/- d7 - e6 -/- c1 - g5 -/- g7 - g6 -/- d1 - f3 -/- g8 - g7 -/- e5 - c6 -/- e6 - d7 -/x c6 - d8 q/- f8 - e8 -/x f3 - f6 n/- g7 - f8 -/x f6 - f7 p",
                "r2Nrk2/2pb1Q1p/pb4p1/3p2B1/8/2P4P/PPB2PP1/RN3RK1 w - -");
        Assertions.assertTrue(FEN.contains("rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR"));
    }

    @Test
    public void testGameThree() {
        //Mednik, Boris (1648) vs Kashin, Yury (1848)
        //Date:	2015-10-10
        //Event:	Chigorin Memorial 2015, St Petersburg RUS
        String FEN = gameTest("- e2 - e4 -/- e7 - e6 -/- d2 - d4 -/- d7 - d5 -/- b1 - c3 -/- f8 - b4 -/- e4 - e5 -/- c7 - c5 -/- a2 - a3 -/- b4 - a5 -/- b2 - b4 -/x c5 - d4 p/- c3 - b5 -/- a5 - c7 -/- g1 - f3 -/- b8 - c6 -/- c1 - f4 -/- g8 - e7 -/x b5 - c7 b/x d8 - c7 n/x f3 - d4 p/- a7 - a6 -/x d4 - c6 n/x c7 - c6 n/- f1 - d3 -/- e7 - g6 -/x d3 - g6 n/x h7 - g6 b/- e1 - g1 -/- c8 - d7 -/- f4 - e3 -/- a8 - c8 -/- a1 - c1 -/- c6 - c3 -/- d1 - d3 -/x c3 - e5 p/- e3 - d4 -/x e5 - h2 p",
                "2r1k2r/1p1b1pp1/p3p1p1/3p4/1P1B4/P2Q4/2P2PPq/2R2RK1 b kq -");
        Assertions.assertTrue(FEN.contains("rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR"));
    }

    @Test
    public void testGameFour() {
        //Thomas, Ben (1850) vs Emblem, Mark (1572)
        //Date:	2015-07-15
        //Event:	12th South Wales Int 2015, Cardiff WLS
        String FEN = gameTest("- e2 - e4 -/- c7 - c5 -/- c2 - c3 -/- g7 - g6 -/- d2 - d4 -/x c5 - d4 p/x c3 - d4 p/- f8 - g7 -/- b1 - c3 -/- b8 - c6 -/- g1 - f3 -/- e7 - e6 -/- c3 - b5 -/- d7 - d6 -/- f1 - e2 -/- g8 - e7 -/- c1 - f4 -/- e6 - e5 -/x d4 - e5 p/x d6 - e5 p/- b5 - d6 -/- e8 - f8 -/- f4 - e3 -/- c6 - d4 -/x f3 - d4 n/x d8 - d6 n/- d4 - e6 -/x d6 - e6 n/- d1 - d8 -",
                "r1bQ1k1r/pp2npbp/4q1p1/4p3/4P3/4B3/PP2BPPP/R3K2R w KQ -");
        Assertions.assertTrue(FEN.contains("rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR"));
    }

    @Test
    public void testGameFive() {
        //Andreev, Dobrotich (1881) vs Doychev, Ivan (1188)
        //Date:	2017-09-24
        //Event:	190th MGU Open 2017, Sofia BUL
        String FEN = gameTest("- e2 - e4 -/- c7 - c5 -/- g1 - f3 -/- e7 - e6 -/- d2 - d4 -/x c5 - d4 p/x f3 - d4 p/- g8 - f6 -/- b1 - c3 -/- f8 - b4 -/- e4 - e5 -/- f6 - d5 -/- c1 - d2 -/x b4 - c3 n/x b2 - c3 b/- d8 - c7 -/- f2 - f4 -/x d5 - c3 p/- d1 - f3 -/- c3 - d5 -/- d4 - b5 -/x c7 - c2 p/- a1 - c1 -/- c2 - a4 -/x c1 - c8 b/- e8 - e7 -/x c8 - h8 r/x a4 - a2 p/- b5 - d6 -/- d5 - c7 -/- d6 - c8 -",
                "rnN4R/ppnpkppp/4p3/4P3/5P2/5Q2/q2B2PP/4KB1R w KQ -");
        Assertions.assertTrue(FEN.contains("rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR"));
    }

    @Test
    public void testGameSix() {
        //Taylor, Richard (1888) vs Wei, Louie (1669)
        //Date:	2016-01-07
        //Event:	123rd ch-NZL Open 2016, Auckland NZL
        String FEN = gameTest("- e2 - e4 -/- g8 - f6 -/- e4 - e5 -/- f6 - d5 -/- d2 - d4 -/- d7 - d6 -/- g1 - f3 -/- b8 - c6 -/- c2 - c4 -/- d5 - b6 -/- c1 - f4 -/- c8 - g4 -/- f1 - e2 -/x d6 - e5 p/x f4 - e5 p/x c6 - e5 b/x f3 - e5 n/x g4 - e2 b/x d1 - e2 b/x d8 - d4 p/- b1 - c3 -/- g7 - g6 -/- a1 - d1 -/- d4 - f4 -/- g2 - g3 -/- f4 - f5 -/- c3 - b5 -/- f5 - c8 -/- c4 - c5 -/- b6 - a4 -/x b5 - c7 p/x c8 - c7 n/- e2 - b5 -/- c7 - c6 -/x e5 - c6 q/x b7 - c6 n/x b5 - c6 p",
                "r3kb1r/p3pp1p/2Q3p1/2P5/n7/6P1/PP3P1P/3RK2R w KQkq - -");
        Assertions.assertTrue(FEN.contains("rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR"));
    }

    @Test
    public void testGameSeven() {
        //Hernandez, Albert (2211) vs Moulton, Richard (1777)
        //Date:	2016-08-25
        //Event:	27th NATO Chess 2016, Shrivenham ENG
        String FEN = gameTest("- d2 - d4 -/- d7 - d5 -/- c2 - c4 -/- c7 - c6 -/- b1 - c3 -/- g8 - f6 -/- c1 - f4 -/x d5 - c4 p/- a2 - a4 -/- f6 - d5 -/- f4 - d2 -/x d5 - c3 n/x d2 - c3 n/- b7 - b5 -/- e2 - e3 -/- c8 - b7 -/- b2 - b3 -/x c4 - b3 p/x d1 - b3 p/- a7 - a6 -/- g1 - f3 -/- b8 - d7 -/- f1 - e2 -/- e7 - e6 -/- e1 - g1 -/- d7 - f6 -/- f3 - e5 -/- d8 - d5 -/- e2 - c4 -/x b5 - c4 b/x b3 - b7 b/- c6 - c5 -/x b7 - f7 p/- e8 - d8 -/- c3 - a5 -/- d8 - c8 -/- f7 - c7 -",
                "r1k2b1r/2Q3pp/p3pn2/B1pqN3/P1pP4/4P3/5PPP/R4RK1 w - -");
        Assertions.assertTrue(FEN.contains("rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR"));
    }

    @Test
    public void testGameEight() {
        //Hasegawa, Emi (1752) vs Fong, Mi Yen (1885)
        //Date:	2012-08-29
        //Event:	40th Olympiad Women, Istanbul TUR
        String FEN = gameTest("- d2 - d4 -/- g8 - f6 -/- c2 - c4 -/- g7 - g6 -/- g1 - f3 -/- f8 - g7 -/- b1 - c3 -/- d7 - d6 -/- e2 - e4 -/- e8 - g8 -/- f1 - d3 -/- e7 - e5 -/- d4 - d5 -/- b8 - d7 -/- b2 - b4 -/- f6 - h5 -/- e1 - g1 -/- d8 - e7 -/- c3 - e2 -/- c7 - c5 -/- b4 - b5 -/- f7 - f5 -/- a1 - b1 -/- f5 - f4 -/- g1 - h1 -/- g6 - g5 -/- e2 - g1 -/- g5 - g4 -/- f3 - d2 -/- e7 - h4 -/- f2 - f3 -/- h5 - g3 -",
                "r1b2rk1/pp1n2bp/3p4/1PpPp3/2P1Pppq/3B1Pn1/P2N2PP/1RBQ1RNK b - -");
        Assertions.assertTrue(FEN.contains("rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR"));
    }


}
