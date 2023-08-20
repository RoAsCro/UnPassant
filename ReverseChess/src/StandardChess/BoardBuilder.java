package StandardChess;

/**
 * A builder for ChessBoards, providing static methods that take a FEN and ouput a ChessBoard.
 * @author Roland Crompton
 */
public class BoardBuilder {
    /**The ASCII value of 0*/
    private final static int VALUE_OF_ZERO = 48;
    /**Stores an instance of a PieceFactory*/
    private final static PieceFactory factory = StandardPieceFactory.getInstance();
    /**The FEN for the normal starting position of a chess board*/
    private final static String INITIAL_POSITION = "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq -";


    /**
     * Builds a ChessBoard in the starting position.
     * @return a ChessBoard in the starting position
     */
    public static ChessBoard buildBoard() {
        return buildBoard(INITIAL_POSITION);
    }

    /**
     * Returns a ChessBoard that's a copy of the given ChessBoard.
     * @param copiedBoard the board to  be copied
     * @return a ChessBoard that's a copy of the given board
     */
    public static ChessBoard buildBoard(ChessBoard copiedBoard) {
        return new StandardBoard(copiedBoard);
    }

    /**
     * Builds a board with the parameters of the given FEN.
     * @param FEN the FEN
     * @return a ChessBoard with the parameters of the FEN
     * @throws IllegalArgumentException if the FEN is improperly formatted
     */
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

        if (paramLength > 2) {
            castleDecode(params[2], board);
        }
        if (paramLength > 3) {
            String enPassant = params[3];
            if (!enPassant.equals("-")) {
                board.setEnPassant(new Coordinate((char) ((int)enPassant.charAt(0) - Coordinates.LOWER_ASCII_A), Integer.parseInt(enPassant.substring(1)) - 1));
            }
        }
        return board;
    }

    /**
     * A helper method that sets the ChessBoard's castling parameters according to the input.
     * @param input the castling parameters
     * @param board the ChessBoard whose parameters are being set
     * @throws IllegalArgumentException if the castling parameters are improperly formatted
     */
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

    /**
     * A helper method that reads the position given by the FEN and sets up the ChessBoard.
     * @param FEN the section of the FEN that details the arrangement of pieces on the board
     * @param board the ChessBoard being populated with pieces
     * @throws IllegalArgumentException if the FEN is not properly formatted
     */
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
}
