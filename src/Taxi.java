import java.util.Properties;
import bagel.*;
import bagel.Image;

/**
 * Class for the taxi entity in the game.
 */
public class Taxi extends Entity implements Damageable {
    /**
     * The font path used for displaying taxi health information.
     */
    private final String FONT_PATH;

    /**
     * The font size used for displaying taxi health information.
     */
    private final int FONT_SIZE;

    /**
     * The horizontal speed of taxi.
     */
    private final int SPEED_X;

    /**
     * The text used to render taxi's health on screen.
     */
    private final String TAXI_TEXT;

    /**
     * The X-coordinate for displaying the taxi health information text.
     */
    private final int TAXI_TEXT_X;

    /**
     * The Y-coordinate for displaying the taxi health information text.
     */
    private final int TAXI_TEXT_Y;

    /**
     * The image of the taxi when it is broken.
     */
    private final Image DAMAGED_IMAGE;

    /**
     * The maximum (initial) health of the taxi.
     */
    private final double HEALTH;

    /**
     * The amount of damage the taxi inflicts onto other damageable objects upon collision.
     */
    private final double DAMAGE;

    /**
     * The amount of vertical pixels moved per frame while initial timeout collision frames is active.
     */
    private final int SEPARATE_Y = 1;

    /**
     * The instance that controls the entire gameplay logic.
     */
    private final Gameplay GAMEPLAY;

    /**
     * The instance which tracks all the currently active power-ups.
     */
    private final PowerUpState POWER_UP_STATE;

    /**
     * Boolean that indicates whether the taxi has been moved at a point in time.
     */
    private boolean isTaxiMoved;

    /**
     * Boolean that indicates whether taxi currently has a driver or not.
     */
    private boolean hasDriver;

    /**
     * The current passenger in the taxi.
     */
    private Passenger currentPassenger;

    /**
     * The current health of the taxi.
     */
    private double currentHealth;

    /**
     * The number of frames remaining to separate the taxi from another damageable object post-collision.
     */
    private int initialCollisionTimeoutFramesRemaining;

    /**
     * The number of frames remaining before the taxi can collide with another damageable object after any collision.
     */
    private int collisionTimeoutFramesRemaining;

    /**
     * The car that is currently colliding with the taxi.
     */
    private Car collidingCar;

    /**
     * Constructor for taxi entity.
     * @param x The X-coordinate of the taxi.
     * @param y The Y-coordinate of the taxi.
     * @param gameplay The instance that controls the entire gameplay logic.
     * @param powerUpState The instance which tracks all the currently active power-ups.
     * @param gameProps The properties object containing game configuration values.
     * @param messageProps The properties object containing rendered text configuration values.
     */
    public Taxi(int x, int y, Gameplay gameplay, PowerUpState powerUpState, Properties gameProps, Properties messageProps) {
        super(x, y, gameProps, "gameObjects.taxi.image", "gameObjects.taxi.radius");
        this.isTaxiMoved = false;
        this.hasDriver = false;
        this.currentPassenger = null;
        this.GAMEPLAY = gameplay;
        this.POWER_UP_STATE = powerUpState;

        SPEED_X = Integer.parseInt(gameProps.getProperty("gameObjects.taxi.speedX"));

        FONT_SIZE = Integer.parseInt(gameProps.getProperty("gamePlay.info.fontSize"));
        FONT_PATH = gameProps.getProperty("font");

        TAXI_TEXT = messageProps.getProperty("gamePlay.taxiHealth");
        TAXI_TEXT_X = Integer.parseInt(gameProps.getProperty("gamePlay.taxiHealth.x"));
        TAXI_TEXT_Y = Integer.parseInt(gameProps.getProperty("gamePlay.taxiHealth.y"));

        int PROPS_TO_GAME_MULTIPLIER = 100; // The game properties stores health and damage as (value / 100).
        DAMAGED_IMAGE = new Image(gameProps.getProperty("gameObjects.taxi.damagedImage"));
        HEALTH = Double.parseDouble(gameProps.getProperty("gameObjects.taxi.health")) * PROPS_TO_GAME_MULTIPLIER;
        DAMAGE = Double.parseDouble(gameProps.getProperty("gameObjects.taxi.damage")) * PROPS_TO_GAME_MULTIPLIER;

        this.currentHealth = HEALTH;
    }

    /**
     * Updates the current health of taxi after some damage is inflicted on it.
     * If taxi is broken, mark the driver and passenger as ejected.
     * @param damage The damage inflicted onto the taxi.
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
     * Separates the taxi from another object (post-collision during initial timeout frames).
     * For 10 frames, moves the taxi vertically upwards if it is above the other object, or vice versa.
     * @param other The other object that this taxi has collided with.
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
     * Checks if a collision has occurred that involves this taxi.
     * A collision has occurred if:
     * 1. The other car's health is greater than or equal to 0.
     * 2. This taxi has no more collision timeout frames remaining.
     * 3. The distance between this taxi and other car is less than the combined collision radius of both objects.
     * @param other The other car that the taxi has potentially collided with
     * @return True if a legal collision has occurred, false otherwise.
     */
    @Override
    public boolean handleCollision(Car other) {
        if (other.getCurrentHealth() > 0) {
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
     * Calls another method to continue separation of object from collided object if still in initial timeout.
     * If not currently in collision timeout, then moves the object in y direction according to its fixed speed.
     * Renders the object where necessary.
     * Moves the taxi according to player's mouse/keyboard input.
     * Renders the health of taxi on top right of screen.
     * @param input The current mouse/keyboard input.
     */
    @Override
    public void update(Input input) {
        draw();
        checkIsCurrentPassengerDroppedOff();
        updateCollisionTimeoutFramesRemaining();
        separateFromObject(collidingCar);
        isTaxiMoved = false;

        if (hasDriver) {
            if (input.isDown(Keys.LEFT)) {
                moveLeft();
                isTaxiMoved = true;
            } else if (input.isDown(Keys.RIGHT)) {
                moveRight();
                isTaxiMoved = true;
            } else if (input.isDown(Keys.UP)) {
                // In reality, it is only the background that has moved. However, for this purpose taxi "has" moved.
                isTaxiMoved = true;
            }

            // If taxi has not moved in a unit time, check if taxi can pick up a passenger or drop a passenger to the flag.
            if (!isTaxiMoved) {
                GAMEPLAY.checkIfTaxiIsAdjacentToPassengerOrFlag();
            }
        } else {
            if (input.isDown(Keys.UP)) {
                moveDown();
            }
        }

        if (currentHealth > 0) {
            renderHealth();
        }
    }

    /**
     * Moves the taxi to the left.
     */
    private void moveLeft() {
        setX(getX() - SPEED_X);
    }

    /**
     * Moves the taxi to the right.
     */
    private void moveRight() {
        setX(getX() + SPEED_X);
    }

    /**
     * Draws the taxi entity.
     * Image rendered differs according to taxi's current health.
     */
    @Override
    public void draw() {
        (currentHealth > 0 ? IMAGE : DAMAGED_IMAGE).draw(getX(), getY());
    }

    private void renderHealth() {
        Font font = new Font(FONT_PATH, FONT_SIZE);
        font.drawString( TAXI_TEXT + currentHealth, TAXI_TEXT_X, TAXI_TEXT_Y);
    }

    /**
     * Picks up the passenger if the conditions are fulfilled.
     * Taxi must have stopped (i.e. be stationary).
     * Taxi must have no current passenger (i.e. is empty).
     * Taxi must be adjacent to passenger.
     * @param passenger Passenger to be picked up by taxi.
     */
    public void pickUpPassenger(Passenger passenger) {
        if (!passenger.isPickedUp() && !hasPassenger() && isAdjacentToPassenger(passenger)) {
            // Move the passenger to the taxi
            passenger.pickUp(getX(), getY());
            if (passenger.isPickedUp()) {
                this.currentPassenger = passenger;
                GAMEPLAY.startTrip(passenger);
            }
        }
    }

    /**
     * Drops off the passenger to the trip end flag, if the conditions are fulfilled.
     * Taxi must have stopped (i.e. be stationary).
     * Taxi must be above the flag
     *      OR
     * Taxi must be adjacent to the trip end flag.
     * @param flag The trip end flag where the passenger should move towards to.
     */
    public void dropOffPassenger(TripEndFlag flag) {
        if (currentPassenger != null) {
            if (getY() <= flag.getY() || getDistanceTo(flag.getX(), flag.getY()) <= flag.getRadius()) {
                if (!currentPassenger.isMovingToFlag()) {
                    // Only need to initiate drop off, the rest of movement is handled by another function
                    currentPassenger.dropOff(flag);
                }
            }
        }
    }

    /**
     * Checks if taxi has collided with (picked up) any power-ups.
     * @param powerUp The power-up that taxi has potentially collided with (picked up).
     * @return True if taxi has indeed collided with (can pick up) a power-up, false otherwise.
     */
    public boolean collidedWith(PowerUp powerUp) {
        double collisionRange = RADIUS + powerUp.getRadius();
        return getDistanceTo(powerUp.getX(), powerUp.getY()) <= collisionRange;
    }

    /**
     * Helper function to check if the taxi is adjacent to any passengers.
     * @param passenger The passenger that the taxi may be adjacent to.
     * @return True if taxi is adjacent to passenger (and thus can pick up), false otherwise.
     */
    public boolean isAdjacentToPassenger(Passenger passenger) {
        return getDistanceTo(passenger.getX(), passenger.getY()) <= passenger.getDetectRadius();
    }

    /**
     * Helper function to check if the taxi is adjacent to the driver.
     * @param driver The driver that the taxi may be adjacent to.
     * @return True if taxi is adjacent to the driver (and thus driver can enter taxi), false otherwise.
     */
    public boolean isAdjacentToDriver(Driver driver) {
        return getDistanceTo(driver.getX(), driver.getY()) <= driver.getTaxiGetInRadius();
    }

    /**
     * Calculates the Euclidean distance of this taxi and another object.
     * @param otherX The x-coordinate of the other object.
     * @param otherY The y-coordinate of the other object.
     * @return The Euclidean distance between this taxi and the other object.
     */
    @Override
    public double getDistanceTo(double otherX, double otherY) {
        return Math.sqrt(Math.pow((otherX - getX()), 2) + Math.pow((otherY - getY()), 2));
    }

    /**
     * Sets currentPassenger to null if passenger has already left the taxi.
     */
    private void checkIsCurrentPassengerDroppedOff() {
        if (currentPassenger != null && currentPassenger.isMovingToFlag()) {
            this.currentPassenger = null;
        }
    }

    /**
     * Getter function to check if passenger is moving to flag.
     *  (i.e. leaving the taxi and approaching the trip end flag).
     * @return True if passenger is moving to flag, false otherwise.
     */
    public boolean isPassengerMovingToFlag() {
        if (currentPassenger != null) {
            return currentPassenger.isMovingToFlag();
        }
        return false;
    }

    /**
     * Gets the amount of damage that the taxi inflicts towards other object upon collision.
     * @return The amount of damage the taxi inflicts towards other object.
     */
    @Override
    public double getDamage() {
        return DAMAGE;
    }

    /**
     * Gets the radius of the taxi.
     * @return Radius of the taxi.
     */
    @Override
    public double getRadius() {
        return RADIUS;
    }

    /**
     * Gets the current health of the taxi.
     * @return The current health of the taxi.
     */
    @Override
    public double getCurrentHealth() {
        return currentHealth;
    }

    /**
     * Sets the taxi to having a driver.
     * This function is called when the driver has entered the taxi.
     */
    public void driverEntered() {
        this.hasDriver = true;
    }

    /**
     * Checks if the taxi currently has a driver.
     * @return true if the taxi currently has a driver, false otherwise.
     */
    public boolean hasDriver() {
        return hasDriver;
    }

    /**
     * Checks if the taxi has a passenger.
     * @return true if the taxi has a passenger, false otherwise.
     */
    public boolean hasPassenger() {
        return currentPassenger != null;
    }

    /**
     * Gets the current passenger of the taxi.
     * @return the current passenger of the taxi.
     */
    public Passenger getCurrentPassenger() {
        return currentPassenger;
    }

    /**
     * Sets the taxi to not having a driver.
     * This function is called when the taxi is broken and thus the driver is ejected from the taxi.
     */
    public void driverEjected() {
        hasDriver = false;
    }

    /**
     * Sets the taxi to not having a passenger.
     * This function is called when the taxi is broken and thus the passenger is ejected from the taxi.
     */
    public void passengerEjected() {
        currentPassenger = null;
    }

    /**
     * Sets the current passenger of the taxi.
     * @param passenger the passenger to be set.
     */
    public void setCurrentPassenger(Passenger passenger) {
        currentPassenger = passenger;
    }

}
