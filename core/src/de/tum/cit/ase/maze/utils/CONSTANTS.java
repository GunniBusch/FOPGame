package de.tum.cit.ase.maze.utils;

/**
 * Saves constants
 */
public record CONSTANTS() {
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
    public static final boolean DEBUG = false;
    public static final float SCALE = 2f;
    /**
     * Filter bit default
     */
    public static final short DEFAULT_BIT = 0x0001;

    /**
     * Filter bit for Box2d sensor
     */
    public static final short SENSOR_BIT = 0x0002;
    /**
     * Filter bit for Box2dLights
     */
    public static final short LIGHT_BIT = 0x0003;
    /**
     * Filter bit for Player
     */
    public static final short PLAYER_BIT = 0x0004;
    /**
     * Filter bit for Enemy
     */
    public static final short ENEMY_BIT = 0x0005;

    /**
     * Filter bit for Collectable
     */
    public static final short COLLECTABLE_BIT = 0x0006;
    /**
     * Filter bit IGNORE
     */
    public static final short IGNORE_GROUP_BIT = -0x0007;

}
