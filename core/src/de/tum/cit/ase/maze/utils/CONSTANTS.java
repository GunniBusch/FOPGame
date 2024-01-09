package de.tum.cit.ase.maze.utils;

/**
 * Saves constants
 */
public final class CONSTANTS {
    /**
     * Scale factor to convert Box2d meter to pixel.
     * Getting Box2D units: Multiply by PPM.
     * Giving to Box2D units: divide by PPM.
     * <p>
     * e.g. getting position multiply. setting position divide.
     */
    public static final float PPM = 32;
    /**
     * The maximal health a player has.
     */
    public static final int PLAYER_MAX_HEALTH = 4;
    /**
     * Tells if the game is in development mode.
     */
    public static final boolean DEBUG = true;
    public static final float SCALE = 2f;

}
