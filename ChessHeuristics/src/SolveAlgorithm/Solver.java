package SolveAlgorithm;

import Heuristics.BoardInterface;
import Heuristics.Detector.StateDetectorFactory;
import Heuristics.Detector.DetectorInterface;
import Heuristics.Path;
import StandardChess.*;

import java.util.*;
import java.util.function.Predicate;

public class Solver {
    private int numberOfTests = 0;
    private long timeSpentOnTests = 0;
    Predicate<String> fenPredicate = p -> true;
    private Predicate<DetectorInterface> detectorPredicate = d -> true;
    private boolean allowNonIntrusiveMovement = true;
    String originalBoard;
    private boolean legalFirstAlwaysTrue = false;
    private boolean legalFirst = false;
    private int additionalDepth = 2;
    private int maxDepth;
    private int numberOfSolutions = 10;
    private StateLog stateLog = new StateLog();

//    private LinkedList<String> finalStates = new LinkedList<>();

    private final static List<String> PIECES = List.of("", "p", "r", "b", "n", "q");

    private int count = 0;

    public Solver(){};

    public Solver(Predicate<String> fenPredicate, Predicate<DetectorInterface> detectorPredicate){
        this.fenPredicate = fenPredicate;
        this.detectorPredicate = detectorPredicate;
        this.allowNonIntrusiveMovement = false;
    }

    public Solver(Predicate<String> fenPredicate){
        this.fenPredicate = fenPredicate;
    }

    public List<String> solve(ChessBoard board, int depth) throws IllegalArgumentException {
        this.maxDepth = depth;
        if (this.legalFirst) {
            this.legalFirstAlwaysTrue = true;
        }
        this.originalBoard = board.getReader().toFEN();
        List<String> solutions = new LinkedList<>();
        try {
            CheckUtil.switchTurns(board);
            if (testState(board)) {
                solutions = iterate(this.originalBoard, depth, false, 0);
            }

        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        System.out.println("TESTS");
        System.out.println(numberOfTests);
        System.out.println(timeSpentOnTests);
        System.out.println(solutions);

        return solutions;

    }

    private List<String> iterate(String startingFen, int depth, boolean any, int recursionDepth) throws InterruptedException {
        LinkedList<String> states = new LinkedList<>();
        LinkedList<String> finalStates = new LinkedList<>();
//        ArrayList<Integer> stateSizes = new ArrayList<>();
//        stateSizes.add(1);
        startingFen = startingFen + "::";
        if (any) {
            startingFen = startingFen + recursionDepth;
        } else {
            startingFen = startingFen + "0";
        }
        states.add(startingFen);

        while (!states.isEmpty()) {
            if (finalStates.size() >= this.numberOfSolutions) {
                break;
            }
//            int toAdd = stateSizes.get(currentDepth);
//            stateSizes.remove(currentDepth);
//            stateSizes.add(currentDepth, toAdd - 1);
            String state;
            /* If in an Any state, there's a good chance the algorithm is trying to get out of check */
            if (any) {
                state =
                        states.stream().filter(s -> s.charAt(s.length() - 1)
                                        == states.getFirst().charAt(states.getFirst().length() - 1))
                                .filter(s -> !CheckUtil.eitherInCheck(new BoardInterface(BoardBuilder.buildBoard(s.split(":")[0]))))
                                .findAny()
                                .orElse(states.getFirst());
                states.remove(state);
            } else {
                state = states.pop();
            }

            String[] stateDescription = state.split(":");
            int currentDepth = Integer.parseInt(stateDescription[2]);
            String currentState = stateDescription[0];
            ChessBoard currentBoard = BoardBuilder.buildBoard(currentState);
            CheckUtil.switchTurns(currentBoard);
//            if (currentDepth == depth || !this.allowNonIntrusiveMovement || !nonIntrusiveMovement(state)) {
            System.out.println(state);
            if (!testState(currentBoard)) {
                System.out.println("afila");
                continue;
            }
            if (state.equals("k1K5/3pQ3/8/2B1P3/3P4/7P/8/7B w - d5:Pd7-d5, Pe5xPd6, :2")) {
                System.out.println(currentBoard.getEnPassant());
            }
            System.out.println("pass");

//            System.out.println(states);

            CheckUtil.switchTurns(currentBoard);
            if (currentDepth != depth + recursionDepth) {
                List<Coordinate> pieces = allPieces(currentBoard);
                List<String> newStates = new LinkedList<>();
                for (Coordinate piece : pieces) {
                    if (!currentBoard.getEnPassant().equals(Coordinates.NULL_COORDINATE)) {
                        if (!piece.equals(currentBoard.getEnPassant())) {
                            continue;
                        }
                    }
                    newStates.addAll(iterateThroughMoves(currentBoard, piece, state, any && currentDepth == depth - 1));

                }
//                toAdd = 0;
//                if (stateSizes.size() >= currentDepth + 2) {
////                    toAdd = stateSizes.get(currentDepth + 1);
////                    stateSizes.remove(currentDepth + 1);
//                }
////                stateSizes.add(currentDepth + 1, toAdd + newStates.size());
                newStates.forEach(s ->
                        states.push(s.split(":")[0]
                                + ":"
                                + s.split(":")[1]
                                + (stateDescription.length > 1 ? (", "
                                + stateDescription[1]) : "")
                        + ":" + (currentDepth + 1)));
//                if (stateSizes.get(currentDepth + 1) > 0) {
//                    currentDepth++;
//                }

            } else {
                if (any) {
                    CheckUtil.switchTurns(currentBoard);
                    if (!legalFirst || testState(currentBoard)) {
                        boolean pass = true;
                        if (CheckUtil.eitherInCheck(new BoardInterface(currentBoard))) {
                            System.out.println("going down");

                            System.out.println("going down");

                            pass = !iterate(currentState.split(":")[0], 1, true, recursionDepth + depth).isEmpty();
                            System.out.println("coming out");

                        }
                        if (pass) {
                            finalStates.add(currentState + ":" + stateDescription[1]);
                            return finalStates;
                        }
                        //System.out.println("coming out");
                    }
                } else {
//                    this.legalFirst = true;
                    System.out.println("going down");
                    if (this.additionalDepth == 0 || !iterate(currentState.split(":")[0], this.additionalDepth, true, recursionDepth + depth).isEmpty()) {
                        System.out.println("coming out");

                        boolean pass = true;
                        if (this.additionalDepth == 0) {
                            CheckUtil.switchTurns(currentBoard);
                            if (CheckUtil.eitherInCheck(new BoardInterface(currentBoard))) {
                                System.out.println("going down");

                                pass = !iterate(currentState.split(":")[0], 1, true, recursionDepth + depth).isEmpty();
                            }
                        }
                        if (pass) {
                            finalStates.add(currentState + ":" + stateDescription[1]);
                        }
                    }
//                    if (!legalFirstAlwaysTrue) {
//                        this.legalFirst = false;
//                    }
                }
            }
//            while (stateSizes.size() >= currentDepth + 1 && stateSizes.get(currentDepth) < 1) {
//                stateSizes.remove(currentDepth);
//                if (currentDepth != 0) {
//                    currentDepth--;
//                }
//            }
        }
        return finalStates;
    }

    private String toLAN(ChessBoard board, Coordinate origin, Coordinate target, String piece, boolean castle) {
        return board.at(target).getType().toUpperCase().charAt(board.at(target).getType().equals("knight") ? 1 : 0)+
                Coordinates.readableString(target)
                + (piece.equals("") ? "-" : "x")
                + (castle ? (Math.abs(origin.getX() - target.getX()) == 2 ? "O-O" : "O-O-O") : "")
                + piece.toUpperCase()
                + Coordinates.readableString(origin);
    }

    private List<Coordinate> allPieces(ChessBoard board) {
        return new BoardInterface(board).getBoardFacts().getAllCoordinates(board.getTurn())
                .values().stream().flatMap(Path::stream).toList();
    }

    public List<String> iterateThroughMoves(ChessBoard board, Coordinate origin, String currentState, boolean any) {
        // If piece is on final rank, allow it to be a pawn.
        List<String> states = new LinkedList<>();

        Coordinate[] moves = board.at(origin).getUnMoves(origin);
        String type = board.at(origin).getType();
        boolean white = board.getTurn().equals("white");
        int y = origin.getY();
        int x = origin.getX();
        boolean king = type.equals("king");
        boolean rook = type.equals("rook");
        if (king || rook) {
            String colour = white ? "white" : "black";
            if ((rook && board.canCastle(origin.getX() == Coordinates.WHITE_KING_ROOK.getX() ? "king" : "queen", colour))
                    || (king && board.canCastle("queen", colour) || board.canCastle("king", colour))) {
                return states;
            }
        }
        if (((white && y == 7) || (!white && y == 0)) && !king) {
            Coordinate[] additionalMoves = StandardPieceFactory.getInstance().getPiece(white ? "p" : "P").getMoves(origin);
            states.addAll(iterateThroughMovesHelper(board, additionalMoves, origin, currentState, true, false, any, false));
            if (!legalFirst && any && !states.isEmpty()) {
                return states;
            }

        } else if (((white && y == 5) || (!white && y == 2)) && type.equals("pawn")) {
            int offfset = white ? -1 : 1;
            Coordinate[] additionalMoves = new Coordinate[]{new Coordinate(x + 1, y + offfset), new Coordinate(x - 1, y + offfset)};
            states.addAll(iterateThroughMovesHelper(board, additionalMoves, origin, currentState, false, true, any, false));
            if (!legalFirst && any && !states.isEmpty()) {
                return states;
            }
        } else if (king) {
            Coordinate[] additionalMoves = new Coordinate[]{new Coordinate(x - 2, y), new Coordinate(x + 2, y)};
            states.addAll(iterateThroughMovesHelper(board, additionalMoves, origin, currentState, false, false, any, true));
            if (!legalFirst && any && !states.isEmpty()) {
                return states;
            }

        } else if (type.equals("pawn")) {
            Coordinate[] additionalMoves = new Coordinate[]{new Coordinate(x, y +  (white ? -2 : 2))};
            states.addAll(iterateThroughMovesHelper(board, additionalMoves, origin, currentState, false, false, any, false));
            if (!legalFirst && any && !states.isEmpty()) {
                return states;
            }

        }
        states.addAll(iterateThroughMovesHelper(board, moves, origin, currentState, false, false, any, false));
        return states;
    }

    private List<String> iterateThroughMovesHelper(ChessBoard board, Coordinate[] moves,
                                                   Coordinate origin, String currentState, boolean promotion,
                                                   boolean enPassant,
                                                   boolean any,
                                                   boolean castle) {
        boolean previousEnPassant = !board.getEnPassant().equals(Coordinates.NULL_COORDINATE);
        List<String> states = new LinkedList<>();
        for (Coordinate currentMove : moves) {
            if (previousEnPassant) {
                if (currentMove.getX() != origin.getX()) {
                    continue;
                }
            }
            // Revert the coordinate to directions
            Coordinate direction = Coordinates.add(currentMove, new Coordinate(-origin.getX(), -origin.getY()));
            List<String> pieces = PIECES;
            if (enPassant) {
                pieces = List.of("p");
            }
            for (int i = 1 ; ; i++) {
                boolean continueFlag = true;
                if (previousEnPassant) {
                    i = 2;
                    continueFlag = false;
                }
                // For each UnTakeable piece
                for (String piece : pieces) {
                    ChessBoard currentBoard = BoardBuilder.buildBoard(board);
                    Coordinate target = Coordinates.add(origin, new Coordinate(direction.getX() * i, direction.getY() * i));
                    if (makeJustMove(currentBoard, origin, target, piece, promotion, enPassant)){
                        CheckUtil.switchTurns(currentBoard);
                        String move = currentBoard.getReader().toFEN() + ":" + toLAN(currentBoard, origin, target, piece, castle);
                        CheckUtil.switchTurns(currentBoard);
                        if (CheckUtil.check(new BoardInterface(currentBoard))
                        && this.fenPredicate.test(move +
                                (currentState.split(":").length > 1 ? (":" + currentState.split(":")[2])
                                        : ":0"))) {
                            //                            String movedPiece = currentBoard.at(target).getType();

//                            if (legalFirst) {
                            //                            } else if (this.allowNonIntrusiveMovement && nonIntrusiveMovement(promotion, piece, movedPiece)) {
//                                pass = true;
////                                this.stateLog.register(new BoardInterface(currentBoard), true);
//
//                            } else if (testState(currentBoard)) {
//                                pass = true;
//                            }
                            CheckUtil.switchTurns(currentBoard);
                            if (previousEnPassant) {
                                currentBoard.setEnPassant(Coordinates.NULL_COORDINATE);
                            }
                            String boardAndMove = currentBoard.getReader().toFEN() + ":" + toLAN(currentBoard, origin, target, piece, castle);
                            states.add(boardAndMove);
                            if (!legalFirst && any) {

                                break;
                            }
                        }
                    } else {
                        // May break from creating a pawn
                        if (!piece.equals("pawn")) {
                            continueFlag = false;
                        }
                    }
                }
                if (!continueFlag) {
                    break;
                }
            }
        }
        return states;
    }

    private static boolean nonIntrusiveMovement(boolean promotion, String piece, String movedPiece) {
        return !(movedPiece.charAt(0) == 'p')
                && piece.equals("") && !promotion;
    }
    private static boolean nonIntrusiveMovement(String fen) {
//        System.out.println(fen);
        String move = fen.split(":")[1];
        if (move.equals("")) {
            return true;
        }
        return !(move.charAt(0) == 'p')
                && !(move.charAt(3) == 'x');
    }

    public boolean makeMove(ChessBoard board, Coordinate origin, Coordinate target, String piece, boolean promotion, boolean enPassant) {
        boolean justMove = makeJustMove(board, origin, target, piece, promotion, enPassant);
        return justMove && testState(board);
    }

    public boolean testState(ChessBoard board) {
        DetectorInterface detector;
        detector = StateDetectorFactory.getDetectorInterface(board);
        numberOfTests++;
        return detector.testState() && castleCheck(board, detector) && this.detectorPredicate.test(detector);
    }

    private boolean makeJustMove(ChessBoard board, Coordinate origin, Coordinate target,
                                 String piece, boolean promotion, boolean enPassant) {
        UnMoveMaker moveMaker = new UnMoveMaker(board);
        if (promotion) {
            moveMaker.setPromotionFlag(true);
        }
        if (enPassant) {
            moveMaker.setEnPassantFlag(enPassant);
        }
        if (!piece.equals("")) {
            moveMaker.setCaptureFlag(true);
            moveMaker.setCapturePiece(StandardPieceFactory.getInstance()
                    .getPiece(board.getTurn().equals("white") ? piece.toLowerCase() : piece.toUpperCase()));
        }
        return moveMaker.makeUnMove(origin, target);
    }

    private boolean castleCheck(ChessBoard board, DetectorInterface detector) {
        boolean white = true;
        for (int i = 0 ; i < 2 ; i++) {
            String piece = "king";
            for (int j = 0 ; j < 2 ; j++) {
                if (board.canCastle(piece, white ? "white" : "black")) {
                    if (!detector.canCastle(white)) {
                        return false;
                    }
                }
                piece = "queen";
            }
            white = false;
        }
        return true;
    }

    public int getAdditionalDepth() {
        return additionalDepth;
    }

    public void setAdditionalDepth(int additionalDepth) {
        this.additionalDepth = additionalDepth;
    }

    public void setNumberOfSolutions(int numberOfSolutions) {
        this.numberOfSolutions = numberOfSolutions;
    }
}
