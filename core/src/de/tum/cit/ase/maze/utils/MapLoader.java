package de.tum.cit.ase.maze.utils;


import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.math.Vector2;
import de.tum.cit.ase.maze.map.path.Grid;
import de.tum.cit.ase.maze.objects.ObjectType;
import de.tum.cit.ase.maze.utils.exceptions.MapLoadingException;
import de.tum.cit.ase.maze.utils.exceptions.ObjectTypeException;

import java.util.*;
import java.util.stream.Collectors;

/**
 * The MapLoader class is responsible for loading map files and creating a game grid based on the loaded map.
 * It provides methods to access the loaded map coordinates and the game grid.
 */
public final class MapLoader {
    private static Map<ObjectType, List<Vector2>> map;
    private static Grid gameGrid;
    public static float width, height;

    /**
     * Loads a map file and creates a game grid based on the loaded map.
     *
     * @param fileHandle the handle of the map file to load
     * @throws MapLoadingException if there is an error loading the map file
     */
    public static void loadMapFile(FileHandle fileHandle) {
        map = new HashMap<>();

        try {
            String fileText = fileHandle.readString();

            List<String> lineList = new ArrayList<>(List.of(fileText.split("\\R")));
            map = lineList.stream()
                    .map(s -> new ArrayList<>(List.of(s.split("[,=]"))))
                    .collect(Collectors.groupingBy(strings -> ObjectType.valueOfLabel(Integer.parseInt(strings.get(2))),
                                    Collectors.mapping(
                                            strings -> new Vector2(Float.parseFloat(strings.get(0)), Float.parseFloat(strings.get(1))), Collectors.toList()
                                    )
                            )
                    );


        } catch (IndexOutOfBoundsException | ObjectTypeException e) {
            throw new MapLoadingException("Can't load map", e);
        }
        var prefs = Gdx.app.getPreferences("maze-game-general");
        prefs.putString("LastMap", fileHandle.path());
        prefs.flush();
        width = (int) getMapCoordinates(ObjectType.Wall).stream().filter(vector2 -> vector2.y == 0f).max(Comparator.comparing(vector2 -> vector2.x)).orElseThrow().x;
        height = (int) getMapCoordinates(ObjectType.Wall).stream().filter(vector2 -> vector2.x == 0f).max(Comparator.comparing(vector2 -> vector2.y)).orElseThrow().y;


        gameGrid = new Grid((int) (width + 1), (int) (height + 1));

        for (Vector2 vector2 : map.get(ObjectType.Wall)) {
            gameGrid.setObstacle((int) vector2.x, (int) vector2.y, true);

        }

    }

    public static List<Vector2> getMapCoordinates(ObjectType type) {
        return new ArrayList<>(map.getOrDefault(type, new ArrayList<>()));
    }

    public static Grid getGameGrid() {
        return gameGrid;
    }
}



