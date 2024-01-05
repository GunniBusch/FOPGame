package de.tum.cit.ase.maze.utils;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;

import java.io.IOException;
import java.util.Properties;

public final class MapLoader {
    private static Properties map;
    public static void loadMapFile(FileHandle fileHandle) {
        map = new Properties();

        try {
            if (fileHandle.exists()) {
                map.load(fileHandle.read()); // Load the properties from the file
            } else {
                Gdx.app.error("PropertiesLoader", "File not found: " + filename);
            }
        } catch (IOException e) {
            Gdx.app.error("PropertiesLoader", "Error reading file: " + filename);
            e.printStackTrace();
        }

    }
}



