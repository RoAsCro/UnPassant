import SolveAlgorithm.Solver;
import StandardChess.Coordinate;
import StandardChess.Coordinates;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

public class SolverGameTest {
    public class GamePlayerTwo extends TestGamePlayer {

        public void playWithRetroCheck(String game) {
            String[] moves = game.split(" ");
            Arrays.stream(moves).forEach(s -> {
                Coordinate origin = new Coordinate(s.charAt(0) - 97, s.charAt(1) - 49);
                Coordinate target = new Coordinate(s.charAt(2) - 97, s.charAt(3) - 49);
                System.out.println("origin = " + origin);
                System.out.println("target = " + target);
                Assertions.assertTrue(moveMaker.makeMove(origin, target));
                Solver solver = new Solver();
                solver.setNumberOfSolutions(1);
                solver.setAdditionalDepth(0);
                Coordinate enPassant = this.board.getEnPassant();
                this.board.setEnPassant(Coordinates.NULL_COORDINATE);
//                this.board.setTurn(this.board.getTurn().equals("white") ? "black" : "white");
                Assertions.assertNotEquals(0, solver.solve(this.board, 1).size(), this.board.getReader().toFEN());
//                this.board.setTurn(this.board.getTurn().equals("white") ? "black" : "white");
                this.board.setEnPassant(enPassant);

            });

            }
    }

    GamePlayerTwo tester = new GamePlayerTwo();
    @Test
    public void testOne() {
        //Sebastien Midoux vs Christopher Baumgartner
        //Reykjavik Open (2016), Reykjavik ISL, rd 5, Mar-11
        tester.playWithRetroCheck("e2e4 e7e5 d2d4 g8f6 d4e5 f6e4 f1c4 d8h4 d1f3 d7d5 c4d5 f7f5 e5f6 e4f6 f3e2 f8e7 g1f3 h4b4 c2c3 b4b6 d5b3 b8c6 b1a3 c8g4 c1e3 b6a5 e1c1 e7a3 e3c5 c6e7 e2e7");
    }

    @Test
    public void testTwo() {
        //Alec Elias Sigurdarson vs Sindri Snaer Kristofersson
        //Reykjavik Open (2014), Reykjavik ISL, rd 6, Mar-08
        tester.playWithRetroCheck("e2e4 e7e5 g1f3 b8c6 f1b5 a7a6 b5a4 f8c5 e1g1 d7d6 c2c3 g8f6 d2d4 c5b6 d4d5 c8d7 d5c6 b7c6 a4c2 e8g8 h2h3 d6d5 e4d5 c6d5 f3e5 d7e6 c1g5 g7g6 d1f3 g8g7 e5c6 e6d7 c6d8 f8e8 f3f6 g7f8 f6f7");
    }

    @Test
    public void testThree() {
        //Mednik, Boris (1648) vs Kashin, Yury (1848)
        //Date:	2015-10-10
        //Event:	Chigorin Memorial 2015, St Petersburg RUS
        tester.playWithRetroCheck("e2e4 e7e6 d2d4 d7d5 b1c3 f8b4 e4e5 c7c5 a2a3 b4a5 b2b4 c5d4 c3b5 a5c7 g1f3 b8c6 c1f4 g8e7 b5c7 d8c7 f3d4 a7a6 d4c6 c7c6 f1d3 e7g6 d3g6 h7g6 e1g1 c8d7 f4e3 a8c8 a1c1 c6c3 d1d3 c3e5 e3d4 e5h2");
    }

    @Test
    public void testFour() {
        //Thomas, Ben (1850) vs Emblem, Mark (1572)
        //Date:	2015-07-15
        //Event:	12th South Wales Int 2015, Cardiff WLS
        tester.playWithRetroCheck("e2e4 c7c5 c2c3 g7g6 d2d4 c5d4 c3d4 f8g7 b1c3 b8c6 g1f3 e7e6 c3b5 d7d6 f1e2 g8e7 c1f4 e6e5 d4e5 d6e5 b5d6 e8f8 f4e3 c6d4 f3d4 d8d6 d4e6 d6e6 d1d8");
    }

    @Test
    public void testFive() {
        //Andreev, Dobrotich (1881) vs Doychev, Ivan (1188)
        //Date:	2017-09-24
        //Event:	190th MGU Open 2017, Sofia BUL
        tester.playWithRetroCheck("e2e4 c7c5 g1f3 e7e6 d2d4 c5d4 f3d4 g8f6 b1c3 f8b4 e4e5 f6d5 c1d2 b4c3 b2c3 d8c7 f2f4 d5c3 d1f3 c3d5 d4b5 c7c2 a1c1 c2a4 c1c8 e8e7 c8h8 a4a2 b5d6 d5c7 d6c8");
    }

    @Test
    public void testSix() {
        //Taylor, Richard (1888) vs Wei, Louie (1669)
        //Date:	2016-01-07
        //Event:	123rd ch-NZL Open 2016, Auckland NZL
        tester.playWithRetroCheck("e2e4 g8f6 e4e5 f6d5 d2d4 d7d6 g1f3 b8c6 c2c4 d5b6 c1f4 c8g4 f1e2 d6e5 f4e5 c6e5 f3e5 g4e2 d1e2 d8d4 b1c3 g7g6 a1d1 d4f4 g2g3 f4f5 c3b5 f5c8 c4c5 b6a4 b5c7 c8c7 e2b5 c7c6 e5c6 b7c6 b5c6");
    }

    @Test
    public void testSeven() {
        //Hernandez, Albert (2211) vs Moulton, Richard (1777)
        //Date:	2016-08-25
        //Event:	27th NATO Chess 2016, Shrivenham ENG
        tester.playWithRetroCheck("d2d4 d7d5 c2c4 c7c6 b1c3 g8f6 c1f4 d5c4 a2a4 f6d5 f4d2 d5c3 d2c3 b7b5 e2e3 c8b7 b2b3 c4b3 d1b3 a7a6 g1f3 b8d7 f1e2 e7e6 e1g1 d7f6 f3e5 d8d5 e2c4 b5c4 b3b7 c6c5 b7f7 e8d8 c3a5 d8c8 f7c7");
    }

    @Test
    public void testEight() {
        //Hasegawa, Emi (1752) vs Fong, Mi Yen (1885)
        //Date:	2012-08-29
        //Event:	40th Olympiad Women, Istanbul TUR
        tester.playWithRetroCheck("d2d4 g8f6 c2c4 g7g6 g1f3 f8g7 b1c3 d7d6 e2e4 e8g8 f1d3 e7e5 d4d5 b8d7 b2b4 f6h5 e1g1 d8e7 c3e2 c7c5 b4b5 f7f5 a1b1 f5f4 g1h1 g6g5 e2g1 g5g4 f3d2 e7h4 f2f3 h5g3");
    }
}
