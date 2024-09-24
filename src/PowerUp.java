import bagel.*;
import bagel.Image;
import java.util.Properties;

/**
 * Abstract class for power-up entities.
 */


public abstract class PowerUp implements Drawable {

    // Constants
    private final Image IMAGE;
    private final double RADIUS;
    private final int SCROLL_SPEED;

    // Variables
    private int x;
    private int y;
    private boolean isTaken;

    public PowerUp(int x, int y, Properties properties, String imageProperty, String radiusProperty) {
        this.x = x;
        this.y = y;
        this.isTaken = false;

        IMAGE = new Image(properties.getProperty(imageProperty));
        RADIUS = Double.parseDouble(properties.getProperty(radiusProperty));

        // Scroll speed for all power-ups can be referred to taxi's "scroll speed".
        SCROLL_SPEED = Integer.parseInt(properties.getProperty("gameObjects.taxi.speedY"));
    }

    /***
     * Updates the power-up entity based on keyboard input.
     */
    public void update(Input input) {
        if (input.isDown(Keys.UP)) {
            moveDown();
        }
        draw();
    }

    public abstract void activate(PowerUpState powerUpState);

    /***
     * Moves the power-up entity down.
     */
    protected void moveDown() {
        this.y += SCROLL_SPEED;
    }

    /***
     * Draws the power-up entity on game screen.
     */
    @Override
    public void draw() {
        if (!isTaken) {
            IMAGE.draw(x, y);
        }
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public double getRadius() {
        return RADIUS;
    }

    public void wasTaken() {
        this.isTaken = true;
    }

    public boolean isTaken() {
        return isTaken;
    }
}
