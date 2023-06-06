public class CheckAssertion implements Assertion{
    @Override
    public Boolean check(BoardInterface boardInterface) {

        return boardInterface.inCheck(boardInterface.getTurn());
    }
}
