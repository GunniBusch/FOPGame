package de.tum.cit.ase.maze.Input;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.Manifold;
import de.tum.cit.ase.maze.objects.dynamic.Enemy;
import de.tum.cit.ase.maze.objects.dynamic.Player;
import de.tum.cit.ase.maze.objects.still.Exit;
import de.tum.cit.ase.maze.objects.still.Key;
import de.tum.cit.ase.maze.objects.still.collectable.Collectable;

/**
 * Class that receives collisions from {@link com.badlogic.gdx.physics.box2d.Box2D}
 */
public class ListenerClass implements ContactListener {


    /**
     * Called when two fixtures begin to touch.
     *
     * @param contact
     */
    @Override
    public void beginContact(Contact contact) {

        // Ignore enemies colliding;

        if (contact.getFixtureA().getUserData() != null && contact.getFixtureB().getUserData() != null) {
            if (contact.getFixtureA().getUserData() instanceof Enemy && contact.getFixtureB().getUserData() instanceof Enemy)
                return;

            if (contact.getFixtureB().isSensor()) {
                Gdx.app.debug("Contact with sensor", contact.getFixtureA().getUserData().getClass().getName() + " : " + contact.getFixtureB().getUserData().getClass().getName());

                // Player seen by Enemy
                if (contact.getFixtureB().getUserData() instanceof Enemy enemy && contact.getFixtureA().getUserData() instanceof Player player) {


                    enemy.setPlayer(player);
                    enemy.isFollowing = true;

                }
                // Player reached Exit
                if (contact.getFixtureB().getUserData() instanceof Exit exit && contact.getFixtureA().getUserData() instanceof Player player) {

                    exit.requestOpening(player);

                }
                // Player Collect Collectable
                if (contact.getFixtureA().getUserData() instanceof Player player && contact.getFixtureB().getUserData() instanceof Collectable collectable) {
                    // Gdx.app.debug("Collectable", "Player collected Collectable");
                    collectable.collect(player);
                }


            } else {
                Gdx.app.debug("Contact with class", contact.getFixtureA().getUserData().getClass().getName() + " : " + contact.getFixtureB().getUserData().getClass().getName());

                // Player bumped into an Enemy
                if (contact.getFixtureB().getUserData() instanceof Enemy enemy && contact.getFixtureA().getUserData() instanceof Player player) {
                    player.setInReach(true);
                    if(player.isAttacking()) {
                        enemy.damage(1);
                    }
                    enemy.setPlayer(player);
                    enemy.isFollowing = false;

                    player.makeDamage(1);


                } // Enemy bumped into a Player
                else if (contact.getFixtureA().getUserData() instanceof Enemy enemy && contact.getFixtureB().getUserData() instanceof Player player) {
                    player.setInReach(true);
                    if(player.isAttacking()) {
                        enemy.damage(1);
                    }

                    enemy.setPlayer(player);
                    enemy.isFollowing = false;

                    enemy.damage(1);

                }

            }
        }
    }

    /**
     * Called when two fixtures cease to touch.
     *
     * @param contact
     */
    @Override
    public void endContact(Contact contact) {
        // Ignore enemies colliding;


        if (contact.getFixtureA().getUserData() != null && contact.getFixtureB().getUserData() != null) {
            if (contact.getFixtureA().getUserData() instanceof Enemy && contact.getFixtureB().getUserData() instanceof Enemy)
                return;

            if (contact.getFixtureB().isSensor()) {
                Gdx.app.debug("End contact with sensor", contact.getFixtureA().getUserData().getClass().getName() + " : " + contact.getFixtureB().getUserData().getClass().getName());

                //Player cant be seen by Enemy
                if (contact.getFixtureB().getUserData() instanceof Enemy enemy && contact.getFixtureA().getUserData() instanceof Player player) {


                    enemy.setPlayer(player);
                    enemy.isFollowing = false;

                }


            } else {
                Gdx.app.debug("End contact with class", contact.getFixtureA().getUserData().getClass().getName() + " : " + contact.getFixtureB().getUserData().getClass().getName());

                // Player bumped into an Enemy
                if (contact.getFixtureB().getUserData() instanceof Enemy enemy && contact.getFixtureA().getUserData() instanceof Player player) {


                    enemy.setPlayer(player);
                    enemy.isFollowing = true;


                } // Enemy bumped into a Player
                else if (contact.getFixtureA().getUserData() instanceof Enemy enemy && contact.getFixtureB().getUserData() instanceof Player player) {


                    enemy.setPlayer(player);
                    enemy.isFollowing = true;

                }
            }
        }

    }

    /**
     * @param contact
     * @param oldManifold
     */
    @Override
    public void preSolve(Contact contact, Manifold oldManifold) {

    }

    /**
     * @param contact
     * @param impulse
     */
    @Override
    public void postSolve(Contact contact, ContactImpulse impulse) {

    }
}

