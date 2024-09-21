import bagel.*;
import bagel.Image;
import java.util.Properties;

/**
 * Class for the trip end flag entity.
 */
public class TripEndFlag {

    // Constants
    private final Image IMAGE;
    private final double RADIUS;
    private final int SCROLL_SPEED;

    // Variables
    private int x;
    private int y;
    private boolean isActive;

    public TripEndFlag(int startX, int startY, int distanceY, Properties properties) {
        this.x = startX;
        this.y = startY - distanceY; // Put end flag at the passenger destination
        this.isActive = false;

        IMAGE = new Image(properties.getProperty("gameObjects.tripEndFlag.image"));
        RADIUS = Double.parseDouble(properties.getProperty("gameObjects.tripEndFlag.radius"));

        // Scroll speed for trip end flag can be referred to taxi's "scroll speed".
        SCROLL_SPEED = Integer.parseInt(properties.getProperty("gameObjects.taxi.speedY"));
    }

    /**
     * Constantly updates the trip end flag.
     */
    public void update(Input input) {
        if (input.isDown(Keys.UP)) {
            moveDown();
        }
        draw();
    }

    /**
     * Move the trip end flag down.
     */
    private void moveDown() {
        if (isActive) {
            this.y += SCROLL_SPEED;
        }
    }

    /**
     * Draws the trip end flag when trip is ongoing.
     */
    private void draw() {
        if (isActive) {
            IMAGE.draw(x, y);
        }
    }

    public void activate() {
        this.isActive = true;
    }

    public void deactivate() {
        this.isActive = false;
    }

    public double getRadius() {
        return RADIUS;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

}
