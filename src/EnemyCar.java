import java.util.Properties;
import java.util.ArrayList;

public class EnemyCar extends Car {

    private final int FIREBALL_SPAWN_RATE = 300;
    private final Properties GAME_PROPS;
    private ArrayList<Fireball> fireballs;

    public EnemyCar(Properties gameProps, ArrayList<Fireball> fireballs) {
        super(gameProps,
                gameProps.getProperty("gameObjects.enemyCar.image"),
                "gameObjects.enemyCar.radius", "gameObjects.enemyCar.health",
                "gameObjects.enemyCar.damage", "gameObjects.enemyCar.minSpeedY",
                "gameObjects.enemyCar.maxSpeedY");
        this.GAME_PROPS = gameProps;
        this.fireballs = fireballs;
    }

    @Override
    public void update() {
        super.update();

        if (MiscUtils.canSpawn(FIREBALL_SPAWN_RATE)) {
            shootFireball();
        }
    }

    private void shootFireball() {
        Fireball newFireball = new Fireball(GAME_PROPS, getX(), getY());
        fireballs.add(newFireball);
    }
}
