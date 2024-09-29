import bagel.Font;
import java.util.Properties;

/**
 * CoinState class tracks the state of coin entity in the gameplay.
 * (i.e. is coin effect supposed to be taking place at this time).
 */

public class CoinState {
    // Constants related to font.
    private final String FONT_PATH;
    private final int FONT_SIZE;

    // Constants related to coin state gameplay.
    private final int COIN_MAX_FRAMES;
    private final int GAMEPLAY_COIN_X;
    private final int GAMEPLAY_COIN_Y;
    private final int INCREASE_PER_FRAME;
    private final int INITIAL_COIN_FRAMES;

    // Variables
    private boolean isCoinActivated;
    private int coinFrameCount;

    private final Font FONT;

    public CoinState(Properties properties) {
        this.isCoinActivated = false;

        COIN_MAX_FRAMES = Integer.parseInt(properties.getProperty("gameObjects.coin.maxFrames"));
        GAMEPLAY_COIN_X = Integer.parseInt(properties.getProperty("gameplay.coin.x"));
        GAMEPLAY_COIN_Y = Integer.parseInt(properties.getProperty("gameplay.coin.y"));

        FONT_PATH = properties.getProperty("font");
        FONT_SIZE = Integer.parseInt(properties.getProperty("gamePlay.info.fontSize"));

        INCREASE_PER_FRAME = 1;
        INITIAL_COIN_FRAMES = 0;

        this.FONT = new Font(FONT_PATH, FONT_SIZE);
    }

    /***
     * Updates the coin state of the gameplay every frame.
     */
    public void update() {
        renderCoinFrames();
        updateCoinFrameCount();
    }

    /***
     * Activates the coin effect in the gameplay.
     * (i.e. Result of taxi collision with coin).
     */
    public void activateCoinEffect(Coin coin) {
        // Set coin to be "taken" so that it will no longer be rendered on the game
        coin.wasTaken();
        this.isCoinActivated = true;
        refreshCoinFrameCount();
    }

    /***
     * Regenerates the frame count for coin taking effect.
     * Does this everytime coin is picked up.
     */
    private void refreshCoinFrameCount() {
        this.coinFrameCount = INITIAL_COIN_FRAMES;
    }

    /***
     * Draws the coin frame text on the top right of gameplay screen.
     */
    private void renderCoinFrames() {
        if (isCoinActivated) {
            FONT.drawString(String.valueOf(coinFrameCount), GAMEPLAY_COIN_X, GAMEPLAY_COIN_Y);
        }
    }

    /***
     * Updates the coin frame count text on top right of gameplay screen.
     */
    private void updateCoinFrameCount() {
        if (coinFrameCount <= COIN_MAX_FRAMES) {
            coinFrameCount += INCREASE_PER_FRAME;
        } else {
            isCoinActivated = false;
        }
    }

    public boolean isCoinActivated() {
        return isCoinActivated;
    }
}
