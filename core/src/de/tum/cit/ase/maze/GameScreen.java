package de.tum.cit.ase.maze;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.World;
import de.tum.cit.ase.maze.Input.GameInputProcessor;
import de.tum.cit.ase.maze.Input.ListenerClass;
import de.tum.cit.ase.maze.objects.GameElement;
import de.tum.cit.ase.maze.objects.dynamic.Enemy;
import de.tum.cit.ase.maze.objects.dynamic.Player;
import de.tum.cit.ase.maze.utils.MapLoader;

import java.util.ArrayList;
import java.util.List;

import static de.tum.cit.ase.maze.utils.CONSTANTS.DEBUG;
import static de.tum.cit.ase.maze.utils.CONSTANTS.PPM;

/**
 * The GameScreen class is responsible for rendering the gameplay screen.
 * It handles the game logic and rendering of the game elements.
 */
public class GameScreen implements Screen {

    private final MazeRunnerGame game;
    private final OrthographicCamera camera;
    private final OrthographicCamera hudCamera;
    private final float SCALE = 1f;
    private final BitmapFont font;
    private final Player player;
    private final InputAdapter inputAdapter;
    private final List<GameElement> entities;
    private final World world;
    private final Box2DDebugRenderer b2DDr;
    private final ShapeRenderer shapeRenderer;
    private final int mapCacheID;

    //added boolean pause, for pause functionality
    private boolean paused;

    //ToDo Check what viewport does and if we need it.

    /**
     * Constructor for GameScreen. Sets up the camera and font.
     *
     * @param game The main game class, used to access global resources and methods.
     */
    public GameScreen(MazeRunnerGame game) {
        this.game = game;
        this.entities = new ArrayList<>();
        this.world = new World(new Vector2(0, 0), true);
        world.setContactListener(new ListenerClass());
        this.player = new Player(world, 0f, 20f * PPM * 2f);
        this.entities.add(player);

        //Gdx.gl.glEnable(GL20.GL_BLEND);
        this.b2DDr = new Box2DDebugRenderer(true, true, false, true, true, true);
        this.inputAdapter = new GameInputProcessor(game, player);
        MapLoader mapLoader = new MapLoader(world, game.getSpriteCache());

        this.entities.add(new Enemy(world, mapLoader.getWallList(), player, 10f * PPM * 2f, 1f * PPM * 2f));
        this.entities.add(new Enemy(world, mapLoader.getWallList(), player, 25f * PPM * 2f, 2f * PPM * 2f));
        this.entities.add(new Enemy(world, mapLoader.getWallList(), player, 30f * PPM * 2f, 1f * PPM * 2f));

        // Create and configure the camera for the game view
        this.shapeRenderer = new ShapeRenderer();
        camera = new OrthographicCamera();
        camera.setToOrtho(false, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        // ToDo: Make Global
        camera.zoom = 6f;
        hudCamera = new OrthographicCamera();
        hudCamera.setToOrtho(false, Gdx.graphics.getWidth() / SCALE, Gdx.graphics.getHeight() / SCALE);
        hudCamera.zoom = 2f;
        this.game.getSpriteBatch().setProjectionMatrix(camera.combined);
        game.getSpriteCache().setProjectionMatrix(camera.combined);
        game.getSpriteCache().beginCache();
        mapLoader.render(0f);
        mapCacheID = game.getSpriteCache().endCache();


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
        b2DDr.render(world, camera.combined.cpy().scl(PPM));

        //ScreenUtils.clear(0, 0, 0, 1); // Clear the screen

        // Set up and begin drawing with the sprite batch
        game.getSpriteBatch().begin();
        entities.forEach(entity -> entity.render(this.game.getSpriteBatch()));
        game.getSpriteBatch().end();

        if (DEBUG) {
            var enemies = entities.parallelStream()
                    .filter(gameElement -> gameElement instanceof Enemy).map(gameElement -> (Enemy) gameElement).toList();
            shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
            for (Enemy mob : enemies) {
                shapeRenderer.setColor(0.f, 1, 0f, 1f);
                for (int i = 0; i < mob.getPath().size() - 2; ++i) {
                    var o = mob.getPath().get(i).cpy().scl(PPM);
                    var p = mob.getPath().get(i + 1).cpy().scl(PPM);
                    shapeRenderer.line(o.x * 2f, o.y * 2f, p.x * 2f, p.y * 2f);
                }
                if (mob.getPath().size() > 1) {
                    shapeRenderer.setColor(0.1f, 0.453f, 1f, 1f);
                    shapeRenderer.line(mob.getPath().get(mob.getPath().size() - 2).cpy().scl(PPM).scl(2), mob.getPath().get(mob.getPath().size() - 1).cpy().scl(PPM).scl(2));
                }

            }
            shapeRenderer.end();
        }


        game.getSpriteCache().begin();
        game.getSpriteCache().draw(mapCacheID);
        game.getSpriteCache().end();


        this.renderHud(delta);

    }

    /**
     * Invokes all update methods for e.g. a player.
     *
     * @param dt Time in seconds since the last render.
     */
    private void update(float dt) {
        this.world.step(1 / 60f, 6, 2);
        this.entities.parallelStream().forEach(entity -> entity.update(dt));
        this.cameraUpdate(dt);

        game.getSpriteBatch().setProjectionMatrix(camera.combined);

        game.getSpriteCache().setProjectionMatrix(camera.combined);

        shapeRenderer.setProjectionMatrix(camera.combined);

    }

    /**
     * Renders the HUD
     *
     * @param dt
     */
    private void renderHud(float dt) {
        game.getSpriteBatch().setProjectionMatrix(hudCamera.combined);
        game.getSpriteBatch().begin();
        font.draw(game.getSpriteBatch(), "FPS: " + Gdx.graphics.getFramesPerSecond(), 20, hudCamera.viewportHeight * hudCamera.zoom - 10);
        game.getSpriteBatch().end();
    }

    /**
     * Updates the camera.
     *
     * @param dt Time since last frame.
     */
    private void cameraUpdate(float dt) {


        if (!camera.frustum.pointInFrustum(new Vector3(this.player.getPosition().cpy().scl(PPM), 0))) {

            Vector3 position = camera.position;
            //Have player centered on camera.
            position.x = player.getPosition().x * PPM;
            position.y = player.getPosition().y * PPM;
            camera.position.set(position);

        }

        camera.update();
        hudCamera.update();
    }

    @Override
    public void resize(int width, int height) {
        camera.setToOrtho(false, width / SCALE, height / SCALE);
        hudCamera.setToOrtho(false, width / SCALE, height / SCALE);
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
        this.entities.forEach(GameElement::dispose);
        this.b2DDr.dispose();
        this.world.dispose();


    }
}
