package de.tum.cit.ase.editor.utlis;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonValue;
import de.tum.cit.ase.editor.data.Map;
import de.tum.cit.ase.editor.utlis.exceptions.InvalidMapFile;
import de.tum.cit.ase.maze.objects.ObjectType;
import de.tum.cit.ase.maze.utils.MapLoader;

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

    public static void saveMapProject(final FileHandle mapFile, Map mapToSave) {
        validate(mapToSave);
        json.toJson(mapToSave, mapFile);
    }

    static boolean validate(final Map map) {
        var grid = map.map();
        if (grid != null) {
            if (grid.length == 0) {
                throw new InvalidMapFile("Grid of map has invalid height");

            } else if (grid[0].length == 0) {
                throw new InvalidMapFile("Grid of map has invalid width");

            } else {
                return true;
            }
        } else {
            throw new InvalidMapFile("Map does not have a grid");

        }
    }

    public static Map exportMap(final FileHandle mapFile) {
        MapLoader.loadMapFile(mapFile);
        TileTypes[][] rawMap = new TileTypes[(int) (MapLoader.height + 1)][(int) (MapLoader.width + 1)];
        for (ObjectType value : ObjectType.values()) {
            MapLoader.getMapCoordinates(value);
        }
        return null;
    }

    public static Map importMap(final FileHandle mapFile) {
        MapLoader.loadMapFile(mapFile);
        TileTypes[][] rawMap = new TileTypes[(int) (MapLoader.height + 1)][(int) (MapLoader.width + 1)];
        for (ObjectType value : ObjectType.values()) {
            MapLoader.getMapCoordinates(value);
        }
        return null;
    }


    public static Map readMapProject(final FileHandle mapFile) {
        var map = json.fromJson(Map.class, mapFile);
        validate(map);
        return map;
    }


}
