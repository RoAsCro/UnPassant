package StandardChess;

import java.util.stream.Stream;

public class BoardBuilder {

    private final static int LENGTH = 8;
    private final static int VALUE_OF_ZERO = 48;
//    private final static StandardChess.Piece NULL_PIECE = new StandardChess.Piece() {
//        @Override
//        public String getType() {
//            return "null";
//        }
//    };
    private final static PieceFactory factory = StandardPieceFactory.getInstance();

    public static Board buildBoard(String FEN) throws IllegalArgumentException {
        String[] params = FEN.split(" ");
        int paramLength = params.length;

        if (paramLength < 1) {
            throw new IllegalArgumentException();
        }
        Board board = new Board();
        fenDecode(params[0], board);

        String turn = "white";
        if (paramLength > 1) {
            if (params[1].equals("b")) {
                turn = "black";
            }
        }
        String castleInput;
        if (paramLength > 2) {
            castleInput = params[2];
        } else {
            castleInput = "KQkq";
        }
        castleDecode(castleInput, board);

        return board;
    }

    private static void castleDecode(String input, Board board) throws IllegalArgumentException {
        if (!input.equals("-")) {
            char[] inputs = input.toCharArray();
            for (char c : inputs) {
                switch (c) {
                    case 'K' -> board.setCastleBlackKing(true);
                    case 'Q' -> board.setCastleBlackQueen(true);
                    case 'k' -> board.setCastleWhiteKing(true);
                    case 'q' -> board.setCastleWhiteQueen(true);
                    default -> throw new IllegalArgumentException("Bad castling parameters");
                }
            }
        }
    }

    private static void fenDecode(String FEN, Board board) throws IllegalArgumentException {
        String[] rows = FEN.split("/");
        if (rows.length != LENGTH) {
            throw new IllegalArgumentException("FEN not formatted correctly.");
        }
        try {
            for (int y = 0 ; y < LENGTH ; y++) {
                String row = rows[LENGTH - 1 - y];
                char[] characters = row.toCharArray();
                int x = 0;
                for (int c = 0; c < characters.length ; c++) {
                    char current = row.charAt(c);
                    
                    int span = 1;
                    Piece piece = null;

                    if (Character.isDigit(current)) {
                        span = ((int) current) - VALUE_OF_ZERO;
                    } else {
                        System.out.println("Here");
                        piece = factory.getPiece("" + current);
                    }

                    for (int i = x ; i < x + span ; i++) {
                        board.place(new Coordinate(i, y), piece);
                    }
                    x += span;

                }
            }
        } catch (IndexOutOfBoundsException e) {
            throw new IllegalArgumentException("FEN not formatted correctly.");
        }

    }

}
