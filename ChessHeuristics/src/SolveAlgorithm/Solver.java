package SolveAlgorithm;

import Heuristics.BoardInterface;
import Heuristics.Path;
import StandardChess.*;

import java.util.*;
import java.util.function.Predicate;

public class Solver implements Runnable {

    Predicate<String> fenPredicate = p -> true;
    private Predicate<SolverImpossibleStateDetector> detectorPredicate = d -> true;
    String originalBoard;
    private boolean legalFirstAlwaysTrue = false;
    private boolean legalFirst = false;
    private int additionalDepth = 2;
    private int maxDepth;
    private int numberOfSolutions = 100;
    private StateComparator comparator = new StateComparator();
    private StateLog stateLog = new StateLog();

//    private LinkedList<String> finalStates = new LinkedList<>();

    private final static List<String> PIECES = List.of("", "p", "r", "b", "n", "q");

    private int count = 0;

    private Map<String, SolverImpossibleStateDetector> stringDetectorMap = new HashMap<>();

    public Solver(){};

    public Solver(Predicate<String> fenPredicate, Predicate<SolverImpossibleStateDetector> detectorPredicate){
        this.fenPredicate = fenPredicate;
        this.detectorPredicate = detectorPredicate;
    }

    public Solver(Predicate<String> fenPredicate){
        this.fenPredicate = fenPredicate;
    }

    public List<String> solve(ChessBoard board, int depth) {
        this.maxDepth = depth;
        if (this.legalFirst) {
            this.legalFirstAlwaysTrue = true;
        }
        this.originalBoard = board.getReader().toFEN();
        List<String> solutions = new LinkedList<>();
        try {
            board.setTurn(board.getTurn().equals("white") ? "black" : "white");
            if (testAndRegisterState(board)) {
                solutions = iterate(this.originalBoard, depth, false);
            }

        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        System.out.println(solutions);
        return solutions;

    }

    private List<String> iterate(String startingFen, int depth, boolean any) throws InterruptedException {
//        Map<String, CombinedPawnMap>


        LinkedList<String> states = new LinkedList<>();
        LinkedList<String> finalStates = new LinkedList<>();
        ArrayList<Integer> stateSizes = new ArrayList<>();
        stateSizes.add(1);
        int currentDepth = 0;
//        LinkedList<List<Coordinate>> statePieces
        startingFen = startingFen + "::";
        if (any) {
            startingFen = startingFen + this.maxDepth;
        } else {
            startingFen = startingFen + "0";
        }
        states.add(startingFen);

        while (!states.isEmpty()) {
            if (finalStates.size() >= this.numberOfSolutions) {
                break;
            }

            int toAdd = stateSizes.get(currentDepth);
            stateSizes.remove(currentDepth);
            stateSizes.add(currentDepth, toAdd - 1);
            String state = states.pop();
            String[] stateDescription = state.split(":");
            String currentState = stateDescription[0];
            ChessBoard currentBoard = BoardBuilder.buildBoard(currentState);
//            if (any && legalFirst) {
//                System.out.println(currentState);
//            }

            if (currentDepth != depth) {
//                if (any && legalFirst) {
//                    System.out.println("A");
//                }
                List<Coordinate> pieces = allPieces(currentBoard);

                List<SolverRunner> runnerPool = new LinkedList<>();
                BoardInterface boardInterface = new BoardInterface(currentBoard);
//                System.out.println("twr");
                if (this.stateLog.test(boardInterface) != -1) {
                    for (Coordinate piece : pieces) {
                        if (!currentBoard.getEnPassant().equals(Coordinates.NULL_COORDINATE)) {
                            if (!piece.equals(currentBoard.getEnPassant())) {
                                continue;
                            }
                        }
//                    if (currentState.equals("k1K5/3pQ3/8/2B1P3/3P4/7P/8/7B w - -")) {
//                    }
//                    for (int i = 0 ; i < 10 ; i++) {
//                    System.out.println("launchingThread");
                        SolverRunner runner = new SolverRunner(this, currentBoard, piece, state, any && currentDepth == depth - 1);
                        runnerPool.add(runner);
                        Thread thread = new Thread(runner);
                        thread.start();
//                    }
                    }
                }
                int sleepCount = 0;
                while (!runnerPool.stream().allMatch(SolverRunner::isFinished)) {
                    sleepCount++;
//                    System.out.println("sleeping");

                    Thread.sleep(100L * sleepCount);
                }
//                System.out.println("threads done");

                List<String> newStates = runnerPool.stream().flatMap(r -> r.getStates().stream()).toList();
                if (newStates.isEmpty()) {
                    this.stateLog.register(new BoardInterface(currentBoard), false);
                }
//                    System.out.println(currentDepth);
//                    System.out.println(depth);
                toAdd = 0;
                if (stateSizes.size() >= currentDepth + 2) {
                    toAdd = stateSizes.get(currentDepth + 1);
                    stateSizes.remove(currentDepth + 1);
                }
                stateSizes.add(currentDepth + 1, toAdd + newStates.size());
//                    System.out.println("----");
//                    System.out.println(currentDepth);
//                    System.out.println(stateSizes.size());
                int finalCurrentDepth = currentDepth;
                newStates.forEach(s ->
                        states.push(s.split(":")[0]
                                + ":"
                                + s.split(":")[1]
                                + (stateDescription.length > 1 ? (", "
                                + stateDescription[1]) : "")
                        + ":" + (finalCurrentDepth + 1)));


                if (stateSizes.get(currentDepth + 1) > 0) {
//                    System.out.println(stateSizes.get(currentDepth + 1));
                    currentDepth++;
                }
            } else {
//                if (any && legalFirst) {
//                    System.out.println("B");
//                }
//                System.out.println("l");
//                boolean pass = false;
                if (any) {
//                    if (any && legalFirst) {
//                        System.out.println("C");
//                    }

                    if (!legalFirst || testAndRegisterState(currentBoard)) {
//                        if (any && legalFirst) {
//                            System.out.println("E");
//                        }

                            finalStates.add(currentState + ":" + stateDescription[1]);
                            return finalStates;

                    }
//                    if (any && legalFirst) {
//                        System.out.println("D");
//                    }

                } else {
                    this.legalFirst = true;
                    if (this.additionalDepth == 0 || !iterate(currentState, this.additionalDepth, true).isEmpty()) {

//                        if (testState(currentBoard)) {
//                        System.out.println(currentDepth);
                        //System.out.println("Here");
                        finalStates.add(currentState + ":" + stateDescription[1]);
//                            finalStates.add(currentState + ":" + stateDescription[1]);
//                        }
//                    }
                    }
                    if (!legalFirstAlwaysTrue) {
                        this.legalFirst = false;
                    }
                }
//                System.out.println(currentState);
//                System.out.println(any);

//                else {
//                    pass = true;
//                }
            }
            while (stateSizes.size() >= currentDepth + 1 && stateSizes.get(currentDepth) < 1) {
                stateSizes.remove(currentDepth);
//                System.out.println("c");
                if (currentDepth != 0) {
                    currentDepth--;
                }
            }
        }
//        System.out.println(currentDepth);
        return finalStates;
    }

    private String toLAN(ChessBoard board, Coordinate origin, Coordinate target, String piece, boolean castle) {
//        System.out.println(board.getReader().toFEN());
//
//        System.out.println(origin);
//        System.out.println(target);

        return
                board.at(target).getType().toUpperCase().charAt(board.at(target).getType().equals("knight") ? 1 : 0)+
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

//        if (origin.equals(new Coordinate(3, 4))) {
////            StandardPieceFactory.getInstance().getPiece("P").tryUnMove()
//            System.out.println(moves[0]);
//            System.out.println(board.getReader().toFEN());
//        }
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
//            List<Coordinate> tempMoves = new LinkedList<>(Arrays.stream(moves).toList());
//            tempMoves.addAll((Arrays.stream(additionalMoves).toList()));
//            moves = tempMoves.toArray(moves);
        } else if (((white && y == 5) || (!white && y == 2)) && type.equals("pawn")) {
            int offfset = white ? -1 : 1;
            Coordinate[] additionalMoves = new Coordinate[]{new Coordinate(x + 1, y + offfset), new Coordinate(x - 1, y + offfset)};
            states.addAll(iterateThroughMovesHelper(board, additionalMoves, origin, currentState, false, true, any, false));
//            System.out.println(additionalMoves[0]);
//            System.out.println(states);
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
        boolean white = board.getTurn().equals("white");
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
//                System.out.println("--------");
//                System.out.println(moves.length);
//                System.out.println(origin);
//
//                System.out.println(direction);
//                System.out.println(i);
                // For each UnTakeable piece
                for (String piece : pieces) {
                    ChessBoard currentBoard = BoardBuilder.buildBoard(board);

                    Coordinate target = Coordinates.add(origin, new Coordinate(direction.getX() * i, direction.getY() * i));
//                    this.count++;
//                    System.out.println(this.count);


                    if (makeJustMove(currentBoard,
                            origin,
                            target,
                            piece,
                            promotion,
                            enPassant)){
//                        System.out.println("pass");
//                        if (previousEnPassant) {
//                            System.out.println("justMove");
//                        }
                        currentBoard.setTurn(white ? "black" : "white");
                        String move = currentBoard.getReader().toFEN() + ":" + toLAN(currentBoard, origin, target, piece, castle);
                        currentBoard.setTurn(white ? "white" : "black");
                        if (CheckUtil.check(new BoardInterface(currentBoard))
                        && this.fenPredicate.test(move +
                                (currentState.split(":").length > 1 ? (":" + currentState.split(":")[2])
                                        : ":0"))) {
                            boolean pass = false;
                            String movedPiece = currentBoard.at(target).getType();
//                            System.out.println("?" + currentBoard.getReader().toFEN());
//                            System.out.println(nonIntrosiveMovement(promotion, piece, movedPiece));

                            if (legalFirst) {
                                pass = true;
                            } else if (nonIntrosiveMovement(promotion, piece, movedPiece)) {
                                pass = true;
                                this.stateLog.register(new BoardInterface(currentBoard), true);

                            } else if (testAndRegisterState(currentBoard
//                                            new Move(board.getReader().toFEN(), origin, target, movedPiece, piece, enPassant)
                                    )) {
                                pass = true;
                            }
                            if (pass) {
                                currentBoard.setTurn(white ? "black" : "white");
                                if (previousEnPassant) {
                                    currentBoard.setEnPassant(Coordinates.NULL_COORDINATE);
                                }
                                String boardAndMove = currentBoard.getReader().toFEN() + ":" + toLAN(currentBoard, origin, target, piece, castle);
//                                 if (this.fenPredicate.test(boardAndMove +
//                                         (currentState.split(":").length > 1 ? (":" + currentState.split(":")[2])
//                                                 : ":0"))) {
//                                     System.out.println(boardAndMove);
                                     states.add(boardAndMove);
//                                 }
                                if (!legalFirst && any) {
                                    break;
                                }
                            }
                        }
//                        }
                    } else {
//                        System.out.println("Fail");
                        // May break from creating a pawn
                        if (!piece.equals("pawn")) {
                            continueFlag = false;
                        }
                    }
                }
//                System.out.println(continueFlag);

                if (!continueFlag) {
                    break;
                }
            }
        }
        return states;
    }

    private static boolean nonIntrosiveMovement(boolean promotion, String piece, String movedPiece) {
        return !(movedPiece.charAt(0) == 'p')
                && piece.equals("") && !promotion;
    }

    public boolean makeMove(ChessBoard board, Coordinate origin, Coordinate target, String piece, boolean promotion, boolean enPassant) {
        boolean justMove = makeJustMove(board, origin, target, piece, promotion, enPassant);
        return justMove && testState(board);
    }

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
//            }
        }
        return (shortcut == 0 && pass) || shortcut == 1;
    }

    public boolean testState(ChessBoard board) {
        SolverImpossibleStateDetector detector;
        detector = StateDetectorFactory.getDetector(board);
        boolean pass = detector.testState();
        if (pass) {
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
//        String original = board.getReader().toFEN();
        //        if (s) {
//            System.out.println("::");
//            System.out.println(original);
//            System.out.println(board.getReader().toFEN());
//
//        }
//        if (origin.equals(new Coordinate(3, 4))) {
//            System.out.println(target);
//        }

        return moveMaker.makeUnMove(origin, target);
    }

    private boolean castleCheck(ChessBoard board, SolverImpossibleStateDetector detector) {


        boolean white = true;
        for (int i = 0 ; i < 2 ; i++) {
            String piece = "king";
            for (int j = 0 ; j < 2 ; j++) {
//                System.out.println(white);
//                System.out.println(piece);
//                System.out.println(board.canCastle(piece, white ? "white" : "black"));
//                System.out.println(detector.canCastle(white));

                if (board.canCastle(piece, white ? "white" : "black")) {
                    if (!detector.canCastle(white)) {
                        return false;
                    } else if (white && piece.equals("queen")) {
                        //System.out.println(board.getReader().toFEN());
                    }
                }
                piece = "queen";
            }
            white = false;
        }
//        System.out.println(board.canCastle("queen", "white"));
//
        //System.out.println(board.getReader().toFEN());
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

    @Override
    public void run() {

    }
    public void statest(int currentDepth) {

    }

    public List<String> iterateTwo(String startingFen, int depth, boolean any) throws InterruptedException {
//        Map<String, CombinedPawnMap>

        Map<Integer, LinkedList<String>> depthFen = new HashMap<>();
        LinkedList<String> finalStates = new LinkedList<String>();
        LinkedList<String> stateOne = new LinkedList<>();
        startingFen = startingFen + "::";
        stateOne.add(startingFen);
        depthFen.put(0, stateOne);
        List<SovlverRunnerTwo> runnerPool = new LinkedList<>();
        while ((any && !finalStates.isEmpty()) || finalStates.size() < this.numberOfSolutions ) {
//            System.out.println(any);
            runnerPool.stream().filter(SovlverRunnerTwo::isFinished).toList().forEach(s -> {
                finalStates.addAll(s.getFinalStates());
                if (!depthFen.containsKey(s.currentDepth + 1)) {
                    depthFen.put(s.currentDepth + 1, new LinkedList<>());
                }
                depthFen.get(s.currentDepth + 1).addAll(s.getNewStates());
                runnerPool.remove(s);
            });
            if (depthFen.values().stream().flatMap(LinkedList::stream).toList().isEmpty() && runnerPool.isEmpty()) {
                break;
            }
            if (any && !finalStates.isEmpty()) {
                break;
            }
            depthFen.keySet().stream().filter(i -> i > depth).toList().stream().forEach(depthFen::remove);
//                break;

            // Get a state of the highest possible value
            Map.Entry<Integer, LinkedList<String>> entry = depthFen.entrySet().stream()
                    .filter(e -> !(e.getKey() > depth))
                    .filter(e -> !e.getValue().isEmpty())
                    .reduce((e, f) -> e.getKey() > f.getKey() ? e : f)
                    .orElse(null);
            if (entry == null) {
//                System.out.println(depthFen);
                continue;
            }
            int currentDepth = entry.getKey();
            String fen = entry.getValue().pop();
            SovlverRunnerTwo sovlverRunnerTwo = new SovlverRunnerTwo(this,
                    this.legalFirst, this.additionalDepth, any, currentDepth, fen, depth, numberOfSolutions);
            runnerPool.add(sovlverRunnerTwo);
//            System.out.println("launching");
            new Thread(sovlverRunnerTwo).start();
//        System.out.println(currentDepth);
        }
        return finalStates;
    }
}
