package de.tum.cit.ase.maze.objects.still;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.EdgeShape;
import com.badlogic.gdx.physics.box2d.World;
import de.tum.cit.ase.maze.objects.GameElement;
import de.tum.cit.ase.maze.screens.GameScreen;
import de.tum.cit.ase.maze.utils.MapLoader;

import static de.tum.cit.ase.maze.utils.CONSTANTS.DEBUG;
import static de.tum.cit.ase.maze.utils.CONSTANTS.SCALE;

/**
 * Defines the entry of the game. Cant be passed.
 */
public class Entry extends GameElement {


    public Entry(World world, Vector2 position, GameScreen game) {
        super();
        this.world = world;
        BodyDef def = new BodyDef();
        def.type = BodyDef.BodyType.StaticBody;
        def.fixedRotation = true;
        body = world.createBody(def);
        body.setAwake(true);
        // If in dev modem, allow to move out of the game world.
        if (DEBUG) body.setActive(false);
        createBody(position);
    }

    @Override
    public void render(SpriteBatch spriteBatch) {
    }

    @Override
    public void update(float deltaTime) {
    }


    private void createBody(Vector2 position) {
        float x, y;
        var halfSize = 1f;
        EdgeShape shape = new EdgeShape();
        x = position.x * SCALE;
        y = position.y * SCALE;

        var p = position.cpy().sub(new Vector2(MapLoader.width, MapLoader.height).scl(0.5f));
        Vector2[] corners;
        if (MathUtils.round(Math.abs(p.x)) > MathUtils.round(Math.abs(p.y))) {
            // Side

            corners = new Vector2[]{
                    new Vector2(x, y + halfSize), // top
                    new Vector2(x, y - halfSize), // bottom
            };
            if (p.x > 0) {
                for (Vector2 corner : corners) {
                    corner.add(halfSize, 0);
                }


            } else {
                for (Vector2 corner : corners) {
                    corner.sub(halfSize, 0);
                }
            }
        } else {

            // Tops

            corners = new Vector2[]{
                    new Vector2(x + halfSize, y), // right
                    new Vector2(x - halfSize, y), // left
            };
            if (p.y > 0) {
                for (Vector2 corner : corners) {
                    corner.add(0, halfSize);
                }
            } else {
                for (Vector2 corner : corners) {
                    corner.sub(0, halfSize);
                }
            }

        }

        shape.set(corners[0], corners[1]);
        body.createFixture(shape, 0f);
        shape.dispose();
        Gdx.app.debug("Entry Pos", "Bdd: " + corners[0] + ", " + corners[1] + " : " + position);


    }


    @Override
    public void dispose() {
    }
}
