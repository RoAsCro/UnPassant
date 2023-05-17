import java.util.Arrays;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public class Board {

    private final static int LENGTH = 8;
    private final Piece nullPiece = new Piece() {
        @Override
        public String getType() {
            return "null";
        }
    };
    private final Piece[][] board = new Piece[LENGTH][LENGTH];
    private final PieceFactory factory = new PieceFactory() {
        @Override
        public Piece getPiece(String type) {
            return new Piece() {
                @Override
                public String getType() {
                    return "rook";
                }
            };
        }
    };


    public Board(String FEN) throws IllegalArgumentException {
        fenDecode(FEN.substring(0, !FEN.contains(" ")
                ? FEN.length()
                : FEN.indexOf(' ')));
    }

    public void fenDecode(String FEN) throws IllegalArgumentException {
        String[] rows = FEN.split("/");
        if (rows.length != LENGTH) {
            throw new IllegalArgumentException("FEN not formatted correctly.");
        }
        for (int y = LENGTH - 1 ; y >= 0 ; y--) {
            String row = rows[y];
            System.out.println(row);
            int finalY = y;
            Stream.iterate(0, i -> i + 1)
                    .limit(row.length() - 1)
                    .forEach(x -> {
                        char current = row.charAt(x);
                        int span = 1;
                        Piece piece = this.nullPiece;
                        System.out.println(current);

                        if (Character.isDigit(current)) {
                            span = current;
                        } else {
                            piece = this.factory.getPiece("" + current);
                        }
                        for (int i = x ; i < x + span ; i++) {
                            this.board[i][finalY] = piece;
                        }

                    });
        }

    }

    public Piece at(int x, int y) {
        return this.board[x][y];
    }



}
