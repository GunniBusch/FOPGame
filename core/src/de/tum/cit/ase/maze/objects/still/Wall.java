package de.tum.cit.ase.maze.objects.still;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteCache;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.ChainShape;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Disposable;
import de.tum.cit.ase.maze.utils.CONSTANTS;

import java.util.ArrayList;
import java.util.List;

import static de.tum.cit.ase.maze.utils.CONSTANTS.PPM;

/**
 * The class Wall implements the different Mazes in the game.
 * It reads an input file in the maps directory.
 */

public class Wall implements Disposable {
    public static int height = 32;
    public static int width = 32;
    private final SpriteCache spriteCache;
    private final List<Vector2> outsideWalls;
    private final List<Vector2> insideWalls;
    private final List<Vector2> fullWalls;
    private final Body body;
    private final float SCALE = 1 / CONSTANTS.SCALE;
    private Texture img;


    public Wall(List<Vector2> map, SpriteCache spriteCache, World world) {
        BodyDef def = new BodyDef();
        def.type = BodyDef.BodyType.StaticBody;
        def.fixedRotation = true;
        body = world.createBody(def);
        body.setAwake(false);
        this.spriteCache = spriteCache;
        fullWalls = new ArrayList<>(map);
        insideWalls = map.stream()
                .filter(vector2 -> map.stream()
                        .filter(vector21 -> ((vector21.y + 1f == vector2.y || vector21.y - 1f == vector2.y || vector21.y == vector2.y) && (vector21.x + 1f == vector2.x || vector21.x - 1f == vector2.x || vector21.x == vector2.x)))
                        .count() == 9L)
                .toList();
        outsideWalls = map;
        outsideWalls.removeAll(insideWalls);
        createBody();

    }

    public void render() {
        Texture texture = new Texture("basictiles.png");
        TextureRegion textureRegion = new TextureRegion(texture, 16, 0, 16, 16);
        //TODO: logic checking wether inner or outer wall

        /*if(innerWall) {
            // TODO: find nice texture for innerwalls, decide with leon
            img = new Texture("wall.png");

            spriteCache.draw(img, position.x, position.y);
        } else {
            // TODO: find nice texture for outerWalls, decide with leon
            img = new Texture("lava.png");
            spriteCache.draw(img, position.x, position.y);
        }*/
        for (Vector2 outsideWall : outsideWalls) {
            // Draw wall
            spriteCache.add(textureRegion, outsideWall.x * width / SCALE - (width / SCALE / 2), outsideWall.y * height / SCALE - (height / SCALE / 2), width / SCALE, height / SCALE);
        }
        for (Vector2 insideWall : insideWalls) {
            spriteCache.add(textureRegion, insideWall.x * width / SCALE - (width / SCALE / 2), insideWall.y * height / SCALE - (height / SCALE / 2), width / SCALE, height / SCALE);
        }

    }

    private void createBody() {


        var halfSize = 1f * width / PPM;
        //If  scaling uncomment and remove next line

        float x, y;
        ChainShape shape;
        Vector2[] corners;
        for (Vector2 outsideWall : outsideWalls) {


            shape = new ChainShape();
            x = outsideWall.x * width / PPM / SCALE;
            y = outsideWall.y * width / PPM / SCALE;


            //If scaling uncomment and remove next line
            corners = new Vector2[]{
                    new Vector2(x - halfSize, y - halfSize), // bottom-left
                    new Vector2(x + halfSize, y - halfSize), // bottom-right
                    new Vector2(x + halfSize, y + halfSize), // top-right
                    new Vector2(x - halfSize, y + halfSize)  // top-left
            };
            shape.createLoop(corners);
            body.createFixture(shape, 0f);
            shape.dispose();
        }
    }


    @Override
    public void dispose() {

    }

    public List<Vector2> getOutsideWalls() {
        return outsideWalls;
    }

    public List<Vector2> getInsideWalls() {
        return insideWalls;
    }

    public List<Vector2> getFullWalls() {
        return fullWalls;
    }
}
