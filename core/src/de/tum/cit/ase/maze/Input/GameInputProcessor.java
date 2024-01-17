package de.tum.cit.ase.maze.Input;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
import de.tum.cit.ase.maze.MazeRunnerGame;
import de.tum.cit.ase.maze.objects.dynamic.Movable;
import de.tum.cit.ase.maze.objects.dynamic.Player;
import de.tum.cit.ase.maze.objects.dynamic.WalkDirection;
import de.tum.cit.ase.maze.screens.GameScreen;

public class GameInputProcessor extends InputAdapter {
    private final MazeRunnerGame game;
    private final Movable character;

    public GameInputProcessor(MazeRunnerGame game, Movable character) {
        this.game = game;
        this.character = character;
    }

    /**
     * @param keycode one of the constants in {@link Input.Keys}
     * @return
     */
    @Override
    public boolean keyDown(int keycode) {
        if (isVisible()) {
            switch (keycode) {
                case Input.Keys.W -> character.startMoving(WalkDirection.UP);
                case Input.Keys.A -> character.startMoving(WalkDirection.LEFT);
                case Input.Keys.S -> character.startMoving(WalkDirection.DOWN);
                case Input.Keys.D -> character.startMoving(WalkDirection.RIGHT);
                case Input.Keys.SHIFT_LEFT -> ((Player) character).setSprint(true);
                case Input.Keys.ESCAPE -> game.goToPause();
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
                case Input.Keys.SHIFT_LEFT -> ((Player) character).setSprint(false);
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
