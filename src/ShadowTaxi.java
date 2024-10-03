import bagel.*;
import java.util.Properties;

/**
 * Skeleton Code for SWEN20003 Project 2B, Semester 2, 2024
 * Please enter your name below
 * Clement Chau
 */

/**
 * Main class for the ShadowTaxi game.
 * This class is where all the logic starts from.
 */
public class ShadowTaxi extends AbstractGame {

    /**
     * The properties object containing game configuration values.
     */
    private final Properties GAME_PROPS;

    /**
     * The properties object containing rendered text configuration values.
     */
    private final Properties MESSAGE_PROPS;

    /**
     * The current game state which determines which screen is displayed to the player.
     */
    private GameState currentGameState = GameState.HOME_SCREEN;

    /**
     * The screen that renders the starting home screen of the game.
     */
    private final HomeScreen HOME_SCREEN;

    /**
     * The screen that renders the player information screen where players can input their name and instructions given.
     */
    private final PlayerInfoScreen PLAYER_INFO_SCREEN;

    /**
     * The screen that renders the ongoing game screen.
     */
    private final OngoingGameScreen ONGOING_GAME_SCREEN;

    /**
     * The screen that renders the game over screen which indicates win/loss and player's final score.
     */
    private GameEndScreen gameEndScreen;

    /**
     * Constructor for ShadowTaxi class, the main class of the game.
     * @param gameProps The properties object containing game configuration values.
     * @param messageProps The properties object containing text configuration values.
     */
    public ShadowTaxi(Properties gameProps, Properties messageProps) {
        super(Integer.parseInt(gameProps.getProperty("window.width")),
                Integer.parseInt(gameProps.getProperty("window.height")),
                messageProps.getProperty("home.title"));

        this.GAME_PROPS = gameProps;
        this.MESSAGE_PROPS = messageProps;

        HOME_SCREEN = new HomeScreen(GAME_PROPS, MESSAGE_PROPS);
        PLAYER_INFO_SCREEN = new PlayerInfoScreen(GAME_PROPS, MESSAGE_PROPS);
        ONGOING_GAME_SCREEN = new OngoingGameScreen(GAME_PROPS, MESSAGE_PROPS);
    }

    /**
     * Render the relevant screens based on the current game's state as well as player's mouse/keyboard input.
     * @param input The current mouse/keyboard input.
     */
    @Override
    protected void update(Input input) {
        if (input.wasPressed(Keys.ESCAPE)) {
            Window.close();
        }
        switch (currentGameState) {
            case HOME_SCREEN:
                HOME_SCREEN.draw();
                // If "ENTER" key is pressed, then switch to player_info screen.
                if (input.wasPressed(Keys.ENTER)) {
                    currentGameState = GameState.PLAYER_INFO;
                }
                break;
            case PLAYER_INFO:
                PLAYER_INFO_SCREEN.enterPlayerName(input);
                PLAYER_INFO_SCREEN.draw();
                // If "ENTER" key is pressed, then start the game.
                if (input.wasPressed(Keys.ENTER)) {
                    currentGameState = GameState.GAME_ONGOING;
                }
                break;
            case GAME_ONGOING:
                ONGOING_GAME_SCREEN.draw();
                ONGOING_GAME_SCREEN.update(input);
                // Check if game over conditions were met.
                if (ONGOING_GAME_SCREEN.canGameEnd()) {
                    currentGameState = GameState.GAME_END;
                    gameEndScreen = new GameEndScreen(PLAYER_INFO_SCREEN.getPlayerName(),
                            ONGOING_GAME_SCREEN.getTotalScore(), GAME_PROPS, MESSAGE_PROPS);
                }
                break;
            case GAME_END:
                gameEndScreen.draw();
                break;
        }
        if (currentGameState == GameState.GAME_END && input.wasPressed(Keys.SPACE)) {
            PLAYER_INFO_SCREEN.resetPlayerName();
            currentGameState = GameState.HOME_SCREEN;
            ONGOING_GAME_SCREEN.resetGame();
        }
    }

    /**
     * Main entry point for the ShadowTaxi game, initializes properties and runs the game logic.
     * @param args Command line arguments (not used in this game).
     */
    public static void main(String[] args) {
        Properties game_props = IOUtils.readPropertiesFile("res/app.properties");
        Properties message_props = IOUtils.readPropertiesFile("res/message_en.properties");
        ShadowTaxi game = new ShadowTaxi(game_props, message_props);
        game.run();
    }
}
