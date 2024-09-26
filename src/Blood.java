import java.util.Properties;

public class Blood extends TemporaryEffect {
    public Blood(int x, int y, Properties gameProps) {
        super(x, y, gameProps, "gameObjects.blood.ttl", "gameObjects.blood.image");
    }
}
