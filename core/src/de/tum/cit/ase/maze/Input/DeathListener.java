package de.tum.cit.ase.maze.Input;

import de.tum.cit.ase.maze.GameScreen;
import de.tum.cit.ase.maze.objects.dynamic.Character;
import de.tum.cit.ase.maze.objects.dynamic.Player;

public class DeathListener {
    private final GameScreen game;

    public DeathListener(GameScreen game) {
        this.game = game;
    }

    public void onDeath(Character deadCharacter){
        if (deadCharacter instanceof Player){
            this.game.handleEndOfGame(false);
        }
    }
}
