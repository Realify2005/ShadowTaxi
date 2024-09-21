import bagel.*;
import bagel.util.Colour;
import java.util.Properties;

/**
 * Class to render the player info screen.
 */
public class PlayerInfoScreen {

    // Constants related to properties that other variables refer to.
    private final Properties GAME_PROPS;
    private final Properties MESSAGE_PROPS;

    // Constants related to rendering.
    private final Image PLAYER_INFO_BACKGROUND_IMAGE;
    private final String FONT_PATH;
    private final int FONT_SIZE;

    // Constants related to the texts from message_en.properties.
    private final String PLAYER_NAME_TEXT;
    private final String START_TEXT;

    // Constants related to Y coordinates for rendered texts.
    private final double PLAYER_NAME_Y;
    private final double PLAYER_NAME_INPUT_Y;
    private final double START_Y;

    // Constant that defines the distance between lines for additional instructions.
    private final double DISTANCE_BETWEEN_LINES;

    // Variables
    private String playerName = "";

    public PlayerInfoScreen(Properties gameProps, Properties messageProps) {
        this.GAME_PROPS = gameProps;
        this.MESSAGE_PROPS = messageProps;

        // Initialize constants
        PLAYER_INFO_BACKGROUND_IMAGE = new Image(GAME_PROPS.getProperty("backgroundImage.playerInfo"));
        FONT_PATH = GAME_PROPS.getProperty("font");
        FONT_SIZE = Integer.parseInt(GAME_PROPS.getProperty("playerInfo.fontSize"));

        PLAYER_NAME_TEXT = MESSAGE_PROPS.getProperty("playerInfo.playerName");
        START_TEXT = MESSAGE_PROPS.getProperty("playerInfo.start");

        PLAYER_NAME_Y = Double.parseDouble(GAME_PROPS.getProperty("playerInfo.playerName.y"));
        PLAYER_NAME_INPUT_Y = Double.parseDouble(GAME_PROPS.getProperty("playerInfo.playerNameInput.y"));
        START_Y = Double.parseDouble(GAME_PROPS.getProperty("playerInfo.start.y"));

        DISTANCE_BETWEEN_LINES = 30; // Set distance between lines for additional instructions
    }

    /**
     * Renders the player info screen.
     */
    public void render() {
        // Draw player info background image
        PLAYER_INFO_BACKGROUND_IMAGE.draw(Window.getWidth() / 2.0, Window.getHeight() / 2.0);

        // Draw player info message
        Font font = new Font(FONT_PATH, FONT_SIZE);
        DrawOptions drawOptions = new DrawOptions();

        // Draw instruction message "ENTER YOUR NAME" at the top
        font.drawString(PLAYER_NAME_TEXT,
                Window.getWidth() / 2.0 - font.getWidth(PLAYER_NAME_TEXT) / 2.0,
                PLAYER_NAME_Y, drawOptions);

        // Draw entered name
        drawOptions.setBlendColour(Colour.BLACK);
        font.drawString(playerName,
                Window.getWidth() / 2.0 - font.getWidth(playerName) / 2.0,
                PLAYER_NAME_INPUT_Y, drawOptions);

        // Draw additional instructions (split into lines)
        String[] lines = START_TEXT.split("\n");

        // Draw first line of instructions
        double line1X = Window.getWidth() / 2.0 - font.getWidth(lines[0]) / 2.0;
        double line1Y = START_Y;
        font.drawString(lines[0], line1X, line1Y);

        // Draw second line of instructions
        double line2X = Window.getWidth() / 2.0 - font.getWidth(lines[1]) / 2.0;
        double line2Y = line1Y + DISTANCE_BETWEEN_LINES;
        font.drawString(lines[1], line2X, line2Y);
    }

    /**
     * Helper function, used in playerInfo screen to let the player type in their name.
     * @param input The current mouse/keyboard input.
     */
    public void enterPlayerName(Input input) {
        String key = MiscUtils.getKeyPress(input);

        // Handle keyboard press input
        if (key != null) {
            playerName += key;
        }

        // If Backspace is pressed, delete the last character
        if (input.wasPressed(Keys.BACKSPACE) && !playerName.isEmpty()) {
            playerName = playerName.substring(0, playerName.length() - 1); // Remove last character
        }
    }

    /**
     * Resets player name (i.e. when game has ended and player wants to play again).
     */
    public void resetPlayerName() {
        playerName = "";
    }

    public String getPlayerName() {
        return playerName;
    }
}
