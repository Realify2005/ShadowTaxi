import java.util.Properties;
import bagel.*;
import bagel.Image;

/**
 * Class for the taxi entity.
 */
public class Taxi extends Entity implements Damageable {

    // Constant that defines the horizontal and vertical speed of taxi.
    private final int SPEED_X;

    private boolean isTaxiMoved;
    private boolean hasDriver;
    private Passenger currentPassenger;
    private double currentHealth;
    private int initialCollisionTimeoutFramesRemaining;
    private int collisionTimeoutFramesRemaining;
    private Car collidingCar;

    private final String FONT_PATH;
    private final int FONT_SIZE;

    private final String TAXI_TEXT;
    private final int TAXI_TEXT_X;
    private final int TAXI_TEXT_Y;

    private final Image DAMAGED_IMAGE;
    private final double HEALTH;
    private final double DAMAGE;

    private final int SEPARATE_Y = 1;

    private final Gameplay GAMEPLAY;
    private final PowerUpState POWER_UP_STATE;

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

        int PROPS_TO_GAME_MULTIPLIER = 100;
        DAMAGED_IMAGE = new Image(gameProps.getProperty("gameObjects.taxi.damagedImage"));
        HEALTH = Double.parseDouble(gameProps.getProperty("gameObjects.taxi.health")) * PROPS_TO_GAME_MULTIPLIER;
        DAMAGE = Double.parseDouble(gameProps.getProperty("gameObjects.taxi.damage")) * PROPS_TO_GAME_MULTIPLIER;

        this.currentHealth = HEALTH;
    }

    @Override
    public void receiveDamage(double damage) {
        this.currentHealth -= damage;
        if (this.currentHealth < 0) {
            this.currentHealth = 0;
            hasDriver = false; // driver gets ejected
            currentPassenger = null; // passenger gets ejected
        }
    }

    @Override
    public void updateCollisionTimeoutFramesRemaining() {
        if (initialCollisionTimeoutFramesRemaining > 0) {
            initialCollisionTimeoutFramesRemaining--;
        }
        if (collisionTimeoutFramesRemaining > 0) {
            collisionTimeoutFramesRemaining--;
        }
    }

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

    @Override
    public boolean handleCollision(Car other) {

        if (other.getCurrentHealth() > 0) {
            double distance = getDistanceTo(other.getX(), other.getY());
            double collisionRange = this.getRadius() + other.getRadius();

            if (collisionTimeoutFramesRemaining == 0 && distance < collisionRange) {
                collidingCar = other;
                other.receiveCollision(this);
                if (!POWER_UP_STATE.isInvincibleActivated()) {
                    collisionTimeoutFramesRemaining = COLLISION_TIMEOUT_FRAMES_TOTAL;
                    initialCollisionTimeoutFramesRemaining = COLLISION_TIMEOUT_FRAMES_INITIAL;
                    this.receiveDamage(other.getDamage());
                }
                return true;
            }
        }

        return false;
    }

    /**
     * Constantly updates the taxi entity.
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
     */
    public void pickUpPassenger(Passenger passenger) {
        if (!passenger.isPickedUp() && isEmpty() && isAdjacentToPassenger(passenger)) {
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
     * Checks if taxi has collided with any power-ups.
     */
    public boolean collidedWith(PowerUp powerUp) {
        double collisionRange = RADIUS + powerUp.getRadius();
        return getDistanceTo(powerUp.getX(), powerUp.getY()) <= collisionRange;
    }

    /**
     * Helper function to check if taxi is adjacent to any passengers.
     */
    public boolean isAdjacentToPassenger(Passenger passenger) {
        return getDistanceTo(passenger.getX(), passenger.getY()) <= passenger.getDetectRadius();
    }

    public boolean isAdjacentToDriver(Driver driver) {
        return getDistanceTo(driver.getX(), driver.getY()) <= driver.getTaxiGetInRadius();
    }

    /**
     * Helper function to get distances from taxi to another object.
     * Used for distance between taxi and passenger, between taxi and trip end flag, and between taxi and coins.
     */
    @Override
    public double getDistanceTo(double targetX, double targetY) {
        return Math.sqrt(Math.pow((targetX - getX()), 2) + Math.pow((targetY - getY()), 2));
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
     */
    public boolean isPassengerMovingToFlag() {
        if (currentPassenger != null) {
            return currentPassenger.isMovingToFlag();
        }
        return false;
    }

    public boolean isEmpty() {
        return currentPassenger == null;
    }

    public void driverEntered() {
        this.hasDriver = true;
    }

    public boolean hasDriver() {
        return hasDriver;
    }

    @Override
    public double getDamage() {
        return DAMAGE;
    }

    @Override
    public double getRadius() {
        return RADIUS;
    }

    @Override
    public double getCurrentHealth() {
        return currentHealth;
    }

    public Passenger getCurrentPassenger() {
        return currentPassenger;
    }

    public void driverEjected() {
        hasDriver = false;
    }

    public void setCurrentPassenger(Passenger passenger) {
        currentPassenger = passenger;
    }
}
