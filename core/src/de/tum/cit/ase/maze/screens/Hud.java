package de.tum.cit.ase.maze.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import de.tum.cit.ase.maze.objects.dynamic.Player;
import de.tum.cit.ase.maze.objects.still.collectable.TimedCollectable;
import org.reflections.Reflections;

import java.util.List;
import java.util.Stack;
import java.util.*;
import java.util.stream.Collectors;

import static de.tum.cit.ase.maze.utils.CONSTANTS.DEBUG;
import static de.tum.cit.ase.maze.utils.CONSTANTS.PLAYER_MAX_HEALTH;


/**
 * Class that represents a game HUD, that shows information to the player like health.
 */
public class Hud implements Disposable {
    private final float singleHealthWidth = 18f;
    private final float singleKeyWidth = 18f;
    /**
     * Stage to show HUD
     */
    private final Stage stage;
    private final Skin skin;
    private final Label fps;
    private final Player player;
    private final ProgressBar healthBar;
    private final ProgressBar keyBar;
    private final GameScreen gameScreen;
    private final Map<Class<? extends TimedCollectable>, List<Widget>> labelMap;
    private final MiniMap miniMap;
    private final Image minimapBorder;
    private ProgressBar respawnBarDebug;
    private boolean minimapEnabled;
    //private ProgressBar respawnBarDebug;

    /**
     * Creates a new HUD
     *
     * @param hudCamera   {@link OrthographicCamera} that looks at the HUD
     * @param spriteBatch {@link SpriteBatch} that renders the HUD
     * @param player      The {@link Player} information has to be displayed
     */
    public Hud(OrthographicCamera hudCamera, SpriteBatch spriteBatch, Player player, GameScreen gameScreen, boolean enableMiniMap) {
        //this.viewport = new FitViewport(Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), hudCamera);

        this.minimapEnabled = enableMiniMap;
        this.gameScreen = gameScreen;
        this.player = player;
        hudCamera.zoom = 1.01f;
        hudCamera.setToOrtho(false);

        this.stage = new Stage(new ScreenViewport(hudCamera), spriteBatch);
        this.skin = new Skin(Gdx.files.internal("Exported/skin.json"));
        this.miniMap = new MiniMap(gameScreen, spriteBatch, player);

        // Top Table:
        Table table = new Table();
        table.align(Align.topLeft);
        table.setFillParent(true);

        fps = new Label("60", skin);
        if (this.gameScreen.getCollectableManager().canRespawn) {
            respawnBarDebug = new ProgressBar(0, gameScreen.getCollectableManager().RESPAWN_TIME, 5, false, skin);
            respawnBarDebug.setVisible(DEBUG);

        }
        fps.setVisible(DEBUG);


        table.add(fps);
        table.row();
        table.add(respawnBarDebug);
        stage.addActor(table);

        // Bottom Table:
        table = new Table();
        table.align(Align.bottomLeft);

        Label label = new Label("Health: ", skin);
        table.add(label).spaceRight(10.0f).align(Align.left);

        healthBar = new ProgressBar(0.0f, PLAYER_MAX_HEALTH, 1.0f, false, skin, "health-bar-no-border");
        table.add(healthBar).align(Align.left).width(singleHealthWidth * PLAYER_MAX_HEALTH);

        table.row();

        label = new Label("Keys: ", skin);
        label.setName("key-lable");
        table.add(label).align(Align.left);

        keyBar = new ProgressBar(0.0f, player.numberOfKeys, 1.0f, false, skin, "key-bar-no-border");
        table.add(keyBar).align(Align.left).width(18 * player.numberOfKeys);

        stage.addActor(table);
        // Booster table
        table = new Table();
        table.setFillParent(true);


        Reflections reflections = new Reflections("de.tum.cit.ase.maze");

        var timedCollecteablesClassesSet = reflections.getSubTypesOf(TimedCollectable.class);
        var timedCollecteablesClassesStack = timedCollecteablesClassesSet.stream().collect(Collectors.toCollection(Stack::new));

        int rows = ((int) Math.ceil(timedCollecteablesClassesStack.size() / 2f));
        int columns = 2;
        ProgressBar progBarC;
        labelMap = new HashMap<>();

        for (int i = 0; i < rows; i++) {
            table.row();

            var ts = timedCollecteablesClassesStack.size();
            for (int j = 0; j < ts % (columns + 1); j++) {


                var collectableClass = timedCollecteablesClassesStack.pop();
                var arr = new ArrayList<Widget>();
                System.out.println(TimedCollectable.class.getClasses().length);
                label = new Label(collectableClass.getSimpleName(), skin);
                label.setFontScale(0.7f);
                table.add(label).align(Align.center).spaceRight(5).spaceTop(5);
                progBarC = new ProgressBar(0, 30, 1, false, skin);
                progBarC.setAnimateInterpolation(Interpolation.linear);
                progBarC.setAnimateDuration(.8f);
                table.add(progBarC).align(Align.center).spaceTop(7.5f).spaceRight(10);

                arr.add(label);
                arr.add(progBarC);

                labelMap.put(collectableClass, arr);

            }
        }


        table.align(Align.bottomRight);
        minimapBorder = new Image(skin, "panel-transparent-center-000");
        minimapBorder.setVisible(minimapEnabled);
        var minimapView = miniMap.getViewport();
//        minimapBorder.setPosition(minimapView.getScreenX() - minimapView.getScreenWidth() / 2f, minimapView.getScreenY() - minimapView.getScreenHeight() / 2f);
        minimapBorder.setWidth(minimapView.getScreenWidth());
        minimapBorder.setHeight(minimapView.getScreenHeight());
        minimapBorder.setBounds(minimapView.getScreenX() - 5, minimapView.getScreenY() - 5, (minimapView.getScreenWidth() / 2f) + 15, (minimapView.getScreenHeight() / 2f) + 15);
        stage.addActor(minimapBorder);
        stage.addActor(table);
        //Debug stuff
        stage.setDebugAll(DEBUG);
        stage.addListener(new InputListener() {
            @Override
            public boolean keyUp(InputEvent event, int keycode) {

                switch (keycode) {
                    // For me this is the "+" key
                    case Input.Keys.RIGHT_BRACKET -> {
                        if (minimapEnabled) {
                            miniMap.switchZoom();
                            var minimapView = miniMap.getViewport();
                            minimapBorder.setBounds(minimapView.getScreenX() - 5, minimapView.getScreenY() - 5, (minimapView.getScreenWidth() / 2f) + 15, (minimapView.getScreenHeight() / 2f) + 15);
                            minimapBorder.setVisible(miniMap.getZoomState() != MiniMap.ZoomState.Off);
                            return true;
                        } else return false;
                    }
                    default -> {
                        return false;
                    }
                }
            }
        });
    }

    /**
     * Called when windows is resized
     */
    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
        miniMap.resize(width, height);
        var minimapView = miniMap.getViewport();

        minimapBorder.setBounds(minimapView.getScreenX() - 5, minimapView.getScreenY() - 5, (minimapView.getScreenWidth() / 2f) + 15, (minimapView.getScreenHeight() / 2f) + 15);

    }

    /**
     * Updates the HUD
     */
    public void update(float dt) {
        if (minimapEnabled) this.miniMap.update(dt);
        labelMap.forEach((aClass, widgets) -> {
            var label = ((Label) widgets.get(0));
            var progBar = ((ProgressBar) widgets.get(1));
            if (player.getTimedCollectables().stream().noneMatch(aClass::isInstance)) {
                label.setVisible(false);
                progBar.setVisible(false);
                progBar.setValue(0);

            } else {
                var timedCollectable = player.getTimedCollectables().stream().filter(aClass::isInstance).findFirst().orElseThrow();
                System.out.println(timedCollectable);
                widgets.get(0).setVisible(true);
                widgets.get(1).setVisible(true);
                var durel = timedCollectable.getDurationAndElapsed();
                progBar.setRange(0, durel[1]);
                progBar.setValue(durel[0]);
            }
        });


        this.fps.setText(Gdx.graphics.getFramesPerSecond());
        if (this.gameScreen.getCollectableManager().canRespawn) {
            this.respawnBarDebug.setValue(this.gameScreen.getCollectableManager().getRespawnTask().getTimeToExecutionInSeconds());
        }
        healthBar.setValue(player.getHealth());
        keyBar.setValue(player.getKeyList().size());
        this.stage.act(dt);

    }

    /**
     * Renders the HUD
     */
    public void render() {
        this.stage.getViewport().apply(true);
        this.stage.draw();
        if (minimapEnabled) this.miniMap.render();


    }

    public boolean isMinimapEnabled() {
        return minimapEnabled;
    }

    public void setMinimapEnabled(boolean minimapEnabled) {
        this.minimapEnabled = minimapEnabled;
        minimapBorder.setVisible(minimapEnabled);

    }

    public Stage getStage() {
        return stage;
    }

    @Override
    public void dispose() {
        this.stage.dispose();
        this.skin.dispose();
        this.miniMap.dispose();

    }
}
