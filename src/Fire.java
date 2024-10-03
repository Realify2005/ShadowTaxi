import java.util.Properties;

/**
 * Fire is a temporary effect that is created when car or taxi entity has health less than or equal to 0 (destroyed).
 * It lives temporarily in the game (i.e. disappears after 20 frames of being drawn).
 */
public class Fire extends TemporaryEffect {
    /**
     * Constructor for Fire temporary effect.
     * Initialises its (x, y) position, time (frames) to live, and image.
     * @param x The x-coordinate for the fire to be rendered.
     * @param y The y-coordinate for the fire to be rendered.
     * @param gameProps The game properties object containing various game configuration values.
     */
    public Fire(int x, int y, Properties gameProps) {
        super(x, y, gameProps, "gameObjects.fire.ttl", "gameObjects.fire.image");
    }
}
