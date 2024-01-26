package de.tum.cit.ase.maze.screens;

import box2dLight.RayHandler;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.MathUtils;
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
import de.tum.cit.ase.maze.objects.CollectableManager;
import de.tum.cit.ase.maze.objects.GameElement;
import de.tum.cit.ase.maze.objects.ObjectType;
import de.tum.cit.ase.maze.objects.dynamic.Enemy;
import de.tum.cit.ase.maze.objects.dynamic.Player;
import de.tum.cit.ase.maze.objects.still.*;
import de.tum.cit.ase.maze.objects.still.collectable.DamageDeflect;
import de.tum.cit.ase.maze.objects.still.collectable.HealthCollectable;
import de.tum.cit.ase.maze.objects.still.collectable.SpeedBoost;
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
    private final InputMultiplexer inputMultiplexer;
    private final InputAdapter inputAdapter;
    private final List<GameElement> entities;
    private final World world;
    private final Box2DDebugRenderer b2DDr;
    private final ShapeRenderer shapeRenderer;
    private final int mapCacheID, backgroundCacheId;
    private final DeathListener deathListener;
    private final Hud hud;
    private final float zoom = .9f;
    private final Wall wall;
    private final CollectableManager collectableManager;
    private final RayHandler rayHandler;
    private final Vector3 target;
    private boolean victory = false;
    private boolean end = false;
    //added boolean pause, for pause functionality
    private float stateTime = 0f;

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
        float val = 0.14f;
        rayHandler.setShadows(true);
        rayHandler.setAmbientLight(new Color(val, val, val, 0.4f));

        RayHandler.useDiffuseLight(true);
        rayHandler.setBlurNum(10);
        world.setContactListener(new ListenerClass());


        //Gdx.gl.glEnable(GL20.GL_BLEND);
        this.b2DDr = new Box2DDebugRenderer(true, true, false, true, true, true);
        //MapLoader.loadMapFile(Gdx.files.internal("level-1.properties"));
        wall = new Wall(MapLoader.getMapCoordinates(ObjectType.Wall), game.getSpriteCache(), world);

        var playerCord = MapLoader.getMapCoordinates(ObjectType.EntryPoint).get(0).cpy();
        this.player = new Player(world, deathListener, rayHandler, playerCord.scl(PPM).scl(2f));
        // To debug no damage
        if (DEBUG) player.markAsFinished();
        this.entities.add(player);
        this.collectableManager = new CollectableManager(world, rayHandler, true);
        spawnCollectables();
        this.background = new Texture("StoneFloorTexture.png");
        this.spawnEntities();
        // Create and configure the camera for the game view
        this.shapeRenderer = new ShapeRenderer();
        camera = new OrthographicCamera();
        camera.setToOrtho(false, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

        camera.position.set(playerCord.cpy().scl(PPM).scl(2f), 0);
        camera.zoom = zoom;
        this.viewport = new ScreenViewport(camera);
        target = new Vector3(player.getPosition(), 0).scl(PPM);
        camera.position.set(target);
        viewport.apply(false);

        hudCamera = new OrthographicCamera();
        this.hud = new Hud(hudCamera, this.game.getSpriteBatch(), player, this, true);
        this.inputAdapter = new GameInputProcessor(game, player);
        this.inputMultiplexer = new InputMultiplexer();
        this.inputMultiplexer.addProcessor(hud.getStage());
        this.inputMultiplexer.addProcessor(this.inputAdapter);

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

    private void spawnCollectables() {
        collectableManager.spawn(HealthCollectable.class, 0.01f);
        collectableManager.spawn(SpeedBoost.class, 0.01f);
        collectableManager.spawn(DamageDeflect.class, 0.008f);
        collectableManager.spawn(Key.class, MapLoader.getMapCoordinates(ObjectType.Key));
        collectableManager.spawn(Traps.class, MapLoader.getMapCoordinates(ObjectType.Trap));
        collectableManager.spawn(Trap2.class, 0.01f);

    }

    private void spawnEntities() {


        for (Vector2 enemyCord : MapLoader.getMapCoordinates(ObjectType.Enemy)) {
            var scaledEnemyCord = enemyCord.cpy().scl(PPM).scl(2f);
            this.entities.add(new Enemy(world, deathListener, player, scaledEnemyCord));
        }
        this.entities.add(new Exit(world, MapLoader.getMapCoordinates(ObjectType.Exit).get(0), this));
        new Entry(world, MapLoader.getMapCoordinates(ObjectType.EntryPoint).get(0), this);

        for (Vector2 exitCord : MapLoader.getMapCoordinates(ObjectType.Exit)) {
            this.entities.add(new Exit(world, exitCord, this));
        }

    }

    public void handleEndOfGame(boolean victory) {
        this.end = true;
        this.victory = victory;
    }

    @Override
    public void show() {
        Gdx.input.setInputProcessor(this.inputMultiplexer);
        this.collectableManager.getTimer().start();
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
        collectableManager.render(this.game.getSpriteBatch());
        entities.forEach(entity -> entity.render(this.game.getSpriteBatch()));

        game.getSpriteBatch().end();

        if (DEBUG) {
            var enemies = entities.parallelStream()
                    .filter(gameElement -> gameElement instanceof Enemy).map(gameElement -> (Enemy) gameElement).toList();
            shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
            for (Enemy mob : enemies) {
                shapeRenderer.setColor(0.f, 1, 0f, 1f);
                Vector2 o = new Vector2();
                Vector2 p = new Vector2();
                for (int i = 0; i < mob.getPath().size() - 2; ++i) {

                    o.set(mob.getPath().get(i).cpy().scl(PPM));
                    p.set(mob.getPath().get(i + 1).cpy().scl(PPM));
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
        // switch screens after game is finished
        if (end) {
            if (victory) {
                this.game.goToVictoryScreen();
            } else {
                this.game.goToDefeatScreen();
            }
        }
    }

    /**
     * Invokes all update methods for e.g. a player.
     *
     * @param dt Time in seconds since the last render.
     */
    private void update(float dt) {
        this.world.step(1 / 60f, 6, 2);
        stateTime += dt;
        rayHandler.update();
        this.entities.parallelStream().forEach(entity -> entity.update(dt));
        this.collectableManager.update(dt);
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

        var frustum = camera.frustum;
        Vector3 c1, c2, c3, c4, dim, pos;
        dim = new Vector3(player.getDimensions(), 0).scl(0.5f);
        pos = new Vector3(player.getPosition(), 0).scl(PPM);
        c1 = pos.cpy().sub(dim);
        c2 = pos.cpy().add(dim);
        c3 = pos.cpy().sub(dim.x, 0, 0).add(0, dim.y, 0);
        c4 = pos.cpy().sub(0, dim.y, 0).add(dim.x, 0, 0);
        if (!(frustum.pointInFrustum(c1) && frustum.pointInFrustum(c2) && frustum.pointInFrustum(c3) && frustum.pointInFrustum(c4))) {
            target.set(pos);

        }
        // Duration in seconds
        var duration = .5;

        camera.position.interpolate(target, .2f, Interpolation.smoother);


        float extraSize = .5f; // 21 meters extra in all directions
        float mapStartX = -extraSize * PPM * SCALE; // X-coordinate where the map starts
        float mapStartY = -extraSize * PPM * SCALE; // Y-coordinate where the map starts
        float viewX = zoom * (camera.viewportWidth / 2);
        float viewY = zoom * (camera.viewportHeight / 2);

// Adjusted width and height calculations
        float adjustedWidth = (MapLoader.width + 2 * extraSize) * PPM * SCALE; // total world width in pixels
        float adjustedHeight = (MapLoader.height + 2 * extraSize) * PPM * SCALE; // total world height in pixels

// Calculate w and h
        float w = adjustedWidth - viewX * 2;
        float h = adjustedHeight - viewY * 2;

// Ensure w and h are not negative
        w = Math.max(w, 0);
        h = Math.max(h, 0);

// Adjust for map start coordinates
        camera.position.x = MathUtils.clamp(camera.position.x, viewX + mapStartX, w + viewX + mapStartX);
        camera.position.y = MathUtils.clamp(camera.position.y, viewY + mapStartY, h + viewY + mapStartY);


        viewport.apply();

    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height);
        hud.resize(width, height);
    }

    @Override
    public void pause() {
    }

    @Override
    public void resume() {
    }

    @Override
    public void hide() {
        this.collectableManager.getTimer().stop();
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
        collectableManager.dispose();


    }

    public CollectableManager getCollectableManager() {
        return collectableManager;
    }

    public ShapeRenderer getShapeRenderer() {
        return shapeRenderer;
    }
}
