package de.tum.cit.ase.maze.Input;

import com.badlogic.gdx.*;
import de.tum.cit.ase.maze.GameScreen;
import de.tum.cit.ase.maze.objects.dynamic.Character;
import de.tum.cit.ase.maze.objects.dynamic.WalkDirection;

public class GameInputProcessor extends InputAdapter {
    private Game game;
    private Character character;

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
        if (isVisible()) {
            switch (keycode) {
                case Input.Keys.W:
                    break;
                case Input.Keys.S:
                    character.startMoving(WalkDirection.DOWN);
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
                case Input.Keys.S -> character.startMoving(WalkDirection.DOWN);
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
