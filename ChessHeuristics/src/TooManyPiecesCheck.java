public class TooManyPiecesCheck implements Check{
    private static final int MAX_PIECE_NUMBER = 16;
    @Override
    public Boolean check(BoardInterface boardInterface) {
        return boardInterface.getBlackPieceNumber() <= MAX_PIECE_NUMBER
                && boardInterface.getWhitePieceNumber() <= MAX_PIECE_NUMBER;
    }
}
