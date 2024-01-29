package de.tum.cit.ase.editor.input;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * The ShortcutAdapter interface defines methods for handling shortcuts in an application.
 *
 * <p>
 * This interface provides default implementations for adding and removing keys from the set of pressed keys,
 * as well as checking if a given set of keys is a shortcut.
 * </p>
 *
 * <p>
 * The implementation uses a set of pressed keys to keep track of the keys that are currently being pressed.
 * </p>
 */
public interface ShortcutAdapter {
    Set<Integer> pressedKeys = new HashSet<>(10);

    /**
     * Determines if the given set of keys is a shortcut.
     *
     * @param keys the set of keys to check
     * @return {@code true} if the set of keys is a shortcut, {@code false} otherwise
     */
    default boolean isShortcut(int... keys) {
        return pressedKeys.equals(Arrays.stream(keys).boxed().collect(Collectors.toSet()));
    }

    default boolean isShortcut(Shortcuts.Shortcut shortcut) {
        return isShortcut(shortcut.key(), shortcut.modKeys());
    }

    default boolean isShortcut(int key, int... modifiers) {
        Set<Integer> i = null;
        if (modifiers != null) {
            i = Arrays.stream(modifiers).boxed().collect(Collectors.toSet());
            i.add(key);
        } else {
            i = Set.of(key);
        }


        return pressedKeys.equals(i);
    }

    /**
     * Adds a key to the set of pressed keys.
     *
     * @param key the key to add
     */
    default void addKey(int key) {
        pressedKeys.add(key);
    }

    /**
     * Removes a key from the set of pressed keys.
     *
     * @param key the key to remove
     */
    default void removeKey(int key) {
        pressedKeys.remove(key);
    }


}
