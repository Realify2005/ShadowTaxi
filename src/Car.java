import bagel.Image;
import java.util.Properties;

public abstract class Car implements Drawable {
    protected final Image IMAGE;
    protected final double RADIUS;
    protected final double HEALTH;
    protected final double DAMAGE;
    protected final int MIN_SPEED_Y;
    protected final int MAX_SPEED_Y;

    protected final int CAR_Y_1 = -50;
    protected final int CAR_Y_2 = 768;

    protected final int ROAD_LANE_CENTER_1;
    protected final int ROAD_LANE_CENTER_2;
    protected final int ROAD_LANE_CENTER_3;

    // Variables for position and state
    private double currentHealth;
    private int currentSpeed;
    private int x;
    private int y;

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
        draw();
        moveUp();
    }

    private void moveUp() {
        this.y -= currentSpeed;
    }

    public void draw() {
        IMAGE.draw(x, y);
    }

    // Method to handle receiving damage
    public void receiveDamage(int damage) {

    }

    // Method to update collision timeout frames
    public void updateCollisionTimeoutFramesRemaining() {

    }

    // Separate the car from another object to avoid overlap
    public void separateFromObject(double otherX, double otherY) {

    }

    public double getCurrentHealth() {
        return currentHealth;
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

}
