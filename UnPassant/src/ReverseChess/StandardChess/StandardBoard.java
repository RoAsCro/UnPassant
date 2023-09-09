package ReverseChess.StandardChess;

import java.util.Map;
import java.util.TreeMap;

/**
 * Implements ChessBoard.
 * @author Roland Crompton
 */
public class StandardBoard implements ChessBoard {
    /**An array storing the locations of Pieces on the board*/
    private final Piece[][] board = new Piece[LENGTH][LENGTH];
    /**A Map storing the castling rights*/
    private final Map<String, Boolean> castlingRights = new TreeMap<>();
    /**The en passant Coordinate*/
    private Coordinate enPassant = Coordinates.NULL_COORDINATE;
    /**A String saying whose turn it is "white" or "black"*/
    private String turn = "white";

    /**
     * Constructs a copy of the given ChessBoard.
     * @param chessBoard the ChessBoard to copy
     */
    public StandardBoard(ChessBoard chessBoard) {
        for (int x = 0 ; x < LENGTH ; x++) {
            for (int y = 0 ; y < LENGTH ; y++) {
                this.board[x][y] = chessBoard.at(new Coordinate(x, y));
            }
        }
        this.castlingRights.put("blackking", chessBoard.canCastle("king", "black"));
        castlingRights.put("blackqueen", chessBoard.canCastle("queen", "black"));
        castlingRights.put("whiteking", chessBoard.canCastle("king", "white"));
        castlingRights.put("whitequeen", chessBoard.canCastle("queen", "white"));
        enPassant = chessBoard.getEnPassant();
        turn = chessBoard.getTurn();
    }

    /**
     * Constructs an empty ChessBoard, setting all castling rights to false.
     */
    public StandardBoard() {
        castlingRights.put("blackking", false);
        castlingRights.put("blackqueen", false);
        castlingRights.put("whiteking", false);
        castlingRights.put("whitequeen", false);
    }

    /**
     * Returns the Piece at the given Coordinate. If there is no piece at that location,
     * returns an instance of NullPiece.
     * @param coordinate the Coordinate of the Piece
     * @return the Piece at the Coordinate
     */
    @Override
    public Piece at(Coordinate coordinate) {
        int x = coordinate.getX();
        int y = coordinate.getY();

        return x < LENGTH && x >= 0 && y < LENGTH && y >= 0
                ? this.board[coordinate.getX()][coordinate.getY()]
                : NullPiece.getInstance();
    }

    /**
     * Returns the player whose turn it is as a String, "white" or "black"
     * @return the player whose turn it is
     */
    @Override
    public String getTurn() {
        return turn;
    }

    /**
     * Sets the player whose turn it is as a String, "white" or "black"
     * @param turn the player whose turn it is
     */
    @Override
    public void setTurn(String turn) {
        this.turn = turn;
    }

    /**
     * Returns a StandardBoardReader.
     * @return a StandardBoardReader
     */
    @Override
    public BoardReader getReader() {
        return new StandardBoardReader(this);
    }


    /**
     * Places the given Piece at the given Coordinate on the Board.
     * Does nothing if the location is out of bounds
     * @param coordinate the Coordinate the Piece will be placed at
     * @param piece the Piece to place at the Coordinate
     */
    @Override
    public void place(Coordinate coordinate, Piece piece) {
        if (Coordinates.inBounds(coordinate)) {
            this.board[coordinate.getX()][coordinate.getY()] = piece;
        }
    }

    /**
     * Removes the Piece currently at the given location.
     * If there is no Piece at the Coordinate, does nothing.
     * @param coordinate the Coordinate of the Piece to remove
     */
    @Override
    public void remove(Coordinate coordinate) {
        if (Coordinates.inBounds(coordinate)) {
            this.board[coordinate.getX()][coordinate.getY()] = NullPiece.getInstance();
        }
    }

    /**
     * Returns whether the king of the given colour can castle on the given side.
     * The colour should be "white" or "black", and the piece side should be "queen"
     * or "king".
     * <p></p>
     * In reverse play, this represents whether the king MUST castle on the given side.
     * @param pieceSide the castle side, "queen" or "king"
     * @param colour the colour of the king "white" or "black"
     * @return whether the king of the given colour can castle on the given side
     */
    @Override
    public boolean canCastle(String pieceSide, String colour) {
        return this.castlingRights.get(colour.toLowerCase() + pieceSide.toLowerCase());
    }

    /**
     * Sets whether the king of the given colour can castle on the given side.
     * The colour should be "white" or "black", and the piece side should be "queen"
     * or "king".
     * <p></p>
     * In reverse play, this represents whether the king MUST castle on the given side.
     * @param pieceSide the castle side, "queen" or "king"
     * @param colour the colour of the king "white" or "black"
     * @param canCastle whether the side should be allowed to castle
     */
    @Override
    public void setCastle(String pieceSide, String colour, boolean canCastle) {
        this.castlingRights.replace(colour.toLowerCase() + pieceSide.toLowerCase(), canCastle);
    }

    /**
     * In forward play, returns the location to which a pawn may move to capture en passant.
     * In reverse play, returns the location of the pawn which must move double move in reverse.
     * If this isn't set, returns a NullCoordinate.
     * @return the en passant Coordinate
     */
    @Override
    public Coordinate getEnPassant() {
        return this.enPassant;
    }

    /**
     * Sets the en passant Coordinate.
     * @param coordinate the Coordinate to set the en passant Coordinate to
     */
    @Override
    public void setEnPassant(Coordinate coordinate) {
        this.enPassant = coordinate;
    }

}
