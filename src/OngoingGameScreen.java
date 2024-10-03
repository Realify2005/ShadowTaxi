import bagel.*;

import java.util.ArrayList;
import java.util.Properties;

/**
 * Class to render the ongoing game screen.
 * Handles the logic behind switching between sunny and rainy background screens.
 * Also handles the ongoing game's background scrolling.
 */
public class OngoingGameScreen extends Screen {

    /**
     * The vertical movement speed of the ongoing game background.
     */
    private final int SCROLL_SPEED;

    /**
     * The maximum height of the game window.
     */
    private final int WINDOW_MAX_HEIGHT;

    /**
     * The taxi entity in the ongoing game.
     */
    private Taxi taxi;

    /**
     * The driver entity in the ongoing game.
     */
    private Driver driver;

    /**
     * The list of all passenger entities in the ongoing game.
     */
    private ArrayList<Passenger> passengers;

    /**
     * The list of all power-ups in the ongoing game.
     */
    private ArrayList<PowerUp> powerUps;

    /**
     * The trip end flag is placed at the end target Y-coordinate of a current active trip to drop off the passenger to.
     */
    private TripEndFlag tripEndFlag;

    /**
     * The current state of power-ups in the ongoing game.
     */
    private PowerUpState powerUpState;

    /**
     * Class to track various game statistics and renders them on screen.
     */
    private GameStats gameStats;

    /**
     * Class that manages the central game flow and interactions.
     */
    private Gameplay gameplay;

    /**
     * A boolean flag indicating whether the weather is currently raining or not.
     */
    private boolean isRaining;

    /**
     * The Y-coordinate position for the first background to mimic scrolling effect.
     */
    private double background1Y = Window.getHeight() / 2.0;

    /**
     * The Y-coordinate position for the second background to mimic scrolling effect.
     */
    private double background2Y = -Window.getHeight() / 2.0;

    /**
     * List of weather conditions that controls the sunny/rainy weather in-game.
     */
    private ArrayList<Weather> weatherInfo;

    /**
     * The current frame of the game, increases by 1 per unit time and is capped at a certain number.
     */
    private int currentFrame;

    /**
     * Constructor for ongoing game screen class.
     * Initialises both sunny/rainy background images, various gameplay related classes to handle logic and track
     * gameplay statistics.
     * Calls helper functions to load in and create initial entities on screen as well as sunny/rain conditions based
     * on game objects and weather information files.
     * @param gameProps The properties object containing game configuration values.
     * @param messageProps The properties object containing text configuration values.
     */
    public OngoingGameScreen(Properties gameProps, Properties messageProps) {
        super(gameProps, messageProps, new Image(gameProps.getProperty("backgroundImage.sunny")));

        // Scroll speed for the ongoing game background can be referred to taxi's "scroll speed".
        SCROLL_SPEED = Integer.parseInt(gameProps.getProperty("gameObjects.taxi.speedY"));

        WINDOW_MAX_HEIGHT = Integer.parseInt(gameProps.getProperty("window.height"));

        powerUpState = new PowerUpState(gameProps);
        gameStats = new GameStats(gameProps, messageProps);
        gameplay = new Gameplay(tripEndFlag, powerUpState, gameStats, gameProps, messageProps);
        loadGameObjects(gameProps.getProperty("gamePlay.objectsFile"));
        loadWeatherInfo(gameProps.getProperty("gamePlay.weatherFile"));

        currentFrame = 0;
    }

    /**
     * Renders the main (gameplay) screen.
     */
    @Override
    public void draw() {
        Weather currentWeather = getCurrentWeather();

        assert currentWeather != null; // This should always pass as long as the weather file is set up properly.

        if (currentWeather.getType().equals("SUNNY")) {
            BACKGROUND_IMAGE = new Image(this.GAME_PROPS.getProperty("backgroundImage.sunny"));
            isRaining = false;
        } else {
            BACKGROUND_IMAGE = new Image(this.GAME_PROPS.getProperty("backgroundImage.raining"));
            isRaining = true;
        }

        // Draw first background image, coordinate (512, 384)
        BACKGROUND_IMAGE.draw(Window.getWidth() / 2.0, background1Y);

        // Draw second background image, coordinate (512, -384)
        BACKGROUND_IMAGE.draw(Window.getWidth() / 2.0, background2Y);
    }

    /**
     * Resets the ongoing game background to its original position.
     */
    public void resetBackground() {
        background1Y = Window.getHeight() / 2.0;
        background2Y = -Window.getHeight() / 2.0;
    }

    /**
     * Constantly updates the ongoing game background screen, including all the helper classes to control gameplay flow.
     * Mainly for the vertical scrolling.
     * @param input The current mouse/keyboard input.
     */
    public void update(Input input) {
        final int BACKGROUND_LEFT_BOTTOM_WINDOW = 1152;
        currentFrame++;

        if (input.isDown(Keys.UP)) {
            background1Y += SCROLL_SPEED;
            background2Y += SCROLL_SPEED;

            // Check if backgrounds need to be repositioned
            if (background1Y >= BACKGROUND_LEFT_BOTTOM_WINDOW) {
                background1Y = background2Y - Window.getHeight();
            }
            if (background2Y >= BACKGROUND_LEFT_BOTTOM_WINDOW) {
                background2Y = background1Y - Window.getHeight();
            }

        }

        powerUpState.update();
        gameStats.update();
        gameplay.update(input, isRaining);
    }

    /**
     * Loads given game object file path, creates instance of taxi, driver, array of passengers instances,
     * array of coins instances, and array of invincible power instances to be used by the game (class).
     * Also initialises necessary other classes inside classes such as gameplay and passenger for gameplay logic.
     * @param filePath The game objects csv file path to be processed (i.e. gameObjects.csv).
     */
    private void loadGameObjects(String filePath) {
        String[][] gameObjects = IOUtils.readCommaSeparatedFile(filePath);
        passengers = new ArrayList<>();
        powerUps = new ArrayList<>();
        for (String[] objectData : gameObjects) {
            String Item = objectData[0];
            switch (Item) {
                case "TAXI":
                    int taxiX = Integer.parseInt(objectData[1]);
                    int taxiY = Integer.parseInt(objectData[2]);
                    taxi = new Taxi(taxiX, taxiY, gameplay, powerUpState, GAME_PROPS, MESSAGE_PROPS);
                    break;
                case "DRIVER":
                    int driverX = Integer.parseInt(objectData[1]);
                    int driverY = Integer.parseInt(objectData[2]);
                    driver = new Driver(driverX, driverY, powerUpState, GAME_PROPS, MESSAGE_PROPS);
                    break;
                case "PASSENGER":
                    int passengerX = Integer.parseInt(objectData[1]);
                    int passengerY = Integer.parseInt(objectData[2]);
                    int priority = Integer.parseInt(objectData[3]);
                    int endX = Integer.parseInt(objectData[4]);
                    int distanceY = Integer.parseInt(objectData[5]);
                    int hasUmbrella = Integer.parseInt(objectData[6]);
                    passengers.add(new Passenger(passengerX, passengerY, priority, endX, distanceY, hasUmbrella,
                            powerUpState, GAME_PROPS, MESSAGE_PROPS));
                    break;
                case "COIN":
                    int coinX = Integer.parseInt(objectData[1]);
                    int coinY = Integer.parseInt(objectData[2]);
                    powerUps.add(new Coin(coinX, coinY, GAME_PROPS));
                    break;
                case "INVINCIBLE_POWER":
                    int invinciblePowerX = Integer.parseInt(objectData[1]);
                    int invinciblePowerY = Integer.parseInt(objectData[2]);
                    powerUps.add(new InvinciblePower(invinciblePowerX, invinciblePowerY, GAME_PROPS));
            }
        }
        gameplay.initialiseTaxi(taxi);
        gameplay.initialiseDriver(driver);
        gameplay.initialisePassengers(passengers);
        gameplay.initialisePowerUps(powerUps);

        for (Passenger passenger : passengers) {
            passenger.initialiseDriver(driver);
        }
    }

    /**
     * Loads given weather file path, creates instances of Weather and stores them in a list.
     * This will serve as a guide to the type of weather (sunny/rainy) to be rendered at a certain timeframe.
     * @param filePath The weather csv file path to be processed (i.e. gameWeather.csv).
     */
    private void loadWeatherInfo(String filePath) {
        weatherInfo = new ArrayList<>();
        String[][] rows = IOUtils.readCommaSeparatedFile(filePath);
        for (String[] row : rows) {
            String type = row[0];
            int startFrame = Integer.parseInt(row[1]);
            int endFrame = Integer.parseInt(row[2]);
            weatherInfo.add(new Weather(type, startFrame, endFrame));
        }
    }

    /**
     * Gets the current weather type at a given timeframe.
     * @return The weather type for currentFrame.
     */
    private Weather getCurrentWeather() {
        for (Weather weather : weatherInfo) {
            if (currentFrame >= weather.getStartFrame() && currentFrame <= weather.getEndFrame()) {
                return weather;
            }
        }
        return null; // It should never reach here if the weather file is set up correctly.
    }

    /**
     * Resets the game to its initial state.
     */
    public void resetGame() {
        taxi = null;
        passengers = new ArrayList<>();
        powerUps = new ArrayList<>();
        tripEndFlag = null;
        powerUpState = new PowerUpState(GAME_PROPS);
        gameStats = new GameStats(GAME_PROPS, MESSAGE_PROPS);
        gameplay = new Gameplay(tripEndFlag, powerUpState, gameStats, GAME_PROPS, MESSAGE_PROPS);
        resetBackground();
        loadGameObjects(GAME_PROPS.getProperty("gamePlay.objectsFile"));
        currentFrame = 0;
    }

    /**
     * Checks if the game can end.
     * There are 5 conditions in which a game can be declared to have ended, each will be explained in its own functions
     * below.
     * @return True if game can end, false otherwise.
     */
    public boolean canGameEnd() {
        return ranOutOfFrames() || winningScoreReached() || taxiLeftScreenWithoutDriver()
                || isDriverDead() || isPassengerDead();
    }

    /**
     * Checks if maximum total score has been reached. This will result in a win.
     * @return True if maximum total score has been reached, false otherwise.
     */
    private boolean winningScoreReached() {
        return gameStats.getTotalScore() >= 500;
    }

    /**
     * Checks if maximum frames has been reached. This will result in a loss.
     * @return True if maximum frames has been reached, false otherwise.
     */
    private boolean ranOutOfFrames() {
        return gameStats.getRemainingFrames() <= 0;
    }

    /**
     * Checks if an empty active taxi has left the screen from the bottom. This will result in a loss.
     * @return True if empty active taxi has left the screen from the bottom without a driver inside it.
     */
    private boolean taxiLeftScreenWithoutDriver() {
        return (gameplay.getTaxi().getY() >= WINDOW_MAX_HEIGHT) && !gameplay.getTaxi().hasDriver();
    }

    /**
     * Checks if driver is dead. This will result in a loss.
     * @return True if driver has died, false otherwise.
     */
    private boolean isDriverDead() {
        return driver.getCurrentHealth() <= 0;
    }

    /**
     * Checks if any passenger is dead. This will result in a loss.
     * @return True if any passenger has died, false otherwise.
     */
    private boolean isPassengerDead() {
        for (Passenger passenger : passengers) {
            if (passenger.getCurrentHealth() <= 0) {
                return true;
            }
        }
        return false;
    }

    /**
     * Gets total score from game stats.
     * @return The current total score of gameplay.
     */
    public double getTotalScore() {
        return gameStats.getTotalScore();
    }
}
