package de.tum.cit.ase.maze.screens;

import box2dLight.RayHandler;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
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
    private final Texture background;
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
    private final int mapCacheID, backgroundCacheId;
    private final DeathListener deathListener;
    private final Hud hud;
    private boolean victory = false;
    private boolean end = false;
    private final float zoom = .9f;
    private Vector3 target;
    private final Wall wall;

    private final RayHandler rayHandler;
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
        RayHandler.setGammaCorrection(true);

        this.rayHandler = new RayHandler(world);
        float val = 0.1f;
        rayHandler.setAmbientLight(new Color(val, val, val, 0.4f));

        RayHandler.useDiffuseLight(true);
        rayHandler.setBlurNum(10);
        world.setContactListener(new ListenerClass());


        //Gdx.gl.glEnable(GL20.GL_BLEND);
        this.b2DDr = new Box2DDebugRenderer(true, true, false, true, true, true);
        MapLoader.loadMapFile(Gdx.files.internal("level-3.properties"));
        wall = new Wall(MapLoader.getMapCoordinates(ObjectType.Wall), game.getSpriteCache(), world);

        var playerCord = MapLoader.getMapCoordinates(ObjectType.EntryPoint).get(0).cpy();
        this.player = new Player(world, deathListener, rayHandler, playerCord.scl(PPM).scl(2f));
        this.entities.add(player);
        this.inputAdapter = new GameInputProcessor(game, player);
        this.background = new Texture("StoneFloorTexture.png");
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

        var numW = Math.ceil((double) MapLoader.width * PPM * SCALE / background.getWidth());
        var numH = Math.ceil((double) MapLoader.height * PPM * SCALE / background.getHeight());

        this.game.getSpriteCache().beginCache();
        for (int i = 0; i < numH; i++) {
            for (int j = 0; j < numW; j++) {
                this.game.getSpriteCache().add(background, j * background.getWidth() - 1 * PPM, i * background.getHeight() - 1 * PPM);

            }
        }

        backgroundCacheId = game.getSpriteCache().endCache();


    }


    // Screen interface methods with necessary functionality
    @Override
    public void render(float delta) {
        // Check for escape key press to go back to the menu
        this.update(delta);
        ScreenUtils.clear(0, 0, 0, 1, true);
        //viewport.apply(false);
        game.getSpriteCache().begin();
        game.getSpriteCache().draw(backgroundCacheId);
        game.getSpriteCache().draw(mapCacheID);
        game.getSpriteCache().end();

        if (DEBUG) b2DDr.render(world, camera.combined.cpy().scl(PPM));

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


        rayHandler.render();


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
        rayHandler.update();
        this.entities.parallelStream().forEach(entity -> entity.update(dt));
        hud.update(dt);
        this.cameraUpdate(dt);
        game.getSpriteBatch().setProjectionMatrix(camera.combined);

        game.getSpriteCache().setProjectionMatrix(camera.combined);


        shapeRenderer.setProjectionMatrix(camera.combined);
        float viewX = zoom * (camera.viewportWidth / 2);
        float viewY = zoom * (camera.viewportHeight / 2);
        Vector3 pos = camera.position.cpy().scl(PPM);
        //rayHandler.setCombinedMatrix(camera.combined.cpy().scl(PPM), pos.x, pos.y, viewX, viewY);
        rayHandler.setCombinedMatrix(camera.combined.cpy().scl(PPM));
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
        float extraSize = 0.5f; // 2 meters extra in both width and height
        float mapStartX = -1*PPM; // X-coordinate where your map starts
        float mapStartY = -1*PPM; // Y-coordinate where your map starts
        float viewX = zoom * (camera.viewportWidth / 2);
        float viewY = zoom * (camera.viewportHeight / 2);

// Adjust for map start coordinates and extra size
        if (position.x < viewX + mapStartX) {
            position.x = viewX + mapStartX;
        }
        if (position.y < viewY + mapStartY) {
            position.y = viewY + mapStartY;
        }

// Adjust the width and height calculations
        float adjustedWidth = MapLoader.width + extraSize;
        float adjustedHeight = MapLoader.height + extraSize;
        float w = (adjustedWidth * PPM * SCALE - mapStartX) - viewX * 2;
        float h = (adjustedHeight * PPM * SCALE - mapStartY) - viewY * 2;

// Right and top boundary checks with map start position and extra size
        if (position.x > viewX + w + mapStartX) {
            position.x = viewX + w + mapStartX;
        }
        if (position.y > viewY + h + mapStartY) {
            position.y = viewY + h + mapStartY;
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
        rayHandler.dispose();
        background.dispose();


    }
}
