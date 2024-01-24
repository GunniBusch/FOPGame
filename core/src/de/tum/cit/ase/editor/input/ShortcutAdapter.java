package de.tum.cit.ase.editor.input;

import java.util.HashSet;
import java.util.Set;

public interface ShortcutAdapter {
    Set<Integer> pressedKeys = new HashSet<>(10);

    default boolean isShortcut(Integer... keys) {
        return pressedKeys.equals(Set.of(keys));
    }

    default void addKey(int key) {
        pressedKeys.add(key);
    }

    default void removeKey(int key) {
        pressedKeys.remove(key);
    }


}
