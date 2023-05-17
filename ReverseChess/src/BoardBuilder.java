import java.util.stream.Stream;

public class BoardBuilder {

    private final static int LENGTH = 8;
    private final static int VALUE_OF_ZERO = 51;
    private final static Piece NULL_PIECE = new Piece() {
        @Override
        public String getType() {
            return "null";
        }
    };
    private final static PieceFactory factory = new PieceFactory() {
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

    public static Board buildBoard(String FEN) throws IllegalArgumentException {
        Board board = new Board();
        fenDecode(FEN.substring(0, !FEN.contains(" ")
                ? FEN.length()
                : FEN.indexOf(' ')), board);
        return board;
    }


    private static void fenDecode(String FEN, Board board) throws IllegalArgumentException {
        String[] rows = FEN.split("/");
        if (rows.length != LENGTH) {
            throw new IllegalArgumentException("FEN not formatted correctly.");
        }
        for (int y = LENGTH - 1 ; y >= 0 ; y--) {
            String row = rows[y];
            int finalY = y;
            Stream.iterate(0, i -> i + 1)
                    .limit(row.length())
                    .forEach(x -> {
                        char current = row.charAt(x);
                        int span = 1;
                        Piece piece = NULL_PIECE;

                        if (Character.isDigit(current)) {
                            span = current - VALUE_OF_ZERO;
                        } else {
                            piece = factory.getPiece("" + current);
                        }

                        for (int i = x ; i < x + span ; i++) {
                            board.place(new Coordinate(i, finalY), piece);
                        }

                    });
        }

    }

}
