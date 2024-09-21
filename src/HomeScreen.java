import bagel.*;
import java.util.Properties;

/**
 * Class to render the home screen.
 * Also handles the ongoing game scrolling info background.
 */
public class HomeScreen {

    // Constants related to properties that other variables refer to.
    private final Properties GAME_PROPS;
    private final Properties MESSAGE_PROPS;

    // Constants related to rendering.
    private final Image HOME_BACKGROUND_IMAGE;
    private final String FONT_PATH;
    private final int TITLE_FONT_SIZE;
    private final int INSTRUCTION_FONT_SIZE;

    // Constants related to the texts from message_en.properties.
    private final String TITLE_TEXT;
    private final String INSTRUCTION_TEXT;

    // Constants related to Y coordinates for rendered texts.
    private final double TITLE_Y;
    private final double INSTRUCTION_Y;

    public HomeScreen(Properties gameProps, Properties messageProps) {
        this.GAME_PROPS = gameProps;
        this.MESSAGE_PROPS = messageProps;

        HOME_BACKGROUND_IMAGE = new Image(GAME_PROPS.getProperty("backgroundImage.home"));
        FONT_PATH = GAME_PROPS.getProperty("font");

        TITLE_FONT_SIZE = Integer.parseInt(GAME_PROPS.getProperty("home.title.fontSize"));
        INSTRUCTION_FONT_SIZE = Integer.parseInt(GAME_PROPS.getProperty("home.instruction.fontSize"));

        TITLE_TEXT = MESSAGE_PROPS.getProperty("home.title");
        INSTRUCTION_TEXT = MESSAGE_PROPS.getProperty("home.instruction");

        TITLE_Y = Double.parseDouble(GAME_PROPS.getProperty("home.title.y"));
        INSTRUCTION_Y = Double.parseDouble(GAME_PROPS.getProperty("home.instruction.y"));
    }

    /**
     * Renders the home screen.
     */
    public void render() {
        // Draw home background image
        HOME_BACKGROUND_IMAGE.draw(Window.getWidth() / 2.0, Window.getHeight() / 2.0);

        // Draw home title message
        Font titleFont = new Font(FONT_PATH, TITLE_FONT_SIZE);
        double titleX = (Window.getWidth() - titleFont.getWidth(TITLE_TEXT)) / 2.0;
        titleFont.drawString(TITLE_TEXT, titleX, TITLE_Y);

        // Draw home instruction message
        Font instructionFont = new Font(FONT_PATH, INSTRUCTION_FONT_SIZE);
        double instructionX = (Window.getWidth() - instructionFont.getWidth(INSTRUCTION_TEXT)) / 2.0;
        instructionFont.drawString(INSTRUCTION_TEXT, instructionX, INSTRUCTION_Y);
    }
}
