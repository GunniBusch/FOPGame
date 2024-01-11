package de.tum.cit.ase.maze.Input;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.Manifold;
import de.tum.cit.ase.maze.objects.still.collectable.Collectable;
import de.tum.cit.ase.maze.objects.dynamic.Enemy;
import de.tum.cit.ase.maze.objects.dynamic.Player;
import de.tum.cit.ase.maze.objects.still.Exit;

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
        if (contact.getFixtureA().getUserData() instanceof Enemy && contact.getFixtureB().getUserData() instanceof Enemy)
            return;
        ;
        if (contact.getFixtureA().getUserData() != null && contact.getFixtureB().getUserData() != null) {


            if (contact.getFixtureB().isSensor()) {
                Gdx.app.debug("Contact with sensor", contact.getFixtureA().getUserData().getClass().getName() + " : " + contact.getFixtureB().getUserData().getClass().getName());

                // Player seen by Enemy
                if (contact.getFixtureB().getUserData() instanceof Enemy && contact.getFixtureA().getUserData() instanceof Player) {


                    ((Enemy) contact.getFixtureB().getUserData()).setPlayer((Player) contact.getFixtureA().getUserData());
                    ((Enemy) contact.getFixtureB().getUserData()).isFollowing = true;

                }
                // Player reached Exit
                if (contact.getFixtureB().getUserData() instanceof Exit && contact.getFixtureA().getUserData() instanceof Player) {

                    ((Exit) contact.getFixtureB().getUserData()).requestOpening((Player) contact.getFixtureA().getUserData());

                }
                // Player Collect Collectable
                if (contact.getFixtureA().getUserData() instanceof Player && contact.getFixtureB().getUserData() instanceof Collectable) {
                    Gdx.app.debug("Collectable", "Player collected Collectable");
                    ((Collectable) contact.getFixtureB().getUserData()).collect((Player) contact.getFixtureA().getUserData());
                }

            } else {
                Gdx.app.debug("Contact with class", contact.getFixtureA().getUserData().getClass().getName() + " : " + contact.getFixtureB().getUserData().getClass().getName());

                // Player bumped into an Enemy
                if (contact.getFixtureB().getUserData() instanceof Enemy && contact.getFixtureA().getUserData() instanceof Player) {


                    ((Enemy) contact.getFixtureB().getUserData()).setPlayer((Player) contact.getFixtureA().getUserData());
                    ((Enemy) contact.getFixtureB().getUserData()).isFollowing = false;

                    ((Player) contact.getFixtureA().getUserData()).makeDamage(1);


                } // Enemy bumped into a Player
                else if (contact.getFixtureA().getUserData() instanceof Enemy && contact.getFixtureB().getUserData() instanceof Player) {


                    ((Enemy) contact.getFixtureA().getUserData()).setPlayer((Player) contact.getFixtureB().getUserData());
                    ((Enemy) contact.getFixtureA().getUserData()).isFollowing = false;

                    ((Player) contact.getFixtureB().getUserData()).makeDamage(1);

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
        if (contact.getFixtureA().getUserData() instanceof Enemy && contact.getFixtureB().getUserData() instanceof Enemy)
            return;
        ;

        if (contact.getFixtureA().getUserData() != null && contact.getFixtureB().getUserData() != null) {


            if (contact.getFixtureB().isSensor()) {
                Gdx.app.debug("End contact with sensor", contact.getFixtureA().getUserData().getClass().getName() + " : " + contact.getFixtureB().getUserData().getClass().getName());

                //Player cant be seen by Enemy
                if (contact.getFixtureB().getUserData() instanceof Enemy && contact.getFixtureA().getUserData() instanceof Player) {


                    ((Enemy) contact.getFixtureB().getUserData()).setPlayer((Player) contact.getFixtureA().getUserData());
                    ((Enemy) contact.getFixtureB().getUserData()).isFollowing = false;

                }


            } else {
                Gdx.app.debug("End contact with class", contact.getFixtureA().getUserData().getClass().getName() + " : " + contact.getFixtureB().getUserData().getClass().getName());

                // Player bumped into an Enemy
                if (contact.getFixtureB().getUserData() instanceof Enemy && contact.getFixtureA().getUserData() instanceof Player) {


                    ((Enemy) contact.getFixtureB().getUserData()).setPlayer((Player) contact.getFixtureA().getUserData());
                    ((Enemy) contact.getFixtureB().getUserData()).isFollowing = true;


                } // Enemy bumped into a Player
                else if (contact.getFixtureA().getUserData() instanceof Enemy && contact.getFixtureB().getUserData() instanceof Player) {


                    ((Enemy) contact.getFixtureA().getUserData()).setPlayer((Player) contact.getFixtureB().getUserData());
                    ((Enemy) contact.getFixtureA().getUserData()).isFollowing = true;

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

