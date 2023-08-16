package SolveAlgorithm;

import Heuristics.BoardInterface;
import Heuristics.Detector.DetectorInterface;
import Heuristics.Detector.StateDetectorFactory;
import Heuristics.Path;
import StandardChess.*;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Predicate;

public class SolverV2 {

    Predicate<String> fenPredicate = p -> true;
    private Predicate<DetectorInterface> detectorPredicate = d -> true;
    private boolean allowNonIntrusiveMovement = true;
    String originalBoard;
    private boolean legalFirstAlwaysTrue = false;
    private boolean legalFirst = false;
    private int additionalDepth = 2;
    private int maxDepth;
    private int numberOfSolutions = 100;
    private StateLog stateLog = new StateLog();

//    private LinkedList<String> finalStates = new LinkedList<>();

    private final static List<String> PIECES = List.of("", "p", "r", "b", "n", "q");

    private int count = 0;

    public SolverV2(){};

    public SolverV2(Predicate<String> fenPredicate, Predicate<DetectorInterface> detectorPredicate){
        this.fenPredicate = fenPredicate;
        this.detectorPredicate = detectorPredicate;
        this.allowNonIntrusiveMovement = false;
    }

    public SolverV2(Predicate<String> fenPredicate){
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
                solutions = iterate(this.originalBoard, depth, false);
            }

        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        return solutions;

    }

    private List<String> iterate(String startingFen, int depth, boolean any) throws InterruptedException {
        ArrayList<LinkedList<String>> stateArray = new ArrayList<>();
        for (int i = 0 ; i <= depth ; i++) {
            stateArray.add(i, new LinkedList<>());
        }
        startingFen = startingFen + "::";
        if (any) {
            startingFen = startingFen + this.maxDepth;
        } else {
            startingFen = startingFen + "0";
        }
        stateArray.get(0).add(startingFen);
        LinkedList<String> finalStates = new LinkedList<>();
        List<SolverV2.SolverRunner> runnerPool = new LinkedList<>();
        while (true) {
            List<SolverRunner> toRemove = runnerPool.stream().filter(r -> r.finished).toList();
            toRemove.forEach(r -> {
                runnerPool.remove(r);
                r.states.forEach(s -> {
                    stateArray.get(r.runnerDepth + 1).add(s.split(":")[0]
                        + ":"
                        + s.split(":")[1]
                        + (r.previousMoves)
                        + ":" + (r.runnerDepth + 1));});
            });

            if (finalStates.size() >= this.numberOfSolutions ||
                    (stateArray.stream().allMatch(List::isEmpty) && runnerPool.isEmpty())) {
                break;
            }
            if (runnerPool.size() > 10) {
                continue;
            }
            String state = "";
            int currentDepth = 0;
            for (int i = depth ; i >= 0 ; i--) {
                LinkedList<String> currentList = stateArray.get(i);
                if (!currentList.isEmpty()) {
                    state = stateArray.get(i).pop();
                    currentDepth = i;
                }
            }
            if (state.equals("")) {
                continue;
            }
            String[] stateDescription = state.split(":");
            String currentState = state;
            ChessBoard currentBoard = BoardBuilder.buildBoard(stateDescription[0]);
            if (currentDepth != depth) {
                List<Coordinate> pieces = allPieces(currentBoard);
                for (Coordinate piece : pieces) {
                    if (!currentBoard.getEnPassant().equals(Coordinates.NULL_COORDINATE)) {
                        if (!piece.equals(currentBoard.getEnPassant())) {
                            continue;
                        }
                    }
                    SolverRunner runner = new SolverRunner(currentBoard, piece, state,
                            any && currentDepth == depth - 1, currentDepth);
                    runnerPool.add(runner);
                    Thread thread = new Thread(runner);
                    thread.start();
                }
            } else {
                if (any) {
                    CheckUtil.switchTurns(currentBoard);
                    if (!legalFirst || testState(currentBoard)) {
                        boolean pass = true;
                        if (CheckUtil.eitherInCheck(new BoardInterface(currentBoard))) {
                            pass = !iterate(currentState, 1, true).isEmpty();
                        }
                        if (pass) {
                            finalStates.add(currentState + ":" + stateDescription[1]);
                            return finalStates;
                        }
                    }
                } else {
                    this.legalFirst = true;
                    if (this.additionalDepth == 0 || !iterate(currentState, this.additionalDepth, true).isEmpty()) {
                        boolean pass = true;
                        if (this.additionalDepth == 0) {
                            CheckUtil.switchTurns(currentBoard);
                            if (CheckUtil.eitherInCheck(new BoardInterface(currentBoard))) {
                                pass = !iterate(currentState, 1, true).isEmpty();
                            }
                        }
                        if (pass) {
                            finalStates.add(currentState + ":" + stateDescription[1]);
                        }
                    }
                    if (!legalFirstAlwaysTrue) {
                        this.legalFirst = false;
                    }
                }
            }
        }
        return finalStates;
    }

    private void iteratePartTwo(String state, boolean any, int currentDepth, int depth) {
        String[] stateDescription = state.split(":");
        String currentState = stateDescription[0];
        ChessBoard currentBoard = BoardBuilder.buildBoard(currentState);
        List<Coordinate> pieces = allPieces(currentBoard);
        List<String> newStates = new LinkedList<>();
//                List<Solver.SolverRunner> runnerPool = new LinkedList<>();
        BoardInterface boardInterface = new BoardInterface(currentBoard);
        for (Coordinate piece : pieces) {
            if (!currentBoard.getEnPassant().equals(Coordinates.NULL_COORDINATE)) {
                if (!piece.equals(currentBoard.getEnPassant())) {
                    continue;
                }
            }
            newStates.addAll(iterateThroughMoves(currentBoard, piece, state, any && currentDepth == depth - 1));
        }
        LinkedList<String> states  = new LinkedList<>();
        int finalCurrentDepth = currentDepth;
        newStates.forEach(s ->
                states.push(s.split(":")[0]
                        + ":"
                        + s.split(":")[1]
                        + (stateDescription.length > 1 ? (", "
                        + stateDescription[1]) : "")
                        + ":" + (finalCurrentDepth + 1)));
//            } else {
//                if (any) {
//                    CheckUtil.switchTurns(currentBoard);
//                    if (!legalFirst || testState(currentBoard)) {
//                        //System.out.println("Down again..." + state);
//
//                        boolean pass = true;
//                        if (CheckUtil.eitherInCheck(new BoardInterface(currentBoard))) {
//                            //System.out.println("check " + state);
//                            pass = !iterate(currentState, 1, true).isEmpty();
//                        }
//                        if (pass) {
//                            //System.out.println("finish " + state);
//
//                            finalStates.add(currentState + ":" + stateDescription[1]);
//                            return finalStates;
//                        }
//                        //System.out.println("coming out");
//                    }else {
//                    }
//                } else {
//                    this.legalFirst = true;
//                    //System.out.println("goinf down " + state);
//                    if (this.additionalDepth == 0 || !iterate(currentState, this.additionalDepth, true).isEmpty()) {
//                        boolean pass = true;
//
//                        if (this.additionalDepth == 0) {
//                            CheckUtil.switchTurns(currentBoard);
//                            if (CheckUtil.eitherInCheck(new BoardInterface(currentBoard))) {
//                                //System.out.println("check " + state);
//                                pass = !iterate(currentState, 1, true).isEmpty();
//                            }
//                        }
//                        if (pass) {
//                            finalStates.add(currentState + ":" + stateDescription[1]);
//                        }
//                    }
//                    if (!legalFirstAlwaysTrue) {
//                        this.legalFirst = false;
//                    }
//                }
//            }
//            while (stateSizes.size() >= currentDepth + 1 && stateSizes.get(currentDepth) < 1) {
//                stateSizes.remove(currentDepth);
//                if (currentDepth != 0) {
//                    currentDepth--;
//                }
//            }
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
//                //System.out.println("--------");
//                //System.out.println(moves.length);
//                //System.out.println(origin);
//
//                //System.out.println(direction);
//                //System.out.println(i);
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
                            boolean pass = false;
                            String movedPiece = currentBoard.at(target).getType();

                            if (legalFirst) {
                                pass = true;
                            } else if (this.allowNonIntrusiveMovement && nonIntrusiveMovement(promotion, piece, movedPiece)) {
                                pass = true;
                                this.stateLog.register(new BoardInterface(currentBoard), true);

                            } else if (testState(currentBoard)) {
                                pass = true;
                            }
                            if (pass) {
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

    public boolean makeMove(ChessBoard board, Coordinate origin, Coordinate target, String piece, boolean promotion, boolean enPassant) {
        boolean justMove = makeJustMove(board, origin, target, piece, promotion, enPassant);
        return justMove && testState(board);
    }

    public boolean testState(ChessBoard board) {
        DetectorInterface detector;
        detector = StateDetectorFactory.getDetectorInterface(board);
        boolean pass = detector.testState();
        if (pass) {
            //System.out.println("XXX");

            pass = castleCheck(board, detector);
            if (pass) {
                pass = this.detectorPredicate.test(detector);
            }
        } else {
            //System.out.println(board.getReader().toFEN());
            //System.out.println(detector.getErrorMessage());

        }

        return pass;
    }

    private boolean makeJustMove(ChessBoard board, Coordinate origin, Coordinate target, String piece, boolean promotion, boolean enPassant) {
        UnMoveMaker moveMaker = new UnMoveMaker(board);
        if (promotion) {
            moveMaker.setPromotionFlag(promotion);
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
                        ////System.out.println(board.getReader().toFEN());
                        ////System.out.println(white);

                        ////System.out.println("CAN:T CASTLE");
                        return false;
                    } else if (white && piece.equals("queen")) {
                        ////System.out.println(board.getReader().toFEN());
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

    private class SolverRunner implements Runnable {

        private final ChessBoard board;
        private final Coordinate origin;
        private final String currentState;
        private final boolean any;
        private boolean finished = false;
        private List<String> states = new LinkedList<>();
        private String previousMoves;
        private int runnerDepth;

        SolverRunner(ChessBoard board, Coordinate origin, String currentState, boolean any, int currentDepth) {
            this.board = board;
            this.origin = origin;
            this.currentState = currentState.split(":")[0];
            this.previousMoves = currentState.split(":")[1];
            this.any = any;
            this.runnerDepth = currentDepth;
        }
        @Override
        public void run() {
            this.states = iterateThroughMoves(this.board, this.origin, this.currentState, this.any);
            this.finished = true;
        }
        public List<String> getStates() {
            return this.states;
        }

        public boolean isFinished() {
            return finished;
        }


    }
}
