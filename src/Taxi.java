import java.util.Properties;
import bagel.*;
import bagel.Image;

/**
 * Class for the taxi entity.
 */
public class Taxi extends Entity {

    // Constant that defines the horizontal and vertical speed of taxi.
    private final int SPEED_X;

    private boolean isTaxiMoved;
    private boolean hasDriver;
    private Passenger currentPassenger;
    private double currentHealth;

    private final String FONT_PATH;
    private final int FONT_SIZE;

    private final String TAXI_TEXT;
    private final int TAXI_TEXT_X;
    private final int TAXI_TEXT_Y;

    private final Image DAMAGED_IMAGE;
    private final double HEALTH;
    private final double DAMAGE;
    private final int NEXT_SPAWN_MIN_Y;
    private final int NEXT_SPAWN_MAX_Y;

    private final Gameplay GAMEPLAY;

    public Taxi(int x, int y, Gameplay gameplay, Properties gameProps, Properties messageProps) {
        super(x, y, gameProps, "gameObjects.taxi.image", "gameObjects.taxi.radius");
        this.isTaxiMoved = false;
        this.hasDriver = false;
        this.currentPassenger = null;
        this.GAMEPLAY = gameplay;

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
        NEXT_SPAWN_MIN_Y = Integer.parseInt(gameProps.getProperty("gameObjects.taxi.nextSpawnMinY"));
        NEXT_SPAWN_MAX_Y = Integer.parseInt(gameProps.getProperty("gameObjects.taxi.nextSpawnMaxY"));

        this.currentHealth = HEALTH;
    }

    /**
     * Constantly updates the taxi entity.
     */
    @Override
    public void update(Input input) {
        draw();
        checkIsCurrentPassengerDroppedOff();
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

        renderHealth();
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
        IMAGE.draw(getX(), getY());
        // Damaged car etc logic here
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
        return getDistanceTo(passenger.getX(), passenger.getY()) <= passenger.getRadius();
    }

    public boolean isAdjacentToDriver(Driver driver) {
        return getDistanceTo(driver.getX(), driver.getY()) <= driver.getTaxiGetInRadius();
    }

    /**
     * Helper function to get distances from taxi to another object.
     * Used for distance between taxi and passenger, between taxi and trip end flag, and between taxi and coins.
     */
    private double getDistanceTo(double targetX, double targetY) {
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

}
