package de.tum.cit.ase.maze.objects.still;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteCache;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;

import java.util.List;

import static de.tum.cit.ase.maze.utils.CONSTANTS.PPM;

/**
 * The class Wall implements the different Mazes in the game.
 * It reads an input file in the maps directory.
 */

public class Wall {
    private SpriteCache spriteCache;
    private Texture img;
    private List<Vector2> outsideWalls;
    private List<Vector2> insideWalls;
    private Body body;
    private final float SCALE = 0.5f;
    private final int textureHeight = 32;
    private final int textureWidth = 32;




    public Wall(List<Vector2> map, SpriteCache spriteCache) {
        this.spriteCache = spriteCache;
        insideWalls = map.stream()
                .filter(vector2 -> map.stream()
                        .filter(vector21 -> ((vector21.y + 1f == vector2.y || vector21.y - 1f == vector2.y || vector21.y == vector2.y) && (vector21.x + 1f == vector2.x || vector21.x - 1f == vector2.x || vector21.x == vector2.x)))
                        .count() == 9L)
                .toList();
        outsideWalls = map;
        outsideWalls.removeAll(insideWalls);

    }

    public void render() {
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
        for (int i = 0; i < insideWalls.size(); i++) {
            // Draw wall
            spriteBatch.add(textureRegion, insideWalls.get(i).x - (textureHeight / SCALE / 2), insideWalls.get(i).y - (textureHeight / SCALE / 2), textureHeight / SCALE, textureHeight / SCALE);

        }

    }






}
