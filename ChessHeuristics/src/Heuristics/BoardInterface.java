package Heuristics;

import StandardChess.*;

import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Predicate;

public class BoardInterface {

    public static final int MAX_PIECE_NUMBER = 16;
    public static final int MAX_PAWN_NUMBER = 8;

    private StandardObserver standardObserver = new StandardObserver();

    private ChessBoard board;
    private Coordinate whiteKing;
    private Coordinate blackKing;

    public BoardInterface(ChessBoard board) {
        this.board = board;
        findKings();
        findPieces();
        correctCastling();
    }

    private void correctCastling() {
        Map<Coordinate,String> rookCoords = Map.of(Coordinates.WHITE_QUEEN_ROOK, "tt",
                Coordinates.WHITE_KING_ROOK, "tf",
                Coordinates.BLACK_QUEEN_ROOK, "ft", Coordinates.BLACK_KING_ROOK, "ff");
        rookCoords.forEach((c, s) -> {
            boolean white = s.charAt(0) == 't';
            boolean queen = s.charAt(1) == 't';
            Piece rook = board.at(c);
            if (canMove(white, queen)) {
                if (!rook.getType().equals("rook") || !rook.getColour().equals(white ? "white" : "black")) {
                    board.setCastle(queen ? "queen" : "king", white ? "white" : "black", false);
                }
            }
        });
    }

    private void iterateOverBoard(Predicate<Coordinate> condition, Consumer<Coordinate> function) {
        BoardReader reader = this.board.getReader();
        reader.to(new Coordinate(0, 0));
        reader.nextWhile(Coordinates.RIGHT,
                c -> new WholeBoardPredicate().test(reader) && condition.test(c),
                p -> function.accept(reader.getCoord()));
    }

    private void findPieces() {
        iterateOverBoard(c -> true, c -> {
            String type = this.board.at(c).getType();
            String colour = this.board.at(c).getColour();
            if (!type.equals("null")) {
                standardObserver.put(colour.equals("white"), type, c);
            }
        });
    }
    private void findKings() {
        iterateOverBoard(c -> true, c -> {
            Piece p = this.board.at(c);
            if (p.getType().equals("king")) {
                if (p.getColour().equals("white")) {
                    this.whiteKing = c;
                } else {
                    this.blackKing = c;
                }
            }
        });
    }

    public String getTurn() {
        return this.board.getTurn();
    }


    public StandardObserver getBoardFacts() {
        return this.standardObserver;
    }

    public BoardReader getReader() {
        return board.getReader();
    }


    public boolean inCheck(String player) {
        Coordinate king = player.equals("white") ? this.whiteKing : this.blackKing;
        if (king == null) {
            findKings();
            if (king == null) {
                return false;
            }
        }
        return this.board.getReader().inCheck(king);
    }

    public boolean canKingMove(boolean white) {
        return canMove(white, true) || canMove(white, false);
    }

    public boolean canMove(boolean white, boolean queenSide) {
        return this.board.canCastle(queenSide ? "queen" : "king", white ? "white" : "black");
    }

//    @Override
//    public int hashCode() {
//        String[] o1FEN = getReader().toFEN().split(" ");
//        String[] o1Board = o1FEN[0].split("/");
//        String criticalRegion = o1Board[0] + o1Board[1] + o1Board[6] + o1Board[7];
//
//        return Objects.hash(criticalRegion, getBoardFacts().getCoordinates(true, "pawn"), getBoardFacts().getCoordinates(false, "pawn"),
//                getBoardFacts().getCoordinates(false, "rook").size(), getBoardFacts().getCoordinates(false, "bishop").size(),
//                getBoardFacts().getCoordinates(false, "knight").size(), getBoardFacts().getCoordinates(false, "queen").size(),
//                getBoardFacts().getCoordinates(true, "rook").size(), getBoardFacts().getCoordinates(true, "bishop").size(),
//                getBoardFacts().getCoordinates(true, "knight").size(), getBoardFacts().getCoordinates(true, "queen").size());
//    }

//    @Override
//    public int equals(BoardInterface o1, BoardInterface o2) {
//        boolean equal = o1.getBoardFacts().getCoordinates(true, "pawn").equals(o2.getBoardFacts().getCoordinates(true, "pawn"))
//                &&
//                o1.getBoardFacts().getCoordinates(false, "pawn").equals(o2.getBoardFacts().getCoordinates(false, "pawn"))
//                &&
//                PIECE_CODES.stream().allMatch(piece ->
//                        o1.getBoardFacts().getCoordinates(false, piece).size() == o2.getBoardFacts().getCoordinates(false, piece).size()
//                                &&
//                                o1.getBoardFacts().getCoordinates(true, piece).size() == o2.getBoardFacts().getCoordinates(true, piece).size());
//        if (equal) {
//            String[] o1FEN = o1.getReader().toFEN().split(" ");
//            String[] o2FEN = o2.getReader().toFEN().split(" ");
//            String[] o1Board = o1FEN[0].split("/");
//            String[] o2Board = o2FEN[0].split("/");
//            String o1CriticalRegion = o1Board[0] + o1Board[1] + o1Board[6] + o1Board[7];
//            String o2CriticalRegion = o2Board[0] + o2Board[1] + o2Board[6] + o2Board[7];
//            equal = o1CriticalRegion.equals(o2CriticalRegion) && o1FEN[2].equals(o2FEN[2]);
//        }
//        int comparison = equal ? 0 : o1.hashCode() - o2.hashCode();
//        return comparison;
//    }

}
