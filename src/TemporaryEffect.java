import bagel.*;
import java.util.Properties;

public abstract class TemporaryEffect implements Drawable, Scrollable {

    protected final Image IMAGE;
    protected final int SCROLL_SPEED;

    private int framesRemaining;
    private int x;
    private int y;

    public TemporaryEffect(int x, int y, Properties gameProps, String TTLProperty, String imageProperty) {
        this.x = x;
        this.y = y;
        this.framesRemaining = Integer.parseInt(gameProps.getProperty(TTLProperty));

        IMAGE = new Image(gameProps.getProperty(imageProperty));
        SCROLL_SPEED = Integer.parseInt(gameProps.getProperty("gameObjects.taxi.speedY"));
    }

    public void update() {
        draw();
        updateFramesRemaining();
    }

    public void moveDown() {
        y += SCROLL_SPEED;
    }

    private void updateFramesRemaining() {
        if (framesRemaining > 0) {
            framesRemaining--;
        }
    }

    public void draw() {
        if (framesRemaining > 0) {
            IMAGE.draw(x, y);
        }
    }
}
