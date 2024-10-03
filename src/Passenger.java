import java.util.Properties;
import bagel.*;
import bagel.Font;

/**
 * Class for the passenger entity.
 * Includes every single logic that involves the Passenger entity and its interactions with other game entities.
 */
public class Passenger extends Entity implements Damageable, Ejectable {
    /**
     * The size of the font used to display the passenger's health text.
     */
    private final int FONT_SIZE;

    /**
     * The file path to the font used for rendering the passenger's health text.
     */
    private final String FONT_PATH;

    /**
     * The base rate used for calculating the trip earnings.
     */
    private final double TRIP_RATE;

    /**
     * The amount of extra earnings for a passenger with priority 1.
     */
    private final int PRIORITY_RATE_1;

    /**
     * The amount of extra earnings for a passenger with priority 2.
     */
    private final int PRIORITY_RATE_2;

    /**
     * The amount of extra earnings for a passenger with priority 3.
     */
    private final int PRIORITY_RATE_3;

    /**
     * The X-coordinate speed of the passenger.
     */
    private final int WALK_SPEED_X;

    /**
     * The Y-coordinate speed of the passenger.
     */
    private final int WALK_SPEED_Y;

    /**
     * The collision radius of the passenger used for detecting collisions.
     */
    private final int COLLISION_RADIUS;

    /**
     * The initial health value of the passenger.
     */
    private final double HEALTH;

    /**
     * The difference in X-coordinate at which the passenger will be ejected from the original position of the taxi.
     */
    private final int EJECT_X = 100;

    /**
     * The amount of X-coordinate pixels the passenger moves per frame during initial collision timeout frames.
     */
    private final int SEPARATE_X = 2;

    /**
     * The amount of Y-coordinate pixels the passenger moves per frame during initial collision timeout frames.
     */
    private final int SEPARATE_Y = 2;

    /**
     * The amount of damage the passenger can inflict onto other damageable objects during a collision.
     */
    private final int DAMAGE = 0;

    /**
     * The original priority of the passenger.
     */
    private int originalPriority;

    /**
     * The current priority of the passenger.
     */
    private int priority;

    /**
     * The X-coordinate of the passenger's final trip destination.
     */
    private int endX;

    /**
     * The distance the passenger needs to travel along the Y-axis to reach their destination.
     */
    private int distanceY;

    /**
     * Boolean to indicate if the passenger has an Umbrella or not (not shown in the image).
     */
    private boolean hasUmbrella;

    /**
     * The final X-coordinate of the flag the passenger is moving towards after being dropped off.
     */
    private int finalFlagX;

    /**
     * The final Y-coordinate of the flag the passenger is moving towards after being dropped off.
     */
    private int finalFlagY;

    /**
     * Trip penalty imposed on the passenger, if any.
     */
    private double penalty;

    /**
     * Boolean indicating whether the passenger has been picked up by a taxi from the side road.
     */
    private boolean isPickedUp;

    /**
     * Boolean indicating whether the passenger is moving to its target flag position after being dropped off by the taxi.
     */
    private boolean isMovingToFlag;

    /**
     * Boolean indicating whether the passenger has been dropped off by a taxi.
     */
    private boolean isDroppedOff;

    /**
     * Boolean indicating that the passenger's priority has been decreased (due to a coin power-up).
     */
    private boolean isPriorityDecreased;

    /**
     * Boolean indicating whether the passenger is currently inside a taxi or not.
     */
    private boolean isInTaxi;

    /**
     * The total earnings for the passenger after completing a trip.
     */
    private double earnings;

    /**
     * Boolean indicating whether the passenger's trip earnings have been added to the total score.
     */
    private boolean isEarningsAdded;

    /**
     * Boolean indicating whether a penalty has been imposed on the passenger's trip, if any.
     */
    private boolean isPenaltyImposed;

    /**
     * The current health of the passenger.
     */
    private double currentHealth;

    /**
     * The last car object that collided with the passenger.
     */
    private Car collidingCar;

    /**
     * The number of frames remaining to separate the car from another damageable object post-collision.
     */
    private int initialCollisionTimeoutFramesRemaining;

    /**
     * The number of frames remaining before the car can collide with another damageable object after any collision.
     */
    private int collisionTimeoutFramesRemaining;

    /**
     * The power-up state class that tracks all current active power-ups such as coins.
     */
    private PowerUpState powerUpState;

    /**
     * The driver class that drives the taxi.
     */
    private Driver driver;


    /**
     * Constructor for the Passenger class.
     * @param x The initial X-coordinate of the passenger.
     * @param y The initial Y-coordinate of the passenger.
     * @param priority The priority of the passenger.
     * @param endX The final X-coordinate of the passenger's trip destination.
     * @param distanceY The minimum distance the passenger needs to travel along the Y-axis to complete a trip.
     * @param hasUmbrella Indicates if the passenger has an umbrella.
     * @param powerUpState The current power-up state affecting the passenger (e.g. is coin currently active or not).
     * @param gameProps The properties file containing various game configuration values.
     * @param messageProps The properties file containing text configuration values.
     */
    public Passenger(int x, int y, int priority, int endX, int distanceY, int hasUmbrella,
                     PowerUpState powerUpState, Properties gameProps, Properties messageProps) {
        super(x, y, gameProps,"gameObjects.passenger.image",
                "gameObjects.passenger.taxiDetectRadius");
        this.originalPriority = this.priority = priority;
        this.endX = endX;
        this.distanceY = distanceY;
        this.finalFlagX = -1; // has not been set
        this.finalFlagY = -1; // has not been set
        this.hasUmbrella = (hasUmbrella != 0); // Convert int from world file to boolean
        this.penalty = 0;
        this.powerUpState = powerUpState;
        this.isPickedUp = false;
        this.isDroppedOff = false;
        this.isMovingToFlag = false;
        this.isInTaxi = false;
        this.isPriorityDecreased = false;
        this.isEarningsAdded = false;
        this.isPenaltyImposed = false;

        WALK_SPEED_X = Integer.parseInt(gameProps.getProperty("gameObjects.passenger.walkSpeedX"));
        WALK_SPEED_Y = Integer.parseInt(gameProps.getProperty("gameObjects.passenger.walkSpeedY"));

        FONT_SIZE = Integer.parseInt(gameProps.getProperty("gameObjects.passenger.fontSize"));
        FONT_PATH = gameProps.getProperty("font");

        TRIP_RATE = Double.parseDouble(gameProps.getProperty("trip.rate.perY"));
        PRIORITY_RATE_1 = Integer.parseInt(gameProps.getProperty("trip.rate.priority1"));
        PRIORITY_RATE_2 = Integer.parseInt(gameProps.getProperty("trip.rate.priority2"));
        PRIORITY_RATE_3 = Integer.parseInt(gameProps.getProperty("trip.rate.priority3"));

        int PROPS_TO_GAME_MULTIPLIER = 100; // The game properties stores health and damage as (value / 100).
        COLLISION_RADIUS = Integer.parseInt(gameProps.getProperty("gameObjects.passenger.radius"));
        HEALTH = Double.parseDouble(gameProps.getProperty("gameObjects.passenger.health")) * PROPS_TO_GAME_MULTIPLIER;

        this.currentHealth = HEALTH;
        this.earnings = calculateEarnings();
    }

    /**
     * Updates the current health of passenger after some damage is inflicted on it.
     * @param damage The damage inflicted onto the passenger.
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
     * Separates the passenger from another object (post-collision during initial timeout frames).
     * For 10 frames, moves the passenger vertically upwards if it is above the other object, or vice versa.
     * @param other The other object that this passenger has collided with.
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
     * Checks if a collision has occurred that involves this passenger.
     * A collision has occurred if:
     * 1. The other car's health is greater than or equal to 0.
     * 2. This passenger has no more collision timeout frames remaining.
     * 3. The distance between this passenger and other car is less than the combined collision radius of both objects.
     * @param other The other car that this passenger has potentially collided with
     * @return True if a legal collision has occurred, false otherwise.
     */
    @Override
    public boolean handleCollision(Car other) {
        if (other.getCurrentHealth() > 0 && !isInTaxi) {
            double distance = getDistanceTo(other.getX(), other.getY());
            double collisionRange = this.getRadius() + other.getRadius();
            if (collisionTimeoutFramesRemaining == 0 && distance < collisionRange) {
                this.receiveDamage(other.getDamage());
                collidingCar = other;
                other.receiveCollision(this);
                collisionTimeoutFramesRemaining = COLLISION_TIMEOUT_FRAMES_TOTAL;
                initialCollisionTimeoutFramesRemaining = COLLISION_TIMEOUT_FRAMES_INITIAL;
                return true;
            }
        }
        return false;
    }

    /**
     * Constantly updates the passenger entity.
     * Controls rendering, updating of collision timeouts frames, priority change according to movement,
     * and movement according to other active entities such as driver as well as current user keyboard input.
     * @param input The user's mouse/keyboard input.
     * @param isRaining True if the weather is currently raining, false otherwise.
     */
    public void update(Input input, boolean isRaining) {
        draw(); // Draws the passenger entity.
        displayEarnings(); // Displays the expected earnings for each passenger entity.
        if (input.isDown(Keys.UP)) {
            moveDown();
            moveFinalFlagPositionDown();
        }
        // Make sure passenger coordinates are up-to-date with taxi's coordinates if they are on an ongoing trip.
        updateWithDriverMovement(driver.getX(), driver.getY());

        // Decrease passenger's priority number if passenger is on an ongoing trip and coin effect is in place
        if (this.isPickedUp && powerUpState.isCoinActivated()) {
            this.decreasePriority();
        }

        if (!hasUmbrella && !isDroppedOff) {
            // Change passenger's priority according to weather changes, and then recalculate earnings.
            if (isRaining) {
                this.priority = 1;
            } else {
                this.priority = originalPriority;
            }
            this.earnings = calculateEarnings();
        }
        updateCollisionTimeoutFramesRemaining();
        separateFromObject(collidingCar);
    }

    /**
     * Calculate earnings for passenger.
     * @return Current earnings for this passenger entity.
     */
    public double calculateEarnings() {
        double updatedEarnings = distanceY * TRIP_RATE + getPriorityRate() - penalty;
        if (updatedEarnings >= 0) {
            return updatedEarnings;
        } else {
            // Earnings cannot be negative
            return 0;
        }
    }

    /**
     * Helper function to get priority rate according to their priority number.
     * @return The priority rate.
     */
    private int getPriorityRate() {
        return switch (priority) {
            case 1 -> PRIORITY_RATE_1;
            case 2 -> PRIORITY_RATE_2;
            case 3 -> PRIORITY_RATE_3;
            default -> 0; // Put default as 0, even though per assignment specifications this should not be possible
        };
    }

    /**
     * Draws the expected earnings for all passengers that are not picked up on the side road.
     */
    private void displayEarnings() {
        if (!isPickedUp) {
            double preEarnings = calculateEarnings();
            Font font = new Font(FONT_PATH, FONT_SIZE);
            font.drawString(String.format("%.1f", preEarnings), getX() - 100, getY()); // Draw estimated earnings text
            font.drawString(Integer.toString(priority), getX() - 30, getY()); // Draw priority text
        }
    }

    /**
     * Slowly moves the passenger towards the taxi.
     * @param taxiX The current X-coordinate of the taxi.
     * @param taxiY The current Y-coordinate of the taxi.
     */
    private void moveTowardsTaxi(double taxiX, double taxiY) {
        if (!isPickedUp) {
            if (getX() < taxiX) {
                setX(getX() + WALK_SPEED_X);
            } else if (getX() > taxiX) {
                setX(getX() - WALK_SPEED_X);
            }

            if (getY() < taxiY) {
                setY(getY() + WALK_SPEED_Y);
            } else if (getY() > taxiY) {
                setY(getY() - WALK_SPEED_Y);
            }

            // Check if the passenger can now be picked up by taxi (i.e. coordinates are equal to taxi)
            if (getDistanceTo(taxiX, taxiY) == 0) {
                this.isPickedUp = true;  // Passenger is picked up
                this.isInTaxi = true;
            }
        }
    }

    /**
     * Slowly moves the passenger towards the trip end flag.
     */
    private void moveTowardsFlag() {
        if (isPickedUp && !isDroppedOff) {

            isMovingToFlag = true;

            if (getX() < finalFlagX) {
                setX(getX() + WALK_SPEED_X);
            } else if (getX() > finalFlagX) {
                setX(getX() - WALK_SPEED_X);
            }

            if (getY() < finalFlagY) {
                setY(getY() + WALK_SPEED_Y);
            } else if (getY() > finalFlagY) {
                setY(getY() - WALK_SPEED_Y);
            }

            if (getX() == finalFlagX && getY() == finalFlagY) {
                this.isDroppedOff = true; // Passenger is dropped off
            }
        }
    }

    /**
     * Calculates the Euclidean distance of this passenger and another object.
     * @param otherX The x-coordinate of the other object.
     * @param otherY The y-coordinate of the other object.
     * @return The Euclidean distance between this passenger and the other object.
     */
    @Override
    public double getDistanceTo(double otherX, double otherY) {
        return Math.sqrt(Math.pow(otherX - getX(), 2) + Math.pow(otherY - getY(), 2));
    }

    /**
     * Helper function to keep passenger's coordinates up to date with taxi's coordinate
     * @param driverX The current X-coordinate of driver.
     * @param driverY The current Y-coordinate of driver.
     */
    private void updateWithDriverMovement(int driverX, int driverY) {
        if (isPickedUp && !isMovingToFlag) {
            setX(driverX);
            setY(driverY);
        }
    }

    /**
     * Picks up the passenger (i.e. towards the taxi).
     * @param taxiX The current X-coordinate of taxi.
     * @param taxiY The current Y-coordinate of taxi.
     */
    public void pickUp(double taxiX, double taxiY) {
        if (!isPickedUp) {
            moveTowardsTaxi(taxiX, taxiY);
        }
    }

    /**
     * Drops off the passenger (i.e. from the taxi).
     * @param flag The trip end flag entity where the passenger should go towards to.
     */
    public void dropOff(TripEndFlag flag) {
        if (isPickedUp && !isDroppedOff) {
            if ((finalFlagX == -1) && (finalFlagY == -1)) {
                finalFlagX = flag.getX();
                finalFlagY = flag.getY();
            }
            moveTowardsFlag();
        }

        // No longer render the flag ONLY IF taxi has not picked up another passenger before
        // the old passenger arrives to the trip end flag.
        if (isDroppedOff && (getY() == flag.getY())) {
            deactivate(flag);
        }
    }

    /**
     * Draws the passenger entity.
     */
    @Override
    public void draw() {
        if (!isPickedUp || isMovingToFlag || (!isInTaxi && isDroppedOff)) {
            IMAGE.draw(getX(), getY());
        }
    }

    /**
     * Decreases the priority number of the passenger (i.e. increases its priority)
     */
    public void decreasePriority() {
        // Only can decrease passenger's priority maximum once
        if (!isPriorityDecreased && originalPriority > 1) {
            originalPriority--;
            // Recalculate earnings after decreasing priority due to coins effect
            this.earnings = calculateEarnings();

            if (hasUmbrella) {priority = originalPriority;} // These passengers' priority are not affected by weather
        }
        isPriorityDecreased = true;
    }

    /**
     * Make sure the final flag end position follows key up movement and behaves as intended.
     */
    private void moveFinalFlagPositionDown() {
        if (finalFlagY != -1) {
            finalFlagY += SCROLL_SPEED;
        }
    }

    /**
     * Deactivate the flag when passenger hits it so that it disappears.
     * @param flag The trip end flag that the passenger is standing on currently (given that passenger has reached flag).
     */
    private void deactivate(TripEndFlag flag) {
        flag.deactivate();
    }

    /**
     * Sets the penalty for this passenger's trip and recalculates the earnings.
     * @param penalty the penalty amount to be imposed.
     */
    public void setPenalty(double penalty) {
        this.penalty = penalty;
        this.earnings = calculateEarnings();
        isPenaltyImposed = true;
    }

    /**
     * Ejects the passenger from the taxi upon taxi being broken.
     */
    @Override
    public void eject() {
        isInTaxi = false;
        setX(getX() - EJECT_X);
    }

    /**
     * Gets the amount of damage that this passenger inflicts towards other object upon collision.
     * @return The amount of damage that this passenger inflicts towards other object.
     */
    @Override
    public double getDamage() {
        return DAMAGE;
    }

    /**
     * Gets the current health of the passenger.
     * @return The current health of the passenger.
     */
    @Override
    public double getCurrentHealth() {
        return currentHealth;
    }

    /**
     * Gets the radius of the passenger.
     * @return Radius of the passenger.
     */
    @Override
    public double getRadius() {
        return COLLISION_RADIUS;
    }

    /**
     * Gets the minimum X-coordinate where the trip can end.
     * @return The minimum X-coordinate where the trip can end.
     */
    public int getEndX() {
        return endX;
    }

    /**
     * Gets the minimum Y-distance before the trip can end.
     * @return The minimum Y-distance before the trip can end.
     */
    public int getDistanceY() {
        return distanceY;
    }

    /**
     * Gets the priority level of this passenger.
     * @return The priority level of this passenger.
     */
    public int getPriority() {
        return priority;
    }

    /**
     * Gets the total earnings for the trip.
     * @return The total earnings for the trip.
     */
    public double getEarnings() {
        return earnings;
    }

    /**
     * Checks if the passenger has been picked up from its initial position on the side road.
     * @return True if the passenger has been picked up, false otherwise.
     */
    public boolean isPickedUp() {
        return isPickedUp;
    }

    /**
     * Checks if the earnings for this passenger trip have been added to the total earnings
     * @return True if earnings has been added, false otherwise.
     */
    public boolean isEarningsAdded() {
        return isEarningsAdded;
    }

    /**
     * Set the passenger earnings to have been added to the total earnings.
     */
    public void addedEarnings() {
        this.isEarningsAdded = true;
    }

    /**
     * Checks if this passenger is moving towards its trip end flag.
     * @return True if passenger is moving towards its trip end flag, false otherwise.
     */
    public boolean isMovingToFlag() {
        return isMovingToFlag;
    }

    /**
     * Checks if a penalty (if any) has been imposed onto the passenger's trip.
     * @return True if penalty has been imposed, false otherwise.
     */
    public boolean isPenaltyImposed() {
        return isPenaltyImposed;
    }

    /**
     * Checks if this passenger is currently in the taxi.
     * @return True if this passenger is currently in the taxi, false otherwise.
     */
    public boolean isInTaxi() {
        return isInTaxi;
    }

    /**
     * Gets the radius responsible for detecting if passenger can enter a taxi.
     * @return Taxi get in radius.
     */
    public double getDetectRadius() {
        return RADIUS;
    }

    /**
     * Initialises the taxi driver.
     * @param driver The taxi driver.
     */
    public void initialiseDriver(Driver driver) {
        this.driver = driver;
    }
}
