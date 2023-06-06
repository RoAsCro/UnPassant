import StandardChess.*;

public class BoardInterface {

    private ChessBoard board;
    private Coordinate whiteKing;
    private Coordinate blackKing;

    public BoardInterface(ChessBoard board) {
        this.board = board;
        findKings();
    }

    private void findKings() {
        BoardReader reader = this.board.getReader();
        Piece current = this.board.at(new Coordinate(0, 0));
        reader.to(new Coordinate(0, 0));

        for (int i = 0 ; i < 2 ; i++) {
            if (i == 1 || !this.board.at(reader.getCoord()).getType().equals("king")) {
                reader.nextWhile(Coordinates.RIGHT,
                        c -> !this.board.at(c).getType().equals("king"),
                        c -> {
                            int currentX = reader.getCoord().getX();
                            int currentY = reader.getCoord().getY();
                            if (currentY > 7){
                                throw new RuntimeException("Board is missing a king");
                            }
                            if (currentX > 7){
                                reader.to(new Coordinate(0, currentY + 1));
                            }
                    }
                );
                current = reader.next(Coordinates.RIGHT);

            }

            if (current.getColour().equals("white")){
                whiteKing = reader.getCoord();
            } else{
                blackKing = reader.getCoord();
            }
        }
    }

    public String getTurn() {
        return this.board.getTurn();
    }

    public boolean inCheck(String player) {
        return this.board.getReader().inCheck(player.equals("white") ? this.whiteKing : this.blackKing);
    }

}
