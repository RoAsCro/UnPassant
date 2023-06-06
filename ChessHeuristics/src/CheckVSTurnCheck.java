public class CheckVSTurnCheck implements Check {
    @Override
    public Boolean check(BoardInterface boardInterface) {
        return boardInterface.inCheck(boardInterface.getTurn());
    }
}
