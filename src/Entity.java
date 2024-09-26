import bagel.*;
import java.util.Properties;


public abstract class Entity implements Drawable, Scrollable {

    protected final Image IMAGE;
    protected final double RADIUS;
    protected final int SCROLL_SPEED;

    // Position variables
    private int x;
    private int y;

    public Entity(int x, int y, Properties gameProps, String imageProperty, String radiusProperty) {
        this.x = x;
        this.y = y;

        IMAGE = new Image(gameProps.getProperty(imageProperty));
        RADIUS = Double.parseDouble(gameProps.getProperty(radiusProperty));
        SCROLL_SPEED = Integer.parseInt(gameProps.getProperty("gameObjects.taxi.speedY"));
    }

    /**
     * Update the entity based on input.
     */
    public void update(Input input) {
        if (input.isDown(Keys.UP)) {
            moveDown();
        }
        draw();
    }

    /**
     * Moves the entity down the screen.
     */
    @Override
    public void moveDown() {
        this.y += SCROLL_SPEED;
    }

    /**
     * Draws the entity.
     */
    @Override
    public void draw() {
        IMAGE.draw(x, y);
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    protected void setX(int x) {
        this.x = x;
    }

    protected void setY(int y) {
        this.y = y;
    }

    public double getRadius() {
        return RADIUS;
    }
}
