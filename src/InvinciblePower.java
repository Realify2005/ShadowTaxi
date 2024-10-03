import java.util.Properties;

/**
 * Class representing Invincible Power entity in the game, which is a PowerUp.
 * Invincible power can be collected by either taxi or driver.
 * When collected, the entity that collects it will be immune to collisions for a certain number of frames.
 * Driver collecting this power-up transfers over its perks to the next taxi it enters, but not vice-versa.
 */
public class InvinciblePower extends PowerUp {

    /**
     * Constructor for the Invincible Power class.
     * Initializes its (x, y) position, image, and radius.
     * @param x The x-coordinate of invincible power's position.
     * @param y The y-coordinate of invincible power's position.
     * @param gameProps The game properties object containing various game configuration values.
     */
    public InvinciblePower(int x, int y, Properties gameProps) {
        super(x, y, gameProps, "gameObjects.invinciblePower.image",
                "gameObjects.invinciblePower.radius");
    }

    /***
     * Activates the invincible power in the gameplay.
     * (i.e. Result of taxi/driver collision with invincible power).
     * @param powerUpState The class that tracks the current state of power-ups in the game.
     */
    @Override
    public void activate(PowerUpState powerUpState) {
        this.wasTaken();
        powerUpState.refreshInvincibleFrameCount();
    }
}
