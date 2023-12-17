package de.tum.cit.ase.maze.utils;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;

import java.util.ArrayList;
import java.util.List;

import static de.tum.cit.ase.maze.utils.CONSTANTS.PPM;

public class MapLoader {
    private SpriteBatch spriteBatch;
    private List<Vector2> WallList;
    private TextureRegion textureRegion;
    private World world;
    private final float SCALE = 0.5f;
    private List<Body> bodies;

    public MapLoader(World world, SpriteBatch spriteBatch) {
        this.spriteBatch = spriteBatch;
        this.world = world;
        Texture texture = new Texture("basictiles.png");
        this.textureRegion = new TextureRegion(texture, 16, 0, 16, 16);
        this.bodies = new ArrayList<>();
        FileHandle handle = Gdx.files.internal("/Users/leonadomaitis/IdeaProjects/FOPGame/maps/level-3.properties");
        String text = handle.readString();
        String[] lines = text.split("\\R");
        List<String> stringList = new ArrayList<>(List.of(lines));
        this.WallList = new ArrayList<>();
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


        System.out.println(WallList);


        for (Vector2 pos : WallList) {

            this.createBody(pos.x, pos.y);

        }


    }

    public void render(float dt) {

        this.spriteBatch.begin();
        for (int i = 0; i < WallList.size(); i++) {
            spriteBatch.draw(textureRegion, bodies.get(i).getPosition().x * PPM - (32 / SCALE / 2), bodies.get(i).getPosition().y * PPM - (32 / SCALE / 2), 32 / SCALE, 32 / SCALE);


        }
        this.spriteBatch.end();
    }

    private void createBody(float x, float y) {
        Body pBody;
        BodyDef def = new BodyDef();


        def.type = BodyDef.BodyType.StaticBody;

        def.position.set(x / SCALE, y / SCALE);
        Gdx.app.log("BP", def.position.toString());
        def.fixedRotation = true;
        pBody = world.createBody(def);

        PolygonShape shape = new PolygonShape();


        shape.setAsBox(32 / 2f / SCALE / PPM, 32 / 2f / SCALE / PPM);
        pBody.createFixture(shape, 0);
        shape.dispose();

        this.bodies.add(pBody);
    }
}
