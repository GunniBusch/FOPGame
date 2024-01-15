package de.tum.cit.ase.maze.objects.still;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.physics.box2d.CircleShape;
import de.tum.cit.ase.maze.objects.GameElement;


public class Key extends GameElement {
    private Vector2 position;
    private boolean isCollected;
    private TextureRegion textureRegion;

    public Key(World world, Vector2 position) {
        isCollected = false;
        BodyDef def = new BodyDef();
        def.type = BodyDef.BodyType.StaticBody;
        def.fixedRotation = true;
        body = world.createBody(def);
        body.setAwake(true);
        createBody(position);

        texture = new Texture("gameKey.png");
        textureRegion = new TextureRegion(texture);

    }

    private void createBody(Vector2 position) {
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.StaticBody;
        bodyDef.position.set(position.x, position.y);

        body = world.createBody(bodyDef);

        CircleShape shape = new CircleShape();
        //TODO adjust radius
        shape.setRadius(1f);

        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = shape;

        body.createFixture(fixtureDef);

        shape.dispose();
    }

    public void collectKey() {
        isCollected = true;
        textureRegion.getTexture().dispose();
        world.destroyBody(body);
        //TODO update HUD
    }
    @Override
    public void render(SpriteBatch spriteBatch) {
        spriteBatch.draw(textureRegion, position.x, position.y);
    }

    @Override
    public void update(float deltaTime) {

    }

    @Override
    public void dispose() {
        //textureRegion.getTexture().dispose();
        //world.destroyBody(body);
    }
}
