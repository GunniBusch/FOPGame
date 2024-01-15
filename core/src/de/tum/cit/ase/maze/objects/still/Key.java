package de.tum.cit.ase.maze.objects.still;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.World;
import de.tum.cit.ase.maze.objects.GameElement;
import de.tum.cit.ase.maze.screens.GameScreen;

public class Key extends GameElement {
    private Vector2 position;
    private boolean isCollected;

    public Key(World world, Vector2 position, GameScreen game) {
        BodyDef def = new BodyDef();
        def.type = BodyDef.BodyType.StaticBody;
        def.fixedRotation = true;
        body = world.createBody(def);
        body.setAwake(true);
        createBody(position);
    }

    private void createBody(Vector2 position) {

    }
    @Override
    public void render(SpriteBatch spriteBatch) {

    }

    @Override
    public void update(float deltaTime) {

    }

    @Override
    public void dispose() {

    }
}
