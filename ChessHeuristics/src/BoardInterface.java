import StandardChess.BoardReader;
import StandardChess.ChessBoard;
import StandardChess.Coordinate;
import StandardChess.Coordinates;

public class BoardInterface {

    private ChessBoard board;
    private Coordinate whiteKing;
    private Coordinate blackKing;

    public BoardInterface(ChessBoard board) {
        this.board = board;
        BoardReader reader = this.board.getReader();
        reader.to(new Coordinate(0, 0));
        reader.nextWhile(Coordinates.RIGHT, c -> !this.board.at(c).getType().equals("king"));
        // find kings...
    }

    public String getTurn() {
        return this.board.getTurn();
    }

    public boolean inCheck(String player) {
        return this.board.getReader().inCheck(player.equals("w") ? this.whiteKing : this.blackKing);
    }

}
