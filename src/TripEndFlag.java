import java.util.Properties;

/**
 * Class representing the trip end flag entity in the game.
 * The trip end flag marks the final destination of a trip and can be moved down and drawn on the screen.
 */
public class TripEndFlag extends Entity {

    /**
     * Boolean flag indicating whether the trip end flag is active.
     */
    private boolean isActive;

    /**
     * Constructor for the trip end flag entity.
     * @param startX The initial X-coordinate for the trip end flag.
     * @param startY The initial Y-coordinate for the passenger associated with the trip end flag.
     * @param distanceY The vertical distance between the passenger and the actual position of the trip end flag.
     * @param gameProps The properties object containing game configuration values.
     */
    public TripEndFlag(int startX, int startY, int distanceY, Properties gameProps) {
        super(startX, startY - distanceY, gameProps,
                "gameObjects.tripEndFlag.image", "gameObjects.tripEndFlag.radius");
        this.isActive = false;
    }

    /**
     * Moves the trip end flag down.
     */
    @Override
    public void moveDown() {
        if (isActive) {
            setY(getY() + SCROLL_SPEED);
        }
    }

    /**
     * Draws the trip end flag on the screen if the flag is currently active.
     */
    @Override
    public void draw() {
        if (isActive) {
            IMAGE.draw(getX(), getY());
        }
    }

    /**
     * Activates the trip end flag.
     */
    public void activate() {
        this.isActive = true;
    }

    /**
     * Deactivates the trip end flag.
     */
    public void deactivate() {
        this.isActive = false;
    }
}
