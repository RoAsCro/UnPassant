import Heuristics.BoardInterface;
import Heuristics.Deductions.CombinedPawnMap;
import Heuristics.Path;
import StandardChess.*;

import java.util.*;

public class Solver {

    String originalBoard;
    private boolean legalFirst = false;
    private int additionalDepth = 2;
    private int numberOfSolutions = 5;

    private final static List<String> PIECES = List.of("", "p", "r", "b", "n", "q");

    private int count = 0;

    private Map<String, SolverImpossibleStateDetector> stringDetectorMap = new HashMap<>();


    public void solve(ChessBoard board, int depth) {
        this.originalBoard = board.getReader().toFEN();
        System.out.println(iterate(this.originalBoard, depth, false));

    }

    private List<String> iterate(String startingFen, int depth, boolean any) {
//        Map<String, CombinedPawnMap>


        LinkedList<String> states = new LinkedList<>();
        LinkedList<String> finalStates = new LinkedList<>();
        LinkedList<Integer> stateSizes = new LinkedList<>();
        stateSizes.push(1);
        int currentDepth = 0;
//        LinkedList<List<Coordinate>> statePieces
        states.add(startingFen + ": ");
        while (!states.isEmpty()) {
            if (finalStates.size() >= this.numberOfSolutions) {
                break;
            }
            stateSizes.push(stateSizes.pop() - 1);
            String state = states.pop();
            String[] stateDescription = state.split(":");
            String currentState = stateDescription[0];
            ChessBoard currentBoard = BoardBuilder.buildBoard(currentState);
            if (any && legalFirst) {
                System.out.println(currentState);
            }

            if (currentDepth != depth) {
//                if (any && legalFirst) {
//                    System.out.println("A");
//                }

                List<Coordinate> pieces = allPieces(currentBoard);
                stateSizes.push(0);
                for (Coordinate piece : pieces) {
                    if (!currentBoard.getEnPassant().equals(Coordinates.NULL_COORDINATE)) {

                        if (!piece.equals(currentBoard.getEnPassant())){

                            continue;
                        }
                    }
//                    if (currentState.equals("k1K5/3pQ3/8/2B1P3/3P4/7P/8/7B w - -")) {
//                    }
                    List<String> newStates = iterateThroughMoves(currentBoard, piece, state, any && currentDepth == depth - 1);
//                    System.out.println(currentDepth);
//                    System.out.println(depth);
                    stateSizes.push(stateSizes.pop() + newStates.size());
                    newStates.forEach(s ->
                            states.push(s.split(":")[0]
                                    + ":"
                                    + s.split(":")[1]
                                    + ", "
                                    + stateDescription[1]));

                }
//                System.out.println("finish");
                currentDepth++;
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

                    if (!legalFirst || testState(currentBoard)) {
//                        if (any && legalFirst) {
//                            System.out.println("E");
//                        }

                        finalStates.add(currentState);
                        return finalStates;

                    }
//                    if (any && legalFirst) {
//                        System.out.println("D");
//                    }

                } else {
                    this.legalFirst = true;
                    if (this.additionalDepth == 0 || !iterate(currentState, this.additionalDepth, true).isEmpty()) {

//                        if (testState(currentBoard)) {
                        finalStates.add(currentState + ":" + stateDescription[1]);
//                            finalStates.add(currentState + ":" + stateDescription[1]);
//                        }
//                    }
                    }
                    this.legalFirst = false;
                }
//                System.out.println(currentState);
//                System.out.println(any);

//                else {
//                    pass = true;
//                }
            }
            if (stateSizes.getFirst() < 1) {
                stateSizes.pop();
                currentDepth--;
            }
        }
        return finalStates;
    }

    private String toLAN(ChessBoard board, Coordinate origin, Coordinate target, String piece) {
//        System.out.println(board.getReader().toFEN());
//
//        System.out.println(origin);
//        System.out.println(target);

        return
                board.at(target).getType().toUpperCase().charAt(board.at(target).getType().equals("knight") ? 1 : 0)+
                Coordinates.readableString(target)
                + (piece.equals("") ? "-" : "x")
                + piece.toUpperCase()
                + Coordinates.readableString(origin);
    }

    private List<Coordinate> allPieces(ChessBoard board) {
        return new BoardInterface(board).getBoardFacts().getAllCoordinates(board.getTurn())
                .values().stream().flatMap(Path::stream).toList();
    }

    private List<String> iterateThroughMoves(ChessBoard board, Coordinate origin, String currentState, boolean any) {
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
        if (((white && y == 7) || (!white && y == 0)) && !type.equals("king")) {
            Coordinate[] additionalMoves = StandardPieceFactory.getInstance().getPiece(white ? "p" : "P").getMoves(origin);
            states.addAll(iterateThroughMovesHelper(board, additionalMoves, origin, currentState, true, false, any));
            if (!legalFirst && any && !states.isEmpty()) {
                return states;
            }
//            List<Coordinate> tempMoves = new LinkedList<>(Arrays.stream(moves).toList());
//            tempMoves.addAll((Arrays.stream(additionalMoves).toList()));
//            moves = tempMoves.toArray(moves);
        } else if (((white && y == 5) || (!white && y == 2)) && type.equals("pawn")) {
            int x = origin.getX();
            int offfset = white ? -1 : 1;
            Coordinate[] additionalMoves = new Coordinate[]{new Coordinate(x + 1, y + offfset), new Coordinate(x - 1, y + offfset)};
            states.addAll(iterateThroughMovesHelper(board, additionalMoves, origin, currentState, false, true, any));
//            System.out.println(additionalMoves[0]);
//            System.out.println(states);

            if (!legalFirst && any && !states.isEmpty()) {
                return states;
            }
        }


        states.addAll(iterateThroughMovesHelper(board, moves, origin, currentState, false, false, any));
//        if (origin.equals(new Coordinate(3, 4))) {
////            StandardPieceFactory.getInstance().getPiece("P").tryUnMove()
//            System.out.println("moves[0]");
//            System.out.println(any);
//            System.out.println(states);
//        }
//        System.out.println(any);
        return states;
    }

    private List<String> iterateThroughMovesHelper(ChessBoard board, Coordinate[] moves,
                                                   Coordinate origin, String currentState, boolean promotion,
                                                   boolean enPassant,
                                                   boolean any) {
        boolean previousEnPassant = !board.getEnPassant().equals(Coordinates.NULL_COORDINATE);
        List<String> states = new LinkedList<>();
        boolean white = board.getTurn().equals("white");
        for (Coordinate currentMove : moves) {
            if (previousEnPassant) {
                if (currentMove.getX() != origin.getX()) {
                    continue;
                }
            }
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
                for (String piece : pieces) {
                    ChessBoard currentBoard = BoardBuilder.buildBoard(board.getReader().toFEN());

                    Coordinate target = Coordinates.add(origin, new Coordinate(direction.getX() * i, direction.getY() * i));
//                    this.count++;
//                    System.out.println(this.count);

                    if (makeJustMove(currentBoard,
                            origin,
                            target,
                            piece,
                            promotion,
                            enPassant)){
//                        if (previousEnPassant) {
//                            System.out.println("justMove");
//                        }


                            if (CheckUtil.check(new BoardInterface(currentBoard))) {
                            boolean pass = false;
                            if (legalFirst) {
                                pass = true;
                            } else if (!(currentBoard.at(target).getType().charAt(0) == 'p')
                                    && piece.equals("") && !promotion || testState(currentBoard)) {
                                pass = true;
                            }
//
//
                            if (pass) {
                                currentBoard.setTurn(white ? "black" : "white");
                                if (previousEnPassant) {
                                    currentBoard.setEnPassant(Coordinates.NULL_COORDINATE);
                                }
                                states.add(currentBoard.getReader().toFEN() + ":" + toLAN(currentBoard, origin, target, piece));
                                if (!legalFirst && any) {
                                    break;
                                }
                            }
                        }
//                        }
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

    public boolean makeMove(ChessBoard board, Coordinate origin, Coordinate target, String piece, boolean promotion, boolean enPassant) {
        boolean justMove = makeJustMove(board, origin, target, piece, promotion, enPassant);
        return justMove && testState(board);
    }

    private boolean testState(ChessBoard board) {
//        String lastMove =  move.split(":")[1];
//        if (!(board.at(target).getType().charAt(0) == 'p') && piece.equals("") && !promotion) {
////            System.out.println(board.getReader().toFEN());
//            return true;
//        }
        SolverImpossibleStateDetector detector;
//        if (this.stringDetectorMap.containsKey(move)) {
//            detector = DetectorUpdater.update(board, move, this.stringDetectorMap.get(move));
//        } else {
            detector = StateDetectorFactory.getDetector(board);
//        }
        // System.out.println(board.getReader().toFEN());
        boolean pass = detector.testState();
//        if (pass) {
//            this.stringDetectorMap.put(move, detector);
//        }
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

    public int getAdditionalDepth() {
        return additionalDepth;
    }

    public void setAdditionalDepth(int additionalDepth) {
        this.additionalDepth = additionalDepth;
    }
}
