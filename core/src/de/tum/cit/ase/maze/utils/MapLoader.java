package de.tum.cit.ase.maze.utils;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteCache;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Properties;

import static de.tum.cit.ase.maze.utils.CONSTANTS.PPM;

public class MapLoader {
    private final SpriteCache spriteBatch;
    private final List<Vector2> WallList;
    private final TextureRegion textureRegion;
    private final World world;
    private final float SCALE = 0.5f;
    private final List<Body> bodies;

    public MapLoader(World world, SpriteCache spriteBatch) {
        this.spriteBatch = spriteBatch;
        this.world = world;
        Texture texture = new Texture("basictiles.png");
        this.textureRegion = new TextureRegion(texture, 16, 0, 16, 16);
        this.bodies = new ArrayList<>();
        FileHandle handle = Gdx.files.internal("level-3.properties");
        String text = handle.readString();
        String[] lines = text.split("\\R");
        List<String> stringList = new ArrayList<>(List.of(lines));
        this.WallList = new ArrayList<>();
        // TODO: Refactor -> this method splits map?
        List<List<String>> tList = stringList.stream()
                .map(s ->
                        List.of(s.split("[,=]")))
                .filter(strings -> strings.get(2).equals("0"))
                .peek(strings -> {
                            var s = strings.stream().map(Float::parseFloat).toList();
                            WallList.add(new Vector2(s.get(0), s.get(1)));
                        }
                )
                .toList();


        // ToDo test if neccesary
        WallList.sort((o1, o2) -> {
            if (o1.y == o2.y) {
                return (int) (o1.x - o2.x);
            } else {
                return (int) (o1.y - o2.y);
            }
        });

        //unnötig
        int width = (int) this.WallList.stream().filter(vector2 -> vector2.y == 0f).max(Comparator.comparing(vector2 -> vector2.x)).orElseThrow().x;
        int height = (int) this.WallList.stream().filter(vector2 -> vector2.x == 0f).max(Comparator.comparing(vector2 -> vector2.y)).orElseThrow().y;

        //ToDO: Refactor
        // List with enclosed walls (inside) and or make gloabl
        List<Vector2> operateList = WallList.stream()
                .filter(vector2 -> WallList.stream()
                        .filter(vector21 -> ((vector21.y + 1f == vector2.y || vector21.y - 1f == vector2.y || vector21.y == vector2.y) && (vector21.x + 1f == vector2.x || vector21.x - 1f == vector2.x || vector21.x == vector2.x)))
                        .count() == 9L)
                .toList();
        // ToDo: Refactor / Remove
        // Break the inner loop if a match is found
        /*
        var lst = this.WallList.stream().collect(Collectors.groupingBy(w -> w.y));
        List<List<Vector2>> lslst = new ArrayList<>();

        for (int i = 0; i < height; i++) {
            lslst.add(lst.get(Integer.valueOf(i).floatValue()));
        }
        var removeList = new ArrayList<>();
        for (List<Vector2> i :lslst) {
            if (lslst.get(0).equals(i)){
                var
                continue;
            }
            if (lslst.get(height-1).equals(i)) continue;;
            for (Vector2 j : i) {
                if (i.get(0).equals(i)) continue;
                if (i.get(i.size()-1).equals(i)) continue;
                if (i.y - 1
                i.y+1
            i.x+1
            i.x-1
            i.x + 1 i.y +1
            i.x+1 i.y-1
            i.x- 1 i.y+1
            i.x-1 i.y-y)





            }
            System.out.println(lslst);
        System.out.println(lst.keySet());

         */

        this.WallList.removeAll(operateList);

        System.out.println(WallList);
        System.out.println(operateList);

        for (Vector2 pos : WallList) {

            this.createBody(pos.x, pos.y);

        }


    }



    public void render(float dt) {


        for (int i = 0; i < WallList.size(); i++) {
            // Drqw wall
            spriteBatch.add(textureRegion, bodies.get(i).getPosition().x * PPM - (32 / SCALE / 2), bodies.get(i).getPosition().y * PPM - (32 / SCALE / 2), 32 / SCALE, 32 / SCALE);


        }
    }

    private void createBody(float x, float y) {
        Body pBody;
        BodyDef def = new BodyDef();


        def.type = BodyDef.BodyType.StaticBody;

        //If  scaling uncomment and remove next line
        def.position.set(x / SCALE, y / SCALE);
        //def.position.set(x, y);

        // Gdx.app.log("BP", def.position.toString());
        def.fixedRotation = true;
        pBody = world.createBody(def);
        pBody.setAwake(false);

        PolygonShape shape = new PolygonShape();


        //If scaling uncomment and remove next line
        shape.setAsBox(32 / 2f / SCALE / PPM, 32 / 2f / SCALE / PPM);
        //shape.setAsBox(1f / PPM, 1 / PPM);
        pBody.createFixture(shape, 0);
        shape.dispose();

        this.bodies.add(pBody);
    }

    /**
     *
     * gets all the coordinates of the elements in the mapfile as keyValue Pairs
     */
    public Properties loadMapFile(FileHandle fileHandle) {
        Properties properties = new Properties();

        try {
            if (fileHandle.exists()) {
                properties.load(fileHandle.read()); // Load the properties from the file
            } else {
                Gdx.app.error("PropertiesLoader", "File not found: " + filename);
            }
        } catch (IOException e) {
            Gdx.app.error("PropertiesLoader", "Error reading file: " + filename);
            e.printStackTrace();
        }

        return properties;
    }

    public List<Vector2> getWallList() {
        return WallList;
    }
}
