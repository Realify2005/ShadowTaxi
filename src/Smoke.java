import java.util.Properties;

public class Smoke extends TemporaryEffect {
    public Smoke(int x, int y, Properties gameProps) {
        super(x, y, gameProps, "gameObjects.smoke.ttl", "gameObjects.smoke.image");
    }
}
