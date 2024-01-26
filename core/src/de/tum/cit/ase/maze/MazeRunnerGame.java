package de.tum.cit.ase.maze;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.SpriteCache;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.Logger;
import de.tum.cit.ase.maze.screens.*;
import de.tum.cit.ase.maze.utils.CONSTANTS;
import games.spooky.gdx.nativefilechooser.NativeFileChooser;

/**
 * The MazeRunnerGame class represents the core of the Maze Runner game.
 * It manages the screens and global resources like SpriteBatch and Skin.
 */
public class MazeRunnerGame extends Game {
    Music backgroundMusic;
    Music soundEffect;
    // Screens
    private MenuScreen menuScreen;
    private GameScreen gameScreen;
    private PauseScreen pauseScreen;
    // Sprite Batch for rendering
    private SpriteBatch spriteBatch;
    private SpriteCache spriteCache;
    // UI Skin
    private Skin skin;

    /**
     * Constructor for MazeRunnerGame.
     *
     * @param fileChooser The file chooser for the game, typically used in desktop environment.
     */
    public MazeRunnerGame(NativeFileChooser fileChooser) {
        super();
    }

    /**
     * Called when the game is created. Initializes the SpriteBatch and Skin.
     */
    @Override
    public void create() {
        if (CONSTANTS.DEBUG) Gdx.app.setLogLevel(Logger.DEBUG);
        else //noinspection GDXJavaLogLevel
            Gdx.app.setLogLevel(Logger.INFO);
        spriteBatch = new SpriteBatch(); // Create SpriteBatch
        spriteCache = new SpriteCache(8191, false);
        skin = new Skin(Gdx.files.internal("craft/craftacular-ui.json")); // Load UI skin
        //this.loadCharacterAnimation(); // Load character animation

        // Play some background music
        // Background sound

        backgroundMusic = Gdx.audio.newMusic(Gdx.files.internal("epic_menu.mp3"));


        goToMenu(); // Navigate to the menu screen
    }

    /**
     * Switches to the menu screen.
     */
    public void goToMenu() {
        spriteCache.clear();
        this.backgroundMusic.stop();
        this.backgroundMusic = Gdx.audio.newMusic(Gdx.files.internal("Ancient Mystery Waltz Presto.mp3"));
        backgroundMusic.setLooping(true);
        //backgroundMusic.play();
        this.setScreen(new MenuScreen(this)); // Set the current screen to MenuScreen
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
        this.backgroundMusic.stop();
        this.backgroundMusic = Gdx.audio.newMusic(Gdx.files.internal("Long Note Four.mp3"));
        backgroundMusic.setLooping(true);
        //backgroundMusic.play();
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
        this.backgroundMusic = Gdx.audio.newMusic(Gdx.files.internal("Ancient Mystery Waltz Presto.mp3"));
        backgroundMusic.setLooping(true);
        //backgroundMusic.play();
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
}
