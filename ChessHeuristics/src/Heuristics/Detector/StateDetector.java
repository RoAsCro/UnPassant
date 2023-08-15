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
    Map<Coordinate, Path> getPromotedPieceMap();



    //Piece mapping data
    Map<Coordinate, Boolean> getCaged(); //Important
    Map<Coordinate, Map<Coordinate, Path>> getStartLocations(); //Mostly important


    // Pawn Data
    Map<Coordinate, Path> getPawnOrigins(boolean white); // Change instead to references to pawn paths



    // Capture Data
    Path getnonPawnCaptures(boolean white); // CL and ProMap - part of the rollup - This is essentially innaccessible taken rooks
    void setPawnsCapturedByPawns(boolean white, int pawnsCapturedByPawns);
    int getPawnsCapturedByPawns(boolean white);    //This and the above: CL and PPS - Roll this into a single dataset with
    //Pawntakeablecaptures and additionalcaptures - a set of impossibly taken pieces?


    // Pawn Data
    Map<Coordinate, List<Path>> getPawnPaths(boolean white); //Important



    int minimumPawnCaptures(boolean white); //Important



    // TODO Deprecate
    Map<Coordinate, Path> getSinglePawnPaths(boolean white); //Pawns with one origin and one possible path to that origin
                                                            //Maybe there's a shortcut here using capture numbers?
    int pawnTakeablePieces(boolean white);
    void reducePawnTakeablePieces(boolean white, int subtrahend);
    //DEPRECATED
    @Deprecated
    Map<Coordinate, Integer> getCaptureSet(boolean white); //Used by CP and PM + PromotionPM - can be changed to a public method
    @Deprecated
    int getCapturedPieces(boolean white); //Used by CPM and PM
    @Deprecated
    void setCapturedPieces(boolean white, int capturedPieces); //Used by CPM and PM
    @Deprecated
    int getMaxCaptures(boolean white, Coordinate coordinate); // CPM
    @Deprecated
    List<Path> getPromotionPaths(boolean white);
    @Deprecated
    int getPawnNumbers(boolean white);
    @Deprecated
    int capturedPieces(boolean white);
    @Deprecated
    public PieceNumber getPieceNumber();
    @Deprecated
    void update(); //Get rid of when combining Pawn maps
    @Deprecated
    Path getPromotedPawns(boolean white);




}
