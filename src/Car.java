import bagel.Image;
import java.util.Properties;

public abstract class Car implements Drawable, Damageable {
    protected final Image IMAGE;
    protected final double RADIUS;
    protected final double HEALTH;
    protected final double DAMAGE;
    protected final int MIN_SPEED_Y;
    protected final int MAX_SPEED_Y;

    protected final int CAR_Y_1 = -50;
    protected final int CAR_Y_2 = 768;
    protected final int SEPARATE_Y = 1;

    protected final int ROAD_LANE_CENTER_1;
    protected final int ROAD_LANE_CENTER_2;
    protected final int ROAD_LANE_CENTER_3;

    private int initialCollisionTimeoutFramesRemaining;
    private int collisionTimeoutFramesRemaining;
    private boolean fireEffectAdded;

    // Variables for position and state
    private double currentHealth;
    private int currentSpeed;
    private int x;
    private int y;
    private Damageable collidingOtherObject;

    private final int COLLISION_TIMEOUT_FRAMES_TOTAL = 200;
    private final int COLLISION_TIMEOUT_FRAMES_INITIAL = 10;

    public Car(Properties gameProps, String imagePath, String radiusProperty, String healthProperty,
               String damageProperty, String minSpeedYProperty, String maxSpeedYProperty) {
        int PROPS_TO_GAME_MULTIPLIER = 100;

        IMAGE = new Image(imagePath);
        RADIUS = Double.parseDouble(gameProps.getProperty(radiusProperty));
        HEALTH = Double.parseDouble(gameProps.getProperty(healthProperty)) * PROPS_TO_GAME_MULTIPLIER;
        DAMAGE = Double.parseDouble(gameProps.getProperty(damageProperty)) * PROPS_TO_GAME_MULTIPLIER;
        MIN_SPEED_Y = Integer.parseInt(gameProps.getProperty(minSpeedYProperty));
        MAX_SPEED_Y = Integer.parseInt(gameProps.getProperty(maxSpeedYProperty));

        ROAD_LANE_CENTER_1 = Integer.parseInt(gameProps.getProperty("roadLaneCenter1"));
        ROAD_LANE_CENTER_2 = Integer.parseInt(gameProps.getProperty("roadLaneCenter2"));
        ROAD_LANE_CENTER_3 = Integer.parseInt(gameProps.getProperty("roadLaneCenter3"));

        this.currentHealth = HEALTH;
        this.currentSpeed = getRandomSpeed();
        this.x = getRandomPositionX();
        this.y = getRandomPositionY();
        this.fireEffectAdded = false;
    }

    public double getDistanceTo(double otherX, double otherY) {
        return Math.sqrt(Math.pow(otherX - this.x, 2) + Math.pow(otherY - this.y, 2));
    }

    public int getRandomPositionX() {
        return MiscUtils.selectAValue(
                MiscUtils.selectAValue(ROAD_LANE_CENTER_1, ROAD_LANE_CENTER_2),
                ROAD_LANE_CENTER_3);
    }

    private int getRandomSpeed() {
        return MiscUtils.getRandomInt(MIN_SPEED_Y, MAX_SPEED_Y);
    }

    private int getRandomPositionY() {
        return MiscUtils.selectAValue(CAR_Y_1, CAR_Y_2);
    }

    public void update() {
        updateCollisionTimeoutFramesRemaining();

        // Check if still in collision timeout
        if (collisionTimeoutFramesRemaining > 0) {
            separateFromObject(collidingOtherObject);
        } else {
            moveUp();
        }
        draw();
    }

    private void moveUp() {
        this.y -= currentSpeed;
    }

    @Override
    public void draw() {
        if (currentHealth > 0) {
            IMAGE.draw(x, y);
        }
    }

    @Override
    public void receiveDamage(double damage) {
        this.currentHealth -= damage;
        if (this.currentHealth < 0) {
            this.currentHealth = 0;
        }
    }

    @Override
    public void updateCollisionTimeoutFramesRemaining() {
        if (initialCollisionTimeoutFramesRemaining > 0) {
            initialCollisionTimeoutFramesRemaining--;
        }
        if (collisionTimeoutFramesRemaining > 0) {
            collisionTimeoutFramesRemaining--;
        }
    }

    @Override
    public void separateFromObject(Damageable other) {
        if (initialCollisionTimeoutFramesRemaining > 0 && initialCollisionTimeoutFramesRemaining <= 10) {
            int otherY = other.getY();
            int thisY = this.getY();

            if (thisY < otherY) {
                this.setY(thisY - SEPARATE_Y);
            } else {
                this.setY(thisY + SEPARATE_Y);
            }
        }
    }

    public void receiveCollision(Damageable other) {

        double distance = getDistanceTo(other.getX(), other.getY());
        double collisionRange = this.getRadius() + other.getRadius();

        if (collisionTimeoutFramesRemaining == 0 && distance < collisionRange) {
            this.receiveDamage(other.getDamage());
            collidingOtherObject = other;
            collisionTimeoutFramesRemaining = COLLISION_TIMEOUT_FRAMES_TOTAL;
            initialCollisionTimeoutFramesRemaining = COLLISION_TIMEOUT_FRAMES_INITIAL;
        }
    }

    @Override
    public boolean handleCollision(Car other) {

        if (other.getCurrentHealth() > 0) {
            double distance = getDistanceTo(other.getX(), other.getY());
            double collisionRange = this.getRadius() + other.getRadius();

            if (collisionTimeoutFramesRemaining == 0 && distance < collisionRange) {
                this.receiveDamage(other.getDamage());

                collidingOtherObject = other;
                other.receiveCollision(this);

                collisionTimeoutFramesRemaining = COLLISION_TIMEOUT_FRAMES_TOTAL;
                initialCollisionTimeoutFramesRemaining = COLLISION_TIMEOUT_FRAMES_INITIAL;
                return true;
            }
        }

        return false;
    }

    public void setCurrentHealth(int currentHealth) {
        this.currentHealth = currentHealth;
    }

    public int getCurrentSpeed() {
        return currentSpeed;
    }

    public void setCurrentSpeed(int currentSpeed) {
        this.currentSpeed = currentSpeed;
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    @Override
    public double getDamage() {
        return DAMAGE;
    }

    @Override
    public double getRadius() {
        return RADIUS;
    }

    @Override
    public double getCurrentHealth() {
        return currentHealth;
    }

    public void fireEffectWasAdded() {
        fireEffectAdded = true;
    }

    public boolean isFireEffectAdded() {
        return fireEffectAdded;
    }
}
