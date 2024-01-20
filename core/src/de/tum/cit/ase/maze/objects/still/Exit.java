package de.tum.cit.ase.maze.objects.still;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import de.tum.cit.ase.maze.objects.GameElement;
import de.tum.cit.ase.maze.objects.dynamic.Player;
import de.tum.cit.ase.maze.screens.GameScreen;
import de.tum.cit.ase.maze.utils.MapLoader;

import static de.tum.cit.ase.maze.utils.CONSTANTS.PPM;
import static de.tum.cit.ase.maze.utils.CONSTANTS.SCALE;

/**
 * Defines the exit of the Game. Cant be passed BUT can end the game with a victory if the player Requests an opening and all steps to win are fulfilled like having all keys.
 */
public class Exit extends GameElement {


    private final Vector2 closedPosition;
    private final GameScreen game;
    private final TextureRegion textureRegion;
    private final Music doorOpenSound;
    public boolean open = false;
    private Vector2 openPosition = new Vector2();
    private Vector2 position;

    public Exit(World world, Vector2 position, GameScreen game) {
        super();
        this.game = game;
        this.position = position;
        this.world = world;
        this.closedPosition = position.cpy().scl(SCALE);
        BodyDef def = new BodyDef();
        def.type = BodyDef.BodyType.KinematicBody;
        def.fixedRotation = true;
        body = world.createBody(def);
        body.setAwake(false);
        createBody(position);
        this.doorOpenSound = Gdx.audio.newMusic(Gdx.files.internal("Door Scrape Heavy Stone Loop 5 - QuickSounds.com.mp3"));
        this.doorOpenSound.setLooping(true);
        texture = new Texture("basictiles.png");
        textureRegion = new TextureRegion(texture, 16 * 5, 0, 16, 16);
    }

    /**
     * Creates the {@link Body} for the exit;
     *
     * @param position position of the exit
     */
    private void createBody(Vector2 position) {
        float x, y;
        var halfSize = 1f * Wall.width / PPM / SCALE;
        ChainShape shape = new ChainShape();
        x = position.x * Wall.width / PPM / SCALE;
        y = position.y * Wall.width / PPM / SCALE;


        var p = position.cpy().sub(new Vector2(MapLoader.width, MapLoader.height).scl(1 / SCALE));
        Vector2[] corners;
        if (MathUtils.round(Math.abs(p.x)) > MathUtils.round(Math.abs(p.y))) {
            // Side
            this.openPosition = new Vector2(0, 2f);
            corners = new Vector2[]{
                    new Vector2(x - halfSize, y - halfSize).sub(0, 2), // bottom-left
                    new Vector2(x + halfSize, y - halfSize).sub(0, 2), // bottom-right
                    new Vector2(x + halfSize, y + halfSize), // top-right
                    new Vector2(x - halfSize, y + halfSize)  // top-left
            };

        } else {
            this.openPosition = new Vector2(2f, 0);
            // Tops
            corners = new Vector2[]{
                    new Vector2(x - halfSize, y - halfSize).sub(2, 0), // bottom-left
                    new Vector2(x + halfSize, y - halfSize), // bottom-right
                    new Vector2(x + halfSize, y + halfSize), // top-right
                    new Vector2(x - halfSize, y + halfSize).sub(2, 0)  // top-left
            };

        }


        //If scaling uncomment and remove next line

        shape.createLoop(corners);
        body.createFixture(shape, 0f);
        shape.dispose();
        CircleShape cs = new CircleShape();
        cs.setRadius(2.5f);
        cs.setPosition(position.cpy().scl(SCALE));
        FixtureDef fd = new FixtureDef();
        fd.friction = 0f;
        fd.isSensor = true;
        fd.shape = cs;
        body.createFixture(fd).setUserData(this);
        cs.dispose();


    }

    /**
     * Renders the Exit
     */
    @Override
    public void render(SpriteBatch spriteBatch) {
        spriteBatch.draw(textureRegion, position.x * Wall.width - (32 * SCALE / 2), position.y * PPM - (32 * SCALE / 2), 32 * SCALE, 32 * SCALE);

    }

    /**
     * @param deltaTime Time since last frame.
     */
    @Override
    public void update(float deltaTime) {
        this.position = closedPosition.cpy().add(body.getPosition());
        if (open) {
            open();
        }

    }

    /**
     * Opens the exit.
     */
    private void open() {

        if (!this.doorOpenSound.isPlaying()) {
            this.doorOpenSound.play();
        }
        this.body.setTransform(this.body.getPosition().cpy().lerp(openPosition, 0.09f), 0f);


        if (this.body.getPosition().epsilonEquals(openPosition, 0.01f)) {
            this.doorOpenSound.stop();
            //ToDo Finish game with victory
            game.handleEndOfGame(true);
        }
    }

    /**
     * Request opening of the exit. Opens the exit if the Player is eligible and marks Player as finished respectively.
     *
     * @param player {@link Player} that "requests" opening.
     * @return true if opening was granted
     */
    public boolean requestOpening(Player player) {
        if (player.numberOfKeys != 0) {
            this.open = true;
            player.markAsFinished();
            return true;
        }
        this.open = false;
        return false;
    }

    /**
     * Closes the exit.
     */
    private void close() {
        this.body.setTransform(this.body.getPosition().cpy().interpolate(closedPosition, 0.1f, Interpolation.smoother), 0f);
    }

    /**
     * Disposes the {@link Texture};
     */
    @Override
    public void dispose() {
        this.texture.dispose();
        this.doorOpenSound.dispose();
    }
}
