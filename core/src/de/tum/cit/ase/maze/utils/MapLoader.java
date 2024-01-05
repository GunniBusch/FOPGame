package de.tum.cit.ase.maze.utils;


import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.math.Vector2;
import de.tum.cit.ase.maze.objects.ObjectType;
import de.tum.cit.ase.maze.utils.exceptions.MapLoadingException;
import de.tum.cit.ase.maze.utils.exceptions.ObjectTypeException;


import java.util.*;
import java.util.stream.Collectors;

public final class MapLoader {
    private static Map<ObjectType, List<Vector2>> map;

    public static void loadMapFile(FileHandle fileHandle) {
        map = new HashMap<>();

        try {
            String fileText = fileHandle.readString();

            List<String> lineList = new ArrayList<>(List.of(fileText.split("\\R")));
             map = lineList.stream()
                    .map(s -> new ArrayList<>(List.of(s.split("[,=]"))))
                    .collect(Collectors.groupingBy(strings -> ObjectType.valueOfLabel(Integer.valueOf(strings.get(2))),
                                    Collectors.mapping(
                                            strings -> new Vector2(Float.parseFloat(strings.get(0)), Float.parseFloat(strings.get(1))), Collectors.toList()
                                    )
                            )
                    );


        } catch (IndexOutOfBoundsException | ObjectTypeException e) {
            throw new MapLoadingException("Can't load map");
        }

    }

    public static List<Vector2> getMapCoordinates(ObjectType type) {
        return new ArrayList<>(map.get(type));
    }
}



