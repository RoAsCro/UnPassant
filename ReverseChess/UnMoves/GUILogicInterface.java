import StandardChess.Coordinate;

public class GUILogicInterface extends Game {
    public GUILogicInterface(String fen) {
        super(fen);

    }

    @Override
    public void setFEN(String fen) {
        super.setFEN(fen);
        GUI.update();
    }

    @Override
    public boolean makeMove(Coordinate origin, Coordinate target) {
        boolean flag = super.makeMove(origin, target);
        GUI.update();
        return flag;
    }

}
