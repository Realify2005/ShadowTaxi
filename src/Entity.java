import bagel.*;
import java.util.Properties;

/**
 * Abstract class representing Entities in the game.
 * Entities are objects that have (x, y) coordinates, an image, radius, and can move down.
 */
public abstract class Entity implements Drawable, Scrollable {

    /**
     * The image of the entity.
     */
    protected final Image IMAGE;

    /**
     * The radius of the entity, used for collision detection.
     */
    protected final double RADIUS;

    /**
     * The scroll speed of the entity, to indicate how fast it moves down the screen.
     */
    protected final int SCROLL_SPEED;

    /**
     * The X position of the entity.
     */
    private int x;

    /**
     * The Y position of the entity.
     */
    private int y;

    /**
     * Constructor for the Entity class.
     * Initializes the entity's (x, y) coordinates, image, radius, and scroll speed.
     * @param x The x-coordinate of the entity's initial position.
     * @param y The y-coordinate of the entity's initial position.
     * @param gameProps The properties object containing game configuration values.
     * @param imageProperty The key to the image property of the entity.
     * @param radiusProperty The key to the radius property of the entity.
     */
    public Entity(int x, int y, Properties gameProps, String imageProperty, String radiusProperty) {
        this.x = x;
        this.y = y;

        IMAGE = new Image(gameProps.getProperty(imageProperty));
        RADIUS = Double.parseDouble(gameProps.getProperty(radiusProperty));
        SCROLL_SPEED = Integer.parseInt(gameProps.getProperty("gameObjects.taxi.speedY"));
    }

    /**
     * Moves the entity down if the up keyboard key is pressed.
     * Draws the entity as well.
     * @param input The current mouse/keyboard input.
     */
    public void update(Input input) {
        if (input.isDown(Keys.UP)) {
            moveDown();
        }
        draw();
    }

    /**
     * Moves the entity down the screen according to its scroll speed.
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

    /**
     * Gets the x-coordinate of the entity.
     * @return The current x-coordinate of the entity.
     */
    public int getX() {
        return x;
    }

    /**
     * Gets the y-coordinate of the entity.
     * @return The current y-coordinate of the entity.
     */
    public int getY() {
        return y;
    }

    /**
     * Sets the x-coordinate of the entity.
     * @param x The new x-coordinate of the entity.
     */
    protected void setX(int x) {
        this.x = x;
    }

    /**
     * Sets the y-coordinate of the entity.
     * @param y The new y-coordinate of the entity.
     */
    protected void setY(int y) {
        this.y = y;
    }

    /**
     * Gets the radius of the entity.
     * @return The radius of the entity.
     */
    public double getRadius() {
        return RADIUS;
    }
}
