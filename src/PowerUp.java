import java.util.Properties;

/**
 * Abstract class representing a power-up entity in the game.
 * Power-ups can be picked up by taxi or driver, and it will activate a certain perk for a certain amount of frames.
 */
public abstract class PowerUp extends Entity {

    /** Boolean which indicates whether the power-up has been taken by taxi or driver.. */
    private boolean isTaken;

    /**
     * Constructor for power-up entity.
     * @param x the X-coordinate of the power-up
     * @param y the Y-coordinate of the power-up
     * @param gameProps The properties object containing game configuration values.
     * @param imageProperty the property key to the image property of power-ups.
     * @param radiusProperty the property key to the radius property of power-ups.
     */
    public PowerUp(int x, int y, Properties gameProps, String imageProperty, String radiusProperty) {
        super(x, y, gameProps, imageProperty, radiusProperty);
        this.isTaken = false;
    }

    /**
     * Draws the power-up entity on the game screen if it has not been taken by the taxi or driver.
     */
    @Override
    public void draw() {
        if (!isTaken) {
            IMAGE.draw(getX(), getY());
        }
    }

    /**
     * Activates the power-up.
     * @param powerUpState Class that tracks the current state of all power-ups.
     */
    public abstract void activate(PowerUpState powerUpState);

    /**
     * Sets the power-up as taken.
     */
    public void wasTaken() {
        this.isTaken = true;
    }

    /**
     * Checks if the power-up has been taken off the ground.
     * @return True if power-up has been taken, false otherwise.
     */
    public boolean isTaken() {
        return isTaken;
    }
}
