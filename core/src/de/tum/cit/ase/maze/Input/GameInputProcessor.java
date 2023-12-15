package de.tum.cit.ase.maze.Input;

import com.badlogic.gdx.*;
import de.tum.cit.ase.maze.GameScreen;
import de.tum.cit.ase.maze.MazeRunnerGame;
import de.tum.cit.ase.maze.objects.dynamic.Character;
import de.tum.cit.ase.maze.objects.dynamic.WalkDirection;

public class GameInputProcessor extends InputAdapter {
    private final Game game;
    private final Character character;

    public GameInputProcessor(Game game, Character character) {
        this.game = game;
        this.character = character;
    }

    /**
     * @param keycode one of the constants in {@link Input.Keys}
     * @return
     */
    @Override
    public boolean keyDown(int keycode) {
        Gdx.app.log("f", "" + keycode);
        if (isVisible()) {
            switch (keycode) {
                case Input.Keys.W:
                    character.startMoving(WalkDirection.UP);
                    break;
                case Input.Keys.A:
                    character.startMoving(WalkDirection.LEFT);
                    break;
                case Input.Keys.S:
                    character.startMoving(WalkDirection.DOWN);
                    break;
                case Input.Keys.D:
                    character.startMoving(WalkDirection.RIGHT);
                    break;
                case Input.Keys.ESCAPE:
                    ((MazeRunnerGame) game).goToMenu();
                    break;
            }
            return true;
        } else {
            return false;
        }
    }

    /**
     * @param keycode one of the constants in {@link Input.Keys}
     * @return
     */
    @Override
    public boolean keyUp(int keycode) {
        if (isVisible()) {
            switch (keycode) {
                case Input.Keys.W -> character.stopMoving(WalkDirection.UP);
                case Input.Keys.A -> character.stopMoving(WalkDirection.LEFT);
                case Input.Keys.S -> character.stopMoving(WalkDirection.DOWN);
                case Input.Keys.D -> character.stopMoving(WalkDirection.RIGHT);
            }
            return true;
        } else {
            return false;
        }
    }


    private boolean isVisible() {
        return game.getScreen() instanceof GameScreen;
    }
}
