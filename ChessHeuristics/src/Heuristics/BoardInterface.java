package Heuristics;

import StandardChess.*;

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
                standardObserver.put(colour, type, c);
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


    public boolean inCheck(String player) {
        return this.board.getReader().inCheck(player.equals("white") ? this.whiteKing : this.blackKing);
    }

}
