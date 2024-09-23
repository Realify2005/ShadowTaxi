import bagel.*;
import bagel.Image;
import java.util.Properties;

/**
 * Class for the coin entity.
 */

public class InvinciblePower extends PowerUp {

    public InvinciblePower(int x, int y, Properties properties) {
        super(x, y, properties, "gameObjects.invinciblePower.image",
                "gameObjects.invinciblePower.radius");
    }
}
