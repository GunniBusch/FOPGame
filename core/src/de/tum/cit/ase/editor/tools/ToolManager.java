package de.tum.cit.ase.editor.tools;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.Pools;

import java.lang.ref.SoftReference;

public class ToolManager {


    public static <T extends Tool> SoftReference<T> getTool(Class<T> toolClass) {
        return new SoftReference<>(Pools.obtain(toolClass));
    }

    public static <T extends Tool> void freeTool(T toolObject) {
        Gdx.app.debug("Free", toolObject.toString() + " ; " + Pools.get(toolObject.getClass()).getFree());
        Pools.free(toolObject);
        Gdx.app.debug("Free2", toolObject + " ; " + Pools.get(toolObject.getClass()).getFree());

    }
}
