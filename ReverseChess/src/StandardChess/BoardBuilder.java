package StandardChess;


public class BoardBuilder {

    private final static int LENGTH = 8;
    private final static int VALUE_OF_ZERO = 48;
    private final static String INITIAL_POSITION = "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1";

    private final static PieceFactory factory = StandardPieceFactory.getInstance();

    public static ChessBoard buildBoard() {
        return buildBoard(INITIAL_POSITION);
    }

    public static ChessBoard buildBoard(String FEN) throws IllegalArgumentException {
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
            castleInput = castleParse(board);
        }
        castleDecode(castleInput, board);

        return board;
    }

    private static String castleParse(Board board) {

        String returnString = "";
        if (castleParseHelper(board.at(new Coordinate(4, 0)), "king", "white")) {
            if (castleParseHelper(board.at(new Coordinate(7, 0)), "rook", "white")) {
                returnString += "K";
            }
            if (castleParseHelper(board.at(new Coordinate(0, 0)), "rook", "white")) {
                returnString += "Q";
            }
        }
        if (castleParseHelper(board.at(new Coordinate(4, 7)), "king", "black")) {
            if (castleParseHelper(board.at(new Coordinate(7, 7)), "rook", "black")) {
                returnString += "k";
            }
            if (castleParseHelper(board.at(new Coordinate(0, 7)), "rook", "black")) {
                returnString += "q";
            }
        }
//        System.out.println(returnString);
        return returnString;
    }

    private static boolean castleParseHelper(Piece piece, String type, String colour) {
        return piece != null && piece.getType().equals(type) && piece.getColour().equals(colour);
    }

    private static void castleDecode(String input, Board board) throws IllegalArgumentException {
        if (!input.equals("-")) {
            char[] inputs = input.toCharArray();
            for (char c : inputs) {
                switch (c) {
                    case 'k' -> board.setCastleBlackKing(true);
                    case 'q' -> board.setCastleBlackQueen(true);
                    case 'K' -> board.setCastleWhiteKing(true);
                    case 'Q' -> board.setCastleWhiteQueen(true);
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
                    Piece piece = NullPiece.getInstance();

                    if (Character.isDigit(current)) {
                        span = ((int) current) - VALUE_OF_ZERO;
                    } else {
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
