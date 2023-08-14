package Heuristics.Deductions;

import Heuristics.BoardInterface;
import Heuristics.Deduction;
import Heuristics.Observations.PawnNumber;
import Heuristics.Observations.PieceNumber;
import Heuristics.Path;
import Heuristics.StateDetector;
import StandardChess.Coordinate;

import java.util.*;
import java.util.function.Function;

public class TestImpossibleStateDetector implements StateDetector {

    private static final int MAX_PAWNS = 8;
    private static final int MAX_PIECES = 16;

    private static final int WHITE = 0;
    private static final int BLACK = 1;


    private final PawnNumber pawnNumber;
    private final PieceNumber pieceNumber;

    private List<Deduction> deductions;
    private List<Deduction> finishedDeductions = new LinkedList();

    //PawnMap stuff
    private int[] piecesTakableByPawns = new int[]{MAX_PIECES, MAX_PIECES};
    private final Map<Coordinate, Integer>[] captureSet = new Map[]{new TreeMap<Coordinate, Path>(), new TreeMap<Coordinate, Path>()};
    private Map<Coordinate, Path>[] pawnOrigins = new Map[]{new TreeMap<Coordinate, Path>(), new TreeMap<Coordinate, Path>()};
    private Map<Coordinate, Boolean>[] originFree = new Map[]{new TreeMap<Coordinate, Boolean>(), new TreeMap<Coordinate, Boolean>()};
    private int[] capturedPieces = new int[]{0, 0};

    //CombinedPawnMapStuff
    private Map<Coordinate, List<Path>>[] pawnPaths = new Map[]{new TreeMap<Coordinate, List<Path>>(), new TreeMap<Coordinate, List<Path>>()};
    private final Map<Coordinate, Path>[] singlePawnPaths = new Map[]{new TreeMap<Coordinate, Path>(), new TreeMap<Coordinate, Path>()};
    public static final Function<Path, Integer> PATH_DEVIATION = p -> p.stream()
            .reduce(new Coordinate(p.get(0).getX(), 0), (c, d) -> {
                if (c.getX() != d.getX()) {
                    return new Coordinate(d.getX(), c.getY() + 1);
                }
                return c;
            }).getY();

    //PieceMap stuff
    private boolean[][] kingRookMovement = new boolean[][]{{false, false, false}, {false, false, false}};
    private boolean blackKingMoved = false;
    private final Map<Coordinate, Map<Coordinate, Path>> startLocations = new TreeMap<>();
    private final Map<Coordinate, Boolean> caged = new TreeMap<>();
    private final Map<String, Map<Path, Integer>> promotionNumbers = new TreeMap<>();
    private final Map<Coordinate, Path> promotedPieceMap = new TreeMap<>();

    //CaptureLocation Stuff
    private Path[] cagedCaptures = new Path[]{new Path(), new Path()};
    private int[] pawnsCapturedByPawns = new int[]{0, 0};
    private final Path[] promotedPawns = new Path[]{new Path(), new Path()};


    public TestImpossibleStateDetector(PawnNumber pawnNumber, PieceNumber pieceNumber, Deduction ... deductions) {
        this.pawnNumber = pawnNumber;
        this.pieceNumber = pieceNumber;
        this.deductions = Arrays.stream(deductions).toList();
        this.deductions.forEach(d -> d.registerDetector(this));
        for (int i = 0; i < 8 ; i++ ) {
            originFree[WHITE].put(new Coordinate(i, 1), true);
            originFree[BLACK].put(new Coordinate(i, 6), true);
        }
    }

    @Override
    public boolean testState(BoardInterface board) {

        this.pieceNumber.observe(board);
        if (board.inCheck(board.getTurn().equals("white") ? "black" : "white")) {
//            System.out.println("Eh1");

            return false;
        }
        if (this.pieceNumber.getBlackPieces() > MAX_PIECES || this.pieceNumber.getWhitePieces() > MAX_PIECES) {
//            System.out.println("Eh2");

            return false;
        }

        this.pawnNumber.observe(board);
        if (this.pawnNumber.getBlackPawns() > MAX_PAWNS || this.pawnNumber.getWhitePawns() > MAX_PAWNS) {
//            System.out.println("Eh3");

            return false;
        }

        for (Deduction deduction : this.deductions) {
            System.out.println("X");


            deduction.deduce(board);
            if (!this.deductions.stream().allMatch(Deduction::getState)) {
                System.out.println(deduction);
                System.out.println("f");
                return false;
            }
            this.finishedDeductions.add(deduction);
        }
        return true;
    }



    public List<Deduction> getDeductions() {
        return this.deductions;
    }


    @Override
    public int getPawnNumbers(boolean white) {
        return white ? this.pawnNumber.getWhitePawns() : this.pawnNumber.getBlackPawns();
    }
    @Override
    public PieceNumber getPieceNumber() {
        return this.pieceNumber;
    }

    // Pawn Map Stuff
    @Override
    public int pawnTakeablePieces(boolean white) {
        return this.piecesTakableByPawns[white ? WHITE : BLACK];
    }

    @Override
    public void reducePawnTakeablePieces(boolean white, int subtrahend) {
        this.piecesTakableByPawns[white ? WHITE : BLACK] -= subtrahend;
    }

    @Override
    public Map<Coordinate, Integer> getCaptureSet(boolean white) {
        return this.captureSet[white ? WHITE : BLACK];
    }

    @Override
    public Map<Coordinate, Path> getPawnOrigins(boolean white) {
        return pawnOrigins[white ? WHITE : BLACK];
    }
    @Override
    public Map<Coordinate, Boolean> getOriginFree(boolean white) {
        return this.originFree[white ? WHITE : BLACK];
    }

    @Override
    public int getCapturedPieces(boolean white) {
        return capturedPieces[white ? WHITE : BLACK];
    }

    @Override
    public void setCapturedPieces(boolean white, int capturedPieces) {
        this.capturedPieces[white ? WHITE : BLACK] = capturedPieces;
    }
    @Override
    public int getMaxCaptures(boolean white, Coordinate coordinate) {
        return getCapturedPieces(white) + getCaptureSet(white).get(coordinate);
    }


    //CombinedPawnMap stuff
    @Override
    public Map<Coordinate, List<Path>> getPawnPaths(boolean white) {
        return pawnPaths[white ? WHITE : BLACK];
    }

    @Override
    public Map<Coordinate, Path> getSinglePawnPaths(boolean white) {
        return this.singlePawnPaths[white ? WHITE : BLACK];
    }

    @Override
    public int minimumPawnCaptures(boolean white) {
        Map<Coordinate, List<Path>> player = getPawnPaths(white);
        Path claimed = new Path();
        int[] size = new int[]{0};
        // With the claimed clause this will not work 100% of the time
        player.values().stream().forEach(paths -> {
                    size[0] = size[0] + paths.stream()
                            .filter(p -> !claimed.contains(p.getFirst()))
                            .reduce((integer, integer2) -> PATH_DEVIATION.apply(integer) < PATH_DEVIATION.apply(integer2) ? integer : integer2)
                            .map(p -> {
                                claimed.add(p.getFirst());
                                return PATH_DEVIATION.apply(p);
                            })
                            .orElse(0);
                }
        );
        return size[0];
    }


    @Override
    public void update() {
        this.finishedDeductions.forEach(Deduction::update);
    }

    @Override
    public void reTest(BoardInterface boardInterface) {
        System.out.println(finishedDeductions.size());
        this.finishedDeductions.forEach(d -> d.deduce(boardInterface));
    }


    @Override
    public boolean getKingMovement(boolean white) {
        return kingRookMovement[white ? WHITE : BLACK][0];
    }
    @Override
    public void setKingMovement(boolean white, boolean moved) {
        this.kingRookMovement[white ? WHITE : BLACK][0] = moved;
    }
    @Override
    public boolean getRookMovement(boolean white, boolean queen) {
        return kingRookMovement[white ? WHITE : BLACK][queen ? 1 : 2];
    }
    @Override
    public void setRookMovement(boolean white, boolean queen, boolean moved) {
        this.kingRookMovement[white ? WHITE : BLACK][queen ? 1 : 2] = moved;
    }
    @Override
    public Map<Coordinate, Map<Coordinate, Path>> getStartLocations() {
        return startLocations;
    }

    @Override
    public Map<Coordinate, Boolean> getCaged() {
        return caged;
    }
    // <Type of piece, <potentially promoted pieces, how many are may not be promoted>
    @Override
    public Map<String, Map<Path, Integer>> getPromotionNumbers() {
        return promotionNumbers;
    }

    @Override
    public Path getCagedCaptures(boolean white) {
        return cagedCaptures[white ? WHITE : BLACK];
    }

    @Override
    public void setPawnsCapturedByPawns(boolean white, int pawnsCapturedByPawns) {
        this.pawnsCapturedByPawns[white ? WHITE : BLACK] = pawnsCapturedByPawns;
    }

    @Override
    public int getPawnsCapturedByPawns(boolean white) {
        return pawnsCapturedByPawns[white ? WHITE : BLACK];
    }

    @Override
    public Path getPromotedPawns(boolean white) {
        return promotedPawns[white ? WHITE : BLACK];
    }

    @Override
    public Map<Coordinate, Path> getPromotedPieceMap() {
        return promotedPieceMap;
    }


    @Override
    public int capturedPieces(boolean white) {
        //Sys tem.out.println("CPCP");
        //Sys tem.out.println(this.detector.pawnTakeablePieces(white));
        //Sys tem.out.println((white
//                ? this.detector.getPieceNumber().getBlackPieces()
//                : this.detector.getPieceNumber().getWhitePieces()));
        return pawnTakeablePieces(white) - (white
                ? getPieceNumber().getBlackPieces()
                : getPieceNumber().getWhitePieces());
    }
}
