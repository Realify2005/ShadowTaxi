import java.util.Properties;
import bagel.*;
import bagel.Font;
import bagel.Image;

/**
 * Class for the passenger entity.
 */
public class Passenger {
    private final Image IMAGE;

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
    private final int SCROLL_SPEED;

    // Radius for collision detection between taxi and passenger.
    private final int RADIUS;

    // Variables
    private int x;
    private int y;
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
    private double earnings;
    private boolean isEarningsAdded;
    private boolean isPenaltyImposed;

    private Taxi taxi;
    private PowerUpState powerUpState;

    public Passenger(int x, int y, int priority, int endX, int distanceY, int hasUmbrella,
                     Taxi taxi, PowerUpState powerUpState, Properties properties) {
        this.x = x;
        this.y = y;
        this.originalPriority = this.priority = priority;
        this.endX = endX;
        this.distanceY = distanceY;
        this.finalFlagX = -1; // has not been set
        this.finalFlagY = -1; // has not been set
        this.hasUmbrella = (hasUmbrella != 0); // Convert int from world file to boolean
        this.penalty = 0;
        this.taxi = taxi;
        this.powerUpState = powerUpState;
        this.isPickedUp = false;
        this.isDroppedOff = false;
        this.isMovingToFlag = false;
        this.isPriorityDecreased = false;
        this.isEarningsAdded = false;
        this.isPenaltyImposed = false;

        IMAGE = new Image(properties.getProperty("gameObjects.passenger.image"));
        WALK_SPEED_X = Integer.parseInt(properties.getProperty("gameObjects.passenger.walkSpeedX"));
        WALK_SPEED_Y = Integer.parseInt(properties.getProperty("gameObjects.passenger.walkSpeedY"));

        FONT_SIZE = Integer.parseInt(properties.getProperty("gameObjects.passenger.fontSize"));
        FONT_PATH = properties.getProperty("font");

        TRIP_RATE = Double.parseDouble(properties.getProperty("trip.rate.perY"));
        PRIORITY_RATE_1 = Integer.parseInt(properties.getProperty("trip.rate.priority1"));
        PRIORITY_RATE_2 = Integer.parseInt(properties.getProperty("trip.rate.priority2"));
        PRIORITY_RATE_3 = Integer.parseInt(properties.getProperty("trip.rate.priority3"));

        RADIUS = Integer.parseInt(properties.getProperty("gameObjects.passenger.taxiDetectRadius"));

        // Scroll speed for passenger can be referred to taxi's "scroll speed".
        SCROLL_SPEED = Integer.parseInt(properties.getProperty("gameObjects.taxi.speedY"));

        this.earnings = calculateEarnings();
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
        updateWithTaxiMovement(taxi.getX(), taxi.getY());

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
            font.drawString(String.format("%.1f", preEarnings), x - 100, y); // Draw estimated earnings text
            font.drawString(Integer.toString(priority), x - 30, y); // Draw priority text
        }
    }

    /**
     * Slowly moves the passenger towards the taxi.
     */
    private void moveTowardsTaxi(double taxiX, double taxiY) {
        if (!isPickedUp) {
            if (x < taxiX) {
                this.x += WALK_SPEED_X;
            } else if (x > taxiX) {
                this.x -= WALK_SPEED_X;
            }

            if (y < taxiY) {
                this.y += WALK_SPEED_Y;
            } else if (y > taxiY) {
                this.y -= WALK_SPEED_Y;
            }

            // Check if the passenger can now be picked up by taxi (i.e. coordinates are equal to taxi)
            if (calculateDistance(taxiX, taxiY) == 0) {
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

            if (x < finalFlagX) {
                this.x += WALK_SPEED_X;
            } else if (x > finalFlagX) {
                this.x -= WALK_SPEED_X;
            }

            if (y < finalFlagY) {
                this.y += WALK_SPEED_Y;
            } else if (y > finalFlagY) {
                this.y -= WALK_SPEED_Y;
            }

            if (this.x == finalFlagX && this.y == finalFlagY) {
                this.isDroppedOff = true; // Passenger is dropped off
            }
        }
    }

    /**
     * Helper function to calculate distance between passenger and taxi.
     */
    private double calculateDistance(double taxiX, double taxiY) {
        return Math.sqrt(Math.pow(taxiX - x, 2) + Math.pow(taxiY - y, 2));
    }

    /**
     * Moves the passenger down.
     */
    private void moveDown() {
        this.y += SCROLL_SPEED;
    }

    /**
     * Helper function to keep passenger's coordinates up to date with taxi's coordinate
     */
        private void updateWithTaxiMovement(int taxiX, int taxiY) {
        if (isPickedUp && !isMovingToFlag) {
            this.x = taxiX;
            this.y = taxiY;
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
        if (isDroppedOff && (y == flag.getY()) && taxi.isEmpty()) {
            deactivate(flag);
        }
    }

    /**
     * Draws the passenger entity.
     */
    private void draw() {
        if (!isPickedUp || isDroppedOff || isMovingToFlag) {
            IMAGE.draw(x, y);
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

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
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

    public int getRadius() {
        return RADIUS;
    }

    public void setPenalty(double penalty) {
        this.penalty = penalty;
        isPenaltyImposed = true;
    }
}
