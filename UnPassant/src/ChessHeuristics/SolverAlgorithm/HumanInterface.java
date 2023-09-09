package ChessHeuristics.SolverAlgorithm;

import ChessHeuristics.Heuristics.Detector.DetectorInterface;
import ChessHeuristics.Heuristics.Detector.StateDetectorFactory;
import ChessHeuristics.Heuristics.HeuristicsUtil;
import ChessHeuristics.SolverAlgorithm.UnMoveConditions.UnMoveCondition;
import ReverseChess.StandardChess.BoardBuilder;
import ReverseChess.StandardChess.ChessBoard;
import ReverseChess.StandardChess.Coordinates;

import java.util.LinkedList;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class HumanInterface {
    static String fen = "4k2r/8/8/8/2p5/5P2/2P2PP1/3b1RK1 w k -";
    private static final List<Character> PIECES = List.of('-', 'P', 'K', 'R', 'N', 'B', 'Q');

    public static void main(String[] args) {
        boolean go = true;
        DetectorInterface detector = null;
        String FEN = null;
        while (go) {
            System.out.println("Please enter a FEN");
            System.out.println("The player whose turn it is should be the player who made he last single move");
            System.out.println("Castling parameters should indicate where kings must be able to castle");
            System.out.println("The en passant coordinate should be the coordinate of a pawn whose last move was " +
                    "to double move");
            FEN = System.console().readLine();
            try {
                ChessBoard board = BoardBuilder.buildBoard(FEN);
                board.setTurn(board.getTurn().equals("white") ? "black" : "white");
                detector = StateDetectorFactory.getDetectorInterface(board);
            } catch (IllegalArgumentException e) {
                System.out.println("Bad FEN, " + FEN);
                System.out.println();
                System.out.println();
                continue;
            }
            detector.testState();
            if (!detector.getState()) {
                System.out.println(detector.getErrorMessage());
                System.out.println();
                System.out.println();
            } else {
                go = false;
            }
        }
        System.out.println("Board Information:");
        System.out.println(detector);
        System.out.println();
        go = true;
        int depth = 0;
        while (go) {
            System.out.println("Please enter depth.");
            try {
                depth = Integer.parseInt(System.console().readLine());
            } catch (NumberFormatException e) {
                System.out.println("Not a number.");
                continue;
            }
            go = false;
        }
        go = true;
        int addDepth = 0;
        while (go) {
            System.out.println("Please enter additional depth.");
            try {
                addDepth = Integer.parseInt(System.console().readLine());
            } catch (NumberFormatException e) {
                System.out.println("Not a number.");
                continue;
            }
            go = false;
        }
        go = true;
        int solutions = 0;
        while (go) {
            System.out.println("Please enter number of solutions to be found.");
            try {
                solutions = Integer.parseInt(System.console().readLine());
            } catch (NumberFormatException e) {
                System.out.println("Not a number.");
                continue;
            }
            go = false;
        }
        go = true;
        List<Predicate<String>> conditions = new LinkedList<>();
        while (go) {
            System.out.println("Enter an un-move criterion? y/n");
            if (System.console().readLine().equals("n")) {
                go = false;
                break;
            }
            int from = 0;
            System.out.println("Format the condition as the following separated by commas:");
            System.out.println("1. The depth being checked from, starting at 0");
            System.out.println("2. The depth being checked to (non-inclusive)");
            System.out.println("3. The player in question (w/b/-)");
            System.out.println("4. The starting coordinate in question (- if irrelevant)");
            System.out.println("5. The end coordinate in question (- if irrelevant)");
            System.out.println("6. The type of piece being moved (P, K, N, etc. - if irrelevant)");
            System.out.println("7. The type of piece being captured (P, K, N, etc. - if irrelevant)");
            System.out.println("8. What kind of move is being checked (-/x/0-0/0-0-0/e.p/any)");
            System.out.println("9. Negative or positive? (true/false)");
            try {
                String condition = System.console().readLine();
                conditions.add(readUnMoveCondition(condition));
                continue;
            } catch (IllegalArgumentException e) {
                System.out.println("Condition not properly formatted.");
                continue;
            }
        }


        go = true;
        List<Predicate<DetectorInterface>> promotionLimits = new LinkedList<>();
        while (go) {
            System.out.println("Enter a board state criterion? y/n");
            if (System.console().readLine().equals("n")) {
                go = false;
                break;
            }
            System.out.println("Promotion or castling criterion? (p/c)");
            if (System.console().readLine().equals("p")) {
                System.out.println("Format the condition as the following separated by commas:");
                System.out.println("1. The player in question (w/b/-)");
                System.out.println("2. The type of piece in question (queen, pawn. - if irrelevant.)");
                System.out.println("3. The maximum number of promoted piece");

                System.out.println("If nobody may promote, type 'n'");
                String input = System.console().readLine();
                if (input.equals("n")) {
                    promotionLimits.add(new StateConditions.NoPromotions());
                    conditions.add(s -> !(s.split(":")[1].charAt(0) == 'P'
                            && (s.split(":")[1].endsWith("1") || s.split(":")[1].endsWith("8"))));
                    continue;
                }
                try {
                    promotionLimits.add(readPromotionConditions(input));
                    conditions.add(s -> !(s.split(":")[1].charAt(0) == 'P'
                            && (s.split(":")[1].endsWith("1")  || s.split(":")[1].endsWith("8"))));
                } catch (IllegalArgumentException e) {
                    System.out.println("Condition improperly formatted.");
                    continue;
                }
            } else {
                System.out.println("Who must castle? (w/b)");
                String input = System.console().readLine();
                boolean white = input.equals("w");
                promotionLimits.add(detectorInterface -> detectorInterface.canCastle(white, true)
                        || detectorInterface.canCastle(white, false));

            }
        }

        Predicate<String> unMoveCondition = s -> true;
        if (conditions.size() > 0) {
            for (Predicate<String> u : conditions) {
                unMoveCondition = unMoveCondition.and(u);
            }
        }
        Predicate<DetectorInterface> stateCondition = d -> true;
        if (promotionLimits.size() > 0) {
            for (Predicate<DetectorInterface> s : promotionLimits) {
                stateCondition = stateCondition.and(s);
            }
        }
        Solver solver;
        if (promotionLimits.isEmpty()) {
            solver = new Solver(unMoveCondition);
        } else {
            solver = new Solver(unMoveCondition, stateCondition);
        }
        solver.setNumberOfSolutions(solutions);
        solver.setAdditionalDepth(addDepth);
        System.out.println("Solving...");
        List<String> foundSolutions = solver.solve(FEN, depth);
        System.out.println();

        if (foundSolutions.isEmpty()) {
            System.out.println("No solutions found - position is not legal.");
        } else {
            System.out.println("Solutions found: ");
            System.out.println(foundSolutions.stream().map(s -> {
                String[] fen = s.split(":");
                return fen[0] + ":   " + fen[1];
            })
                    .collect(Collectors.joining("\n")));
        }

    }

    private static StateConditions.PromotionLimit readPromotionConditions(String condition) throws IllegalArgumentException {
        String[] input = condition.split(",");
        if (input.length != 3) {
            throw new IllegalArgumentException();
        }
        int number;
        try {
            number = Integer.parseInt(input[2]);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException();
        }
        char player = input[0].charAt(0);
        int playerTwo = player == '-' ? 0 : player == 'w' ? 1 : -1;

        String piece = input[1];
        List<String> pieces;
        if (piece.equals("-")) {
            pieces = HeuristicsUtil.PIECE_CODES.keySet().stream().toList();
        } else {
            pieces = List.of(piece);
        }

        return new StateConditions.PromotionLimit(playerTwo, number, pieces);

    }

    private static UnMoveCondition readUnMoveCondition(String condition) throws IllegalArgumentException {

        String[] input = condition.split(",");
        if (input.length != 9) {
            throw new IllegalArgumentException();
        }
        int from;
        try {
            from = Integer.parseInt(input[0]);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException();
        }
        int to;
        try {
            to = Integer.parseInt(input[1]);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException();
        }

        char player = input[2].charAt(0);

        String c1 = input[3];
        String c2 = input[4];

        if (!coordinateCheck(c1) || !coordinateCheck(c2)) {
            throw new IllegalArgumentException();
        }

        char piece1 = input[5].charAt(0);
        char piece2 = input[6].charAt(0);

        String how = input[7];
        if (!how.equals("-") &&  !how.equals("x") && !how.equals("e.p.") && !how.equals("0-0")
        && !how.equals("0-0-0") && !how.equals("any")) {
            throw new IllegalArgumentException();
        }

        boolean negative = input[8].equals("false");

        return new UnMoveCondition(from, to, player, c1, c2, piece1, piece2, how, negative);
    }

    private static boolean coordinateCheck(String coord) {
        if (coord.equals("-")) {
            return true;
        }
        if (coord.length() != 2) {
            return false;
        }
        if (coord.charAt(0) < Coordinates.LOWER_ASCII_A || coord.charAt(0) > Coordinates.LOWER_ASCII_A + 8) {
            return false;
        }
        try {
            Integer.parseInt(coord.substring(1));
        } catch (NumberFormatException e) {
            return false;
        }
        return true;
    }
}
