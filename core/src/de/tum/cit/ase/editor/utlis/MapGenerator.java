package de.tum.cit.ase.editor.utlis;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonValue;
import de.tum.cit.ase.editor.data.EditorConfig;
import de.tum.cit.ase.editor.data.Map;
import de.tum.cit.ase.editor.utlis.exceptions.InvalidMapFile;
import de.tum.cit.ase.maze.map.AStar;
import de.tum.cit.ase.maze.objects.ObjectType;
import de.tum.cit.ase.maze.utils.MapLoader;
import de.tum.cit.ase.maze.utils.exceptions.MapLoadingException;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

/**
 * The MapGenerator class is responsible for generating, saving, importing, and validating maps.
 */
public class MapGenerator {
    private static final Json json;


    static {
        json = new Json();
        json.setElementType(Map.class, "map", TileTypes[][].class);
        json.setElementType(Map.class, "name", String.class);
        json.setSerializer(TileTypes[].class, new Json.Serializer<TileTypes[]>() {
            @Override
            public void write(Json json, TileTypes[] object, Class knownType) {
                json.writeObjectStart(TileTypes[].class, knownType);
                json.writeValue("Size", object.length, int.class);

                for (int i = 0; i < object.length; i++) {
                    if (object[i] != null) {
                        json.writeValue(String.format("%d", i), object[i]);


                    }
                }
                json.writeObjectEnd();

            }


            @Override
            public TileTypes[] read(Json json, JsonValue jsonData, Class type) {
                TileTypes[] tileArr = new TileTypes[json.readValue("Size", int.class, jsonData)];
                for (int i = 0; i < tileArr.length; i++) {
                    tileArr[i] = json.readValue(String.format("%d", i), TileTypes.class, jsonData);
                }
                return tileArr;
            }
        });
//        json.setSerializer(TileTypes[][].class, new Json.Serializer<TileTypes[][]>() {
//            @Override
//            public void write(Json json, TileTypes[][] object, Class knownType) {
//                json.writeArrayStart();
//                for (TileTypes[] tileTypes : object) {
//                    json.writeValue(tileTypes);
//                }
//                json.writeArrayEnd();
//
//            }
//
//
//            @Override
//            public TileTypes[][] read(Json json, JsonValue jsonData, Class type) {
//                return json.readValue(TileTypes[][].class, TileTypes[].class, jsonData);
//            }
//        });
        json.setSerializer(Map.class, new Json.Serializer<Map>() {
            @Override
            public void write(Json json, Map object, Class knownType) {
                json.writeObjectStart();
                json.writeField(object, "name", String.class);
                json.writeField(object, "map", TileTypes[][].class);
                json.writeObjectEnd();
            }

            @Override
            public Map read(Json json, JsonValue jsonData, Class type) {
                return new Map(json.readValue("name", String.class, jsonData), json.readValue("map", TileTypes[][].class, jsonData));
            }
        });

    }

    /**
     * Saves a map project to a specified file.
     *
     * @param mapFile   The {@code FileHandle} representing the file to save the map project to.
     * @param mapToSave The {@code Map} object to save.
     * @throws InvalidMapFile if the map is invalid.
     */
    public static void saveMapProject(final FileHandle mapFile, Map mapToSave) {
        validate(mapToSave);
        json.toJson(mapToSave, mapFile);
    }

    /**
     * Validates a map by checking if it has a grid and if the grid has valid dimensions.
     *
     * @param map The {@code Map} object to validate.
     * @throws InvalidMapFile if the map is invalid.
     */
    static void validate(final Map map) {
        var grid = map.map();
        if (grid != null) {
            if (grid.length == 0) {
                throw new InvalidMapFile("Grid of map has invalid height");

            } else if (grid[0].length == 0) {
                throw new InvalidMapFile("Grid of map has invalid width");

            }
        } else {
            throw new InvalidMapFile("Map does not have a grid");

        }
    }

    /**
     * Validates an exported map by checking for check specified in {@link de.tum.cit.ase.editor.data.EditorConfig}.
     *
     * @param map The {@code Map} object to validate.
     * @throws InvalidMapFile If the map is invalid.
     */
    public static void validateExport(Map map) {
        var grid = map.map();
        Set<String> errorList = new HashSet<>();
        if (grid == null) {
            throw new InvalidMapFile("Map grid does not exist");
        } else {
            try {
                loadMapIntoGame(map);
            } catch (MapLoadingException e) {
                if (Arrays.stream(grid).allMatch(tileTypes -> Arrays.stream(tileTypes).allMatch(Objects::isNull)))
                    throw new InvalidMapFile("Map is empty", e);
                else
                    throw e;
            }

            // Check if grid has an exit and entry.

            if (Arrays.stream(grid).noneMatch(tileTypes -> Arrays.asList(tileTypes).contains(TileTypes.Entry))) {
                throw new InvalidMapFile("Map does not have an entry");
            }

            if (EditorConfig.exportCheckHasExit) {
                if (Arrays.stream(grid).noneMatch(tileTypes -> Arrays.asList(tileTypes).contains(TileTypes.Exit))) {
                    throw new InvalidMapFile("Check exit is on. Map does not have an exit");
                }

                if (EditorConfig.exportCheckCanReachExit) {
                    var exits = MapLoader.getMapCoordinates(ObjectType.Exit);
                    var entry = MapLoader.getMapCoordinates(ObjectType.EntryPoint).get(0);
                    for (Vector2 exit : exits) {
                        var path = AStar.findPath(MapLoader.getGameGrid(), entry, exit);
                        if (path.isEmpty()) {
                            throw new InvalidMapFile("Check can reach exit is on. Exit can not be reached");
                        }
                    }
                }
            }
            if (EditorConfig.exportCheckHasKey) {
                if (Arrays.stream(grid).noneMatch(tileTypes -> Arrays.asList(tileTypes).contains(TileTypes.Key))) {
                    throw new InvalidMapFile("Check key is on. Map does not have a key");
                }

                if (EditorConfig.exportCheckCanReachKey) {
                    var keys = MapLoader.getMapCoordinates(ObjectType.Key);
                    var entry = MapLoader.getMapCoordinates(ObjectType.EntryPoint).get(0);
                    for (Vector2 key : keys) {
                        var path = AStar.findPath(MapLoader.getGameGrid(), entry, key);
                        if (path.isEmpty()) {
                            throw new InvalidMapFile("Check can reach key is on. Key can not be reached");
                        }
                    }
                }
            }


        }
    }

    /**
     * Loads a map into the game.
     *
     * @param map The {@code Map} object representing the map to be loaded.
     * @throws MapLoadingException if an error occurs while loading the map.
     */
    public static void loadMapIntoGame(Map map) throws MapLoadingException {

        var fh = FileHandle.tempFile("maploader-x");
        exportMap(map, fh, false);
        MapLoader.loadMapFile(fh);
    }

    /**
     * Imports a map from a file and converts it into a Map object.
     *
     * @param mapFile The {@code FileHandle} representing the file to import the map from.
     * @return The imported Map object.
     */
    public static Map importMap(final FileHandle mapFile) {
        MapLoader.loadMapFile(mapFile);
        TileTypes[][] rawMap = new TileTypes[(int) (MapLoader.height + 1)][(int) (MapLoader.width + 1)];
        for (ObjectType value : ObjectType.values()) {
            var mCor = MapLoader.getMapCoordinates(value);
            for (Vector2 vector2 : mCor) {
                rawMap[(int) vector2.y][(int) vector2.x] = TileTypes.convertFromObjectType(value);
            }
        }
        return new Map(mapFile.nameWithoutExtension(), rawMap);
    }


    /**
     * Reads a map project file and converts it into a Map object.
     *
     * @param mapFile The {@code FileHandle} representing the file to read the map project from.
     * @return The {@code Map} object representing the map project.
     */
    public static Map readMapProject(final FileHandle mapFile) {
        var map = json.fromJson(Map.class, mapFile);
        validate(map);
        return map;
    }

    /**
     * Exports a Map object to a specified file.
     *
     * @param map        The Map object to export.
     * @param exportFile The FileHandle representing the file to export the Map object to.
     */
    public static void exportMap(Map map, final FileHandle exportFile) {
        exportMap(map, exportFile, true);
    }

    public static void exportMap(Map map, final FileHandle exportFile, boolean validate) {
        if (validate) {
            validateExport(map);
        }
        for (int y = 0; y < map.map().length; y++) {
            for (int x = 0; x < map.map()[0].length; x++) {
                if (map.map()[y][x] != null) {
                    exportFile.writeString(x + "," + y + "=" + TileTypes.convertToObjectType(map.map()[y][x]).ordinal() + "\n", true);
                }
            }
        }
    }


}
