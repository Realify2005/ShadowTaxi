import java.util.Properties;
import java.util.ArrayList;

/**
 * Class representing the Enemy Car entity, which has all functionalities as Car.
 * The only difference it has with Car is the fact that it can shoot fireballs at random times during gameplay.
 */
public class EnemyCar extends Car {

    /**
     * The rate at which fireballs can be spawned.
     */
    private final int FIREBALL_SPAWN_RATE = 300;

    /**
     * The game properties object containing various game configuration values.
     */
    private final Properties GAME_PROPS;

    /**
     * List of fireballs currently active in-game.
     * Shared between all instances of enemy cars.
     */
    private ArrayList<Fireball> fireballs;

    /**
     * Constructor for Enemy Car class.
     * Initialises its initial (x, y) position, image, radius, starting health, damage, fixed speed. (Extends from car).
     * @param gameProps The game properties object containing various game configuration values.
     * @param fireballs The global list of fireballs to where the enemy car can add to when successfully creating new
     *                  fireballs.
     */
    public EnemyCar(Properties gameProps, ArrayList<Fireball> fireballs) {
        super(gameProps,
                gameProps.getProperty("gameObjects.enemyCar.image"),
                "gameObjects.enemyCar.radius", "gameObjects.enemyCar.health",
                "gameObjects.enemyCar.damage", "gameObjects.enemyCar.minSpeedY",
                "gameObjects.enemyCar.maxSpeedY");
        this.GAME_PROPS = gameProps;
        this.fireballs = fireballs;
    }

    /**
     * By calling car's update() function,
     * Update the collision timeout frames remaining if it is currently in collision timeout.
     * Calls another method to continue separation of object from collided object if still in initial timeout.
     * If not currently in collision timeout, then moves the object in y direction according to its fixed speed.
     * Renders the object where necessary.
     * Furthermore, it attempts to spawn fireballs randomly, according to the set fireball spawn rate.
     */
    @Override
    public void update() {
        super.update();
        // Fireball spawns if (1000 % FIREBALL_SPAWN_RATE == 0).
        if (MiscUtils.canSpawn(FIREBALL_SPAWN_RATE)) {
            shootFireball();
        }
    }

    /**
     * Shoots a fireball from the enemy car's current position if the car's health is greater than 0.
     * The new fireball is then added to the global list of fireballs.
     */
    private void shootFireball() {
        if (getCurrentHealth() > 0) {
            fireballs.add(new Fireball(GAME_PROPS, getX(), getY(), this));
        }
    }
}
