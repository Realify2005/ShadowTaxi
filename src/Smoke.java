import java.util.Properties;

/**
 * Smoke is a temporary effect that is created when car or taxi entity takes damage.
 * It lives temporarily in the game (i.e. disappears after 20 frames of being drawn).
 */
public class Smoke extends TemporaryEffect {
    /**
     * Constructor for Smoke temporary effect.
     * Initialises its (x, y) position, time (frames) to live, and image.
     * @param x The x-coordinate for the smoke to be rendered.
     * @param y The y-coordinate for the smoke to be rendered.
     * @param gameProps The game properties object containing various game configuration values.
     */
    public Smoke(int x, int y, Properties gameProps) {
        super(x, y, gameProps, "gameObjects.smoke.ttl", "gameObjects.smoke.image");
    }
}
