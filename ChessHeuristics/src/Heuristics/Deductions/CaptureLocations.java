package Heuristics.Deductions;

import Heuristics.BoardInterface;
import Heuristics.Observation;
import Heuristics.Observations.PawnNumber;
import Heuristics.Observations.PieceNumber;
import Heuristics.Path;

import java.util.List;
import java.util.Map;

public class CaptureLocations extends AbstractDeduction {

    PawnNumber pawnNumber;
    PieceNumber pieceNumber;
    PieceMap pieceMap;

    @Override
    public List<Observation> getObservations() {
        return null;
    }

    @Override
    public boolean deduce(BoardInterface board) {
        int missingPawns = (8 - this.pawnNumber.getBlackPawns());
        int missingNonPawns = (16 - this.pieceNumber.getBlackPieces()) - missingPawns;
        Path ofWhichCaged = Path.of(this.pieceMap.getCaged().entrySet().stream()
                .filter(Map.Entry::getValue) //Is Caged
                .filter(entry -> this.pieceMap.getStartLocations().get(entry.getKey()).isEmpty()) //Is missing
                .map(Map.Entry::getKey)
                .toList());
        int ofWhichBishop = (int) ofWhichCaged.stream()
                .filter(coordinate -> coordinate.getX() == 2 || coordinate.getX() == 5) // Is a bishop
                .count();

        // Rooks are the only piece capable of being both caged and captured on the pawn rank
        Path ofWhichRook = Path.of(ofWhichCaged.stream()
                .filter(coordinate -> coordinate.getX() == 0 || coordinate.getX() == 7) // Is a rook
                .toList());

        int ofWhichQueen = ofWhichCaged.size() - ofWhichBishop - ofWhichRook.size();

        //TODO Update pawn maps, removing caged bishops and queen from the equation

        //TODO Update pawn maps, making it so that only pawns on the enemy pawn rank account for caged rooks




        return false;
    }
}
