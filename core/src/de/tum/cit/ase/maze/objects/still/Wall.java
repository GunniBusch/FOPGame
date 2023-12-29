package de.tum.cit.ase.maze.objects.still;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;

import java.util.Properties;
import java.util.Vector;

/**
 * The class Wall implements the different Mazes in the game.
 * It reads an input file in the maps directory.
 */

public class Wall {
    private boolean innerWall = false;
    private SpriteBatch spriteBatch;
    private Texture img;
    private Vector2 position;




    // TODO: Player choose load map option in menu functionality to be implemented in method below
    public void loadMapFile(String filePath) {

    }

    public void applyTexture() {
        //TODO: logic checking wether inner or outer wall
        spriteBatch.begin();
        if(innerWall) {
            // TODO: find nice texture for innerwalls, decide with leon
            img = new Texture("wall.png");

            spriteBatch.draw(img, position.x, position.y);
        } else {
            // TODO: find nice texture for outerWalls, decide with leon
            img = new Texture("lava.png");
            spriteBatch.draw(img, position.x, position.y);
        }
        spriteBatch.end();
    }




}
