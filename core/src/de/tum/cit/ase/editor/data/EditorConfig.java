package de.tum.cit.ase.editor.data;

import com.badlogic.gdx.files.FileHandle;
import de.tum.cit.ase.editor.tools.EditorTool;
import de.tum.cit.ase.editor.utlis.TileTypes;

public final class EditorConfig {
    public static TileTypes selectedTile;
    public static EditorTool selectedTool;
    public static FileHandle loadedFileName = null;
    public static boolean exportCheckHasExit = true;
    public static boolean exportCheckCanReachExit = true;
    public static boolean exportCheckHasKey = false;
    public static boolean exportCheckCanReachKey = true;

}
