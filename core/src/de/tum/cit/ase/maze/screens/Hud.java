package de.tum.cit.ase.maze.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import de.tum.cit.ase.maze.objects.dynamic.Player;

import static de.tum.cit.ase.maze.utils.CONSTANTS.DEBUG;
import static de.tum.cit.ase.maze.utils.CONSTANTS.PLAYER_MAX_HEALTH;

/**
 * Class that represents a game HUD, that shows information to the player like health.
 */
public class Hud implements Disposable {
    /**
     * Stage to show HUD
     */
    private final Stage stage;
    private final Skin skin;
    private final Label fps;
    private final Player player;
    private final ProgressBar healthBar;
    private final ProgressBar keyBar;

    /**
     * Creates a new HUD
     *
     * @param hudCamera   {@link OrthographicCamera} that looks at the HUD
     * @param spriteBatch {@link SpriteBatch} that renders the HUD
     * @param player      The {@link Player} information has to be displayed
     */
    public Hud(OrthographicCamera hudCamera, SpriteBatch spriteBatch, Player player) {
        //this.viewport = new FitViewport(Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), hudCamera);

        this.player = player;
        hudCamera.zoom = 1.01f;
        hudCamera.setToOrtho(false);

        this.stage = new Stage(new ScreenViewport(hudCamera), spriteBatch);
        this.skin = new Skin(Gdx.files.internal("craft/craftacular-ui.json"));

        // Top Table:
        Table table = new Table();
        table.align(Align.topLeft);
        table.setFillParent(true);

        fps = new Label("60", skin);
        fps.setVisible(DEBUG);


        table.add(fps);
        stage.addActor(table);

        // Bottom Table:
        table = new Table();
        table.align(Align.bottomLeft);
        table.setFillParent(true);

        Label label = new Label("Health: ", skin);
        table.add(label).spaceRight(10.0f).align(Align.left);

        healthBar = new ProgressBar(0.0f, PLAYER_MAX_HEALTH, 1.0f, false, skin, "health");
        table.add(healthBar).align(Align.left).width(18f * PLAYER_MAX_HEALTH);

        table.row();

        label = new Label("Keys: ", skin);
        label.setName("key-lable");
        table.add(label).align(Align.left);

        keyBar = new ProgressBar(0.0f, 2, 1.0f, false, skin, "health");
        table.add(keyBar).align(Align.left).width(36.0f);

        stage.addActor(table);
        //Debug stuff
        stage.setDebugAll(DEBUG);
    }

    /**
     * Called when windows is resized
     */
    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
    }

    /**
     * Updates the HUD
     */
    public void update(float dt) {
        this.fps.setText(Gdx.graphics.getFramesPerSecond());
        healthBar.setValue(player.getHealth());
        this.stage.act(dt);

    }

    /**
     * Renders the HUD
     */
    public void render() {
        this.stage.getViewport().apply(true);
        this.stage.draw();

    }

    @Override
    public void dispose() {
        this.stage.dispose();
        this.skin.dispose();

    }
}
