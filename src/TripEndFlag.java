import bagel.*;
import bagel.Image;
import java.util.Properties;

/**
 * Class for the trip end flag entity.
 */
public class TripEndFlag extends Entity {

    // Variables
    private boolean isActive;

    public TripEndFlag(int startX, int startY, int distanceY, Properties properties) {
        super(startX, startY - distanceY, properties,
                "gameObjects.tripEndFlag.image", "gameObjects.tripEndFlag.radius");
        this.isActive = false;
    }

    /**
     * Move the trip end flag down.
     */
    @Override
    public void moveDown() {
        if (isActive) {
            setY(getY() + SCROLL_SPEED);
        }
    }

    /**
     * Draws the trip end flag when trip is ongoing.
     */
    @Override
    public void draw() {
        if (isActive) {
            IMAGE.draw(getX(), getY());
        }
    }

    public void activate() {
        this.isActive = true;
    }

    public void deactivate() {
        this.isActive = false;
    }

}
