import bagel.*;
import java.util.Properties;

/**
 * Class for the driver entity.
 */
public class Driver extends Entity implements Damageable, Ejectable {
    private final int FONT_SIZE;
    private final String FONT_PATH;

    private final int WALK_SPEED_X;
    private final int WALK_SPEED_Y;
    private final int TAXI_GET_IN_RADIUS;
    private final double HEALTH;

    private final String DRIVER_TEXT;
    private final int DRIVER_TEXT_X;
    private final int DRIVER_TEXT_Y;

    private final int SEPARATE_X = 2;
    private final int SEPARATE_Y = 2;
    private final int EJECT_X = 50;
    private final int DAMAGE = 0;

    // Variables
    private boolean inTaxi;
    private double currentHealth;
    private Car collidingCar;

    private int initialCollisionTimeoutFramesRemaining;
    private int collisionTimeoutFramesRemaining;

    private final PowerUpState POWER_UP_STATE;

    public Driver(int x, int y, PowerUpState powerUpState, Properties gameProps, Properties messageProps) {
        super(x, y, gameProps, "gameObjects.driver.image", "gameObjects.driver.radius");
        this.inTaxi = false;

        int PROPS_TO_GAME_MULTIPLIER = 100;
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

        if (other.getCurrentHealth() > 0 && !inTaxi) {
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

    private void moveUp() {
        setY(getY() - WALK_SPEED_Y);
    }

    @Override
    public void moveDown() {
        setY(getY() + WALK_SPEED_Y);
    }

    private void moveLeft() {
        setX(getX() - WALK_SPEED_X);
    }

    private void moveRight() {
        setX(getX() + WALK_SPEED_X);
    }

    /**
     * Helper function to keep driver's coordinates up to date with taxi's coordinate
     */
    private void updateWithTaxiMovement(int taxiX, int taxiY) {
        if (inTaxi) {
            setX(taxiX);
            setY(taxiY);
        }
    }

    private boolean isAdjacentToTaxi(Taxi taxi) {
        return getDistanceTo(taxi.getX(), taxi.getY()) <= TAXI_GET_IN_RADIUS;
    }

    @Override
    public double getDistanceTo(double targetX, double targetY) {
        return Math.sqrt(Math.pow((targetX - getX()), 2) + Math.pow((targetY - getY()), 2));
    }

    /**
     * Checks if taxi has collided with any power-ups.
     */
    public boolean collidedWith(PowerUp powerUp) {
        double collisionRange = RADIUS + powerUp.getRadius();
        return getDistanceTo(powerUp.getX(), powerUp.getY()) <= collisionRange;
    }

    @Override
    public void draw() {
        if (!inTaxi) {
            IMAGE.draw(getX(), getY());
        }
    }

    private void renderHealth() {
        Font font = new Font(FONT_PATH, FONT_SIZE);
        font.drawString( DRIVER_TEXT + currentHealth, DRIVER_TEXT_X, DRIVER_TEXT_Y);
    }

    public boolean isInTaxi() {
        return inTaxi;
    }

    public void enteredTaxi() { this.inTaxi = true; }

    public double getHealth() {
        return HEALTH;
    }

    public int getTaxiGetInRadius() { return TAXI_GET_IN_RADIUS; }

    @Override
    public void eject() {
        inTaxi = false;
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
        return RADIUS;
    }
}
