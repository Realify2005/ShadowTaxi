import java.util.Properties;

/**
 * Abstract class for power-up entities.
 */


public abstract class PowerUp extends Entity {

    private boolean isTaken;

    public PowerUp(int x, int y, Properties properties, String imageProperty, String radiusProperty) {
        super(x, y, properties, imageProperty, radiusProperty);
        this.isTaken = false;
    }

    /***
     * Draws the power-up entity on game screen.
     */
    @Override
    public void draw() {
        if (!isTaken) {
            IMAGE.draw(getX(), getY());
        }
    }


    public abstract void activate(PowerUpState powerUpState);

    public void wasTaken() {
        this.isTaken = true;
    }

    public boolean isTaken() {
        return isTaken;
    }
}
