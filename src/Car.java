import bagel.Image;
import java.util.Properties;

/**
 * An abstract class for Car, an object entity that can move on its own without user input.
 * Spawns randomly, only moves vertically, and can deal damage towards other damageable entities.
 * Car can also receive damage and will disappear if its health reaches 0.
 */
public abstract class Car implements Drawable, Damageable {

    /**
     * The image representing the car.
     */
    protected final Image IMAGE;

    /**
     * The collision radius of the car.
     */
    protected final double RADIUS;

    /**
     * The maximum health of the car (i.e. initial health of car upon creation).
     */
    protected final double HEALTH;

    /**
     * The amount of damage the car inflicts upon other damageable objects.
     */
    protected final double DAMAGE;

    /**
     * The minimum vertical fixed speed of the car.
     */
    protected final int MIN_SPEED_Y;

    /**
     * The maximum vertical fixed speed of the car.
     */
    protected final int MAX_SPEED_Y;

    /**
     * The first possible starting y-coordinate when the car spawns on the screen.
     */
    protected final int CAR_Y_1 = -50;

    /**
     * The second possible starting y-coordinate when the car spawns on the screen.
     */
    protected final int CAR_Y_2 = 768;

    /**
     * Amount of y-coordinate changed per frame when separating car from another damageable object during a collision.
     */
    protected final int SEPARATE_Y = 1;

    /**
     * The center x-coordinate of the first road lane.
     */
    protected final int ROAD_LANE_CENTER_1;

    /**
     * The center x-coordinate of the second road lane.
     */
    protected final int ROAD_LANE_CENTER_2;

    /**
     * The center x-coordinate of the third road lane.
     */
    protected final int ROAD_LANE_CENTER_3;

    /**
     * The number of frames remaining to separate the car from another damageable object post-collision.
     */
    private int initialCollisionTimeoutFramesRemaining;

    /**
     * The number of frames remaining before the car can collide with another damageable object after any collision.
     */
    private int collisionTimeoutFramesRemaining;

    /**
     * A boolean indicating whether the fire effect has been added when the car's health reaches 0.
     */
    private boolean fireEffectAdded;

    /**
     * The current health of the car.
     */
    private double currentHealth;

    /**
     * The fixed speed of the car.
     */
    private final int SPEED;

    /**
     * The current x-coordinate position of the car.
     */
    private int x;

    /**
     * The current y-coordinate position of the car.
     */
    private int y;

    /**
     * The damageable object that the car has collided with, used to handle separation post-collision.
     */
    private Damageable collidingOtherObject;

    /**
     * The total number of frames during which the car is immune from collisions after a collision occurs.
     */
    private final int COLLISION_TIMEOUT_FRAMES_TOTAL = 200;

    /**
     * The initial number of frames during which the car must separate from the object it has collided with.
     */
    private final int COLLISION_TIMEOUT_FRAMES_INITIAL = 10;

    /**
     * Constructor for Car class.
     * Initialises its starting (x, y) position, image, radius, starting health, and fixed vertical speed.
     * @param gameProps The game properties object containing various game configuration values.
     * @param imagePath The string path to the image of the car.
     * @param radiusProperty The property key to the radius property of the car.
     * @param healthProperty The property key to the health property of the car.
     * @param damageProperty The property key to the damage property of the car.
     * @param minSpeedYProperty The property key to the minimum fixed vertical speed of the car.
     * @param maxSpeedYProperty The property key to the maximum fixed vertical speed of the car.
     */
    public Car(Properties gameProps, String imagePath, String radiusProperty, String healthProperty,
               String damageProperty, String minSpeedYProperty, String maxSpeedYProperty) {

        int PROPS_TO_GAME_MULTIPLIER = 100; // The game properties stores health and damage as (value / 100).
        IMAGE = new Image(imagePath);
        RADIUS = Double.parseDouble(gameProps.getProperty(radiusProperty));
        HEALTH = Double.parseDouble(gameProps.getProperty(healthProperty)) * PROPS_TO_GAME_MULTIPLIER;
        DAMAGE = Double.parseDouble(gameProps.getProperty(damageProperty)) * PROPS_TO_GAME_MULTIPLIER;
        MIN_SPEED_Y = Integer.parseInt(gameProps.getProperty(minSpeedYProperty));
        MAX_SPEED_Y = Integer.parseInt(gameProps.getProperty(maxSpeedYProperty));

        ROAD_LANE_CENTER_1 = Integer.parseInt(gameProps.getProperty("roadLaneCenter1"));
        ROAD_LANE_CENTER_2 = Integer.parseInt(gameProps.getProperty("roadLaneCenter2"));
        ROAD_LANE_CENTER_3 = Integer.parseInt(gameProps.getProperty("roadLaneCenter3"));

        this.currentHealth = HEALTH;
        this.SPEED = getRandomSpeed();
        this.x = getRandomPositionX();
        this.y = getRandomPositionY();
        this.fireEffectAdded = false;
    }

    /**
     * Generates a random x-coordinate position for spawning cars.
     * @return The generated random x-coordinate position.
     */
    public int getRandomPositionX() {
        return MiscUtils.selectAValue(
                MiscUtils.selectAValue(ROAD_LANE_CENTER_1, ROAD_LANE_CENTER_2),
                ROAD_LANE_CENTER_3);
    }

    /**
     * Generates a random fixed speed for spawning cars.
     * @return The generated fixed speed.
     */
    private int getRandomSpeed() {
        return MiscUtils.getRandomInt(MIN_SPEED_Y, MAX_SPEED_Y);
    }

    /**
     * Generates a random y-coordinate position for spawning cars.
     * @return The generated random y-coordinate position.
     */
    private int getRandomPositionY() {
        return MiscUtils.selectAValue(CAR_Y_1, CAR_Y_2);
    }

    /**
     * Update the collision timeout frames remaining if it is currently in collision timeout.
     * Calls another method to continue separation of object from collided object if still in initial timeout.
     * If not currently in collision timeout, then moves the object in y direction according to its fixed speed.
     * Renders the object where necessary.
     */
    public void update() {
        updateCollisionTimeoutFramesRemaining();

        // Check if still in collision timeout
        if (collisionTimeoutFramesRemaining > 0) {
            separateFromObject(collidingOtherObject);
        } else {
            moveUp();
        }
        draw();
    }

    /**
     * Moves the object up, in the y-direction.
     */
    private void moveUp() {
        this.y -= SPEED;
    }

    /**
     * Draws the car image if it has not been broken yet.
     */
    @Override
    public void draw() {
        if (currentHealth > 0) {
            IMAGE.draw(x, y);
        }
    }

    /**
     * Updates the current health of the car after some damage is inflicted on it.
     * @param damage The damage inflicted onto the car.
     */
    @Override
    public void receiveDamage(double damage) {
        this.currentHealth -= damage;
        if (this.currentHealth < 0) {
            this.currentHealth = 0;
        }
    }

    /**
     * Checks if a collision has occurred that involves this car object.
     * A collision has occurred if:
     * 1. The other car's health is greater than or equal to 0.
     * 2. This (car) object has no more collision timeout frames remaining.
     * 3. The distance between this (car) and other car is less than the combined collision radius of both objects.
     * @param other The other car that car has potentially collided with
     * @return True if a legal collision has occurred, false otherwise.
     */
    @Override
    public boolean handleCollision(Car other) {
        if (other.getCurrentHealth() > 0) {
            double distance = getDistanceTo(other.getX(), other.getY());
            double collisionRange = this.getRadius() + other.getRadius();
            if (collisionTimeoutFramesRemaining == 0 && distance < collisionRange) {
                this.receiveDamage(other.getDamage());
                collidingOtherObject = other;
                other.receiveCollision(this);
                collisionTimeoutFramesRemaining = COLLISION_TIMEOUT_FRAMES_TOTAL;
                initialCollisionTimeoutFramesRemaining = COLLISION_TIMEOUT_FRAMES_INITIAL;
                return true;
            }
        }
        return false;
    }

    /**
     * Reduces the collision timeout frames remaining if it is still active.
     * This function is called once every frame.
     */
    @Override
    public void updateCollisionTimeoutFramesRemaining() {
        if (initialCollisionTimeoutFramesRemaining > 0) {
            initialCollisionTimeoutFramesRemaining--;
        }
        if (collisionTimeoutFramesRemaining > 0) {
            collisionTimeoutFramesRemaining--;
        }
    }

    /**
     * Separates the car object from another object (post-collision during initial timeout frames).
     * For 10 frames, moves the car vertically upwards if it is above the other object, or vice versa.
     * @param other The other object that car has collided with.
     */
    @Override
    public void separateFromObject(Damageable other) {
        if (initialCollisionTimeoutFramesRemaining > 0 && initialCollisionTimeoutFramesRemaining <= 10) {
            int otherY = other.getY();
            int thisY = this.getY();

            if (thisY < otherY) {
                this.setY(thisY - SEPARATE_Y);
            } else {
                this.setY(thisY + SEPARATE_Y);
            }
        }
    }

    /**
     * The function that is on the receiving end of handleCollision.
     * handleCollision will be called by gameplay class, and receiveCollision will be called by handleCollision.
     * If a collision has occurred, receiveCollision method will be called by the other object's handleCollision method.
     * @param other The other object that car has collided with
     */
    public void receiveCollision(Damageable other) {
        double distance = getDistanceTo(other.getX(), other.getY());
        double collisionRange = this.getRadius() + other.getRadius();
        if (collisionTimeoutFramesRemaining == 0 && distance < collisionRange) {
            this.receiveDamage(other.getDamage());
            collidingOtherObject = other;
            collisionTimeoutFramesRemaining = COLLISION_TIMEOUT_FRAMES_TOTAL;
            initialCollisionTimeoutFramesRemaining = COLLISION_TIMEOUT_FRAMES_INITIAL;
        }
    }


    /**
     * Calculates the Euclidean distance of the car and another object.
     * @param otherX The x-coordinate of the other object.
     * @param otherY The y-coordinate of the other object.
     * @return The Euclidean distance between the car and the other object.
     */
    @Override
    public double getDistanceTo(double otherX, double otherY) {
        return Math.sqrt(Math.pow(otherX - this.x, 2) + Math.pow(otherY - this.y, 2));
    }

    /**
     * Gets the current x-coordinate of the car.
     * @return Current x-coordinate of the car.
     */
    @Override
    public int getX() {
        return x;
    }

    /**
     * Sets the current x-coordinate of the car.
     * @param x The value of x-coordinate to be set.
     */
    public void setX(int x) {
        this.x = x;
    }

    /**
     * Gets the current y-coordinate of the car.
     * @return Current y-coordinate of the car.
     */
    @Override
    public int getY() {
        return y;
    }

    /**
     * Sets the current y-coordinate of the car.
     * @param y The value of y-coordinate to be set.
     */
    public void setY(int y) {
        this.y = y;
    }

    /**
     * Gets the amount of damage that the car inflicts towards other object upon collision.
     * @return The amount of damage the car inflicts towards other object.
     */
    @Override
    public double getDamage() {
        return DAMAGE;
    }

    /**
     * Gets the radius of the car.
     * @return Radius of the car.
     */
    @Override
    public double getRadius() {
        return RADIUS;
    }

    /**
     * Gets the current health of the car.
     * @return The current health of the car.
     */
    @Override
    public double getCurrentHealth() {
        return currentHealth;
    }

    /**
     * Sets the fireEffectAdded boolean to true.
     * Used when rendering fire for car when its health is less than or equal to 0.
     */
    public void fireEffectWasAdded() {
        fireEffectAdded = true;
    }

    /**
     * Gets the boolean state of fireEffectAdded.
     * @return True if fire effect was added for the car, false otherwise.
     */
    public boolean isFireEffectAdded() {
        return fireEffectAdded;
    }
}
