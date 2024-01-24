package de.tum.cit.ase.editor.input;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonValue;

import java.lang.reflect.Field;
import java.util.HashMap;

public class Shortcuts {
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
                return new Shortcut(json.readValue(Integer[].class, jsonData.child));
            }
        });

        Preferences prefs = Gdx.app.getPreferences("maze-editor-shortcuts");

        for (Field declaredField : initClass.getDeclaredFields()) {
            try {
                var z = new HashMap<String, Integer[]>();
                var s = ((Shortcut) declaredField.get(null));
                if (!prefs.contains(declaredField.getName())) {
                    System.out.println(json.prettyPrint(s));
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

    public static class UI {


        public static Shortcut ZOOM = new Shortcut(Input.Keys.SHIFT_LEFT);

        static {
            Shortcuts.load(UI.class);
        }

    }

    public static class EDITOR {


        public static Shortcut STRAIGHT_LINE = new Shortcut(Input.Keys.CONTROL_LEFT);

        static {
            Shortcuts.load(EDITOR.class);
        }

    }

    public record Shortcut(Integer... keys) {

    }
}
