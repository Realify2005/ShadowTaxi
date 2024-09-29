import bagel.Font;
import bagel.*;
import java.util.Iterator;
import java.util.ArrayList;
import java.util.Properties;
import java.util.HashSet;
import java.util.Set;

/**
 * Class that handles the gameplay logic
 */
public class Gameplay {
    // Constants related to properties.
    private final Properties GAME_PROPS;
    private final Properties MESSAGE_PROPS;

    // Variables
    private Taxi taxi;
    private Driver driver;
    private Trip trip;
    private TripEndFlag tripEndFlag;
    private final PowerUpState POWER_UP_STATE;
    private final GameStats GAME_STATS;

    private ArrayList<Car> cars;
    private ArrayList<Fireball> fireballs;
    private ArrayList<TemporaryEffect> temporaryEffects;
    private ArrayList<Taxi> damagedTaxis;
    private ArrayList<PowerUp> powerUps;
    private ArrayList<Fireball> fireballsToRemove;
    private final int OTHER_CAR_SPAWN_RATE = 200;
    private final int ENEMY_CAR_SPAWN_RATE = 400;

    // Full list of passengers.
    private ArrayList<Passenger> passengers;

    // Constants for rendering trip info
    private final String FONT_PATH;
    private final int FONT_SIZE;
    private final double TRIP_INFO_X;
    private final double TRIP_INFO_Y;

    // Constants for message texts from message_en.properties
    private final String ONGOING_TRIP_TEXT;
    private final String COMPLETED_TRIP_TEXT;
    private final String EXPECTED_EARNINGS_TEXT;
    private final String PRIORITY_TEXT;
    private final String PENALTY_TEXT;

    private final String PASSENGER_TEXT;
    private final int PASSENGER_TEXT_X;
    private final int PASSENGER_TEXT_Y;

    private final int ROAD_LANE_CENTER_1;
    private final int ROAD_LANE_CENTER_3;
    private final int TAXI_NEXT_SPAWN_MIN_Y;
    private final int TAXI_NEXT_SPAWN_MAX_Y;

    private Set<Car[]> collidedPairs = new HashSet<>();

    private double passengerHealth;
    private double lowestPassengerHealth;

    public Gameplay(TripEndFlag tripEndFlag, PowerUpState powerUpState,
                    GameStats gameStats, Properties gameProps, Properties messageProps) {
        this.trip = null;
        this.tripEndFlag = tripEndFlag;
        this.POWER_UP_STATE = powerUpState;
        this.GAME_STATS = gameStats;

        this.GAME_PROPS = gameProps;
        this.MESSAGE_PROPS = messageProps;

        // Initialize constants for rendering trip info
        this.FONT_PATH = GAME_PROPS.getProperty("font");
        this.FONT_SIZE = Integer.parseInt(GAME_PROPS.getProperty("gamePlay.info.fontSize"));
        this.TRIP_INFO_X = Double.parseDouble(GAME_PROPS.getProperty("gamePlay.tripInfo.x"));
        this.TRIP_INFO_Y = Double.parseDouble(GAME_PROPS.getProperty("gamePlay.tripInfo.y"));

        // Initialize constants for messages
        this.ONGOING_TRIP_TEXT = MESSAGE_PROPS.getProperty("gamePlay.onGoingTrip.title");
        this.COMPLETED_TRIP_TEXT = MESSAGE_PROPS.getProperty("gamePlay.completedTrip.title");
        this.EXPECTED_EARNINGS_TEXT = MESSAGE_PROPS.getProperty("gamePlay.trip.expectedEarning");
        this.PRIORITY_TEXT = MESSAGE_PROPS.getProperty("gamePlay.trip.priority");
        this.PENALTY_TEXT = MESSAGE_PROPS.getProperty("gamePlay.trip.penalty");

        PASSENGER_TEXT = messageProps.getProperty("gamePlay.passengerHealth");
        PASSENGER_TEXT_X = Integer.parseInt(gameProps.getProperty("gamePlay.passengerHealth.x"));
        PASSENGER_TEXT_Y = Integer.parseInt(gameProps.getProperty("gamePlay.passengerHealth.y"));

        TAXI_NEXT_SPAWN_MIN_Y = Integer.parseInt(gameProps.getProperty("gameObjects.taxi.nextSpawnMinY"));
        TAXI_NEXT_SPAWN_MAX_Y = Integer.parseInt(gameProps.getProperty("gameObjects.taxi.nextSpawnMaxY"));

        ROAD_LANE_CENTER_1 = Integer.parseInt(gameProps.getProperty("roadLaneCenter1"));
        ROAD_LANE_CENTER_3 = Integer.parseInt(gameProps.getProperty("roadLaneCenter3"));

        int PROPS_TO_GAME_MULTIPLIER = 100;
        this.passengerHealth = Double.parseDouble(
                gameProps.getProperty("gameObjects.passenger.health")
        ) * PROPS_TO_GAME_MULTIPLIER;
        this.lowestPassengerHealth = this.passengerHealth; // Initially set to default health for passenger entity,

        this.cars = new ArrayList<>();
        this.fireballs = new ArrayList<>();
        this.temporaryEffects = new ArrayList<>();
        this.damagedTaxis = new ArrayList<>();
        this.fireballsToRemove = new ArrayList<>();
    }

    /**
     * Checks if taxi is adjacent to either passenger or flag
     * If so, either pick up the passenger towards the taxi or drop the passenger towards the trip end flag
     */
    public void checkIfTaxiIsAdjacentToPassengerOrFlag() {
        for (Passenger passenger : passengers) {
            // Check if taxi is adjacent to a passenger that has not been picked up before.
            if (taxi.isEmpty() && taxi.isAdjacentToPassenger(passenger) && !passenger.isPickedUp()) {
                taxi.pickUpPassenger(passenger);
            }

            if (!taxi.isEmpty()) {
                // Check if taxi is adjacent to flag. (Logic is in taxi.dropOffPassenger function).
                taxi.dropOffPassenger(tripEndFlag);
                trip.checkPenalty();
            }
        }
    }

    /**
     * Constantly updates the gameplay class.
     * This function performs all the needed checks at any given time to make the gameplay smooth.
     */
    public void update(Input input) {
        renderTripInfo();
        taxi.update(input);

        if (tripEndFlag != null) {
            tripEndFlag.update(input);
        }

        for (Passenger passenger : passengers) {
            // If passenger left the taxi and is moving to flag,
            // make sure that the passenger arrives to its final destination (i.e. flag).
            if (passenger.isMovingToFlag()) {
                passenger.dropOff(tripEndFlag);
            }
            // means that passenger is ejected, so need to put it back to taxi
            if (passenger.isPickedUp() && !passenger.isInTaxi()) {
                taxi.setCurrentPassenger(passenger);
            }

            if (passenger.getCurrentHealth() < this.passengerHealth) {
                this.lowestPassengerHealth = passenger.getCurrentHealth();
            }
        }

        // Set trip as completed as soon as passenger leaves the taxi.
        if (trip != null && taxi.isPassengerMovingToFlag()) {
            trip.setTripAsCompleted();
        }

        if (taxi.isAdjacentToDriver(driver)) {
            taxi.driverEntered();
            driver.enteredTaxi();
        }

        driver.update(input, taxi);

        for (Car car : cars) {
            if (taxi.handleCollision(car)) {
                temporaryEffects.add(new Smoke(taxi.getX(), taxi.getY(), GAME_PROPS));
            }
            car.update();
            if (car.getCurrentHealth() <= 0 && !car.isFireEffectAdded()) {
                temporaryEffects.add(new Fire(car.getX(), car.getY(), GAME_PROPS));
                car.fireEffectWasAdded();
            }
            if (driver.handleCollision(car)) {
                temporaryEffects.add(new Blood(driver.getX(), driver.getY(), GAME_PROPS));
            }
            for (Passenger passenger : passengers) {
                if (passenger.handleCollision(car)) {
                    temporaryEffects.add(new Blood(passenger.getX(), passenger.getY(), GAME_PROPS));
                }
            }
        }

        collidedPairs.clear();
        for (int i = 0; i < cars.size(); i++) {
            Car car1 = cars.get(i);
            for (int j = i + 1; j < cars.size(); j++) {
                Car car2 = cars.get(j);

                Car[] carPair = {car1, car2};
                if (!collidedPairs.contains(carPair)) {
                    if (car1.handleCollision(car2)) {
                        temporaryEffects.add(new Smoke(car1.getX(), car1.getY(), GAME_PROPS));
                        temporaryEffects.add(new Smoke(car2.getX(), car2.getY(), GAME_PROPS));
                        collidedPairs.add(carPair);
                    }
                }
            }
        }

        for (Fireball fireball : fireballs) {
            fireball.update();
            double fireballDamage = fireball.getDamage();

            for (Passenger passenger : passengers) {
                if (fireball.collidesWith(passenger.getX(), passenger.getY(), passenger.getRadius())) {
                    passenger.receiveDamage(fireballDamage);
                    fireball.wasCollided();
                    break;
                }
            }

            if (!fireball.isCollided()) {
                for (Car car : cars) {
                    if (car != fireball.getSpawnedBy() && fireball.collidesWith(car.getX(), car.getY(), car.getRadius())) {
                        car.receiveDamage(fireballDamage);
                        fireball.wasCollided();
                        break;
                    }
                }
            }

            if (!fireball.isCollided()) {
                if (fireball.collidesWith(driver.getX(), driver.getY(), driver.getRadius())) {
                    driver.receiveDamage(fireballDamage);
                    fireball.wasCollided();
                }
            }

            if (!fireball.isCollided()) {
                if (fireball.collidesWith(taxi.getX(), taxi.getY(), taxi.getRadius())) {
                    taxi.receiveDamage(fireballDamage);
                    fireball.wasCollided();
                }
            }

            if (fireball.isCollided()) {
                fireballsToRemove.add(fireball);
            }
        }
        fireballs.removeAll(fireballsToRemove);

        for (TemporaryEffect temporaryEffect : temporaryEffects) {
            temporaryEffect.update();
        }

        for (Taxi damagedTaxi : damagedTaxis) {
            damagedTaxi.update(input);
        }

        if (MiscUtils.canSpawn(OTHER_CAR_SPAWN_RATE)) {
            cars.add(new OtherCar(GAME_PROPS));
        }

        if (MiscUtils.canSpawn(ENEMY_CAR_SPAWN_RATE)) {
            cars.add(new EnemyCar(GAME_PROPS, fireballs));
        }

        if (taxi.getCurrentHealth() <= 0) {
            damagedTaxis.add(taxi);
            temporaryEffects.add(new Fire(taxi.getX(), taxi.getY(), GAME_PROPS));
            if (taxi.getCurrentPassenger() != null && !taxi.isPassengerMovingToFlag()) {
                taxi.getCurrentPassenger().eject();
            }
            if (taxi.hasDriver()) {
                driver.eject();
                taxi.driverEjected();
            }
            taxi = new Taxi(getTaxiRandomSpawnX(), getTaxiRandomSpawnY(), this, GAME_PROPS, MESSAGE_PROPS);
            if (trip != null) {
                trip.setTaxi(taxi);
            }
        }

        for (PowerUp powerUp : powerUps) {
            powerUp.update(input);
            if (taxi.collidedWith(powerUp) && !powerUp.isTaken()) {
                POWER_UP_STATE.activatePowerUp(powerUp);
            }
            // If driver collided with coin/invincible power too...
        }

        if (trip != null) {
            this.passengerHealth = trip.getPassenger().getCurrentHealth();
        } else {
            this.passengerHealth = this.lowestPassengerHealth;
        }

        renderPassengerHealth();
    }

    /**
     * Renders the bottom left "Current Trip" or "Last Trip" information text on screen during gameplay.
     */
    private void renderTripInfo() {
        Font font = new Font(FONT_PATH, FONT_SIZE);

        if (trip != null && trip.isOngoing()) {
            // Render "Current Trip" information.
            font.drawString(ONGOING_TRIP_TEXT, TRIP_INFO_X, TRIP_INFO_Y);

            double expectedEarnings = trip.getPassengerEarnings();
            String expectedEarningsText = String.format(EXPECTED_EARNINGS_TEXT + "%.1f", expectedEarnings);
            font.drawString(expectedEarningsText, TRIP_INFO_X, TRIP_INFO_Y + 30);

            int priority = trip.getPassengerPriority();
            String priorityText = String.format(PRIORITY_TEXT + "%d", priority);
            font.drawString(priorityText, TRIP_INFO_X, TRIP_INFO_Y + 60);
        } else if (trip != null && trip.isCompleted()) {
            // Render "Last Trip" information.
            font.drawString(COMPLETED_TRIP_TEXT, TRIP_INFO_X, TRIP_INFO_Y);

            double totalEarnings = trip.getPassengerEarnings();
            double penalty = trip.getPenalty();
            double earningsWithPenalty = totalEarnings + penalty;
            String totalEarningsText = String.format(EXPECTED_EARNINGS_TEXT + "%.1f", earningsWithPenalty);
            font.drawString(totalEarningsText, TRIP_INFO_X, TRIP_INFO_Y + 30);

            int priority = trip.getPassengerPriority();
            String priorityText = String.format(PRIORITY_TEXT + "%d", priority);
            font.drawString(priorityText, TRIP_INFO_X, TRIP_INFO_Y + 60);

            String penaltyText = String.format(PENALTY_TEXT + "%.2f", penalty);
            font.drawString(penaltyText, TRIP_INFO_X, TRIP_INFO_Y + 90);
        }
    }

    /**
     * Initialises the taxi entity.
     */
    public void initialiseTaxi(Taxi taxi) {
        this.taxi = taxi;
    }

    /**
     * Initialises the driver entity.
     */
    public void initialiseDriver(Driver driver) { this.driver = driver; }

    /**
     * Initialises the list of passengers.
     */
    public void initialisePassengers(ArrayList<Passenger> passengers) {
        this.passengers = passengers;
    }

    public void initialisePowerUps(ArrayList<PowerUp> powerUps) {
        this.powerUps = powerUps;
    }

    /**
     * Logic to start trip if a passenger has successfully entered the taxi
     */
    public void startTrip(Passenger passenger) {
        tripEndFlag = new TripEndFlag(passenger.getEndX(), passenger.getY(),
                passenger.getDistanceY(), GAME_PROPS);
        trip = new Trip(taxi, passenger, tripEndFlag, POWER_UP_STATE, GAME_STATS, GAME_PROPS);
        trip.beginTrip();
    }

    public void resetObjects() {
        cars = new ArrayList<>();
        fireballs = new ArrayList<>();
        damagedTaxis = new ArrayList<>();
        temporaryEffects = new ArrayList<>();
    }

    private void renderPassengerHealth() {
        Font font = new Font(FONT_PATH, FONT_SIZE);
        font.drawString( PASSENGER_TEXT + passengerHealth, PASSENGER_TEXT_X, PASSENGER_TEXT_Y);
    }

    private int getTaxiRandomSpawnX() {
        return MiscUtils.selectAValue(ROAD_LANE_CENTER_1, ROAD_LANE_CENTER_3);
    }

    private int getTaxiRandomSpawnY() {
        return MiscUtils.getRandomInt(TAXI_NEXT_SPAWN_MIN_Y, TAXI_NEXT_SPAWN_MAX_Y);
    }

    public Taxi getTaxi() {
        return taxi;
    }
}
