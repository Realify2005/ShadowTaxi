import bagel.*;
import java.util.Properties;

/**
 * Class for the driver entity.
 */
public class Driver extends Entity {
    private final int FONT_SIZE;
    private final String FONT_PATH;

    private final int WALK_SPEED_X;
    private final int WALK_SPEED_Y;
    private final int TAXI_GET_IN_RADIUS;
    private final double HEALTH;

    private final String DRIVER_TEXT;
    private final int DRIVER_TEXT_X;
    private final int DRIVER_TEXT_Y;

    // Variables
    private boolean inTaxi;

    public Driver(int x, int y, Properties gameProps, Properties messageProps) {
        super(x, y, gameProps, "gameObjects.driver.image", "gameObjects.driver.radius");
        this.inTaxi = false;

        WALK_SPEED_X = Integer.parseInt(gameProps.getProperty("gameObjects.driver.walkSpeedX"));
        WALK_SPEED_Y = Integer.parseInt(gameProps.getProperty("gameObjects.driver.walkSpeedY"));
        TAXI_GET_IN_RADIUS = Integer.parseInt(gameProps.getProperty("gameObjects.driver.taxiGetInRadius"));
        HEALTH = Double.parseDouble(gameProps.getProperty("gameObjects.driver.health")) * 100;

        FONT_SIZE = Integer.parseInt(gameProps.getProperty("gamePlay.info.fontSize"));
        FONT_PATH = gameProps.getProperty("font");

        DRIVER_TEXT = messageProps.getProperty("gamePlay.driverHealth");
        DRIVER_TEXT_X = Integer.parseInt(gameProps.getProperty("gamePlay.driverHealth.x"));
        DRIVER_TEXT_Y = Integer.parseInt(gameProps.getProperty("gamePlay.driverHealth.y"));
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
        updateWithTaxiMovement(taxi.getX(), taxi.getY());
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

    private double getDistanceTo(double targetX, double targetY) {
        return Math.sqrt(Math.pow((targetX - getX()), 2) + Math.pow((targetY - getY()), 2));
    }

    @Override
    public void draw() {
        if (!inTaxi) {
            IMAGE.draw(getX(), getY());
        }
        renderHealth();
    }

    private void renderHealth() {
        Font font = new Font(FONT_PATH, FONT_SIZE);
        font.drawString( DRIVER_TEXT + HEALTH, DRIVER_TEXT_X, DRIVER_TEXT_Y);
    }

    public void ejectFromTaxi() {
        if (inTaxi) {
            setX(getX() - 50);
            this.inTaxi = false;
        }
    }

    public boolean isInTaxi() {
        return inTaxi;
    }

    public void enteredTaxi() { this.inTaxi = true; }

    public double getHealth() {
        return HEALTH;
    }

    public int getTaxiGetInRadius() { return TAXI_GET_IN_RADIUS; }
}
