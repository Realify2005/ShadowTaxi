import java.util.Properties;

/**
 * Class that handles the trip logic in the game.
 * This includes trip earnings and penalty calculation.
 */
public class Trip {

    /**
     * The penalty rate for a trip.
     * Used when taxi moves too far above the trip end flag's y-coordinate before dropping the passenger.
     */
    private final double PENALTY_RATE;

    /**
     * The taxi in the trip.
     */
    private Taxi TAXI;

    /**
     * The passenger in the trip.
     */
    private final Passenger PASSENGER;

    /**
     * The trip end flag that sets the trip's destination.
     */
    private final TripEndFlag TRIP_END_FLAG;

    /**
     * The current state of power-ups that may affect trip's earnings (e.g. coin).
     */
    private final PowerUpState POWER_UP_STATE;

    /**
     * The game statistics class used to track and display expected earnings of current and last trip.
     */
    private final GameStats GAME_STATS;

    /**
     * Boolean flag indicating whether the trip is currently ongoing.
     */
    private boolean isOngoing;

    /**
     * Boolean flag indicating whether the trip has been completed.
     */
    private boolean isCompleted;

    /**
     * The amount of penalty imposed on the trip if taxi moves too far above the trip end flag's y-coordinate
     * before dropping the passenger.
     */
    private double penalty;

    /**
     * Constructor to initialize the trip class.
     * @param taxi The taxi in the trip.
     * @param passenger The passenger in the trip.
     * @param tripEndFlag The trip end flag that sets the trip's destination.
     * @param powerUpState The current state of power-ups that may affect trip's earnings (e.g. coin).
     * @param gameStats The game statistics class used to track and display expected earnings of current and last trip.
     * @param gameProps The properties object containing game configuration values.
     */
    public Trip(Taxi taxi, Passenger passenger, TripEndFlag tripEndFlag,
                PowerUpState powerUpState, GameStats gameStats, Properties gameProps) {
        this.TAXI = taxi;
        this.PASSENGER = passenger;
        this.TRIP_END_FLAG = tripEndFlag;
        this.isOngoing = false;
        this.isCompleted = false;
        this.penalty = 0;
        this.POWER_UP_STATE = powerUpState;
        this.GAME_STATS = gameStats;

        PENALTY_RATE = Double.parseDouble(gameProps.getProperty("trip.penalty.perY"));
    }

    /**
     * Begins the trip by setting and activating the trip end flag and setting the trip as ongoing.
     * Decreases passenger priority if a coin effect is currently active.
     */
    public void beginTrip() {
        TRIP_END_FLAG.activate();
        this.isOngoing = true;

        // Decrease passenger priority if a coin effect is active and passenger is picked up
        if (PASSENGER.isPickedUp() && POWER_UP_STATE.isCoinActivated()) {
            PASSENGER.decreasePriority();
        }
    }

    /**
     * Checks if any penalty should be imposed on the trip due to the taxi moving too far from the trip end flag.
     * If the taxi is vertically too far from the flag, a penalty is calculated and applied to the passenger's earnings.
     */
    public void checkPenalty() {
        // If taxi has moved beyond the trip end flag
        if (TAXI.getY() < TRIP_END_FLAG.getY() && !PASSENGER.isPenaltyImposed()) {
            // Calculate the distance between the taxi and the trip end flag
            double taxiFlagDistance = Math.sqrt(Math.pow(TAXI.getX() - TRIP_END_FLAG.getX(), 2) +
                    Math.pow(TAXI.getY() - TRIP_END_FLAG.getY(), 2));

            // If the distance is greater than the flag's radius, impose penalty
            if (taxiFlagDistance > TRIP_END_FLAG.getRadius()) {
                this.penalty = PENALTY_RATE * Math.abs(TAXI.getY() - TRIP_END_FLAG.getY());

                PASSENGER.setPenalty(penalty);
            }
        }
    }

    /**
     * Marks the trip as completed.
     * Calculates and adds the passenger's earnings to the total earnings to be rendered on screen.
     */
    public void setTripAsCompleted() {
        this.isOngoing = false;
        this.isCompleted = true;

        // Calculate and add the trip's earnings to the game stats
        countTripEarnings();
    }

    /**
     * Calculates the total earnings for the trip and adds them to the total earnings.
     */
    private void countTripEarnings() {
        if (!PASSENGER.isEarningsAdded() && PASSENGER.isMovingToFlag()) {
            GAME_STATS.addTotalScore(PASSENGER.getEarnings());
            PASSENGER.addedEarnings();
        }
    }

    /**
     * Checks if the trip is currently ongoing.
     * @return True if the trip is currently ongoing, false otherwise.
     */
    public boolean isOngoing() {
        return isOngoing;
    }

    /**
     * Checks if the trip has been completed.
     * @return True if the trip has been completed, false otherwise.
     */
    public boolean isCompleted() {
        return isCompleted;
    }

    /**
     * Gets the earnings of the passenger for the trip.
     * @return The earnings of the passenger for the trip.
     */
    public double getPassengerEarnings() {
        return PASSENGER.getEarnings();
    }

    /**
     * Gets the priority of the current passenger.
     * @return The priority of the current passenger.
     */
    public int getPassengerPriority() {
        return PASSENGER.getPriority();
    }

    /**
     * Gets the penalty amount applied to the trip's earnings.
     * @return The penalty amount applied to the trip's earnings.
     */
    public double getPenalty() {
        return penalty;
    }

    /**
     * Sets a new taxi for the trip to replace the old broken one (if the old one has been broken).
     * @param taxi The new taxi to be set for the trip.
     */
    public void setTaxi(Taxi taxi) {
        this.TAXI = taxi;
    }

    /**
     * Gets the passenger currently involved in the trip.
     * @return The passenger currently involved in the trip.
     */
    public Passenger getPassenger() {
        return PASSENGER;
    }
}
