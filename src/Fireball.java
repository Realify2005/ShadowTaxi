import bagel.Image;
import java.util.Properties;

/**
 * Class representing a fireball entity.
 * A fireball is shot by an enemy car in-game and moves upwards.
 * It can collide with any damageable object, disappears, and then inflict a certain amount of damage on them.
 */
public class Fireball implements Drawable {

    /**
     * The image of the fireball.
     */
    private final Image IMAGE;

    /**
     * The radius of the fireball used for collision detection.
     */
    private final double RADIUS;

    /**
     * The damage the fireball inflicts onto other damageable objects upon collision.
     */
    private final double DAMAGE;

    /**
     * The vertical speed at which the fireball moves upward.
     */
    private final int SHOOT_SPEED_Y;

    /**
     * The enemy car that shot this fireball.
     */
    private final EnemyCar SPAWNED_BY;

    /**
     * The current x-coordinate of the fireball.
     */
    private int x;

    /**
     * The current y-coordinate of the fireball.
     */
    private int y;

    /**
     * A flag indicating whether the fireball has collided with a damageable object.
     */
    private boolean isCollided;

    /**
     * Constructor for fireball entity.
     * Initialises its (x, y) position, image, radius, damage, and vertical speed.
     * @param gameProps The properties object containing game configuration values.
     * @param startX The initial X position of fireball (i.e. X position of enemy car when shooting this fireball).
     * @param startY The initial Y position of fireball (i.e. Y position of enemy car when shooting this fireball).
     * @param SPAWNED_BY The enemy car that shot this fireball.
     */
    public Fireball(Properties gameProps, int startX, int startY, EnemyCar SPAWNED_BY) {
        int PROPS_TO_GAME_MULTIPLIER = 100;
        this.IMAGE = new Image(gameProps.getProperty("gameObjects.fireball.image"));
        this.RADIUS = Double.parseDouble(gameProps.getProperty("gameObjects.fireball.radius"));
        this.DAMAGE = Double.parseDouble(gameProps.getProperty("gameObjects.fireball.damage"))
                * PROPS_TO_GAME_MULTIPLIER;
        this.SHOOT_SPEED_Y = Integer.parseInt(gameProps.getProperty("gameObjects.fireball.shootSpeedY"));

        // Set initial position of the fireball
        this.x = startX;
        this.y = startY;
        this.isCollided = false;
        this.SPAWNED_BY = SPAWNED_BY;
    }

    /**
     * Updates the fireball's entity state.
     * Moves the fireball upwards and draws it.
     */
    public void update() {
        moveUp();
        draw();
    }

    /**
     * Checks whether the fireball collides with another object based on their positions and radius.
     * @param otherX The x-coordinate of the other object.
     * @param otherY The y-coordinate of the other object.
     * @param otherRadius The radius of the other object.
     * @return True if the fireball collides with the object, false otherwise.
     */
    public boolean collidesWith(int otherX, int otherY, double otherRadius) {
        double distance = getDistanceTo(otherX, otherY);
        double collisionRange = this.RADIUS + otherRadius;
        return distance < collisionRange;
    }

    /**
     * Calculates the distance from the fireball to another object.
     * @param otherX The x-coordinate of the other object.
     * @param otherY The y-coordinate of the other object.
     * @return The distance between the fireball and the other object.
     */
    public double getDistanceTo(double otherX, double otherY) {
        return Math.sqrt(Math.pow(otherX - this.x, 2) + Math.pow(otherY - this.y, 2));
    }

    /**
     * Moves the fireball upwards.
     */
    private void moveUp() {
        this.y -= SHOOT_SPEED_Y;
    }

    /**
     * Draws the fireball if it is still currently within game screen and has not been collided yet.
     */
    @Override
    public void draw() {
        if (!isOffScreen() && !isCollided) {
            IMAGE.draw(x, y);
        }
    }

    /**
     * Checks whether the fireball has moved off the top of the screen.
     * @return True if the fireball is off-screen, false otherwise.
     */
    public boolean isOffScreen() {
        return this.y <= 0;
    }

    /**
     * Returns the damage the fireball inflicts onto other damageable objects upon collision.
     * @return The damage value of the fireball.
     */
    public double getDamage() {
        return DAMAGE;
    }

    /**
     * Returns the enemy car that spawned this fireball.
     * @return The enemy car that spawned the fireball.
     */
    public EnemyCar getSpawnedBy() {
        return SPAWNED_BY;
    }

    /**
     * Set the fireball as it has been collided.
     */
    public void wasCollided() {
        isCollided = true;
    }

    /**
     * Checks whether the fireball has collided with another damageable object.
     * @return True if the fireball has collided with another damageable object, false otherwise.
     */
    public boolean isCollided() {
        return isCollided;
    }
}
