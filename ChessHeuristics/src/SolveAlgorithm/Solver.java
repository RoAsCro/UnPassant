package SolveAlgorithm;

import Heuristics.BoardInterface;
import Heuristics.Detector.DetectorInterface;
import Heuristics.Detector.StateDetectorFactory;
import Heuristics.Path;
import SolveAlgorithm.UnMoveConditions.MovementCondition;
import SolveAlgorithm.UnMoveConditions.PieceCondition;
import StandardChess.*;

import java.util.LinkedList;
import java.util.List;
import java.util.function.Predicate;

import static Heuristics.HeuristicsUtil.*;

public class Solver {
    private static final Predicate<String> NORMAL_MOVE =
            new MovementCondition("-")
                    .and(new PieceCondition('P').negate());
    Predicate<String> fenPredicate = p -> true;
    private Predicate<DetectorInterface> detectorPredicate = d -> true;
    private boolean allowNonIntrusiveMovement = true;
    private boolean legalFirstAlwaysTrue = false;
    private boolean legalFirst = false;
    private int additionalDepth = 2;
    private int maxDepth;
    private int numberOfSolutions = 10;

    private final static List<String> PIECES = List.of("", "p", "r", "b", "n", "q");

    private int count = 0;
    private int testCount = 0;


    public Solver(){}

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
        List<String> solutions = new LinkedList<>();
        String originalBoard = board.getReader().toFEN();
        CheckUtil.switchTurns(board);
        if (testState(board)) {
            solutions = iterate(originalBoard, depth, false, 0);
        }
        System.out.println("NIMTIME");
        System.out.println(count);
        System.out.println("TESTTIME");
        System.out.println(testCount);
        System.out.println(solutions);

        return solutions;
    }

    private List<String> iterate(String startingFen, int depth, boolean any, int recursionDepth) {
        LinkedList<String> states = new LinkedList<>();
        LinkedList<String> finalStates = new LinkedList<>();
        startingFen = startingFen + "::";
        startingFen = startingFen + recursionDepth;
        states.add(startingFen);

        while (!states.isEmpty()) {
            if (finalStates.size() >= this.numberOfSolutions) {
                break;
            }
            String state;
            /* If in an 'any' state, there's a good chance the algorithm is trying to get out of check */
            if (any) {
                state =
                        states.stream().filter(s -> s.charAt(s.length() - 1)
                                        == states.getFirst().charAt(states.getFirst().length() - 1))
                                .filter(s ->!CheckUtil.eitherInCheck(
                                        new BoardInterface(BoardBuilder.buildBoard(s.split(":")[0]))))
                                .findAny()
                                .orElse(states.getFirst());
                states.remove(state);
            } else {
                state = states.pop();
            }

            String[] stateDescription = state.split(":");
            String currentFEN = stateDescription[0];
            ChessBoard currentBoard = BoardBuilder.buildBoard(currentFEN);
            CheckUtil.switchTurns(currentBoard);
//            if (allowNonIntrusiveMovement && nonIntrusiveMovement(state) && !testState(currentBoard)) {
//                throw new RuntimeException(state);
//            }
            if (!(allowNonIntrusiveMovement && nonIntrusiveMovement(state)) &&
                    !testState(currentBoard)) {
                continue;
            }
            CheckUtil.switchTurns(currentBoard);

            String movement = stateDescription[1];
            int currentDepth = Integer.parseInt(stateDescription[2]);
            if (currentDepth != depth + recursionDepth) {
                List<Coordinate> pieces = allPieces(currentBoard);
                List<String> newStates = new LinkedList<>();
                Coordinate enPassant = currentBoard.getEnPassant();
                boolean enPassantNull = enPassant.equals(Coordinates.NULL_COORDINATE);
                for (Coordinate piece : pieces) {
                    if (!enPassantNull && !piece.equals(enPassant)) {
                        continue;
                    }
                    newStates.addAll(iterateThroughMoves(currentBoard, piece, state, any
                            && currentDepth == depth - 1));
                }
                String addition = (!movement.equals("") ? ", " : "");
                newStates.forEach(s ->
                        states.push(s.split(":")[0]
                                + ":"
                                + s.split(":")[1]
                                + addition
                                + movement
                                + ":" + (currentDepth + 1)));
            } else {
                if (any) {
                    CheckUtil.switchTurns(currentBoard);
//                    if (!legalFirst || testState(currentBoard)) {
                    boolean pass = true;
                    if (compulsoryContinuation(currentBoard)) {
                        pass = !iterate(currentFEN,
                                1, true, recursionDepth + depth).isEmpty();
                    }
                    if (pass) {
                        finalStates.add(currentFEN + ":" + movement);
                        return finalStates;
                    }
//                    }
                } else {
//                    this.legalFirst = true;
                    if (this.additionalDepth == 0 || !iterate(currentFEN,
                            this.additionalDepth, true, recursionDepth + depth).isEmpty()) {
                        boolean pass = true;
                        if (this.additionalDepth == 0) {
                            CheckUtil.switchTurns(currentBoard);
                            if (compulsoryContinuation(currentBoard)) {
                                pass = !iterate(currentFEN,
                                        1, true,recursionDepth + depth).isEmpty();
                            }
                        }
                        if (pass) {
                            finalStates.add(currentFEN + ":" + movement);
                        }
                    }
                }
            }
        }
        return finalStates;
    }

    private String toLAN(ChessBoard board, Coordinate origin, Coordinate target, String piece,
                         boolean castle, boolean enPassant) {
        String movementString = castle ? (origin.getX() < target.getX()? "O-O" : "O-O-O") :
                (enPassant ? "e.p." : piece.equals("") ? "-" : "x");
        return PIECE_CODES.get(board.at(target).getType()).toUpperCase()+
                Coordinates.readableString(target)
                + movementString
                + piece.toUpperCase()
                + Coordinates.readableString(origin);
    }

    /**
     * Returns a List of Coordinates of all pieces belonging to the players whose turn it is on the given board.
     * @param board the board being checked
     * @return a List of Coordinates of all pieces belonging to the players whose turn it is on the given board
     */
    private List<Coordinate> allPieces(ChessBoard board) {
        return new BoardInterface(board).getBoardFacts().getAllCoordinates(board.getTurn())
                .values().stream().flatMap(Path::stream).toList();
    }

    /**
     * Finds all moves available to the current piece and trys each one, returning the states resulting from the
     * valid moves found, plus a colon-separated descriptor of the move.
     * @param board the board on which the move will be made
     * @param origin the starting Coordinate of the move
     * @param currentState a String of the current board state, plus previous moves and turn number
     * @param any
     * @return a List of Strings representing states resulting from legal moves, plus a colon-separated
     * description of the move
     */
    public List<String> iterateThroughMoves(ChessBoard board, Coordinate origin, String currentState, boolean any) {
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
            // Don't try to move pieces that are locked by UnCastling
            // TODO fix this
            if ((rook && (white && (origin.equals(Coordinates.WHITE_KING_ROOK) || origin.equals(Coordinates.WHITE_QUEEN_ROOK)) ||
                    (!white && origin.equals(Coordinates.BLACK_KING_ROOK) || origin.equals(Coordinates.BLACK_QUEEN_ROOK)))
            && board.canCastle(origin.getX() == Coordinates.WHITE_KING_ROOK.getX() ? "king" : "queen", colour))
                    || (king && (board.canCastle("queen", colour) || board.canCastle("king", colour)))) {
                return states;
            }
        }
        Coordinate[] additionalMoves = new Coordinate[0];
        boolean addEnPassant = false;
        boolean addCastle = false;
        boolean addPromotion = false;

        // UnPromotion
        if (((white && y == FINAL_RANK_Y) || (!white && y == FIRST_RANK_Y)) && !king) {
            additionalMoves = StandardPieceFactory.getInstance().getPiece(white ? "p" : "P").getMoves(origin);
            addPromotion = true;

        // UnPassant
        } else if (((white && y == BLACK_ESCAPE_Y) || (!white && y == WHITE_ESCAPE_Y)) && type.equals("pawn")) {
            int offfset = white ? -1 : 1;
            additionalMoves = new Coordinate[]{new Coordinate(x + 1, y + offfset),
                    new Coordinate(x - 1, y + offfset)};
            addEnPassant = true;

        // UnCastle
        } else if (king) {
            additionalMoves = new Coordinate[]{new Coordinate(x - 2, y), new Coordinate(x + 2, y)};
            addCastle = true;

        // UnDoubleMove
        } else if (type.equals("pawn")) {
            additionalMoves = new Coordinate[]{new Coordinate(x, y +  (white ? -2 : 2))};
        }

        if (additionalMoves.length != 0) {
            states.addAll(tryMoves(board, additionalMoves, origin, currentState,
                    addPromotion, addEnPassant, any, addCastle));
        }

        states.addAll(tryMoves(board, moves, origin, currentState,
                false, false, any, false));
        return states;
    }

    private List<String> tryMoves(ChessBoard board, Coordinate[] moves,
                                  Coordinate origin, String currentState, boolean promotion,
                                  boolean enPassant,
                                  boolean any,
                                  boolean castle) {
        boolean previousEnPassant = !board.getEnPassant().equals(Coordinates.NULL_COORDINATE);
        List<String> states = new LinkedList<>();
        for (Coordinate currentMove : moves) {
            if (previousEnPassant && currentMove.getX() != origin.getX()) {
                    continue;
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
                    Coordinate target = Coordinates.add(origin, new Coordinate(direction.getX() * i,
                            direction.getY() * i));

                    if (makeJustMove(currentBoard, origin, target, piece, promotion, enPassant)){
                        CheckUtil.switchTurns(currentBoard);
                        if (previousEnPassant) {
                            currentBoard.setEnPassant(Coordinates.NULL_COORDINATE);
                        }
                        String move = currentBoard.getReader().toFEN() + ":"
                                + toLAN(currentBoard, origin, target, piece, castle, enPassant);
                        CheckUtil.switchTurns(currentBoard);
                        if (CheckUtil.check(new BoardInterface(currentBoard))
                        && this.fenPredicate.test(move +
//                                (currentState.split(":").length > 1 ?
                                        (":" + currentState.split(":")[2])
//                                        : ":0")
                        )){

//                            CheckUtil.switchTurns(currentBoard);
//                            if (previousEnPassant) {
//                                currentBoard.setEnPassant(Coordinates.NULL_COORDINATE);
//                            }
//                            String boardAndMove = currentBoard.getReader().toFEN() + ":" + toLAN(currentBoard, origin, target, piece, castle, enPassant);
                            states.add(move);
//                            if (!legalFirst && any) {
//                                break;
//                            }
                        }
                    } else {
                        // May break from creating a pawn
                        if (!piece.equals("p") || enPassant) {
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

    /**
     * Checks whether movement is "non-intrusive" - if a non-pawn piece moved without castling or capturing.
     * @param move the description of the move
     * @return whether the move is non-intrusive
     */
    private boolean nonIntrusiveMovement(String move) {
        boolean test = NORMAL_MOVE.test(move);
        if (test) {
            this.count++;
        }
        return test;
    }

    /**
     * Tries to make a move then test the resulting state. Returns false if either the move fails or the resulting
     * position is not legal.
     * @param board the board the move is made on
     * @param origin the starting coordinate of the move
     * @param target the end coordinate of the move
     * @param capturedPiece the piece captured by the move or an empty String
     * @param promotion whether there was a promotion
     * @param enPassant whether there was an en passant
     * @return true if both the move is successful and the resulting board state is legal, false otherwise
     */
    public boolean makeMove(ChessBoard board, Coordinate origin, Coordinate target,
                            String capturedPiece, boolean promotion, boolean enPassant) {
        boolean justMove = makeJustMove(board, origin, target, capturedPiece, promotion, enPassant);
        return justMove && testState(board);
    }

    /**
     * Tests the board state for legality. This involves creating a StateDetector to test the board state,
     * checking whether the current state violates any castling conditions on the board, and any other conditions
     * given at construction of this Solver.
     * @param board the board to be tested
     * @return whether the board state is legal
     */
    public boolean testState(ChessBoard board) {
        testCount++;
        DetectorInterface detector = StateDetectorFactory.getDetectorInterface(board);
        return detector.testState() && castleCheck(board, detector) && this.detectorPredicate.test(detector);
    }

    /**
     * Attempts to make the given un move on the given ChessBoard. Returns true if this is successful.
     * @param board the board the move is made on
     * @param origin the starting coordinate of the move
     * @param target the end coordinate of the move
     * @param capturedPiece the piece captured by the move or an empty String
     * @param promotion whether there was a promotion
     * @param enPassant whether there was an en passant
     * @return true if the move was successful, false otherwise
     */
    private boolean makeJustMove(ChessBoard board, Coordinate origin, Coordinate target,
                                 String capturedPiece, boolean promotion, boolean enPassant) {
        UnMoveMaker moveMaker = new UnMoveMaker(board);
        if (promotion) {
            moveMaker.setPromotionFlag(true);
        }
        if (enPassant) {
            moveMaker.setEnPassantFlag(true);
        }
        if (!capturedPiece.equals("")) {
            moveMaker.setCaptureFlag(true);
            moveMaker.setCapturePiece(StandardPieceFactory.getInstance()
                    .getPiece(board.getTurn().equals("white")
                            ? capturedPiece.toLowerCase()
                            : capturedPiece.toUpperCase()));
        }
        return moveMaker.makeUnMove(origin, target);
    }

    /**
     * Checks whether all the castling parameters set on the given ChessBoard match the castling parameters
     * of the given DetectorInterface
     * @param board the board being checked
     * @param detector the DetectorInterface being checked
     * @return true if the castling parameters match, false otherwise
     */
    private boolean castleCheck(ChessBoard board, DetectorInterface detector) {
        boolean white = true;
        for (int i = 0 ; i < 2 ; i++) {
            String piece = "king";
            for (int j = 0 ; j < 2 ; j++) {
                if (board.canCastle(piece, white ? "white" : "black")) {
                    if (!detector.canCastle(white, piece.equals("queen"))) {
                        return false;
                    }
                }
                piece = "queen";
            }
            white = false;
        }
        return true;
    }

    /**
     * Checks if the Solver needs to continue adding depth as a result of a king currently being in check
     * or an un-passant having just taken place. In both cases, the Solver needs to keep iterating to ensure
     * another legal un move can be made
     * @param board the board being checked
     * @return true if there is a compulsory move to be made, false otherwise
     */
    private boolean compulsoryContinuation(ChessBoard board) {
        return CheckUtil.eitherInCheck(new BoardInterface(board))
                || !board.getReader().toFEN().split(" ")[3].equals("-");
    }

    /**
     * Returns the additional depth of the Solver.
     * @return the additional depth
     */
    public int getAdditionalDepth() {
        return additionalDepth;
    }

    /**
     * Sets the additional depth, the depth to which the Solver will go after it's reached the depth given
     * when solve() is called, searching instead for only one valid board state instead of as many as are set in the
     * number of solutions.
     * @param additionalDepth the additional depth for the Solver to  go to when solving
     */
    public void setAdditionalDepth(int additionalDepth) {
        this.additionalDepth = additionalDepth;
    }

    /**
     * Sets the number of solutions for the Solver to try to find when solving. When this number is reached, it will
     * stop searching for solutions.
     * @param numberOfSolutions the number of solutions the solver will look for when solving
     */
    public void setNumberOfSolutions(int numberOfSolutions) {
        this.numberOfSolutions = numberOfSolutions;
    }
}
