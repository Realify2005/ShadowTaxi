import bagel.Image;
import java.util.Properties;

public class Fireball implements Drawable {

    private final Image IMAGE;
    private final double RADIUS;
    private final double DAMAGE;
    private final int SHOOT_SPEED_Y;

    private int x;
    private int y;

    public Fireball(Properties gameProps, int startX, int startY) {
        this.IMAGE = new Image(gameProps.getProperty("gameObjects.fireball.image"));
        this.RADIUS = Double.parseDouble(gameProps.getProperty("gameObjects.fireball.radius"));
        this.DAMAGE = Double.parseDouble(gameProps.getProperty("gameObjects.fireball.damage"));
        this.SHOOT_SPEED_Y = Integer.parseInt(gameProps.getProperty("gameObjects.fireball.shootSpeedY"));

        // Start position of the fireball at the enemy car's current coordinates
        this.x = startX;
        this.y = startY;
    }

    public void update() {
        moveUp();
        draw();
    }

    private void moveUp() {
        this.y -= SHOOT_SPEED_Y;
    }

    public void draw() {
        if (!isOffScreen()) {
            IMAGE.draw(x, y);
        }
    }

    public boolean isOffScreen() {
        return this.y <= 0;
    }
}
