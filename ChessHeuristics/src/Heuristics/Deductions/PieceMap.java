package Heuristics.Deductions;

import Heuristics.BoardInterface;
import Heuristics.Observation;
import Heuristics.Path;
import Heuristics.Pathfinder;
import StandardChess.Coordinate;
import StandardChess.StandardPieceFactory;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.function.Predicate;

public class PieceMap extends AbstractDeduction{
    private CombinedPawnMap pawnMap;

//    private PieceNumber = new PieceNumber

    private final Map<Coordinate, List<Path>> startLocations = new TreeMap<>();

    private final static Map<Integer, String> STANDARD_STARTS = Map.of(
            0, "rook", 1, "knight", 2, "bishop", 3, "queen", 4,
            "king", 5, "bishop", 6, "knight", 7, "rook"
    );

    private final Map<String, Predicate<Path>> pathConditions = Map.of(
            //
            "bishop", path ->
                    !(path.getLast().getY() == 1 && pawnMap.getWhitePaths().containsKey(path.getLast()))
                    && !(
                            path.getLast().getY() == 2
                            && pawnMap.getWhitePaths().containsKey(path.getLast())
                            && this.pawnMap.getSinglePath("white", path.getLast()) != null
                            && Pathfinder.pathsExclusive(this.pawnMap.getSinglePath("white", path.getLast()), Path.of(path, new Coordinate(-1, -1)))
                    )
    );

    public PieceMap(CombinedPawnMap pawnMap) {
        this.pawnMap = pawnMap;
    }


    public Map<Coordinate, List<Path>> getStartLocations() {
        return this.startLocations;
    }
    @Override
    public List<Observation> getObservations() {
        return null;
    }

    @Override
    public boolean deduce(BoardInterface board) {
        pawnMap.deduce(board);
//
        findFromOrigin(board, 2, true);
        findFromOrigin(board, 5, true);

        String colour = "white";
        for (int x = 0; x < 8 ; x++) {

        }


        return false;
    }
    private void findFromOrigin(BoardInterface board, int originX, boolean white) {

        Coordinate start = new Coordinate(originX, white ? 0 : 7);
        String pieceName = STANDARD_STARTS.get(originX);
        int escapeLocation = white ? 2 : 5;
        String pieceCode = (pieceName.equals("knight") ? "n" : pieceName.substring(0, 1));
        if (white){
            pieceCode = pieceCode.toUpperCase();
        }
        //
        List<Coordinate> candidatePieces = board.getBoardFacts().getCoordinates(white ? "white" : "black", pieceName);
        System.out.println(candidatePieces);
        List<Path> candidatePaths = new LinkedList<>();
        for (Coordinate target : candidatePieces) {
            if (pieceName.equals("bishop") && (start.getX() + start.getY()) % 2 != (target.getX() + target.getY()) % 2) {
                continue;
            }
            System.out.println(start + " - " + target);
            candidatePaths.add(
                    Pathfinder.findFirstPath(
                            StandardPieceFactory.getInstance().getPiece(pieceCode),
                            start,
                            (b, c) -> c.equals(target) || c.getY() == escapeLocation,
                            board,
                            this.pathConditions.get(pieceName)
                    )
            );
        }
        this.startLocations.put(start, candidatePaths);
    }

}
