package de.tum.cit.ase.editor.input;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

public interface ShortcutAdapter {
    Set<Integer> pressedKeys = new HashSet<>(10);

    default boolean isShortcut(int... keys) {
        return pressedKeys.equals(Arrays.stream(keys).boxed().collect(Collectors.toSet()));
    }

    default void addKey(int key) {
        pressedKeys.add(key);
    }

    default void removeKey(int key) {
        pressedKeys.remove(key);
    }


}
