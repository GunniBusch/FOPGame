package de.tum.cit.ase.maze.objects.dynamic;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class SwordSwingAnimation extends ApplicationAdapter {
    private SpriteBatch batch;
    private AssetManager assetManager;
    private TextureRegion[] playerFrames;
    private Animation<TextureRegion> swordSwingAnimation;
    private float stateTime;
    private boolean isAttacking;

    @Override
    public void create() {
        batch = new SpriteBatch();
        assetManager = new AssetManager();
        isAttacking = false;

        // Load animation frames as textures (adjust the paths as needed).
        assetManager.load("ani1.png", Texture.class);
        assetManager.load("ani2.png", Texture.class);
        assetManager.load("ani3.png", Texture.class);
        assetManager.finishLoading();

        // Create an array of TextureRegions from the loaded textures.
        playerFrames = new TextureRegion[3];
        playerFrames[0] = new TextureRegion(assetManager.get("sword_swing_frame1.png", Texture.class));
        playerFrames[1] = new TextureRegion(assetManager.get("sword_swing_frame2.png", Texture.class));
        playerFrames[2] = new TextureRegion(assetManager.get("sword_swing_frame3.png", Texture.class));

        // Create the sword swing animation.
        swordSwingAnimation = new Animation<>(0.1f, playerFrames);
        stateTime = 0f;
    }

    @Override
    public void render() {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        // Update the animation time if the player is attacking.
        if (isAttacking) {
            stateTime += Gdx.graphics.getDeltaTime();
        }

        batch.begin();

        // Render the sword swing animation at the player's position.
        TextureRegion currentFrame = swordSwingAnimation.getKeyFrame(stateTime, true);
        batch.draw(currentFrame, 100, 100); // Adjust the position as needed.

        batch.end();

        // Simulate an attack when the user presses a key.
        if (Gdx.input.isKeyPressed(Input.Keys.E)) {
            isAttacking = true;
            stateTime = 0f; // Start the animation from the beginning.
        }
    }

    @Override
    public void dispose() {
        batch.dispose();
        assetManager.dispose();
    }
}

