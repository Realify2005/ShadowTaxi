import bagel.*;
import java.util.Properties;

/**
 * Class representing the driver entity. Driver needs to be inside a taxi for a taxi to work.
 * Driver needs to be inside a taxi to take in passengers and start trips.
 * Driver can be ejected out of the taxi if taxi is broken.
 * Driver can take damage from moving cars and fireballs when it is outside the taxi.
 * If driver's health is less than or equal to 0, the game ends in a lost.
 * Driver can collect coins and invincible powers while being outside of taxi and its effect will transfer over to the
 * new taxi.
 * Driver can move up, down, left, and right while being outside taxi, and health is displayed on top right of screen.
 */
public class Driver extends Entity implements Damageable, Ejectable {

    /**
     * The font size used to render driver's health on screen.
     */
    private final int FONT_SIZE;

    /**
     * The file path to the font used to render driver's health on screen.
     */
    private final String FONT_PATH;

    /**
     * The driver's walking speed in the X (horizontal) direction.
     */
    private final int WALK_SPEED_X;

    /**
     * The driver's walking speed in the Y (vertical) direction.
     */
    private final int WALK_SPEED_Y;

    /**
     * The radius within which the driver can enter a non-broken taxi.
     */
    private final int TAXI_GET_IN_RADIUS;

    /**
     * The maximum health of driver (i.e. initial health of driver upon creation).
     */
    private final double HEALTH;

    /**
     * The text rendered on the screen representing the driver's health.
     */
    private final String DRIVER_TEXT;

    /**
     * The X-coordinate where the driver's health text is rendered.
     */
    private final int DRIVER_TEXT_X;

    /**
     * The Y-coordinate where the driver's health text is rendered.
     */
    private final int DRIVER_TEXT_Y;

    /**
     * The amount of pixels the driver is separated from other colliding objects horizontally per frame.
     */
    private final int SEPARATE_X = 2;

    /**
     * The amount of pixels the driver is separated from other colliding objects vertically per frame.
     */
    private final int SEPARATE_Y = 2;

    /**
     * The amount of pixels the driver is ejected horizontally when being ejected from a broken taxi.
     */
    private final int EJECT_X = 50;

    /**
     * The amount of damage the driver inflicts to other damageable objects.
     */
    private final int DAMAGE = 0;

    /**
     * Boolean indicating whether the driver is currently inside a taxi.
     */
    private boolean inTaxi;

    /**
     * The current health of the driver.
     */
    private double currentHealth;

    /**
     * The car the driver has most recently collided with, if any.
     */
    private Car collidingCar;

    /**
     * The number of frames remaining to separate the driver from another damageable object post-collision.
     */
    private int initialCollisionTimeoutFramesRemaining;

    /**
     * The number of frames remaining before the driver can collide with another damageable object after any collision.
     */
    private int collisionTimeoutFramesRemaining;

    /**
     * Power-up state class used to track the state of power-ups in-game (i.e. if any power-ups are currently active).
     */
    private final PowerUpState POWER_UP_STATE;

    /**
     * Constructor for Driver class.
     * Initialises its starting (x, y) position, image, radius, and starting health.
     * @param x The starting X position of the driver entity.
     * @param y The starting Y position of the driver entity.
     * @param powerUpState The class used to track the state of power-ups in the game.
     * @param gameProps The game properties object containing various game configuration values.
     * @param messageProps The message properties object containing various in-game text configuration values.
     */
    public Driver(int x, int y, PowerUpState powerUpState, Properties gameProps, Properties messageProps) {
        super(x, y, gameProps, "gameObjects.driver.image", "gameObjects.driver.radius");

        int PROPS_TO_GAME_MULTIPLIER = 100; // The game properties stores health and damage as (value / 100).
        WALK_SPEED_X = Integer.parseInt(gameProps.getProperty("gameObjects.driver.walkSpeedX"));
        WALK_SPEED_Y = Integer.parseInt(gameProps.getProperty("gameObjects.driver.walkSpeedY"));
        TAXI_GET_IN_RADIUS = Integer.parseInt(gameProps.getProperty("gameObjects.driver.taxiGetInRadius"));
        HEALTH = Double.parseDouble(gameProps.getProperty("gameObjects.driver.health")) * PROPS_TO_GAME_MULTIPLIER;

        FONT_SIZE = Integer.parseInt(gameProps.getProperty("gamePlay.info.fontSize"));
        FONT_PATH = gameProps.getProperty("font");

        DRIVER_TEXT = messageProps.getProperty("gamePlay.driverHealth");
        DRIVER_TEXT_X = Integer.parseInt(gameProps.getProperty("gamePlay.driverHealth.x"));
        DRIVER_TEXT_Y = Integer.parseInt(gameProps.getProperty("gamePlay.driverHealth.y"));

        this.POWER_UP_STATE = powerUpState;
        this.currentHealth = HEALTH;
        this.inTaxi = false;
    }

    /**
     * Updates the current health of the driver after some damage is inflicted on it.
     * @param damage The damage inflicted onto the driver.
     */
    @Override
    public void receiveDamage(double damage) {
        this.currentHealth -= damage;
        if (this.currentHealth < 0) {
            this.currentHealth = 0;
        }
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
     * Separates the driver from another object (post-collision during initial timeout frames).
     * For 10 frames, moves the driver vertically upwards if it is above the other object, or vice versa.
     * @param other The other object that the driver has collided with.
     */
    @Override
    public void separateFromObject(Damageable other) {
        if (initialCollisionTimeoutFramesRemaining > 0 && initialCollisionTimeoutFramesRemaining <= 10) {
            int otherX = other.getX();
            int otherY = other.getY();
            int thisX = this.getX();
            int thisY = this.getY();

            if (thisX < otherX) {
                this.setX(thisX - SEPARATE_X);
            } else {
                this.setX(thisX + SEPARATE_X);
            }
            if (thisY < otherY) {
                this.setY(thisY - SEPARATE_Y);
            } else {
                this.setY(thisY + SEPARATE_Y);
            }
        }
    }

    /**
     * Checks if a collision has occurred that involves this driver entity.
     * A collision has occurred if:
     * 1. The other car's health is greater than or equal to 0.
     * 2. This driver entity has no more collision timeout frames remaining.
     * 3. The distance between this (driver) and other car is less than the combined collision radius of both objects.
     * @param other The other car that car has potentially collided with
     * @return True if a legal collision has occurred, false otherwise.
     */
    @Override
    public boolean handleCollision(Car other) {
        if (other.getCurrentHealth() > 0 && !inTaxi) {
            double distance = getDistanceTo(other.getX(), other.getY());
            double collisionRange = this.getRadius() + other.getRadius();
            if (collisionTimeoutFramesRemaining == 0 && distance < collisionRange) {
                collidingCar = other;
                other.receiveCollision(this); // Other entity always receives damage regardless of active power-up.
                if (POWER_UP_STATE.isInvincibleActivated()) {
                    // Invincible power is active, so a collision has not happened.
                    return false;
                }
                collisionTimeoutFramesRemaining = COLLISION_TIMEOUT_FRAMES_TOTAL;
                initialCollisionTimeoutFramesRemaining = COLLISION_TIMEOUT_FRAMES_INITIAL;
                this.receiveDamage(other.getDamage());
                return true;
            }
        }
        return false;
    }

    /**
     * Update the collision timeout frames remaining if it is currently in collision timeout.
     * If not in taxi, move the driver according to user's input up, down, left, and right.
     * Keep driver updated with taxi's position if it is inside a taxi.
     * During an initial collision timeout of 10 frames, continue to separate the driver from the other collided object.
     * Renders the object if it's not in taxi, also render driver's health value.
     * @param input The current mouse/keyboard input.
     * @param taxi The currently active taxi entity.
     */
    public void update(Input input, Taxi taxi) {
        if (!inTaxi) {
            draw();
            if (input.isDown(Keys.UP)) {
                moveUp();
            }
            if (input.isDown(Keys.DOWN)) {
                moveDown();
            }
            if (input.isDown(Keys.LEFT)) {
                moveLeft();
            }
            if (input.isDown(Keys.RIGHT)) {
                moveRight();
            }
        }
        updateCollisionTimeoutFramesRemaining();
        updateWithTaxiMovement(taxi.getX(), taxi.getY());
        separateFromObject(collidingCar);
        renderHealth();
    }

    /**
     * Moves the driver entity up.
     */
    private void moveUp() {
        setY(getY() - WALK_SPEED_Y);
    }

    /**
     * Moves the driver entity down.
     */
    @Override
    public void moveDown() {
        setY(getY() + WALK_SPEED_Y);
    }

    /**
     * Moves the driver entity left.
     */
    private void moveLeft() {
        setX(getX() - WALK_SPEED_X);
    }

    /**
     * Moves the driver entity right.
     */
    private void moveRight() {
        setX(getX() + WALK_SPEED_X);
    }

    /**
     * Helper function to keep driver's coordinates up to date with taxi's coordinates.
     * @param taxiX The current X position of taxi.
     * @param taxiY The current Y position of taxi.
     */
    private void updateWithTaxiMovement(int taxiX, int taxiY) {
        if (inTaxi) {
            setX(taxiX);
            setY(taxiY);
        }
    }

    /**
     * Helper function to calculate the Euclidean distance to any other coordinates.
     * @param targetX The X position of the other object/entity.
     * @param targetY The Y position of the other object/entity.
     * @return The Euclidean distance between this driver and target object/entity.
     */
    @Override
    public double getDistanceTo(double targetX, double targetY) {
        return Math.sqrt(Math.pow((targetX - getX()), 2) + Math.pow((targetY - getY()), 2));
    }

    /**
     * Checks if driver has collided with any power-ups.
     * @param powerUp The power-up object (i.e. coin or invincible power).
     * @return True if driver has collided with any power-up, false otherwise.
     */
    public boolean collidedWith(PowerUp powerUp) {
        double collisionRange = RADIUS + powerUp.getRadius();
        return getDistanceTo(powerUp.getX(), powerUp.getY()) <= collisionRange;
    }

    /**
     * Draws the driver image if it is not inside a taxi.
     */
    @Override
    public void draw() {
        if (!inTaxi) {
            IMAGE.draw(getX(), getY());
        }
    }

    /**
     * Renders the driver's health on the top right of ongoing game screen.
     */
    private void renderHealth() {
        Font font = new Font(FONT_PATH, FONT_SIZE);
        font.drawString( DRIVER_TEXT + currentHealth, DRIVER_TEXT_X, DRIVER_TEXT_Y);
    }

    /**
     * Ejects the driver from the taxi upon taxi being broken.
     */
    @Override
    public void eject() {
        if (inTaxi) {
            inTaxi = false;
            setX(getX() - EJECT_X);
        }
    }

    /**
     * Gets the amount of damage that the driver inflicts towards other object upon collision.
     * @return The amount of damage the driver inflicts towards other object.
     */
    @Override
    public double getDamage() {
        return DAMAGE;
    }

    /**
     * Gets the radius of the driver.
     * @return Radius of the driver.
     */
    @Override
    public double getRadius() {
        return RADIUS;
    }

    /**
     * Gets the current health of the driver.
     * @return The current health of the driver.
     */
    @Override
    public double getCurrentHealth() {
        return currentHealth;
    }

    /**
     * Sets the inTaxi boolean to true.
     */
    public void enteredTaxi() { this.inTaxi = true; }

    /**
     * Gets the radius responsible for checking if driver can enter the taxi.
     * @return The taxi get in radius.
     */
    public int getTaxiGetInRadius() { return TAXI_GET_IN_RADIUS; }

    /**
     * Gets the vertical walk speed of the driver.
     * @return The vertical walk speed of the driver.
     */
    public int getWalkSpeedY() {
        return WALK_SPEED_Y;
    }
}
