import java.util.Properties;
import bagel.*;
import bagel.Font;

/**
 * Class for the passenger entity.
 */
public class Passenger extends Entity implements Damageable, Ejectable {
    // Constants related to font for rendering.
    private final int FONT_SIZE;
    private final String FONT_PATH;

    // Constants related to the rates of earnings for base case and each passenger's priority number.
    private final double TRIP_RATE;
    private final int PRIORITY_RATE_1;
    private final int PRIORITY_RATE_2;
    private final int PRIORITY_RATE_3;

    // Constants related to the movement speed of passengers.
    private final int WALK_SPEED_X;
    private final int WALK_SPEED_Y;

    private final int COLLISION_RADIUS;
    private final double HEALTH;

    private final int EJECT_X = 100;
    private final int SEPARATE_X = 2;
    private final int SEPARATE_Y = 2;
    private final int DAMAGE = 0;

    // Variables
    private int originalPriority;
    private int priority;
    private int endX;
    private int distanceY;
    private boolean hasUmbrella;
    private int finalFlagX;
    private int finalFlagY;
    private double penalty;
    private boolean isPickedUp;
    private boolean isMovingToFlag;
    private boolean isDroppedOff;
    private boolean isPriorityDecreased;
    private boolean isInTaxi;
    private double earnings;
    private boolean isEarningsAdded;
    private boolean isPenaltyImposed;

    private double currentHealth;

    private Car collidingCar;
    private int initialCollisionTimeoutFramesRemaining;
    private int collisionTimeoutFramesRemaining;

    private Taxi taxi;
    private PowerUpState powerUpState;
    private Driver driver;

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

        int PROPS_TO_GAME_MULTIPLIER = 100;
        COLLISION_RADIUS = Integer.parseInt(gameProps.getProperty("gameObjects.passenger.radius"));
        HEALTH = Double.parseDouble(gameProps.getProperty("gameObjects.passenger.health")) * PROPS_TO_GAME_MULTIPLIER;

        this.currentHealth = HEALTH;

        this.earnings = calculateEarnings();
    }

    @Override
    public void receiveDamage(double damage) {
        this.currentHealth -= damage;
        if (this.currentHealth < 0) {
            this.currentHealth = 0;
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
     * Constantly updates the passenger entity
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
     * Calculate earnings for passenger
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
     */
    private int getPriorityRate() {
        return switch (priority) {
            case 1 -> PRIORITY_RATE_1;
            case 2 -> PRIORITY_RATE_2;
            case 3 -> PRIORITY_RATE_3;
            default -> 0; // Put default as 0, even though per assignment specifications this should be possible
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
     * Helper function to calculate distance between passenger and taxi.
     */
    @Override
    public double getDistanceTo(double otherX, double otherY) {
        return Math.sqrt(Math.pow(otherX - getX(), 2) + Math.pow(otherY - getY(), 2));
    }

    /**
     * Helper function to keep passenger's coordinates up to date with taxi's coordinate
     */
    private void updateWithDriverMovement(int driverX, int driverY) {
        if (isPickedUp && !isMovingToFlag) {
            setX(driverX);
            setY(driverY);
        }
    }

    /**
     * Picks up the passenger (i.e. towards the taxi).
     */
    public void pickUp(double taxiX, double taxiY) {
        if (!isPickedUp) {
            moveTowardsTaxi(taxiX, taxiY);
        }
    }

    /**
     * Drops off the passenger (i.e. from the taxi).
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
        if (isDroppedOff && (getY() == flag.getY()) && taxi.isEmpty()) {
            deactivate(flag);
        }
    }

    /**
     * Draws the passenger entity.
     */
    @Override
    public void draw() {
        if (!isPickedUp || isDroppedOff || isMovingToFlag || !isInTaxi) {
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
     */
    private void deactivate(TripEndFlag flag) {
        flag.deactivate();
    }

    public int getEndX() {
        return endX;
    }

    public int getDistanceY() {
        return distanceY;
    }

    public int getPriority() {
        return priority;
    }

    public double getEarnings() {
        return earnings;
    }

    public void setEarnings(double earnings) {
        this.earnings = earnings;
    }

    public boolean isPickedUp() {
        return isPickedUp;
    }

    public boolean isEarningsAdded() {
        return isEarningsAdded;
    }

    public void addedEarnings() {
        this.isEarningsAdded = true;
    }

    public boolean isMovingToFlag() {
        return isMovingToFlag;
    }

    public boolean isPenaltyImposed() {
        return isPenaltyImposed;
    }

    public boolean isInTaxi() {
        return isInTaxi;
    }

    public void setPenalty(double penalty) {
        this.penalty = penalty;
        this.earnings = calculateEarnings();
        isPenaltyImposed = true;
    }

    @Override
    public void eject() {
        isInTaxi = false;
        setX(getX() - EJECT_X);
    }

    @Override
    public double getDamage() {
        return DAMAGE;
    }

    @Override
    public double getCurrentHealth() {
        return currentHealth;
    }

    @Override
    public double getRadius() {
        return COLLISION_RADIUS;
    }

    public double getDetectRadius() {
        return RADIUS;
    }

    public void initialiseTaxi(Taxi taxi) {
        this.taxi = taxi;
    }

    public void initialiseDriver(Driver driver) {
        this.driver = driver;
    }
}
