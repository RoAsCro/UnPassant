package SolveAlgorithm;

import SolveAlgorithm.SolverShortcuts.AbstractSolverShortcut;
import SolveAlgorithm.SolverShortcuts.GreaterBoardShortcut;

import java.lang.reflect.InvocationTargetException;
import java.util.Map;
import java.util.Set;

public class ShortcutFactory {
    private static final Map<String, AbstractSolverShortcut> registeredShortcuts = Map.of("GBS", new GreaterBoardShortcut());

    public static Set<String> getShortcutCodes() {
        return registeredShortcuts.keySet();
    }

    public static AbstractSolverShortcut getShortcut(String code) throws NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
        return registeredShortcuts.get(code).getClass().getConstructor().newInstance();
    }
}
