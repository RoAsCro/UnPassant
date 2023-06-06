import StandardChess.*;

import java.util.function.Consumer;
import java.util.function.Predicate;

public class BoardInterface {

    private ChessBoard board;
    private Coordinate whiteKing;
    private Coordinate blackKing;
    private int pieceNumber;
    private int pawnNumber;

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
            if (!type.equals("null")) {
                this.pieceNumber++;
                if (type.equals("pawn")) {
                    this.pawnNumber++;
                }
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

    public boolean inCheck(String player) {
        System.out.println(whiteKing);
        System.out.println(blackKing);
        return this.board.getReader().inCheck(player.equals("white") ? this.whiteKing : this.blackKing);
    }

}
