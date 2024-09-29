import bagel.Image;
import java.util.Properties;

public class Fireball implements Drawable {

    private final Image IMAGE;
    private final double RADIUS;
    private final double DAMAGE;
    private final int SHOOT_SPEED_Y;
    private final Car SPAWNED_BY;

    private int x;
    private int y;
    private boolean isCollided;

    public Fireball(Properties gameProps, int startX, int startY, Car SPAWNED_BY) {
        int PROPS_TO_GAME_MULTIPLIER = 100;
        this.IMAGE = new Image(gameProps.getProperty("gameObjects.fireball.image"));
        this.RADIUS = Double.parseDouble(gameProps.getProperty("gameObjects.fireball.radius"));
        this.DAMAGE = Double.parseDouble(
                gameProps.getProperty("gameObjects.fireball.damage")
        ) * PROPS_TO_GAME_MULTIPLIER;
        this.SHOOT_SPEED_Y = Integer.parseInt(gameProps.getProperty("gameObjects.fireball.shootSpeedY"));

        // Start position of the fireball at the enemy car's current coordinates
        this.x = startX;
        this.y = startY;
        this.isCollided = false;
        this.SPAWNED_BY = SPAWNED_BY;
    }

    public void update() {
        moveUp();
        draw();
    }

    public boolean collidesWith(int otherX, int otherY, double otherRadius) {

        double distance = getDistanceTo(otherX, otherY);
        double collisionRange = this.RADIUS + otherRadius;

        return distance < collisionRange;
    }

    public double getDistanceTo(double otherX, double otherY) {
        return Math.sqrt(Math.pow(otherX - this.x, 2) + Math.pow(otherY - this.y, 2));
    }

    private void moveUp() {
        this.y -= SHOOT_SPEED_Y;
    }

    @Override
    public void draw() {
        if (!isOffScreen() && !isCollided) {
            IMAGE.draw(x, y);
        }
    }

    public boolean isOffScreen() {
        return this.y <= 0;
    }

    public double getDamage() {
        return DAMAGE;
    }

    public Car getSpawnedBy() {
        return SPAWNED_BY;
    }

    public void wasCollided() {
        isCollided = true;
    }

    public boolean isCollided() {
        return isCollided;
    }
}
