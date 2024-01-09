package de.tum.cit.ase.maze.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import de.tum.cit.ase.maze.Input.DeathListener;
import de.tum.cit.ase.maze.Input.GameInputProcessor;
import de.tum.cit.ase.maze.Input.ListenerClass;
import de.tum.cit.ase.maze.MazeRunnerGame;
import de.tum.cit.ase.maze.objects.GameElement;
import de.tum.cit.ase.maze.objects.ObjectType;
import de.tum.cit.ase.maze.objects.dynamic.Enemy;
import de.tum.cit.ase.maze.objects.dynamic.Player;
import de.tum.cit.ase.maze.objects.still.Entry;
import de.tum.cit.ase.maze.objects.still.Exit;
import de.tum.cit.ase.maze.objects.still.Wall;
import de.tum.cit.ase.maze.utils.MapLoader;

import java.util.ArrayList;
import java.util.List;

import static de.tum.cit.ase.maze.utils.CONSTANTS.*;

/**
 * The GameScreen class is responsible for rendering the gameplay screen.
 * It handles the game logic and rendering of the game elements.
 */
public class GameScreen implements Screen {

    private final MazeRunnerGame game;
    private final OrthographicCamera camera;
    private final OrthographicCamera hudCamera;
    private final Viewport viewport;
    private final BitmapFont font;
    private final Player player;
    private final InputAdapter inputAdapter;
    private final List<GameElement> entities;
    private final World world;
    private final Box2DDebugRenderer b2DDr;
    private final ShapeRenderer shapeRenderer;
    private final int mapCacheID;
    private final DeathListener deathListener;
    private final Hud hud;
    private boolean victory = false;
    private boolean end = false;
    private final float zoom = 0.9f;
    private Vector3 target;
    private final Wall wall;

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
        this.deathListener = new DeathListener(this);
        this.entities = new ArrayList<>();
        this.world = new World(new Vector2(0, 0), true);
        world.setContactListener(new ListenerClass());

        //Gdx.gl.glEnable(GL20.GL_BLEND);
        this.b2DDr = new Box2DDebugRenderer(true, true, false, true, true, true);
        MapLoader.loadMapFile(Gdx.files.internal("level-3.properties"));
        wall = new Wall(MapLoader.getMapCoordinates(ObjectType.Wall), game.getSpriteCache(), world);

        var playerCord = MapLoader.getMapCoordinates(ObjectType.EntryPoint).get(0);
        this.player = new Player(world, deathListener, playerCord.scl(PPM ).scl(2f));
        this.entities.add(player);
        this.inputAdapter = new GameInputProcessor(game, player);

        this.spawnEntities();
        // Create and configure the camera for the game view
        this.shapeRenderer = new ShapeRenderer();
        camera = new OrthographicCamera();
        camera.setToOrtho(false, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

        camera.position.set(playerCord.cpy().scl(PPM).scl(2f), 0);
        camera.zoom = zoom;
        this.viewport = new ScreenViewport(camera);
        target = new Vector3(camera.position.cpy());
        camera.position.set(target);

        hudCamera = new OrthographicCamera();
        this.hud = new Hud(hudCamera, this.game.getSpriteBatch(), player);

        this.game.getSpriteBatch().setProjectionMatrix(camera.combined);
        game.getSpriteCache().setProjectionMatrix(camera.combined);
        game.getSpriteCache().beginCache();
        wall.render();
        mapCacheID = game.getSpriteCache().endCache();


        // Get the font from the game's skin
        font = game.getSkin().getFont("font");


    }


    // Screen interface methods with necessary functionality
    @Override
    public void render(float delta) {
        // Check for escape key press to go back to the menu
        this.update(delta);
        ScreenUtils.clear(0, 0, 0, 1, true);
        //viewport.apply(false);

        b2DDr.render(world, camera.combined.cpy().scl(PPM));

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
        if (end) {
            this.game.goToMenu();
        }

    }

    /**
     * Invokes all update methods for e.g. a player.
     *
     * @param dt Time in seconds since the last render.
     */
    private void update(float dt) {
        this.world.step(1 / 60f, 6, 2);
        this.entities.parallelStream().forEach(entity -> entity.update(dt));
        hud.update(dt);
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
        hud.render();
    }

    /**
     * Updates the camera.
     *
     * @param dt Time since last frame.
     */
    private void cameraUpdate(float dt) {


        if (!camera.frustum.pointInFrustum(new Vector3(this.player.getPosition().cpy().scl(PPM), 0))) {

            target = new Vector3(player.getPosition().cpy().scl(PPM), 0);

        }
        //camera.position.slerp(target, .1f);
        camera.position.interpolate(target, .2f, Interpolation.smoother);

        Vector3 position = camera.position;
        float viewX = zoom * (camera.viewportWidth / 2);
        float viewY = zoom * (camera.viewportHeight / 2);
        if (position.x < viewX) {
            position.x = viewX;
        }
        if (position.y < viewY) {
            position.y = viewY;
        }
        float w = MapLoader.width * PPM * SCALE - viewX * 2;
        if (position.x > viewX + w) {
            position.x = viewX + w;
        }
        float h = MapLoader.height * PPM * SCALE - viewY * 2;
        if (position.y > viewY + h) {
            position.y = viewY + h;
        }
        camera.position.set(position);


        viewport.apply();

    }

    public void handleEndOfGame(boolean victory) {
        this.end = true;
        this.victory = victory;
    }

    private void spawnEntities() {


        for (Vector2 enemyCord : MapLoader.getMapCoordinates(ObjectType.Enemy)) {
            var scaledEnemyCord = enemyCord.cpy().scl(PPM).scl(2f);
            this.entities.add(new Enemy(world, deathListener, player, scaledEnemyCord));
        }
        this.entities.add(new Exit(world, MapLoader.getMapCoordinates(ObjectType.Exit).get(0), this));
        new Entry(world, MapLoader.getMapCoordinates(ObjectType.EntryPoint).get(0), this);


    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height);
        //camera.setToOrtho(false, width / SCALE, height / SCALE);
        //hudCamera.setToOrtho(false, width / SCALE, height / SCALE);
        hud.resize(width, height);
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
        this.hud.dispose();


    }
}
