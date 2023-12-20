package de.tum.cit.ase.maze;

import com.badlogic.gdx.*;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.World;
import de.tum.cit.ase.maze.Input.GameInputProcessor;
import de.tum.cit.ase.maze.Input.ListenerClass;
import de.tum.cit.ase.maze.map.path.Node;
import de.tum.cit.ase.maze.objects.dynamic.Enemy;
import de.tum.cit.ase.maze.objects.dynamic.Player;
import de.tum.cit.ase.maze.utils.MapLoader;

import java.util.ArrayList;
import java.util.List;

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
    private final List<Enemy> mobs = new ArrayList<>();
    private World world;
    private Box2DDebugRenderer b2DDr;
    private MapLoader mapLoader;
    private ShapeRenderer shapeRenderer;

    //ToDo Check what viewport does and if we need it.

    /**
     * Constructor for GameScreen. Sets up the camera and font.
     *
     * @param game The main game class, used to access global resources and methods.
     */
    public GameScreen(MazeRunnerGame game) {
        this.game = game;
        this.world = new World(new Vector2(0, 0), true);
        world.setContactListener(new ListenerClass());
        this.player = new Player(world, 0f, 20f * PPM * 2f);

        this.b2DDr = new Box2DDebugRenderer(true, true, false, true, true, true);
        this.inputAdapter = new GameInputProcessor(game, player);
        mapLoader = new MapLoader(world, game.getSpriteBatch());
        this.mobs.add(new Enemy(world, this.mapLoader.getWallList(), 10f * PPM * 2f, 1f * PPM * 2f));
        this.mobs.add(new Enemy(world, this.mapLoader.getWallList(), 25f * PPM * 2f, 2f * PPM * 2f));
        this.mobs.add(new Enemy(world, this.mapLoader.getWallList(), 30f * PPM * 2f, 1f * PPM * 2f));
        this.mobs.forEach(enemy -> enemy.setPlayer(player));

        // Create and configure the camera for the game view
        this.shapeRenderer = new ShapeRenderer();
        camera = new OrthographicCamera();
        camera.setToOrtho(false, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        // ToDo: Make Global
        float zoom = 0.9f;
        camera.zoom = zoom;
        hudCamera = new OrthographicCamera();
        hudCamera.setToOrtho(false, Gdx.graphics.getWidth() / SCALE, Gdx.graphics.getHeight() / SCALE);
        hudCamera.zoom = 2f;
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
        b2DDr.render(world, camera.combined.cpy().scl(PPM));

        //ScreenUtils.clear(0, 0, 0, 1); // Clear the screen

        // Todo Rendercalls before and after loop not in loop
        // Set up and begin drawing with the sprite batch
        for (Enemy mob : mobs) {
            var points = mob.al.stream()
                    .map(Node::getPosition)
                    .toList();

            // ToDo: Refactor debug path show
            // Draws the enemy path
            shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
            shapeRenderer.setColor(0.f, 1, 0f, 1f);
            for (int i = 0; i < points.size() - 2; ++i) {
                var o = points.get(i).cpy().scl(PPM);
                var p = points.get(i + 1).cpy().scl(PPM);
                shapeRenderer.line(o.x * 2f, o.y * 2f, p.x * 2f, p.y * 2f);
            }
            if (points.size() > 1) {
                shapeRenderer.setColor(0.1f, 0.453f, 1f, 1f);
                shapeRenderer.line(points.get(points.size() - 2).cpy().scl(PPM).scl(2), points.get(points.size() - 1).cpy().scl(PPM).scl(2));
            }
            shapeRenderer.end();
            mob.render(this.game.getSpriteBatch());
        }



        mapLoader.render(delta);
        Gdx.app.log("MX", game.getSpriteBatch().maxSpritesInBatch + " : " +  this.game.getSpriteBatch().renderCalls);


        game.getSpriteBatch().begin(); // Important to call this before drawing anything


        game.getSpriteBatch().draw(
                this.player.getTexture(),
                this.player.getPosition().x * PPM - (this.player.getTexture().getRegionWidth() / 2f),
                this.player.getPosition().y * PPM - (this.player.getTexture().getRegionHeight() / 2f)

        );

        game.getSpriteBatch().end(); // Important to call this after drawing everything

        this.renderHud(delta);

    }

    /**
     * Invokes all update methods for e.g. a player.
     *
     * @param dt Time in seconds since the last render.
     */
    private void update(float dt) {
        this.world.step(1 / 60f, 6, 2);
        this.player.update(dt);
        this.mobs.forEach(mob -> mob.update(dt));
        this.cameraUpdate(dt);

        game.getSpriteBatch().setProjectionMatrix(camera.combined);
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
        Vector3 position = camera.position;
        //Have player centered on camera.
        position.x = player.getPosition().x * PPM;
        position.y = player.getPosition().y * PPM;
        camera.position.set(position);
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
        this.mobs.forEach(Enemy::dispose);
        this.b2DDr.dispose();
        this.world.dispose();

    }
}
