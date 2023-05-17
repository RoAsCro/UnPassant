package StandardChess.StandardPieces;

public abstract class AbstractStrategy implements PieceStrategy{
    private String name;

    public AbstractStrategy(String name) {
        this.name = name;
    }

    @Override
    public String getName() {
        return this.name;
    }

}
