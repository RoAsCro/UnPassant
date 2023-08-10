package StandardChess;


public class BoardBuilder {
    private final static int VALUE_OF_ZERO = 48;
    private final static String INITIAL_POSITION = "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq -";

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
        ChessBoard board = new StandardBoard();
        positionDecode(params[0], board);

        String turn = "white";
        if (paramLength > 1) {
            if (params[1].equals("b")) {
                turn = "black";
            }
        }
        board.setTurn(turn);

        String castleInput;
        if (paramLength > 2) {
            castleInput = params[2];
//        }
//        else {
//            castleInput = castleParse(board);
//        }
        castleDecode(castleInput, board);
        }
        if (paramLength > 3) {
            String enPassant = params[3];
            if (!enPassant.equals("-")) {
                board.setEnPassant(new Coordinate((char) ((int)enPassant.charAt(0) - Coordinates.LOWER_ASCII_A), Integer.parseInt(enPassant.substring(1)) - 1));
            }
        }


        return board;
    }

    private static String castleParse(ChessBoard board) {

        String returnString = "";
        if (castleParseHelper(board.at(Coordinates.WHITE_KING), "king", "white")) {
            if (castleParseHelper(board.at(Coordinates.WHITE_KING_ROOK), "rook", "white")) {
                returnString += "K";
            }
            if (castleParseHelper(board.at(Coordinates.WHITE_QUEEN_ROOK), "rook", "white")) {
                returnString += "Q";
            }
        }
        if (castleParseHelper(board.at(Coordinates.BLACK_KING), "king", "black")) {
            if (castleParseHelper(board.at(Coordinates.BLACK_KING_ROOK), "rook", "black")) {
                returnString += "k";
            }
            if (castleParseHelper(board.at(Coordinates.BLACK_QUEEN_ROOK), "rook", "black")) {
                returnString += "q";
            }
        }
        return returnString;
    }

    private static boolean castleParseHelper(Piece piece, String type, String colour) {
        return piece != null && piece.getType().equals(type) && piece.getColour().equals(colour);
    }

    private static void castleDecode(String input, ChessBoard board) throws IllegalArgumentException {
        if (!input.equals("-")) {
            String king = "king";
            String queen = "queen";
            String white = "white";
            String black = "black";
            char[] inputs = input.toCharArray();
            for (char c : inputs) {
                switch (c) {
                    case 'k' -> board.setCastle(king, black, true);
                    case 'q' -> board.setCastle(queen, black, true);
                    case 'K' -> board.setCastle(king, white, true);
                    case 'Q' -> board.setCastle(queen, white, true);
                    default -> throw new IllegalArgumentException("Bad castling parameters");
                }
            }
        }
    }

    private static void positionDecode(String FEN, ChessBoard board) throws IllegalArgumentException {
        String[] rows = FEN.split("/");
        if (rows.length != ChessBoard.LENGTH) {
            throw new IllegalArgumentException("FEN not formatted correctly.");
        }
        try {
            for (int y = 0 ; y < ChessBoard.LENGTH ; y++) {
                String row = rows[ChessBoard.LENGTH - 1 - y];
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
    //TODO consider including en passant rights
}
