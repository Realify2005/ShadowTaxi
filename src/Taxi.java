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

        private final Gameplay GAMEPLAY;

        public Taxi(int x, int y, Gameplay gameplay, Properties gameProps) {
            super(x, y, gameProps, "gameObjects.taxi.image", "gameObjects.taxi.radius");
            this.isTaxiMoved = false;
            this.hasDriver = false;
            this.currentPassenger = null;
            this.GAMEPLAY = gameplay;

            SPEED_X = Integer.parseInt(gameProps.getProperty("gameObjects.taxi.speedX"));
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
