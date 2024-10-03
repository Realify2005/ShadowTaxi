import bagel.*;
import java.util.Properties;

/**
 * Class to render the home screen.
 * Also handles the ongoing game scrolling info background.
 */
public class HomeScreen extends Screen {

    /**
     * The font size used for rendering the title text.
     */
    private final int TITLE_FONT_SIZE;

    /**
     * The font size used for rendering the instruction text.
     */
    private final int INSTRUCTION_FONT_SIZE;

    /**
     * The title text.
     */
    private final String TITLE_TEXT;

    /**
     * The instruction text.
     */
    private final String INSTRUCTION_TEXT;

    /**
     * The Y-coordinate where the title text is drawn.
     */
    private final double TITLE_Y;

    /**
     * The Y-coordinate where the instruction text is drawn.
     */
    private final double INSTRUCTION_Y;

    /**
     * Constructor for Home Screen.
     * Initialises the background image, title and instruction texts, as well as its Y coordinates and font sizes.
     * @param gameProps The properties object containing game configuration values.
     * @param messageProps The properties object containing text configuration values.
     */
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
