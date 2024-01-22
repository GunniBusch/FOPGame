package de.tum.cit.ase.editor.utlis;

import com.badlogic.gdx.files.FileHandle;
import de.tum.cit.ase.editor.data.Map;
import de.tum.cit.ase.maze.objects.ObjectType;
import de.tum.cit.ase.maze.utils.MapLoader;


public class MapGenerator {

    static Map readMap(final FileHandle mapFile) {
        MapLoader.loadMapFile(mapFile);
        TileTypes[][] rawMap = new TileTypes[(int) (MapLoader.height + 1)][(int) (MapLoader.width + 1)];
        for (ObjectType value : ObjectType.values()) {
            MapLoader.getMapCoordinates(value);
        }
        return null;
    }

}
