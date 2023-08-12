package SolveAlgorithm;

import SolveAlgorithm.SolverShortcuts.AbstractSolverShortcut;
import SolveAlgorithm.SolverShortcuts.GreaterBoardShortcut;
import StandardChess.Coordinate;

import java.util.*;

public class StateComparator {
    List<AbstractSolverShortcut> shortcuts = List.of(new GreaterBoardShortcut());
    Map<String, Map<Move, Boolean>> shortcutLog = new HashMap<>();

    public void registerState(Move move, boolean state) {
        if (findState(move) != 0) {
            this.shortcutLog.get(move.fen()).put(move, state);
        }

    }

    public int findState(Move move) {
        if (!this.shortcutLog.containsKey(move.fen())) {
            this.shortcutLog.put(move.fen(), new HashMap<>());
        }
        Optional<Map.Entry<Move, Boolean>> entry = this.shortcutLog.get(move.fen()).entrySet().stream()
                .filter(e -> shortcuts.stream()
                        .anyMatch(s -> s.match(e.getKey(), move)))
                .findAny();
        return entry.map(moveBooleanEntry -> moveBooleanEntry.getValue() ? 1 : -1).orElse(0);
    }

    public void removeState(String fen) {
        this.shortcutLog.remove(fen);
    }

}
