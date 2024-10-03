import bagel.*;
import bagel.util.Colour;
import java.util.Properties;

/**
 * Class to render the player info screen and its contents.
 * Responsible for letting the player enter their name and keeping track of it.
 */
public class PlayerInfoScreen extends Screen {

    /**
     * The font size used for rendering texts on the player info screen.
     */
    private final int FONT_SIZE;

    /**
     * The text label for the player name input field.
     */
    private final String PLAYER_NAME_TEXT;

    /**
     * The text label for the start prompt.
     */
    private final String START_TEXT;

    /**
     * The Y-coordinate for the player name text label.
     */
    private final double PLAYER_NAME_Y;

    /**
     * The Y-coordinate for the input player name box.
     */
    private final double PLAYER_NAME_INPUT_Y;

    /**
     * The Y-coordinate for the start prompt text label.
     */
    private final double START_Y;

    /**
     * The vertical distance between the lines of instructions text.
     */
    private final double DISTANCE_BETWEEN_LINES;

    /**
     * The player's name as entered by the player.
     */
    private String playerName = "";


    /**
     * Constructor for player info screen class.
     * @param gameProps The properties object containing game configuration values.
     * @param messageProps The properties object containing text configuration values.
     */
    public PlayerInfoScreen(Properties gameProps, Properties messageProps) {
        super(gameProps, messageProps, new Image(gameProps.getProperty("backgroundImage.playerInfo")));

        FONT_SIZE = Integer.parseInt(GAME_PROPS.getProperty("playerInfo.fontSize"));

        PLAYER_NAME_TEXT = MESSAGE_PROPS.getProperty("playerInfo.playerName");
        START_TEXT = MESSAGE_PROPS.getProperty("playerInfo.start");

        PLAYER_NAME_Y = Double.parseDouble(GAME_PROPS.getProperty("playerInfo.playerName.y"));
        PLAYER_NAME_INPUT_Y = Double.parseDouble(GAME_PROPS.getProperty("playerInfo.playerNameInput.y"));
        START_Y = Double.parseDouble(GAME_PROPS.getProperty("playerInfo.start.y"));

        DISTANCE_BETWEEN_LINES = 30; // Set distance between lines for instructions text
    }

    /**
     * Draws the player info screen and its contents.
     */
    @Override
    public void draw() {
        // Draw player info background image
        super.draw();

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

    /**
     * Gets the current player's name.
     * @return Current player's name.
     */
    public String getPlayerName() {
        return playerName;
    }
}
