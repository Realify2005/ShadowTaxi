import bagel.Font;
import bagel.*;
import java.util.ArrayList;
import java.util.Properties;
import java.util.HashSet;
import java.util.Set;

/**
 * Class that handles most of the gameplay logic.
 * Handles the interactions and collisions between all combinations of objects in-game.
 */
public class Gameplay {
    /**
     * The properties object containing game configuration values.
     */
    private final Properties GAME_PROPS;

    /**
     * The properties object containing rendered text configuration values.
     */
    private final Properties MESSAGE_PROPS;

    /**
     * Spawn rate for other cars in the game.
     */
    private final int OTHER_CAR_SPAWN_RATE = 200;

    /**
     * Spawn rate for enemy cars in the game.
     */
    private final int ENEMY_CAR_SPAWN_RATE = 400;

    /**
     * Path to the font used for rendering trip info.
     */
    private final String FONT_PATH;

    /**
     * Size of the font used for rendering trip info.
     */
    private final int FONT_SIZE;

    /**
     * X-coordinate for rendering trip info on screen.
     */
    private final double TRIP_INFO_X;

    /**
     * Y-coordinate for rendering trip info on screen.
     */
    private final double TRIP_INFO_Y;

    /**
     * Text displayed when a trip is currently ongoing.
     */
    private final String ONGOING_TRIP_TEXT;

    /**
     * Text displayed when no trip is currently ongoing and another trip was completed before.
     */
    private final String COMPLETED_TRIP_TEXT;

    /**
     * Text for expected earnings in a trip.
     */
    private final String EXPECTED_EARNINGS_TEXT;

    /**
     * Text for the priority level of a trip.
     */
    private final String PRIORITY_TEXT;

    /**
     * Text for the penalty amount in a trip.
     */
    private final String PENALTY_TEXT;

    /**
     * Text for the passenger's health.
     */
    private final String PASSENGER_TEXT;

    /**
     * X-coordinate for rendering passenger health on screen.
     */
    private final int PASSENGER_TEXT_X;

    /**
     * Y-coordinate for rendering passenger health on screen.
     */
    private final int PASSENGER_TEXT_Y;

    /**
     * Center X-position of the first road lane.
     */
    private final int ROAD_LANE_CENTER_1;

    /**
     * Center X-position of the third road lane.
     */
    private final int ROAD_LANE_CENTER_3;

    /**
     * Minimum Y-coordinate for spawning the next taxi.
     */
    private final int TAXI_NEXT_SPAWN_MIN_Y;

    /**
     * Maximum Y-coordinate for spawning the next taxi.
     */
    private final int TAXI_NEXT_SPAWN_MAX_Y;

    /**
     * The taxi object in the gameplay.
     */
    private Taxi taxi;

    /**
     * The driver object in the gameplay.
     */
    private Driver driver;

    /**
     * The current active trip.
     */
    private Trip trip;

    /**
     * The flag indicating where the passenger should be dropped off at the end of a trip.
     */
    private TripEndFlag tripEndFlag;

    /**
     * The current state of power-ups in the game.
     */
    private final PowerUpState POWER_UP_STATE;

    /**
     * Tracks and renders the game's statistics.
     */
    private final GameStats GAME_STATS;

    /**
     * List of all passengers in the game.
     */
    private ArrayList<Passenger> passengers;

    /**
     * List of cars currently in the game.
     */
    private ArrayList<Car> cars;

    /**
     * List of currently active fireballs in the game.
     */
    private ArrayList<Fireball> fireballs;

    /**
     * List of temporary visual effects in the game.
     */
    private ArrayList<TemporaryEffect> temporaryEffects;

    /**
     * List of taxis that are broken.
     */
    private ArrayList<Taxi> damagedTaxis;

    /**
     * List of power-ups (coin, invincible power) in the game.
     */
    private ArrayList<PowerUp> powerUps;

    /**
     * List of fireballs to be removed after a collision with a damageable entity.
     */
    private ArrayList<Fireball> fireballsToRemove;

    /**
     * Set of collided car pairs, used to track collisions between cars.
     */
    private Set<Car[]> collidedPairs = new HashSet<>();

    /**
     * Current health of the passenger of current ongoing trip.
     */
    private double passengerHealth;

    /**
     * Tracks the lowest passenger health across all passenger healths in the game..
     */
    private double lowestPassengerHealth;

    /**
     * Tracks the latest ejected passenger;
     */
    private Passenger lastEjectedPassenger;

    /**
     * Constructor for gameplay class.
     * Initialises all the necessary attributes and lists for a gameplay class.
     * @param tripEndFlag The flag indicating where the passenger should be dropped off at the end of a trip.
     * @param powerUpState The current state of power-ups in the game.
     * @param gameStats Tracks and renders game's statistics.
     * @param gameProps The properties object containing game configuration values.
     * @param messageProps The properties object containing rendered text configuration values.
     */
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

        int PROPS_TO_GAME_MULTIPLIER = 100; // The game properties stores health and damage as (value / 100).
        this.passengerHealth = Double.parseDouble(
                gameProps.getProperty("gameObjects.passenger.health")
        ) * PROPS_TO_GAME_MULTIPLIER;
        this.lowestPassengerHealth = this.passengerHealth;
        this.lastEjectedPassenger = null;

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
            if (!taxi.hasPassenger() && taxi.isAdjacentToPassenger(passenger)
                    && !passenger.isPickedUp() && taxi.hasDriver()) {
                taxi.pickUpPassenger(passenger);
            }

            if (taxi.hasPassenger()) {
                // Check if taxi is adjacent to flag. (Logic is in taxi.dropOffPassenger function).
                taxi.dropOffPassenger(tripEndFlag);
                trip.checkPenalty();
            }
        }
    }

    /**
     * Constantly updates the gameplay class.
     * This function performs all the needed checks at any given time to make the gameplay logic flow working.
     * Performs calls to other functions where its functionality will be explained in their javadocs respectively.
     * @param input The current mouse/keyboard input.
     * @param isRaining True if weather is currently raining for current frame, false otherwise.
     */
    public void update(Input input, boolean isRaining) {

        // Update the trip end flag.
        if (tripEndFlag != null) {
            tripEndFlag.update(input);
        }

        // Set trip as completed as soon as passenger leaves the taxi.
        if (trip != null && taxi.isPassengerMovingToFlag()) {
            trip.setTripAsCompleted();
        }

        renderTripInfo();
        taxi.update(input);
        driver.update(input, taxi);
        checkAndHandleCollisions(input);
        randomlySpawnCars();
        checkIfDriverCanEnterTaxi();
        updatePassengerLogic(input, isRaining);
        updatePassengerHealth();
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
     * Performs call to other functions which altogether checks and handles all combination of collisions.
     * Updates the temporary effects (blood, fire, smoke) as well as the broken (damaged) taxis.
     * @param input The current mouse/keyboard input.
     */
    private void checkAndHandleCollisions(Input input) {
        checkCarCollisions(input);
        checkFireballCollisions(input);
        checkPowerUpCollisions(input);
        checkIfTaxiIsBroken();

        for (TemporaryEffect temporaryEffect : temporaryEffects) {
            temporaryEffect.update(input);
        }

        for (Taxi damagedTaxi : damagedTaxis) {
            damagedTaxi.update(input);
        }
    }

    /**
     * Checks and handles all possible collisions involving cars.
     * Cars can collide with taxi, driver, passengers, and other cars.
     */
    private void checkCarCollisions(Input input) {
        // Check collisions between car and other entities.
        for (Car car : cars) {
            if (taxi.handleCollision(car)) {
                temporaryEffects.add(new Smoke(taxi.getX(), taxi.getY(), GAME_PROPS));
            }
            car.update(input, taxi, driver);
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

        // Check collisions between car and cars.
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
    }

    /**
     * Checks and handles all possible collisions involving fireballs.
     * Fireballs can inflict damage towards passengers, cars, taxi, and driver.
     */
    private void checkFireballCollisions(Input input) {

        for (Fireball fireball : fireballs) {
            fireball.update(input, taxi, driver);
            double fireballDamage = fireball.getDamage();

            // Check possible collisions between fireball and passengers.
            for (Passenger passenger : passengers) {
                if (fireball.collidesWith(passenger.getX(), passenger.getY(), passenger.getRadius())) {
                    passenger.receiveDamage(fireballDamage);
                    fireball.wasCollided();
                    break;
                }
            }

            // Check possible collisions between fireball and cars.
            if (!fireball.isCollided()) {
                for (Car car : cars) {
                    if (car != fireball.getSpawnedBy() && fireball.collidesWith(car.getX(), car.getY(), car.getRadius())) {
                        car.receiveDamage(fireballDamage);
                        fireball.wasCollided();
                        break;
                    }
                }
            }

            // Check possible collisions between fireball and the driver.
            if (!fireball.isCollided()) {
                if (fireball.collidesWith(driver.getX(), driver.getY(), driver.getRadius())) {
                    driver.receiveDamage(fireballDamage);
                    fireball.wasCollided();
                }
            }

            // Check possible collisions between fireball and the taxi.
            if (!fireball.isCollided()) {
                if (fireball.collidesWith(taxi.getX(), taxi.getY(), taxi.getRadius())) {
                    taxi.receiveDamage(fireballDamage);
                    fireball.wasCollided();
                }
            }

            // Remove all collided (and thus disappeared) fireballs from the arraylist.
            if (fireball.isCollided()) {
                fireballsToRemove.add(fireball);
            }
        }
        fireballs.removeAll(fireballsToRemove);
    }

    /**
     * Checks and handles all possible collisions involving power-up entities (coin, invincible power).
     * Also update all power-up entities according to player's mouse/keyboard input.
     * @param input The current mouse/keyboard input.
     */
    private void checkPowerUpCollisions(Input input) {
        for (PowerUp powerUp : powerUps) {
            powerUp.update(input);
            // Both taxi and driver can pick up power-ups.
            if ((taxi.collidedWith(powerUp) || driver.collidedWith(powerUp)) && !powerUp.isTaken()) {
                POWER_UP_STATE.activatePowerUp(powerUp);
            }
        }
    }


    /**
     * Checks if current active taxi is broken (damaged).
     * If so, eject driver and passenger where necessary, and randomly create a new one for driver and passenger to
     * get inside to continue their trip.
     */
    private void checkIfTaxiIsBroken() {
        if (taxi.getCurrentHealth() <= 0) {
            damagedTaxis.add(taxi);
            temporaryEffects.add(new Fire(taxi.getX(), taxi.getY(), GAME_PROPS));
            if (taxi.getCurrentPassenger() != null && !taxi.isPassengerMovingToFlag()) {
                System.out.println("passenger ejected");
                lastEjectedPassenger = taxi.getCurrentPassenger();
                taxi.getCurrentPassenger().eject(); // eject passenger.
                taxi.passengerEjected();
            }
            if (taxi.hasDriver()) {
                driver.eject(); // eject driver.
                taxi.driverEjected();
            }
            taxi = new Taxi(getTaxiRandomSpawnX(), getTaxiRandomSpawnY(), this,
                    POWER_UP_STATE, GAME_PROPS, MESSAGE_PROPS);
            if (trip != null) {
                trip.setTaxi(taxi); // Update current trip with new taxi.
            }
            POWER_UP_STATE.resetPowerUps(); // Power-ups do not carry over to driver (resets when driver is ejected).
        }
    }

    /**
     * Randomly spawns both other cars and enemy cars according to their set spawn rate.
     */
    private void randomlySpawnCars() {
        if (MiscUtils.canSpawn(OTHER_CAR_SPAWN_RATE)) {
            cars.add(new OtherCar(GAME_PROPS));
        }

        if (MiscUtils.canSpawn(ENEMY_CAR_SPAWN_RATE)) {
            cars.add(new EnemyCar(GAME_PROPS, fireballs));
        }
    }

    /**
     * Logic to start trip if a passenger has successfully entered the taxi.
     * Creates both trip end flag and trip instances.
     * @param passenger The passenger that has successfully entered the taxi.
     */
    public void startTrip(Passenger passenger) {
        tripEndFlag = new TripEndFlag(passenger.getEndX(), passenger.getY(),
                passenger.getDistanceY(), GAME_PROPS);
        trip = new Trip(taxi, passenger, tripEndFlag, POWER_UP_STATE, GAME_STATS, GAME_PROPS);
        trip.beginTrip();
    }

    /**
     * Checks if driver is adjacent to the taxi (given that passenger is currently outside the taxi).
     * If so, calls the setter functions in both taxi and driver class to mark this event.
     */
    private void checkIfDriverCanEnterTaxi() {
        // Check if driver can enter taxi (given that driver is outside taxi)
        if (taxi.isAdjacentToDriver(driver) && !taxi.hasDriver()) {
            taxi.driverEntered();
            driver.enteredTaxi();
            if (lastEjectedPassenger != null && !lastEjectedPassenger.isInTaxi()) {
                // Make sure that passenger is updated with taxi movement.
                taxi.setCurrentPassenger(lastEjectedPassenger);
                lastEjectedPassenger.enteredTaxi();
            }
        }
    }

    /**
     * Updates all passenger's state according to current mouse/keyboard input and weather.
     * @param input The current mouse/keyboard input.
     * @param isRaining True if current weather is rainy, false otherwise.
     */
    private void updatePassengerLogic(Input input, boolean isRaining) {
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

            // Keep track of lowest passenger health out of all passengers to be rendered if passenger not in taxi.
            if (passenger.getCurrentHealth() < this.passengerHealth) {
                this.lowestPassengerHealth = passenger.getCurrentHealth();
            }

            passenger.update(input, isRaining);
        }
    }

    /**
     * Constantly updates the passenger health to be rendered onto the screen.
     * Rendered passenger health is the health of the passenger of the current ongoing trip.
     * If there is no current ongoing trip, the rendered health will be the lowest passenger health across all passengers.
     */
    private void updatePassengerHealth() {
        if (trip != null) {
            this.passengerHealth = trip.getPassenger().getCurrentHealth();
        } else {
            this.passengerHealth = this.lowestPassengerHealth;
        }
    }

    /**
     * Renders the passenger health onto the screen.
     */
    private void renderPassengerHealth() {
        Font font = new Font(FONT_PATH, FONT_SIZE);
        font.drawString( PASSENGER_TEXT + passengerHealth, PASSENGER_TEXT_X, PASSENGER_TEXT_Y);
    }

    /**
     * Helper function to generate a random X position in which a new taxi can spawn when the old one breaks.
     * @return The X position for the new taxi.
     */
    private int getTaxiRandomSpawnX() {
        return MiscUtils.selectAValue(ROAD_LANE_CENTER_1, ROAD_LANE_CENTER_3);
    }

    /**
     * Helper function to generate a random Y position in which a new taxi can spawn when the old one breaks.
     * @return The Y position for the new taxi.
     */
    private int getTaxiRandomSpawnY() {
        return MiscUtils.getRandomInt(TAXI_NEXT_SPAWN_MIN_Y, TAXI_NEXT_SPAWN_MAX_Y);
    }

    /**
     * Initialises the taxi entity onto the gameplay class.
     * @param taxi The taxi entity to be initialised.
     */
    public void initialiseTaxi(Taxi taxi) {
        this.taxi = taxi;
    }

    /**
     * Initialises the driver entity onto the gameplay class.
     * @param driver The driver entity to be initialised.
     */
    public void initialiseDriver(Driver driver) { this.driver = driver; }

    /**
     * Initialises the list of passengers onto the gameplay class.
     * @param passengers The list of passengers to be initialised.
     */
    public void initialisePassengers(ArrayList<Passenger> passengers) {
        this.passengers = passengers;
    }

    /**
     * Initialises the list of power-ups (coin and invincible power) onto the gameplay class.
     * @param powerUps The list of power-ups to be initialised.
     */
    public void initialisePowerUps(ArrayList<PowerUp> powerUps) {
        this.powerUps = powerUps;
    }

    /**
     * Gets the current active taxi involved in the gameplay.
     * @return The current active taxi involved in the gameplay.
     */
    public Taxi getTaxi() {
        return taxi;
    }
}
