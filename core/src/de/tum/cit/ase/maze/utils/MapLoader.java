package de.tum.cit.ase.maze.utils;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.math.Vector2;
import de.tum.cit.ase.maze.objects.ObjectType;
import de.tum.cit.ase.maze.utils.exceptions.MapLoadingException;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

public final class MapLoader {
    private static Map<ObjectType, List<Vector2>> map;

    public static void loadMapFile(FileHandle fileHandle) {
        map = new HashMap<>();

        try {

        } catch (IndexOutOfBoundsException e) {
            throw new MapLoadingException("Can't load map");
        }

    }
}



