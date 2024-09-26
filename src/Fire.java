import java.util.Properties;

public class Fire extends TemporaryEffect {
    public Fire(int x, int y, Properties gameProps) {
        super(x, y, gameProps, "gameObjects.fire.ttl", "gameObjects.fire.image");
    }
}
