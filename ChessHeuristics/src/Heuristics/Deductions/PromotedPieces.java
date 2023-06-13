package Heuristics.Deductions;

import Heuristics.BoardInterface;
import Heuristics.Deduction;
import Heuristics.Observation;
import Heuristics.Observations.AbstractObservation;
import Heuristics.Observations.PawnNumber;
import Heuristics.Observations.PieceNumber;
import StandardChess.Coordinate;

import java.util.*;
import java.util.stream.Collectors;

public class PromotedPieces extends AbstractDedcution {

    private static final Map<String, Integer> PIECE_NUMBERS = Map.of(
            "rook", 2,
//            "bishop", 2,
            "queen", 1,
            "knight", 2
    );
    List<Observation> observations = new ArrayList<Observation>();
    Map<Coordinate, PromotedPiece> promotedPieces = new TreeMap<>(new Comparator<Coordinate>() {
        @Override
        public int compare(Coordinate o1, Coordinate o2) {
            return o1.hashCode() - o2.hashCode();
        }
    });

    PawnNumber pawnNumber;
    PieceNumber pieceNumber;

    int possibleWhitePromotedPieces = 0;
    int possibleBlackPromotedPieces = 0;
    int whitePromotedPieceCount = 0;
    int blackPromotedPieceCount = 0;



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

    @Override
    public boolean deduce(BoardInterface board) {
        this.possibleWhitePromotedPieces = MAX_PAWNS - pawnNumber.getWhitePawns();
        this.possibleBlackPromotedPieces = MAX_PAWNS - pawnNumber.getBlackPawns();

        String colour = "white";

        PIECE_NUMBERS.keySet().stream().forEach(s -> {
            List<Coordinate> currentPieceCoords = board.getBoardFacts().getCoordinates(colour, s);
            int pieceCountDifference = currentPieceCoords.size() - PIECE_NUMBERS.get(s);
            if (pieceCountDifference > 0){
                PromotedPiece piece = new PromotedPiece(currentPieceCoords.get(0));
                this.promotedPieces.put(piece.location, piece);
                currentPieceCoords.stream().forEach(c -> {
                    piece.addOr(new PromotedPiece(c));
                });
            }
            this.whitePromotedPieceCount += pieceCountDifference;

        });
        if (this.whitePromotedPieceCount > this.possibleWhitePromotedPieces) {
            return false;
        }

        List<Coordinate> bishops = board.getBoardFacts().getCoordinates(colour, "bishop");
        int bishopNumber = bishops.size();
        int bishopMax = 2;
        Map<Integer, List<Coordinate>> lightAndDarkSquares =
                bishops.stream().collect(Collectors.groupingBy(c -> (c.getY() + c.getX()) % 2));
        if (bishopNumber >= bishopMax) {
            for (Integer i : lightAndDarkSquares.keySet()) {
                List<Coordinate> currentSet = lightAndDarkSquares.get(i);
                if (currentSet.size() >= 2) {
                    this.whitePromotedPieceCount++;
                    Coordinate location = currentSet.get(0);
                    PromotedPiece piece = new PromotedPiece(location);
                    this.promotedPieces.put(location, piece);
                    currentSet.stream().forEach(c -> {
                                piece.addOr(new PromotedPiece(c));
                            });
//                    piece.addOr(new PromotedPiece(lightAndDarkSquares.get(0).get(1)));
                }
            }
        }



        return false;
    }

    public Map<Coordinate, PromotedPiece> getPromotedPieces() {
        return this.promotedPieces;
    }

    public class PromotedPiece extends AbstractDedcution {

        Coordinate location;

        public PromotedPiece(Coordinate location) {
            this.location = location;
        }

        @Override
        public List<Observation> getObservations() {
            return null;
        }

        @Override
        public boolean deduce(BoardInterface board) {
            return false;
        }

        public Coordinate getLocation() {
            return this.location;
        }

    }

}
