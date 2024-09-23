import bagel.*;

import java.util.ArrayList;
import java.util.Properties;

/**
 * Class to render the ongoing game screen.
 * Also handles the ongoing game's background scrolling.
 */
public class OngoingGameScreen extends Screen {

    // Constant that defines the vertical scroll speed of the ongoing game background.
    private final int SCROLL_SPEED;

    // Game entities
    private Taxi taxi;
    private ArrayList<Passenger> passengers;
    private ArrayList<Coin> coins;
    private ArrayList<InvinciblePower> invinciblePowers;
    private TripEndFlag tripEndFlag;
    private PowerUpState powerUpState;
    private GameStats gameStats;
    private Gameplay gameplay;

    private boolean isRaining;

    // Ongoing game background positions
    private double background1Y = Window.getHeight() / 2.0; // Y-coordinate = 384
    private double background2Y = -Window.getHeight() / 2.0; // Y-coordinate = -384

    private ArrayList<Weather> weatherInfo;
    private int currentFrame;

    public OngoingGameScreen(Properties gameProps, Properties messageProps) {
        super(gameProps, messageProps, new Image(gameProps.getProperty("backgroundImage.sunny")));

        // Scroll speed for the ongoing game background can be referred to taxi's "scroll speed".
        SCROLL_SPEED = Integer.parseInt(gameProps.getProperty("gameObjects.taxi.speedY"));

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
     * Constantly updates the ongoing game background screen.
     * Mainly for the vertical scrolling.
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

        for (Coin coin : coins) {
            coin.update(input);
            if (taxi.collidedWith(coin) && !coin.isTaken()) {
                powerUpState.activateCoinEffect(coin);
            }
        }

        for (InvinciblePower invinciblePower : invinciblePowers) {
            invinciblePower.update(input);
            if (taxi.collidedWith(invinciblePower) && !invinciblePower.isTaken()) {
                powerUpState.activateInvincibleEffect(invinciblePower);
            }
            // if driver collided with invincible power too...
        }

        for (Passenger passenger : passengers) {
            passenger.update(input, isRaining);
        }

        powerUpState.update();
        gameStats.update();
        gameplay.update(input);
        taxi.update(input);
    }

    /**
     * Loads given file path, creates instance of taxi,
     * array of passengers instances, array of coins instances
     * to be used by the game (class).
     * @param filePath The csv file path to be processed (i.e. gameObjects.csv).
     */
    private void loadGameObjects(String filePath) {
        String[][] gameObjects = IOUtils.readCommaSeparatedFile(filePath);
        passengers = new ArrayList<>();
        coins = new ArrayList<>();
        invinciblePowers = new ArrayList<>();
        for (String[] objectData : gameObjects) {
            String Item = objectData[0];
            switch (Item) {
                case "TAXI":
                    int taxiX = Integer.parseInt(objectData[1]);
                    int taxiY = Integer.parseInt(objectData[2]);
                    taxi = new Taxi(taxiX, taxiY, gameplay, GAME_PROPS);
                    break;
                case "PASSENGER":
                    int passengerX = Integer.parseInt(objectData[1]);
                    int passengerY = Integer.parseInt(objectData[2]);
                    int priority = Integer.parseInt(objectData[3]);
                    int endX = Integer.parseInt(objectData[4]);
                    int distanceY = Integer.parseInt(objectData[5]);
                    int hasUmbrella = Integer.parseInt(objectData[6]);
                    passengers.add(new Passenger(passengerX, passengerY, priority, endX, distanceY, hasUmbrella,
                            taxi, powerUpState, GAME_PROPS));
                    break;
                case "COIN":
                    int coinX = Integer.parseInt(objectData[1]);
                    int coinY = Integer.parseInt(objectData[2]);
                    coins.add(new Coin(coinX, coinY, GAME_PROPS));
                    break;
                case "INVINCIBLE_POWER":
                    int invinciblePowerX = Integer.parseInt(objectData[1]);
                    int invinciblePowerY = Integer.parseInt(objectData[2]);
                    invinciblePowers.add(new InvinciblePower(invinciblePowerX, invinciblePowerY, GAME_PROPS));
            }
        }
        gameplay.initialiseTaxi(taxi);
        gameplay.initialisePassengers(passengers);
    }

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
        coins = new ArrayList<>();
        tripEndFlag = null;
        powerUpState = new PowerUpState(GAME_PROPS);
        gameStats = new GameStats(GAME_PROPS, MESSAGE_PROPS);
        gameplay = new Gameplay(tripEndFlag, powerUpState, gameStats, GAME_PROPS, MESSAGE_PROPS);
        resetBackground();
        loadGameObjects(GAME_PROPS.getProperty("gamePlay.objectsFile"));
        currentFrame = 0;
    }

    /**
     * Checks if the game has ended.
     */
    public boolean hasGameEnded() {
        return gameStats.getRemainingFrames() <= 0 || gameStats.getTotalScore() >= 500;
    }

    /**
     * Gets total score from game stats.
     */
    public double getTotalScore() {
        return gameStats.getTotalScore();
    }
}
