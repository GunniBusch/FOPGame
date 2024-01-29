package de.tum.cit.ase.maze;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.SpriteCache;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.Logger;
import de.tum.cit.ase.editor.screens.Editor;
import com.badlogic.gdx.utils.TimeUtils;
import de.tum.cit.ase.maze.screens.*;
import de.tum.cit.ase.maze.utils.CONSTANTS;
import de.tum.cit.ase.maze.utils.Score;
import games.spooky.gdx.nativefilechooser.NativeFileChooser;

/**
 * The MazeRunnerGame class represents the core of the Maze Runner game.
 * It manages the screens and global resources like SpriteBatch and Skin.
 */
public class MazeRunnerGame extends Game {
    private final NativeFileChooser fileChooser;
    Music backgroundMusic;
    Music soundEffect;
    // Screens
    private MenuScreen menuScreen;
    private GameScreen gameScreen;
    private PauseScreen pauseScreen;
    private Editor editor;
    // Sprite Batch for rendering
    private SpriteBatch spriteBatch;
    private SpriteCache spriteCache;
    Music backgroundMusic;
    private final NativeFileChooser fileChooser;


    // UI Skin
    private Skin skin;
    private long startTime;
    private Score playerScore;
    private int gameTime;


    /**
     * Constructor for MazeRunnerGame.
     *
     * @param fileChooser The file chooser for the game, typically used in desktop environment.
     */
    public MazeRunnerGame(NativeFileChooser fileChooser) {
        super();
        this.fileChooser = fileChooser;
    }


    /**
     * Called when the game is created. Initializes the SpriteBatch and Skin.
     */
    @Override
    public void create() {
        if (CONSTANTS.DEBUG) Gdx.app.setLogLevel(Logger.DEBUG);
        else
            Gdx.app.setLogLevel(Logger.ERROR);
        spriteBatch = new SpriteBatch(); // Create SpriteBatch
        spriteCache = new SpriteCache(8191, false);
        skin = new Skin(Gdx.files.internal("Exported/skin.json")); // Load UI skin
        //this.loadCharacterAnimation(); // Load character animation

        // Play some background music
        // Background sound

        backgroundMusic = Gdx.audio.newMusic(Gdx.files.internal("epic_menu.mp3"));

        //start timer for playerScore
        startTime = TimeUtils.millis() / 1000;
        playerScore = new Score();


        goToMenu(); // Navigate to the menu screen
    }

    public void goToEditor() {
        this.setScreen(this.editor = new Editor(this));

    }

    public void quitEditor() {
        this.setScreen(null);
        this.editor.dispose();
        this.editor = null;
        this.goToMenu();
    }

    /**
     * Switches to the menu screen.
     */
    public void goToMenu() {
        Gdx.graphics.setWindowedMode(
                Math.round(0.8f * Gdx.graphics.getDisplayMode().width),
                Math.round(0.8f * Gdx.graphics.getDisplayMode().height)
        );
        Gdx.graphics.setUndecorated(true);

        spriteCache.clear();
        this.backgroundMusic.stop();
        this.backgroundMusic.dispose();
        this.backgroundMusic = Gdx.audio.newMusic(Gdx.files.internal("Ancient Mystery Waltz Presto.mp3"));
        backgroundMusic.setLooping(true);
        backgroundMusic.play();
        this.setScreen(this.menuScreen = new MenuScreen(this)); // Set the current screen to MenuScreen
        if (gameScreen != null) {
            gameScreen.dispose(); // Dispose the game screen if it exists
            gameScreen = null;
        }
    }

    /**
     * Switches to the victory screen.
     */
    public void goToVictoryScreen() {
        spriteCache.clear();
        this.backgroundMusic.stop();
        this.backgroundMusic.dispose();
        this.backgroundMusic = Gdx.audio.newMusic(Gdx.files.internal("Victory(chosic.com).mp3"));
        backgroundMusic.setLooping(true);
        backgroundMusic.play();
        this.setScreen(new VictoryScreen(this)); // Set the current screen to VictoryScreen
        if (gameScreen != null) {
            gameScreen.dispose(); // Dispose the game screen if it exists
            gameScreen = null;
        }
    }

    public void goToDefeatScreen() {
        spriteCache.clear();
        this.backgroundMusic.stop();
        this.backgroundMusic.dispose();
        this.backgroundMusic = Gdx.audio.newMusic(Gdx.files.internal("Treasures of Ancient Dungeon.mp3"));
        backgroundMusic.setLooping(true);
        backgroundMusic.play();
        this.setScreen(new DefeatScreen(this)); // Set the current screen to DefeatScreen
        if (gameScreen != null) {
            gameScreen.dispose(); // Dispose the game screen if it exists
            gameScreen = null;
        }
    }

    /**
     * Switches to the game screen.
     */
    public void goToGame(boolean fromPause) {
        Gdx.graphics.setFullscreenMode(Gdx.graphics.getDisplayMode());
        startTime = TimeUtils.millis(); // Reset the timer
        playerScore.resetScore(); // Reset the score
        this.backgroundMusic.stop();
        this.backgroundMusic.dispose();
        this.backgroundMusic = Gdx.audio.newMusic(Gdx.files.internal("Long Note Four.mp3"));
        backgroundMusic.setLooping(true);
        backgroundMusic.play();
        if (!fromPause) {
            this.gameScreen = new GameScreen(this);
            this.setScreen(this.gameScreen); // Set the current screen to GameScreen
            if (menuScreen != null) {
                menuScreen.dispose(); // Dispose the menu screen if it exists
                menuScreen = null;
            }
        } else {
            setScreen(this.gameScreen);
            this.pauseScreen.dispose();
        }

    }

    //TODO: implement continue game method for MenuScreen
    public void goToPause() {
        this.backgroundMusic.stop();
        this.backgroundMusic.dispose();
        this.backgroundMusic = Gdx.audio.newMusic(Gdx.files.internal("Ancient Mystery Waltz Presto.mp3"));
        backgroundMusic.setLooping(true);
        backgroundMusic.play();
        this.pauseScreen = new PauseScreen(this);
        this.setScreen(this.pauseScreen); // Set the current screen to GameScreen

    }


    /**
     * Cleans up resources when the game is disposed.
     */
    @Override
    public void dispose() {
        getScreen().hide(); // Hide the current screen
        getScreen().dispose(); // Dispose the current screen
        spriteBatch.dispose(); // Dispose the spriteBatch
        spriteCache.dispose();
        skin.dispose(); // Dispose the skin
    }

    // Getter methods
    public Skin getSkin() {
        return skin;
    }

    public SpriteBatch getSpriteBatch() {
        return spriteBatch;
    }

    public SpriteCache getSpriteCache() {
        return spriteCache;
    }

    public NativeFileChooser getFileChooser() {
        return fileChooser;
    }

    public long getStartTime() {
        return startTime;
    }

    public void setGameTime(int time) {
        this.gameTime = time;
    }

    public int getGameTime() {
        return gameTime;
    }
}
