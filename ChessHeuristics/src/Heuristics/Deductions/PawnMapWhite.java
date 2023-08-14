package Heuristics.Deductions;

public class PawnMapWhite extends PawnMap{
    public PawnMapWhite() {
        super(true);
    }

    @Override
    public void update() {
        super.update(true);
    }
}
