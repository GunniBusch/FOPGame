package de.tum.cit.ase.maze;

import com.badlogic.gdx.*;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.World;
import de.tum.cit.ase.maze.Input.GameInputProcessor;
import de.tum.cit.ase.maze.objects.dynamic.Player;
import de.tum.cit.ase.maze.utils.MapLoader;

import static de.tum.cit.ase.maze.utils.CONSTANTS.PPM;

/**
 * The GameScreen class is responsible for rendering the gameplay screen.
 * It handles the game logic and rendering of the game elements.
 */
public class GameScreen implements Screen {

    private final MazeRunnerGame game;
    private final OrthographicCamera camera;

    private final float SCALE = 2f;
    private final BitmapFont font;
    private final Player player;
    private final InputAdapter inputAdapter;
    private World world;
    private Box2DDebugRenderer b2DDr;
    private MapLoader mapLoader;

    /**
     * Constructor for GameScreen. Sets up the camera and font.
     *
     * @param game The main game class, used to access global resources and methods.
     */
    public GameScreen(MazeRunnerGame game) {
        this.game = game;
        this.world = new World(new Vector2(0, 0), false);
        this.player = new Player(world);
        this.b2DDr = new Box2DDebugRenderer(true, true, false, true, true, true);
        this.inputAdapter = new GameInputProcessor(game, player);
        mapLoader = new MapLoader(world, game.getSpriteBatch());


        // Create and configure the camera for the game view
        camera = new OrthographicCamera();
        camera.setToOrtho(false, Gdx.graphics.getWidth() / SCALE, Gdx.graphics.getHeight() / SCALE);
        camera.zoom = 0.75f;
        this.game.getSpriteBatch().setProjectionMatrix(camera.combined);

        // Get the font from the game's skin
        font = game.getSkin().getFont("font");

    }


    // Screen interface methods with necessary functionality
    @Override
    public void render(float delta) {
        // Check for escape key press to go back to the menu
        this.update(delta);
        Gdx.gl.glClearColor(0, 0, 0, 1f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        //ScreenUtils.clear(0, 0, 0, 1); // Clear the screen
        b2DDr.render(world, camera.combined.scl(PPM));


        ; // Update the camera
        // Set up and begin drawing with the sprite batch


        mapLoader.render(delta);
        game.getSpriteBatch().begin(); // Important to call this before drawing anything

        game.getSpriteBatch().draw(
                this.player.getTexture(),
                this.player.getPosition().x * PPM - (this.player.getTexture().getRegionWidth() / 2f),
                this.player.getPosition().y * PPM - (this.player.getTexture().getRegionHeight() / 2f)

        );


        game.getSpriteBatch().end(); // Important to call this after drawing everything
    }

    /**
     * Invokes all update methods for e.g. a player.
     *
     * @param dt Time in seconds since the last render.
     */
    private void update(float dt) {
        this.world.step(1 / 60f, 6, 2);
        this.player.update(dt);
        this.cameraUpdate(dt);
        game.getSpriteBatch().setProjectionMatrix(camera.combined);

    }

    /**
     * Updates the camera.
     *
     * @param dt Time since last frame.
     */
    private void cameraUpdate(float dt) {
        Vector3 position = camera.position;
        //Have player centered on camera.
        position.x = player.getPosition().x * PPM;
        position.y = player.getPosition().y * PPM;
        camera.position.set(position);
        camera.update();
    }

    @Override
    public void resize(int width, int height) {
        camera.setToOrtho(false, width / SCALE, height / SCALE);
    }

    @Override
    public void pause() {
    }

    @Override
    public void resume() {
    }

    @Override
    public void show() {
        Gdx.input.setInputProcessor(this.inputAdapter);

    }

    @Override
    public void hide() {
    }

    @Override
    public void dispose() {
        this.player.dispose();
        this.b2DDr.dispose();
        this.world.dispose();
    }

    // Additional methods and logic can be added as needed for the game screen
}
