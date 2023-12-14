package de.tum.cit.ase.maze.objects.dynamic;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class Player extends Character {
    public Player(float x, float y) {
        super(x, y);
        this.downMove = false;
        this.upMove = false;
        this.leftMove = false;
        this.rightMove = false;


    }

    /**
     * Moves the Object
     */
    @Override
    public void updateMotion() {

    }

    /**
     * @param move
     */
    @Override
    public void setLeftMove(boolean move) {

    }

    /**
     * @param move
     */
    @Override
    public void setRightMove(boolean move) {

    }

    /**
     * @param move
     */
    @Override
    public void setDownMove(boolean move) {

    }

    /**
     * @param move
     */
    @Override
    public void setUpMove(boolean move) {

    }
}