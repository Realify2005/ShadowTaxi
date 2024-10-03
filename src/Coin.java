import java.util.Properties;

/**
 * Class representing coin entity in the game, which is a PowerUp.
 * Coin can be collected by either taxi or driver.
 * When collected, it will set all passenger's priority to increase by 1.
 * This effect will last for a certain number of frames.
 * After that, player will need to collect another coin to reactivate said PowerUp.
 */
public class Coin extends PowerUp {

    /**
     * Constructor for the Coin class.
     * Initializes its (x, y) position, image, and radius.
     * @param x The x-coordinate of coin's position.
     * @param y The y-coordinate of coin's position.
     * @param gameProps The game properties object containing various game configuration values.
     */
    public Coin(int x, int y, Properties gameProps) {
        super(x, y, gameProps, "gameObjects.coin.image", "gameObjects.coin.radius");
    }

    /**
     * Activates coin effect when it is "picked up" by taxi or driver.
     * The collided coin will no longer be drawn (since it was taken).
     * The coin frame count at the top right of screen will be rendered to indicate coin power is currently active.
     * @param powerUpState Class that tracks the state ana handles the overall logic of power-ups in the ongoing game.
     */
    @Override
    public void activate(PowerUpState powerUpState) {
        this.wasTaken();
        powerUpState.refreshCoinFrameCount();
    }

}
