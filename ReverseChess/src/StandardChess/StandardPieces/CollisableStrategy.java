package StandardChess.StandardPieces;

public abstract class CollisableStrategy extends AbstractStrategy {
    public CollisableStrategy(String name) {
        super(name);
    }

    @Override
    public boolean tryMove() {
        //Check collision...
        return false;
    }
}
