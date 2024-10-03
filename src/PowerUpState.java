import bagel.Font;
import java.util.Properties;

/**
 * PowerUpState class tracks the current state of all power-ups in-game, including coin and invincible power.
 */
public class PowerUpState {
    /**
     * The path to the font used to draw the frames remaining for coin when activated.
     */
    private final String FONT_PATH;

    /**
     * The font size used to draw the frames remaining for coin when activated.
     */
    private final int FONT_SIZE;

    /**
     * The maximum number of frames for which a coin power-up is active.
     */
    private final int COIN_MAX_FRAMES;

    /**
     * The X-coordinate for displaying the coin frame count.
     */
    private final int GAMEPLAY_COIN_X;

    /**
     * The Y-coordinate for displaying the coin frame count.
     */
    private final int GAMEPLAY_COIN_Y;

    /**
     * The maximum number of frames for which the invincible power-up is active.
     */
    private final int INVINCIBLE_MAX_FRAMES;

    /**
     * The number of frames to increase the current frame count for power-ups per unit time.
     */
    private final int INCREASE_PER_FRAME;

    /**
     * The initial frame count for power-ups.
     */
    private final int INITIAL_FRAME_COUNT;

    /**
     * Boolean that indicates whether the coin power-up is currently active.
     */
    private boolean isCoinActivated;

    /**
     * Boolean that indicates whether the invincible power-up is currently active.
     */
    private boolean isInvincibleActivated;

    /**
     * The current frame count for the coin power-up.
     */
    private int coinFrameCount;

    /**
     * The current frame count for the invincible power-up.
     */
    private int invincibleFrameCount;

    /**
     * The font object used for rendering text.
     */
    private final Font FONT;

    /**
     * Constructor to create a power up state class.
     * @param gameProps The properties object containing game configuration values.
     */
    public PowerUpState(Properties gameProps) {
        this.isCoinActivated = false;
        this.isInvincibleActivated = false;

        COIN_MAX_FRAMES = Integer.parseInt(gameProps.getProperty("gameObjects.coin.maxFrames"));
        GAMEPLAY_COIN_X = Integer.parseInt(gameProps.getProperty("gameplay.coin.x"));
        GAMEPLAY_COIN_Y = Integer.parseInt(gameProps.getProperty("gameplay.coin.y"));

        INVINCIBLE_MAX_FRAMES = Integer.parseInt(gameProps.getProperty("gameObjects.invinciblePower.maxFrames"));

        FONT_PATH = gameProps.getProperty("font");
        FONT_SIZE = Integer.parseInt(gameProps.getProperty("gamePlay.info.fontSize"));

        INCREASE_PER_FRAME = 1;
        INITIAL_FRAME_COUNT = 0;

        this.FONT = new Font(FONT_PATH, FONT_SIZE);
    }

    /**
     * Updates the coin and invincible power-up total frames.
     * Draws the total frames of coin in the top right screen.
     */
    public void update() {
        renderCoinFrames();
        updateCoinFrameCount();
        updateInvincibleFrameCount();
    }

    /**
     * Activates the power-up.
     * @param powerUp The power-up to be activated.
     */
    public void activatePowerUp(PowerUp powerUp) {
        powerUp.activate(this);
    }

    /**
     * Resets the coin total frame count when the coin power-up is picked up.
     */
    public void refreshCoinFrameCount() {
        this.coinFrameCount = INITIAL_FRAME_COUNT;
        this.isCoinActivated = true;
    }

    /**
     * Resets the invincible total frame count when the invincible power-up is picked up.
     */
    public void refreshInvincibleFrameCount() {
        this.invincibleFrameCount = INITIAL_FRAME_COUNT;
        this.isInvincibleActivated = true;
    }

    /**
     * Draws the coin frame count on the top right screen.
     */
    private void renderCoinFrames() {
        if (isCoinActivated) {
            FONT.drawString(String.valueOf(coinFrameCount), GAMEPLAY_COIN_X, GAMEPLAY_COIN_Y);
        }
    }

    /**
     * Updates the current coin total frame count.
     */
    private void updateCoinFrameCount() {
        if (coinFrameCount <= COIN_MAX_FRAMES) {
            coinFrameCount += INCREASE_PER_FRAME;
        } else {
            isCoinActivated = false;
        }
    }

    /**
     * Updates the current invincible total frame count.
     */
    private void updateInvincibleFrameCount() {
        if (invincibleFrameCount <= INVINCIBLE_MAX_FRAMES) {
            invincibleFrameCount += INCREASE_PER_FRAME;
        } else {
            isInvincibleActivated = false;
        }
    }

    /**
     * Disable all current active power-ups (When taxi is broken).
     */
    public void resetPowerUps() {
        this.coinFrameCount = COIN_MAX_FRAMES;
        this.invincibleFrameCount = INVINCIBLE_MAX_FRAMES;
    }

    /**
     * Checks if the coin power-up is currently active.
     * @return True if coin power-up is currently active, false otherwise.
     */
    public boolean isCoinActivated() {
        return isCoinActivated;
    }

    /**
     * Checks if the invincible power-up is currently active.
     * @return True if the invincible power-up is currently active, false otherwise.
     */
    public boolean isInvincibleActivated() {
        return isInvincibleActivated;
    }
}
