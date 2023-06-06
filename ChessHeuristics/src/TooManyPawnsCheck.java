public class TooManyPawnsCheck implements Check{
    private static final int MAX_PAWN_NUMBER = 8;

    @Override
    public Boolean check(BoardInterface boardInterface) {
        return boardInterface.getBlackPawnNumber() <= MAX_PAWN_NUMBER &&
                boardInterface.getWhitePawnNumber() <= MAX_PAWN_NUMBER;
    }
}
