import bagel.*;
import java.util.Properties;

public abstract class Screen implements Drawable {

    protected Properties GAME_PROPS;
    protected Properties MESSAGE_PROPS;
    protected Image BACKGROUND_IMAGE;
    protected final String FONT_PATH;

    public Screen(Properties gameProps, Properties messageProps, Image backgroundImage) {
        this.GAME_PROPS = gameProps;
        this.MESSAGE_PROPS = messageProps;
        this.BACKGROUND_IMAGE = backgroundImage;

        this.FONT_PATH = GAME_PROPS.getProperty("font");
    }

    @Override
    public void draw() {
        BACKGROUND_IMAGE.draw(Window.getWidth() / 2.0, Window.getHeight() / 2.0);
    }
}
