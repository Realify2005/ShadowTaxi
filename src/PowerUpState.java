import bagel.Font;
import java.util.Properties;

/**
 * CoinState class tracks the state of all power ups in the gameplay.
 * (i.e. coin and invincible power).
 */

public class PowerUpState {
    // Constants related to font.
    private final String FONT_PATH;
    private final int FONT_SIZE;

    // Constants related to coin state gameplay.
    private final int COIN_MAX_FRAMES;
    private final int GAMEPLAY_COIN_X;
    private final int GAMEPLAY_COIN_Y;

    private final int INVINCIBLE_MAX_FRAMES;

    private final int INCREASE_PER_FRAME;
    private final int INITIAL_FRAME_COUNT;

    // Variables
    private boolean isCoinActivated;
    private boolean isInvincibleActivated;
    private int coinFrameCount;
    private int invincibleFrameCount;

    private final Font FONT;

    public PowerUpState(Properties properties) {
        this.isCoinActivated = false;
        this.isInvincibleActivated = false;

        COIN_MAX_FRAMES = Integer.parseInt(properties.getProperty("gameObjects.coin.maxFrames"));
        GAMEPLAY_COIN_X = Integer.parseInt(properties.getProperty("gameplay.coin.x"));
        GAMEPLAY_COIN_Y = Integer.parseInt(properties.getProperty("gameplay.coin.y"));

        INVINCIBLE_MAX_FRAMES = Integer.parseInt(properties.getProperty("gameObjects.invinciblePower.maxFrames"));

        FONT_PATH = properties.getProperty("font");
        FONT_SIZE = Integer.parseInt(properties.getProperty("gamePlay.info.fontSize"));

        INCREASE_PER_FRAME = 1;
        INITIAL_FRAME_COUNT = 0;

        this.FONT = new Font(FONT_PATH, FONT_SIZE);
    }

    /***
     * Updates the coin state of the gameplay every frame.
     */
    public void update() {
        renderCoinFrames();
        updateCoinFrameCount();
        updateInvincibleFrameCount();
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

    public void activateInvincibleEffect(InvinciblePower invinciblePower) {
        invinciblePower.wasTaken();
        this.isInvincibleActivated = true;
        refreshInvincibleFrameCount();
    }

    /***
     * Regenerates the frame count for coin taking effect.
     * Does this everytime coin is picked up.
     */
    private void refreshCoinFrameCount() {
        this.coinFrameCount = INITIAL_FRAME_COUNT;
    }

    private void refreshInvincibleFrameCount() {this.invincibleFrameCount = INITIAL_FRAME_COUNT; }

    /***
     * Draws the coin frame text on the top right of gameplay screen.
     */
    private void renderCoinFrames() {
        if (isCoinActivated) {
            FONT.drawString(String.valueOf(coinFrameCount), GAMEPLAY_COIN_X, GAMEPLAY_COIN_Y);
        }
        if (isInvincibleActivated) {
            FONT.drawString(String.valueOf(invincibleFrameCount), 900, 50); // delete after
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

    private void updateInvincibleFrameCount() {
        if (invincibleFrameCount <= INVINCIBLE_MAX_FRAMES) {
            invincibleFrameCount += INCREASE_PER_FRAME;
        } else {
            isInvincibleActivated = false;
        }
    }

    public boolean isCoinActivated() {
        return isCoinActivated;
    }

    public boolean isInvincibleActivated() { return isInvincibleActivated; }
}