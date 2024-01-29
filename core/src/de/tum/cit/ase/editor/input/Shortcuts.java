package de.tum.cit.ase.editor.input;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonValue;
import com.badlogic.gdx.utils.SharedLibraryLoader;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;

/**
 * The Shortcuts class is responsible for loading and managing keyboard shortcuts.
 * It utilizes the Json library for serialization and the Preferences class for storing shortcut configurations.
 */
public class Shortcuts {
    /**
     * Loads and initializes shortcuts from the specified class.
     *
     * @param initClass The class containing the shortcut fields.
     */
    public static void load(Class<?> initClass) {
        Json json = new Json();
        json.setElementType(Shortcut.class, "keys", Integer[].class);

        json.setSerializer(Shortcut.class, new Json.Serializer<Shortcut>() {
            @Override
            public void write(Json json, Shortcut object, Class knownType) {
                json.writeObjectStart();
                json.writeValue("keys", object.keys());
                json.writeObjectEnd();
            }

            @Override
            public Shortcut read(Json json, JsonValue jsonData, Class type) {
                return new Shortcut(json.readValue(int[].class, jsonData.child));
            }
        });

        Preferences prefs = Gdx.app.getPreferences("maze-editor-shortcuts-" + System.getProperty("os.name"));

        for (Field declaredField : initClass.getDeclaredFields()) {
            try {
                var s = ((Shortcut) declaredField.get(null));
                if (!prefs.contains(declaredField.getName())) {
                    prefs.putString(declaredField.getName(), json.toJson(s));
                }
                var js = json.fromJson(Shortcut.class, prefs.getString(declaredField.getName()));
                if (js.keys() != null) {
                    declaredField.set(null, js);
                } else {
                    prefs.putString(declaredField.getName(), json.toJson(s));
                }
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        }
        prefs.flush();

    }

    /**
     * The UI class represents a collection of static shortcuts for various UI actions.
     */
    public static class UI {


        public static Shortcut ZOOM = SharedLibraryLoader.isMac ? new Shortcut(Input.Keys.SYM) : new Shortcut(Input.Keys.SHIFT_LEFT);
        public static Shortcut UNDO = SharedLibraryLoader.isMac ? new Shortcut(Input.Keys.SYM, Input.Keys.Z) : new Shortcut(Input.Keys.CONTROL_LEFT, Input.Keys.Z);
        public static Shortcut REDO = SharedLibraryLoader.isMac ? new Shortcut(Input.Keys.SYM, Input.Keys.SHIFT_LEFT, Input.Keys.Z) : new Shortcut(Input.Keys.CONTROL_LEFT, Input.Keys.SHIFT_LEFT, Input.Keys.Z);
        public static Shortcut SAVE = SharedLibraryLoader.isMac ? new Shortcut(Input.Keys.SYM, Input.Keys.S) : new Shortcut(Input.Keys.CONTROL_LEFT, Input.Keys.S);
        public static Shortcut EXPORT = SharedLibraryLoader.isMac ? new Shortcut(Input.Keys.SYM, Input.Keys.E) : new Shortcut(Input.Keys.CONTROL_LEFT, Input.Keys.E);

        static {
            Shortcuts.load(UI.class);
        }

    }

    /**
     * The EDITOR class represents a collection of static shortcuts for editing actions.
     */
    public static class EDITOR {


        public static Shortcut STRAIGHT_LINE = new Shortcut(Input.Keys.CONTROL_LEFT);

        static {
            Shortcuts.load(EDITOR.class);
        }

    }

    /**
     * The Shortcut class represents a shortcut with one or more keys.
     * The result of {@link Shortcut#keys()} is not converted to the current Layout. Only {@link Shortcut#modKeys()} and {@link Shortcut#key()} convert them.
     */
    public record Shortcut(int... keys) {
        public int key() {
            return KeyMapper.convertKey(keys[keys.length - 1]);
        }

        /**
         * Returns an array of modifier keys see {@link com.ray3k.stripe.StripeMenuBar.KeyboardShortcut KeyboardShortcut}.
         *
         * @return an array of modified keys, or null if there is only one key in the Shortcut.
         */
        public int[] modKeys() {
            if (keys.length > 1) {
                int[] is = new int[keys.length - 1];
                List<Integer> integers = Arrays.stream(keys).boxed().toList().subList(0, keys.length - 1);
                for (int i = 0; i < integers.size(); i++) {
                    is[i] = KeyMapper.convertKey(integers.get(i));
                }
                return is;
            } else return null;
        }

        @Override
        public String toString() {
            StringBuilder s = new StringBuilder();
            for (int i = 0; i < keys.length; i++) {
                var key = "";
                switch (keys[i]) {
                    case Input.Keys.SYM -> key = (SharedLibraryLoader.isMac ? "CMD" : "WIN");
                    default -> key = Input.Keys.toString(keys[i]);
                }
                s.append(key).append(i < keys.length - 1 ? "+" : "");
            }

            return s.toString();
        }
    }
}
