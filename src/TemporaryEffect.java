import bagel.*;
import java.util.Properties;

/**
 * Abstract class representing a temporary effect in the game that can be temporarily drawn on screen for a certain
 * number of frames and moved vertically with a fixed speed according to player's input.
 * Used by blood, fire, and smoke classes.
 */
public abstract class TemporaryEffect implements Drawable, Scrollable {

    /**
     * The image of the temporary effect.
     */
    protected final Image IMAGE;

    /**
     * The vertical speed of the temporary effect.
     */
    protected final int SCROLL_SPEED;

    /**
     * The number of frames remaining before the temporary effect is no longer visible.
     * Set initial as their time to live (ttl).
     */
    private int framesRemaining;

    /**
     * The X-coordinate position of the temporary effect.
     */
    private int x;

    /**
     * The Y-coordinate position of the temporary effect.
     */
    private int y;

    /**
     * Constructor for temporary effect abstract class.
     * @param x The initial X-coordinate of the temporary effect.
     * @param y The initial Y-coordinate of the temporary effect.
     * @param gameProps The game properties object containing various game configuration values.
     * @param TTLProperty The property key to the time-to-live (ttl) of the temporary effect.
     * @param imageProperty The property key to the image file of the temporary effect.
     */
    public TemporaryEffect(int x, int y, Properties gameProps, String TTLProperty, String imageProperty) {
        this.x = x;
        this.y = y;
        this.framesRemaining = Integer.parseInt(gameProps.getProperty(TTLProperty));

        IMAGE = new Image(gameProps.getProperty(imageProperty));
        SCROLL_SPEED = Integer.parseInt(gameProps.getProperty("gameObjects.taxi.speedY"));
    }

    /**
     * Render the temporary effect and reduce its current remaining frames.
     */
    public void update() {
        draw();
        updateFramesRemaining();
    }

    /**
     * Moves the temporary effect down.
     */
    @Override
    public void moveDown() {
        y += SCROLL_SPEED;
    }

    /**
     * Reduces the number of frames remaining for the temporary effect.
     */
    private void updateFramesRemaining() {
        if (framesRemaining > 0) {
            framesRemaining--;
        }
    }

    /**
     * Draws the temporary effect on the screen if it has frames remaining.
     * The temporary effect will no longer be rendered once there are no more frames remaining.
     */
    @Override
    public void draw() {
        if (framesRemaining > 0) {
            IMAGE.draw(x, y);
        }
    }
}
