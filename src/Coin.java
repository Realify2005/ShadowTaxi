import java.util.Properties;

/**
 * Class for the coin entity.
 */

public class Coin extends PowerUp {

    public Coin(int x, int y, Properties properties) {
        super(x, y, properties, "gameObjects.coin.image", "gameObjects.coin.radius");
    }
}

