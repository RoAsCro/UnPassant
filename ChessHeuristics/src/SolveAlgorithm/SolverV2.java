package SolveAlgorithm;

import Heuristics.BoardInterface;
import Heuristics.Detector.DetectorInterface;
import Heuristics.Detector.StateDetectorFactory;
import Heuristics.Path;
import StandardChess.*;

import java.util.*;
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
    ArrayList<List<SolverRunnerV2>> runnerPoolList = new ArrayList<>();
    private List<SolverRunnerV2> runnerPoolV2 = new LinkedList<>();
    private List<SolverRunnerV2> runnerPoolV2Any = new LinkedList<>();


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
                solutions = iterate(this.originalBoard, depth, false, 0);
            }

        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        return solutions;

    }

    private List<String> iterate(String startingFen, int depth, boolean any, int recursionDepth) throws InterruptedException {
        ArrayList<LinkedList<State>> stateArray = new ArrayList<>();
        Map<String, Boolean> potentialStatePool = new HashMap<>();
//        int startingI = any ? this.maxDepth : 0;
//        int endI = any ? this.maxDepth + depth : depth;
        int qualifiedDepth = (recursionDepth > 0 ? this.maxDepth : 0)
                + (recursionDepth > 1 ? this.additionalDepth : 0)
                + (recursionDepth > 2 ? (recursionDepth - 2) : 0);
        List<SolverRunnerV2> runnerPool = new LinkedList<>();
        this.runnerPoolList.add(recursionDepth, runnerPool);
        for (int i = 0 ; i <= depth ; i++) {
            stateArray.add(i, new LinkedList<>());
        }
//        startingFen = startingFen + "::";
//        startingFen = startingFen + qualifiedDepth;

        stateArray.get(0).add(new State(startingFen, "", 0, false, ""));
        LinkedList<String> finalStates = new LinkedList<>();
//        System.out.println(startingFen);
//        if (any) {
//            System.out.println(runnerPoolV2Any);
//        }
//        System.out.println(startingFen);
//        List<SolverV2.SolverRunner> runnerPool = new LinkedList<>();
        while (true) {
//            System.out.println(runnerPool.size());
//            System.out.println(stateArray.stream().map(List::size).reduce(Integer::sum).orElse(0));


            List<SolverRunnerV2> toRemove = runnerPool.stream().filter(r -> r.finished).toList();
            toRemove.forEach(r -> {
                runnerPool.remove(r);
                if (!r.newState.equals("")) {
                    if (r.state.depth() >= maxDepth) {
//                        System.out.println("New");
//                        System.out.println(r.state.depth());
                    }
                    if (stateArray.size() <= r.state.depth() + 1) {
                        stateArray.add(r.state.depth() + 1, new LinkedList<>());
//                        System.out.println("add"+stateArray.size());

                    }
                    stateArray.get(r.state.depth() + 1).add(new State(r.newState.split(":")[0],
                            r.state.moves() + ", " + r.newState.split(":")[1],
                            r.state.depth()+1, r.state.any(), r.state.previousState()));
//                    stateArray.get(r.runnerDepth + 1 - qualifiedDepth).add(r.newState.split(":")[0]
//                            + ":"
//                            + r.newState.split(":")[1] + ", "
//                            + (r.previousMoves)
//                            + ":" + (r.runnerDepth + 1));
                }
            });

            if (potentialStatePool.values().stream().filter(b -> b).toList().size() >= this.numberOfSolutions ||
                    (stateArray.stream().allMatch(List::isEmpty) && runnerPool.isEmpty())) {
//                System.out.println("Break");
                break;
            }
//            if (runnerPool.size() > 100) {
//                continue;
//            }
            State state = null;
            int currentDepth = 0;

            for (int i = stateArray.size() - 1 ; i >= 0 ; i--) {
                LinkedList<State> currentList = stateArray.get(i);
                if (!currentList.isEmpty()) {
                    state = stateArray.get(i).pop();
                    currentDepth = i;
                    break;
                }
            }
//            if (stateArray.size() > maxDepth + 1) {
//                System.out.println("Size" + stateArray.get(3));
//                System.out.println("State" + currentDepth);
//                if (!Objects.isNull(state)){
//                    System.out.println(state);
//                }
//            }
            if (Objects.isNull(state)) {
//                System.out.println("Continue1");
                continue;
            }

//            if (state.depth())

            State currentState = state;
            ChessBoard currentBoard = BoardBuilder.buildBoard(state.fen());

            if (currentDepth == depth) {

                potentialStatePool.put(currentState.fen(), false);
                currentState = new State(currentState.fen(),
                        currentState.moves(), currentState.depth(), currentState.any(), currentState.fen());
            } if (additionalDepth== 0 || currentDepth >= depth + additionalDepth){
//                System.out.println("Succeed");
                CheckUtil.switchTurns(currentBoard);
                if (!CheckUtil.eitherInCheck(new BoardInterface(currentBoard))) {
                    potentialStatePool.put(currentState.previousState(), true);
                    continue;
                }
                CheckUtil.switchTurns(currentBoard);
            }
            if (currentDepth >= depth) {
                if (potentialStatePool.get(currentState.previousState())) {
                    continue;
                }
            }

//            if (currentDepth < depth) {
//                System.out.println("Depth");
                List<Coordinate> pieces = allPieces(currentBoard);
//                System.out.println("all" + pieces.size());

                for (Coordinate piece : pieces) {
                    if (!currentBoard.getEnPassant().equals(Coordinates.NULL_COORDINATE)) {
                        if (!piece.equals(currentBoard.getEnPassant())) {
//                            System.out.println("EPContinue");
                            continue;
                        }
                    }
//                    SolverRunner runner = new SolverRunner(currentBoard, piece, state,
//                            any && currentDepth == depth - 1, currentDepth);
//                    runnerPool.add(runner);
//                    Thread thread = new Thread(runner);
//                    thread.start();
//                    System.out.println(piece);
                    iterateThroughMoves(currentBoard, piece, currentState, any, recursionDepth);
//                    System.out.println(runnerPool.size());
                }
//                System.out.println("---");
//                System.out.println(runnerPoolV2.size());
//                System.out.println(currentDepth);
//            }
//            else {
//                System.out.println("Depth reached");
//                if (any) {
//                    CheckUtil.switchTurns(currentBoard);
//                    System.out.println("FINAL TESTING");
//                    if (!legalFirst || testState(currentBoard)) {
//                        boolean pass = true;
//                        if (CheckUtil.eitherInCheck(new BoardInterface(currentBoard))) {
//                            pass = !iterate(currentState.split(":")[0], 1, true, recursionDepth + 1).isEmpty();
//                        }
//                        if (pass) {
//                            finalStates.add(currentState);
//                            runnerPool.clear();
//                            return finalStates;
//                        }
//                    }
//                } else {
//                    potentialStatePool.put(currentState, false);
//                    if (this.additionalDepth == 0 || !iterate(currentState.split(":")[0], this.additionalDepth, true, recursionDepth + 1).isEmpty()) {
//                        boolean pass = true;
//                        if (this.additionalDepth == 0) {
//                            CheckUtil.switchTurns(currentBoard);
//                            if (CheckUtil.eitherInCheck(new BoardInterface(currentBoard))) {
//                                pass = !iterate(currentState.split(":")[0], 1, true, recursionDepth + 1).isEmpty();
//                            }
//                        }
//                        if (pass) {
//                            finalStates.add(currentState);
//                        }
//                    }
////                    if (!legalFirstAlwaysTrue) {
////                        this.legalFirst = false;
////                    }
//                }
//            }
        }
        runnerPool.clear();
//        System.out.println(potentialStatePool);
        return potentialStatePool.entrySet().stream().filter(Map.Entry::getValue).map(Map.Entry::getKey).toList();
    }

//    private void iteratePartTwo(String state, boolean any, int currentDepth, int depth) {
//        String[] stateDescription = state.split(":");
//        String currentState = stateDescription[0];
//        ChessBoard currentBoard = BoardBuilder.buildBoard(currentState);
//        List<Coordinate> pieces = allPieces(currentBoard);
//        List<String> newStates = new LinkedList<>();
////                List<Solver.SolverRunner> runnerPool = new LinkedList<>();
//        BoardInterface boardInterface = new BoardInterface(currentBoard);
//        for (Coordinate piece : pieces) {
//            if (!currentBoard.getEnPassant().equals(Coordinates.NULL_COORDINATE)) {
//                if (!piece.equals(currentBoard.getEnPassant())) {
//                    continue;
//                }
//            }
//            newStates.addAll(iterateThroughMoves(currentBoard, piece, state, any && currentDepth == depth - 1));
//        }
//        LinkedList<String> states  = new LinkedList<>();
//        int finalCurrentDepth = currentDepth;
//        newStates.forEach(s ->
//                states.push(s.split(":")[0]
//                        + ":"
//                        + s.split(":")[1]
//                        + (stateDescription.length > 1 ? (", "
//                        + stateDescription[1]) : "")
//                        + ":" + (finalCurrentDepth + 1)));
//
//    }

    private boolean testAndRegisterState(ChessBoard board) {
//        System.out.println("-----------");
//        System.out.println(board.getReader().toFEN());
        boolean pass = false;
//        int shortcut = this.comparator.findState(move);
        int shortcut = this.stateLog.test(new BoardInterface(board));
        if (shortcut == 0) {
            pass = testState(board);
//            if (pass) {
//                comparator.registerState(move, pass);
//            System.out.println("next");
            this.stateLog.register(new BoardInterface(board), pass);

        }
        return (shortcut == 0 && pass) || shortcut == 1;
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

    public List<String> iterateThroughMoves(ChessBoard board, Coordinate origin,
                                            State currentState, boolean any, int recursionDepth) {
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
            states.addAll(iterateThroughMovesHelper(board, additionalMoves, origin, currentState, true, false, any, false, recursionDepth));
            if (!legalFirst && any && !states.isEmpty()) {
                return states;
            }

        } else if (((white && y == 5) || (!white && y == 2)) && type.equals("pawn")) {
            int offfset = white ? -1 : 1;
            Coordinate[] additionalMoves = new Coordinate[]{new Coordinate(x + 1, y + offfset), new Coordinate(x - 1, y + offfset)};
            states.addAll(iterateThroughMovesHelper(board, additionalMoves, origin, currentState, false, true, any, false, recursionDepth));
            if (!legalFirst && any && !states.isEmpty()) {
                return states;
            }
        } else if (king) {
            Coordinate[] additionalMoves = new Coordinate[]{new Coordinate(x - 2, y), new Coordinate(x + 2, y)};
            states.addAll(iterateThroughMovesHelper(board, additionalMoves, origin, currentState, false, false, any, true, recursionDepth));
            if (!legalFirst && any && !states.isEmpty()) {
                return states;
            }

        } else if (type.equals("pawn")) {
            Coordinate[] additionalMoves = new Coordinate[]{new Coordinate(x, y +  (white ? -2 : 2))};
            states.addAll(iterateThroughMovesHelper(board, additionalMoves, origin, currentState, false, false, any, false, recursionDepth));
            if (!legalFirst && any && !states.isEmpty()) {
                return states;
            }

        }
        states.addAll(iterateThroughMovesHelper(board, moves, origin, currentState, false, false, any, false, recursionDepth));
        return states;
    }

    private List<String> iterateThroughMovesHelper(ChessBoard board, Coordinate[] moves,
                                                   Coordinate origin, State currentState, boolean promotion,
                                                   boolean enPassant,
                                                   boolean any,
                                                   boolean castle, int recursionDepth) {
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
            for (int i = 1 ; i < 8; i++) {
//                System.out.println("for loop");
                boolean continueFlag = true;
//
                if (previousEnPassant) {
//                    System.out.println("Two");
                    i = 2;
                    continueFlag = false;
                }
                for (String piece : pieces) {
//                    if (currentState.depth()> maxDepth) {
////                        System.out.println("make");
////                        System.out.println(currentState.depth());
//
//                    }
                    SolverRunnerV2 runnerV2 =
                            new SolverRunnerV2(board, origin, direction, piece,
                                    i, previousEnPassant, castle, enPassant, currentState);
                    this.runnerPoolList.get(recursionDepth).add(runnerV2);
                    new Thread(runnerV2).start();
//                    ChessBoard currentBoard = BoardBuilder.buildBoard(board);
//
//                    Coordinate target = Coordinates.add(origin, new Coordinate(direction.getX() * i, direction.getY() * i));
//                    if (makeJustMove(currentBoard, origin, target, piece, promotion, enPassant)){
//                        CheckUtil.switchTurns(currentBoard);
//                        String move = currentBoard.getReader().toFEN() + ":" + toLAN(currentBoard, origin, target, piece, castle);
//                        CheckUtil.switchTurns(currentBoard);
//                        if (CheckUtil.check(new BoardInterface(currentBoard))
//                                && this.fenPredicate.test(move +
//                                (currentState.split(":").length > 1 ? (":" + currentState.split(":")[2])
//                                        : ":0"))) {
//                            boolean pass = false;
//                            String movedPiece = currentBoard.at(target).getType();
//
//                            if (legalFirst) {
//                                pass = true;
//                            } else if (this.allowNonIntrusiveMovement && nonIntrusiveMovement(promotion, piece, movedPiece)) {
//                                pass = true;
//                                this.stateLog.register(new BoardInterface(currentBoard), true);
//
//                            } else if (testState(currentBoard)) {
//                                pass = true;
//                            }
//                            if (pass) {
//                                CheckUtil.switchTurns(currentBoard);
//                                if (previousEnPassant) {
//                                    currentBoard.setEnPassant(Coordinates.NULL_COORDINATE);
//                                }
//                                String boardAndMove = currentBoard.getReader().toFEN() + ":" + toLAN(currentBoard, origin, target, piece, castle);
//                                states.add(boardAndMove);
//                                if (!legalFirst && any) {
//                                    break;
//                                }
//                            }
//                        }
//                    } else {
//                        // May break from creating a pawn
//                        if (!piece.equals("pawn")) {
//                            continueFlag = false;
//                        }
//                    }
//                }
//                if (!continueFlag) {
//                    break;
//                }
                }
                if (previousEnPassant) {
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
                    if (!detector.canCastle(white, piece.equals("queen"))) {
                        ////System.out.println(board.getReader().toFEN());
                        ////System.out.println(white);

                        ////System.out.println("CAN:T CASTLE");
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
//            this.states = iterateThroughMoves(this.board, this.origin, this.currentState, this.any);
            this.finished = true;
        }
        public List<String> getStates() {
            return this.states;
        }

        public boolean isFinished() {
            return finished;
        }


    }

    private class SolverRunnerV2 implements Runnable {

        private final ChessBoard board;
        private final Coordinate origin;
        private boolean finished = false;
        private String newState = "";
        boolean promotion;
        boolean enPassant;
        private State state;
        boolean castle;
        boolean previousEnPassant;
        int i;
        Coordinate direction;
        String piece;

        SolverRunnerV2(ChessBoard board, Coordinate origin,
                       Coordinate direction, String piece, int i, boolean previousEnPassant, boolean castle,
                       boolean enPassant, State state) {
            this.board = board;
            this.origin = origin;
//            System.out.println(currentState);
            this.direction = direction;
            this.piece = piece;
            this.i = i;
            this.previousEnPassant = previousEnPassant;
            this.castle = castle;
            this.enPassant = enPassant;
            this.state = state;
//            if (!state.previousState().equals("")) {

//            }
        }
        @Override
        public void run() {
                ChessBoard currentBoard = BoardBuilder.buildBoard(board);
                Coordinate target = Coordinates.add(origin, new Coordinate(direction.getX() * i, direction.getY() * i));
                if (makeJustMove(currentBoard, origin, target, piece, promotion, enPassant)){
                    CheckUtil.switchTurns(currentBoard);
                    String move = currentBoard.getReader().toFEN() + ":" + toLAN(currentBoard, origin, target, piece, castle);
                    CheckUtil.switchTurns(currentBoard);
                    if (CheckUtil.check(new BoardInterface(currentBoard))
                            && fenPredicate.test(move + ":" + state.depth())) {
                        boolean pass = false;
                        String movedPiece = currentBoard.at(target).getType();

                        if (legalFirst) {
                            pass = true;
                        } else if (allowNonIntrusiveMovement && nonIntrusiveMovement(promotion, piece, movedPiece)) {
                            pass = true;
                        } else if (testState(currentBoard)) {
                            pass = true;
                        }
                        if (pass) {

                            CheckUtil.switchTurns(currentBoard);
                            if (previousEnPassant) {
                                currentBoard.setEnPassant(Coordinates.NULL_COORDINATE);
                            }
                            String boardAndMove = currentBoard.getReader().toFEN() + ":" +
                                    toLAN(currentBoard, origin, target, piece, castle);
                            newState = boardAndMove;
//                            if (state.depth() >= maxDepth) {
//                                System.out.println(state.depth());
//                                System.out.println(newState);
//
//                            }
                        }
                    }
                }

            this.finished = true;
        }
        public String getState() {

            return this.newState;
        }

        public boolean isFinished() {
            return finished;
        }


    }
}
