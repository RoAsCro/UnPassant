import Heuristics.BoardInterface;
import Heuristics.Path;
import StandardChess.*;

import java.util.LinkedList;
import java.util.List;

public class Solver {

    String originalBoard;
    LinkedList<String> fens;
    UnMoveMaker mover;
    boolean turnIsWhite;
    private final static List<String> PIECES = List.of("", "p", "r", "b", "n", "q");

    public void solve(ChessBoard board, int depth) {
        this.originalBoard = board.getReader().toFEN();

        LinkedList<String> states = new LinkedList<>();
        LinkedList<String> finalStates = new LinkedList<>();
        LinkedList<Integer> stateSizes = new LinkedList<>();
        stateSizes.push(1);
        int currentDepth = 0;
//        LinkedList<List<Coordinate>> statePieces
        states.add(this.originalBoard);
        while (!states.isEmpty()) {
            stateSizes.push(stateSizes.pop() - 1);
            String currentState = states.pop();
            if (currentDepth != depth) {
                ChessBoard currentBoard = BoardBuilder.buildBoard(currentState);
                List<Coordinate> pieces = allPieces(currentBoard);
                stateSizes.push(0);
                for (Coordinate piece : pieces) {
                    List<String> newStates = iterateThroughMoves(currentBoard, piece);
                    stateSizes.push(stateSizes.pop() + newStates.size());
                    newStates.forEach(states::push);

                }
                currentDepth++;
            } else {
//                for(int i = 0 ; i < stateSizes.pop() ; i++) {
                    finalStates.add(currentState);
//                }
            }
            if (stateSizes.getFirst() < 1) {
                stateSizes.pop();
                currentDepth--;
            }
        }
        System.out.println(finalStates);

    }

    private List<Coordinate> allPieces(ChessBoard board) {
        return new BoardInterface(board).getBoardFacts().getAllCoordinates(board.getTurn())
                .values().stream().flatMap(Path::stream).toList();
    }

    private List<String> iterateThroughMoves(ChessBoard board, Coordinate origin) {
        List<String> states = new LinkedList<>();
        Coordinate[] moves = board.at(origin).getMoves(origin);
        for (Coordinate currentMove : moves) {
            Coordinate direction = Coordinates.add(currentMove, new Coordinate(-origin.getX(), -origin.getY()));
            for (int i = 1 ; ; i++) {
                boolean continueFlag = true;
                for (String piece : PIECES) {
                    ChessBoard currentBoard = BoardBuilder.buildBoard(board.getReader().toFEN());
                    if (makeJustMove(currentBoard,
                            origin,
                            Coordinates.add(origin, new Coordinate(direction.getX() * i, direction.getY() * i)),
                            piece)){
                        if (testState(currentBoard)) {
                            currentBoard.setTurn(currentBoard.getTurn().equals("white") ? "black" : "white");
                            states.add(currentBoard.getReader().toFEN());
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

    public boolean makeMove(ChessBoard board, Coordinate origin, Coordinate target, String piece) {
        return makeJustMove(board, origin, target, piece) && testState(board);
    }

    private boolean testState(ChessBoard board) {
        return StateDetectorFactory.getDetector(board).testState();
    }

    private boolean makeJustMove(ChessBoard board, Coordinate origin, Coordinate target, String piece) {
        UnMoveMaker moveMaker = new UnMoveMaker(board);
        if (!piece.equals("")) {
            moveMaker.setCaptureFlag(true);
            moveMaker.setCapturePiece(StandardPieceFactory.getInstance()
                    .getPiece(board.getTurn().equals("white") ? piece.toLowerCase() : piece.toUpperCase()));
        }
        return moveMaker.makeUnMove(origin, target);
    }

}
