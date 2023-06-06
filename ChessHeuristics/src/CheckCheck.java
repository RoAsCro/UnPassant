public class CheckCheck implements Check {
    @Override
    public Boolean check(BoardInterface boardInterface) {

        return boardInterface.inCheck(boardInterface.getTurn());
    }
}
