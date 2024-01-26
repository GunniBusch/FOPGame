package de.tum.cit.ase.editor.data;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.files.FileHandle;
import de.tum.cit.ase.editor.tools.EditorTool;
import de.tum.cit.ase.editor.utlis.TileTypes;

public final class EditorConfig {
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
            exportCheckCanReachKey = true;
            prefs.putBoolean("exportCheckCanReachKey", false);

        }
        prefs.flush();
    }

    public static final String settings = "mazeGame-EditorSettings";
    public static TileTypes selectedTile;
    public static EditorTool selectedTool;
    public static FileHandle loadedFileName = null;
    public static boolean exportCheckHasExit;
    public static boolean exportCheckCanReachExit;
    public static boolean exportCheckHasKey;
    public static boolean exportCheckCanReachKey;

    public static void saveSettings() {
        Preferences prefs = Gdx.app.getPreferences(settings);
        prefs.putBoolean("exportCheckHasExit", exportCheckHasExit);
        prefs.putBoolean("exportCheckCanReachExit", exportCheckCanReachExit);
        prefs.putBoolean("exportCheckHasKey", exportCheckHasKey);
        prefs.putBoolean("exportCheckCanReachKey", exportCheckCanReachKey);
        prefs.flush();
    }

}
