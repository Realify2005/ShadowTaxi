import bagel.*;
import bagel.Image;
import java.util.Properties;

/**
 * Class for the coin entity.
 */

public class Coin {

    // Constants
    private final Image IMAGE;
    private final double RADIUS;
    private final int SCROLL_SPEED;

    // Variables
    private int x;
    private int y;
    private boolean isTaken;

    public Coin(int x, int y, Properties properties) {
        this.x = x;
        this.y = y;
        this.isTaken = false;

        IMAGE = new Image(properties.getProperty("gameObjects.coin.image"));
        RADIUS = Double.parseDouble(properties.getProperty("gameObjects.coin.radius"));

        // Scroll speed for coin can be referred to taxi's "scroll speed".
        SCROLL_SPEED = Integer.parseInt(properties.getProperty("gameObjects.taxi.speedY"));
    }

    /***
     * Updates the coin class based on keyboard input.
     */
    public void update(Input input) {
        if (input.isDown(Keys.UP)) {
            moveDown();
        }
        draw();
    }

    /***
     * Moves the coin down.
     */
    private void moveDown() {
        this.y += SCROLL_SPEED;
    }

    /***
     * Draws the coin entity on game screen.
     */
    private void draw() {
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
