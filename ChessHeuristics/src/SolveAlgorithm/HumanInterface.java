package SolveAlgorithm;
import Games.Game;
import Heuristics.Detector.DetectorInterface;
import Heuristics.Detector.StateDetectorFactory;
import StandardChess.BoardBuilder;
import StandardChess.Coordinate;
import StandardChess.Coordinates;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

public class HumanInterface {
    static String fen = "4k2r/8/8/8/2p5/5P2/2P2PP1/3b1RK1 w k -";
    private static final List<Character> PIECES = List.of('-', 'P', 'K', 'R', 'N', 'B', 'Q');
    private static List<UnMoveCondition> conditions = new LinkedList<>();
//    {
//            new UnMoveCondition(true, 0, 2, '-', "-",
//                    "-", '-', '-', "x", true)};

    public static void main(String[] args) {
            HumanInterface humanInterface;
            humanInterface = new HumanInterface();
            DetectorInterface detector;
            try {
               detector = StateDetectorFactory.getDetectorInterface(args[0]);
            } catch (IllegalArgumentException e) {
                System.out.println("Bad FEN, " + args[0]);
                return;
            }

            for (int i = 1 ; i < args.length ; i++) {
                try {
                    if (!readUnMoveCondition(args[i])) {
                        break;
                    }
                } catch (IllegalArgumentException e) {
                    System.out.println("UnMove condition " + i + " formatted incorrectly. " + e.getMessage());
                    return;
                } catch (IndexOutOfBoundsException e) {
                    System.out.println("Unmove conditions must end in 'end'.");
                }
            }

            detector.testState();
            System.out.println(detector);
            Solver solver = new Solver(s -> (conditions).stream().allMatch(c -> c.test(s)));
            solver.setNumberOfSolutions(2);
            solver.setAdditionalDepth(0);
            System.out.println(solver.solve(BoardBuilder.buildBoard(args[0]), 2));

    }

    private static boolean readUnMoveCondition(String condition) throws IllegalArgumentException {
        // "[any/from], [x], [y], [-/w/b], [-/a2/h8/...], [-/a2/h8/...], [-/P/K/...], [-/P/K/...], [-/x],
        // [true/false]
        if (condition.equals("end")) {
            return false;
        }
        String[] input = condition.split(", ");
        if (input.length != 10) {
            throw new IllegalArgumentException("Not enough arguments.");
        }
        if (!(input[0].equalsIgnoreCase("any") || input[0].equalsIgnoreCase("from"))) {
            throw new IllegalArgumentException("Time not formatted correctly.");
        }
        boolean when1 = input[0].equalsIgnoreCase("from");
        int when2;
        int when3;
        try {
            when2 = Integer.parseInt(input[1]);
            when3 = Integer.parseInt(input[2]);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Time not formatted correctly.");
        }
        if (!(input[3].equalsIgnoreCase("-") || input[3].equalsIgnoreCase("b")
                || input[3].equalsIgnoreCase("w"))) {
            throw new IllegalArgumentException("Invalid player.");
        }
        char who = input[3].charAt(0);
        if (!(coordinateCheck(input[4]) && coordinateCheck(input[5]))) {
            throw new IllegalArgumentException("Coordinates not formatted correctly.");
        }
        String from = input[4];
        String to = input[5];
        if (!PIECES.contains(input[6].charAt(0)) || !PIECES.contains(input[7].charAt(0))){
            throw new IllegalArgumentException("Not a valid piece.");
        }
        char what1 = input[6].charAt(0);
        char what2 = input[7].charAt(0);
        if (!(input[8].equals("-") || input[8].equals("x"))) {
            throw new IllegalArgumentException("Unreadable capture parameters.");
        }
        String how = input[8];
        if (!(input[9].equals("false") || input[9].equals("true"))) {
            throw new IllegalArgumentException("Unreadable 'not' condition.");
        }
        boolean not = input[9].equals("true");
        conditions.add(new UnMoveCondition(when1, when2, when3, who, from, to, what1, what2, how, not));
        return true;
    }

    private static boolean coordinateCheck(String coord) {
        if (coord.equals("-")) {
            return true;
        }
        if (coord.length() != 2) {
            return false;
        }
        int charInt =  - Coordinates.LOWER_ASCII_A;
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


    private void makeBoard(String fen){

    }
}
