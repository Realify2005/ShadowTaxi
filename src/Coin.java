import java.util.Properties;

/**
 * Class for the coin entity.
 */

public class Coin extends PowerUp {

    public Coin(int x, int y, Properties properties) {
        super(x, y, properties, "gameObjects.coin.image", "gameObjects.coin.radius");
    }

    /***
     * Activates the coin effect in the gameplay.
     * (i.e. Result of taxi/driver collision with coin).
     */
    @Override
    public void activate(PowerUpState powerUpState) {
        this.wasTaken();
        powerUpState.refreshCoinFrameCount();
    }
}

