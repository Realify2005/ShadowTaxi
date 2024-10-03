import bagel.*;
import java.util.Properties;

/**
 * Abstract class representing a screen in the game.
 * Used to implement all kinds of screens in the game.
 * Implements the drawable interface since it needs to be rendered.
 */
public abstract class Screen implements Drawable {

    /**
     * The properties object containing game configuration values.
     */
    protected final Properties GAME_PROPS;

    /**
     * The properties object containing rendered text configuration values.
     */
    protected final Properties MESSAGE_PROPS;

    /**
     * The background image to be drawn on the screen.
     */
    protected Image BACKGROUND_IMAGE;

    /**
     * The file path to the font used for rendering text.
     */
    protected final String FONT_PATH;

    /**
     * Constructor for abstract Screen class.
     * @param gameProps The properties object containing game configuration values.
     * @param messageProps The properties object containing rendered text configuration values.
     * @param backgroundImage The background image to be drawn on the screen.
     */
    public Screen(Properties gameProps, Properties messageProps, Image backgroundImage) {
        this.GAME_PROPS = gameProps;
        this.MESSAGE_PROPS = messageProps;
        this.BACKGROUND_IMAGE = backgroundImage;

        this.FONT_PATH = GAME_PROPS.getProperty("font");
    }

    /**
     * Draws the background image at the center of the screen.
     */
    @Override
    public void draw() {
        BACKGROUND_IMAGE.draw(Window.getWidth() / 2.0, Window.getHeight() / 2.0);
    }
}
