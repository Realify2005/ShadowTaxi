import bagel.*;
import java.util.Properties;

/**
 * Class for the driver entity.
 */
public class Driver {
    private final Image IMAGE;

    private final int FONT_SIZE;
    private final String FONT_PATH;

    private final int WALK_SPEED_X;
    private final int WALK_SPEED_Y;
    private final int RADIUS;
    private final int TAXI_GET_IN_RADIUS;
    private final double HEALTH;

    private final String DRIVER_TEXT;
    private final int DRIVER_TEXT_X;
    private final int DRIVER_TEXT_Y;

    // Variables
    private int x;
    private int y;
    private boolean inTaxi;

    public Driver(int x, int y, Properties gameProps, Properties messageProps) {
        this.x = x;
        this.y = y;
        this.inTaxi = false;

        IMAGE = new Image(gameProps.getProperty("gameObjects.driver.image"));
        WALK_SPEED_X = Integer.parseInt(gameProps.getProperty("gameObjects.driver.walkSpeedX"));
        WALK_SPEED_Y = Integer.parseInt(gameProps.getProperty("gameObjects.driver.walkSpeedY"));
        RADIUS = Integer.parseInt(gameProps.getProperty("gameObjects.driver.radius"));
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
        this.y -= WALK_SPEED_Y;
    }

    private void moveDown() {
        this.y += WALK_SPEED_Y;
    }

    private void moveLeft() {
        this.x -= WALK_SPEED_X;
    }

    private void moveRight() {
        this.x += WALK_SPEED_X;
    }

    /**
     * Helper function to keep driver's coordinates up to date with taxi's coordinate
     */
    private void updateWithTaxiMovement(int taxiX, int taxiY) {
        if (inTaxi) {
            this.x = taxiX;
            this.y = taxiY;
        }
    }

    private boolean isAdjacentToTaxi(Taxi taxi) {
        return getDistanceTo(taxi.getX(), taxi.getY()) <= TAXI_GET_IN_RADIUS;
    }

    private double getDistanceTo(double targetX, double targetY) {
        return Math.sqrt(Math.pow((targetX - x), 2) + Math.pow((targetY - y), 2));
    }

    private void draw() {
        if (!inTaxi) {
            IMAGE.draw(x, y);
        }
        renderHealth();
    }

    private void renderHealth() {
        Font font = new Font(FONT_PATH, FONT_SIZE);
        font.drawString( DRIVER_TEXT + HEALTH, DRIVER_TEXT_X, DRIVER_TEXT_Y);
    }

    public void ejectFromTaxi() {
        if (inTaxi) {
            this.x -= 50;
            this.inTaxi = false;
        }
    }

    public boolean isInTaxi() {
        return inTaxi;
    }

    public void enteredTaxi() { this.inTaxi = true; }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public double getHealth() {
        return HEALTH;
    }

    public int getTaxiGetInRadius() { return TAXI_GET_IN_RADIUS; }
}
