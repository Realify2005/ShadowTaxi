import java.util.Properties;

/**
 * Blood is a temporary effect that is created when passenger or driver entity takes damage.
 * It lives temporarily in the game (i.e. disappears after 20 frames of being drawn).
 */
public class Blood extends TemporaryEffect {
    /**
     * Constructor for Blood temporary effect.
     * Initialises its (x, y) position, time (frames) to live, and image.
     * @param x The x-coordinate for the blood to be rendered.
     * @param y The y-coordinate for the blood to be rendered.
     * @param gameProps The game properties object containing various game configuration values.
     */
    public Blood(int x, int y, Properties gameProps) {
        super(x, y, gameProps, "gameObjects.blood.ttl", "gameObjects.blood.image");
    }
}
