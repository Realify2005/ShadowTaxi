import java.util.Properties;

/**
 * Class representing the Other Car entity, which basically has exact same functionalities as the abstract class Car.
 */
public class OtherCar extends Car {

    /**
     * Constructor for Enemy Car class.
     * Initialises its initial (x, y) position, image, radius, starting health, damage, fixed speed. (Extends from car).
     * @param gameProps The game properties object containing various game configuration values.
     */
    public OtherCar(Properties gameProps) {
        super(gameProps,
                String.format(gameProps.getProperty("gameObjects.otherCar.image"), MiscUtils.selectAValue(1, 2)),
                "gameObjects.otherCar.radius", "gameObjects.otherCar.health",
                "gameObjects.otherCar.damage", "gameObjects.otherCar.minSpeedY",
                "gameObjects.otherCar.maxSpeedY");
    }
}
