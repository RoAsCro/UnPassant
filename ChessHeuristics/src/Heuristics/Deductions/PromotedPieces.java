package Heuristics.Deductions;

import Heuristics.BoardInterface;
import Heuristics.HeuristicsUtil;
import Heuristics.Observation;
import Heuristics.Observations.PawnNumber;
import Heuristics.Observations.PieceNumber;
import StandardChess.Coordinate;

import java.util.*;
import java.util.stream.Collectors;

public abstract class PromotedPieces extends AbstractDeduction {

    private static final Map<String, Integer> PIECE_NUMBERS = Map.of(
            "rook", 2,
//            "bishop", 2,
            "queen", 1,
            "knight", 2
    );
    List<Observation> observations = new ArrayList<>();

    // The Pieces that MAY have been promoted based on the number of pieces
    Map<Coordinate, PromotedPiece> promotedPieces = new TreeMap<>(Comparator.comparingInt(Coordinate::hashCode));
    private List<PromotedPieceSet> andSet = new LinkedList<>();

    PawnNumber pawnNumber;
    PieceNumber pieceNumber;

    int possiblePromotedPieces = 0;


    public PromotedPieces() {
        PawnNumber pawnNumber = new PawnNumber();
        this.observations.add(pawnNumber);
        this.pawnNumber = pawnNumber;
        PieceNumber pieceNumber = new PieceNumber();
        this.observations.add(pieceNumber);
        this.pieceNumber = pieceNumber;
    }
    @Override
    public List<Observation> getObservations() {
        return this.observations;
    }

    public boolean deduce(BoardInterface board, String colour) {
        this.possiblePromotedPieces = MAX_PAWNS - (colour.equals("white")
                ? pawnNumber.getWhitePawns()
                : pawnNumber.getBlackPawns());

        PIECE_NUMBERS.keySet().stream().forEach(s -> {
            List<Coordinate> currentPieceCoords = board.getBoardFacts().getCoordinates(colour, s);
            int pieceCountDifference = currentPieceCoords.size() - PIECE_NUMBERS.get(s);
            if (pieceCountDifference > 0) {
                for (int i = 0; i < pieceCountDifference; i++) {
                    PromotedPieceSet xorSet = new PromotedPieceSet(s, true);
                    andSet.add(xorSet);
                    currentPieceCoords.stream()
                            .forEach(c -> {
                                xorSet.add(new PromotedPiece(c));
                            });
                }
            }
        });

        List<Coordinate> bishops = board.getBoardFacts().getCoordinates(colour, "bishop");
        int bishopNumber = bishops.size();
        int bishopMax = 2;
        Map<Integer, List<Coordinate>> lightAndDarkSquares =
                bishops.stream().collect(Collectors.groupingBy(c -> (c.getY() + c.getX()) % 2));
        if (bishopNumber >= bishopMax) {
            for (Integer i : lightAndDarkSquares.keySet()) {
                List<Coordinate> currentSet = lightAndDarkSquares.get(i);
                if (currentSet.size() >= 2) {
                    for (int j = 0; j < currentSet.size() - 1; j++) {
                        PromotedPieceSet xorSet = new PromotedPieceSet("bishop", true);
                        andSet.add(xorSet);
                        currentSet.stream()
                                .forEach(c -> {
                                    xorSet.add(new PromotedPiece(c));
                                });
                    }
                }
            }
        }
        int promotedPieceCount = this.andSet.size();
        if (promotedPieceCount > this.possiblePromotedPieces) {
            this.state = false;
            return false;
        }
//        for (int i = 0 ; i < this.possiblePromotedPieces - promotedPieceCount; i++) {
//            PromotedPieceSet pieceSet = new PromotedPieceSet("any", null);
//            this.andSet.add(pieceSet);
//            HeuristicsUtil.PIECE_NAMES.stream()
//                            .forEach(type -> {
//                                board.getBoardFacts().getCoordinates(colour, type).stream()
//                                        .forEach(coordinate -> pieceSet.add(new PromotedPiece(coordinate)));
//                            });
//        }

        return true;
    }


    public void update() {
        updateIter(0);
    }
    private void updateIter(int previousUpdates) {
        int updates = 0;
        for (int i = 0 ; i < this.andSet.size() ; i++) {
            PromotedPieceSet set = this.andSet.get(i);
            if (set.getPieces().size() == 1) {
                updates++;
                Coordinate coordinate = set.getLocation();
                for (int j = 0 ; j < this.andSet.size() ; j++) {
                    if (j != i) {
                        PromotedPieceSet setTwo = this.andSet.get(j);
                        setTwo.remove(coordinate);
                    }
                }
            }
        }
        if (updates != previousUpdates) {
            updateIter(updates);
        }
    }

//    public Map<Coordinate, PromotedPiece> getPromotedPieces() {
//        return this.promotedPieces;
//    }

    public List<PromotedPieceSet> getPromotedPieces() {
        return andSet;
    }


}
