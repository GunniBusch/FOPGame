package de.tum.cit.ase.editor.data;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.files.FileHandle;
import de.tum.cit.ase.editor.tools.EditorTool;
import de.tum.cit.ase.editor.utlis.TileTypes;

public final class EditorConfig {
    public static final String settings = "mazeGame-EditorSettings";
    public static FileHandle loadedMapProject;
    public static TileTypes selectedTile;
    public static EditorTool selectedTool;
    public static boolean loadPreviousProject;
    public static boolean exportCheckHasExit;
    public static boolean exportCheckCanReachExit;
    public static boolean exportCheckHasKey;
    public static boolean exportCheckCanReachKey;

    static {
        Preferences prefs = Gdx.app.getPreferences("mazeGame-EditorSettings");
        if (prefs.contains("exportCheckHasExit")) {
            exportCheckHasExit = prefs.getBoolean("exportCheckHasExit");
        } else {
            exportCheckHasExit = true;
            prefs.putBoolean("exportCheckHasExit", true);

        }
        if (prefs.contains("exportCheckCanReachExit")) {
            exportCheckCanReachExit = prefs.getBoolean("exportCheckCanReachExit");
        } else {
            exportCheckCanReachExit = true;
            prefs.putBoolean("exportCheckCanReachExit", true);

        }
        if (prefs.contains("exportCheckHasKey")) {
            exportCheckHasKey = prefs.getBoolean("exportCheckHasKey");
        } else {
            exportCheckHasKey = false;
            prefs.putBoolean("exportCheckHasKey", false);

        }
        if (prefs.contains("exportCheckCanReachKey")) {
            exportCheckCanReachKey = prefs.getBoolean("exportCheckCanReachKey");
        } else {
            exportCheckCanReachKey = false;
            prefs.putBoolean("exportCheckCanReachKey", false);

        }
        if (prefs.contains("loadPreviousProject")) {
            var s = loadPreviousProject = prefs.getBoolean("loadPreviousProject");
            if (s) {
                if (prefs.contains("loadedMapProject")) {
                    loadedMapProject = prefs.getString("loadedMapProject").isBlank() ? null : new FileHandle(prefs.getString("loadedMapProject"));
                } else {
                    loadedMapProject = null;
                    prefs.putString("loadedMapProject", "");
                }
            }
        } else {
            loadPreviousProject = false;
            prefs.putBoolean("loadPreviousProject", false);

        }
        prefs.flush();
    }

    public static void saveSettings() {
        Preferences prefs = Gdx.app.getPreferences(settings);
        prefs.putBoolean("exportCheckHasExit", exportCheckHasExit);
        prefs.putBoolean("exportCheckCanReachExit", exportCheckCanReachExit);
        prefs.putBoolean("exportCheckHasKey", exportCheckHasKey);
        prefs.putBoolean("exportCheckCanReachKey", exportCheckCanReachKey);
        prefs.putBoolean("loadPreviousProject", loadPreviousProject);
        if (loadPreviousProject) {
            prefs.putString("loadedMapProject", loadedMapProject != null ? loadedMapProject.path() : "");
        }
        prefs.flush();
    }

}
