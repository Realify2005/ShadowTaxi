import java.util.Properties;

public class OtherCar extends Car {

    public OtherCar(Properties gameProps) {
        super(gameProps,
                String.format(gameProps.getProperty("gameObjects.otherCar.image"), MiscUtils.selectAValue(1, 2)),
                "gameObjects.otherCar.radius", "gameObjects.otherCar.health",
                "gameObjects.otherCar.damage", "gameObjects.otherCar.minSpeedY",
                "gameObjects.otherCar.maxSpeedY");
    }
}
