package Heuristics.Deductions;

import StandardChess.Coordinate;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

public class PromotedPieceSet extends PromotedPiece {
    private final List<PromotedPiece> pieces = new LinkedList<>();

    private final String type;

    PromotedPieceSet(String type, Boolean state) {
        super(new Coordinate(0, 0));
        this.type = type;
        this.state = state;
    }

    public void add(PromotedPiece p) {
        if (this.pieces.isEmpty()) {
            this.location = p.location;
        }
        this.pieces.add(p);
    }

    public PromotedPiece get(int n) {
        return this.pieces.get(n);
    }

    public String getType() {
        return this.type;
    }

    public List<PromotedPiece> getPieces() {
        return this.pieces;
    }

    public void remove(Coordinate c) {
        Iterator<PromotedPiece> iterator = pieces.iterator();
        while (iterator.hasNext()) {
            PromotedPiece p = iterator.next();
            if (p.getLocation().equals(c)) {
                iterator.remove();
            }
        }
        if (this.pieces.size() == 1) {
            this.location = this.pieces.get(0).location;
        }
    }

    @Override
    public String toString() {
        return this.pieces.stream()
                .map(PromotedPiece::toString)
                .collect(Collectors.joining(", ", this.type + " [", "]"));
    }
}
