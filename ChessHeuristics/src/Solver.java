import Heuristics.BoardInterface;
import Heuristics.Deductions.CombinedPawnMap;
import Heuristics.Path;
import StandardChess.*;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class Solver {

    String originalBoard;
    LinkedList<String> fens;
    UnMoveMaker mover;
    boolean turnIsWhite;
    private int additionalDepth = 2;
    private final static List<String> PIECES = List.of("", "p", "r", "b", "n", "q");

    private int count = 0;

    public void solve(ChessBoard board, int depth) {
        this.originalBoard = board.getReader().toFEN();
        System.out.println(iterate(this.originalBoard, depth, false));

    }

    private List<String> iterate(String startingFen, int depth, boolean any) {
        Map<String, CombinedPawnMap>


        LinkedList<String> states = new LinkedList<>();
        LinkedList<String> finalStates = new LinkedList<>();
        LinkedList<Integer> stateSizes = new LinkedList<>();
        stateSizes.push(1);
        int currentDepth = 0;
//        LinkedList<List<Coordinate>> statePieces
        states.add(startingFen + ": ");
        while (!states.isEmpty()) {
            stateSizes.push(stateSizes.pop() - 1);
            String[] stateDescription = states.pop().split(":");
            String currentState = stateDescription[0];
            if (currentDepth != depth) {
                ChessBoard currentBoard = BoardBuilder.buildBoard(currentState);
                List<Coordinate> pieces = allPieces(currentBoard);
                stateSizes.push(0);
                for (Coordinate piece : pieces) {
                    List<String> newStates = iterateThroughMoves(currentBoard, piece);
                    stateSizes.push(stateSizes.pop() + newStates.size());
                    newStates.forEach(s ->
                            states.push(s.split(":")[0]
                                    + ":"
                                    + s.split(":")[1]
                                    + ", "
                                    + stateDescription[1]));

                }
                currentDepth++;
            } else {
                if (any) {
                    finalStates.add(currentState);
                    return finalStates;
                } else if (!iterate(currentState, this.additionalDepth, true).isEmpty()) {
                    finalStates.add(currentState + ":" + stateDescription[1]);
                }
            }
            if (stateSizes.getFirst() < 1) {
                stateSizes.pop();
                currentDepth--;
            }
        }
        return finalStates;
    }

    private String toLAN(ChessBoard board, Coordinate origin, Coordinate target, String piece) {
        return
                board.at(origin).getType().toUpperCase().charAt(board.at(origin).getType().equals("knight") ? 1 : 0)+
                Coordinates.readableString(target)
                + (piece.equals("") ? "-" : "x")
                + piece.toUpperCase()
                + Coordinates.readableString(origin);
    }

    private List<Coordinate> allPieces(ChessBoard board) {
        return new BoardInterface(board).getBoardFacts().getAllCoordinates(board.getTurn())
                .values().stream().flatMap(Path::stream).toList();
    }

    private List<String> iterateThroughMoves(ChessBoard board, Coordinate origin) {
        // If piece is on final rank, allow it to be a pawn.
        List<String> states = new LinkedList<>();

        Coordinate[] moves = board.at(origin).getMoves(origin);
        boolean white = board.getTurn().equals("white");
        if ((white && origin.getY() == 7) || (!white && origin.getY() == 0) && !board.at(origin).getType().equals("king")) {
            Coordinate[] additionalMoves = StandardPieceFactory.getInstance().getPiece(white ? "p" : "P").getMoves(origin);
            states.addAll(iterateThroughMovesHelper(board, additionalMoves, origin, true));
//            List<Coordinate> tempMoves = new LinkedList<>(Arrays.stream(moves).toList());
//            tempMoves.addAll((Arrays.stream(additionalMoves).toList()));
//            moves = tempMoves.toArray(moves);
        }
        states.addAll(iterateThroughMovesHelper(board, moves, origin, false));

        return states;
    }

    private List<String> iterateThroughMovesHelper(ChessBoard board, Coordinate[] moves, Coordinate origin, boolean promotion) {
        List<String> states = new LinkedList<>();
        boolean white = board.getTurn().equals("white");
        for (Coordinate currentMove : moves) {
            Coordinate direction = Coordinates.add(currentMove, new Coordinate(-origin.getX(), -origin.getY()));
            for (int i = 1 ; ; i++) {
                boolean continueFlag = true;
                for (String piece : PIECES) {
                    ChessBoard currentBoard = BoardBuilder.buildBoard(board.getReader().toFEN());
                    Coordinate target = Coordinates.add(origin, new Coordinate(direction.getX() * i, direction.getY() * i));
                    this.count++;
                    System.out.println(this.count);
                    if (makeJustMove(currentBoard,
                            origin,
                            target,
                            piece,
                            promotion)){
                        if (testState(currentBoard)) {
                            currentBoard.setTurn(white ? "black" : "white");
                            states.add(currentBoard.getReader().toFEN() + ":" + toLAN(board, origin, target, piece));
                        }
                    } else {
                        continueFlag = false;
                    }
                }
                if (!continueFlag) {
                    break;
                }
            }
        }
        return states;
    }

    public boolean makeMove(ChessBoard board, Coordinate origin, Coordinate target, String piece, boolean promotion) {
        boolean justMove = makeJustMove(board, origin, target, piece, promotion);
        return justMove && testState(board);
    }

    private boolean testState(ChessBoard board) {
//        System.out.println(board.getReader().toFEN());
        return StateDetectorFactory.getDetector(board).testState();
    }

    private boolean makeJustMove(ChessBoard board, Coordinate origin, Coordinate target, String piece, boolean promotion) {
        UnMoveMaker moveMaker = new UnMoveMaker(board);
        if (promotion) {
            moveMaker.setPromotionFlag(true);
        }
        if (!piece.equals("")) {
            moveMaker.setCaptureFlag(true);
            moveMaker.setCapturePiece(StandardPieceFactory.getInstance()
                    .getPiece(board.getTurn().equals("white") ? piece.toLowerCase() : piece.toUpperCase()));
        }
        return moveMaker.makeUnMove(origin, target);
    }

    public int getAdditionalDepth() {
        return additionalDepth;
    }

    public void setAdditionalDepth(int additionalDepth) {
        this.additionalDepth = additionalDepth;
    }
}
