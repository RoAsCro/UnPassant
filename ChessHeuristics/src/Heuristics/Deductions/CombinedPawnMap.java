package Heuristics.Deductions;

import Heuristics.BoardInterface;
import Heuristics.Path;
import Heuristics.Pathfinder;
import StandardChess.Coordinate;
import StandardChess.Coordinates;

import java.util.*;

public class CombinedPawnMap extends AbstractDeduction {
    public CombinedPawnMap(){};

    @Override
    public boolean deduce(BoardInterface board) {
        boolean changed = true;
        boolean another = true;
        while (changed) {
            HashSet<List<Path>> startingWhite = new HashSet<>(this.detector.getPawnPaths(true).values());
            HashSet<List<Path>> startingBlack = new HashSet<>(this.detector.getPawnPaths(false).values());

            makeMaps(board, false);
            makeMaps(board, true);
            if ((!exclude(board, true) & !exclude(board, false))
                    || (startingWhite.containsAll(new HashSet<>(this.detector.getPawnPaths(true).values()))
                    && startingBlack.containsAll(new HashSet<>(this.detector.getPawnPaths(false).values())))
            ) {
                if (!another) {
                    changed = false;
                } else {
                    another = false;
                }
            }
        }

        if (this.detector.getPawnPaths(true).values().stream().anyMatch(List::isEmpty) || this.detector.getPawnPaths(false).values().stream().anyMatch(List::isEmpty)) {
            //System.out.println();
            this.state = false;
        }
        return false;
    }


    /**
     *
     * @return whether or not there was a change
     */
    protected boolean exclude(BoardInterface board, boolean white) {
        Map<Coordinate, List<Path>> checkedPlayerPaths = this.detector.getPawnPaths(white);

        Map<Coordinate, List<Path>> opposingPlayerPaths = this.detector.getPawnPaths(!white);
        PiecePathFinderUtil pathFinderUtil = new PiecePathFinderUtil(this.detector);
        // Find every pawn of the opposing player with one origin and one possible path
        List<Map.Entry<Coordinate, List<Path>>> singleOriginPawns = new ArrayList<>(opposingPlayerPaths.entrySet()
                .stream()
                .filter(entry -> entry.getValue().size() == 1 && !(entry.getValue().get(0).size() == 1))
                .filter(entry ->
                        pathFinderUtil.findAllPawnPath(board, entry.getValue().get(0).getFirst(), this.detector.getMaxCaptures(!white, entry.getKey()),
                                (b, c) -> c.equals(entry.getKey()), !white)
                        .size() == 1)
                .toList());
        singleOriginPawns.addAll(opposingPlayerPaths.entrySet()
                .stream().filter(entry ->entry.getValue().size() == 1 && entry.getValue().get(0).size() == 1).toList());
        if (singleOriginPawns.isEmpty()) {
            // New implementation may hugely impact performance here
            updateP();
            return false;
        }

        singleOriginPawns.forEach(entry -> this.detector.getSinglePawnPaths(!white).put(entry.getKey(), entry.getValue().get(0)));

        List<Path> newPaths = new LinkedList<>();
        singleOriginPawns
                .forEach(entry -> checkedPlayerPaths.entrySet()
                        .stream()
                        .filter(innerEntry -> !innerEntry.getValue().isEmpty())
                        .filter(innerEntry -> {
                            Coordinate entryKey = entry.getKey();
                            Coordinate innerEntryKey = innerEntry.getKey();
                            if (entry.getValue().get(0).contains(innerEntryKey)
                                    || innerEntry.getValue().get(0).contains(entryKey)) {
                                return true;
                            }
                            int y2 = innerEntryKey.getY();
                            if (y2 == FINAL_RANK_Y || y2 == FIRST_RANK_Y) {
                                return entry.getValue().get(0).contains(innerEntry.getValue().get(0).get(innerEntry.getValue().get(0).size() - 2));
                            }
                            return false;
                        })
                        .forEach(innerEntry -> {
                            //system.out.println("inner entry" + innerEntry);
                            innerEntry.getValue()
                                    .stream().filter(path -> Pathfinder.pathsExclusive(entry.getValue().get(0), path))
                                    .forEach(path -> {

                                        Path toPut = makeExclusiveMaps(board, path, white, singleOriginPawns);
                                        if (toPut.isEmpty()) {
                                            toPut.add(path.getFirst());
                                            toPut.add(Coordinates.NULL_COORDINATE);
                                            toPut.add(innerEntry.getKey());
                                        }
                                        newPaths.add(toPut);
                                    });
                        }));



        List<Coordinate[]> forRemoval = new LinkedList<>();
        newPaths.stream().forEach(path -> {
            List<Path> pathList = checkedPlayerPaths.get(path.getLast());
            Path toRemove = pathList
                    .stream().filter(path2 -> path2.getFirst() == path.getFirst())
                    .findFirst()
                    .orElse(null);
            pathList.remove(toRemove);
            if (!(path.contains(Coordinates.NULL_COORDINATE))) {
                pathList.add(path);
            } else {
                forRemoval.add(new Coordinate[]{path.getLast(), path.getFirst()});
            }
        });
        forRemoval.forEach(coordinates -> this.detector.getPawnOrigins(white).get(coordinates[0]).remove(coordinates[1]));
        updateP();
        return !forRemoval.isEmpty() || !newPaths.isEmpty();
    }

    protected void updateP() {
        this.detector.update();
    }

    private Path makeExclusiveMaps(BoardInterface board, Path path, boolean white, List<Map.Entry<Coordinate, List<Path>>> forbiddenPaths) {

        //TODO current forbidden paths won't contain paths that are one coordinate long
        Coordinate target = path.getLast();
        return new PiecePathFinderUtil(detector).findShortestPawnPath(board,
                path.getFirst(), this.detector.getMaxCaptures(white, target),
                (b, c) -> c.equals(target),
                white, true, forbiddenPaths.stream().flatMap(e -> e.getValue().stream()).toList());
    }

    private void makeMaps(BoardInterface board, boolean white) {
        this.detector.getPawnOrigins(white).entrySet()
                .stream()
                .forEach(entry -> {
                    List<Path> paths = new LinkedList<>();
                    entry.getValue().stream()
                            .forEach(coordinate -> {
                                Path path =
                                        new PiecePathFinderUtil(this.detector).findShortestPawnPath(
                                                board, coordinate, this.detector.getMaxCaptures(white, entry.getKey()),
                                                (b, c) -> c.equals(entry.getKey()), white, true, new LinkedList<>()
                                );
                                if (!path.isEmpty()) {

                                    paths.add(path);
                                }
                            });
                    this.detector.getPawnPaths(white).put(entry.getKey(), paths);
                });
    }

}
