package Heuristics.Detector;

import Heuristics.BoardInterface;
import Heuristics.Observations.PieceNumber;
import Heuristics.Path;
import StandardChess.Coordinate;

import java.util.List;
import java.util.Map;

public interface StateDetector {

    boolean getState();

    boolean testState();

    boolean testState(BoardInterface board);







    Map<Coordinate, Boolean> getOriginFree(boolean white);





    void reTest(BoardInterface boardInterface);
    String getErrorMessage();


    // Castling data

    boolean getKingMovement(boolean white);

    void setKingMovement(boolean white, boolean moved);

    boolean getRookMovement(boolean white, boolean queen);

    void setRookMovement(boolean white, boolean queen, boolean moved);

    //Promotion data
    Map<String, Map<Path, Integer>> getPromotionNumbers();
    Path getCagedCaptures(boolean white); // CL and ProMap - part of the rollup - This is essentially innaccessible taken rooks


    //Piece mapping data
    Map<Coordinate, Path> getPromotedPieceMap();
    Map<Coordinate, Boolean> getCaged(); //Important
    Map<Coordinate, Map<Coordinate, Path>> getStartLocations(); //Mostly important


    // Pawn Data
    Map<Coordinate, Path> getPawnOrigins(boolean white); // Change instead to references to pawn paths
    void setPawnsCapturedByPawns(boolean white, int pawnsCapturedByPawns);
    int getPawnsCapturedByPawns(boolean white);    //This and the above: CL and PPS - Roll this into a single dataset with
                                                   //Pawntakeablecaptures and additionalcaptures - a set of impossibly taken pieces?
    Path getPromotedPawns(boolean white);
    int minimumPawnCaptures(boolean white); //Important
    int pawnTakeablePieces(boolean white); //Important
    void reducePawnTakeablePieces(boolean white, int subtrahend); //Important
    Map<Coordinate, List<Path>> getPawnPaths(boolean white); //Important


    Map<Coordinate, Path> getSinglePawnPaths(boolean white); //Pawns with one origin and one possible path to that origin
                                                            //Maybe there's a shortcut here using capture numbers?

    Map<Coordinate, Integer> getCaptureSet(boolean white); //Used by CP and PM + PromotionPM - can be changed to a public method
    int getCapturedPieces(boolean white); //Used by CPM and PM
    void setCapturedPieces(boolean white, int capturedPieces); //Used by CPM and PM
    int getMaxCaptures(boolean white, Coordinate coordinate); // CPM


    //DEPRECATED
    @Deprecated
    List<Path> getPromotionPaths(boolean white);
    @Deprecated
    int getPawnNumbers(boolean white);
    @Deprecated
    int capturedPieces(boolean white);
    @Deprecated
    public PieceNumber getPieceNumber();
    void update(); //Get rid of when combining Pawn maps




}
