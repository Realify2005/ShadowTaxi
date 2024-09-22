import bagel.*;
import java.util.Properties;

/**
 * Skeleton Code for SWEN20003 Project 1, Semester 2, 2024
 * Please enter your name below
 * Clement Chau
 */

public class ShadowTaxi extends AbstractGame {

    // Constants related to properties that other variables refer to.
    private final Properties GAME_PROPS;
    private final Properties MESSAGE_PROPS;

    // Game State controls what is shown on screen.
    private GameState currentGameState = GameState.HOME_SCREEN;

    // Screen renderer responsible for rendering home, playerInfo, and ongoing game backgrounds.
    private final HomeScreen HOME_SCREEN;
    private final PlayerInfoScreen PLAYER_INFO_SCREEN;
    private final OngoingGameScreen ONGOING_GAME_SCREEN;
    private GameEndScreen gameEndScreen; // Responsible for rendering game end screen.

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
     * Render the relevant screens and game objects based on the keyboard input
     * given by the user and the status of the game play.
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
                if (ONGOING_GAME_SCREEN.hasGameEnded()) {
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

    public static void main(String[] args) {
        Properties game_props = IOUtils.readPropertiesFile("res/app.properties");
        Properties message_props = IOUtils.readPropertiesFile("res/message_en.properties");
        ShadowTaxi game = new ShadowTaxi(game_props, message_props);
        game.run();
    }
}
