import bagel.*;
import java.util.Properties;

/**
 * Class to render the home screen.
 * Also handles the ongoing game scrolling info background.
 */
public class HomeScreen extends Screen {

    // Constants related to rendering.
    private final int TITLE_FONT_SIZE;
    private final int INSTRUCTION_FONT_SIZE;

    // Constants related to the texts from message_en.properties.
    private final String TITLE_TEXT;
    private final String INSTRUCTION_TEXT;

    // Constants related to Y coordinates for rendered texts.
    private final double TITLE_Y;
    private final double INSTRUCTION_Y;

    public HomeScreen(Properties gameProps, Properties messageProps) {
        super(gameProps, messageProps, new Image(gameProps.getProperty("backgroundImage.home")));

        TITLE_FONT_SIZE = Integer.parseInt(GAME_PROPS.getProperty("home.title.fontSize"));
        INSTRUCTION_FONT_SIZE = Integer.parseInt(GAME_PROPS.getProperty("home.instruction.fontSize"));

        TITLE_TEXT = MESSAGE_PROPS.getProperty("home.title");
        INSTRUCTION_TEXT = MESSAGE_PROPS.getProperty("home.instruction");

        TITLE_Y = Double.parseDouble(GAME_PROPS.getProperty("home.title.y"));
        INSTRUCTION_Y = Double.parseDouble(GAME_PROPS.getProperty("home.instruction.y"));
    }

    /**
     * Draws the home screen.
     */
    @Override
    public void draw() {
        // Draw home background image
        super.draw();

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
